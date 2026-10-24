# 13: The function walkers follow the house idioms

**What to build:** the classes added for functions do the same things the same way as their siblings.
Behaviour does not change; every existing test keeps passing.

**Blocked by:** None (can start immediately)

**Status:** resolved

- [x] every `dagger.Lazy` added by the functions effort is unwrapped through `private val x by lazy { provider.get() }`, as CLAUDE.md asks — `ExpressionNodeDecorator`'s call decorator, `EntryDeclarationLowerer`, `FunctionDeclarationLowerer`, `ReturnStatementLowerer`; the fields added to `ExpressionLowerer` and `StatementLowerer` keep those files' existing inline `.get()` style
- [x] `StatementVisitor.visitBlockStatement` gets a name that does not read like an ANTLR override, or merges with `visitBlock`
- [x] `FunctionAlreadyDeclared` and `EntryAlreadyDeclared` take the same kind of "existing" argument, a parse context — nullable only for a function, since a primitive type has none
- [x] `RootVisitor` handles an unknown declaration rule as CLAUDE.md asks, by throwing; `UnhandledParseRule` stays the default of every ANTLR visitor, as in the other visitors
- [x] `ControlTransferStatement` is sealed if Kotlin's same-package rule allows it, so `BlockStatementDecorator` dispatches over it exhaustively

## Comments

Found by the two-axis review of `cccfb8a` (standards axis, finding 1 and smells).
