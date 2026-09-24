import dagger.Component
import me.eriknikli.rhenium.transpiler.CTranspilerModule
import me.eriknikli.rhenium.transpiler.ITranspiler
import me.eriknikli.rhenium.transpiler.actions.IActionTranspiler
import javax.inject.Singleton

@Component(modules = [CTranspilerModule::class])
@Singleton
interface TranspilerTestComponent {
    fun makeTranspiler(): ITranspiler
    fun makeActionTranspiler(): IActionTranspiler
}
