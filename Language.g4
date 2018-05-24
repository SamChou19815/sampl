/**
 * Grammar for SAPL
 */
grammar Language;

compilationUnit : importDeclaration? classDeclaration;

importDeclaration : 'import' '{' Identifier (',' Identifier)* '}';

classDeclaration : privateModifier? 'class' Identifier '{'
    classTypeDeclaration
    classMemberDeclaration+
'}';

classMemberDeclaration : classConstantDeclaration | classMethodDeclaration;

classTypeDeclaration : 'type' Identifier '=' typeValue;

typeValue : tupleTypeValue | variantTypeValue | structTypeValue;

tupleTypeValue : typeIdentifier ('*' typeIdentifier)*;

variantTypeValue : ('|' Identifier ('of' typeValue))+;

structTypeValue : '{' annotatedVariable (';' annotatedVariable)* '}';

classConstantDeclaration : privateModifier? 'const' annotatedVariable '=' expression;

classMethodDeclaration : privateModifier? 'function' Identifier '('
    (annotatedVariable (',' annotatedVariable)*)?
')' ':' Identifier '{' expression '}';

expression
    : primaryExpression
    | expression bitOperator expression
    | expression factorOperator expression
    | expression termOperator expression
    | expression '^' expression
    | expression '&&' expression
    | expression '||' expression
    | 'let' annotatedPattern '=' expression ';' expression
    | Identifier '.' Identifier
    | Identifier '.' Identifier '(' (Identifier (',' Identifier)*)? ')'
    ; // TODO

primaryExpression
    : '(' expression ')'
    | 'this'
    | literal
    | Identifier
    ;

annotatedVariable : Identifier ':' typeIdentifier;

annotatedPattern : Identifier ':' typeIdentifier;

typeIdentifier : Identifier ('<' Identifier (',' Identifier)* '>')?;

privateModifier : 'private';

bitOperator : '<<' | '<<<' | '>>';

factorOperator : '*'|'/'|'*.'|'/.'|'%';

termOperator : '+'|'-'|'+.'|'-.';

// LITERALS

literal
    :   integerLiteral
    |   FloatingPointLiteral
    |   CharacterLiteral
    |   StringLiteral
    |   booleanLiteral
    ;

integerLiteral : HexLiteral | OctalLiteral | DecimalLiteral;

booleanLiteral : 'true' | 'false';

// LEXER

HexLiteral : '0' ('x'|'X') HexDigit+ IntegerTypeSuffix? ;

DecimalLiteral : ('0' | '1'..'9' '0'..'9'*) IntegerTypeSuffix? ;

OctalLiteral : '0' ('0'..'7')+ IntegerTypeSuffix? ;

fragment
HexDigit : ('0'..'9'|'a'..'f'|'A'..'F') ;

fragment
IntegerTypeSuffix : ('l'|'L') ;

FloatingPointLiteral
    :   ('0'..'9')+ '.' ('0'..'9')* Exponent? FloatTypeSuffix?
    |   '.' ('0'..'9')+ Exponent? FloatTypeSuffix?
    |   ('0'..'9')+ Exponent FloatTypeSuffix?
    |   ('0'..'9')+ FloatTypeSuffix
    |   ('0x' | '0X') (HexDigit )*
        ('.' (HexDigit)*)?
        ( 'p' | 'P' )
        ( '+' | '-' )?
        ( '0' .. '9' )+
        FloatTypeSuffix?
    ;

fragment
Exponent : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;

fragment
FloatTypeSuffix : ('f'|'F'|'d'|'D') ;

CharacterLiteral
    :   '\'' ( EscapeSequence | ~('\''|'\\') ) '\''
    ;

StringLiteral
    :  '"' ( EscapeSequence | ~('\\'|'"') )* '"'
    ;

fragment
EscapeSequence
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')
    |   UnicodeEscape
    |   OctalEscape
    ;

fragment
OctalEscape
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment
UnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;

Identifier
    :   Letter (Letter|JavaIDDigit)*
    ;

/**I found this char range in JavaCC's grammar, but Letter and Digit overlap.
   Still works, but...
 */
fragment
Letter
    :  '\u0024' |
       '\u0041'..'\u005a' |
       '\u005f' |
       '\u0061'..'\u007a' |
       '\u00c0'..'\u00d6' |
       '\u00d8'..'\u00f6' |
       '\u00f8'..'\u00ff' |
       '\u0100'..'\u1fff' |
       '\u3040'..'\u318f' |
       '\u3300'..'\u337f' |
       '\u3400'..'\u3d2d' |
       '\u4e00'..'\u9fff' |
       '\uf900'..'\ufaff'
    ;

fragment
JavaIDDigit
    :  '\u0030'..'\u0039' |
       '\u0660'..'\u0669' |
       '\u06f0'..'\u06f9' |
       '\u0966'..'\u096f' |
       '\u09e6'..'\u09ef' |
       '\u0a66'..'\u0a6f' |
       '\u0ae6'..'\u0aef' |
       '\u0b66'..'\u0b6f' |
       '\u0be7'..'\u0bef' |
       '\u0c66'..'\u0c6f' |
       '\u0ce6'..'\u0cef' |
       '\u0d66'..'\u0d6f' |
       '\u0e50'..'\u0e59' |
       '\u0ed0'..'\u0ed9' |
       '\u1040'..'\u1049'
   ;

COMMENT
    :   '/*' .*? '*/'    -> channel(HIDDEN) // match anything between /* and */
    ;
WS  :   [ \r\t\u000C\n]+ -> channel(HIDDEN)
    ;

LINE_COMMENT
    : '//' ~[\r\n]* '\r'? '\n' -> channel(HIDDEN)
    ;

