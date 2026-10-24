# 06: Every path of a non-Void function returns

**What to build:** a function with a return type must return on every path. See the
[spec](../spec.md).

**Blocked by:** 05

**Status:** resolved

- [x] a body without a return: `function 'f' does not return a value on every path.`
- [x] an if/else whose branches both return counts; an if without an else does not
- [x] a return in a nested block counts
- [x] a return only inside a loop does not count, including `while (true)`
- [x] no report for a Void function or a poisoned return type
