/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package umcg.genetica.math.stats;

import JSci.maths.ArrayMath;
import java.util.Arrays;

/**
 *
 * @author juha
 */
public class FDRUtils {

    public enum CloneArrays {

        CLONE_BOTH, DONT_CLONE, CLONE_REAL
    }

    public enum SortArrays {

        SORT_DESCENDING, ALREADY_SORTED_DESCENDING
    }

    public enum AbsoluteValues {

        USE_ABSOLUTE, USE_AS_IS
    }

    // prevent instantiation, only use static factory methods
    private FDRUtils() {
    }

    /**
     *
     * Returns the threshold value in the whole of given real data matrix that
     * corresponds to the given false discovery rate against the null
     * distribution from given permuted data matrix.
     *
     * @param real real data [x][y]
     * @param permuted permuted data [x][y]
     * @param fdr false discovery rate to find
     * @param c should the data be cloned or not (because the arrays are
     * modified)
     * @param s should the data be sorted first or are they already sorted
     * @param a should absolute values be used
     * @return largest threshold value in real data so that FDR is not larger
     * than the given one
     */
    public static double getGlobalThreshold(double[][] real, double[][] permuted, double fdr, CloneArrays c, SortArrays s, AbsoluteValues a) {

        double[][][] perm3d = new double[1][][];
        perm3d[0] = permuted;
        return getGlobalThreshold(real, perm3d, fdr, c, s, a);
    }

    /**
     *
     * Returns the threshold value in the whole of given real data matrix that
     * corresponds to the given false discovery rate against the null
     * distribution from given permuted data matrices.
     *
     * @param real real data [x][y]
     * @param permuted permuted data [permutation][x][y]
     * @param fdr false discovery rate to find
     * @param c should the data be cloned or not (because the arrays are
     * modified)
     * @param s should the data be sorted first or are they already sorted
     * @param a should absolute values be used
     * @return largest threshold value in real data so that FDR is not larger
     * than the given one
     */
    public static double getGlobalThreshold(double[][] real, double[][][] permuted, double fdr, CloneArrays c, SortArrays s, AbsoluteValues a) {

        checkNulls(real, permuted);
        checkSizes(real, permuted);
        checkFDR(fdr);

        int nrPermutations = permuted.length;
        double[][] realData = real;
        double[][][] permData = permuted;
        switch (c) {
            case CLONE_BOTH:
                realData = real.clone();
                permData = permuted.clone();
                break;
            case CLONE_REAL:
                realData = real.clone();
                break;
        }

        switch (s) {
            case SORT_DESCENDING:
                realData = sortDesc(realData);
                permData = sortDesc(permData);
                break;
        }

        switch (a) {
            case USE_ABSOLUTE:
                realData = ArrayMath.abs(realData);
                for (int p = 0; p < nrPermutations; p++) {
                    permData[p] = ArrayMath.abs(permData[p]);
                }
                break;
        }

        double threshold = -1;
        int nrSignificantInReal = 0;
        int nrSignificantInPerm = 0;
        int[] realColsTraversed = new int[realData.length];
        int[][] permColsTraversed = new int[nrPermutations][realData.length];
        double foundFDR = -1;

        while (foundFDR < fdr) {

            // get next largest value i.e. threshold from real data
            // knowing that the data are sorted both by rows and columns
            double realThreshold = 0;
            int nextMaxRow = -1;
            for (int i = 0; i < realColsTraversed.length; i++) {
                if (realData[i][realColsTraversed[i]] > realThreshold) {
                    realThreshold = realData[i][realColsTraversed[i]];
                    nextMaxRow = i;
                }
                if (realColsTraversed[i] == 0) {
                    break;
                }
            }
            realColsTraversed[nextMaxRow]++;
            nrSignificantInReal++;

            for (int p = 0; p < nrPermutations; p++) {
                for (int i = 0; i < permColsTraversed[0].length; i++) {
                    while (permData[p][i][permColsTraversed[p][i]] > realThreshold) {
                        nrSignificantInPerm++;
                        permColsTraversed[p][i]++;
                    }
                    if (permColsTraversed[p][i] == 0) {
                        break;
                    }
                }
            }

            foundFDR = (double) nrSignificantInPerm / (double) nrPermutations / (double) nrSignificantInReal;
            if (foundFDR <= fdr) {
                threshold = realThreshold;
            }
        }

        return threshold;
    }

    /**
     *
     * Returns the threshold values per row in the given real data matrix that
     * corresponds to the given false discovery rate against the null
     * distribution from rows of given permuted data matrix.
     *
     * @param real real data [x][y]
     * @param permuted permuted data [x][y]
     * @param fdr false discovery rate to find
     * @param c should the data be cloned or not (because the arrays are
     * modified)
     * @param s should the data be sorted first or are they already sorted
     * @param a should absolute values be used
     * @return largest threshold values per row in real data so that FDR is not
     * larger than the given one
     */
    public static double[] getThresholdsPerRow(double[][] real, double[][] permuted, double fdr, CloneArrays c, SortArrays s, AbsoluteValues a) {

        double[][][] perm3d = new double[1][][];
        perm3d[0] = permuted;
        return getThresholdsPerRow(real, perm3d, fdr, c, s, a);
    }

