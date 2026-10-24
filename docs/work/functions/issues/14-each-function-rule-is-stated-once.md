# 14: Each function rule is stated once

**What to build:** rules the functions effort wrote twice are written once. Behaviour does not change;
every existing test keeps passing.

**Blocked by:** None (can start immediately)

**Status:** resolved

- [x] "a `Void` type has no value" is one check, shared by type names and expressions
- [x] "this return type needs no value" (`Void` or poisoned) is one rule, shared by the return statement and the every-path check
- [x] the entry and function declaration lowerers share how they build a function action
- [x] the source text a diagnostic quotes comes from one helper beside `location`, not a chain inside `VoidHasNoValue`
- [x] the spec's "`Void` … assignable to nothing" says it is assignable only to the poison type, as every type is

## Comments

Found by the two-axis review of `cccfb8a` (standards axis, duplicated code and message chain; spec axis, finding c2).
