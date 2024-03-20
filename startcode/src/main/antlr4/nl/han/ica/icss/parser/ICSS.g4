grammar ICSS;

//--- LEXER: ---

// IF support:
IF: 'if';
ELSE: 'else';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';


//Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;


//Color value takes precedence over id idents
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];

//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

//
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';




//--- PARSER: ---
stylesheet: variableAssignment* stylerule* EOF;

selector: ID_IDENT #idSelector | CLASS_IDENT #classSelector | (LOWER_IDENT | CAPITAL_IDENT) #tagSelector;

stylerule: selector OPEN_BRACE declaration* CLOSE_BRACE;

declaration: LOWER_IDENT COLON (literal | variableReference) SEMICOLON;

literal: COLOR #colorLiteral | PIXELSIZE #pixelLiteral | SCALAR #scalarLiteral | PERCENTAGE #percentageLiteral | TRUE #trueLiteral | FALSE #falseLiteral;

variableReference: CAPITAL_IDENT;

variableAssignment: variableReference ASSIGNMENT_OPERATOR (literal | variableReference) SEMICOLON;


