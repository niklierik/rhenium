grammar Lang;

@header {
    package me.eriknikli.lang.grammar;
}

start:
    expression;

expression:
    primitive;

assignment:
    primitive #assignmentPrimitive
    | primitive '=' assignment #assignmentExpr;

primitive:
    group #groupPrimitive
    | literal #literalPrimitive
    | Id #idPrimitive
;

group:
    '(' expression ')'
;

literal:
	typedNumericLiteral #typed
	| IntegerLiteral #integer
	| FloatingPointLiteral #real
	| BooleanLiteral #bool
	| NullLiteral #null
	| StringLiteral #string
	| CharacterLiteral #character
	// | TextBlock #textBlock
;

typedNumericLiteral:
	F64 (FloatingPointLiteral) #f64
	| F32 (FloatingPointLiteral) #f32
	| I64 (IntegerLiteral) #i64
	| I32 (IntegerLiteral) #i32
	| I16 (IntegerLiteral) #i16
	| I8 (IntegerLiteral) #i8
	| U64 (IntegerLiteral) #u64
	| U32 (IntegerLiteral) #u32
	| U16 (IntegerLiteral) #u16
	| U8 (IntegerLiteral) #u8
;

F64	: 'f64';
F32	: 'f32';

I64	: 'i64';
I32	: 'i32';
I16	: 'i16';
I8	: 'i8';

U64	: 'u64';
U32	: 'u32';
U16	: 'u16';
U8	: 'u8';

// Lexer for literals and basic tokens from https://github.com/antlr/grammars-v4/blob/master/java/java20/Java20Lexer.g4
IntegerLiteral:
	DecimalIntegerLiteral
	| HexIntegerLiteral
	| BinaryIntegerLiteral
;

fragment DecimalIntegerLiteral: DecimalNumeral;

fragment HexIntegerLiteral: HexNumeral;

fragment BinaryIntegerLiteral: BinaryNumeral;

fragment DecimalNumeral:
	'0'
	| NonZeroDigit (Digits? | Underscores Digits)
;

fragment Digits:
	Digit (DigitsAndUnderscores? Digit)?
;

fragment Digit: '0' | NonZeroDigit;

fragment NonZeroDigit: [1-9];

fragment DigitsAndUnderscores: DigitOrUnderscore+;

fragment DigitOrUnderscore: Digit | '_';

fragment Underscores: '_'+;

fragment HexNumeral: '0' [xX] HexDigits;

fragment HexDigits:
	HexDigit (HexDigitsAndUnderscores? HexDigit)?
;

fragment HexDigit: [0-9a-fA-F];

fragment HexDigitsAndUnderscores:
	HexDigitOrUnderscore+
;

fragment HexDigitOrUnderscore: HexDigit | '_';

fragment BinaryNumeral: '0' [bB] BinaryDigits;

fragment BinaryDigits:
	BinaryDigit (
		BinaryDigitsAndUnderscores? BinaryDigit
	)?
;

fragment BinaryDigit: [01];

fragment BinaryDigitsAndUnderscores:
	BinaryDigitOrUnderscore+
;

fragment BinaryDigitOrUnderscore:
	BinaryDigit
	| '_'
;

FloatingPointLiteral:
	DecimalFloatingPointLiteral
	| HexadecimalFloatingPointLiteral
;

fragment DecimalFloatingPointLiteral:
	Digits '.' Digits? ExponentPart?
	| '.' Digits ExponentPart?
	| Digits ExponentPart
	| Digits
;

fragment ExponentPart:
	ExponentIndicator SignedInteger
;

fragment ExponentIndicator: [eE];

fragment SignedInteger: Sign? Digits;

fragment Sign: [+-];

fragment HexadecimalFloatingPointLiteral:
	HexSignificand BinaryExponent
;

fragment HexSignificand:
	HexNumeral '.'?
	| '0' [xX] HexDigits? '.' HexDigits
;

fragment BinaryExponent:
	BinaryExponentIndicator SignedInteger
;

fragment BinaryExponentIndicator: [pP];

BooleanLiteral: 'true' | 'false';

CharacterLiteral:
	'\'' SingleCharacter '\''
	| '\'' EscapeSequence '\''
;

fragment SingleCharacter: ~['\\\r\n];

StringLiteral: '"' StringCharacters? '"';

fragment StringCharacters: StringCharacter+;

fragment StringCharacter:
	~["\\\r\n]
	| EscapeSequence
;

TextBlock: '"""' [ \t]* [\n\r] [.\r\b]* '"""';

fragment EscapeSequence:
	'\\' [btnfr"'\\]
	| UnicodeEscape
;

fragment ZeroToThree: [0-3];

fragment UnicodeEscape:
	'\\' 'u'+ HexDigit HexDigit HexDigit HexDigit
;

NullLiteral: 'null';

Id: [a-zA-Z_][a-zA-Z0-9_]*;

WS				: [ \t\r\n\u000C]+ -> skip;
COMMENT			: '/*' .*? '*/' -> channel(HIDDEN);
LINE_COMMENT	: '//' ~[\r\n]* -> channel(HIDDEN);