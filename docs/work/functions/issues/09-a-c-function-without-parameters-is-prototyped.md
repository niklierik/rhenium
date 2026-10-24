# 09: A C function without parameters says so

**What to build:** functions and entries without parameters, and `main`, are emitted as `name(void)`
rather than `name()`. Before C23 empty parentheses declare a function with unspecified parameters, so
clang cannot catch a wrong argument count in the emitted C. The semantic analyzer already rejects one,
so this is a safety net, not a user-visible fix. The decision belongs to lowering or the signature's
printing, and the transpiler stays free of decisions.

**Blocked by:** None (can start immediately)

**Status:** resolved

- [x] a parameterless prototype, definition and `main` print `(void)`
- [x] a function with parameters prints as today
- [x] transpiler seam tests pin the new spelling — it is C spelling of an empty parameter list, printed with the signature, so lowering has nothing to pin

## Comments

Found by the two-axis review of `cccfb8a` (spec axis, finding c3).
