# Compound assignment and increment / decrement statements

**Status:** resolved

## Problem Statement

Accumulating into a variable means naming it twice: `count = count + 1;`, `total = total * factor;`,
`ok = ok && check;`. It is noise in every loop, and the loop is the construct the language just gained.
The [language reference](../../language-reference.md) already promises `+=`, `-=`, `*=`, `/=`, `%=`,
`x++` and `x--`, and none of them parse.

C has the same forms, and in C they are expressions. `y = x++ + ++x;` is legal C whose meaning most
readers cannot state, and whose behaviour is undefined. Rhenium wants the convenience without that
trap.

## Solution

**Compound assignment** — `x op= e` — for every binary operator that exists and makes sense to
accumulate into: `+=`, `-=`, `*=`, `/=`, `%=`, `&&=` and `||=`. `x op= e` means **exactly**
`x = x op e`: the same type rules, the same arithmetic, the same wraparound, the same short-circuit.
There is no second set of rules to learn.

**Increment statement** `x++` and **decrement statement** `x--`: a compound assignment of one, where
the one has the left value's own type. Postfix only — there is no `++x`.

All of them are **statements**, never expressions, exactly as `=` already is. `y = x++;`,
`print x++;`, `x++ + 1` and `a += b += c` do not parse.

```
let i = 0;
let sum = 0;
while (i < 4) {
    sum += i;
    i++;
}
println sum;
```

Prints `6`.

The left side must be a left value whose evaluation has no side effects, so naming it twice is
harmless. Today every left value is a bare variable, so the rule holds by construction; when member
access and indexing arrive, `obj.count++` and `arr[i] += e` are legal and `arr[next()] += e` is not.
The right side is any expression.

Because the meaning is `x = x op e`, `I8` arithmetic still happens at eight bits: `x += 1` with an
`I8` `x` is rejected — `1` is an `I32`, so `x + 1` is an `I32` that does not fit back — and is written
`x += I8(1)`. `x++` needs no such spelling, because its one already has `x`'s type, so it works on
every numeric type and `I8(127)` incremented wraps to `-128`.

## User Stories

1. As a Rhenium programmer, I want to write `x += e`, so that accumulating into a variable does not
   make me name it twice.
2. As a Rhenium programmer, I want `-=`, `*=`, `/=` and `%=` as well, so that every arithmetic operator
   accumulates the same way.
3. As a Rhenium programmer, I want `&&=` and `||=` on `Boolean` variables, so that I can fold a series
   of checks into one flag.
4. As a Rhenium programmer, I want `ok &&= check` to skip evaluating `check` once `ok` is false, so that
   the compound form short-circuits exactly as `ok = ok && check` does.
5. As a Rhenium programmer, I want `x op= e` to accept and reject exactly the programs `x = x op e`
   would, so that I never have to learn a second type rule.
6. As a Rhenium programmer, I want `x op= e` to compute at the same width and wrap the same way as
   `x = x op e`, so that rewriting one into the other never changes what my program prints.
7. As a Rhenium programmer, I want `x++` and `x--`, so that a counter steps by one without ceremony.
8. As a Rhenium programmer, I want `x++` to work on every numeric type, including `I8`, `U8` and the
   floats, so that I do not have to spell a typed literal to step a narrow counter.
9. As a Rhenium programmer, I want an `I8` at `127` to wrap to `-128` when incremented, so that `++`
   follows the same overflow rule as every other arithmetic operation.
10. As a Rhenium programmer, I want `x++` to be rejected anywhere but as a statement of its own, so
    that no line I read depends on the order in which side effects happen.
11. As a Rhenium programmer, I want `x += e` to be rejected inside an expression and as a chain, so
    that an assignment never yields a value, as with `=`.
12. As a Rhenium programmer, I want a compound assignment to a `const` to report that the variable is
    immutable, so that it is caught as `=` would be.
13. As a Rhenium programmer, I want `flag++` and `flag += 1` on a `Boolean` reported naming the
    operator I wrote, so that the message points at `++` or `+=` rather than at a `+` I never typed.
14. As a Rhenium programmer, I want `u += i` with mixed signedness reported as `=` with `+` would be,
    showing the `+=` I wrote, so that the signedness rule is the one I already know.
15. As a Rhenium programmer, I want a compound assignment to an unknown variable to report one
    diagnostic, so that one mistake stays one message.
16. As a Rhenium programmer, I want a broken left value and a broken right side both reported, so that
    one run shows me everything wrong with the statement.
17. As a Rhenium programmer, I want a compound assignment inside a block or loop body to work like any
    other statement there, so that braces do not create a weaker language.
18. As a compiler developer, I want the compound forms to reuse the binary operator's type rule and
    arithmetic lowering, so that a fix to either reaches both.
19. As a compiler developer, I want adding a binary operator to remind me to decide its compound form,
    so that the operator set and the compound set never drift apart.

## Implementation Decisions

**Lexer.** New tokens for `+=`, `-=`, `*=`, `/=`, `%=`, `&&=` and `||=`, named after the existing
operator tokens. `++` and `--` already exist and are unused.

