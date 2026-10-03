# 01: Share the binary operator rules

**Spec:** [../spec.md](../spec.md)

**What to build:** A prefactor with no behaviour change. The binary operator's type rule — poison
short-circuit, Boolean gate, numeric gate, mixed signedness, wider-operand result — becomes callable
with two types and an operator, returning the result type or the reason there is none, so a caller
without a binary operator node can use it and word its own diagnostic. The arithmetic lowering — cast
to the result type, unsigned detour on `+`, `-` and `*` — becomes applicable to operands that are
already actions.

The binary operator's decorator and lowerer are rewritten on top of both and emit exactly what they
did before.

**Blocked by:** None (can start immediately)

**Status:** resolved

- [x] Every existing test passes unchanged
- [x] The binary operator's decorator no longer owns the type rule; it maps the rule's failure reasons
      to its existing diagnostics
- [x] The binary operator's lowerer no longer owns the cast-and-detour; it calls the shared lowering
