package me.eriknikli.rhenium.transpiler.actions

import dagger.Lazy
import me.eriknikli.rhenium.lowering.actions.Block
import me.eriknikli.rhenium.transpiler.IKindTranspiler
import me.eriknikli.rhenium.transpiler.utils.writeText
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

interface IBlockTranspiler : IKindTranspiler<Block>

@Singleton
class CBlockTranspiler
@Inject
constructor() : IBlockTranspiler {
    @Inject
    lateinit var actionTranspilerProvider: Lazy<IActionTranspiler>

    private val actionTranspiler by lazy { actionTranspilerProvider.get() }

    override fun transpile(action: Block, output: OutputStream) {
        action.actions.forEach { actionTranspiler.transpile(it, output) }
    }
}
