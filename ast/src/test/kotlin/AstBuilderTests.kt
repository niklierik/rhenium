import me.eriknikli.rhenium.ast.IAstBuilder
import me.eriknikli.rhenium.ast.tree.RootNode
import me.eriknikli.rhenium.ast.tree.expressions.literals.*
import org.antlr.v4.runtime.CharStreams
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.assertEquals

class AstBuilderTests {
    private val astBuilder: IAstBuilder = DaggerAstTestComponent.create().makeAstBuilder()

    @ParameterizedTest(name = "Run {index}, name {0}")
    @MethodSource("provideData")
    fun `test ast building`(name: String, sourceCode: String, expectedTree: RootNode) {
        val stream = CharStreams.fromString(sourceCode)

        val actualTree = astBuilder.parse(stream)

        assertEquals(expectedTree, actualTree)
    }

    companion object {
        @JvmStatic
        fun provideData(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("Literal test, default unsigned int", "12", RootNode(listOf(I32Literal(12)))),
                Arguments.of("Literal test, default signed int", "-43", RootNode(listOf(I32Literal(-43)))),
                Arguments.of("Literal test, default float", "69.420", RootNode(listOf(F64Literal(69.420)))),

                Arguments.of("Literal test, f64", "F64(-3.464)", RootNode(listOf(F64Literal(-3.464)))),
                Arguments.of("Literal test, f32", "F32(+3.5)", RootNode(listOf(F32Literal(+3.5f)))),

                Arguments.of("Literal test, i64", "I64(+354)", RootNode(listOf(I64Literal(+354)))),
                Arguments.of("Literal test, i32", "I32(-35)", RootNode(listOf(I32Literal(-35)))),
                Arguments.of("Literal test, i16", "I16(255)", RootNode(listOf(I16Literal(255)))),
                Arguments.of("Literal test, i8", "I8(5)", RootNode(listOf(I8Literal(5)))),

                Arguments.of("Literal test, u64", "U64(3542)", RootNode(listOf(U64Literal(3542u)))),
                Arguments.of("Literal test, u32", "U32(432)", RootNode(listOf(U32Literal(432u)))),
                Arguments.of("Literal test, u16", "U16(94)", RootNode(listOf(U16Literal(94u)))),
                Arguments.of("Literal test, u8", "U8(2)", RootNode(listOf(U8Literal(2u)))),
            )
        }
    }
}