lexer grammar AflLexer;

fragment W : [\p{White_Space}] ;
fragment C : ~[\p{White_Space}] ;

WHITE_SPACE : W+ -> skip ;
ETN : [\\] C* { setText(getText().substring(1)); } ;
ID : [_] C+ { setText(getText().substring(1)); } ;

SYM_TRM : [(] ;
SYM_PRC : [[] ;
SYM_IO : [/] ;
SYM_DEC : [<] ;
SYM_DEF : [|] ;
SYM_DOC : [~] ;
SYM_STO : [{] ;
SYM_COM : [*] ;
SYM_MIN : [$] ;
SYM_MOP : [`] ;
SYM_DSP : [@] ;

OUT : [-] ;
TO : [>] ;
TOKEN : C+ ;
