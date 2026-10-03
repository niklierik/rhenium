import arrow.core.getOrElse
import me.eriknikli.rhenium.ast.IAstBuilder
import me.eriknikli.rhenium.ast.tree.AstNode
import me.eriknikli.rhenium.ast.tree.RootNode
import me.eriknikli.rhenium.ast.tree.expressions.Identifier
import me.eriknikli.rhenium.ast.tree.expressions.literals.Literal
import me.eriknikli.rhenium.ast.tree.expressions.operators.BinaryOpExpression
import me.eriknikli.rhenium.ast.tree.expressions.operators.UnaryOpExpression
import me.eriknikli.rhenium.ast.tree.statements.BlockStatement
import me.eriknikli.rhenium.ast.tree.statements.ExpressionStatement
import me.eriknikli.rhenium.ast.tree.statements.IfStatement
import me.eriknikli.rhenium.ast.tree.statements.PrintStatement
import me.eriknikli.rhenium.ast.tree.statements.WhileStatement
import me.eriknikli.rhenium.ast.tree.statements.vars.CompoundAssignmentStatement
import me.eriknikli.rhenium.ast.tree.statements.vars.IncrementStatement
import me.eriknikli.rhenium.ast.tree.statements.vars.VarAssignmentStatement
import me.eriknikli.rhenium.ast.tree.statements.vars.VarDeclarationStatement
import me.eriknikli.rhenium.common.diagnostics.render
import org.antlr.v4.runtime.CharStreams
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class AstBuilderTests {
    private val astBuilder: IAstBuilder = DaggerAstTestComponent.create().makeAstBuilder()

    @ParameterizedTest(name = "Run {index}, name {0}")
    @MethodSource("provideData")
    fun `test ast building`(name: String, sourceCode: String, expectedTree: String) {
        val stream = CharStreams.fromString(sourceCode)

        val actualTree = astBuilder.parse(stream)
            .getOrElse { fail("expected a tree, got diagnostics:\n${it.render()}") }

        assertEquals(expectedTree, actualTree.sexpr())
    }

    @ParameterizedTest(name = "Run {index}, name {0}")
    @MethodSource("provideDiagnostics")
    fun `test ast diagnostics`(name: String, sourceCode: String, expectedDiagnostics: String) {
        val stream = CharStreams.fromString(sourceCode)

        val diagnostics = astBuilder.parse(stream).leftOrNull()
            ?: fail("expected diagnostics, but the source parsed successfully")

        assertEquals(expectedDiagnostics, diagnostics.render())
    }

    @Test
    fun `print without an operand does not parse`() {
        val stream = CharStreams.fromString("print;")

        val diagnostics = astBuilder.parse(stream).leftOrNull()
            ?: fail("expected diagnostics, but the source parsed successfully")

        assertEquals(1, diagnostics.size)
        assertEquals(1, diagnostics.head.line)
        assertEquals(6, diagnostics.head.column)
    }

    @Test
    fun `else is followed by a block or an if and nothing else`() {
        val stream = CharStreams.fromString("if (true) {} else while (true) {}")

        val diagnostics = astBuilder.parse(stream).leftOrNull()
            ?: fail("expected diagnostics, but the source parsed successfully")

        assertEquals(1, diagnostics.head.line)
        assertEquals(19, diagnostics.head.column)
    }

    @ParameterizedTest(name = "Run {index}, name {0}")
    @MethodSource("provideSyntaxErrors")
    fun `test syntax errors`(name: String, sourceCode: String, expectedColumn: Int) {
        val stream = CharStreams.fromString(sourceCode)

        val diagnostics = astBuilder.parse(stream).leftOrNull()
            ?: fail("expected diagnostics, but the source parsed successfully")

        assertEquals(1, diagnostics.head.line)
        assertEquals(expectedColumn, diagnostics.head.column)
    }

    @Test
    fun `syntax errors are reported instead of printed`() {
        val stream = CharStreams.fromString("let a = ;")

        val diagnostics = astBuilder.parse(stream).leftOrNull()
            ?: fail("expected diagnostics, but the source parsed successfully")

        assertEquals(1, diagnostics.size)
        assertEquals(1, diagnostics.head.line)
        assertEquals(9, diagnostics.head.column)
        assertTrue(diagnostics.head.message.isNotBlank())
    }

    companion object {
        @JvmStatic
        fun provideData(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("mutable declaration", "let a = 1;", "(root (let a (i32 1)))"),
                Arguments.of("immutable declaration", "const a = 1;", "(root (const a (i32 1)))"),
                Arguments.of("declared type", "let a: I64 = I64(5);", "(root (let a : I64 (i64 5)))"),
                Arguments.of("float literal", "let a = 1.5;", "(root (let a (f64 1.5)))"),
                Arguments.of("typed float literal", "let a = F32(1.5);", "(root (let a (f32 1.5)))"),
                Arguments.of("assignment", "a = false;", "(root (= a (boolean false)))"),
                Arguments.of("compound addition", "a += 1;", "(root (+= a (i32 1)))"),
                Arguments.of("compound subtraction", "a -= 1;", "(root (-= a (i32 1)))"),
                Arguments.of("compound multiplication", "a *= 1;", "(root (*= a (i32 1)))"),
                Arguments.of("compound division", "a /= 1;", "(root (/= a (i32 1)))"),
                Arguments.of("compound remainder", "a %= 1;", "(root (%= a (i32 1)))"),
                Arguments.of("compound and", "a &&= true;", "(root (&&= a (boolean true)))"),
                Arguments.of("compound or", "a ||= true;", "(root (||= a (boolean true)))"),
                Arguments.of("increment", "a++;", "(root (++ a))"),
                Arguments.of("decrement", "a--;", "(root (-- a))"),
                Arguments.of(
                    "multiplication binds tighter than addition",
                    "let a = 1 + 2 * 3;",
                    "(root (let a (+ (i32 1) (* (i32 2) (i32 3)))))"
                ),
                Arguments.of(
                    "grouping overrides precedence",
                    "let a = (1 + 2) * 3;",
                    "(root (let a (* (+ (i32 1) (i32 2)) (i32 3))))"
                ),
                Arguments.of("less than", "let a = 1 < 2;", "(root (let a (< (i32 1) (i32 2))))"),
                Arguments.of("greater than", "let a = 1 > 2;", "(root (let a (> (i32 1) (i32 2))))"),
                Arguments.of("not equals", "let a = 1 != 2;", "(root (let a (!= (i32 1) (i32 2))))"),
                Arguments.of(
                    "logical operators bind loosest",
                    "let a = 1 < 2 && true;",
                    "(root (let a (&& (< (i32 1) (i32 2)) (boolean true))))"
                ),
                Arguments.of(
                    "unary negation",
                    "let a = !(2 + 3 == 4 + 1);",
                    "(root (let a (! (== (+ (i32 2) (i32 3)) (+ (i32 4) (i32 1))))))"
                ),
                Arguments.of(
                    "multiple statements",
                    "let a = 1;\na = 2;",
                    "(root (let a (i32 1)) (= a (i32 2)))"
                ),
                Arguments.of("expression statement", "1 + 2;", "(root (expr (+ (i32 1) (i32 2))))"),
                Arguments.of(
                    "an expression statement beside a declaration",
                    "let a = 1;\na + 1;",
                    "(root (let a (i32 1)) (expr (+ a (i32 1))))"
                ),
                Arguments.of("print", "print 1;", "(root (print (i32 1)))"),
                Arguments.of("println", "println 1;", "(root (println (i32 1)))"),
                Arguments.of("bare println", "println;", "(root (println))"),
                Arguments.of("an empty block", "{}", "(root (block))"),
                Arguments.of(
                    "a block holds statements",
                    "let a = 1;\n{\n    println a;\n}",
                    "(root (let a (i32 1)) (block (println a)))"
                ),
                Arguments.of(
                    "blocks nest",
                    "{ { let a = 1; } }",
                    "(root (block (block (let a (i32 1)))))"
                ),
                Arguments.of(
                    "a while loop holds its condition and its body",
                    "while (true) { println 1; }",
                    "(root (while (boolean true) (block (println (i32 1)))))"
                ),
                Arguments.of(
                    "a while loop with an empty body still has one",
                    "while (true) {}",
                    "(root (while (boolean true) (block)))"
                ),
                Arguments.of(
                    "while loops nest",
                    "while (true) { while (false) {} }",
                    "(root (while (boolean true) (block (while (boolean false) (block)))))"
                ),
                Arguments.of(
                    "an if holds its condition and its branch",
                    "if (true) { println 1; }",
                    "(root (if (boolean true) (block (println (i32 1)))))"
                ),
                Arguments.of(
                    "an if with an else holds both branches",
                    "if (true) {} else { println 1; }",
                    "(root (if (boolean true) (block) (block (println (i32 1)))))"
                ),
                Arguments.of(
                    "an else if is an if nested in the else side",
                    "if (true) {} else if (false) { println 1; }",
                    "(root (if (boolean true) (block) (if (boolean false) (block (println (i32 1))))))"
                ),
                Arguments.of(
                    "an else if chain may end in an else",
                    "if (true) {} else if (false) {} else {}",
                    "(root (if (boolean true) (block) (if (boolean false) (block) (block))))"
                ),
                Arguments.of(
                    "ifs and while loops nest",
                    "if (true) { while (false) { if (false) {} } }",
                    "(root (if (boolean true) (block (while (boolean false) (block (if (boolean false) (block)))))))"
                ),
                Arguments.of(
                    "println takes a whole expression",
                    "println 1 + 2;",
                    "(root (println (+ (i32 1) (i32 2))))"
                ),
                Arguments.of("empty program", "", "(root)")
            )
        }

        @JvmStatic
        fun provideSyntaxErrors(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("a compound assignment is not an expression", "y = x += 1;", 7),
                Arguments.of("compound assignments do not chain", "a += b += c;", 8),
                Arguments.of("an increment is not a value to assign", "y = x++;", 6),
                Arguments.of("an increment is not a value to print", "print x++;", 8),
                Arguments.of("an increment is not an operand", "x++ + 1;", 5),
                Arguments.of("there is no prefix increment", "++x;", 1)
            )
        }

        @JvmStatic
        fun provideDiagnostics(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    "integer literal out of range",
                    "let a = 99999999999999999999;",
                    "1:9: '99999999999999999999' is not a valid I32 literal."
                ),
                Arguments.of(
                    "typed literal out of range",
                    "let a = U8(300);",
                    "1:9: '300' is not a valid U8 literal."
                ),
                Arguments.of(
                    "both operands are reported, not just the first",
                    "let a = U8(300) + U8(400);",
                    """
                    1:9: '300' is not a valid U8 literal.
                    1:19: '400' is not a valid U8 literal.
                    """.trimIndent()
                ),
                Arguments.of(
                    "a float literal that overflows to infinity is not a valid literal",
                    "let a = F32(30000000000000000000000000000000000000000.0);",
                    "1:9: '30000000000000000000000000000000000000000.0' is not a valid F32 literal."
                ),
                Arguments.of(
                    "the same holds for the wider float",
                    "let a = F64(10000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000.0);",
                    "1:9: '10000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000.0' is not a valid F64 literal."
                ),
                Arguments.of(
                    "every statement is reported",
                    "let a = U8(300);\nlet b = U8(400);",
                    """
                    1:9: '300' is not a valid U8 literal.
                    2:9: '400' is not a valid U8 literal.
                    """.trimIndent()
                )
            )
        }

        private fun AstNode.sexpr(): String = when (this) {
            is RootNode -> (listOf("root") + statements.map { it.sexpr() }).joinToString(" ", "(", ")")

            is VarDeclarationStatement -> {
                val keyword = if (mutable) "let" else "const"
                val type = expectedType?.let { " : ${it.id}" } ?: ""
                "($keyword $name$type ${rightSide.sexpr()})"
            }

            is VarAssignmentStatement -> "(= ${leftValue.sexpr()} ${rightValue.sexpr()})"
            is IncrementStatement -> "(${operator.cString}${operator.cString} ${leftValue.sexpr()})"
            is CompoundAssignmentStatement ->
                "(${operator.cString}= ${leftValue.sexpr()} ${rightValue.sexpr()})"
            is ExpressionStatement -> "(expr ${expression.sexpr()})"

            is BlockStatement ->
                (listOf("block") + statements.map { it.sexpr() }).joinToString(" ", "(", ")")

            is WhileStatement -> "(while ${condition.sexpr()} ${body.sexpr()})"

            is IfStatement -> {
                val elseSide = elseBranch?.let { " ${it.sexpr()}" } ?: ""
                "(if ${condition.sexpr()} ${thenBranch.sexpr()}$elseSide)"
            }

            is PrintStatement -> {
                val keyword = if (newLine) "println" else "print"
                val operand = expression?.let { " ${it.sexpr()}" } ?: ""
                "($keyword$operand)"
            }
            is BinaryOpExpression -> "(${operator.cString} ${left.sexpr()} ${right.sexpr()})"
            is UnaryOpExpression -> "(${operator.cString} ${expression.sexpr()})"
            is Identifier -> id
            is Literal<*> -> "(${javaClass.simpleName.removeSuffix("Literal").lowercase()} $textVersion)"
            else -> error("Unhandled node type in test renderer: ${javaClass.simpleName}")
        }
    }
}
