# Warnings

**Status:** needs-triage

A placeholder, not yet a spec. It records a wish two efforts have run into so the wish is not lost;
it has not been grilled.

## The wish

The compiler can only report errors. A diagnostic *is* the left of `Diagnosed<T>`, meaning "no
value", so producing one halts lowering and makes the compiler exit non-zero. Some things deserve a
report without failing the build.

## What is known to be needed

- A severity on `Diagnostic`.
- `Diagnosed` reshaped so warnings ride alongside a successful value rather than replacing it.
- `Main` printing warnings and still compiling and running the program.
- Every walker's accumulation touched by the reshape.

## Its first uses

1. **Downgrade `UnreachableStatement` to a warning.** The [break-continue spec](../break-continue/spec.md)
   made it an error only because warnings do not exist; it is meant to be a warning.
2. **Warn on a `while (true)` with no `break`** — a loop that cannot terminate. Wanted since the
   [while-loop spec](../while-loop/spec.md).
