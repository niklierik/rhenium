parser grammar RheniumParser;

options {
    tokenVocab = RheniumLexer;
}

root:
    statement* EOF;

statement:
    varDeclarationStatement
    | varAssignmentStatement
    | expressionStatement;

varDeclarationStatement:
    (LET | CONST) name=ID (COLON expectedType=typeName)? EQUALS expression SEMICOLON;

typeName:
    identifier
    | unsignedTypes
    | signedTypes
    | floatTypes;

varAssignmentStatement:
    leftValue EQUALS expression SEMICOLON;

expressionStatement:
    expression SEMICOLON;

expression:
    primary #primaryExp
    | leftValue #leftValueExp
    | op = (PLUS | MINUS | BANG) expression #unaryExp
    | expression HAT expression #powExp
    | expression op = (STAR | SLASH | PERCENT) expression #mulExp
    | expression op = (PLUS | MINUS) expression #addExp;

primary:
    OPEN_BRACKET expression CLOSE_BRACKET #groupPrimitive
    | literal #literalPrimitive;

leftValue:
    identifier;

literal:
    typedLiteral
    | basicLiteral;

identifier:
    ID;

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