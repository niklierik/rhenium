lexer grammar RheniumLexer;

EQUALS: '=';
LET: 'let';

SEMICOLON: ';';

OPEN_BRACKET: '(';
CLOSE_BRACKET: ')';

AS: 'as';

F64: 'F64';
F32: 'F32';

I64: 'I64';
I32: 'I32';
I16: 'I16';
I8: 'I8';

U64: 'U64';
U32: 'U32';
U16: 'U16';
U8: 'U8';

ID: [a-zA-Z_][a-zA-Z0-9_]*;
FLOAT: SIGN? NUMBERPART? DECIMAL NUMBERPART;
SIGNED_INT: SIGN NUMBERPART;
UNSIGNED_INT: NUMBERPART;

fragment DECIMAL: '.';
fragment SIGN: '+' | '-';
fragment NUMBERPART: DIGIT+;
fragment DIGIT: [0-9];

NULL: 'null';

WHITESPACE: [ \t\r\n\u000C]+ -> skip;
BLOCK_COMMENT: '/*' .*? '*/' -> channel(HIDDEN);
LINE_COMMENT: '//' ~[\r\n]* -> channel(HIDDEN);
