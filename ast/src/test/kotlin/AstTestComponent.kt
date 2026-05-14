import dagger.Component
import me.eriknikli.rhenium.ast.AstModule
import me.eriknikli.rhenium.ast.IAstBuilder
import javax.inject.Singleton

@Component(modules = [AstModule::class])
@Singleton
interface AstTestComponent {
    fun makeAstBuilder(): IAstBuilder
}