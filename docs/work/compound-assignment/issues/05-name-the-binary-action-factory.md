# 05: Name the binary action factory

**Spec:** [../spec.md](../spec.md)

**What to build:** A rename with no behaviour change. What ticket 01 introduced to build the cast,
detoured binary action from already-lowered operands is called a builder, which the glossary reserves
against as a synonym for **lowerer**. It is not a lowerer either — it walks no node — so it takes the
name the repo already uses for a thing that makes something without walking a tree: a factory, as the
parse tree factory is.

**Blocked by:** None (can start immediately)

**Status:** resolved

- [x] The interface, class and `@Binds` entry are renamed to the binary action factory
- [x] Every lowerer that uses it — binary operator, compound assignment, increment — calls it by the new name
- [x] Every existing test passes unchanged
