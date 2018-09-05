grammar PL;

import PLLexerPart;

program : classMemberDeclaration* EOF;

classDeclaration :
    PRIVATE? CLASS UpperIdentifier genericsDeclaration?
    (LPAREN typeExprInDeclaration RPAREN)?
    (LBRACE classMemberDeclaration* RBRACE)?;

classMemberDeclaration
    : classConstantDeclaration
    | classFunctionGroupDeclaration
    | classDeclaration
    ;

classConstantDeclaration: PRIVATE? VAL LowerIdentifier ASSIGN expression;

classFunctionGroupDeclaration : classFunctionDeclaration+;

classFunctionDeclaration :
    PRIVATE? FUN genericsDeclaration? LowerIdentifier
        argumentDeclarations typeAnnotation
    ASSIGN expression;

typeExprInAnnotation
    : (UpperIdentifier DOT)* UpperIdentifier genericsSpecialization?
      # SingleIdentifierTypeInAnnotation
    | LPAREN typeExprInAnnotation (COMMA typeExprInAnnotation)* RPAREN ARROW typeExprInAnnotation
      # FunctionTypeInAnnotation
    ;

typeExprInDeclaration : variantTypeInDeclaration | structTypeInDeclaration;
variantTypeInDeclaration : LOR? variantConstructorDeclaration (LOR variantConstructorDeclaration)*;
structTypeInDeclaration : annotatedVariable (COMMA annotatedVariable)* COMMA?;

// Some parser type fragment
genericsSpecialization : LT typeExprInAnnotation (COMMA typeExprInAnnotation)* GT;
variantConstructorDeclaration : UpperIdentifier (OF typeExprInAnnotation)?;
typeAnnotation : COLON typeExprInAnnotation;
annotatedVariable : LowerIdentifier typeAnnotation;
argumentDeclarations : UNIT | (LPAREN annotatedVariable (COMMA annotatedVariable)* RPAREN);
patternToExpr : LOR pattern ARROW expression;
genericsDeclaration : LT UpperIdentifier (COMMA UpperIdentifier)* GT;

expression
    : LPAREN expression RPAREN # NestedExpr
    | literal # LiteralExpr
    | (UpperIdentifier DOT)* LowerIdentifier genericsSpecialization? # IdentifierExpr
    | constructor # ConstructorExpr
    | expression DOT LowerIdentifier # StructMemberAccessExpr
    | NOT expression # NotExpr
    | expression (UNIT | (LPAREN expression (COMMA expression)* RPAREN)) # FunctionApplicationExpr
    | expression bitOperator expression # BitExpr
    | expression factorOperator expression # FactorExpr
    | expression termOperator expression # TermExpr
    | expression comparisonOperator expression # ComparisonExpr
    | expression AND expression # ConjunctionExpr
    | expression OR expression # DisjunctionExpr
    | THROW LT typeExprInAnnotation GT expression # ThrowExpr
    | IF expression THEN expression ELSE expression # IfElseExpr
    | MATCH expression WITH patternToExpr+ # MatchExpr
    | LBRACE argumentDeclarations ARROW expression RBRACE # FunExpr
    | TRY expression CATCH LowerIdentifier expression # TryCatchExpr
    | VAL (LowerIdentifier | WILDCARD) ASSIGN expression SEMICOLON expression # LetExpr
    ;

constructor
    : (UpperIdentifier DOT)+ UpperIdentifier genericsSpecialization?
      # NoArgVariantConstructor
    | (UpperIdentifier DOT)+ UpperIdentifier WITH LPAREN expression RPAREN
      # OneArgVariantConstructor
    | (UpperIdentifier DOT)* UpperIdentifier LBRACE
          structConstructorValueDeclaration (SEMICOLON structConstructorValueDeclaration)*
          SEMICOLON?
      RBRACE
      # StructConstructor
    | LBRACE
          expression WITH
          structConstructorValueDeclaration (SEMICOLON structConstructorValueDeclaration)*
          SEMICOLON?
      RBRACE
      # StructWithConstructor
    ;

structConstructorValueDeclaration : LowerIdentifier ASSIGN expression;

pattern
    : UpperIdentifier (LowerIdentifier | WILDCARD)? # VariantPattern
    | LowerIdentifier # VariablePattern
    | WILDCARD # WildcardPattern
    ;

// Operator collections

bitOperator : SHL | SHR | USHR | XOR | LAND | LOR;

factorOperator : MUL | DIV | MOD | F_MUL | F_DIV;

termOperator : PLUS | MINUS | F_PLUS | F_MINUS | STR_CONCAT;

comparisonOperator : LT | LE | GT | GE | STRUCT_EQ | STRUCT_NE;

// Literal collections

literal
    : UNIT
    | IntegerLiteral
    | FloatingPointLiteral
    | CharacterLiteral
    | StringLiteral
    | BooleanLiteral
    ;

