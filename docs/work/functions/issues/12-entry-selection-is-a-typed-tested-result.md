# 12: Entry selection is a typed, tested result

**What to build:** a failure to pick an entry is a bare string in `Compilation.problem`, built inside
the compiler and never tested; the spec only asked for a hand run. Decide whether the four outcomes
(no entry, unknown name, several and none named, picked) become a small typed result the driver renders,
so each branch can be tested without running clang, and whether "problem" deserves a glossary term or a
better name.

**Blocked by:** None (can start immediately)

**Status:** needs-triage

## Comments

Found by the two-axis review of `cccfb8a` (standards axis, finding 2; spec axis, finding a).
