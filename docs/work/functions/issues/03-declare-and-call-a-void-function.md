# 03: Declare and call a Void function

**What to build:** `fn f() { ... }` declares a function without parameters returning `Void`, and `f();`
calls it, from anywhere in the file whatever the order. Functions share the global scope with types.
See the [spec](../spec.md).

**Blocked by:** 01

**Status:** resolved

- [x] a call before the declaration, recursion and mutual recursion compile
- [x] each function is emitted with a C name `re_<name>_<uuid>`, prototypes before definitions
- [x] `function 'f' is already declared at …` for two functions, and for a function named like a type
- [x] `'f' is a function, not a value.` and `'a' is not a function.`
- [x] a local named like a function is reported
- [x] a function body does not see an entry's variables
- [x] `break` in a function called from inside a loop is `'break' is not inside a loop.`
- [x] `return;` ends a Void function; a statement after it is the unreachable warning
