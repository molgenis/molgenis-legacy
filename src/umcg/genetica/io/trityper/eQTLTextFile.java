/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package umcg.genetica.io.trityper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;
import umcg.genetica.io.text.TextFile;

/**
 *
 * @author harmjan
 */
public class eQTLTextFile extends TextFile {

    public static String sepStr = ";";
    public static String tabStr = "\t";
    public static String nullStr = "-";
    public static Pattern separator = Pattern.compile(sepStr);
    public static String header = "PValue\t"
            + "SNPName\t"
            + "SNPChr\t"
            + "SNPChrPos\t"
            + "ProbeName\t"
            + "ProbeChr\t"
            + "ProbeCenterChrPos\t"
            + "CisTrans\t"
            + "SNPType\t"
            + "AlleleAssessed\t"
            + "OverallZScore\t"
            + "DatasetsWhereSNPProbePairIsAvailableAndPassesQC\t"
            + "DatasetsZScores\t"
            + "DatasetsNrSamples\t"
            + "IncludedDatasetsMeanProbeExpression\t"
            + "IncludedDatasetsProbeExpressionVariance\t"
            + "HGNCName\t"
            + "IncludedDatasetsCorrelationCoefficient\t"
            + "Meta-Beta (SE)\t"
            + "Beta (SE)\t"
            + "FoldChange";

    public eQTLTextFile(String loc, boolean W) throws IOException {
        super(loc, W);
        if (W) {
            write(header + "\n");
        }
    }

    public eQTLTextFile(String loc, boolean W, boolean gz) throws IOException {
        super(loc, W);
        if (W) {
            write(header + "\n");
        }
    }

    public void write(EQTL[] eqtllist) throws IOException {
        for (EQTL e : eqtllist) {
            write(e.toString() + "\n");
        }
    }

    public EQTL[] read() throws IOException {
        /*
         * 0 - pval
         * 1 - rs
         * 2 - rs chr
         * 3 - rs chr pos
         * 4 - probe
         * 5 - probe chr
         * 6 - probe chr center pos
         * 7 - cis
         * 8 - alleles
         * 9 - allele assessed
         * 10 - Z-score
         * 11 - dataset
         * 12 - Z-score
         * 13 - nr samples assessed
         * 14 - probe mean
         * 15 - probe variance
         * 16 - probeHugo
         * 17 - correlation
         * 18 - meta-beta
         * 19 - beta
         * 20 - fc
         * 21 - fdr
         */
        String[] elems = readLineElemsReturnReference(tab); // skip headerline
        boolean fdrpresent = false;
        if (elems[elems.length - 1].equals("FDR")) {
            fdrpresent = true;
        }
        elems = readLineElemsReturnReference(tab);
        ArrayList<EQTL> alEQTLS = new ArrayList<EQTL>();
        while (elems != null) {
            EQTL e = new EQTL();
            if (!elems[0].equals(nullStr)) {
                e.setPvalue(toDouble(elems[0]));
            }

            if (!elems[1].equals(nullStr)) {
                e.setRsName(elems[1]);
            }

            if (!elems[2].equals(nullStr)) {
                e.setRsChr(toByte(elems[2]));
            }

            if (!elems[3].equals(nullStr)) {
                e.setRsChrPos(toInt(elems[3]));
            }

            if (!elems[4].equals(nullStr)) {
                e.setProbe(elems[4]);
            }

            if (!elems[5].equals(nullStr)) {
                e.setProbeChr(toByte(elems[5]));
            }

            if (!elems[6].equals(nullStr)) {
                e.setProbeChrPos(toInt(elems[6]));
            }

            if (!elems[7].equals(nullStr)) {
                e.setType(elems[7]);
            }

            if (!elems[8].equals(nullStr)) {
                e.setAlleles(elems[8]);
            }

            if (!elems[9].equals(nullStr)) {
                e.setAlleleAssessed(elems[9]);
            }

            if (!elems[10].equals(nullStr)) {
                e.setZscore(toDouble(elems[10]));
            }

            if (!elems[16].equals(nullStr)) {
                e.setProbeHUGO(elems[16]);
            }


            if (!elems[18].equals(nullStr)) {
                e.setMetaBeta(elems[18]);
            }


            if (!elems[19].equals(nullStr)) {
                e.setBeta(elems[19]);
            }
            if (elems.length > 20) {
                if (!elems[20].equals(nullStr)) {
                    e.setFC(elems[20]);
                }

            }


            if (fdrpresent && !elems[elems.length - 1].equals(nullStr)) {
                e.setFDR(toDouble(elems[elems.length - 1]));
            }

//            e.setDatasets(separator.split(elems[11]));
//            
//            String[] subelems   = separator.split(elems[12]);
//            
//            double[] dsZScores  = new double[subelems.length];
//            int[] dsSamples     = new int[subelems.length];
//            for(int i=0; i<subelems.length; i++){
//                dsZScores[i] = toDouble(subelems[i]);
//            }
//            
//            subelems   = separator.split(elems[13]);
//            for(int i=0; i<subelems.length; i++){
//                dsSamples[i] = toInt(subelems[i]);
//            }
//            
//            double[] dsPmeans  = new double[subelems.length];
//            double[] dsPvars   = new double[subelems.length];
//            double[] dsCorrs   = new double[subelems.length];
//            
//            subelems   = separator.split(elems[14]);
//            for(int i=0; i<subelems.length; i++){
//                dsPmeans[i] = toDouble(subelems[i]);
//            }
//            
//            subelems   = separator.split(elems[15]);
//            for(int i=0; i<subelems.length; i++){
//                dsPvars[i] = toDouble(subelems[i]);
//            }
//            
//            subelems   = separator.split(elems[17]);
//            for(int i=0; i<subelems.length; i++){
//                dsCorrs[i] = toDouble(subelems[i]);
//            }
//            
//            e.setDatasetZScores(dsZScores);
//            e.setDatasetsSamples(dsSamples);
//            
//            e.setProbeMeans(dsPmeans);
//            e.setProbeVariance(dsPvars);
//            
//            e.setProbeHUGO(separator.split(elems[16]));
//            e.setCorrelations(dsCorrs);
//            
            alEQTLS.add(e);

            elems = readLineElemsReturnReference(tab);
        }
        EQTL[] eqtls = new EQTL[alEQTLS.size()];
        return alEQTLS.toArray(eqtls);
    }

    public double toDouble(String s) {
        return Double.parseDouble(s);
    }

    public int toInt(String s) {
        return Integer.parseInt(s);
    }

    private byte toByte(String string) {
        return Byte.parseByte(string);
    }
}
