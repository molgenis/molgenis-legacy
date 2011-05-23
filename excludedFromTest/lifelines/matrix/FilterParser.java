
package lifelines.matrix;

import java.util.logging.Level;
import java.util.logging.Logger;
import lifelines.matrix.ExprParser.ErrorList;
import lifelines.matrix.ExprParser.ExprLexer;
import lifelines.matrix.ExprParser.ExprParser;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;

/**
 *
 * @author jorislops
 */
public class FilterParser {
    public static String parseExpr(String filter) {
        if(!filter.endsWith(";"))
            filter += ";";

        ANTLRStringStream input = new ANTLRStringStream(filter);
        ExprLexer lexer = new ExprLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ExprParser parser = new ExprParser(tokens);
        ErrorList errorList = new ErrorList();
        lexer.setErrorReporter(errorList);
        parser.setErrorReporter(errorList);

        String result = null;
        try {
            result = parser.prog();
        } catch (org.antlr.runtime.RecognitionException ex) {
            Logger.getLogger(FilterParser.class.getName()).log(Level.SEVERE, null, ex);
        }

        if(errorList.hasErrorMsg()) {
            return null;
        }

        return result;
    }
}
