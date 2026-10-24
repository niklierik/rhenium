parser grammar RheniumParser;

options {
    tokenVocab = RheniumLexer;
}

root:
    (entryDeclaration | functionDeclaration)* EOF;

entryDeclaration:
    ENTRY name=ID body=block;

functionDeclaration:
    FN name=ID OPEN_BRACKET (parameter (COMMA parameter)*)? CLOSE_BRACKET (COLON returnType=typeName)? body=block;

parameter:
    CONST? name=ID COLON type=typeName;

statement:
    varDeclarationStatement
    | varAssignmentStatement
    | compoundAssignmentStatement
    | incrementStatement
    | printStatement
    | expressionStatement
    | whileStatement
    | forStatement
    | ifStatement
    | breakStatement
    | continueStatement
    | returnStatement
    | block;

block:
    OPEN_BRACE statement* CLOSE_BRACE;

whileStatement:
    WHILE OPEN_BRACKET condition=expression CLOSE_BRACKET body=block;

forStatement:
    FOR OPEN_BRACKET initClause
    condition=expression SEMICOLON
    iterationExpression
    CLOSE_BRACKET body=block;

initClause:
    varDeclarationStatement
    | varAssignmentStatement
    | compoundAssignmentStatement
    | incrementStatement
    | expressionStatement;

iterationExpression:
    varAssignment
    | compoundAssignment
    | increment
    | expression;

breakStatement:
    BREAK SEMICOLON;

continueStatement:
    CONTINUE SEMICOLON;

returnStatement:
    RETURN expression? SEMICOLON;

ifStatement:
    IF OPEN_BRACKET condition=expression CLOSE_BRACKET thenBranch=block (ELSE (elseIf=ifStatement | elseBlock=block))?;

varDeclarationStatement:
    (LET | CONST) name=ID (COLON expectedType=typeName)? EQUALS expression SEMICOLON;

typeName:
    identifier
    | unsignedTypes
    | signedTypes
    | floatTypes;

varAssignmentStatement:
    varAssignment SEMICOLON;

varAssignment:
    leftValue EQUALS expression;

compoundAssignmentStatement:
    compoundAssignment SEMICOLON;

compoundAssignment:
    leftValue
    op=(PLUS_EQUALS | MINUS_EQUALS | STAR_EQUALS | SLASH_EQUALS | PERCENT_EQUALS | AND_EQUALS | OR_EQUALS)
    expression;

incrementStatement:
    increment SEMICOLON;

increment:
    leftValue op=(PLUSPLUS | MINUSMINUS);

printStatement:
    PRINT expression SEMICOLON
    | PRINTLN expression? SEMICOLON;

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
    | callee=identifier OPEN_BRACKET (expression (COMMA expression)*)? CLOSE_BRACKET #callPrimitive
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