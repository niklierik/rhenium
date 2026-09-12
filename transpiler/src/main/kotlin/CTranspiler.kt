package me.eriknikli.rhenium.transpiler

import dagger.Lazy
import me.eriknikli.rhenium.lowering.actions.Action
import me.eriknikli.rhenium.transpiler.actions.IActionTranspiler
import me.eriknikli.rhenium.transpiler.utils.writeLineBreak
import me.eriknikli.rhenium.transpiler.utils.writeText
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

interface ITranspiler {
    fun transpile(action: Action, outputStream: OutputStream)
}

@Singleton
class CTranspiler
@Inject
constructor() : ITranspiler {
    @Inject
    lateinit var actionTranspilerProvider: Lazy<IActionTranspiler>

    private val actionTranspiler by lazy { actionTranspilerProvider.get() }

    override fun transpile(action: Action, outputStream: OutputStream) {
        outputStream.writeText(PROLOGUE)
        outputStream.writeLineBreak()
        outputStream.writeLineBreak()

        actionTranspiler.transpile(action, outputStream)
    }
}

private val PROLOGUE = """
    #include <math.h>
    #include <stdio.h>
    #include <stdlib.h>
    #include <stdint.h>
    #include <stdbool.h>
    #include <inttypes.h>

    typedef _Float32 float32_t;
    typedef _Float64 float64_t;
    typedef _Bool boolean_t;
""".trimIndent()
