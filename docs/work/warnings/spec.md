# Warnings

**Status:** resolved

## Problem Statement

The compiler can only report errors. Every diagnostic stops the build, so anything that deserves a
report without failing it has to be either an error or silence. The
[break-continue spec](../break-continue/spec.md) already had to make that choice wrongly: a statement
after `break` or `continue` is legal — it just never runs — yet it is rejected as an unreachable
statement, because there was no other way to say anything about it. The
[while-loop spec](../while-loop/spec.md) wanted a report for a loop that cannot terminate and could
not have one at all.

A programmer whose code is legal but probably not what they meant should be told so, and the program
should still be built and run.

## Solution

Every diagnostic gets a **severity**: it is an **error** or a **warning**. An error stops the program
being compiled, as every diagnostic does today. A warning is printed and the program is compiled and
run anyway.

Every diagnostic is printed with its severity after its position:

```
3:5: warning: unreachable statement after 'break' at 2:5.
4:9: error: unknown symbol 'b'.
```

Errors and warnings are printed together, ordered by position, before the program runs. A program
with at least one error is not compiled and the compiler exits non-zero; its warnings are still
printed. A program with only warnings is compiled and run, its warnings appearing before any of its
output.

The first warning is the existing unreachable statement, downgraded from an error:

```
while (c) {
    break;
    x = 1;
}
```

now reports `3:5: warning: unreachable statement after 'break' at 2:5.` and the program runs.

## User Stories

1. As a Rhenium programmer, I want a statement after `break` or `continue` to be reported but not
   rejected, so that I can leave dead code in a loop while I am working on it.
2. As a Rhenium programmer, I want every diagnostic to say whether it is an error or a warning, so that
   I know which ones I must fix before the program will run.
3. As a Rhenium programmer, I want warnings printed before my program's output, so that they are not
   lost below or mixed into what the program prints.
4. As a Rhenium programmer, I want a program with only warnings to compile and run, so that a warning
   never blocks me.
5. As a Rhenium programmer, I want a program with an error and a warning to report both, so that
   fixing the error does not reveal a warning I could have seen already.
6. As a Rhenium programmer, I want errors and warnings ordered by position in one list, so that I read
   them in the order of my source.
7. As a Rhenium programmer, I want the compiler to exit non-zero only when there is an error, so that a
   script driving it treats a warning as success.
8. As a Rhenium programmer, I want an error inside an unreachable statement to still be reported as an
   error, so that dead code cannot hide a real mistake.
9. As a Rhenium programmer, I want a misplaced `break` with a statement after it to still report only
   that it is outside a loop, so that the downgrade does not bring back a suppressed diagnostic.