**Grammar.** Two new alternatives of `statement`: a compound assignment
(`leftValue op= expression ;`) and an increment / decrement statement (`leftValue (++ | --) ;`).
Neither is an alternative of `expression`, so using one as a value is an ANTLR syntax error reported
through the existing parse-error listener, as `a = b = c` already is. No diagnostic is written for it.

**AST.** Two new statement node kinds: a **compound assignment statement** carrying its left value,
the underlying binary operator and its right value, and an **increment statement** carrying its left
value and whether it increments or decrements. Increment is its own node rather than a compound
assignment with a synthesized one, because the one has no source text and no parse node to point at.
Neither desugars in the AST visitor: desugaring there would decorate the left value twice, doubling
every diagnostic on it, and would lose the operator the user wrote.

**Prefactor first.** The binary operator's type rule — the poison short-circuit, the Boolean gate, the
numeric gate, mixed signedness, the wider-operand result — is lifted out of the binary operator's
decorator into something callable with two types and an operator, returning either the result type or
the reason there is none. The binary operator's decorator, the compound assignment's decorator and
the increment's decorator each turn that reason into their own diagnostic. Likewise the arithmetic
lowering — cast to the result type, the unsigned detour on `+`, `-` and `*` — is lifted out so it can be
applied to operands that are already actions. This lands alone with no behaviour change.

**Decorating.** A compound assignment decorates its left value as a plain assignment does — must be a
left value, must be mutable — accumulated with decorating its right side. It then runs the shared
binary rule on (left value's type, right side's type, operator), and requires the result to be
assignable to the left value's type, reporting the existing type mismatch otherwise. A poisoned side
yields no further diagnostic. An increment runs the same rule with the left value's type on both sides,
so its only possible failure is an operator that does not apply to the type.

**Diagnostics.** Messages name what the user wrote:

- `illegal compound assignment 'Boolean += I32'.`
- `cannot mix signed and unsigned operands in 'U32 += I32'.`
- `illegal increment 'Boolean++'.` and `illegal decrement 'Boolean--'.`
- `type mismatch, found I32 but expected I8.` — unchanged, for `i8 += 1`.
- The existing immutable-left-value diagnostic for a `const` target.

Each new diagnostic is its own file in the analyzer's diagnostics package.

**Lowering.** Both lower to the existing assignment action whose value is the shared binary lowering
applied to a reference to the left value and the right side — `x = (T)(x op e)` in C, detour included.
An increment's right side is a constant one of the left value's type. C's own `+=` and `++` are never
emitted: on a signed type they reintroduce the overflow undefined behaviour the unsigned detour exists
to remove, and they would bypass result-typed evaluation. No new action kind, so the transpiler is
untouched.

**`&&=` and `||=`** lower to a nested binary action, so the C is `x = x && e` and short-circuits by
[ADR 0002](../../adr/0002-expressions-stay-nested-in-the-action-tree.md).

**Documents.** The language reference states the side-effect-free left-value rule and moves `??=` to
"when nullables land". CLAUDE.md gains the reminder that a new binary operator must decide its
compound form. The roadmap lists the forms as implemented as each ticket lands.

## Testing Decisions

The four seams the previous efforts use; no new seam. A test states a claim about Rhenium source, not
about the compiler's classes.

**Semantic analyzer — source in, rendered diagnostics out.** Where most of the rules live: each
operator accepted on a fitting type; `f32 += i32` accepted; `i8 += 1` rejected with the type mismatch;
`flag += 1`, `n &&= true`, `flag++` rejected with the new wording; mixed signedness showing `+=`; a
`const` target; an unknown target reporting once; a broken left and right side both reported; a
compound assignment inside a loop body.

**Lowering — source in, action tree as an s-expression out.** The highest seam: `x += e` lowering to an
assignment of the cast binary, detour included for `I32`, absent for `U32` and for `/`; `x++` on `I8`,
`U8`, `F32` and `F64` with a one of the matching type; `&&=` as a nested `&&`.

**AST — source in, tree as an s-expression out.** One case per new node kind, with the renderer taught
to print it. The forms that must not parse — `y = x++;`, `print x++;`, `x++ + 1`, `a += b += c` — are
pinned here as parse failures.

**Transpiler.** Nothing new to pin; no new action.

Beyond the four: a hand-run program confirming that a counting loop using `+=` and `++` compiles with
`clang` and prints the right total, and that `I8(127)` incremented prints `-128`.

## Out of Scope

- Prefix `++x` and `--x`. Never planned.
- `??=` — there are no nullable types yet.
- `^=` — `^` has no parser rule yet.
- Member access and indexing as left values. The side-effect-free rule is written down now and
  enforced when they arrive.
- The `for` loop's increment slot. It will take these statements without their semicolon when `for`
  lands.

## Further Notes

No ADR: nothing here is hard to reverse, and the one surprising choice — never emitting C's `+=` — is a
direct consequence of result-typed evaluation and the unsigned detour, already recorded in
[docs/work/integer-arithmetic/spec.md](../integer-arithmetic/spec.md). The glossary gained
**compound assignment** and **increment / decrement statement**.