    /**
     *
     * Returns the threshold values per row in the given real data matrix that
     * corresponds to the given false discovery rate against the null
     * distribution from rows of given permuted data matrices.
     *
     * @param real real data [x][y]
     * @param permuted permuted data [permutation][x][y]
     * @param fdr false discovery rate to find
     * @param c should the data be cloned or not (because the arrays are
     * modified)
     * @param s should the data be sorted first or are they already sorted
     * @param a should absolute values be used
     * @return largest threshold values per row in real data so that FDR is not
     * larger than the given one
     */
    public static double[] getThresholdsPerRow(double[][] real, double[][][] permuted, double fdr, CloneArrays c, SortArrays s, AbsoluteValues a) {

        checkNulls(real, permuted);
        checkSizes(real, permuted);
        checkFDR(fdr);

        int nrPermutations = permuted.length;
        double[][] realData = real;
        double[][][] permData = permuted;
        switch (c) {
            case CLONE_BOTH:
                realData = real.clone();
                permData = permuted.clone();
                break;
            case CLONE_REAL:
                realData = real.clone();
                break;
        }

        switch (a) {
            case USE_ABSOLUTE:
                realData = ArrayMath.abs(realData);
                for (int p = 0; p < nrPermutations; p++) {
                    permData[p] = ArrayMath.abs(permData[p]);
                }
                break;
        }

        double[] thresholds = new double[realData.length];

        for (int row = 0; row < realData.length; row++) {

            if (s == SortArrays.SORT_DESCENDING) {
                Arrays.sort(realData[row]);
                realData[row] = ArrayMath.invert(realData[row]);
                for (int p = 0; p < nrPermutations; p++) {
                    Arrays.sort(permData[p][row]);
                    permData[p][row] = ArrayMath.invert(permData[p][row]);
                }
            }

            int nrSignificantInReal = 0;
            int nrSignificantInPermuted = 0;
            int[] permTraversed = new int[nrPermutations];
            double foundFDR = 1;
            for (int i = 0; i < realData[0].length; i++) {

                double thresholdReal = realData[row][i];
                nrSignificantInReal++;

                for (int p = 0; p < nrPermutations; p++) {
                    for (int j = permTraversed[p]; j < permuted[0][0].length; j++) {
//                        if (row == 1) {
//                            System.out.println(i + "\t" + thresholdReal + "\t" + p + "\t" + j + "\t" + permuted[p][row][j]);
//                        }
                        if (permuted[p][row][j] > thresholdReal) {
                            nrSignificantInPermuted++;
                            permTraversed[p]++;
                        } else {
                            break;
                        }
                    }
                }

                foundFDR = (double) (nrSignificantInPermuted) / (double) nrPermutations / (double) nrSignificantInReal;
//                if (row == 1) {
//                    System.out.println(row + "\t" + nrSignificantInReal + "\t" + nrSignificantInPermuted + "\t" + foundFDR + "\t" + thresholdReal);
//                }
                if (foundFDR <= fdr) {
                    thresholds[row] = thresholdReal;
                }
            }
        }

        return thresholds;

    }

    /**
     *
     * Sorts the given array both by rows and by columns.
     *
     * @param array array to be sorted
     * @return sorted array
     */
    private static double[][] sortDesc(double[][] array) {
        // sort data wrt each row
        for (int i = 0; i < array.length; i++) {
            Arrays.sort(array[i]);
            array[i] = ArrayMath.invert(array[i]);
        }
        // sort data wrt each column
        array = ArrayMath.transpose(array);
        for (int i = 0; i < array.length; i++) {
            Arrays.sort(array[i]);
            array[i] = ArrayMath.invert(array[i]);
        }
        array = ArrayMath.transpose(array);

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                System.out.print(array[i][j] + "\t");
            }
            System.out.println();
        }

        return array;
    }

    /**
     *
     * Sorts the given arrays both by rows and by columns.
     *
     * @param array arrays to be sorted ([array][row][col])
     * @return sorted arrays
     */
    private static double[][][] sortDesc(double[][][] array) {

        for (int p = 0; p < array.length; p++) {
            // sort data wrt each row
            for (int i = 0; i < array[0].length; i++) {
                Arrays.sort(array[p][i]);
                array[p][i] = ArrayMath.invert(array[p][i]);
            }
            // sort data wrt each column
            array[p] = ArrayMath.transpose(array[p]);
            for (int i = 0; i < array[0].length; i++) {
                Arrays.sort(array[p][i]);
                array[p][i] = ArrayMath.invert(array[p][i]);
            }
            array[p] = ArrayMath.transpose(array[p]);
        }

        return array;
    }

    private static void checkNulls(double[][] real, double[][][] permuted) {
        if (real == null) {
            throw new IllegalArgumentException("Null real data given.");
        }
        if (permuted == null) {
            throw new IllegalArgumentException("Null permuted data given.");
        }
        for (int i = 0; i < permuted.length; i++) {
            if (permuted[i] == null) {
                throw new IllegalArgumentException("Null permuted data given at index " + i + ".");
            }
        }

    }

    private static void checkSizes(double[][] real, double[][][] permuted) {
        if (real.length != permuted[0].length || real[0].length != permuted[0][0].length) {
            throw new IllegalArgumentException("Matrix sizes (real and permuted) must be the same. Real: " + real.length + " x " + real[0].length
                    + ", permuted: " + permuted.length + " x " + permuted[0].length);
        }
    }

    private static void checkFDR(double fdr) {
        if (fdr < Double.MIN_VALUE || fdr >= 1) {
            throw new IllegalArgumentException("FDR must be larger than 0 and smaller than 1: " + fdr);
        }
    }
}
