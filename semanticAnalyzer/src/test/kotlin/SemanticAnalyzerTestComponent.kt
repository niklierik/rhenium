import dagger.Component
import me.eriknikli.rhenium.ast.AstModule
import me.eriknikli.rhenium.ast.IAstBuilder
import me.eriknikli.rhenium.semanticAnalyzer.ISemanticAnalyzer
import me.eriknikli.rhenium.semanticAnalyzer.SemanticAnalyzerModule
import javax.inject.Singleton

@Component(modules = [AstModule::class, SemanticAnalyzerModule::class])
@Singleton
interface SemanticAnalyzerTestComponent {
    fun makeAstBuilder(): IAstBuilder
    fun makeSemanticAnalyzer(): ISemanticAnalyzer
}
