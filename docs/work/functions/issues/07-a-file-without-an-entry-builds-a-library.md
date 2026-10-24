# 07: A file without an entry builds a library

**What to build:** a file with no entry compiles to an object file with `clang -c` instead of being
reported as having nothing to run, so that it can be linked once imports exist.

**Blocked by:** 02

**Status:** needs-triage

Deliberately left out of the functions effort; see the [spec](../spec.md).
