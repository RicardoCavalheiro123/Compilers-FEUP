grammar Javamm;

@header {
    package pt.up.fe.comp2023;
}

INTEGER : [0-9]+ ;
ID : [a-zA-Z_][a-zA-Z_0-9]* ;

LOGICAL_OP : '&&' | '||' ;
COMP_OP : '==' | '!=' | '<' | '>' | '<=' | '>=';

MULTDIV : '*' | '/';
PLUSMINUS : '+' | '-';
PAR_OPEN : '(';
PAR_CLOSE : ')';

WS : [ \t\n\r\f]+ -> skip ;

program
    : (importDeclaration)* classDeclaration EOF
    ;

importDeclaration
    : 'import' library=ID ('.'ID)* ';';

classDeclaration
    : 'class' name=ID ('extends' extend=ID)? '{' (varDeclaration)* (methodDeclaration)* '}'
    ;

varDeclaration
    : type var=ID ';'
    ;

methodDeclaration
    : ('public')? return_type=type name=ID '(' (type ID (',' type ID)*)? ')' '{' (varDeclaration)* (statement)* 'return' expression ';' '}'
    | ('public')? 'static' 'void' 'main' '(' 'String' '['']' ID ')' '{' (varDeclaration)* (statement)* '}'
    ;

type
    : id='int''['']'
    | id='boolean'
    | id='int'
    | id='String'
    | id=ID
    ;

statement
    : '{' (statement)* '}' #Block
    | 'if' '(' expression ')' statement 'else' statement #IfElse
    | 'while' '(' expression ')' statement #While
    | expression ';' #Stmt
    | id=ID '=' expression ';' #Assign
    | id=ID '[' expression ']' '=' expression ';' #ArrayAssign
    ;

expression
    : PAR_OPEN expression PAR_CLOSE #Parenthesis
    | value=('true' | 'false') #Boolean
    | '!'expression #UnaryOp
    | expression op=MULTDIV expression #BinaryOp
    | expression op=PLUSMINUS expression #BinaryOp
    | expression op=COMP_OP expression #BinaryOp
    | expression op=LOGICAL_OP expression #BinaryOp
    | value=INTEGER #Integer
    | id=ID #Identifier
    | expression '[' index=expression ']' #ArrayAccess
    | expression '.' 'length' #ArrayLength
    | expression '.' method=ID '(' ( expression (',' expression)* )? ')' #MethodCall
    | 'new' 'int' '[' size=expression ']' #NewIntArray
    | 'new' id=ID '(' ')' #NewObject
    | 'this' #This
    ;
