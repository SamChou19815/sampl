grammar PL;

import PLLexerPart;

compilationUnit : importDeclaration? moduleMembersDeclaration EOF;

importDeclaration : IMPORT LBRACE UpperIdentifier (COMMA UpperIdentifier)* RBRACE;

moduleDeclaration : MODULE UpperIdentifier LBRACE moduleMembersDeclaration RBRACE;

moduleMembersDeclaration :
    moduleTypeDeclaration* // first type definitions
    moduleConstantDeclaration* // then constant definitions
    moduleFunctionDeclaration* // then function definitions
    moduleDeclaration* // finally nested module definitions
    ;

moduleTypeDeclaration :
    PRIVATE? TYPE
        UpperIdentifier genericsDeclaration?
    ASSIGN typeExprInDeclaration;

moduleConstantDeclaration: PRIVATE? LET LowerIdentifier ASSIGN expression;

moduleFunctionDeclaration :
    PRIVATE? LET genericsDeclaration? LowerIdentifier
        argumentDeclaration+ typeAnnotation
    ASSIGN expression;

typeExprInAnnotation
    : LPAREN typeExprInAnnotation RPAREN
      # NestedTypeInAnnotation
    | (UpperIdentifier DOT)* UpperIdentifier genericsSpecialization?
      # SingleIdentifierTypeInAnnotation
    | <assoc=right> typeExprInAnnotation ARROW typeExprInAnnotation
      # FunctionTypeInAnnotation
    ;

typeExprInDeclaration : variantTypeInDeclaration | structTypeInDeclaration;
variantTypeInDeclaration : LOR? variantConstructorDeclaration (LOR variantConstructorDeclaration)*;
structTypeInDeclaration : LBRACE annotatedVariable (SEMICOLON annotatedVariable)* SEMICOLON? RBRACE;

// Some parser type fragment
genericsSpecialization : LT typeExprInAnnotation (COMMA typeExprInAnnotation)* GT;
variantConstructorDeclaration : UpperIdentifier (OF typeExprInAnnotation)?;
typeAnnotation : COLON typeExprInAnnotation;
annotatedVariable : LowerIdentifier typeAnnotation;
argumentDeclaration : LPAREN annotatedVariable RPAREN;
patternToExpr : LOR pattern ARROW expression;
genericsDeclaration : LT UpperIdentifier (COMMA UpperIdentifier)* GT;

expression
    : LPAREN expression RPAREN # NestedExpr
    | Literal # LiteralExpr
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
    | FUNCTION argumentDeclaration+ ARROW expression # FunExpr
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

