# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

Rhenium is a compiler for a small statically-typed language that **transpiles to C**, then shells out to `clang` and executes the result. Kotlin/JVM, Gradle multi-module, ANTLR4 for the front end, Dagger 2 for wiring.

## Commands

```bash
./gradlew build                                # compile + test
./gradlew :ast:test                            # one module's tests
./gradlew :ast:test --tests "AstBuilderTests"  # one test class
./gradlew :parser:generateGrammarSource        # regenerate the ANTLR sources
./gradlew run --args="path/to/program.re"      # compile and execute a program
```

The daemon JVM is pinned by [gradle/gradle-daemon-jvm.properties](gradle/gradle-daemon-jvm.properties); on a mismatch Gradle says so and names the version it wants.

`run` takes a path to a `.re` file, resolved against the `app/` project directory. Output lands next to the source: `<path>.c`, then the binary, which is executed immediately. `clang` must be on PATH. To work from the repo root instead, `./gradlew :app:installDist` and run `app/build/install/app/bin/app`.

`src/` is gitignored — it holds scratch `.re` programs, not shipped code.

AST nodes are data classes carrying their `ParserRuleContext`, which compares by identity — so a parsed tree can never equal a hand-built expected node. [AstBuilderTests](ast/src/test/kotlin/AstBuilderTests.kt) therefore renders the tree to an s-expression and compares strings; add cases to its data source and teach the renderer any new node kind.

## Module graph

```
app  →  common, parser, ast, semanticAnalyzer, semanticContext, transpiler
ast  →  common, parser, semanticContext
semanticAnalyzer  →  ast, common, semanticContext
transpiler  →  ast, semanticContext
semanticContext  →  (nothing)
```

- **parser** — Java-only, `antlr` plugin. Split lexer/parser grammars in [parser/src/main/antlr/](parser/src/main/antlr/); generator flags, output directory and package are set in [parser/build.gradle.kts](parser/build.gradle.kts) (visitors only, no listeners). Generated sources live under the build directory — never edit or commit them.
- **ast** — visitors turn the ANTLR parse tree into the AST in [ast/src/main/kotlin/tree/](ast/src/main/kotlin/tree/).
- **semanticContext** — the mutable analysis state (`Context`, `Scope`, `Symbol`, `ExpressionType`) that AST nodes carry. It is its own module precisely so `ast` can hold analysis results without depending on `semanticAnalyzer`.
- **semanticAnalyzer** — "decorators" that walk the AST and fill in each node's `context`.
- **transpiler** — C emitters that read the decorated AST and write to an `OutputStream`.

## Pipeline

[RheniumCompiler.compile](app/src/main/kotlin/RheniumCompiler.kt) is the whole story:

1. `CharStreams.fromFileName` → `IAstBuilder.parse` → `ParseTreeFactory` (lexer/parser) → `RootVisitor` → `RootNode`.
2. `ISemanticAnalyzer.decorateSemanticContext(ast)` — mutates the AST in place: sets `relevantScope`, resolves types, registers symbols.
3. `ITranspiler.transpile(ast, outputStream)` — emits a C prologue (the includes and typedefs the `cName`s rely on) then `int main(){ ... }`.
4. Shells out to `clang`, then executes the produced binary, both via `String.runCommand()` in **common**.

Steps 1 and 2 each return `Diagnosed<T>`, and the chain is an `either { }` block, so step 3 is only reached for a program with no diagnostics — nothing is written next to the source until then. `Main` prints them and exits non-zero.

Each stage is a parallel tree of small classes, one per node kind: `visitors/` (ast) ↔ `tree/` (ast) ↔ decorators (semanticAnalyzer) ↔ `tree/` transpilers (transpiler). Adding syntax means touching all four, in that order, plus a `@Binds` in the relevant Dagger module.

## Conventions

**Dagger everywhere.** The pattern is uniform: an `interface IFoo`, a `@Singleton class Foo @Inject constructor() : IFoo`, and a `@Binds` entry in that module's Dagger `@Module`. The root component is [RheniumCompilerFactory](app/src/main/kotlin/RheniumCompilerFactory.kt); kapt generates `DaggerRheniumCompilerFactory`. Tests get their own component (`AstTestComponent` → `DaggerAstTestComponent`). **Forgetting the `@Binds` is the usual cause of a kapt failure after adding a node type.**

The trees are mutually recursive, so dependencies are field-injected as `dagger.Lazy<IFoo>` and unwrapped through a `private val foo by lazy { provider.get() }`. Keep that idiom for anything that can recurse back into the injector.

**Errors are values.** Anything the user can get wrong is a `Diagnostic` — `line`, `column`, `message` — returned, never thrown. Every walker's signature says so: `Diagnosed<T>` (alias for Arrow's `EitherNel<Diagnostic, T>`) is "a `T`, or every reason there isn't one". A thrown exception means a **compiler bug**, not bad input; an unhandled node kind in a `when` dispatch is the usual case and stays an `IllegalStateException`. Each diagnostic gets its own file under its module's `diagnostics/` package — one public type per file, as everywhere else in this repo — and implements `ContextDiagnostic` when it can point at a parse node, which derives `line` and `column` from it.

Diagnostics accumulate rather than short-circuit, which is the whole point — use Arrow's combinators for it and do not hand-roll collection:

- sibling children of one node → `zipOrAccumulate({ ... }, { ... }) { a, b -> ... }`, so a broken left operand never hides an error on the right
- a list of children → `mapOrAccumulate(children) { ... }`
- inside either, a child result is unwrapped with `.bindNel()`; a single check is `ensure(cond) { Diagnostic }`

Recovery is by **poison type**, not by aborting the subtree: a failed expression yields `InvalidType`, which absorbs every operation and assignment, and an unresolved identifier binds [ErrorSymbol](semanticContext/src/main/kotlin/me/eriknikli/rhenium/semanticContext/scope/vars/ErrorSymbol.kt). A failed declaration still declares its variable, poisoned. That is what keeps one mistake to one message instead of a cascade — there are tests pinning this, so if you add a rule, add the suppression too.

ANTLR reports through a callback, so [ParseTreeFactory](ast/src/main/kotlin/utils/ParseTreeFactory.kt) is the one place diagnostics are collected by mutation; its listener replaces ANTLR's console listener, and the list becomes a value again when it returns.

**Types and C names.** Every `ExpressionType` exposes a `cName` that is emitted verbatim, plus `canAssignTo` (implicit conversions) and `canAssignToExplicit` (casts); the numeric types order themselves by an `index` for width comparisons. Primitives are seeded into the scope by `globalScope()` in [GlobalScope.kt](semanticContext/src/main/kotlin/me/eriknikli/rhenium/semanticContext/scope/GlobalScope.kt). Variables get a mangled `cName` of `re_<name>_<uuid>` so C never sees a shadowing collision.

Dependency versions are declared centrally — [gradle.properties](gradle.properties) and [gradle/libs.versions.toml](gradle/libs.versions.toml) — and read back via `val x: String by project` / `libs.*`; do not inline a version in a module build file. Shared Kotlin/kapt/test setup lives in the convention plugin under [buildSrc/](buildSrc/src/main/kotlin/).
