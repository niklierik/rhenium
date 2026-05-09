lexer grammar RheniumLexer;

@header {
package me.eriknikli.rhenium.parser;
}

NULL: 'null';

WHITESPACE: [ \t\r\n\u000C]+ -> skip;
BLOCK_COMMENT: '/*' .*? '*/' -> channel(HIDDEN);
LINE_COMMENT: '//' ~[\r\n]* -> channel(HIDDEN);
