# 04: Compiling says whether there is a program to run

**Spec:** [../spec.md](../spec.md)

**What to build:** Compiling hands back its diagnostics together with the built program, or with no
program when an error stopped the build. The entry point runs whatever program it is handed and no
longer asks again whether a diagnostic is an error, so that question is answered in one place. The C
file's path joins the binary's path on the compiler options. Behaviour is unchanged.

**Blocked by:** None (can start immediately)

**Status:** resolved

- [x] Compiling returns the diagnostics and either the built program's path or none
- [x] The entry point prints the diagnostics, exits non-zero when there is no program, and otherwise
      runs it; it does not inspect severities
- [x] Both output paths are derived on the compiler options, nowhere else
- [x] Checked by hand: a program with an error, one with only a warning and a clean one each behave as
      before
- [x] The build is green
