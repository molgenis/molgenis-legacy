/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package umcg.genetica.math.matrix;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lude, juha
 */
public class DoubleMatrixDataset<T, U> {

    public double[][] rawData = null;
    public int nrRows = 0;
    public int nrCols = 0;
    public List<T> rowObjects = null;
    public List<U> colObjects = null;
    public Set<T> rowsToInclude = null;
    public Set<U> colsToInclude = null;
    public HashMap<T, Integer> hashRows = new HashMap<T, Integer>();
    public HashMap<U, Integer> hashCols = new HashMap<U, Integer>();
    public String fileName = null;

    public DoubleMatrixDataset(String fileName) throws IOException {
        this(fileName, "\t");
    }

    public DoubleMatrixDataset(String fileName, Set<T> rowsToInclude) throws IOException {
        this.rowsToInclude = rowsToInclude;
        System.out.println("loading dataset: " + fileName);
        if (fileName.endsWith(".binary")) {
            try {
                loadExpressionDataInBinaryFormat(fileName);
            } catch (ClassNotFoundException ex) {
                throw new IOException(ex); // for downward compatibility we don't throw ClassNotFoundExceptions :I
            }
        } else {
            loadExpressionData(fileName, "\t");
        }
    }

    public DoubleMatrixDataset(String fileName, Set<T> rowsToInclude, Set<U> colsToInclude) throws IOException {
        this.rowsToInclude = rowsToInclude;
        this.colsToInclude = colsToInclude;
        System.out.println("loading dataset: " + fileName);
        if (fileName.endsWith(".binary")) {
            try {
                loadExpressionDataInBinaryFormat(fileName);
            } catch (ClassNotFoundException ex) {
                throw new IOException(ex);
            }
        } else {
            loadExpressionData(fileName, "\t");
        }
    }

    public DoubleMatrixDataset(String fileName, String delimiter) throws IOException {
        if (!fileName.endsWith(".binary") && !fileName.endsWith(".txt")) {
            throw new IllegalArgumentException("File type must be .txt or .binary (given filename: " + fileName + ")");
        }
        System.out.println("loading dataset: " + fileName);
        if (fileName.endsWith(".binary")) {
            try {
                loadExpressionDataInBinaryFormat(fileName);
            } catch (ClassNotFoundException ex) {
                throw new IOException(ex);
            }
        } else {
            loadExpressionData(fileName, delimiter);
        }
    }

    public DoubleMatrixDataset(double[][] data) {
        this(data.length, data[0].length);
        this.rawData = data;
    }

    public DoubleMatrixDataset(int nrRows, int nrCols) {
        this.nrRows = nrRows;
        this.nrCols = nrCols;
        // runtime type of the arrays will be Object[] but they can only contain T and U elements
        this.rowObjects = new ArrayList<T>(nrRows);
        for (int i = 0; i < nrRows; i++) {
            this.rowObjects.add(null);
        }
        this.colObjects = new ArrayList<U>(nrCols);
        for (int i = 0; i < nrCols; i++) {
            this.colObjects.add(null);
        }
        this.rawData = new double[nrRows][nrCols];
    }