10. As a Rhenium programmer, I want the diagnostic format to match clang's `line:column: severity:
    message`, so that it reads like the C toolchain underneath.
11. As a compiler contributor, I want severity declared by every diagnostic kind, so that adding a
    diagnostic forces a decision about its severity rather than inheriting one silently.
12. As a compiler contributor, I want severity to belong to the kind of diagnostic and never to one
    occurrence, so that the same mistake is always reported the same way.
13. As a compiler contributor, I want `Diagnosed` and every walker's accumulation left as they are, so
    that adding warnings touches no decorator's signature.
14. As a compiler contributor, I want the decision "does any of this stop the build" made once, after
    every diagnostic is collected, so that no walker has to know about severity.
15. As a compiler contributor, I want a written rule for where a warning may be raised, so that a
    future warning cannot poison a value it was only meant to comment on.
16. As a compiler contributor, I want a program with only warnings to be lowered from its fully
    decorated tree, so that lowering never meets a node a warning left undecorated.
17. As a compiler contributor, I want the unreachable statement's lowering pinned, so that dead code
    after `break` is known to reach the emitted C without breaking it.
18. As a compiler contributor, I want warnings limited to the decorate stage, so that a warning can
    never cost the AST the parse stage builds.
19. As a compiler contributor, I want the glossary to define error, warning and severity, so that a
    "warning" means "legal but suspicious" and never "illegal but tolerated".
20. As a compiler contributor, I want the roadmap and the break-continue spec to stop calling the
    unreachable statement a temporary error, so that the documents match the compiler.
21. As a thesis reader, I want warnings built on the existing diagnostic values rather than a second
    reporting channel, so that "errors are values" still describes the whole compiler.

## Implementation Decisions

**A severity is a property of the diagnostic, nothing more.** A two-value severity — error, warning —
and a severity property on the diagnostic interface. The property is **abstract**: every diagnostic
kind declares its own, so a new diagnostic does not compile until its author chooses. It is fixed per
kind — a property of the class, never a constructor argument. The unreachable statement is a warning;
every other existing diagnostic is an error.

**`Diagnosed` is not reshaped.** It stays `EitherNel<Diagnostic, T>`, and a warning is a left like any
other diagnostic. This works because the decorate stage returns `Unit` and decorates the AST in place:
losing the right loses nothing, and poison recovery already guarantees every node is decorated even
when a left is produced. No decorator signature and no accumulation changes. The placeholder version
of this spec assumed a reshape to carry warnings beside a value; grilling found it unnecessary.

**A warning is raised only from a `Unit`-valued check that runs as its own sibling branch** — a branch
of `zipOrAccumulate` or an element of `mapOrAccumulate` — never on a path that produces a value or
guards a later check. A left on a value path still behaves as an error locally: a declaration would be
poisoned and the checks after it skipped, so a warning there would poison real code and could hide an
error. The unreachable check already has this shape. The rule is written into the "errors are values"
conventions in the contributor guide. A future warning that genuinely needs a value path reopens this
decision; Arrow's `Ior` is the known alternative.

**Warnings come only from the decorate stage.** The parse stage returns its tree as the right of a
`Diagnosed`, so a left there would lose the tree. Every diagnostic in the AST module is an error.

**Rendering** puts the severity after the position — `line:column: error: message` or
`line:column: warning: message` — in lower case, ordering all diagnostics by position as today.

**Partition helper.** A function next to the renderer answers whether a list of diagnostics contains
an error. It is the one definition of which diagnostics stop the build.

**The compiler stops before running the program.** Compiling hands back every diagnostic produced,
together with the built program or with none:

- diagnostics from parsing, all errors, come back with no program;
- a decorate left that contains an error comes back whole, warnings included, with no program, and
  nothing is written next to the source;
- otherwise the program is lowered, transpiled and built with clang, and the warnings — if any — come
  back with the program, unless clang exits non-zero, which leaves no program.

Running the binary moves out of compiling and into the entry point. Both output paths — the C file and
the binary — live on the compiler options. The entry point prints every diagnostic to standard error,
exits non-zero when there is no program, and otherwise runs it — so warnings always precede the
program's output. It never inspects a severity itself.

**The unreachable statement keeps its message, position and suppression.** It is reported once per
block at the first dead statement, the dead statements are still decorated, and a loop control
statement outside a loop still suppresses it. Only its severity changes. The dead statements are
lowered and emitted after the loop control statement, which C accepts.

**Documents.** The glossary already defines diagnostic, severity, error and warning. On completion the
roadmap's known-gaps entry for warnings is replaced by a line in "what the compiler implements today",
and the break-continue spec's note that the unreachable error is temporary points here as done.

## Testing Decisions

A good test feeds source text in and compares rendered output — diagnostics or an action tree — as a
string. Nothing asserts on a severity property, a list's shape or the partition helper directly; they
are observed through the output they produce. No new seam is introduced.

**Semantic analyzer seam — source text in, rendered diagnostics out.** Every expected string gains its
severity. The unreachable cases become warnings: one statement after `break`; two after `continue`
giving one warning; an error inside an unreachable statement reported as an error beside the warning;
a misplaced `break` with a statement after it giving only the outside-a-loop error. A new case pins an
error and a warning in one program, rendered together in position order.

**Lowering seam — source text in, action tree out.** The harness fails only when analysis produced an
error, not on any left, using the partition helper. A new case lowers a loop with a statement after
`break` and pins the dead statement inside the while action's body block after the break action.

**AST seam — source text in, tree or diagnostics out.** Expected diagnostic strings gain `error:`.

Beyond the seams: a program with only an unreachable statement, run by hand through the application,
checked to print the warning before its output and exit zero; and a program with one error and one
warning, checked to print both and exit non-zero. Not committed — there is still no end-to-end harness.

## Out of Scope

- A warning for `while (true)` with no `break`. It builds on this, and needs its own spec for what
  counts as a constant condition and which `break` counts.
- Unused-variable, unused-value and constant-condition warnings.
- Treating warnings as errors, and silencing a warning by kind or by location.
- Warnings from the parse stage, and from lowering.
- Reshaping `Diagnosed` to `Ior` or any carrier that keeps a value beside a warning. The roadmap keeps
  this as a known gap on purpose, so the first warning that needs a value path finds it.
- Propagating the program's own exit code.

## Further Notes

No ADR. Keeping `Diagnosed` as it is fails the "hard to reverse" test: nothing is reshaped, and the
`Ior` route stays open for the first warning that needs it.

Glossary terms added: **severity**, **error** and **warning**. **Diagnostic** now carries a severity and
no longer means "something the user got wrong", and `warning` and `error` left its avoid list.
