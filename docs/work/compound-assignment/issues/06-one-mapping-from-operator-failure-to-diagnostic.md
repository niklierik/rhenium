# 06: One mapping from an operator failure to its diagnostic

**Spec:** [../spec.md](../spec.md)

**What to build:** No behaviour change; every diagnostic keeps its exact wording. Today the binary
operator's decorator, the compound assignment's decorator and the increment's decorator each run the
shared binary rule and each turn its failure into a diagnostic with their own copy of the same `when`.
The same four values — parse node, left type, right type, operator — travel together through all of
them and through three diagnostic classes.

Give the operator *as written* a type of its own — the operator plus whether it was written plain
(`+`), as a compound assignment (`+=`) or as an increment (`++`) — so that its spelling is decided once.
The failure-to-diagnostic mapping then lives in one place, the mixed-signedness diagnostic carries that
type again instead of a string, and the `op=` spelling is no longer assembled in two files.

**Blocked by:** None (can start immediately)

**Status:** resolved

- [x] The written operator's spelling (`+`, `+=`, `++`) is produced in exactly one place
- [x] The mixed-signedness diagnostic takes a domain type, not a string
- [x] The three decorators share one mapping from a rule failure to a diagnostic
- [x] Every existing analyzer test passes with unchanged expected messages
