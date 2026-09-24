import me.eriknikli.rhenium.lowering.actions.*
import me.eriknikli.rhenium.semanticContext.scope.types.SignedIntType
import me.eriknikli.rhenium.semanticContext.scope.types.UnsignedIntType
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.util.stream.Stream
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TranspilerTests {
    private val component = DaggerTranspilerTestComponent.create()
    private val actionTranspiler = component.makeActionTranspiler()
    private val transpiler = component.makeTranspiler()

    @ParameterizedTest(name = "Run {index}, name {0}")
    @MethodSource("provideData")
    fun `test emitted c`(name: String, action: Action, expectedC: String) {
        assertEquals(expectedC, emit { actionTranspiler.transpile(action, it) })
    }

    @Test
    fun `the prologue precedes the program and declares the c names the types rely on`() {
        val program = FunctionAction("main", "int32_t", Block(mutableListOf(ReturnAction(ConstantAction("0")))))

        val c = emit { transpiler.transpile(program, it) }

        listOf(
            "#include <math.h>",
            "#include <stdio.h>",
            "#include <stdlib.h>",
            "#include <stdint.h>",
            "#include <stdbool.h>",
            "#include <inttypes.h>",
            "typedef _Float32 float32_t;",
            "typedef _Float64 float64_t;",
            "typedef _Bool boolean_t;"
        ).forEach { assertContains(c, it) }

        assertTrue(c.endsWith("int32_t main(){return 0;}"), "expected the program to follow the prologue, got:\n$c")
    }

    companion object {
        private fun emit(write: (OutputStream) -> Unit): String =
            ByteArrayOutputStream().also(write).toString(Charsets.UTF_8)

        @JvmStatic
        fun provideData(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    "a constant is written verbatim, because lowering already decided its c spelling",
                    ConstantAction("1"),
                    "1"
                ),
                Arguments.of(
                    "a variable reference writes only its mangled name",
                    VarRefAction("re_a_1"),
                    "re_a_1"
                ),
                Arguments.of(
                    "a cast parenthesises the type and the expression it wraps",
                    CastAction(SignedIntType.I32, ConstantAction("1")),
                    "((int32_t)1)"
                ),
                Arguments.of(
                    "a binary operation is parenthesised, so precedence never depends on c",
                    BinaryAction("+", ConstantAction("1"), ConstantAction("2")),
                    "(1+2)"
                ),
                Arguments.of(
                    "a unary operation is parenthesised for the same reason",
                    UnaryAction("-", ConstantAction("1")),
                    "(-1)"
                ),
                Arguments.of(
                    "a ternary is parenthesised",
                    TernaryAction(ConstantAction("true"), ConstantAction("\"true\""), ConstantAction("\"false\"")),
                    """(true?"true":"false")"""
                ),
                Arguments.of(
                    "a print writes the format the action carries, then the value after a comma",
                    PrintAction("\"%\" PRId32", ConstantAction("1")),
                    """printf("%" PRId32,1);"""
                ),
                Arguments.of(
                    "a print with no value writes the format alone",
                    PrintAction("\"\\n\"", null),
                    """printf("\n");"""
                ),
                Arguments.of(
                    "a println format is two adjacent c string literals, and neither is rewritten",
                    PrintAction("\"%\" PRId32 \"\\n\"", ConstantAction("1")),
                    """printf("%" PRId32 "\n",1);"""
                ),
                Arguments.of(
                    "a value that is itself an expression nests inside the printf argument",
                    PrintAction(
                        "\"%s\"",
                        TernaryAction(
                            VarRefAction("re_flag_1"),
                            ConstantAction("\"true\""),
                            ConstantAction("\"false\"")
                        )
                    ),
                    """printf("%s",(re_flag_1?"true":"false"));"""
                ),
                Arguments.of(
                    "a return separates itself from its value, so the c stays tokenisable",
                    ReturnAction(ConstantAction("0")),
                    "return 0;"
                ),
                Arguments.of(
                    "a bare return writes no separator",
                    ReturnAction(null),
                    "return;"
                ),
                Arguments.of(
                    "a declaration writes the type, the mangled name and the value",
                    VarDeclarationAction(SignedIntType.I32, "re_a_1", ConstantAction("1")),
                    "int32_t re_a_1=1;"
                ),
                Arguments.of(
                    "an assignment writes no type",
                    AssignmentAction(VarRefAction("re_a_1"), ConstantAction("2")),
                    "re_a_1=2;"
                ),
                Arguments.of(
                    "an expression statement writes only a semicolon after its value",
                    ExpressionStatementAction(ConstantAction("1")),
                    "1;"
                ),
                Arguments.of(
                    "a block concatenates its actions with no separator of its own",
                    Block(
                        mutableListOf(
                            ExpressionStatementAction(ConstantAction("1")),
                            ExpressionStatementAction(ConstantAction("2"))
                        )
                    ),
                    "1;2;"
                ),
                Arguments.of(
                    "an empty block writes nothing",
                    Block(mutableListOf()),
                    ""
                ),
                Arguments.of(
                    "a function writes its return type before its name",
                    FunctionAction("main", "int32_t", Block(mutableListOf(ReturnAction(ConstantAction("0"))))),
                    "int32_t main(){return 0;}"
                ),
                Arguments.of(
                    "casts nest to arbitrary depth, each adding exactly one pair of parentheses",
                    CastAction(
                        UnsignedIntType.U16,
                        BinaryAction(
                            "*",
                            CastAction(UnsignedIntType.U32, CastAction(UnsignedIntType.U16, ConstantAction("3u"))),
                            CastAction(UnsignedIntType.U32, CastAction(UnsignedIntType.U16, ConstantAction("4u")))
                        )
                    ),
                    "((uint16_t)(((uint32_t)((uint16_t)3u))*((uint32_t)((uint16_t)4u))))"
                )
            )
        }
    }
}
