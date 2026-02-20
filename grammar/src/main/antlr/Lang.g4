grammar Lang;

@header {
    package me.eriknikli.lang.grammar;

    import me.eriknikli.lang.grammar.*;
    import me.eriknikli.lang.grammar.literals.*;
}

start:
    literals;

typedNumericLiteral returns [Expression expression]:
    F64 (FloatingPointLiteral) {
        var valueString = $FloatingPointLiteral.text;
        var value = Double.parse(valueString);
        $expression = new F64Literal(value);
    } |
    F32 (FloatingPointLiteral) {
            var valueString = $FloatingPointLiteral.text;
            var value = Float.parse(valueString);
            $expression = new F32Literal(value);
        } |
    I64 (IntegerLiteral) {
            var valueString = $IntegerLiteral.text;
            var value = Long.parse(valueString);
            $expression = new I64Literal(value);
        } |
    I32 (IntegerLiteral) {
                var valueString = $IntegerLiteral.text;
                var value = Integer.parse(valueString);
                $expression = new I32Literal(value);
            } |
    I16 (IntegerLiteral) {
                var valueString = $IntegerLiteral.text;
                var value = Short.parse(valueString);
                $expression = new I16Literal(value);
            } |
    I8 (IntegerLiteral) {
                var valueString = $IntegerLiteral.text;
                var value = Byte.parse(valueString);
                $expression = new I8Literal(value);
            } |
    U64 (IntegerLiteral) {
                var valueString = $IntegerLiteral.text;
                $expression = new U64Literal(valueString);
            } |
    U32 (IntegerLiteral) {
                var valueString = $IntegerLiteral.text;
                $expression = new U32Literal(valueString);
            } |
    U16 (IntegerLiteral) {
                var valueString = $IntegerLiteral.text;
                $expression = new U16Literal(valueString);
            } |
    U8 (IntegerLiteral) {
                var valueString = $IntegerLiteral.text;
                $expression = new U8Literal(valueString);
            };

literals returns [Expression expression]:
    typedNumericLiteral {
        $expression = $typedNumericLiteral.expression;
    } |
    IntegerLiteral {
        var valueString = $IntegerLiteral.text;
        $expression = new U64Literal(valueString);
    } |
    FloatingPointLiteral {
        var valueString = $IntegerLiteral.text;
        $expression = new U64Literal(valueString);
    } |
    BooleanLiteral |
    NullLiteral |
    StringLiteral |
    CharacterLiteral |
    TextBlock |
    Id;


F64: 'f64';
F32: 'f32';

I64: 'i64';
I32: 'i32';
I16: 'i16';
I8: 'i8';

U64: 'u64';
U32: 'u32';
U16: 'u16';
U8: 'u8';

// Lexer for literals and basic stuffs from https://github.com/antlr/grammars-v4/blob/master/java/java20/Java20Lexer.g4
IntegerLiteral:
    DecimalIntegerLiteral
    | HexIntegerLiteral
    | OctalIntegerLiteral
    | BinaryIntegerLiteral
;

fragment DecimalIntegerLiteral: DecimalNumeral;

fragment HexIntegerLiteral: HexNumeral;

fragment OctalIntegerLiteral: OctalNumeral;

fragment BinaryIntegerLiteral: BinaryNumeral;

fragment DecimalNumeral: '0' | NonZeroDigit (Digits? | Underscores Digits);

fragment Digits: Digit (DigitsAndUnderscores? Digit)?;

fragment Digit: '0' | NonZeroDigit;

fragment NonZeroDigit: [1-9];

fragment DigitsAndUnderscores: DigitOrUnderscore+;

fragment DigitOrUnderscore: Digit | '_';

fragment Underscores: '_'+;

fragment HexNumeral: '0' [xX] HexDigits;

fragment HexDigits: HexDigit (HexDigitsAndUnderscores? HexDigit)?;

fragment HexDigit: [0-9a-fA-F];

fragment HexDigitsAndUnderscores: HexDigitOrUnderscore+;

fragment HexDigitOrUnderscore: HexDigit | '_';

fragment OctalNumeral: '0' Underscores? OctalDigits;

fragment OctalDigits: OctalDigit (OctalDigitsAndUnderscores? OctalDigit)?;

fragment OctalDigit: [0-7];

fragment OctalDigitsAndUnderscores: OctalDigitOrUnderscore+;

fragment OctalDigitOrUnderscore: OctalDigit | '_';

fragment BinaryNumeral: '0' [bB] BinaryDigits;

fragment BinaryDigits: BinaryDigit (BinaryDigitsAndUnderscores? BinaryDigit)?;

fragment BinaryDigit: [01];

fragment BinaryDigitsAndUnderscores: BinaryDigitOrUnderscore+;

fragment BinaryDigitOrUnderscore: BinaryDigit | '_';

FloatingPointLiteral: DecimalFloatingPointLiteral | HexadecimalFloatingPointLiteral;

fragment DecimalFloatingPointLiteral:
    Digits '.' Digits? ExponentPart?
    | '.' Digits ExponentPart?
    | Digits ExponentPart
    | Digits
;

fragment ExponentPart: ExponentIndicator SignedInteger;

fragment ExponentIndicator: [eE];

fragment SignedInteger: Sign? Digits;

fragment Sign: [+-];

fragment HexadecimalFloatingPointLiteral: HexSignificand BinaryExponent;

fragment HexSignificand: HexNumeral '.'? | '0' [xX] HexDigits? '.' HexDigits;

fragment BinaryExponent: BinaryExponentIndicator SignedInteger;

fragment BinaryExponentIndicator: [pP];

BooleanLiteral: 'true' | 'false';

CharacterLiteral: '\'' SingleCharacter '\'' | '\'' EscapeSequence '\'';

fragment SingleCharacter: ~['\\\r\n];

StringLiteral: '"' StringCharacters? '"';

fragment StringCharacters: StringCharacter+;

fragment StringCharacter: ~["\\\r\n] | EscapeSequence;

TextBlock: '"""' [ \t]* [\n\r] [.\r\b]* '"""';

fragment EscapeSequence:
    '\\' [btnfr"'\\]
    | OctalEscape
    | UnicodeEscape
;

fragment OctalEscape:
    '\\' OctalDigit
    | '\\' OctalDigit OctalDigit
    | '\\' ZeroToThree OctalDigit OctalDigit
;

fragment ZeroToThree: [0-3];

fragment UnicodeEscape: '\\' 'u'+ HexDigit HexDigit HexDigit HexDigit;

NullLiteral: 'null';

Id: [a-zA-Z_][a-zA-Z0-9_]*;

WS: [ \t\r\n\u000C]+ -> skip;
COMMENT: '/*' .*? '*/' -> channel(HIDDEN);
LINE_COMMENT: '//' ~[\r\n]* -> channel(HIDDEN);