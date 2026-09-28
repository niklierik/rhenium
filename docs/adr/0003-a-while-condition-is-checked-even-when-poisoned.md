# A condition is checked even when poisoned

Everywhere else in this compiler the poison type suppresses the consequences of a failed expression, so
that one mistake produces one diagnostic. A condition is the exception: when the condition's type is
`InvalidType`, its type mismatch is reported anyway, so `while (1 + true) { }` yields both
`illegal binary operation 'I32 + Boolean'` and `type mismatch, found <invalid> but expected Boolean.`

This is recorded because it looks like a missing `if (type is InvalidType) return` and reads as a bug to
anyone who has just read the poison-type convention in CLAUDE.md. It is deliberate: the requirement that
a condition be a `Boolean` is a property of the construct the programmer is writing, not a
consequence of the expression they got wrong, and they are told it whatever else is broken.

The rule was first written for `while`. It now covers every construct with a condition, and is enforced
by the one condition decorator they all call rather than by each construct's own decorator, so the
constructs cannot drift apart about it.

## Consequences

The extra message is genuinely redundant in the case above — fixing `1 + true` makes it disappear
without the programmer having done anything about it, which is the cost the convention exists to avoid.
Because every construct shares the check, the language stays consistent about when a condition's
requirement is stated. If the redundant messages turn out to be noise once conditions are everywhere,
reversing this is a one-line suppression in the condition decorator and a handful of test expectations.
