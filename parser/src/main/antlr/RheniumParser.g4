parser grammar RheniumParser;

options {
    tokenVocab = RheniumLexer;
}

root:
    literal* EOF;

literal:
    typedLiteral | basicLiteral;

unsignedTypes:
    U64 | U32 | U16 | U8;

signedTypes:
    I64 | I32 | I16 | I8;

floatTypes: F64 | F32;

typedLiteral:
    unsignedTypes OPEN_BRACKET UNSIGNED_INT CLOSE_BRACKET
    | signedTypes OPEN_BRACKET (SIGNED_INT | UNSIGNED_INT) CLOSE_BRACKET
    | floatTypes OPEN_BRACKET (SIGNED_INT | UNSIGNED_INT | FLOAT) CLOSE_BRACKET;

basicLiteral: UNSIGNED_INT | SIGNED_INT | FLOAT;