# 06: The docs match the code and the glossary

**Spec:** [../spec.md](../spec.md)

**What to build:** The contributor guide describes the pipeline as built: a left of `Diagnosed` may now
hold only warnings, and compiling — not the entry point — is what decides whether there is a program.
The warnings spec and its tickets use the glossary's words, not the ones it says to avoid. The
roadmap's known-gaps bullet about warnings on a value is kept and the spec records it as intended.

**Blocked by:** 04, 05

**Status:** ready-for-agent

- [ ] The contributor guide no longer says a left means there is no value, nor that only one function
      asks whether a diagnostic is an error, and describes what compiling hands back
- [ ] The warnings spec and tickets no longer use "fatal", "level" or "failure" for diagnostics;
      ticket 02's file is renamed to match
- [ ] The spec records the roadmap's known-gaps bullet as intended
- [ ] The build is green