    private void loadExpressionDataInBinaryFormat(String fileName) throws IOException, ClassNotFoundException {
        //First load the raw binary data:
        this.fileName = fileName;
        File fileBinary = new File(fileName + ".dat");
        BufferedInputStream in = null;
        int nrRowsThisBinaryFile = -1;
        int nrColsThisBinaryFile = -1;
        in = new BufferedInputStream(new FileInputStream(fileBinary));
        byte[] bytes = new byte[4];
        in.read(bytes, 0, 4);
        nrRowsThisBinaryFile = byteArrayToInt(bytes);
        in.read(bytes, 0, 4);
        nrColsThisBinaryFile = byteArrayToInt(bytes);

        if (rowsToInclude == null && colsToInclude == null) {
            //We want to load all the data:
            nrRows = nrRowsThisBinaryFile;
            nrCols = nrColsThisBinaryFile;
            rawData = new double[nrRows][nrCols];

            //Now load the row and column identifiers from files
            rowObjects = new ArrayList<T>(nrRows);
            File fileRows = new File(fileName + ".rows.ser");

            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileRows));
            ObjectInputStream ois = new ObjectInputStream(bis);
            try {
                while (bis.available() > 0) {
                    rowObjects.add((T) ois.readObject());
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(DoubleMatrixDataset.class.getName()).log(Level.SEVERE, "Error with row objects", ex);
                ex.printStackTrace();
            }
            bis.close();
            ois.close();
            if (rowObjects.size() != nrRows) {
                throw new IOException("The number of row objects in " + fileRows + " doesn't match the number of rows in " + fileBinary);
            }

            //Now load the column identifiers from file:
            colObjects = new ArrayList<U>(nrCols);
            File fileCols = new File(fileName + ".columns.ser");
            bis = new BufferedInputStream(new FileInputStream(fileCols));
            ois = new ObjectInputStream(bis);
            try {
                while (bis.available() > 0) {
                    colObjects.add((U) ois.readObject());
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(DoubleMatrixDataset.class.getName()).log(Level.SEVERE, "Error with column objects", ex);
                ex.printStackTrace();
            }
            bis.close();
            ois.close();
            if (colObjects.size() != nrCols) {
                throw new IOException("The number of columns objects in " + fileCols + " doesn't match the number of columns in " + fileBinary);
            }

            byte[] buffer = new byte[nrCols * 8];
            long bits = 0;
            for (int row = 0; row < nrRows; row++) {
                in.read(buffer, 0, nrCols * 8);
                int bufferLoc = 0;
                for (int col = 0; col < nrCols; col++) {
                    bits = (long) (0xff & buffer[bufferLoc + 7])
                            | (long) (0xff & buffer[bufferLoc + 6]) << 8
                            | (long) (0xff & buffer[bufferLoc + 5]) << 16
                            | (long) (0xff & buffer[bufferLoc + 4]) << 24
                            | (long) (0xff & buffer[bufferLoc + 3]) << 32
                            | (long) (0xff & buffer[bufferLoc + 2]) << 40
                            | (long) (0xff & buffer[bufferLoc + 1]) << 48
                            | (long) (buffer[bufferLoc]) << 56;

                    rawData[row][col] = Double.longBitsToDouble(bits);
                    bufferLoc += 8;
                }
            }
            in.close();
        } else {
            //We want to confine the set of probes and samples to a subset. Deal with this in a different way.

            //Now load the row identifiers from file
            int[] rowSubsetIndex = new int[nrRowsThisBinaryFile];
            for (int r = 0; r < rowSubsetIndex.length; r++) {
                rowSubsetIndex[r] = -1;
            }
            File fileRows = new File(fileName + ".rows.ser");
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileRows));
            ObjectInputStream ois = new ObjectInputStream(bis);
            int rowIndex = 0;
            rowObjects = new ArrayList<T>(rowsToInclude.size());
            HashMap<T, Integer> hashRowsPresentAndRequested = new HashMap<T, Integer>();
            while (bis.available() > 0) {
                T rowObject = (T) ois.readObject();
                if (rowsToInclude == null || rowsToInclude.contains(rowObject)) {
                    rowSubsetIndex[rowIndex] = hashRowsPresentAndRequested.size();
                    hashRowsPresentAndRequested.put(rowObject, rowIndex);
                    rowObjects.add(rowObject);
                }
                rowIndex++;
            }
            bis.close();
            ois.close();
            nrRows = hashRowsPresentAndRequested.size();

            //Now load the column identifiers from file
            int[] colSubsetIndex = new int[nrColsThisBinaryFile];
            for (int c = 0; c < colSubsetIndex.length; c++) {
                colSubsetIndex[c] = -1;
            }
            File fileCols = new File(fileName + ".columns.ser");
            bis = new BufferedInputStream(new FileInputStream(fileCols));
            ois = new ObjectInputStream(bis);
            int colIndex = 0;
            colObjects = new ArrayList<U>(colsToInclude.size());
            HashMap<U, Integer> hashColsPresentAndRequested = new HashMap<U, Integer>();
            while (bis.available() > 0) {
                U colObject = (U) ois.readObject();
                if (colsToInclude == null || colsToInclude.contains(colObject)) {
                    colSubsetIndex[colIndex] = hashColsPresentAndRequested.size();
                    hashColsPresentAndRequested.put(colObject, colIndex);
                    colObjects.add(colObject);
                }
                colIndex++;
            }
            bis.close();
            ois.close();
            nrCols = hashColsPresentAndRequested.size();

