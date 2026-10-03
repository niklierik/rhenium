# 07: An increment statement steps up or down

**Spec:** [../spec.md](../spec.md)

**What to build:** No behaviour change. An increment statement holds a general binary operator, which
admits any operator although only `+` and `-` mean anything, so "increment or decrement" is re-derived
from it in the diagnostic, the visitor and the test renderer. Give the step its own two-valued type —
up or down — which knows the binary operator it lowers through and the word and spelling it is
reported with.

**Blocked by:** 06

**Status:** resolved

- [x] The increment statement node carries the two-valued step, not an operator
- [x] Nothing outside the step type decides between "increment" and "decrement"
- [x] Every existing test passes unchanged
