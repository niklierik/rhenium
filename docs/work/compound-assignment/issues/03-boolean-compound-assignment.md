# 03: Boolean compound assignment

**Spec:** [../spec.md](../spec.md)

**What to build:** `ok &&= e` and `ok ||= e` on `Boolean` variables, meaning exactly `ok = ok && e`
and `ok = ok || e`, short-circuit included: the action tree keeps the `&&` nested, so `e` is not
evaluated once `ok` already settles the result.

**Blocked by:** 02

**Status:** resolved

- [x] Lexer tokens for `&&=` and `||=`, taken by the compound assignment rule
- [x] Accepted on two `Boolean`s; `n &&= true` with an `I32` `n` reports
      `illegal compound assignment 'I32 &&= Boolean'.`
- [x] Lowering case shows a nested `&&` inside the assignment, with no cast
- [x] Roadmap updated
