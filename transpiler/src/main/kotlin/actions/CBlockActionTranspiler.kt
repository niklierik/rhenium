package me.eriknikli.rhenium.transpiler.actions

import dagger.Lazy
import me.eriknikli.rhenium.lowering.actions.BlockAction
import me.eriknikli.rhenium.transpiler.IKindTranspiler
import me.eriknikli.rhenium.transpiler.utils.writeText
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

interface IBlockActionTranspiler : IKindTranspiler<BlockAction>

@Singleton
class CBlockActionTranspiler
@Inject
constructor() : IBlockActionTranspiler {
    @Inject
    lateinit var blockTranspilerProvider: Lazy<IBlockTranspiler>

    private val blockTranspiler by lazy { blockTranspilerProvider.get() }

    override fun transpile(action: BlockAction, output: OutputStream) {
        output.writeText("{")
        blockTranspiler.transpile(action.body, output)
        output.writeText("}")
    }
}
