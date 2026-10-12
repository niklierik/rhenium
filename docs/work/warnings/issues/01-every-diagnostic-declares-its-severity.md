# 01: Every diagnostic declares its severity

**Spec:** [../spec.md](../spec.md)

**What to build:** Every diagnostic is printed with its severity after its position —
`4:9: error: unknown symbol 'b'.` Each diagnostic kind declares its own severity, and every existing
kind, the unreachable statement included, is an error for now. Nothing else about the compiler's
behaviour changes.

**Blocked by:** None (can start immediately)

**Status:** resolved

- [x] A two-value severity — error, warning — exists, and the diagnostic interface has an abstract
      severity property
- [x] Every existing diagnostic kind declares error as a property of its class, never a constructor
      argument
- [x] Rendering prints `line:column: severity: message` in lower case, still ordered by position
- [x] Every expected diagnostic string in the AST, analyzer and lowering seams gains `error:`
- [x] The build is green and no other behaviour changed
