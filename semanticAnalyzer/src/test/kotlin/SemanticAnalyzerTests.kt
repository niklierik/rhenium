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
        val ast = astBuilder.parse(CharStreams.fromString(sourceCode))
            .getOrElse { fail("expected the source to parse, got:\n${it.render()}") }

        val actualDiagnostics = semanticAnalyzer.decorateSemanticContext(ast)
            .fold({ it.render() }, { "" })

        assertEquals(expectedDiagnostics, actualDiagnostics)
    }

    companion object {
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
                    "2:9: unknown symbol 'b'."
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
                    "1:11: unknown symbol 'a'."
                ),
                Arguments.of(
                    "a declaration may not shadow one from an enclosing block",
                    "let a = 1;\n{ let a = 2; }",
                    "2:3: variable 'a' shadows variable 'a' declared at 1:1."
                ),
                Arguments.of(
                    "shadowing is reported however deeply the blocks nest",
                    "let a = 1;\n{\n    {\n        let a = 2;\n    }\n}",
                    "4:9: variable 'a' shadows variable 'a' declared at 1:1."
                ),
                Arguments.of(
                    "a shadow of a poisoned variable is reported alongside what poisoned it",
                    "let a = b;\n{ let a = 1; }",
                    "1:9: unknown symbol 'b'." + System.lineSeparator() +
                            "2:3: variable 'a' shadows variable 'a' declared at 1:1."
                ),
                Arguments.of(
                    "a shadow is reported beside an unrelated error in the same declaration",
                    "let a = 1;\n{ let a = b; }",
                    "2:3: variable 'a' shadows variable 'a' declared at 1:1." + System.lineSeparator() +
                            "2:11: unknown symbol 'b'."
                ),
                Arguments.of(
                    "a type name is taken, at the top level",
                    "let Boolean = 1;",
                    "1:1: variable 'Boolean' is already declared."
                ),
                Arguments.of(
                    "a type name is taken inside a block too, so a declared type never resolves to a variable",
                    "{ let Boolean = 1; }",
                    "1:3: variable 'Boolean' is already declared."
                ),
                Arguments.of(
                    "a declaration that collides with a type name leaves the type name alone",
                    "{ let Boolean = 1; let b: Boolean = true; }",
                    "1:3: variable 'Boolean' is already declared."
                ),
                Arguments.of(
                    "a broken statement inside a block reports once",
                    "{ b + 1; }",
                    "1:3: unknown symbol 'b'."
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
                    "2:9: unknown symbol 'b'."
                ),
                Arguments.of(
                    "a loop body may not shadow a declaration from around the loop",
                    "let a = 1;\nwhile (true) { let a = 2; }",
                    "2:16: variable 'a' shadows variable 'a' declared at 1:1."
                ),
                Arguments.of(
                    "a broken condition and a broken body statement are both reported",
                    "while (b) { c + 1; }",
                    "1:8: unknown symbol 'b'." + System.lineSeparator() +
                            "1:8: type mismatch, found <invalid> but expected Boolean." + System.lineSeparator() +
                            "1:13: unknown symbol 'c'."
                ),
                Arguments.of(
                    "a broken statement inside a loop body reports once and hides nothing after it",
                    "while (true) { b + 1; c + 1; }",
                    "1:16: unknown symbol 'b'." + System.lineSeparator() +
                            "1:23: unknown symbol 'c'."
                ),
                Arguments.of(
                    "nested loops with a valid body produce no diagnostics",
                    "let a = 1;\nwhile (a < 3) {\n    while (a < 2) {\n        println a;\n    }\n}",
                    ""
                ),
                Arguments.of(
                    "every loop in a program is reported",
                    "while (b) {}\nwhile (c) {}",
                    "1:8: unknown symbol 'b'." + System.lineSeparator() +
                            "1:8: type mismatch, found <invalid> but expected Boolean." + System.lineSeparator() +
                            "2:8: unknown symbol 'c'." + System.lineSeparator() +
                            "2:8: type mismatch, found <invalid> but expected Boolean."
                ),
                Arguments.of(
                    "a relational condition is a boolean, so the loop accepts it",
                    "let a = 1;\nwhile (a < 3) {}",
                    ""
                ),
                Arguments.of(
                    "an integer condition is rejected, because a loop wants a boolean and not c's zero test",
                    "while (1) {}",
                    "1:8: type mismatch, found I32 but expected Boolean."
                ),
                Arguments.of(
                    "a float condition is rejected too",
                    "while (1.5) {}",
                    "1:8: type mismatch, found F64 but expected Boolean."
                ),
                Arguments.of(
                    "a condition that is broken reports its own fault and the loop's requirement (ADR 0003)",
                    "while (1 + true) {}",
                    "1:8: illegal binary operation 'I32 + Boolean'." + System.lineSeparator() +
                            "1:8: type mismatch, found <invalid> but expected Boolean."
                ),
                Arguments.of(
                    "a non-boolean condition and a broken body statement are both reported",
                    "while (1) { b + 1; }",
                    "1:8: type mismatch, found I32 but expected Boolean." + System.lineSeparator() +
                            "1:13: unknown symbol 'b'."
                ),
                Arguments.of(
                    "every loop with a non-boolean condition is reported",
                    "while (1) {}\nwhile (2) {}",
                    "1:8: type mismatch, found I32 but expected Boolean." + System.lineSeparator() +
                            "2:8: type mismatch, found I32 but expected Boolean."
                ),
                Arguments.of(
                    "an if decorates its condition and both branches",
                    "let a = 1;\nif (a < 3) {\n    println a;\n} else {\n    println 0;\n}",
                    ""
                ),
                Arguments.of("an if with empty branches is not a diagnostic", "if (true) {} else {}", ""),
                Arguments.of("a boolean condition is accepted by an if", "let b = true;\nif (b) {}", ""),
                Arguments.of(
                    "an integer condition is rejected by an if in the same words as by a loop",
                    "if (1) {}",
                    "1:5: type mismatch, found I32 but expected Boolean."
                ),
                Arguments.of(
                    "a float condition is rejected by an if too",
                    "if (1.5) {}",
                    "1:5: type mismatch, found F64 but expected Boolean."
                ),
                Arguments.of(
                    "a broken if condition reports its own fault and the requirement (ADR 0003)",
                    "if (1 + true) {}",
                    "1:5: illegal binary operation 'I32 + Boolean'." + System.lineSeparator() +
                            "1:5: type mismatch, found <invalid> but expected Boolean."
                ),
                Arguments.of(
                    "a bad condition and broken statements in both branches are all reported",
                    "if (1) { b + 1; } else { c + 1; }",
                    "1:5: type mismatch, found I32 but expected Boolean." + System.lineSeparator() +
                            "1:10: unknown symbol 'b'." + System.lineSeparator() +
                            "1:26: unknown symbol 'c'."
                ),
                Arguments.of(
                    "a declaration in a branch does not outlive the if",
                    "if (true) { let b = 1; } else { let c = 2; }\nprintln b;\nprintln c;",
                    "2:9: unknown symbol 'b'." + System.lineSeparator() +
                            "3:9: unknown symbol 'c'."
                ),
                Arguments.of(
                    "the two branches may each declare the same name",
                    "if (true) { let a = 1; } else { let a = 2; }",
                    ""
                ),
                Arguments.of(
                    "a branch may not shadow a declaration from around the if",
                    "let a = 1;\nif (true) {} else { let a = 2; }",
                    "2:21: variable 'a' shadows variable 'a' declared at 1:1."
                ),
                Arguments.of(
                    "an else if chain decorates every link",
                    "let a = 7;\nif (a < 5) { println 0; } else if (a < 10) { println 1; } else { println 2; }",
                    ""
                ),
                Arguments.of(
                    "a bad condition in one link of a chain does not hide one in a later link",
                    "if (1) {} else if (2) {} else { b + 1; }",
                    "1:5: type mismatch, found I32 but expected Boolean." + System.lineSeparator() +
                            "1:20: type mismatch, found I32 but expected Boolean." + System.lineSeparator() +
                            "1:33: unknown symbol 'b'."
                ),
                Arguments.of(
                    "ifs and loops nest without diagnostics",
                    "let a = 1;\nif (a < 3) {\n    while (a < 2) {\n        if (a == 1) {\n            a = a + 1;\n        }\n    }\n}",
                    ""
                ),
                Arguments.of(
                    "a broken expression statement reports once",
                    "b + 1;",
                    "1:1: unknown symbol 'b'."
                ),
                Arguments.of("unknown symbol", "b = 3;", "1:1: unknown symbol 'b'."),
                Arguments.of(
                    "assignment to an immutable variable",
                    "const a = 1;\na = 2;",
                    "2:1: cannot assign to 'a', it is not mutable. Declare it with 'let'."
                ),
                Arguments.of(
                    "declared type does not accept the value",
                    "let c: F32 = 1;",
                    "1:1: type mismatch, found I32 but expected F32."
                ),
                Arguments.of(
                    "unknown declared type",
                    "let a: Foo = 1;",
                    "1:8: unknown type 'Foo'."
                ),
                Arguments.of(
                    "redeclaration points at the first declaration",
                    "let a = 1;\nlet a = 9;",
                    "2:1: variable 'a' is already declared at 1:1."
                ),
                Arguments.of(
                    "boolean operand of an arithmetic operator",
                    "let a = true + 1;",
                    "1:9: illegal binary operation 'Boolean + I32'."
                ),
                Arguments.of(
                    "negating a boolean",
                    "let a = -true;",
                    "1:10: operator '-' cannot be applied to Boolean, expected " +
                            "I64 or I32 or I16 or I8 or F64 or F32."
                ),
                Arguments.of(
                    "one unknown symbol yields one diagnostic, not a cascade",
                    "let a = unknown + 1;",
                    "1:9: unknown symbol 'unknown'."
                ),
                Arguments.of(
                    "a poisoned declaration does not make later uses fail again",
                    "let a: Foo = 1;\na = 2;",
                    "1:8: unknown type 'Foo'."
                ),
                Arguments.of(
                    "both operands are reported",
                    "let a = left + right;",
                    """
                    1:9: unknown symbol 'left'.
                    1:16: unknown symbol 'right'.
                    """.trimIndent()
                ),
                Arguments.of(
                    "signed and unsigned operands cannot be mixed",
                    "let a = I32(1) + U32(1);",
                    "1:9: cannot mix signed and unsigned operands in 'I32 + U32'."
                ),
                Arguments.of(
                    "the operand order does not change the diagnostic",
                    "let a = U32(1) + I32(1);",
                    "1:9: cannot mix signed and unsigned operands in 'U32 + I32'."
                ),
                Arguments.of(
                    "a relational operator cannot mix signedness either",
                    "let a = I32(-1) < U32(1);",
                    "1:9: cannot mix signed and unsigned operands in 'I32 < U32'."
                ),
                Arguments.of(
                    "nor can an equality operator",
                    "let a = I32(-1) == U32(1);",
                    "1:9: cannot mix signed and unsigned operands in 'I32 == U32'."
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
                    "1:1: type mismatch, found I32 but expected Boolean."
                ),
                Arguments.of(
                    "a conjunction of booleans is a boolean",
                    "let a: Boolean = true && false;",
                    ""
                ),
                Arguments.of(
                    "a logical operator rejects a number",
                    "let a = true || 1;",
                    "1:9: illegal binary operation 'Boolean || I32'."
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
                    "1:9: illegal binary operation 'I32 && I32'."
                ),
                Arguments.of(
                    "a boolean cannot be compared to a number",
                    "let a = true == 1;",
                    "1:9: illegal binary operation 'Boolean == I32'."
                ),
                Arguments.of(
                    "booleans have no ordering",
                    "let a = true < false;",
                    "1:9: illegal binary operation 'Boolean < Boolean'."
                ),
                Arguments.of(
                    "a broken operand of a logical operator is reported once",
                    "let a = unknown && true;",
                    "1:9: unknown symbol 'unknown'."
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
                    "1:9: illegal binary operation 'F64 % F64'."
                ),
                Arguments.of(
                    "the narrower float has no remainder operator either",
                    "let a = F32(1.0) % F32(2.0);",
                    "1:9: illegal binary operation 'F32 % F32'."
                ),
                Arguments.of(
                    "one float operand is enough to rule out a remainder",
                    "let a = I32(1) % F64(2.0);",
                    "1:9: illegal binary operation 'I32 % F64'."
                ),
                Arguments.of(
                    "integer remainder still resolves",
                    "let a: I32 = I32(7) % I32(2);",
                    ""
                ),
                Arguments.of(
                    "every statement is analysed, and diagnostics come out in source order",
                    "const a = 1;\na = 2;\nb = 3;\nlet c: F32 = 1;\nlet a = 9;",
                    """
                    2:1: cannot assign to 'a', it is not mutable. Declare it with 'let'.
                    3:1: unknown symbol 'b'.
                    4:1: type mismatch, found I32 but expected F32.
                    5:1: variable 'a' is already declared at 1:1.
                    """.trimIndent()
                )
            )
        }
    }
}
