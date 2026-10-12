# 05: A clang failure stops the build

**Spec:** [../spec.md](../spec.md)

**What to build:** When clang exits non-zero there is no program to run, and the compiler exits
non-zero instead of running a missing or stale binary. Clang's own output still reaches the user.

**Blocked by:** 04

**Status:** ready-for-agent

- [ ] Running a command reports its exit code
- [ ] Compiling hands back no program when clang exits non-zero
- [ ] Checked by hand: a clang failure exits non-zero and runs nothing
- [ ] The build is green
