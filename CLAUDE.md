# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

Rhenium is a compiler for a small statically-typed language that **transpiles to C**, then shells out to `clang` and executes the result. Kotlin/JVM, Gradle multi-module, ANTLR4 for the front end, Dagger 2 for wiring.

## Commands

The Gradle daemon must run a JDK matching the `jvmToolchain(...)` version in [buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts](buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts). On a newer daemon JVM, Gradle's embedded Kotlin cannot parse the version string and the whole build dies at configuration time with `java.lang.IllegalArgumentException: <java version>`. `./gradlew -version` prints the daemon JVM; if it does not match the toolchain, point the daemon at one that does — on this machine the toolchain JDK is the `javac` on PATH:

```bash
JH="$(dirname "$(dirname "$(readlink -f "$(which javac)")")")"

./gradlew -Dorg.gradle.java.home="$JH" build      # compile + test
./gradlew -Dorg.gradle.java.home="$JH" :ast:test  # one module's tests
./gradlew -Dorg.gradle.java.home="$JH" :ast:test --tests "AstBuilderTests"
./gradlew -Dorg.gradle.java.home="$JH" :parser:generateGrammarSource  # regenerate ANTLR sources
```

Running the compiler:

```bash
./gradlew -Dorg.gradle.java.home="$JH" run --args="../src/main.re"
```

The argument is a path to a `.re` file. Beware that the `run` task's working directory is `app/`, and that `RheniumCompiler` invokes the built binary as `./<path>.o` — so paths must be **relative** and resolved from `app/`; an absolute path produces `.//abs/path.o` and fails. Output lands next to the source: `<path>.c`, then `<path>.o`, which is executed immediately. `clang` must be on PATH. To run from the repo root instead, use the installed distribution:

```bash
./gradlew -Dorg.gradle.java.home="$JH" :app:installDist
java -cp "app/build/install/app/lib/*" me.eriknikli.rhenium.app.MainKt src/main.re
```

`src/` is gitignored — it holds scratch `.re` programs, not shipped code.

AST nodes are data classes carrying their `ParserRuleContext`, which compares by identity — so a parsed tree can never equal a hand-built expected node. [AstBuilderTests](ast/src/test/kotlin/AstBuilderTests.kt) therefore renders the tree to an s-expression and compares strings; add cases to its `provideData` and teach the renderer any new node kind.

## Module graph

```
app  →  common, parser, ast, semanticAnalyzer, semanticContext, transpiler
ast  →  common, parser, semanticContext
semanticAnalyzer  →  ast, common, semanticContext
transpiler  →  ast, semanticContext
semanticContext  →  (nothing)
```

- **parser** — Java-only, `antlr` plugin. Split lexer/parser grammars in [parser/src/main/antlr/](parser/src/main/antlr/); generator flags, output directory and package are set in [parser/build.gradle.kts](parser/build.gradle.kts) (visitors only, no listeners). `parser/src/main/gen/` is stale IDE-plugin output, gitignored and **not** part of the build — ignore it.
- **ast** — visitors turn the ANTLR parse tree into the AST in [ast/src/main/kotlin/tree/](ast/src/main/kotlin/tree/).
- **semanticContext** — the mutable analysis state (`Context`, `Scope`, `Symbol`, `ExpressionType`) that AST nodes carry. It is its own module precisely so `ast` can hold analysis results without depending on `semanticAnalyzer`.
- **semanticAnalyzer** — "decorators" that walk the AST and fill in each node's `context`.
- **transpiler** — C emitters that read the decorated AST and write to an `OutputStream`.

## Pipeline

[RheniumCompiler.compile](app/src/main/kotlin/RheniumCompiler.kt) is the whole story:

1. `CharStreams.fromFileName` → `IAstBuilder.parse` → `ParseTreeFactory` (lexer/parser) → `RootVisitor` → `RootNode`.
2. `ISemanticAnalyzer.decorateSemanticContext(ast)` — mutates the AST in place: sets `relevantScope`, resolves types, registers symbols.
3. `ITranspiler.transpile(ast, outputStream)` — emits the C prologue (includes plus the `float32_t`/`float64_t`/`boolean_t` typedefs the `cName`s rely on) then `int main(){ ... }`.
4. `clang <file>.c -o <file>.o -lm` then `./<file>.o`, both via `String.runCommand()` in **common**.

Each stage is a parallel tree of small classes, one per node kind: `visitors/` (ast) ↔ `tree/` (ast) ↔ decorators (semanticAnalyzer) ↔ `tree/` transpilers (transpiler). Adding syntax means touching all four, in that order, plus a `@Binds` in the relevant Dagger module.

## Conventions

**Dagger everywhere.** The pattern is uniform: an `interface IFoo`, a `@Singleton class Foo @Inject constructor() : IFoo`, and a `@Binds` entry in the module ([AstModule](ast/src/main/kotlin/AstModule.kt), [SemanticAnalyzerModule](semanticAnalyzer/src/main/kotlin/SemanticAnalyzerModule.kt), [CTranspilerModule](transpiler/src/main/kotlin/CTranspilerModule.kt), [AppModule](app/src/main/kotlin/AppModule.kt)). The root component is [RheniumCompilerFactory](app/src/main/kotlin/RheniumCompilerFactory.kt); kapt generates `DaggerRheniumCompilerFactory`. Tests get their own component (`AstTestComponent` → `DaggerAstTestComponent`). **Forgetting the `@Binds` is the usual cause of a kapt failure after adding a node type.**

The trees are mutually recursive, so dependencies are field-injected as `dagger.Lazy<IFoo>` and unwrapped through a `private val foo by lazy { provider.get() }`. Keep that idiom for anything that can recurse back into the injector.

**Multi-error reporting.** Sibling subtrees are visited through `and(left, right)`, `mapAllAndThrow`, and `forEachAllAndThrow` in [common](common/src/main/kotlin/me/eriknikli/rhenium/common/AggregateException.kt), which run every branch, collect the exceptions, and throw one flattened `AggregateException`. Don't replace these with plain calls — that would report only the first diagnostic. `RheniumCompiler.handleException` recursively unwraps `AggregateException` and logs anything implementing the `RheniumException` marker as a user error; everything else is "Fatal error." with a stack trace. Every concrete exception passes a formatted message to its `SyntaxException`/`SemanticException` super constructor, prefixed with `ParserRuleContext.location` (the `line:column` extension in **common**) — keep that shape so diagnostics stay locatable.

**Types and C names.** `ExpressionType` implementations (`SignedIntType`, `UnsignedIntType`, `FloatType`, `BooleanType`, `InvalidType`) each expose a `cName` that is emitted verbatim, and `canAssignTo` (implicit; same family, widening by `index`) vs `canAssignToExplicit` (casts). Primitives are seeded into the scope by `globalScope()` in [GlobalScope.kt](semanticContext/src/main/kotlin/me/eriknikli/rhenium/semanticContext/scope/GlobalScope.kt). Variables get a mangled `cName` of `re_<name>_<uuid>` so C never sees a shadowing collision.

Dependency versions are declared centrally — [gradle.properties](gradle.properties) for the compiler's own stack, [gradle/libs.versions.toml](gradle/libs.versions.toml) for the kotlinx catalog — and read back via `val x: String by project` / `libs.*`; do not inline a version in a module build file. Shared Kotlin/kapt/test setup is the convention plugin in [buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts](buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts); `parser` is the one module that doesn't use it.
