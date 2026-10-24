# 10: Entry and function C names cannot collide

**What to build:** an entry's C name `re_entry_<name>` and a function's `re_<name>_<uuid>` share the
`re_` prefix, so a function named `entry` gets `re_entry_<uuid>`, in the same space as entries. A real
clash is practically impossible, but the separation rests on luck rather than on construction. Decide
whether entries, functions and variables get distinct prefixes, or whether this is acceptable as is.

**Blocked by:** None (can start immediately)

**Status:** needs-triage

## Comments

Found by the two-axis review of `cccfb8a` (spec axis, finding c4).
