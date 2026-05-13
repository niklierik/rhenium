package me.eriknikli.rhenium.ast

import org.antlr.v4.runtime.CharStream

interface IAstBuilder {
    fun parse(stream: CharStream)
}