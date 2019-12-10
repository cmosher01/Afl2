parser grammar AflParser;

options{
    tokenVocab=AflLexer;
}

flowchart : step* EOF ;
step : symbol id=ID? s+=str* ( OUT o+=str* )? ( ato=TO to=ID? )? ;
str : ( TOKEN | ETN ) ;
symbol : trm | prc | io | dec | def | doc | sto | com | min | mop | dsp ;
trm : SYM_TRM ;
prc : SYM_PRC ;
io : SYM_IO ;
dec : SYM_DEC ;
def : SYM_DEF ;
doc : SYM_DOC ;
sto : SYM_STO ;
com : SYM_COM ;
min : SYM_MIN ;
mop : SYM_MOP ;
dsp : SYM_DSP ;
