grammar PL;

import PLLexerPart;

compilationUnit : importDeclaration? classDeclaration EOF;

importDeclaration : IMPORT LBRACE UpperIdentifier (COMMA UpperIdentifier)* RBRACE;

classDeclaration :
    CLASS UpperIdentifier genericsDeclaration?
    (LPAREN typeExprInDeclaration RPAREN)?
    (LBRACE classMembersDeclaration RBRACE)?;

classMembersDeclaration :
    classConstantDeclaration* // first constant definitions
    classFunctionDeclaration* // then function definitions
    classDeclaration* // finally nested class definitions
    ;

classConstantDeclaration: PRIVATE? LET LowerIdentifier ASSIGN expression;

classFunctionDeclaration :
    PRIVATE? LET genericsDeclaration? LowerIdentifier
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
argumentDeclarations : argumentDeclaration* lastArgumentDeclaration;
argumentDeclaration : LPAREN annotatedVariable RPAREN;
lastArgumentDeclaration : UNIT | argumentDeclaration;
patternToExpr : LOR pattern ARROW expression;
genericsDeclaration : LT UpperIdentifier (COMMA UpperIdentifier)* GT;

expression
    : LPAREN expression RPAREN # NestedExpr
    | literal # LiteralExpr
    | (UpperIdentifier DOT)* LowerIdentifier genericsSpecialization? # IdentifierExpr
    | constructor # ConstructorExpr
    | expression DOT LowerIdentifier # StructMemberAccessExpr
    | NOT expression # NotExpr
    | expression bitOperator expression # BitExpr
    | expression factorOperator expression # FactorExpr
    | expression termOperator expression # TermExpr
    | expression comparisonOperator expression # ComparisonExpr
    | expression AND expression # ConjunctionExpr
    | expression OR expression # DisjunctionExpr
    | THROW LT typeExprInAnnotation GT expression # ThrowExpr
    | IF expression THEN expression ELSE expression # IfElseExpr
    | MATCH expression WITH patternToExpr+ # MatchExpr
    | expression LPAREN expression+ RPAREN # FunctionApplicationExpr
    | FUNCTION argumentDeclarations ARROW expression # FunExpr
    | TRY expression CATCH LowerIdentifier expression # TryCatchExpr
    | LET LowerIdentifier ASSIGN expression SEMICOLON expression # LetExpr
    ;

constructor
    : (UpperIdentifier DOT)+ UpperIdentifier genericsSpecialization? # NoArgVariantConstructor
    | (UpperIdentifier DOT)+ UpperIdentifier LPAREN expression RPAREN # OneArgVariantConstructor
    | (UpperIdentifier DOT)* UpperIdentifier LBRACE
          structConstructorValueDeclaration (SEMICOLON structConstructorValueDeclaration)*
          SEMICOLON?
      RBRACE # StructConstructor
    | LBRACE
          expression WITH
          structConstructorValueDeclaration (SEMICOLON structConstructorValueDeclaration)*
          SEMICOLON?
      RBRACE # StructWithConstructor
    ;

structConstructorValueDeclaration : LowerIdentifier ASSIGN expression;

pattern
    : UpperIdentifier LowerIdentifier # VariantPattern
    | LowerIdentifier # VariablePattern
    | WILDCARD # WildcardPattern
    ;

// Operator collections

bitOperator : SHL | SHR | USHR | XOR | LAND | LOR;

factorOperator : MUL | DIV | MOD | F_MUL | F_DIV;

termOperator : PLUS | MINUS | F_PLUS | F_MINUS | STR_CONCAT;

comparisonOperator : REF_EQ | STRUCT_EQ | LT | LE | GT | GE | REF_NE | STRUCT_NE;

// Literal collections

literal
    : UNIT
    | IntegerLiteral
    | FloatingPointLiteral
    | CharacterLiteral
    | StringLiteral
    | BooleanLiteral
    ;

