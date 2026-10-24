# 08: A colliding parameter or local is still declared, poisoned

**What to build:** a parameter or local variable whose name is already taken is reported once, and its
uses are not reported again. Today the colliding declaration is never added to the scope, so every use
resolves to the function or type that owns the name: `fn f(f: I32) { println f; }` reports
`variable 'f' is already declared at 1:1.` and then `'f' is a function, not a value.` That breaks the
rule that one mistake gives one message, and CLAUDE.md's "a failed declaration still declares its
variable, poisoned". The local case (`let f = 1; println f;` beside `fn f`) predates functions through
the `takenByAType` check, but functions make it far easier to hit. The existing test for a parameter
named like a function has an empty body, which hides the cascade.

**Blocked by:** None (can start immediately)

**Status:** resolved

- [x] `fn f(f: I32) { println f; }` reports only the collision
- [x] `fn f() {}` with `entry Main { let f = 1; println f; }` reports only the collision
- [x] the same holds for a local or parameter named like a type, e.g. `Boolean`
- [x] the declaration's uses inside its scope resolve to the poisoned variable, not the outer symbol
- [x] existing collision and shadowing cases keep their wording

## Comments

Found by the two-axis review of `cccfb8a` (spec axis, finding c1).
