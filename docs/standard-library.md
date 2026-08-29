# Standard library

Planned contents:

- `String` — ASCII only
- `Array`
- `List`, `Set`, `Dictionary`
- `Optional`
- `Console`
- `FileReader`, `FileWriter`

Error handling is explicit, on the [Result pattern](https://en.wikipedia.org/wiki/Result_type),
rather than exceptions.

The library itself is written in Rhenium over [C bindings](c-interop.md); `Lib/` on the `plans`
branch sketches `Console`, `String`, `Culture`, `Array`, `Environment` and the primitive type
declarations.
