package me.eriknikli.rhenium.transpiler.actions

import dagger.Lazy
import me.eriknikli.rhenium.lowering.actions.FunctionAction
import me.eriknikli.rhenium.transpiler.IKindTranspiler
import me.eriknikli.rhenium.transpiler.utils.writeText
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

interface IFunctionTranspiler : IKindTranspiler<FunctionAction>

@Singleton
class CFunctionTranspiler
@Inject
constructor() : IFunctionTranspiler {
    @Inject
    lateinit var blockTranspilerProvider: Lazy<IBlockTranspiler>

    private val blockTranspiler by lazy { blockTranspilerProvider.get() }

    override fun transpile(action: FunctionAction, output: OutputStream) {
        output.writeText("${action.cReturnType} ${action.cName}(){")
        blockTranspiler.transpile(action.body, output)
        output.writeText("}")
    }
}
