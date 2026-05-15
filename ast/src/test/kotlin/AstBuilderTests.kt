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
            
        }
    }
}