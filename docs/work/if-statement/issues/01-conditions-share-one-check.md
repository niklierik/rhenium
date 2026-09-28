# 01: Conditions share one check

**Spec:** [../spec.md](../spec.md)

**What to build:** The rule "a condition must be a `Boolean`, and is told so even when it already
failed" moves out of the while statement's decorator into a **condition** decorator of its own that any
construct with a condition can call. `while` is its first caller and behaves exactly as before:
`while (1) { }` still reports `type mismatch, found I32 but expected Boolean.`, and `while (1 + true) { }`
still reports both the illegal binary operation and the mismatch.

This is the prefactor that makes the `if` tickets a matter of calling it, and it is where the promise in
[ADR 0003](../../../adr/0003-a-while-condition-is-checked-even-when-poisoned.md) that `if` and `for`
inherit the rule becomes structural rather than a convention.

**Blocked by:** None (can start immediately)

**Status:** resolved

- [x] A condition decorator exists in the semantic analyzer, following the project's interface /
      singleton / `@Binds` pattern: it decorates an expression against a scope and requires
      `BooleanType`, reporting the existing `TypeMismatch` with `Boolean` as the only expected type
- [x] It reports the mismatch even when the expression is of the poison type, and returns the
      expression's own diagnostics alongside it, so one call yields every fault of a condition
- [x] The while statement's decorator calls it for its condition and accumulates its result with the
      body as before; it no longer checks the type itself
- [x] ADR 0003 is amended in place: retitled "A condition is checked even when poisoned", noting that
      the rule covers every construct with a condition and is enforced by the shared decorator
- [x] `CONTEXT.md` gains a **Condition** entry: the `Boolean` expression that decides whether an `if`
      branch or a loop body runs, checked by one decorator for every construct that has one, an integer
      rejected rather than compared with zero; _Avoid_: predicate, test, guard
- [x] Every existing `while` test passes unchanged; no test is added or edited for the refactor
- [x] The build is green and no existing behaviour changed
