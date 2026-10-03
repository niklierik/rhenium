# 08: The increment's one is made as a literal is

**Spec:** [../spec.md](../spec.md)

**What to build:** No behaviour change. The increment's lowerer decides on its own that an unsigned
one is spelled `1u`, restating the rule the literal lowerer already owns for every numeric literal. The
one is made through that same rule, so a change to how literals are emitted reaches the increment's
one too.

**Blocked by:** 05

**Status:** ready-for-agent

- [ ] The unsigned-suffix rule for a numeric constant exists in one place
- [ ] The increment lowering cases for `I8`, `U8`, `F32` and `F64` pass unchanged
