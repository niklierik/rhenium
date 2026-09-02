# Give the transpiler module a test harness

Status: ready-for-agent
Blocked by: 01

The `transpiler` module has no tests, and nothing anywhere in the suite asserts emitted C. Print is
the first feature whose correctness lives entirely in the emitted text — a wrong format specifier is
invisible to every existing test.

## Why it needs new dependencies

`transpiler` depends only on `:ast` and `:semanticContext`, and `AstNode.parserContext` is a
non-nullable `ParserRuleContext`, so building a decorated AST by hand means fabricating ANTLR context
objects. Driving the tests from source text instead mirrors `SemanticAnalyzerTests` and keeps the
fixtures readable as documentation of the feature.

## Tasks

- `testImplementation` on `:parser`, `:semanticAnalyzer` and `:common` in the module's build file
- `TranspilerTestComponent`, following `SemanticAnalyzerTestComponent`
- Parameterized cases asserting emitted C: one per printable type, both keywords, bare `println;`,
  and the boolean ternary

## Done when

`./gradlew :transpiler:test` runs cases that would fail if any `cFormat` entry changed.
