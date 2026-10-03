# 04: Increment and decrement statements

**Spec:** [../spec.md](../spec.md)

**What to build:** `x++` and `x--` as statements of their own, on every numeric type, meaning
`x += 1` and `x -= 1` where the one has `x`'s own type. Postfix only. `I8(127)` incremented wraps to
`-128`, through the same unsigned detour as every other signed addition.

**Blocked by:** 01

**Status:** ready-for-agent

- [ ] A `statement` alternative using the existing `++` and `--` tokens; no `expression` alternative
- [ ] Increment statement node, context, decorator, lowerer, with `@Binds` in each module
- [ ] Accepted on every integer and float type
- [ ] `flag++` reports `illegal increment 'Boolean++'.`; `flag--` reports `illegal decrement 'Boolean--'.`
- [ ] A `const` target reports the existing immutable-left-value diagnostic; an unknown target reports once
- [ ] Lowering cases on `I8`, `U8`, `F32` and `F64`, each with a one of the matching type
- [ ] AST case with the renderer taught the node; `y = x++;`, `print x++;` and `x++ + 1;` pinned as
      parse failures
- [ ] Roadmap updated
- [ ] Hand-run: `let x = I8(127); x++; println x;` prints `-128`
