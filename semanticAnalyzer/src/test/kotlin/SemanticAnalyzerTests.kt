import arrow.core.getOrElse
import me.eriknikli.rhenium.common.diagnostics.render
import org.antlr.v4.runtime.CharStreams
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.fail

class SemanticAnalyzerTests {
    private val component = DaggerSemanticAnalyzerTestComponent.create()
    private val astBuilder = component.makeAstBuilder()
    private val semanticAnalyzer = component.makeSemanticAnalyzer()

    @ParameterizedTest(name = "Run {index}, name {0}")
    @MethodSource("provideData")
    fun `test semantic diagnostics`(name: String, sourceCode: String, expectedDiagnostics: String) {
        val ast = astBuilder.parse(CharStreams.fromString(inEntry(sourceCode)))
            .getOrElse { fail("expected the source to parse, got:\n${it.render()}") }

        val actualDiagnostics = semanticAnalyzer.decorateSemanticContext(ast)
            .fold({ it.render() }, { "" })

        assertEquals(expectedDiagnostics, actualDiagnostics)
    }

    @ParameterizedTest(name = "Run {index}, name {0}")
    @MethodSource("providePrograms")
    fun `test program diagnostics`(name: String, sourceCode: String, expectedDiagnostics: String) {
        val ast = astBuilder.parse(CharStreams.fromString(sourceCode))
            .getOrElse { fail("expected the source to parse, got:\n${it.render()}") }

        val actualDiagnostics = semanticAnalyzer.decorateSemanticContext(ast)
            .fold({ it.render() }, { "" })

        assertEquals(expectedDiagnostics, actualDiagnostics)
    }

