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
    | left=expression op = (STAR | SLASH | PERCENT) right=expression #mulExp
    | left=expression op = (PLUS | MINUS) right=expression #addExp
    | left=expression op = (LESS | GREATER | LESS_EQUALS | GREATER_EQUALS) right=expression #relationalExp
    | left=expression op = (EQUALSEQUALS | NOTEQUALS) right=expression #equalityExp
    | left=expression op = (AND | OR) right=expression #logicalExp;

primary:
    OPEN_BRACKET expression CLOSE_BRACKET #groupPrimitive
    | literal #literalPrimitive;

leftValue:
    identifier;

literal:
    booleanLiteral
    | typedLiteral
    | basicLiteral;

booleanLiteral:
    TRUE | FALSE;

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