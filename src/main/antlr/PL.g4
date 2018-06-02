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
    PRIVATE? LET LowerIdentifier genericsDeclaration?
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

typeExprInDeclaration
    : (LOR variantConstructorDeclaration)+ # VariantTypeInDeclaration
    | LBRACE annotatedVariable (SEMICOLON annotatedVariable)* RBRACE # StructTypeInDeclaration
    ;

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
    | (UpperIdentifier DOT)* LowerIdentifier genericsSpecialization # IdentifierExpr
    | NOT expression # NotExpr
    | expression BitOperator expression # BitExpr
    | expression FactorOperator expression # FactorExpr
    | expression TermOperator expression # TermExpr
    | expression ComparisonOperator expression # ComparisonExpr
    | expression AND expression # ConjunctionExpr
    | expression OR expression # DisjunctionExpr
    | THROW LBRACKET typeExprInAnnotation RBRACKET expression # ThrowExpr
    | IF expression THEN expression ELSE expression # IfElseExpr
    | MATCH expression WITH patternToExpr+ # MatchExpr
    | LET LowerIdentifier ASSIGN expression SEMICOLON expression # LetExpr
    | FUNCTION argumentDeclaration+ typeAnnotation ARROW expression # FunExpr
    | expression LPAREN expression+ RPAREN # FunctionApplicationExpr
    | TRY expression CATCH LowerIdentifier expression # TryCatchExpr
    ;

pattern
    : UpperIdentifier LowerIdentifier # VariantPattern
    | LowerIdentifier # VariablePattern
    | WILDCARD # WildcardPattern
    ;