    companion object {
        private fun inEntry(statements: String): String = "entry Main {\n$statements\n}"

        @JvmStatic
        fun providePrograms(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("an empty file has no diagnostics", "", ""),
                Arguments.of("entries with different names", "entry A {}\nentry B {}", ""),
                Arguments.of(
                    "two entries may not share a name",
                    "entry Main {}\nentry Main {}",
                    "2:1: error: entry 'Main' is already declared at 1:1."
                ),
                Arguments.of(
                    "every entry is checked",
                    "entry A {\n    println a;\n}\nentry B {\n    println b;\n}",
                    "2:13: error: unknown symbol 'a'." + System.lineSeparator() + "5:13: error: unknown symbol 'b'."
                ),
                Arguments.of(
                    "entries do not share variables",
                    "entry A {\n    let a = 1;\n}\nentry B {\n    println a;\n}",
                    "5:13: error: unknown symbol 'a'."
                ),
                Arguments.of("a function is called before its declaration", "entry Main {\n    f();\n}\nfn f() {}", ""),
                Arguments.of("a function calls itself", "fn f() {\n    f();\n}", ""),
                Arguments.of("two functions call each other", "fn f() {\n    g();\n}\nfn g() {\n    f();\n}", ""),
                Arguments.of("an explicit Void return type", "fn f(): Void {\n    return;\n}", ""),
                Arguments.of(
                    "two functions may not share a name",
                    "fn f() {}\nfn f() {}",
                    "2:1: error: function 'f' is already declared at 1:1."
                ),
                Arguments.of(
                    "a function may not take a type's name",
                    "fn Boolean() {}",
                    "1:1: error: function 'Boolean' is already declared."
                ),
                Arguments.of(
                    "a function is not a value",
                    "fn f() {}\nentry Main {\n    let a = f;\n}",
                    "3:13: error: 'f' is a function, not a value."
                ),
                Arguments.of(
                    "a function is not assignable",
                    "fn f() {}\nentry Main {\n    f = 1;\n}",
                    "3:5: error: 'f' is a function, not a value."
                ),
                Arguments.of(
                    "a variable is not a function",
                    "entry Main {\n    let a = 1;\n    a();\n}",
                    "3:5: error: 'a' is not a function."
                ),
                Arguments.of(
                    "an unknown function",
                    "entry Main {\n    g();\n}",
                    "2:5: error: unknown symbol 'g'."
                ),
                Arguments.of(
                    "a local may not take a function's name",
                    "fn f() {}\nentry Main {\n    let f = 1;\n}",
                    "3:5: error: variable 'f' is already declared at 1:1."
                ),
                Arguments.of(
                    "a function does not see an entry's variables",
                    "entry Main {\n    let a = 1;\n    f();\n}\nfn f() {\n    println a;\n}",
                    "6:13: error: unknown symbol 'a'."
                ),
                Arguments.of(
                    "loop control does not cross into the caller's loop",
                    "fn f() {\n    break;\n}\nentry Main {\n    while (true) {\n        f();\n    }\n}",
                    "2:5: error: 'break' is not inside a loop."
                ),
                Arguments.of(
                    "a statement after a return in a function is unreachable",
                    "fn f() {\n    return;\n    f();\n}",
                    "3:5: warning: unreachable statement after 'return' at 2:5."
                ),
                Arguments.of(
                    "a function's body and a duplicate are both reported",
                    "fn f() {}\nfn f() {\n    println a;\n}",
                    "2:1: error: function 'f' is already declared at 1:1." + System.lineSeparator() + "3:13: error: unknown symbol 'a'."
                ),
                Arguments.of(
                    "parameters are usable and mutable",
                    "fn f(a: I32, b: Boolean) {\n    a = a + 1;\n    b = !b;\n}\nentry Main {\n    f(1, true);\n}",
                    ""
                ),
                Arguments.of(
                    "a const parameter is not assignable",
                    "fn f(const a: I32) {\n    a = 1;\n}",
                    "2:5: error: cannot assign to 'a', it is not mutable. Declare it with 'let'."
                ),
                Arguments.of(
                    "too many arguments",
                    "fn f(a: I32, b: I32) {}\nentry Main {\n    f(1, 2, 3);\n}",
                    "3:5: error: 'f' takes 2 arguments, but 3 were given."
                ),
                Arguments.of(
                    "too few arguments",
                    "fn f(a: I32) {}\nentry Main {\n    f();\n}",
                    "3:5: error: 'f' takes 1 argument, but 0 were given."
                ),
                Arguments.of(
                    "an argument widens to its parameter",
                    "fn f(a: I32) {}\nentry Main {\n    f(I8(1));\n}",
                    ""
                ),
                Arguments.of(
                    "an argument does not narrow to its parameter",
                    "fn f(a: I8, b: Boolean) {}\nentry Main {\n    f(1, 2);\n}",
                    "3:7: error: type mismatch, found I32 but expected I8." + System.lineSeparator() + "3:10: error: type mismatch, found I32 but expected Boolean."
                ),
                Arguments.of(
                    "a broken argument is reported once",
                    "fn f(a: I32) {}\nentry Main {\n    f(x);\n}",
                    "3:7: error: unknown symbol 'x'."
                ),
                Arguments.of(
                    "arguments of an unknown function are still checked",
                    "entry Main {\n    g(x);\n}",
                    "2:5: error: unknown symbol 'g'." + System.lineSeparator() + "2:7: error: unknown symbol 'x'."
                ),
                Arguments.of(
                    "two parameters may not share a name",
                    "fn f(a: I32, a: I32) {}",
                    "1:14: error: variable 'a' is already declared at 1:6."
                ),
                Arguments.of(
                    "a parameter may not take a function's name",
                    "fn f(f: I32) {}",
                    "1:6: error: variable 'f' is already declared at 1:1."
                ),
                Arguments.of(
                    "a local may not shadow a parameter",
                    "fn f(a: I32) {\n    let a = 1;\n}",
                    "2:5: error: variable 'a' shadows variable 'a' declared at 1:6."
                ),
                Arguments.of(
                    "an unknown parameter type still declares the function and the parameter",
                    "fn f(a: Nope) {\n    println a;\n}\nentry Main {\n    f(1);\n}",
                    "1:9: error: unknown type 'Nope'."
                ),
                Arguments.of(
                    "a parameter cannot be Void",
                    "fn f(a: Void) {}",
                    "1:9: error: 'Void' has no value."
                ),
                Arguments.of(
                    "a call is an expression of the return type",
                    "fn twice(a: I32): I32 {\n    return a * 2;\n}\nentry Main {\n    let b: I64 = twice(2) + 1;\n    println twice(b);\n}",
                    "6:19: error: type mismatch, found I64 but expected I32."
                ),
                Arguments.of(
                    "a returned value is checked against the return type",
                    "fn f(): I8 {\n    return true;\n}",
                    "2:12: error: type mismatch, found Boolean but expected I8."
                ),
                Arguments.of(
                    "a returned value widens to the return type",
                    "fn f(): I64 {\n    return 1;\n}",
                    ""
                ),
                Arguments.of(
                    "a broken returned value is reported once",
                    "fn f(): I32 {\n    return x;\n}",
                    "2:12: error: unknown symbol 'x'."
                ),
                Arguments.of(
                    "a bare return in a function with a return type",
                    "fn f(): I32 {\n    return;\n}",
                    "2:5: error: 'return' needs a value of type I32."
                ),
                Arguments.of(
                    "a returned value in a Void function",
                    "fn f() {\n    return 1;\n}",
                    "2:5: error: a Void function cannot return a value."
                ),
                Arguments.of(
                    "an unknown return type is reported once and poisons the calls",
                    "fn f(): Nope {\n    return 1;\n}\nentry Main {\n    let a: Boolean = f();\n}",
                    "1:9: error: unknown type 'Nope'."
                ),
                Arguments.of(
                    "a Void call has no value, and a condition restates its requirement as ADR 0003 has it",
                    "fn f() {}\nentry Main {\n    let a = f();\n    println f();\n    let b = 1 + f();\n    if (f()) {}\n}",
                    "3:13: error: 'f()' has no value." + System.lineSeparator() + "4:13: error: 'f()' has no value." + System.lineSeparator() + "5:17: error: 'f()' has no value." + System.lineSeparator() + "6:9: error: 'f()' has no value." + System.lineSeparator() +
                        "6:9: error: type mismatch, found <invalid> but expected Boolean."
                ),
                Arguments.of(
                    "a Void call is not an argument",
                    "fn f() {}\nfn g(a: I32) {}\nentry Main {\n    g(f());\n}",
                    "4:7: error: 'f()' has no value."
                ),
                Arguments.of(
                    "a Void call is not a returned value",
                    "fn f() {}\nfn g(): I32 {\n    return f();\n}",
                    "3:12: error: 'f()' has no value."
                ),
                Arguments.of(
                    "a Void call returned from a Void function is one mistake",
                    "fn f() {}\nfn g() {\n    return f();\n}",
                    "3:5: error: a Void function cannot return a value."
                ),
                Arguments.of(
                    "a Void call with arguments is quoted as written",
                    "fn f(a: I32, b: I32) {}\nentry Main {\n    let a = f(1, 2);\n}",
                    "3:13: error: 'f(1, 2)' has no value."
                ),
                Arguments.of(
                    "a variable cannot be Void",
                    "entry Main {\n    let a: Void = 1;\n}",
                    "2:12: error: 'Void' has no value."
                ),
                Arguments.of(
                    "a result may be discarded",
                    "fn f(): I32 {\n    return 1;\n}\nentry Main {\n    f();\n}",
                    ""
                ),
                Arguments.of(
                    "a function with a return type must return",
                    "fn f(): I32 {\n    println 1;\n}",
                    "1:1: error: function 'f' does not return a value on every path."
                ),
                Arguments.of(
                    "an if and an else that both return count as returning",
                    "fn f(a: Boolean): I32 {\n    if (a) {\n        return 1;\n    } else {\n        return 2;\n    }\n}",
                    ""
                ),
                Arguments.of(
                    "an else if chain counts when every branch returns",
                    "fn f(a: I32): I32 {\n    if (a == 1) { return 1; } else if (a == 2) { return 2; } else { return 3; }\n}",
                    ""
                ),
                Arguments.of(
                    "an if without an else does not count",
                    "fn f(a: Boolean): I32 {\n    if (a) {\n        return 1;\n    }\n}",
                    "1:1: error: function 'f' does not return a value on every path."
                ),
                Arguments.of(
                    "one branch returning is not enough",
                    "fn f(a: Boolean): I32 {\n    if (a) { return 1; } else { println 2; }\n}",
                    "1:1: error: function 'f' does not return a value on every path."
                ),
                Arguments.of(
                    "a return in a nested block counts",
                    "fn f(): I32 {\n    {\n        return 1;\n    }\n}",
                    ""
                ),
                Arguments.of(
                    "a return inside a loop does not count, even an endless one",
                    "fn f(): I32 {\n    while (true) {\n        return 1;\n    }\n}",
                    "1:1: error: function 'f' does not return a value on every path."
                ),
                Arguments.of(
                    "a return inside a for statement does not count",
                    "fn f(): I32 {\n    for (let i = 0; i < 1; i++) {\n        return 1;\n    }\n}",
                    "1:1: error: function 'f' does not return a value on every path."
                ),
                Arguments.of("a Void function may fall off its end", "fn f() {\n    println 1;\n}", ""),
                Arguments.of(
                    "a poisoned return type is not also reported as missing a return",
                    "fn f(): Nope {}",
                    "1:9: error: unknown type 'Nope'."
                ),
                Arguments.of("a bare return leaves an entry", "entry Main {\n    return;\n}", ""),
                Arguments.of(
                    "an entry returns no value",
                    "entry Main {\n    return 1;\n}",
                    "2:5: error: a Void function cannot return a value."
                ),
                Arguments.of(
                    "a statement after a return is unreachable",
                    "entry Main {\n    return;\n    println 1;\n}",
                    "3:5: warning: unreachable statement after 'return' at 2:5."
                )
            )
        }

