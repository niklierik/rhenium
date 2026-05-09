parser grammar RheniumParser;

@header {
package me.eriknikli.rhenium.parser;
}

options {
    tokenVocab = RheniumLexer;
}

start:
    NULL;