# 02: Only an error stops the build, the entry point runs the program

**Spec:** [../spec.md](../spec.md)

**What to build:** Compiling collects every diagnostic and only an error stops the build. A program
whose analysis produced diagnostics but no error is lowered, transpiled and built. Running the binary
moves to the entry point, which prints every diagnostic first, exits non-zero if any is an error, and
otherwise runs the program — so diagnostics always precede the program's output. No warning exists
yet, so observable behaviour is unchanged.

**Blocked by:** 01

**Status:** resolved

- [x] A partition helper next to the renderer answers whether a list of diagnostics contains an error
- [x] Compiling returns every diagnostic produced: those from parsing; those from decorating, warnings
      included, when it contains an error, with nothing written next to the source; otherwise the
      decorate stage's warnings, after the program is built
- [x] The binary's path lives on the compiler options, shared by compiling and the entry point
- [x] The entry point prints every diagnostic to standard error, exits non-zero on any error, and
      otherwise runs the binary
- [x] Checked by hand: a program with an error exits non-zero without running; a clean program runs
- [x] The build is green