        @JvmStatic
        fun provideData(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("a valid program has no diagnostics", "let a = 1;\na = 2;", ""),
                Arguments.of("an expression statement is not a diagnostic", "let a = 1;\na + 1;", ""),
                Arguments.of(
                    "a block decorates the statements it holds",
                    "let a = 1;\n{\n    println a;\n}",
                    ""
                ),
                Arguments.of("an empty block is not a diagnostic", "{}", ""),
                Arguments.of(
                    "a declaration does not outlive the block that holds it",
                    "{ let b = 1; }\nprintln b;",
                    "3:9: error: unknown symbol 'b'."
                ),
                Arguments.of(
                    "sibling blocks may each declare the same name",
                    "{ let a = 1; }\n{ let a = 2; }",
                    ""
                ),
                Arguments.of(
                    "a declaration after a block may reuse the block's name",
                    "{ let a = 1; }\nlet a = 2;",
                    ""
                ),
                Arguments.of(
                    "a block does not see a declaration that comes after it",
                    "{ println a; }\nlet a = 1;",
                    "2:11: error: unknown symbol 'a'."
                ),
                Arguments.of(
                    "a declaration may not shadow one from an enclosing block",
                    "let a = 1;\n{ let a = 2; }",
                    "3:3: error: variable 'a' shadows variable 'a' declared at 2:1."
                ),
                Arguments.of(
                    "shadowing is reported however deeply the blocks nest",
                    "let a = 1;\n{\n    {\n        let a = 2;\n    }\n}",
                    "5:9: error: variable 'a' shadows variable 'a' declared at 2:1."
                ),
                Arguments.of(
                    "a shadow of a poisoned variable is reported alongside what poisoned it",
                    "let a = b;\n{ let a = 1; }",
                    "2:9: error: unknown symbol 'b'." + System.lineSeparator() +
                            "3:3: error: variable 'a' shadows variable 'a' declared at 2:1."
                ),
                Arguments.of(
                    "a shadow is reported beside an unrelated error in the same declaration",
                    "let a = 1;\n{ let a = b; }",
                    "3:3: error: variable 'a' shadows variable 'a' declared at 2:1." + System.lineSeparator() +
                            "3:11: error: unknown symbol 'b'."
                ),
                Arguments.of(
                    "a type name is taken, at the top level",
                    "let Boolean = 1;",
                    "2:1: error: variable 'Boolean' is already declared."
                ),
                Arguments.of(
                    "a type name is taken inside a block too, so a declared type never resolves to a variable",
                    "{ let Boolean = 1; }",
                    "2:3: error: variable 'Boolean' is already declared."
                ),
                Arguments.of(
                    "a declaration that collides with a type name leaves the type name alone",
                    "{ let Boolean = 1; let b: Boolean = true; }",
                    "2:3: error: variable 'Boolean' is already declared."
                ),
                Arguments.of(
                    "a broken statement inside a block reports once",
                    "{ b + 1; }",
                    "2:3: error: unknown symbol 'b'."
                ),
                Arguments.of(
                    "a loop decorates its condition and its body",
                    "let a = 1;\nwhile (a < 3) {\n    println a;\n}",
                    ""
                ),
                Arguments.of("a loop with an empty body is not a diagnostic", "while (true) {}", ""),
                Arguments.of(
                    "a declaration in a loop body does not outlive the loop",
                    "while (true) { let b = 1; }\nprintln b;",
                    "3:9: error: unknown symbol 'b'."
                ),
                Arguments.of(
                    "a loop body may not shadow a declaration from around the loop",
                    "let a = 1;\nwhile (true) { let a = 2; }",
                    "3:16: error: variable 'a' shadows variable 'a' declared at 2:1."
                ),
                Arguments.of(
                    "a broken condition and a broken body statement are both reported",
                    "while (b) { c + 1; }",
                    "2:8: error: unknown symbol 'b'." + System.lineSeparator() +
                            "2:8: error: type mismatch, found <invalid> but expected Boolean." + System.lineSeparator() +
                            "2:13: error: unknown symbol 'c'."
                ),
                Arguments.of(
                    "a broken statement inside a loop body reports once and hides nothing after it",
                    "while (true) { b + 1; c + 1; }",
                    "2:16: error: unknown symbol 'b'." + System.lineSeparator() +
                            "2:23: error: unknown symbol 'c'."
                ),
                Arguments.of(
                    "nested loops with a valid body produce no diagnostics",
                    "let a = 1;\nwhile (a < 3) {\n    while (a < 2) {\n        println a;\n    }\n}",
                    ""
                ),
                Arguments.of(
                    "every loop in a program is reported",
                    "while (b) {}\nwhile (c) {}",
                    "2:8: error: unknown symbol 'b'." + System.lineSeparator() +
                            "2:8: error: type mismatch, found <invalid> but expected Boolean." + System.lineSeparator() +
                            "3:8: error: unknown symbol 'c'." + System.lineSeparator() +
                            "3:8: error: type mismatch, found <invalid> but expected Boolean."
                ),
                Arguments.of(
                    "a relational condition is a boolean, so the loop accepts it",
                    "let a = 1;\nwhile (a < 3) {}",
                    ""
                ),
                Arguments.of(
                    "an integer condition is rejected, because a loop wants a boolean and not c's zero test",
                    "while (1) {}",
                    "2:8: error: type mismatch, found I32 but expected Boolean."
                ),
                Arguments.of(
                    "a float condition is rejected too",
                    "while (1.5) {}",
                    "2:8: error: type mismatch, found F64 but expected Boolean."
                ),
                Arguments.of(
                    "a condition that is broken reports its own fault and the loop's requirement (ADR 0003)",
                    "while (1 + true) {}",
                    "2:8: error: illegal binary operation 'I32 + Boolean'." + System.lineSeparator() +
                            "2:8: error: type mismatch, found <invalid> but expected Boolean."
                ),
                Arguments.of(
                    "a non-boolean condition and a broken body statement are both reported",
                    "while (1) { b + 1; }",
                    "2:8: error: type mismatch, found I32 but expected Boolean." + System.lineSeparator() +
                            "2:13: error: unknown symbol 'b'."
                ),
                Arguments.of(
                    "every loop with a non-boolean condition is reported",
                    "while (1) {}\nwhile (2) {}",
                    "2:8: error: type mismatch, found I32 but expected Boolean." + System.lineSeparator() +
                            "3:8: error: type mismatch, found I32 but expected Boolean."
                ),
                Arguments.of(
                    "a for statement's init variable is visible in its condition, iteration expression and body",
                    "for (let i = 0; i < 3; i++) {\n    println i;\n}",
                    ""
                ),
                Arguments.of(
                    "a for statement's init variable does not outlive the loop",
                    "for (let i = 0; i < 3; i++) {}\nprintln i;",
                    "3:9: error: unknown symbol 'i'."
                ),
                Arguments.of(
                    "sibling for statements may each declare the same init variable",
                    "for (let i = 0; i < 3; i++) {}\nfor (let i = 0; i < 3; i++) {}",
                    ""
                ),
                Arguments.of(
                    "a for body may not shadow the init variable",
                    "for (let i = 0; i < 3; i++) {\n    let i = 1;\n}",
                    "3:5: error: variable 'i' shadows variable 'i' declared at 2:6."
                ),
                Arguments.of(
                    "a nested for statement may not shadow the outer init variable",
                    "for (let i = 0; i < 3; i++) {\n    for (let i = 0; i < 3; i++) {}\n}",
                    "3:10: error: variable 'i' shadows variable 'i' declared at 2:6." + System.lineSeparator() +
                            "3:21: error: type mismatch, found <invalid> but expected Boolean."
                ),
                Arguments.of(
                    "an init variable may not shadow a declaration from around the loop",
                    "let i = 0;\nfor (let i = 0; i < 3; i++) {}",
                    "3:6: error: variable 'i' shadows variable 'i' declared at 2:1." + System.lineSeparator() +
                            "3:17: error: type mismatch, found <invalid> but expected Boolean."
                ),
                Arguments.of(
                    "break and continue in a for body are not diagnostics",
                    "for (let i = 0; i < 3; i++) {\n    if (i == 1) {\n        continue;\n    }\n    break;\n}",
                    ""
                ),
                Arguments.of(
                    "a statement after continue in a for body is unreachable",
                    "for (let i = 0; i < 3; i++) {\n    continue;\n    println i;\n}",
                    "4:5: warning: unreachable statement after 'continue' at 3:5."
                ),
                Arguments.of(
                    "a for statement does not make break legal after it ends",
                    "for (let i = 0; i < 3; i++) {}\nbreak;",
                    "3:1: error: 'break' is not inside a loop."
                ),
                Arguments.of(
                    "an integer for condition is rejected in the same words as a while condition",
                    "for (let i = 0; 1; i++) {}",
                    "2:17: error: type mismatch, found I32 but expected Boolean."
                ),
                Arguments.of(
                    "a broken for condition reports its own fault and the loop's requirement (ADR 0003)",
                    "for (let i = 0; i + true; i++) {}",
                    "2:17: error: illegal binary operation 'I32 + Boolean'." + System.lineSeparator() +
                            "2:17: error: type mismatch, found <invalid> but expected Boolean."
                ),
                Arguments.of(
                    "a const init variable cannot be stepped",
                    "for (const i = 0; i < 3; i++) {}",
                    "2:26: error: cannot assign to 'i', it is not mutable. Declare it with 'let'."
                ),
                Arguments.of(
                    "a failed init declaration still declares its variable, so only the condition's requirement is restated (ADR 0003)",
                    "for (let i: Foo = 0; i < 3; i++) {\n    println i;\n}",
                    "2:13: error: unknown type 'Foo'." + System.lineSeparator() +
                            "2:22: error: type mismatch, found <invalid> but expected Boolean."
                ),
                Arguments.of(
                    "faults in a for init clause, condition, iteration expression and body are all reported",
                    "for (let i: Foo = 0; 1; j++) {\n    k + 1;\n}",
                    """
                    2:13: error: unknown type 'Foo'.
                    2:22: error: type mismatch, found I32 but expected Boolean.
                    2:25: error: unknown symbol 'j'.
                    3:5: error: unknown symbol 'k'.
                    """.trimIndent()
                ),
                Arguments.of(
                    "an init clause may assign a variable declared before the loop, which outlives it",
                    "let i = 0;\nfor (i = 5; i < 3; i++) {}\nprintln i;",
                    ""
                ),
                Arguments.of(
                    "an init clause may be a compound assignment and an iteration expression an expression",
                    "let i = 0;\nfor (i += 1; i < 3; i) {}",
                    ""
                ),
                Arguments.of(
                    "expressions in the init clause and iteration expression are decorated",
                    "for (j; true; k) {}",
                    "2:6: error: unknown symbol 'j'." + System.lineSeparator() +
                            "2:15: error: unknown symbol 'k'."
                ),
                Arguments.of(
                    "an init clause may not assign a constant",
                    "const i = 0;\nfor (i = 1; i < 3; i) {}",
                    "3:6: error: cannot assign to 'i', it is not mutable. Declare it with 'let'."
                ),
                Arguments.of(
                    "an if decorates its condition and both branches",
                    "let a = 1;\nif (a < 3) {\n    println a;\n} else {\n    println 0;\n}",
                    ""
                ),
                Arguments.of("an if with empty branches is not a diagnostic", "if (true) {} else {}", ""),
                Arguments.of(
                    "break and continue in a loop body are not diagnostics",
                    "while (true) {\n    continue;\n}\nwhile (true) {\n    break;\n}",
                    ""
                ),
                Arguments.of("break outside a loop is rejected", "break;", "2:1: error: 'break' is not inside a loop."),
                Arguments.of("continue outside a loop is rejected", "continue;", "2:1: error: 'continue' is not inside a loop."),
                Arguments.of(
                    "a block with no loop around it does not make break legal",
                    "{\n    break;\n}",
                    "3:5: error: 'break' is not inside a loop."
                ),
                Arguments.of(
                    "an if with no loop around it does not make continue legal",
                    "if (true) {\n    continue;\n}",
                    "3:5: error: 'continue' is not inside a loop."
                ),
                Arguments.of(
                    "break inside an if inside a loop acts on the loop",
                    "while (true) {\n    if (true) {\n        break;\n    }\n}",
                    ""
                ),
                Arguments.of(
                    "continue inside a nested block inside a loop acts on the loop",
                    "while (true) {\n    {\n        continue;\n    }\n}",
                    ""
                ),
                Arguments.of(
                    "break inside a nested loop is legal",
                    "while (true) {\n    while (false) {\n        break;\n    }\n}",
                    ""
                ),
                Arguments.of(
                    "a loop does not make break legal after it ends",
                    "while (true) {}\nbreak;",
                    "3:1: error: 'break' is not inside a loop."
                ),
                Arguments.of(
                    "a loop control statement as the last statement of a body is not a diagnostic",
                    "let i = 0;\nwhile (true) {\n    i++;\n    break;\n}",
                    ""
                ),
                Arguments.of(
                    "a statement after break is unreachable",
                    "let x = 0;\nwhile (true) {\n    break;\n    x = 1;\n}",
                    "5:5: warning: unreachable statement after 'break' at 4:5."
                ),
                Arguments.of(
                    "only the first unreachable statement in a block is reported",
                    "let x = 0;\nwhile (true) {\n    continue;\n    x = 1;\n    x = 2;\n}",
                    "5:5: warning: unreachable statement after 'continue' at 4:5."
                ),
                Arguments.of(
                    "an error inside an unreachable statement is still reported",
                    "while (true) {\n    break;\n    println b;\n}",
                    """
                    4:5: warning: unreachable statement after 'break' at 3:5.
                    4:13: error: unknown symbol 'b'.
                    """.trimIndent()
                ),
                Arguments.of(
                    "a break outside a loop with code after it reports only that it is outside a loop",
                    "let x = 0;\nbreak;\nx = 1;\n{\n    continue;\n    x = 2;\n}",
                    """
                    3:1: error: 'break' is not inside a loop.
                    6:5: error: 'continue' is not inside a loop.
                    """.trimIndent()
                ),
                Arguments.of(
                    "errors and warnings are reported together in position order",
                    "println a;\nwhile (true) {\n    break;\n    println 1;\n}\nprintln b;",
                    """
                    2:9: error: unknown symbol 'a'.
                    5:5: warning: unreachable statement after 'break' at 4:5.
                    7:9: error: unknown symbol 'b'.
                    """.trimIndent()
                ),
                Arguments.of(
                    "a statement after an if whose branches all break is not unreachable",
                    "let x = 0;\nwhile (true) {\n    if (true) {\n        break;\n    } else {\n        break;\n    }\n    x = 1;\n}",
                    ""
                ),
                Arguments.of(
                    "an unreachable statement inside a nested block is reported there",
                    "let x = 0;\nwhile (true) {\n    if (true) {\n        break;\n        x = 1;\n    }\n}",
                    "6:9: warning: unreachable statement after 'break' at 5:9."
                ),
                Arguments.of("a boolean condition is accepted by an if", "let b = true;\nif (b) {}", ""),
                Arguments.of(
                    "an integer condition is rejected by an if in the same words as by a loop",
                    "if (1) {}",
                    "2:5: error: type mismatch, found I32 but expected Boolean."
                ),
                Arguments.of(
                    "a float condition is rejected by an if too",
                    "if (1.5) {}",
                    "2:5: error: type mismatch, found F64 but expected Boolean."
                ),
                Arguments.of(
                    "a broken if condition reports its own fault and the requirement (ADR 0003)",
                    "if (1 + true) {}",
                    "2:5: error: illegal binary operation 'I32 + Boolean'." + System.lineSeparator() +
                            "2:5: error: type mismatch, found <invalid> but expected Boolean."
                ),
                Arguments.of(
                    "a bad condition and broken statements in both branches are all reported",
                    "if (1) { b + 1; } else { c + 1; }",
                    "2:5: error: type mismatch, found I32 but expected Boolean." + System.lineSeparator() +
                            "2:10: error: unknown symbol 'b'." + System.lineSeparator() +
                            "2:26: error: unknown symbol 'c'."
                ),
                Arguments.of(
                    "a declaration in a branch does not outlive the if",
                    "if (true) { let b = 1; } else { let c = 2; }\nprintln b;\nprintln c;",
                    "3:9: error: unknown symbol 'b'." + System.lineSeparator() +
                            "4:9: error: unknown symbol 'c'."
                ),
                Arguments.of(
                    "the two branches may each declare the same name",
                    "if (true) { let a = 1; } else { let a = 2; }",
                    ""
                ),
                Arguments.of(
                    "a branch may not shadow a declaration from around the if",
                    "let a = 1;\nif (true) {} else { let a = 2; }",
                    "3:21: error: variable 'a' shadows variable 'a' declared at 2:1."
                ),
                Arguments.of(
                    "an else if chain decorates every link",
                    "let a = 7;\nif (a < 5) { println 0; } else if (a < 10) { println 1; } else { println 2; }",
                    ""
                ),
                Arguments.of(
                    "a bad condition in one link of a chain does not hide one in a later link",
                    "if (1) {} else if (2) {} else { b + 1; }",
                    "2:5: error: type mismatch, found I32 but expected Boolean." + System.lineSeparator() +
                            "2:20: error: type mismatch, found I32 but expected Boolean." + System.lineSeparator() +
                            "2:33: error: unknown symbol 'b'."
                ),
                Arguments.of(
                    "ifs and loops nest without diagnostics",
                    "let a = 1;\nif (a < 3) {\n    while (a < 2) {\n        if (a == 1) {\n            a = a + 1;\n        }\n    }\n}",
                    ""
                ),
                Arguments.of(
                    "a broken expression statement reports once",
                    "b + 1;",
                    "2:1: error: unknown symbol 'b'."
                ),
                Arguments.of("unknown symbol", "b = 3;", "2:1: error: unknown symbol 'b'."),
                Arguments.of(
                    "assignment to an immutable variable",
                    "const a = 1;\na = 2;",
                    "3:1: error: cannot assign to 'a', it is not mutable. Declare it with 'let'."
                ),
                Arguments.of(
                    "declared type does not accept the value",
                    "let c: F32 = 1;",
                    "2:1: error: type mismatch, found I32 but expected F32."
                ),
                Arguments.of(
                    "unknown declared type",
                    "let a: Foo = 1;",
                    "2:8: error: unknown type 'Foo'."
                ),
                Arguments.of(
                    "redeclaration points at the first declaration",
                    "let a = 1;\nlet a = 9;",
                    "3:1: error: variable 'a' is already declared at 2:1."
                ),
                Arguments.of(
                    "boolean operand of an arithmetic operator",
                    "let a = true + 1;",
                    "2:9: error: illegal binary operation 'Boolean + I32'."
                ),
                Arguments.of(
                    "negating a boolean",
                    "let a = -true;",
                    "2:10: error: operator '-' cannot be applied to Boolean, expected " +
                            "I64 or I32 or I16 or I8 or F64 or F32."
                ),
                Arguments.of(
                    "one unknown symbol yields one diagnostic, not a cascade",
                    "let a = unknown + 1;",
                    "2:9: error: unknown symbol 'unknown'."
                ),
                Arguments.of(
                    "a poisoned declaration does not make later uses fail again",
                    "let a: Foo = 1;\na = 2;",
                    "2:8: error: unknown type 'Foo'."
                ),
                Arguments.of(
                    "both operands are reported",
                    "let a = left + right;",
                    """
                    2:9: error: unknown symbol 'left'.
                    2:16: error: unknown symbol 'right'.
                    """.trimIndent()
                ),
                Arguments.of(
                    "signed and unsigned operands cannot be mixed",
                    "let a = I32(1) + U32(1);",
                    "2:9: error: cannot mix signed and unsigned operands in 'I32 + U32'."
                ),
                Arguments.of(
                    "the operand order does not change the diagnostic",
                    "let a = U32(1) + I32(1);",
                    "2:9: error: cannot mix signed and unsigned operands in 'U32 + I32'."
                ),
                Arguments.of(
                    "a relational operator cannot mix signedness either",
                    "let a = I32(-1) < U32(1);",
                    "2:9: error: cannot mix signed and unsigned operands in 'I32 < U32'."
                ),
                Arguments.of(
                    "nor can an equality operator",
                    "let a = I32(-1) == U32(1);",
                    "2:9: error: cannot mix signed and unsigned operands in 'I32 == U32'."
                ),
                Arguments.of(
                    "comparing two operands of one signedness still resolves",
                    "I32(1) < I32(2);",
                    ""
                ),
                Arguments.of(
                    "a boolean can be named as a declared type, not only inferred",
                    "let a: Boolean = I32(1) < I32(2);",
                    ""
                ),
                Arguments.of(
                    "a boolean declaration still rejects a number",
                    "let a: Boolean = 1;",
                    "2:1: error: type mismatch, found I32 but expected Boolean."
                ),
                Arguments.of(
                    "a conjunction of booleans is a boolean",
                    "let a: Boolean = true && false;",
                    ""
                ),
                Arguments.of(
                    "a logical operator rejects a number",
                    "let a = true || 1;",
                    "2:9: error: illegal binary operation 'Boolean || I32'."
                ),
                Arguments.of(
                    "a disjunction of booleans is a boolean",
                    "let a: Boolean = true || false;",
                    ""
                ),
                Arguments.of(
                    "booleans can be compared for equality",
                    "let a: Boolean = true == false;",
                    ""
                ),
                Arguments.of(
                    "booleans can be compared for inequality",
                    "let a: Boolean = true != false;",
                    ""
                ),
                Arguments.of(
                    "a logical operator rejects two numbers",
                    "let a = 1 && 2;",
                    "2:9: error: illegal binary operation 'I32 && I32'."
                ),
                Arguments.of(
                    "a boolean cannot be compared to a number",
                    "let a = true == 1;",
                    "2:9: error: illegal binary operation 'Boolean == I32'."
                ),
                Arguments.of(
                    "booleans have no ordering",
                    "let a = true < false;",
                    "2:9: error: illegal binary operation 'Boolean < Boolean'."
                ),
                Arguments.of(
                    "a broken operand of a logical operator is reported once",
                    "let a = unknown && true;",
                    "2:9: error: unknown symbol 'unknown'."
                ),
                Arguments.of(
                    "a logical operator can be a loop condition",
                    "let running = true;\nlet done = false;\nwhile (running && !done) {}",
                    ""
                ),
                Arguments.of(
                    "mixing an integer with a float still resolves",
                    "let a: F64 = I32(1) + F64(1.0);",
                    ""
                ),
                Arguments.of(
                    "a float has no remainder operator",
                    "let a = F64(1.0) % F64(2.0);",
                    "2:9: error: illegal binary operation 'F64 % F64'."
                ),
                Arguments.of(
                    "the narrower float has no remainder operator either",
                    "let a = F32(1.0) % F32(2.0);",
                    "2:9: error: illegal binary operation 'F32 % F32'."
                ),
                Arguments.of(
                    "one float operand is enough to rule out a remainder",
                    "let a = I32(1) % F64(2.0);",
                    "2:9: error: illegal binary operation 'I32 % F64'."
                ),
                Arguments.of(
                    "integer remainder still resolves",
                    "let a: I32 = I32(7) % I32(2);",
                    ""
                ),
                Arguments.of(
                    "every arithmetic compound assignment is accepted on an integer",
                    "let a = 1;\na += 2;\na -= 2;\na *= 2;\na /= 2;\na %= 2;",
                    ""
                ),
                Arguments.of(
                    "a float accumulates an integer, as the plain assignment would",
                    "let f = F32(1.0);\nf += 1;",
                    ""
                ),
                Arguments.of(
                    "a compound assignment narrows no more than the plain assignment",
                    "let a = I8(1);\na += 1;",
                    "3:1: error: type mismatch, found I32 but expected I8."
                ),
                Arguments.of(
                    "a boolean is not accumulated into, and the message names the compound operator",
                    "let b = true;\nb += 1;",
                    "3:1: error: illegal compound assignment 'Boolean += I32'."
                ),
                Arguments.of(
                    "a float has no remainder to accumulate",
                    "let f = 1.5;\nf %= 2.0;",
                    "3:1: error: illegal compound assignment 'F64 %= F64'."
                ),
                Arguments.of(
                    "a compound assignment mixing signedness names the compound operator",
                    "let u = U32(1);\nu += I32(1);",
                    "3:1: error: cannot mix signed and unsigned operands in 'U32 += I32'."
                ),
                Arguments.of(
                    "compound assignment to an immutable variable",
                    "const a = 1;\na += 2;",
                    "3:1: error: cannot assign to 'a', it is not mutable. Declare it with 'let'."
                ),
                Arguments.of("compound assignment to an unknown symbol reports once", "b += 1;", "2:1: error: unknown symbol 'b'."),
                Arguments.of(
                    "both sides of a compound assignment are reported",
                    "b += c;",
                    "2:1: error: unknown symbol 'b'." + System.lineSeparator() +
                            "2:6: error: unknown symbol 'c'."
                ),
                Arguments.of(
                    "a poisoned right side of a compound assignment reports once",
                    "let a = 1;\na += true + 1;",
                    "3:6: error: illegal binary operation 'Boolean + I32'."
                ),
                Arguments.of(
                    "a boolean folds checks in with the logical compound assignments",
                    "let ok = true;\nok &&= 1 < 2;\nok ||= false;",
                    ""
                ),
                Arguments.of(
                    "a number has no logical compound assignment",
                    "let n = 1;\nn &&= true;",
                    "3:1: error: illegal compound assignment 'I32 &&= Boolean'."
                ),
                Arguments.of(
                    "every numeric type steps by one, a narrow one included",
                    "let a = I8(1);\na++;\nlet b = U64(1);\nb--;\nlet c = F32(1.0);\nc++;\nlet d = 1.5;\nd--;",
                    ""
                ),
                Arguments.of(
                    "a boolean has no increment",
                    "let b = true;\nb++;",
                    "3:1: error: illegal increment 'Boolean++'."
                ),
                Arguments.of(
                    "a boolean has no decrement",
                    "let b = true;\nb--;",
                    "3:1: error: illegal decrement 'Boolean--'."
                ),
                Arguments.of(
                    "incrementing an immutable variable",
                    "const a = 1;\na++;",
                    "3:1: error: cannot assign to 'a', it is not mutable. Declare it with 'let'."
                ),
                Arguments.of("incrementing an unknown symbol reports once", "b++;", "2:1: error: unknown symbol 'b'."),
                Arguments.of(
                    "incrementing a poisoned variable reports nothing more",
                    "let a: Foo = 1;\na++;",
                    "2:8: error: unknown type 'Foo'."
                ),
                Arguments.of(
                    "compound assignments accumulate inside a loop body",
                    "let s = 0;\nlet i = 0;\nwhile (i < 4) {\n    s += i;\n    i += 1;\n}",
                    ""
                ),
                Arguments.of(
                    "every statement is analysed, and diagnostics come out in source order",
                    "const a = 1;\na = 2;\nb = 3;\nlet c: F32 = 1;\nlet a = 9;",
                    """
                    3:1: error: cannot assign to 'a', it is not mutable. Declare it with 'let'.
                    4:1: error: unknown symbol 'b'.
                    5:1: error: type mismatch, found I32 but expected F32.
                    6:1: error: variable 'a' is already declared at 2:1.
                    """.trimIndent()
                )
            )
        }
    }
}