            //Now load the binary data:
            rawData = new double[nrRows][nrCols];

            byte[] buffer = new byte[nrColsThisBinaryFile * 8];
            long bits = 0;
            for (int row = 0; row < nrRowsThisBinaryFile; row++) {
                in.read(buffer, 0, nrColsThisBinaryFile * 8);
                int bufferLoc = 0;
                for (int col = 0; col < nrColsThisBinaryFile; col++) {
                    bits = (long) (0xff & buffer[bufferLoc + 7])
                            | (long) (0xff & buffer[bufferLoc + 6]) << 8
                            | (long) (0xff & buffer[bufferLoc + 5]) << 16
                            | (long) (0xff & buffer[bufferLoc + 4]) << 24
                            | (long) (0xff & buffer[bufferLoc + 3]) << 32
                            | (long) (0xff & buffer[bufferLoc + 2]) << 40
                            | (long) (0xff & buffer[bufferLoc + 1]) << 48
                            | (long) (buffer[bufferLoc]) << 56;

                    int rowI = rowSubsetIndex[row];
                    int colI = colSubsetIndex[col];
                    if (rowI != -1 && colI != -1) {
                        rawData[rowI][colI] = Double.longBitsToDouble(bits);
                    }
                    bufferLoc += 8;
                }
            }
            in.close();
        }
        recalculateHashMaps();
        System.out.println("Binary file:\t" + fileName + "\thas been loaded, nrRows:\t" + nrRows + "\tnrCols:\t" + nrCols);
    }

    private void loadExpressionData(String fileName, String delimiter) throws IOException {
        this.fileName = fileName;
        File file = new File(fileName);
        if (!file.canRead()) {
            System.out.println("Error! Cannot open file:\t" + fileName);
            System.exit(0);
        }
        int sampleOffset = 1;
        int[] sampleIndex = null;
        java.io.BufferedReader in = new java.io.BufferedReader(new java.io.FileReader(file));
        String str = in.readLine();
        String[] data = str.split(delimiter);

        nrCols = data.length - sampleOffset;
        colObjects = new ArrayList<U>(nrCols);
        sampleIndex = new int[nrCols];
        for (int s = 0; s < nrCols; s++) {
            colObjects.add((U) data[s + sampleOffset]);
            hashCols.put(colObjects.get(s), s);
            sampleIndex[s] = s;
        }

        nrRows = 0;
        while ((str = in.readLine()) != null) {
            nrRows++;
        }
        in.close();
        rawData = new double[nrRows][nrCols];
        rowObjects = new ArrayList<T>(nrRows);
        in = new java.io.BufferedReader(new java.io.FileReader(file));
        str = in.readLine();
        int row = 0;

        nrRows = 0;
        while ((str = in.readLine()) != null) {
            nrRows++;
        }
        in.close();
        rawData = new double[nrRows][nrCols];
        rowObjects = new ArrayList<T>(nrRows);
        in = new java.io.BufferedReader(new java.io.FileReader(file));
        str = in.readLine();

        boolean correctData = true;
        while ((str = in.readLine()) != null) {
            data = str.split(delimiter);
            rowObjects.add((T) new String(data[0].getBytes()));
            hashRows.put(rowObjects.get(row), row);
            for (int s = 0; s < nrCols; s++) {
                String cell = data[sampleIndex[s] + sampleOffset];
                Double d = Double.NaN;
                try {
                    d = Double.parseDouble(cell);
                } catch (NumberFormatException e) {
                    correctData = false;
                }
                rawData[row][s] = d;
            }
            row++;
            //if (nrProbes%100==0) System.out.println(nrProbes);
        }
        if (!correctData) {
            System.out.println("WARNING: your data contains NaN/unparseable values!");
        }
        in.close();
        recalculateHashMaps();
        System.out.println(fileName + "\thas been loaded, nrProbes:\t" + nrRows + "\tnrSamples:\t" + nrCols);
    }

    public void recalculateHashMaps() {

        hashRows.clear();
        for (int probeItr = 0; probeItr < nrRows; probeItr++) {
            hashRows.put(rowObjects.get(probeItr), probeItr);
        }
        hashCols.clear();
        for (int sampleItr = 0; sampleItr < nrCols; sampleItr++) {
            hashCols.put(colObjects.get(sampleItr), sampleItr);
        }
    }

    public double[][] getRawDataTransposed() {
        double[][] rawDataTransposed = new double[nrCols][nrRows];
        for (int s = 0; s < nrCols; s++) {
            for (int p = 0; p < nrRows; p++) {
                rawDataTransposed[s][p] = rawData[p][s];
            }
        }
        return rawDataTransposed;
    }

    public void calculateProbeCoexpression(String outputFile) {

        standardNormalizeData();

        System.gc();
        System.gc();
        System.gc();
        System.gc();

        SymmetricByteDistanceMatrix matrix = new SymmetricByteDistanceMatrix(nrRows);
        int sampleCountMinusOne = nrCols - 1;
        long[] corrDist = new long[201];
        for (int f = 0; f < nrRows; f++) {
            for (int g = f + 1; g < nrRows; g++) {
                //Calculate correlation:
                double covarianceInterim = 0;
                for (int s = 0; s < nrCols; s++) {
                    covarianceInterim += rawData[f][s] * rawData[g][s];
                }
                double covariance = covarianceInterim / (double) (sampleCountMinusOne);
                double correlation = covariance;
                int corrIndex = (int) Math.round((correlation + 1.0d) * 100.0d);
                corrDist[corrIndex]++;
                matrix.set(f, g, corrIndex);
            }
            if (f % 100 == 99) {
                System.out.println((f + 1) + " Probes processed");
            }
        }
        matrix.save(new File(outputFile));

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(outputFile + "-MatrixProbes.txt"));
            for (int f = 0; f < nrRows; f++) {
                out.write(rowObjects.get(f) + "\t" + rowObjects.get(f) + "\n");
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            System.out.println("Error parsing file:\t" + e.getMessage());
            for (int ex = 0; ex < e.getStackTrace().length; ex++) {
                System.out.println(e.getStackTrace()[ex].getClassName() + "\t" + e.getStackTrace()[ex].getMethodName() + "\t" + e.getStackTrace()[ex].getLineNumber());
            }
        }


    }

    public void standardNormalizeData() {

        /*
         * System.out.println("\nNormalizing data:"); //Calculate the average
         * expression, when per sample all raw expression levels have been
         * ordered: double[] rankedMean = new double[nrProbes]; for (int
         * probeID=0; probeID<nrProbes; probeID++) { double quantile = ((double)
         * probeID + 1.0d) / ((double) nrProbes + 1d); rankedMean[probeID] =
         * cern.jet.stat.Probability.normalInverse(quantile); }
         *
         * //Iterate through each sample: hgea.math.RankDoubleArray
         * rankDoubleArray = new hgea.math.RankDoubleArray(); for (int s=0;
         * s<nrSamples; s++) { double[] probes = new double[nrProbes]; for (int
         * p=0; p<nrProbes; p++) probes[p]=rawData[p][s]; double[] probesRanked
         * = rankDoubleArray.rank(probes); double[] probesQuantileNormalized =
         * new double[nrProbes]; for (int p=0; p<nrProbes; p++) {
         * probesQuantileNormalized[p] = rankedMean[(int) probesRanked[p]]; }
         * for (int p=0; p<nrProbes; p++) rawData[p][s] = (float)
         * probesQuantileNormalized[p]; }
         */


        System.out.println("Setting probe mean to zero and stdev to one for every probe:");
        for (int probeID = 0; probeID < nrRows; probeID++) {
            double vals[] = new double[nrCols];
            System.arraycopy(rawData[probeID], 0, vals, 0, nrCols);
            double mean = JSci.maths.ArrayMath.mean(vals);
            for (int s = 0; s < nrCols; s++) {
                vals[s] -= (double) mean;
            }
            double standardDeviation = JSci.maths.ArrayMath.standardDeviation(vals);
            for (int s = 0; s < nrCols; s++) {
                rawData[probeID][s] = (float) (vals[s] / standardDeviation);
            }
        }

    }

    public void save(String fileName) throws IOException {
        if (fileName.endsWith(".binary")) {

            //Create binary file:
            BufferedOutputStream out = null;
            File fileBinary = new File(fileName + ".dat");
            out = new BufferedOutputStream(new FileOutputStream(fileBinary));
            out.write(intToByteArray(nrRows));
            out.write(intToByteArray(nrCols));
            byte[] buffer = new byte[rawData[0].length * 8];
            for (int row = 0; row < rawData.length; row++) { // rows
                int bufferLoc = 0;
                for (int col = 0; col < rawData[0].length; col++) { // columns
                    long bits = Double.doubleToLongBits(rawData[row][col]);
                    buffer[bufferLoc] = (byte) (bits >> 56);
                    buffer[bufferLoc + 1] = (byte) (bits >> 48 & 0xff);
                    buffer[bufferLoc + 2] = (byte) (bits >> 40 & 0xff);
                    buffer[bufferLoc + 3] = (byte) (bits >> 32 & 0xff);
                    buffer[bufferLoc + 4] = (byte) (bits >> 24 & 0xff);
                    buffer[bufferLoc + 5] = (byte) (bits >> 16 & 0xff);
                    buffer[bufferLoc + 6] = (byte) (bits >> 8 & 0xff);
                    buffer[bufferLoc + 7] = (byte) (bits & 0xff);
                    bufferLoc += 8;
                }
                out.write(buffer);
            }
            out.close();
            File fileRows = new File(fileName + ".rows.ser");
            ObjectOutputStream outRows = new ObjectOutputStream(new FileOutputStream(fileRows));
            for (int p = 0; p < nrRows; p++) {
                outRows.writeObject(rowObjects.get(p));
            }
            outRows.close();
            File fileCols = new File(fileName + ".columns.ser");
            ObjectOutputStream outCols = new ObjectOutputStream(new FileOutputStream(fileCols));
            for (int p = 0; p < nrCols; p++) {
                outCols.writeObject(colObjects.get(p));
            }
            outCols.close();

        } else {
            File fileOut = new File(fileName);
            java.io.BufferedWriter out = new java.io.BufferedWriter(new java.io.FileWriter(fileOut));
            StringBuilder sb = new StringBuilder("-");
            for (int s = 0; s < nrCols; s++) {
                if (colObjects == null) {
                } else {
                    sb.append("\t").append(colObjects.get(s));
                }

            }
            out.write(sb.toString() + "\n");

            for (int p = 0; p < nrRows; p++) {
                if (rowObjects == null) {
                    sb = new StringBuilder();
                } else {
                    sb = new StringBuilder(rowObjects.get(p).toString());
                }
                for (int s = 0; s < nrCols; s++) {
                    sb.append("\t").append(rawData[p][s]);
                }
                out.write(sb.toString());
                out.newLine();
            }
            out.close();
        }
    }

    /**
     *
     * @return transposed dataset with references to the SAME row and column
     * objects and maps, no cloning
     */
    public DoubleMatrixDataset<U, T> getTransposedDataset() {

        DoubleMatrixDataset<U, T> transposed = new DoubleMatrixDataset<U, T>(getRawDataTransposed());
        transposed.rowObjects = colObjects;
        transposed.hashRows = hashCols;
        transposed.colObjects = rowObjects;
        transposed.hashCols = hashRows;
        return transposed;

    }

    private byte[] intToByteArray(int value) {
        return new byte[]{(byte) (value >>> 24),
                    (byte) (value >>> 16),
                    (byte) (value >>> 8),
                    (byte) value};
    }

    private int byteArrayToInt(byte[] b) {
        return (b[0] << 24)
                + ((b[1] & 0xff) << 16)
                + ((b[2] & 0xff) << 8)
                + (b[3] & 0xff);
    }
}
