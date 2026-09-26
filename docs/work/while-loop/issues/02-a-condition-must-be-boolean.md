# 02: A condition must be Boolean

**Spec:** [../spec.md](../spec.md)

**What to build:** The loop rejects any condition that is not a `Boolean`. `while (1) { }` reports
`type mismatch, found I32 but expected Boolean.` Nothing numeric is accepted — C's "non-zero is true"
is exactly what the language's `Boolean` type exists to avoid.

A condition that already failed is reported anyway. `while (1 + true) { }` produces two diagnostics:
the illegal binary operation, and the condition's type mismatch. This departs from the poison-type
convention on purpose — see [ADR 0003](../../../adr/0003-a-while-condition-is-checked-even-when-poisoned.md).

**Blocked by:** 01

**Status:** resolved

- [x] The loop's decorator checks the condition's type and reports the existing `TypeMismatch`
      diagnostic, with `Boolean` as the only expected type; no new diagnostic class is added
- [x] The check accumulates with the body rather than running before it, so a loop with both a bad
      condition and a broken body statement reports both faults in one run
- [x] A condition of the poison type is reported rather than suppressed, and the diagnostic the
      condition itself produced is reported alongside it
- [x] Tested at the semantic analyzer seam: a `Boolean` literal condition and a relational condition
      accepted with no diagnostics; an integer and a float condition each rejected, naming what was
      found and `Boolean`; a poisoned condition reporting both messages; a bad condition and a broken
      body statement reporting both; two loops in one program both reporting
- [x] Verified by hand: `while (1) { }` is rejected and no C file is written next to the source
- [x] The build is green and no existing behaviour changed
