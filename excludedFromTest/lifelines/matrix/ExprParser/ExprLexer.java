// $ANTLR 3.2 Sep 23, 2009 12:02:23 Expr.g 2010-09-21 14:25:38

package lifelines.matrix.ExprParser;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class ExprLexer extends Lexer {
    public static final int SIGN=8;
    public static final int WS=10;
    public static final int T__16=16;
    public static final int T__15=15;
    public static final int T__18=18;
    public static final int NEWLINE=9;
    public static final int T__17=17;
    public static final int T__12=12;
    public static final int T__11=11;
    public static final int T__14=14;
    public static final int T__13=13;
    public static final int OPERATOR=4;
    public static final int LITERAL=7;
    public static final int INT=5;
    public static final int FLOAT=6;
    public static final int EOF=-1;

    // delegates
    // delegators

    public ExprLexer() {;} 
    public ExprLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public ExprLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "Expr.g"; }

    // $ANTLR start "T__11"
    public final void mT__11() throws RecognitionException {
        try {
            int _type = T__11;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Expr.g:7:7: ( ';' )
            // Expr.g:7:9: ';'
            {
            match(';'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__11"

    // $ANTLR start "T__12"
    public final void mT__12() throws RecognitionException {
        try {
            int _type = T__12;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Expr.g:8:7: ( '(' )
            // Expr.g:8:9: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__12"

    // $ANTLR start "T__13"
    public final void mT__13() throws RecognitionException {
        try {
            int _type = T__13;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Expr.g:9:7: ( ')' )
            // Expr.g:9:9: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__13"

    // $ANTLR start "T__14"
    public final void mT__14() throws RecognitionException {
        try {
            int _type = T__14;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Expr.g:10:7: ( 'NOT' )
            // Expr.g:10:9: 'NOT'
            {
            match("NOT"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__14"

    // $ANTLR start "T__15"
    public final void mT__15() throws RecognitionException {
        try {
            int _type = T__15;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Expr.g:11:7: ( 'AND' )
            // Expr.g:11:9: 'AND'
            {
            match("AND"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__15"

    // $ANTLR start "T__16"
    public final void mT__16() throws RecognitionException {
        try {
            int _type = T__16;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Expr.g:12:7: ( 'OR' )
            // Expr.g:12:9: 'OR'
            {
            match("OR"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__16"

    // $ANTLR start "T__17"
    public final void mT__17() throws RecognitionException {
        try {
            int _type = T__17;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Expr.g:13:7: ( ',' )
            // Expr.g:13:9: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__17"

    // $ANTLR start "T__18"
    public final void mT__18() throws RecognitionException {
        try {
            int _type = T__18;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Expr.g:14:7: ( 'IN' )
            // Expr.g:14:9: 'IN'
            {
            match("IN"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__18"

    // $ANTLR start "LITERAL"
    public final void mLITERAL() throws RecognitionException {
        try {
            int _type = LITERAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Expr.g:37:9: ( '\"' ( . )+ '\"' )
            // Expr.g:37:11: '\"' ( . )+ '\"'
            {
            match('\"'); 
            // Expr.g:37:14: ( . )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0=='\"') ) {
                    alt1=2;
                }
                else if ( ((LA1_0>='\u0000' && LA1_0<='!')||(LA1_0>='#' && LA1_0<='\uFFFF')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // Expr.g:37:14: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    if ( cnt1 >= 1 ) break loop1;
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        throw eee;
                }
                cnt1++;
            } while (true);

            match('\"'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LITERAL"

    // $ANTLR start "INT"
    public final void mINT() throws RecognitionException {
        try {
            int _type = INT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Expr.g:38:6: ( ( '0' .. '9' )+ )
            // Expr.g:38:8: ( '0' .. '9' )+
            {
            // Expr.g:38:8: ( '0' .. '9' )+
            int cnt2=0;
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>='0' && LA2_0<='9')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // Expr.g:38:8: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    if ( cnt2 >= 1 ) break loop2;
                        EarlyExitException eee =
                            new EarlyExitException(2, input);
                        throw eee;
                }
                cnt2++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "INT"

    // $ANTLR start "FLOAT"
    public final void mFLOAT() throws RecognitionException {
        try {
            int _type = FLOAT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Expr.g:41:8: ( ( SIGN )? ( '.' ( INT )+ | ( INT )+ '.' ( INT )+ | ( INT )+ '.' ) )
            // Expr.g:41:10: ( SIGN )? ( '.' ( INT )+ | ( INT )+ '.' ( INT )+ | ( INT )+ '.' )
            {
            // Expr.g:41:10: ( SIGN )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0=='+'||LA3_0=='-') ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // Expr.g:41:10: SIGN
                    {
                    mSIGN(); 

                    }
                    break;

            }

            // Expr.g:41:16: ( '.' ( INT )+ | ( INT )+ '.' ( INT )+ | ( INT )+ '.' )
            int alt8=3;
            alt8 = dfa8.predict(input);
            switch (alt8) {
                case 1 :
                    // Expr.g:41:18: '.' ( INT )+
                    {
                    match('.'); 
                    // Expr.g:41:21: ( INT )+
                    int cnt4=0;
                    loop4:
                    do {
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( ((LA4_0>='0' && LA4_0<='9')) ) {
                            alt4=1;
                        }


                        switch (alt4) {
                    	case 1 :
                    	    // Expr.g:41:21: INT
                    	    {
                    	    mINT(); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt4 >= 1 ) break loop4;
                                EarlyExitException eee =
                                    new EarlyExitException(4, input);
                                throw eee;
                        }
                        cnt4++;
                    } while (true);


                    }
                    break;
                case 2 :
                    // Expr.g:41:28: ( INT )+ '.' ( INT )+
                    {
                    // Expr.g:41:28: ( INT )+
                    int cnt5=0;
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( ((LA5_0>='0' && LA5_0<='9')) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // Expr.g:41:28: INT
                    	    {
                    	    mINT(); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt5 >= 1 ) break loop5;
                                EarlyExitException eee =
                                    new EarlyExitException(5, input);
                                throw eee;
                        }
                        cnt5++;
                    } while (true);

                    match('.'); 
                    // Expr.g:41:35: ( INT )+
                    int cnt6=0;
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( ((LA6_0>='0' && LA6_0<='9')) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // Expr.g:41:35: INT
                    	    {
                    	    mINT(); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt6 >= 1 ) break loop6;
                                EarlyExitException eee =
                                    new EarlyExitException(6, input);
                                throw eee;
                        }
                        cnt6++;
                    } while (true);


                    }
                    break;
                case 3 :
                    // Expr.g:41:42: ( INT )+ '.'
                    {
                    // Expr.g:41:42: ( INT )+
                    int cnt7=0;
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( ((LA7_0>='0' && LA7_0<='9')) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // Expr.g:41:42: INT
                    	    {
                    	    mINT(); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt7 >= 1 ) break loop7;
                                EarlyExitException eee =
                                    new EarlyExitException(7, input);
                                throw eee;
                        }
                        cnt7++;
                    } while (true);

                    match('.'); 

                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FLOAT"

    // $ANTLR start "NEWLINE"
    public final void mNEWLINE() throws RecognitionException {
        try {
            int _type = NEWLINE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Expr.g:42:8: ( ( '\\r' )? '\\n' )
            // Expr.g:42:9: ( '\\r' )? '\\n'
            {
            // Expr.g:42:9: ( '\\r' )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0=='\r') ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // Expr.g:42:9: '\\r'
                    {
                    match('\r'); 

                    }
                    break;

            }

            match('\n'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NEWLINE"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Expr.g:43:5: ( ( ' ' | '\\t' )+ )
            // Expr.g:43:9: ( ' ' | '\\t' )+
            {
            // Expr.g:43:9: ( ' ' | '\\t' )+
            int cnt10=0;
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0=='\t'||LA10_0==' ') ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // Expr.g:
            	    {
            	    if ( input.LA(1)=='\t'||input.LA(1)==' ' ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    if ( cnt10 >= 1 ) break loop10;
                        EarlyExitException eee =
                            new EarlyExitException(10, input);
                        throw eee;
                }
                cnt10++;
            } while (true);

            skip();

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WS"

    // $ANTLR start "OPERATOR"
    public final void mOPERATOR() throws RecognitionException {
        try {
            int _type = OPERATOR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Expr.g:44:10: ( ( '<>' | '>=' | '<=' | '=' | '<' | '>' ) )
            // Expr.g:44:12: ( '<>' | '>=' | '<=' | '=' | '<' | '>' )
            {
            // Expr.g:44:12: ( '<>' | '>=' | '<=' | '=' | '<' | '>' )
            int alt11=6;
            switch ( input.LA(1) ) {
            case '<':
                {
                switch ( input.LA(2) ) {
                case '>':
                    {
                    alt11=1;
                    }
                    break;
                case '=':
                    {
                    alt11=3;
                    }
                    break;
                default:
                    alt11=5;}

                }
                break;
            case '>':
                {
                int LA11_2 = input.LA(2);

                if ( (LA11_2=='=') ) {
                    alt11=2;
                }
                else {
                    alt11=6;}
                }
                break;
            case '=':
                {
                alt11=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }

            switch (alt11) {
                case 1 :
                    // Expr.g:44:13: '<>'
                    {
                    match("<>"); 


                    }
                    break;
                case 2 :
                    // Expr.g:44:18: '>='
                    {
                    match(">="); 


                    }
                    break;
                case 3 :
                    // Expr.g:44:23: '<='
                    {
                    match("<="); 


                    }
                    break;
                case 4 :
                    // Expr.g:44:28: '='
                    {
                    match('='); 

                    }
                    break;
                case 5 :
                    // Expr.g:44:32: '<'
                    {
                    match('<'); 

                    }
                    break;
                case 6 :
                    // Expr.g:44:36: '>'
                    {
                    match('>'); 

                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OPERATOR"

    // $ANTLR start "SIGN"
    public final void mSIGN() throws RecognitionException {
        try {
            int _type = SIGN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Expr.g:45:7: ( ( '+' | '-' ) )
            // Expr.g:45:9: ( '+' | '-' )
            {
            if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SIGN"

    public void mTokens() throws RecognitionException {
        // Expr.g:1:8: ( T__11 | T__12 | T__13 | T__14 | T__15 | T__16 | T__17 | T__18 | LITERAL | INT | FLOAT | NEWLINE | WS | OPERATOR | SIGN )
        int alt12=15;
        alt12 = dfa12.predict(input);
        switch (alt12) {
            case 1 :
                // Expr.g:1:10: T__11
                {
                mT__11(); 

                }
                break;
            case 2 :
                // Expr.g:1:16: T__12
                {
                mT__12(); 

                }
                break;
            case 3 :
                // Expr.g:1:22: T__13
                {
                mT__13(); 

                }
                break;
            case 4 :
                // Expr.g:1:28: T__14
                {
                mT__14(); 

                }
                break;
            case 5 :
                // Expr.g:1:34: T__15
                {
                mT__15(); 

                }
                break;
            case 6 :
                // Expr.g:1:40: T__16
                {
                mT__16(); 

                }
                break;
            case 7 :
                // Expr.g:1:46: T__17
                {
                mT__17(); 

                }
                break;
            case 8 :
                // Expr.g:1:52: T__18
                {
                mT__18(); 

                }
                break;
            case 9 :
                // Expr.g:1:58: LITERAL
                {
                mLITERAL(); 

                }
                break;
            case 10 :
                // Expr.g:1:66: INT
                {
                mINT(); 

                }
                break;
            case 11 :
                // Expr.g:1:70: FLOAT
                {
                mFLOAT(); 

                }
                break;
            case 12 :
                // Expr.g:1:76: NEWLINE
                {
                mNEWLINE(); 

                }
                break;
            case 13 :
                // Expr.g:1:84: WS
                {
                mWS(); 

                }
                break;
            case 14 :
                // Expr.g:1:87: OPERATOR
                {
                mOPERATOR(); 

                }
                break;
            case 15 :
                // Expr.g:1:96: SIGN
                {
                mSIGN(); 

                }
                break;

        }

    }


    protected DFA8 dfa8 = new DFA8(this);
    protected DFA12 dfa12 = new DFA12(this);
    static final String DFA8_eotS =
        "\3\uffff\1\5\2\uffff";
    static final String DFA8_eofS =
        "\6\uffff";
    static final String DFA8_minS =
        "\1\56\1\uffff\1\56\1\60\2\uffff";
    static final String DFA8_maxS =
        "\1\71\1\uffff\2\71\2\uffff";
    static final String DFA8_acceptS =
        "\1\uffff\1\1\2\uffff\1\2\1\3";
    static final String DFA8_specialS =
        "\6\uffff}>";
    static final String[] DFA8_transitionS = {
            "\1\1\1\uffff\12\2",
            "",
            "\1\3\1\uffff\12\2",
            "\12\4",
            "",
            ""
    };

    static final short[] DFA8_eot = DFA.unpackEncodedString(DFA8_eotS);
    static final short[] DFA8_eof = DFA.unpackEncodedString(DFA8_eofS);
    static final char[] DFA8_min = DFA.unpackEncodedStringToUnsignedChars(DFA8_minS);
    static final char[] DFA8_max = DFA.unpackEncodedStringToUnsignedChars(DFA8_maxS);
    static final short[] DFA8_accept = DFA.unpackEncodedString(DFA8_acceptS);
    static final short[] DFA8_special = DFA.unpackEncodedString(DFA8_specialS);
    static final short[][] DFA8_transition;

    static {
        int numStates = DFA8_transitionS.length;
        DFA8_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA8_transition[i] = DFA.unpackEncodedString(DFA8_transitionS[i]);
        }
    }

    class DFA8 extends DFA {

        public DFA8(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 8;
            this.eot = DFA8_eot;
            this.eof = DFA8_eof;
            this.min = DFA8_min;
            this.max = DFA8_max;
            this.accept = DFA8_accept;
            this.special = DFA8_special;
            this.transition = DFA8_transition;
        }
        public String getDescription() {
            return "41:16: ( '.' ( INT )+ | ( INT )+ '.' ( INT )+ | ( INT )+ '.' )";
        }
    }
    static final String DFA12_eotS =
        "\12\uffff\1\20\1\21\6\uffff";
    static final String DFA12_eofS =
        "\22\uffff";
    static final String DFA12_minS =
        "\1\11\11\uffff\2\56\6\uffff";
    static final String DFA12_maxS =
        "\1\117\11\uffff\2\71\6\uffff";
    static final String DFA12_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\2\uffff\1\13\1\14"+
        "\1\15\1\16\1\12\1\17";
    static final String DFA12_specialS =
        "\22\uffff}>";
    static final String[] DFA12_transitionS = {
            "\1\16\1\15\2\uffff\1\15\22\uffff\1\16\1\uffff\1\11\5\uffff\1"+
            "\2\1\3\1\uffff\1\13\1\7\1\13\1\14\1\uffff\12\12\1\uffff\1\1"+
            "\3\17\2\uffff\1\5\7\uffff\1\10\4\uffff\1\4\1\6",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\14\1\uffff\12\12",
            "\1\14\1\uffff\12\14",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA12_eot = DFA.unpackEncodedString(DFA12_eotS);
    static final short[] DFA12_eof = DFA.unpackEncodedString(DFA12_eofS);
    static final char[] DFA12_min = DFA.unpackEncodedStringToUnsignedChars(DFA12_minS);
    static final char[] DFA12_max = DFA.unpackEncodedStringToUnsignedChars(DFA12_maxS);
    static final short[] DFA12_accept = DFA.unpackEncodedString(DFA12_acceptS);
    static final short[] DFA12_special = DFA.unpackEncodedString(DFA12_specialS);
    static final short[][] DFA12_transition;

    static {
        int numStates = DFA12_transitionS.length;
        DFA12_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA12_transition[i] = DFA.unpackEncodedString(DFA12_transitionS[i]);
        }
    }

    class DFA12 extends DFA {

        public DFA12(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 12;
            this.eot = DFA12_eot;
            this.eof = DFA12_eof;
            this.min = DFA12_min;
            this.max = DFA12_max;
            this.accept = DFA12_accept;
            this.special = DFA12_special;
            this.transition = DFA12_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T__11 | T__12 | T__13 | T__14 | T__15 | T__16 | T__17 | T__18 | LITERAL | INT | FLOAT | NEWLINE | WS | OPERATOR | SIGN );";
        }
    }
 

}