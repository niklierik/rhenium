# 01: A program runs from an entry

**What to build:** `entry Main { ... }` holds the program's statements, and a statement outside an entry
no longer parses. The entry becomes a C function `re_entry_Main`, and `main` is a wrapper that calls it
and returns 0. `return;` ends an entry early. Every existing test case moves inside an entry. See the
[spec](../spec.md).

**Blocked by:** None (can start immediately)

**Status:** resolved

- [x] `entry Main { println 1; }` compiles and prints `1`
- [x] a loose top-level statement is a syntax error
- [x] `return;` inside an entry ends it; `return 1;` inside an entry is `a Void function cannot return a value.`
- [x] lowering produces a program action: the entry's function action, then `main` calling it
- [x] existing statement-level cases in all four suites pass inside a wrapping entry
