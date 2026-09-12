# Compute arithmetic at the result type's width

Status: resolved
Blocked by: 02

Rhenium resolves `I32 + I32` to `I32`, then emits `(a+b)` and lets C pick the width. [The
spec](../spec.md) makes every arithmetic node emit cast to its own `cName`, and routes signed
`+ - *` and unary `-` through the unsigned counterpart so overflow wraps instead of being undefined.

This is the ticket the roadmap gap names.

## Tasks

- A signed-to-unsigned counterpart property on `SignedIntType`
- `CBinaryOpTranspiler`: cast the result to the node's `cName`; for signed `+ - *`, cast both
  operands to the unsigned counterpart first. Leave `/` and `%` without the detour — routing
  `MIN / -1` through unsigned changes the answer rather than wrapping it
- `CUnaryOpTranspiler`: the same for unary `-` on a signed type. `!` and unary `+` are unaffected
- Cases per family: signed `+ - * / %`, unsigned, float, unary `-`, and a nested expression showing
  the casts compose

## Done when

`./gradlew :transpiler:test` pins the emitted shape for every family, and the gap entry in
`docs/roadmap.md` is replaced by the division-by-zero entry.

## Not covered

A string assertion cannot show that `I32(2147483647) + I32(1)` evaluates to `-2147483648`. Verify
that once by hand through `clang` and record it; the build does not prove it.

## Comments

### Narrow unsigned arithmetic was left undefined (post-review)

The detour shipped as a signed-only rule — a `detourType` member on `SignedIntType`, reached behind
an `is SignedIntType` test in both lowerers. That left `U8` and `U16` arithmetic with no detour at
all, and C's integer promotions make it undefined: `U16(65535) * U16(65535)` emitted
`(uint16_t)(a * b)`, which promotes both operands to signed `int` and overflows there.

`-fsanitize=undefined` reports `signed integer overflow: 65535 * 65535 cannot be represented in type
'int'` on the old emission, and `clang -Winteger-overflow` warns at compile time when the operands
are literals. Neither fires on the new emission, which prints the `1` the language promises.

The spec had already worked out why the detour must be wider than the result type, and used
`(uint16_t)-1 * (uint16_t)-1` as its example of the undefined behaviour — but drew the conclusion
only for signed types. The gap was in the spec first; it has been amended.

`detourType` is now a nullable rule over `ExpressionType`, null for `U32`, `U64` and the non-integer
types, and both lowerers dropped their `is SignedIntType` test as a result. Unary `-` on an unsigned
operand is rejected during analysis, so only the binary operators reach the new case.

Pinned by six cases in `LoweringTests`: `U16`/`U8` multiplication and addition detour, `U64`
addition and `U16` division do not.
