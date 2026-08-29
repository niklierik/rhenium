# Rhenium documentation

Rhenium is a strongly typed, compiled, object-oriented language with **ownership-based manual memory
management**: every heap object has an owner, and an owner's death destroys what it owns,
recursively. Syntax draws on C#, TypeScript and Kotlin. Output is C23 built through CMake, with
clang as the primary target and gcc configurable.

| Document | Contents |
| --- | --- |
| [Language reference](language-reference.md) | entry points, projects, functions, variables, types, operator precedence, control flow, objects |
| [Memory model](memory-model.md) | ownership, `gives` / `take` / `using`, resource states, leak detection |
| [C interop](c-interop.md) | `c_decl`, mapping attributes, hand-written headers |
| [Standard library](standard-library.md) | planned types and error handling |
| [Roadmap and status](roadmap.md) | schedule, what the compiler implements today, known gaps |

## Source of truth

The language design originates on the **`plans` branch**: `README.md` (language reference,
Hungarian), `plan.md` (thesis proposal and schedule), `architecture.md` (compiler diagram),
`memory/` (ownership model), and samples in `BasicCodes/`, `Lib/` and `Game/`. That branch holds no
compiler code.

These documents are the English working copy for compiler work. **On conflict, the `plans` branch
wins.**

## Drift between the design docs and the samples

The samples on `plans` predate its prose and were not updated with it. Where they disagree, the
newer prose wins:

- `gives` is the ownership-returning keyword — the samples still say `grant`
- `entry` is the entry-point keyword — `memory/memory.re` still says `exec`
- `c_decl` always links with `as` — `Console.re` has one stray `for`
