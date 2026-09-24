# Give the transpiler module a test harness

Status: resolved
Blocked by: 01

The `transpiler` module has no tests, and nothing anywhere in the suite asserts emitted C. Print is
the first feature whose correctness lives entirely in the emitted text — a wrong format specifier is
invisible to every existing test.

## Why it needs no new dependencies

This section originally argued the opposite, from a premise that expired — see the Comments. The
printer consumes an action tree, not a decorated AST, and actions are plain data classes carrying no
`ParserRuleContext`, so a fixture is a constructor call. The convention plugin already supplies
`kotlin("test")`, `junit-jupiter-params` and `useJUnitPlatform()` to every module.

Driving from source text was rejected as well as unnecessary: the printer makes no decisions, so
source-text fixtures would re-assert lowering's choices, and would add in test scope the `:ast` edge
`CLAUDE.md` calls deliberately absent.

## Tasks

- No change to the module's build file
- `TranspilerTestComponent`, following `LoweringTestComponent`, wiring `CTranspilerModule` alone
- Parameterized cases asserting emitted C from hand-built actions: every action kind, both
  `PrintAction` and `ReturnAction` null branches, both print keyword shapes, and a nested case

## Done when

`./gradlew :transpiler:test` runs cases that fail when a printer's punctuation changes, demonstrated
by mutation rather than by passing. The `cFormat` table itself is pinned by `LoweringTests`, which is
where that decision now lives.

## Comments

### The seam moved, because the reason for the new dependencies went away

"Why it needs new dependencies" was written when the printer consumed a decorated AST, where
`AstNode.parserContext` is a non-nullable `ParserRuleContext` and a hand-built fixture means
fabricating ANTLR objects. Since the action-tree lowering the printer consumes `Action`s: plain data
classes holding strings and `ExpressionType`s, with no parse context anywhere. Hand-building a
fixture is now a constructor call, so the tests need no `:parser`, `:semanticAnalyzer`, `:ast` or
`:common`, and the module's build file is unchanged. The convention plugin already supplies
`kotlin("test")`, `junit-jupiter-params` and `useJUnitPlatform()`.

Driving from source text was rejected for a second reason: the printer now makes no decisions, so
source-text fixtures would mostly re-assert lowering's choices, and a lowering change would fail
transpiler tests for something that is not the printer's fault. It would also add in test scope the
`:ast` edge `CLAUDE.md` calls deliberately absent.

`TranspilerTestComponent` therefore wires `CTranspilerModule` alone and exposes `IActionTranspiler`
for the per-action cases and `ITranspiler` for the prologue.

### Where the cFormat criterion went

"Done when `:transpiler:test` runs cases that would fail if any `cFormat` entry changed" no longer
describes this module. `PrintAction` carries its `cFormat` as a string chosen during lowering, so the
table itself is pinned by `LoweringTests`, which already asserts `"%" PRId32` and friends per type.
What these tests pin is that the printer writes the format it is handed, verbatim and in the right
place — a comma before the value, the value omitted entirely when there is none.

### Verified by mutation, not only by passing

All nineteen cases passed on the first run, which proves nothing on its own. Deleting the comma in
`CPrintTranspiler` and one parenthesis in `CCastTranspiler` fails four cases between them; both
mutations were reverted. Coverage is all thirteen action kinds, both `PrintAction` and `ReturnAction`
null branches, the empty block, and one nested case pinning that the unsigned detour survives into
the emitted C.
