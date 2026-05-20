package me.eriknikli.rhenium.transpiler

import me.eriknikli.rhenium.ast.tree.RootNode
import me.eriknikli.rhenium.transpiler.tree.IRootTranspiler
import me.eriknikli.rhenium.transpiler.utils.writeLineBreak
import me.eriknikli.rhenium.transpiler.utils.writeText
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

interface ITranspiler {
    fun transpile(root: RootNode, outputStream: OutputStream)
}

@Singleton
class CTranspiler
@Inject constructor(
    private val rootTranspiler: IRootTranspiler
) : ITranspiler {
    override fun transpile(root: RootNode, outputStream: OutputStream) {
        outputStream.writeText(
            """
            #include <math.h>
            #include <stdio.h>
            #include <stdlib.h>
            #include <stdint.h>
            #include <stdbool.h>
            
            typedef _Float32 float32_t;
            typedef _Float64 float64_t;
            typedef _Bool boolean_t;
            """.trimIndent()
        )
        outputStream.writeLineBreak()
        outputStream.writeLineBreak()

        rootTranspiler.transpile(root, outputStream)
    }
}