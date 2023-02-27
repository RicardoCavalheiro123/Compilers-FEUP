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
BOOL: 'true' | 'false';

WS : [ \t\n\r\f]+ -> skip ;

program
    : (importDeclaration)* classDeclaration EOF
    ;

importDeclaration
    : 'import' ID ('.'ID)* ';';

classDeclaration
    : 'class' ID ('extends' ID)? '{' (varDeclaration)* (methodDeclaration)* '}'
    ;

varDeclaration
    : type var=ID ';'
    ;

methodDeclaration
    : ('public')? type ID '(' (type ID (',' type ID)*)? ')' '{' (varDeclaration)* (statement)* 'return' expression ';' '}'
    | ('public')? 'static' 'void' 'main' '(' 'String' '['']' ID ')' '{' (varDeclaration)* (statement)* '}'
    ;

type
    : 'int' '['']'
    | 'boolean'
    | 'int'
    | 'String'
    | ID
    ;

statement
    : '{' (statement)* '}'
    | 'if' '(' expression ')' statement 'else' statement
    | 'while' '(' expression ')' statement
    | expression ';'
    | var=ID '=' expression ';'
    | var=ID '[' expression ']' '=' expression ';'
    ;

expression
    : PAR_OPEN expression PAR_CLOSE #Parenthesis
    | expression op=MULTDIV expression #BinaryOp
    | expression op=PLUSMINUS expression #BinaryOp
    | expression op=(LOGICAL_OP | COMP_OP) expression #BinaryOp
    | value=INTEGER #Integer
    | value=ID #Identifier
    | expression '[' expression ']' #ArrayAccess
    | expression '.' 'length' #ArrayLength
    | expression '.' ID '(' ( expression (',' expression)* )? ')' #MethodCall
    | 'new' 'int' '[' expression ']' #NewIntArray
    | 'new' ID '(' ')' #NewObject
    | '!'expression #UnaryOp
    | BOOL #Boolean
    | 'this' #This
    ;