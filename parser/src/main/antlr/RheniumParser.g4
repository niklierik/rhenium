parser grammar RheniumParser;

options {
    tokenVocab = RheniumLexer;
}

root:
    literal* EOF;

literal:
    typedLiteral
    | basicLiteral;

unsignedTypes:
    U64
    | U32
    | U16
    | U8;

signedTypes:
    I64 | I32 | I16  | I8;

floatTypes: F64 | F32;

typedLiteral:
    unsignedTypes OPEN_BRACKET UNSIGNED_INT CLOSE_BRACKET #unsigned
    | signedTypes OPEN_BRACKET (SIGNED_INT | UNSIGNED_INT) CLOSE_BRACKET #signed
    | floatTypes OPEN_BRACKET (SIGNED_INT | UNSIGNED_INT | FLOAT) CLOSE_BRACKET #float;

basicLiteral: UNSIGNED_INT #unsignedBasic | SIGNED_INT #signedBasic | FLOAT #floatBasic;