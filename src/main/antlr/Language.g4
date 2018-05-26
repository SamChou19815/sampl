grammar Language;

import PLLexer;

compilationUnit : importDeclaration? moduleMemberDeclaration* EOF;

importDeclaration : IMPORT LBRACE UpperIdentifier (COMMA UpperIdentifier)* RBRACE;

moduleDeclaration : MODULE UpperIdentifier LBRACE moduleMemberDeclaration* RBRACE;

moduleMemberDeclaration
    : PRIVATE? moduleValueMemberConcreteDeclaration # ModuleValueMemberDeclaration
    | moduleDeclaration # NestedModuleDeclaration
    ;

moduleValueMemberConcreteDeclaration
    : TYPE typeIdentifier ASSIGN typeExprInDeclaration
      # ModuleTypeDeclaration
    | LET LowerIdentifier ASSIGN expression
      # ModuleConstantDeclaration
    | LET LowerIdentifier genericsDeclaration? argumentDeclaration* typeAnnotation ASSIGN expression
      # ModuleFunctionDeclaration
    ;

typeExprInDeclaration
    : (LOR UpperIdentifier (OF typeIdentifier)?)+ # VariantTypeInDeclaration
    | LBRACE annotatedVariable (SEMICOLON annotatedVariable)* RBRACE # StructClassType
    ;

typeExprInAnnotation
    : LPAREN typeExprInAnnotation RPAREN
      # NestedAnnotableType
    | typeIdentifier
      # SingleIdentifierAnnotableType
    | (LPAREN typeExprInAnnotation (COMMA typeExprInAnnotation) RPAREN)* ARROW typeExprInAnnotation
      # FunctionAnnotableType
    ;

// Some parser fragment
typeIdentifier : (UpperIdentifier DOT)* UpperIdentifier genericsDeclaration?;
annotatedVariable : LowerIdentifier typeAnnotation;
typeAnnotation : COLON typeExprInAnnotation;
argumentDeclaration : UNIT | LPAREN annotatedVariable RPAREN;
patternToExpr : LOR pattern ARROW expression;
genericsDeclaration : LBRACKET UpperIdentifier (COMMA UpperIdentifier)* RBRACKET;

expression
    : LPAREN expression RPAREN # NestedExpr
    | Literal # LiteralExpr
    | LowerIdentifier # IdentifierExpr
    | UpperIdentifier DOT LowerIdentifier # IdentifierInModuleExpr
    | expression (LPAREN expression+ RPAREN) # FunctionApplicationExpr
    | expression BitOperator expression # BitExpr
    | expression FactorOperator expression # FactorExpr
    | expression TermOperator expression # TermExpr
    | expression STR_CONCAT expression # StringConcatExpr
    | expression BinaryLogicalOperator expression # BooleanExpr
    | expression ComparisonOperator expression # ComparisonExpr
    | NOT expression # NotExpr
    | LET pattern typeAnnotation? ASSIGN expression SEMICOLON expression # LetExpr
    | FUNCTION genericsDeclaration? argumentDeclaration* ARROW expression # LambdaExpr
    | IF expression THEN expression ELSE expression # IfElseExpr
    | MATCH LowerIdentifier WITH patternToExpr+ # MatchExpr
    | THROW expression # ThrowExpr
    | TRY expression CATCH LowerIdentifier expression (FINALLY expression)? # TryCatchFinallyExpr
    ;

pattern
    : UNIT # UnitPattern
    | UpperIdentifier LowerIdentifier # VariantPattern
    | LowerIdentifier # VariablePattern
    | WILDCARD # WildcardPattern
    ;