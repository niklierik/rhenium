# 05: The spec, the tickets and the tests say what the code does

**Spec:** [../spec.md](../spec.md)

**What to build:** Close the gaps the code reviews found between the documents and the code. The spec
describes the four parts of a for statement as sibling branches of one accumulation, with the init clause
first because the branches run in order; binding the init clause on its own would stop at its first
failure and hide the other three parts' diagnostics. The for-statement documents and the roadmap use the
glossary's terms instead of the words it says to avoid: header, step, update, counting loop. User story
22 — diagnostics in all four parts reported in one run — is pinned by a test that has a fault in the init
clause as well as in the other three.

**Blocked by:** None (can start immediately)

**Status:** resolved

- [x] The spec's accumulation paragraph describes what the decorator does, and says why
- [x] No for-statement document or the roadmap's for statement paragraph uses header, step, update or
      counting loop for a glossary concept
- [x] The semantic analyzer seam has a case with a fault in the init clause, the condition, the iteration
      expression and the body at once, all reported
- [x] The build is green
