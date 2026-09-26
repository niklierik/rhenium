# 03: Declarations may not shadow

**Spec:** [../spec.md](../spec.md)

**What to build:** A programmer is told when a declaration hides a variable from an enclosing block, so
they can never read the wrong one by accident. `let a = 1; { let a = 2; }` is rejected, and the message
says the inner `a` shadows the one declared further out, naming where that is.

A name used twice in the *same* block keeps the message it has today — two different mistakes, two
different fixes.

**Blocked by:** 02

**Status:** resolved

- [x] A declaration is rejected when its name already resolves anywhere up the scope chain
- [x] A collision in the same scope reports the existing already-declared diagnostic, with its existing
      wording unchanged
- [x] A collision with an enclosing scope reports a new shadowing diagnostic, in its own file, carrying
      its parse node so line and column derive from it, and naming the location of the declaration it
      collided with
- [x] Only left-value symbols collide; nothing is written to anticipate user-declared type names, which
      cannot be spelled yet
- [x] A rejected declaration still declares its variable, poisoned, so one bad name costs one message
- [x] Shadowing a poisoned outer variable is reported alongside the original diagnostic, not suppressed
- [x] Tested at the semantic analyzer seam: the shadow rejected with the outer location; the same-block
      collision still reported as before; the shadow of a poisoned variable reported together with the
      diagnostic that poisoned it; a sibling-block reuse of a name still accepted
- [x] The build is green
