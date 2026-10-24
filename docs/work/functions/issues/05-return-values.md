# 05: Return values

**What to build:** `fn f(): R` returns a value with `return e;`, and its calls are expressions of type
`R`. A `Void` call has no value. See the [spec](../spec.md).

**Blocked by:** 03

**Status:** resolved

- [x] `println Twice(2);` prints `4`
- [x] `return e;` is checked against `R`, and a poisoned `e` is not reported again
- [x] `'return' needs a value of type I32.` and `a Void function cannot return a value.`
- [x] `'f()' has no value.` for a Void call in a declaration, an operand, a print, a condition, an argument or a return
- [x] `'Void' has no value.` for a variable declared `Void`
- [x] `f();` discards a non-Void result silently
