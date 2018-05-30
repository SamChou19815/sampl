grammar PL;

import PLLexerPart;

compilationUnit : importDeclaration? moduleMembersDeclaration EOF;

importDeclaration : IMPORT LBRACE UpperIdentifier (COMMA UpperIdentifier)* RBRACE;

moduleDeclaration : MODULE UpperIdentifier LBRACE moduleMembersDeclaration RBRACE;

moduleMembersDeclaration:
    moduleTypeDeclaration* // first type definitions
    moduleConstantDeclaration* // then constant definitions
    moduleFunctionDeclaration* // then function definitions
    moduleDeclaration* // finally nested module definitions
    ;

moduleTypeDeclaration: TYPE typeIdentifier ASSIGN typeExprInDeclaration;

moduleConstantDeclaration: PRIVATE? LET LowerIdentifier ASSIGN expression;

moduleFunctionDeclaration:
    PRIVATE? LET LowerIdentifier genericsDeclaration?
        argumentDeclaration+ typeAnnotation
    ASSIGN expression;

typeExprInAnnotation
    : LPAREN typeExprInAnnotation RPAREN
      # NestedTypeInAnnotation
    | typeIdentifier
      # SingleIdentifierTypeInAnnotation
    | <assoc=right> typeExprInAnnotation ARROW typeExprInAnnotation
      # FunctionTypeInAnnotation
    ;

typeExprInDeclaration
    : (LOR variantConstructorDeclaration)+ # VariantTypeInDeclaration
    | LBRACE annotatedVariable (SEMICOLON annotatedVariable)* RBRACE # StructTypeInDeclaration
    ;

// Some parser type fragment
typeIdentifier : (UpperIdentifier DOT)* UpperIdentifier genericsBracket?;
genericsBracket : LBRACKET typeIdentifier (COMMA typeIdentifier)* RBRACKET;
variantConstructorDeclaration : UpperIdentifier (OF typeExprInAnnotation)?;
typeAnnotation : COLON typeExprInAnnotation;
annotatedVariable : LowerIdentifier typeAnnotation;
argumentDeclaration : LPAREN annotatedVariable RPAREN;
patternToExpr : LOR pattern ARROW expression;
genericsDeclaration : LBRACKET UpperIdentifier (COMMA UpperIdentifier)* RBRACKET;

expression
    : LPAREN expression RPAREN # NestedExpr
    | Literal # LiteralExpr
    | (UpperIdentifier DOT)* LowerIdentifier genericsBracket # IdentifierExpr
    | expression LPAREN expression+ RPAREN # FunctionApplicationExpr
    | expression BitOperator expression # BitExpr
    | expression FactorOperator expression # FactorExpr
    | expression TermOperator expression # TermExpr
    | expression STR_CONCAT expression # StringConcatExpr
    | expression BinaryLogicalOperator expression # BooleanExpr
    | expression ComparisonOperator expression # ComparisonExpr
    | NOT expression # NotExpr
    | LET LowerIdentifier ASSIGN expression SEMICOLON expression # LetExpr
    | FUNCTION argumentDeclaration+ typeAnnotation ARROW expression # FunExpr
    | IF expression THEN expression ELSE expression # IfElseExpr
    | MATCH LowerIdentifier WITH patternToExpr+ # MatchExpr
    | THROW LBRACKET typeExprInAnnotation RBRACKET expression # ThrowExpr
    | TRY expression CATCH LowerIdentifier expression # TryCatchExpr
    ;

pattern
    : UNIT # UnitPattern
    | UpperIdentifier LowerIdentifier # VariantPattern
    | LowerIdentifier # VariablePattern
    | WILDCARD # WildcardPattern
    ;
