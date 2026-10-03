# 09: Pin unsigned compound addition

**Spec:** [../spec.md](../spec.md)

**What to build:** Ticket 02 asked for a lowering case of `U32 +=` without the detour; the case
written uses `-=`. Add the `+=` case so the claim the ticket made is the claim the tests make.

**Blocked by:** None (can start immediately)

**Status:** ready-for-agent

- [ ] A lowering case shows `U32 +=` assigning the cast sum with no detour
