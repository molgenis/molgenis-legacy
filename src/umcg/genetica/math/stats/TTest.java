/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package umcg.genetica.math.stats;

/**
 *
 * @author harm-jan
 */
public class TTest {
    public static final double test(double[] vals1, double[] vals2){
        double tTestPValue1 = -1;
        double tTestPValue2 = -1;
        double mean1 = JSci.maths.ArrayMath.mean(vals1);
        double mean2 = JSci.maths.ArrayMath.mean(vals2);
        double var1 = JSci.maths.ArrayMath.variance(vals1);
        double var2 = JSci.maths.ArrayMath.variance(vals2);

        double var12 = Math.sqrt( (var1 / vals1.length) + (var2 / vals2.length) );

        double t = (mean1 - mean2) / var12;
        double df = vals1.length + vals2.length - 2;
        cern.jet.random.StudentT tDistColt = new cern.jet.random.StudentT(df, (new cern.jet.random.engine.DRand()));

        tTestPValue1 = tDistColt.cdf(t);

        if (tTestPValue1>0.5){
            tTestPValue1 = 1 - tTestPValue1;
        }
        tTestPValue1*=2;

        return tTestPValue1;
    }
}
