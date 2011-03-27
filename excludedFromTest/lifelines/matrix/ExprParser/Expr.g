grammar Expr;

@header {
package matrix.ExprParser;
}

@lexer::header {
package matrix.ExprParser;
}

@members {

}

//prog:   stat+ ;
prog returns [String value]
	: expr ';' {$value = $expr.value;};

expr returns [String value]
	: '(' e1 = expr ')' c1 = cExpr? {$value = '(' + $e1.value +')' + $c1.value;}
	| OPERATOR? t = value c1 = cExpr {$value = "?column?" + ($OPERATOR.text != null ? $OPERATOR.text : "=" ) + $t.text + $c1.value;}
	| OPERATOR? t = value {$value = "?column?" + ($OPERATOR.text != null ? $OPERATOR.text : "=" ) + $t.text;}
	| 'NOT' e1 = expr {$value = "NOT" +" (" +$e1.value + ") ";}
	| i = in {$value = $i.value;};
cExpr returns [String value]
	: 'AND' e = expr {$value = " AND " + $e.value;}
	| 'OR' e = expr {$value = " OR " + $e.value;};

list returns [String value]
	: t = value {$value = $t.text;}
	| t = value ',' l=list {$value = $t.text +", " +$l.value ; };

in returns [String value]
	: 'IN' '(' l1 = list ')' (e1 = cExpr)? {$value = "?column? IN (" +$l1.value + ") " + (($e1.value != null) ? $e1.value : "");};

value	: INT|FLOAT|LITERAL;
LITERAL : '"'.+'"';
INT 	: '0'..'9'+ ;

//.2323 or 123. or 23.233
FLOAT 	: SIGN? ( '.'INT+ | INT+'.'INT+ | INT+'.');
NEWLINE:'\r'? '\n' ;
WS  :   (' '|'\t')+ {skip();};
OPERATOR : ('<>'|'>='|'<='|'='|'<'|'>');
SIGN 	: ('+'|'-');