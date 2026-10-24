# 11: A source path may contain spaces

**What to build:** before the functions effort the driver joined every argument into one path, so an
unquoted path with spaces worked. Now the first argument is the path and the second the entry name, so
such a path silently loses its second word to the entry name. Decide between requiring the shell to
quote the path (and saying so), or a flag for the entry name such as `--entry Main`.

**Blocked by:** None (can start immediately)

**Status:** needs-triage

## Comments

Found by the two-axis review of `cccfb8a` (spec axis, finding b1).
