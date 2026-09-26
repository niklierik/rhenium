# 02: Declarations are local to their block

**Spec:** [../spec.md](../spec.md)

**What to build:** What a block declares stops existing at its closing brace. `{ let b = 1; } println b;`
reports `unknown symbol 'b'`. A block still sees what encloses it, so `let a = 1; { println a; }` keeps
working.

Because a block's names are its own, two sibling blocks can each declare the same name, and a declaration
after a block can reuse a name the block used.

**Blocked by:** 01

**Status:** resolved

- [x] The block's decorator creates a child scope and walks its children against it
- [x] The block's own context records the enclosing scope, as the statement dispatcher sets it for every
      statement kind; the child scope stays local to the decorator
- [x] Name lookup from inside a block finds the enclosing scopes' variables, using the chain lookup the
      scope type already has
- [x] Visibility is sequential — a block cannot see a declaration that comes after it in an enclosing
      scope
- [x] Emitted C is unaffected: variables already carry a mangled name, so nothing collides
- [x] Tested at the semantic analyzer seam: an inner reference resolving outwards; an inner declaration
      invisible after the block; two sibling blocks declaring the same name; a declaration after a block
      reusing that name; a reference from inside a block to a later declaration reported as an unknown
      symbol; a valid nested program producing no diagnostics
- [x] Verified by hand: `let a = 1; { let b = a + 1; println b; }` compiles, links and prints `2`
- [x] The build is green
