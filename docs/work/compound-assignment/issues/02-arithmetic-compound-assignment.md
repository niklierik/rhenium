# 02: Arithmetic compound assignment

**Spec:** [../spec.md](../spec.md)

**What to build:** `x += e`, `x -= e`, `x *= e`, `x /= e` and `x %= e` compile and run, meaning exactly
`x = x op e`. They are statements only. The emitted C is `x = (T)(x op e)` with the unsigned detour
where the binary operator takes it — never C's own `+=`.

`let s = 0; let i = 0; while (i < 4) { s += i; i = i + 1; } println s;` prints `6`.

**Blocked by:** 01

**Status:** ready-for-agent

- [ ] Lexer tokens for the five operators; a `statement` alternative for compound assignment
- [ ] Compound assignment statement node, context, decorator, lowerer, with `@Binds` in each module
- [ ] Accepted and rejected exactly where `x = x op e` is: `f32 += i32` accepted; `i8 += 1` reports
      `type mismatch, found I32 but expected I8.`
- [ ] `flag += 1` reports `illegal compound assignment 'Boolean += I32'.`
- [ ] `u32 += i32` reports `cannot mix signed and unsigned operands in 'U32 += I32'.`
- [ ] A `const` target reports the existing immutable-left-value diagnostic
- [ ] An unknown target reports once; a broken left and right side are both reported
- [ ] Lowering cases: `I32 +=` with the detour, `U32 +=` without, `/=` without
- [ ] AST case with the renderer taught the node; `y = x += 1;` and `a += b += c;` pinned as parse failures
- [ ] Language reference states the side-effect-free left-value rule and defers `??=` to nullables
- [ ] CLAUDE.md reminds that a new binary operator must decide its compound form
- [ ] Roadmap lists arithmetic compound assignment as implemented
- [ ] Hand-run: the summing loop above prints `6`
