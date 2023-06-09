grammar Javamm;

@header {
    package pt.up.fe.comp2023;
}

INTEGER : '0' | [1-9][0-9]* ;
ID : [$a-zA-Z_][$a-zA-Z_0-9]* ;

AND_OP : '&&' ;
OR_OP : '||' ;
COMP_OP : '==' | '!=' | '<' | '>' | '<=' | '>=';

MULTDIV : '*' | '/';
PLUSMINUS : '+' | '-';
PAR_OPEN : '(';
PAR_CLOSE : ')';


WS : [ \t\n\r\f]+ -> skip ;
COMMENT : '/*' (COMMENT|.)*? '*/' -> skip ;
LINE_COMMENT  : '//' .*? '\n' -> skip ;

program
    : (importDeclaration)* classDeclaration EOF
    ;

importDeclaration
    : 'import' library+=ID ('.' library+=ID)* ';'
    ;

classDeclaration
    : 'class' name=ID ('extends' extend=ID)? '{' (varDeclaration)* (methodDeclaration)* '}'
    ;

varDeclaration
    : type var=ID ('=' expression)? ';'
    ;

parameter
    : type var=ID
    ;

mainParam
    : typeName='String' '['']' var=ID
    ;

ret
    : 'return' expression ';'
    | 'return' ';'
    ;

methodDeclaration
    : ('public' | 'private' | 'protected' | 'default')? type name=ID '(' (parameter (',' parameter)*)? ')' '{' (varDeclaration)* (statement)* ret '}' #Method
    | ('public')? 'static' 'void' name='main' '(' mainParam ')' '{' (varDeclaration)* (statement)* '}' #MainMethod
    ;

type locals[boolean isArray=false]
    : typeName='int'('['']' {$isArray=true;})? #IntType
    | typeName='boolean' #BooleanType
    | typeName='String' ('['']' {$isArray=true;})? #StringType
    | typeName=ID #ObjectType
    ;

statement
    : '{' (statement)* '}' #Block
    | 'if' '(' expression ')' statement ('else' statement)? #IfElse
    | 'while' '(' expression ')' statement #While
    | expression ';' #Stmt
    | id=ID '=' expression ';' #Assign
    | id=ID '[' expression ']' '=' expression ';' #ArrayAssign
    ;

expression
    : value=('true' | 'false') #Boolean
    | value=INTEGER #Integer
    | id=ID #Identifier
    | 'this' #This
    | PAR_OPEN expression PAR_CLOSE #Parenthesis
    | expression '[' expression ']' #ArrayAccess
    | expression '.' method=ID '(' ( expression (',' expression)* )? ')' #MethodCall
    | expression '.' 'length' #ArrayLength
    | 'new' id=ID '(' ')' #NewObject
    | 'new' 'int' '[' size=expression ']' #NewIntArray
    | '!'expression #UnaryOp
    | expression op=MULTDIV expression #BinaryOp
    | expression op=PLUSMINUS expression #BinaryOp
    | expression op=COMP_OP expression #BinaryOp
    | expression op=AND_OP expression #BinaryOp
    | expression op=OR_OP expression #BinaryOp
    ;
