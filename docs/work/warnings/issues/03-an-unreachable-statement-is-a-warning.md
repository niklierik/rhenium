# 03: An unreachable statement is a warning

**Spec:** [../spec.md](../spec.md)

**What to build:** A statement after `break` or `continue` is reported as
`3:5: warning: unreachable statement after 'break' at 2:5.` and the program still compiles and runs,
the warning printed before its output.

**Blocked by:** 01, 02

**Status:** ready-for-agent

- [ ] The unreachable statement declares warning; its message, position and suppression are unchanged
- [ ] Analyzer cases: the unreachable cases render as warnings; an error inside an unreachable
      statement renders as an error beside the warning; a misplaced `break` with a statement after it
      still gives only the outside-a-loop error; a new case pins an error and a warning in one program,
      in position order
- [ ] The lowering harness fails only when analysis produced an error; a new case pins the dead
      statement inside the while action's body block, after the break action
- [ ] The contributor guide's "errors are values" conventions state where a warning may be raised
- [ ] The roadmap lists warnings as implemented rather than as a known gap; the break-continue spec no
      longer calls the unreachable error temporary; the warnings spec is marked resolved
- [ ] Checked by hand: a program with only an unreachable statement prints the warning before its
      output and exits zero; one with an error and a warning prints both and exits non-zero
- [ ] The build is green
