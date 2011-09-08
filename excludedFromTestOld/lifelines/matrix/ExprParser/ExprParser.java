// $ANTLR 3.2 Sep 23, 2009 12:02:23 Expr.g 2010-09-21 14:25:37

package lifelines.matrix.ExprParser;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class ExprParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "OPERATOR", "INT", "FLOAT", "LITERAL", "SIGN", "NEWLINE", "WS", "';'", "'('", "')'", "'NOT'", "'AND'", "'OR'", "','", "'IN'"
    };
    public static final int SIGN=8;
    public static final int WS=10;
    public static final int T__16=16;
    public static final int T__15=15;
    public static final int NEWLINE=9;
    public static final int T__18=18;
    public static final int T__17=17;
    public static final int T__12=12;
    public static final int T__11=11;
    public static final int T__14=14;
    public static final int OPERATOR=4;
    public static final int T__13=13;
    public static final int LITERAL=7;
    public static final int FLOAT=6;
    public static final int INT=5;
    public static final int EOF=-1;

    // delegates
    // delegators


        public ExprParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public ExprParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return ExprParser.tokenNames; }
    public String getGrammarFileName() { return "Expr.g"; }






    // $ANTLR start "prog"
    // Expr.g:16:1: prog returns [String value] : expr ';' ;
    public final String prog() throws RecognitionException {
        String value = null;

        String expr1 = null;


        try {
            // Expr.g:17:2: ( expr ';' )
            // Expr.g:17:4: expr ';'
            {
            pushFollow(FOLLOW_expr_in_prog37);
            expr1=expr();

            state._fsp--;

            match(input,11,FOLLOW_11_in_prog39); 
            value = expr1;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return value;
    }
    // $ANTLR end "prog"


    // $ANTLR start "expr"
    // Expr.g:19:1: expr returns [String value] : ( '(' e1= expr ')' (c1= cExpr )? | ( OPERATOR )? t= value c1= cExpr | ( OPERATOR )? t= value | 'NOT' e1= expr | i= in );
    public final String expr() throws RecognitionException {
        String value = null;

        Token OPERATOR2=null;
        Token OPERATOR3=null;
        String e1 = null;

        String c1 = null;

        ExprParser.value_return t = null;

        String i = null;


        try {
            // Expr.g:20:2: ( '(' e1= expr ')' (c1= cExpr )? | ( OPERATOR )? t= value c1= cExpr | ( OPERATOR )? t= value | 'NOT' e1= expr | i= in )
            int alt4=5;
            switch ( input.LA(1) ) {
            case 12:
                {
                alt4=1;
                }
                break;
            case OPERATOR:
                {
                int LA4_2 = input.LA(2);

                if ( ((LA4_2>=INT && LA4_2<=LITERAL)) ) {
                    int LA4_3 = input.LA(3);

                    if ( (LA4_3==11||LA4_3==13) ) {
                        alt4=3;
                    }
                    else if ( ((LA4_3>=15 && LA4_3<=16)) ) {
                        alt4=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 4, 3, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 2, input);

                    throw nvae;
                }
                }
                break;
            case INT:
            case FLOAT:
            case LITERAL:
                {
                int LA4_3 = input.LA(2);

                if ( (LA4_3==11||LA4_3==13) ) {
                    alt4=3;
                }
                else if ( ((LA4_3>=15 && LA4_3<=16)) ) {
                    alt4=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 3, input);

                    throw nvae;
                }
                }
                break;
            case 14:
                {
                alt4=4;
                }
                break;
            case 18:
                {
                alt4=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }

            switch (alt4) {
                case 1 :
                    // Expr.g:20:4: '(' e1= expr ')' (c1= cExpr )?
                    {
                    match(input,12,FOLLOW_12_in_expr54); 
                    pushFollow(FOLLOW_expr_in_expr60);
                    e1=expr();

                    state._fsp--;

                    match(input,13,FOLLOW_13_in_expr62); 
                    // Expr.g:20:25: (c1= cExpr )?
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( ((LA1_0>=15 && LA1_0<=16)) ) {
                        alt1=1;
                    }
                    switch (alt1) {
                        case 1 :
                            // Expr.g:20:25: c1= cExpr
                            {
                            pushFollow(FOLLOW_cExpr_in_expr68);
                            c1=cExpr();

                            state._fsp--;


                            }
                            break;

                    }

                    value = '(' + e1 +')' + c1;

                    }
                    break;
                case 2 :
                    // Expr.g:21:4: ( OPERATOR )? t= value c1= cExpr
                    {
                    // Expr.g:21:4: ( OPERATOR )?
                    int alt2=2;
                    int LA2_0 = input.LA(1);

                    if ( (LA2_0==OPERATOR) ) {
                        alt2=1;
                    }
                    switch (alt2) {
                        case 1 :
                            // Expr.g:21:4: OPERATOR
                            {
                            OPERATOR2=(Token)match(input,OPERATOR,FOLLOW_OPERATOR_in_expr76); 

                            }
                            break;

                    }

                    pushFollow(FOLLOW_value_in_expr83);
                    t=value();

                    state._fsp--;

                    pushFollow(FOLLOW_cExpr_in_expr89);
                    c1=cExpr();

                    state._fsp--;

                    value = "?column?" + ((OPERATOR2!=null?OPERATOR2.getText():null) != null ? (OPERATOR2!=null?OPERATOR2.getText():null) : "=" ) + (t!=null?input.toString(t.start,t.stop):null) + c1;

                    }
                    break;
                case 3 :
                    // Expr.g:22:4: ( OPERATOR )? t= value
                    {
                    // Expr.g:22:4: ( OPERATOR )?
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0==OPERATOR) ) {
                        alt3=1;
                    }
                    switch (alt3) {
                        case 1 :
                            // Expr.g:22:4: OPERATOR
                            {
                            OPERATOR3=(Token)match(input,OPERATOR,FOLLOW_OPERATOR_in_expr96); 

                            }
                            break;

                    }

                    pushFollow(FOLLOW_value_in_expr103);
                    t=value();

                    state._fsp--;

                    value = "?column?" + ((OPERATOR3!=null?OPERATOR3.getText():null) != null ? (OPERATOR3!=null?OPERATOR3.getText():null) : "=" ) + (t!=null?input.toString(t.start,t.stop):null);

                    }
                    break;
                case 4 :
                    // Expr.g:23:4: 'NOT' e1= expr
                    {
                    match(input,14,FOLLOW_14_in_expr110); 
                    pushFollow(FOLLOW_expr_in_expr116);
                    e1=expr();

                    state._fsp--;

                    value = "NOT" +" (" +e1 + ") ";

                    }
                    break;
                case 5 :
                    // Expr.g:24:4: i= in
                    {
                    pushFollow(FOLLOW_in_in_expr127);
                    i=in();

                    state._fsp--;

                    value = i;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return value;
    }
    // $ANTLR end "expr"


    // $ANTLR start "cExpr"
    // Expr.g:25:1: cExpr returns [String value] : ( 'AND' e= expr | 'OR' e= expr );
    public final String cExpr() throws RecognitionException {
        String value = null;

        String e = null;


        try {
            // Expr.g:26:2: ( 'AND' e= expr | 'OR' e= expr )
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==15) ) {
                alt5=1;
            }
            else if ( (LA5_0==16) ) {
                alt5=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // Expr.g:26:4: 'AND' e= expr
                    {
                    match(input,15,FOLLOW_15_in_cExpr141); 
                    pushFollow(FOLLOW_expr_in_cExpr147);
                    e=expr();

                    state._fsp--;

                    value = " AND " + e;

                    }
                    break;
                case 2 :
                    // Expr.g:27:4: 'OR' e= expr
                    {
                    match(input,16,FOLLOW_16_in_cExpr154); 
                    pushFollow(FOLLOW_expr_in_cExpr160);
                    e=expr();

                    state._fsp--;

                    value = " OR " + e;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return value;
    }
    // $ANTLR end "cExpr"


    // $ANTLR start "list"
    // Expr.g:29:1: list returns [String value] : (t= value | t= value ',' l= list );
    public final String list() throws RecognitionException {
        String value = null;

        ExprParser.value_return t = null;

        String l = null;


        try {
            // Expr.g:30:2: (t= value | t= value ',' l= list )
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( ((LA6_0>=INT && LA6_0<=LITERAL)) ) {
                int LA6_1 = input.LA(2);

                if ( (LA6_1==17) ) {
                    alt6=2;
                }
                else if ( (LA6_1==13) ) {
                    alt6=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // Expr.g:30:4: t= value
                    {
                    pushFollow(FOLLOW_value_in_list179);
                    t=value();

                    state._fsp--;

                    value = (t!=null?input.toString(t.start,t.stop):null);

                    }
                    break;
                case 2 :
                    // Expr.g:31:4: t= value ',' l= list
                    {
                    pushFollow(FOLLOW_value_in_list190);
                    t=value();

                    state._fsp--;

                    match(input,17,FOLLOW_17_in_list192); 
                    pushFollow(FOLLOW_list_in_list196);
                    l=list();

                    state._fsp--;

                    value = (t!=null?input.toString(t.start,t.stop):null) +", " +l ; 

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return value;
    }
    // $ANTLR end "list"


    // $ANTLR start "in"
    // Expr.g:33:1: in returns [String value] : 'IN' '(' l1= list ')' (e1= cExpr )? ;
    public final String in() throws RecognitionException {
        String value = null;

        String l1 = null;

        String e1 = null;


        try {
            // Expr.g:34:2: ( 'IN' '(' l1= list ')' (e1= cExpr )? )
            // Expr.g:34:4: 'IN' '(' l1= list ')' (e1= cExpr )?
            {
            match(input,18,FOLLOW_18_in_in211); 
            match(input,12,FOLLOW_12_in_in213); 
            pushFollow(FOLLOW_list_in_in219);
            l1=list();

            state._fsp--;

            match(input,13,FOLLOW_13_in_in221); 
            // Expr.g:34:27: (e1= cExpr )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( ((LA7_0>=15 && LA7_0<=16)) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // Expr.g:34:28: e1= cExpr
                    {
                    pushFollow(FOLLOW_cExpr_in_in228);
                    e1=cExpr();

                    state._fsp--;


                    }
                    break;

            }

            value = "?column? IN (" +l1 + ") " + ((e1 != null) ? e1 : "");

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return value;
    }
    // $ANTLR end "in"

    public static class value_return extends ParserRuleReturnScope {
    };

    // $ANTLR start "value"
    // Expr.g:36:1: value : ( INT | FLOAT | LITERAL );
    public final ExprParser.value_return value() throws RecognitionException {
        ExprParser.value_return retval = new ExprParser.value_return();
        retval.start = input.LT(1);

        try {
            // Expr.g:36:7: ( INT | FLOAT | LITERAL )
            // Expr.g:
            {
            if ( (input.LA(1)>=INT && input.LA(1)<=LITERAL) ) {
                input.consume();
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "value"

    // Delegated rules


 

    public static final BitSet FOLLOW_expr_in_prog37 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_11_in_prog39 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_12_in_expr54 = new BitSet(new long[]{0x00000000000450F0L});
    public static final BitSet FOLLOW_expr_in_expr60 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_13_in_expr62 = new BitSet(new long[]{0x0000000000018002L});
    public static final BitSet FOLLOW_cExpr_in_expr68 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPERATOR_in_expr76 = new BitSet(new long[]{0x00000000000000F0L});
    public static final BitSet FOLLOW_value_in_expr83 = new BitSet(new long[]{0x0000000000018000L});
    public static final BitSet FOLLOW_cExpr_in_expr89 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPERATOR_in_expr96 = new BitSet(new long[]{0x00000000000000F0L});
    public static final BitSet FOLLOW_value_in_expr103 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_14_in_expr110 = new BitSet(new long[]{0x00000000000450F0L});
    public static final BitSet FOLLOW_expr_in_expr116 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_in_in_expr127 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_15_in_cExpr141 = new BitSet(new long[]{0x00000000000450F0L});
    public static final BitSet FOLLOW_expr_in_cExpr147 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_16_in_cExpr154 = new BitSet(new long[]{0x00000000000450F0L});
    public static final BitSet FOLLOW_expr_in_cExpr160 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_value_in_list179 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_value_in_list190 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_17_in_list192 = new BitSet(new long[]{0x00000000000000F0L});
    public static final BitSet FOLLOW_list_in_list196 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_18_in_in211 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_12_in_in213 = new BitSet(new long[]{0x00000000000000F0L});
    public static final BitSet FOLLOW_list_in_in219 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_13_in_in221 = new BitSet(new long[]{0x0000000000018002L});
    public static final BitSet FOLLOW_cExpr_in_in228 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_value0 = new BitSet(new long[]{0x0000000000000002L});

}