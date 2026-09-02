import dagger.Component
import me.eriknikli.rhenium.ast.AstModule
import me.eriknikli.rhenium.ast.IAstBuilder
import me.eriknikli.rhenium.semanticAnalyzer.ISemanticAnalyzer
import me.eriknikli.rhenium.semanticAnalyzer.SemanticAnalyzerModule
import me.eriknikli.rhenium.transpiler.CTranspilerModule
import me.eriknikli.rhenium.transpiler.ITranspiler
import javax.inject.Singleton

@Component(modules = [AstModule::class, SemanticAnalyzerModule::class, CTranspilerModule::class])
@Singleton
interface TranspilerTestComponent {
    fun makeAstBuilder(): IAstBuilder
    fun makeSemanticAnalyzer(): ISemanticAnalyzer
    fun makeTranspiler(): ITranspiler
}
