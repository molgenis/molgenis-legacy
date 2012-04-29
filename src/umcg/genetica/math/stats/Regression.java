/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package umcg.genetica.math.stats;

/**
 *
 * @author harmjan
 */
public class Regression {
    public static double[] getLinearRegressionCoefficients(double[] xVal, double[] yVal) {
        double n = (double) xVal.length;
        double sumX = 0; double sumXX = 0;
        double sumY = 0; double sumYY = 0;
        double sumXY = 0;
        for (int x=0; x<xVal.length; x++) {
            sumX+= xVal[x];
            sumXX+=xVal[x] * xVal[x];
            sumY+= yVal[x];
            sumYY+=yVal[x] * yVal[x];
            sumXY+=xVal[x] * yVal[x];
        }
        double sXX = sumXX - sumX*sumX / n;
        double sXY = sumXY - sumX*sumY / n;
        double a = sXY / sXX;
        double b = (sumY - a * sumX) / n;
        double[] regressionCoefficients = new double[2];
        regressionCoefficients[0] = a;
        regressionCoefficients[1] = b;
        return regressionCoefficients;
    }
}
