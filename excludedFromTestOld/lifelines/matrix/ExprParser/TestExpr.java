/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lifelines.matrix.ExprParser;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;

/**
 *
 * @author jorislops
 */
public class TestExpr {
    public static void main(String[] args) {
        try {
            ANTLRStringStream input = new ANTLRStringStream(">= 3 AND >= 10;");
            ExprLexer lexer = new ExprLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            ExprParser parser = new ExprParser(tokens);
            String condition = parser.prog();
            System.out.println(condition);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
