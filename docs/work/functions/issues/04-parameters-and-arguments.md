# 04: Parameters and arguments

**What to build:** `fn f(a: I32, const b: I32)` declares parameters, mutable unless `const`; a call
passes one argument per parameter, each checked as an assigned value. See the [spec](../spec.md).

**Blocked by:** 03

**Status:** resolved

- [x] parameters are usable in the body and assignable unless `const`
- [x] `'f' takes 2 arguments, but 3 were given.`
- [x] an `I8` argument passes to an `I32` parameter; the reverse is a type mismatch at the argument
- [x] a broken argument is reported once
- [x] two parameters with one name, a parameter named like a function or type, and a local named like a parameter are reported
- [x] an unknown parameter type is reported and the function still declared
- [x] `'Void' has no value.` for a parameter of type `Void`
