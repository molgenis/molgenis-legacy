package matrix.implementations.binary;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import matrix.AbstractDataMatrixInstance;

import org.apache.log4j.Logger;
import org.molgenis.data.Data;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.matrix.component.interfaces.SliceableMatrix;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservedValue;

public class BinaryDataMatrixInstance_NEW<E> extends AbstractDataMatrixInstance<E>
{
	Logger logger = Logger.getLogger(getClass().getSimpleName());
	int HD_BLOCK_SIZE = 8000000; //FIXME: how to determine? whats best general size?

	/***********/
	/** CONSTRUCTOR */
	/***********/
	public BinaryDataMatrixInstance_NEW(File bin) throws Exception
	{
		this.setBin(bin);

		FileInputStream fis;
		DataInputStream dis;

		fis = new FileInputStream(bin);
		dis = new DataInputStream(fis);

		Data dataDescription = new Data();

		int startOfElements = 0;

		// first 'Data' object metadata contained in the bin file

		this.setNullChar(readStringFromDIS(dis, 1));
		dataDescription.setName(readStringFromDIS(dis, dis.readUnsignedByte()));
		dataDescription.setInvestigation_Name(readStringFromDIS(dis, dis.readUnsignedByte()));
		dataDescription.setFeatureType(readStringFromDIS(dis, dis.readUnsignedByte()));
		dataDescription.setTargetType(readStringFromDIS(dis, dis.readUnsignedByte()));
		dataDescription.setValueType(dis.readBoolean() == true ? "Decimal" : "Text");
		this.setNumberOfCols(dis.readInt());
		this.setNumberOfRows(dis.readInt());

		startOfElements += 1;
		startOfElements += dataDescription.getName().length() + 1;
		startOfElements += dataDescription.getInvestigation_Name().length() + 1;
		startOfElements += dataDescription.getFeatureType().length() + 1;
		startOfElements += dataDescription.getTargetType().length() + 1;
		startOfElements += 1 + 4 + 4;

		this.setData(dataDescription);
		
		// now the information contained within the actual matrix file

		int[] colNameLengths = new int[this.getNumberOfCols()];
		int[] rowNameLengths = new int[this.getNumberOfRows()];

		for (int i = 0; i < colNameLengths.length; i++)
		{
			colNameLengths[i] = dis.readUnsignedByte();
		}

		for (int i = 0; i < rowNameLengths.length; i++)
		{
			rowNameLengths[i] = dis.readUnsignedByte();
		}

		startOfElements += colNameLengths.length;
		startOfElements += rowNameLengths.length;

		ArrayList<String> colNames = new ArrayList<String>(this.getNumberOfCols());
		ArrayList<String> rowNames = new ArrayList<String>(this.getNumberOfRows());

		for (int i = 0; i < this.getNumberOfCols(); i++)
		{
			colNames.add(i, readStringFromDIS(dis, colNameLengths[i]));
			startOfElements += colNameLengths[i];
		}

		for (int i = 0; i < this.getNumberOfRows(); i++)
		{
			rowNames.add(i, readStringFromDIS(dis, rowNameLengths[i]));
			startOfElements += rowNameLengths[i];
		}

		this.setColNames(colNames);
		this.setRowNames(rowNames);

		if (dataDescription.getValueType().equals("Text"))
		{
			this.setTextElementLength(dis.readUnsignedByte());
			logger.debug("this.getTextElementLength() = " + this.getTextElementLength());
			startOfElements += 1;
			if (this.getTextElementLength() == 0)
			{
				byte[] textDataElementLengths = new byte[this.getNumberOfCols() * this.getNumberOfRows()];
				dis.read(textDataElementLengths);
				startOfElements += textDataElementLengths.length;
				this.setTextDataElementLengths(textDataElementLengths);
			}
		}

		// now prepare for random access querying
		this.setStartOfElementsPointer(startOfElements);
		this.setNullCharPattern(Pattern.compile(this.getNullChar() + "+"));

		if (dataDescription.getValueType().equals("Text"))
		{
			if (this.getTextElementLength() == 0)
			{
				int endOfElementsPointer = this.getStartOfElementsPointer();
				for (byte b : this.getTextDataElementLengths())
				{
					endOfElementsPointer += b;
				}
				this.setEndOfElementsPointer(endOfElementsPointer);
			}
			else
			{
				int endOfElementsPointer = startOfElements
						+ (this.getNumberOfCols() * this.getNumberOfRows() * this.getTextElementLength());
				this.setEndOfElementsPointer(endOfElementsPointer);
			}
		}
		else
		{
			int endOfElementsPointer = startOfElements + (this.getNumberOfCols() * this.getNumberOfRows() * 8);
			this.setEndOfElementsPointer(endOfElementsPointer);
		}
		System.out.println(dataDescription.getValueType() + " - getStartOfElementsPointer: "
				+ this.getStartOfElementsPointer());
		System.out.println(dataDescription.getValueType() + " - getEndOfElementsPointer: " + this.getEndOfElementsPointer());
	}

	/***********/
	/** HELPER FUNCTIONS */
	/***********/

	// Primary function to read doubles
	public Double[] readNextDoublesFromRAF(RandomAccessFile raf, int elementAmount) throws IOException
	{
		byte[] arr = new byte[elementAmount * 8];
		raf.read(arr);
		return byteArrayToDoubles(arr);
	}

	/**
	 * Primary function to read strings. Performs one read action from the hard drive.
	 */
	public Object[] readStringsFromRAF(RandomAccessFile raf, int elementAmount, int elementLength) throws IOException
	{
		//System.out.println("readFixedTextFromRAF called with: rafPointer=" + raf.getFilePointer() + ", elementAmount="+ elementAmount + ", elementLength=" + elementLength);
		Object[] result = new Object[elementAmount];
		int totalBytes = elementAmount * elementLength;

		byte[] bytes = new byte[totalBytes];
		char[] chars = new char[totalBytes];

		raf.read(bytes);

		for (int i = 0; i < bytes.length; i++)
		{
			chars[i] = (char) bytes[i];
			//System.out.println("chars[i]: " + chars[i]);
		}

		for (int i = 0; i < elementAmount; i++)
		{
			int start = i * elementLength;
			int stop = start + elementLength;
			char[] subArr = new char[elementLength];
			int count = 0;
			for (int j = start; j < stop; j++)
			{
				subArr[count] = chars[j];
				count++;
			}
			String fromChars = new String(subArr);
			if (this.getNullCharPattern().matcher(fromChars).matches())
			{
				result[i] = "";
			}else{
				result[i] = fromChars;
			}
			//System.out.println("result[" + i + "]: " + result[i]);
		}
		return result;
	}

	// Function to help read the header from a DataInputStream
	public String readStringFromDIS(DataInputStream dis, int stringLength) throws IOException
	{
		byte[] string = new byte[stringLength];
		dis.read(string);
		return new String(string);
	}

	// Wrapped by readNextDoublesFromRAF
	public static Double[] byteArrayToDoubles(byte[] arr)
	{
		int nr = arr.length / 8;
		Double[] res = new Double[nr];
		for (int i = 0; i < arr.length; i += 8)
		{
			long longBits = 0;
			for (int j = 0; j < 8; j++)
			{
				longBits <<= 8;
				longBits |= (long) arr[i + j] & 255;
			}
			double d = Double.longBitsToDouble(longBits);
			if (d == Double.MAX_VALUE)
			{
				res[i / 8] = null;
			}
			else
			{
				res[i / 8] = d;
			}
		}
		return res;
	}

	/***********/
	/** MODEL */
	/***********/
	int textElementLength;
	int startOfElementsPointer;
	int endOfElementsPointer; //FIXME: is this big enough? whats the approx. file size then?
	byte[] textDataElementLengths;
	File bin;
	String nullChar;
	Pattern nullCharPattern;

	public int getTextElementLength()
	{
		return textElementLength;
	}

	private void setTextElementLength(int textElementLength)
	{
		this.textElementLength = textElementLength;
	}

	public int getStartOfElementsPointer()
	{
		return startOfElementsPointer;
	}

	private void setStartOfElementsPointer(int startOfElementsPointer)
	{
		this.startOfElementsPointer = startOfElementsPointer;
	}

	public int getEndOfElementsPointer()
	{
		return endOfElementsPointer;
	}

	private void setEndOfElementsPointer(int endOfElementsPointer)
	{
		this.endOfElementsPointer = endOfElementsPointer;
	}

	public byte[] getTextDataElementLengths()
	{
		return textDataElementLengths;
	}

	private void setTextDataElementLengths(byte[] textDataElementLengths)
	{
		this.textDataElementLengths = textDataElementLengths;
	}

	public File getBin()
	{
		return bin;
	}

	private void setBin(File bin)
	{
		this.bin = bin;
	}

	public String getNullChar()
	{
		return nullChar;
	}

	private void setNullChar(String nullChar)
	{
		this.nullChar = nullChar;
	}

	public Pattern getNullCharPattern()
	{
		return nullCharPattern;
	}

	private void setNullCharPattern(Pattern nullCharPattern)
	{
		this.nullCharPattern = nullCharPattern;
	}

	/***********/
	/** MATRIX IMPLEMENTATION */
	/***********/
	@Override
	/**
	 * Get only one element (from a RandomAccessFile instance), therefore extremely inefficient to retrieve many elements and not optimizable.
	 * @status: Done
	 */
	public Object getElement(int rowindex, int colindex) throws Exception
	{
		Object result = new Object();
		RandomAccessFile raf = new RandomAccessFile(this.getBin(), "r");
		int startIndex = (rowindex * this.getNumberOfCols()) + colindex;
		if (this.getData().getValueType().equals("Decimal"))
		{
			raf.seek(this.startOfElementsPointer + (startIndex * 8));
			result = readNextDoublesFromRAF(raf, 1)[0];
		}
		else
		{
			if (this.getTextElementLength() != 0)
			{
				raf.seek(this.startOfElementsPointer + (startIndex * this.getTextElementLength()));
				result = readStringsFromRAF(raf, 1, this.getTextElementLength())[0];
			}
			else
			{
				long byteOffset = 0;
				for (int i = 0; i < startIndex; i++)
				{
					byteOffset += this.getTextDataElementLengths()[i];
				}
				raf.seek(this.startOfElementsPointer + byteOffset);
				result = readStringsFromRAF(raf, 1, this.getTextDataElementLengths()[startIndex])[0];
			}
		}
		raf.close();
		return result;
	}

	@Override
	/**
	 * Get a row (from a RandomAccessFile instance), optimized by reading the entire row at once
	 * @status: Done
	 */
	public Object[] getRow(int rowIndex) throws Exception
	{
		Object[] result = new Object[this.getNumberOfCols()];
		RandomAccessFile raf = new RandomAccessFile(this.getBin(), "r");

		if (this.getData().getValueType().equals("Decimal"))
		{
			raf.seek(this.startOfElementsPointer + (rowIndex * this.getNumberOfCols() * 8));
			result = readNextDoublesFromRAF(raf, result.length);
		}
		else
		{
			if (this.getTextElementLength() != 0)
			{
				raf.seek(this.startOfElementsPointer
						+ (rowIndex * this.getNumberOfCols() * this.getTextElementLength()));
				result = readStringsFromRAF(raf, this.getNumberOfCols(), this.getTextElementLength());
			}
			else
			{
				int startIndex = rowIndex * this.getNumberOfCols();
				long byteOffset = 0;
				for (int i = 0; i < startIndex; i++)
				{
					byteOffset += this.getTextDataElementLengths()[i];
				}
				raf.seek(this.startOfElementsPointer + byteOffset);
				
//				System.out.println("starting to read at: " + (this.startOfElementsPointer + byteOffset));
//				System.out.println("can read until: " + this.endOfElementsPointer);
				int diff = (int) (this.endOfElementsPointer - (this.startOfElementsPointer + byteOffset));
//				System.out.println("which is " + diff + " bytes");
			
				// read the row as 1 big element
				Object[] tmpResult = readStringsFromRAF(raf, 1, diff);
				String rowLine = tmpResult[0].toString();
				
				//cut up the result
				int startAt = 0;
				for (int i = 0; i <  this.getNumberOfCols(); i++)
				{
					int elementLength = this.getTextDataElementLengths()[i+startIndex];
					result[i] = rowLine.substring(startAt, startAt+elementLength);
					startAt += elementLength;
				}
			}
		}
		raf.close();
		return result;
	}

	@Override
	/**
	 * Get a column (from a RandomAccessFile instance), optimized by reading in blocks of HD_BLOCK_SIZE bytes
	 * to get multiple values at once - unless the colums are further apart than HD_BLOCK_SIZE bytes ofcourse.
	 * (for variable length text elements this cannot be guessed...)
	 * @status: TODO
	 */
	public Object[] getCol(int colIndex) throws Exception
	{
		Object[] result = new Object[this.getNumberOfRows()];
		RandomAccessFile raf = new RandomAccessFile(this.getBin(), "r");
		
		if (this.getData().getValueType().equals("Decimal"))
		{
			raf.seek(this.startOfElementsPointer + (colIndex * 8));
			
			//find out if next column value is more than HD_BLOCK_SIZE away
			//if so: seperate queries
			//if not.. read in ??
			
			//retrieve by skipping over, time it
			//if closer than HD_BLOCK_SIZE together, retrieve multiple and time it
			//pick fastest for the rest
			
			int theEnd = this.getEndOfElementsPointer();
			
			if(this.getNumberOfCols() > HD_BLOCK_SIZE){
				//retrieve per element because we can't use block retrieve anyway!
				//TODO
			}else{
				//maybe retrieving blocks is useful now...
				
				if(this.getNumberOfRows() < 30){
					//test of speed difference only useful when retrieving many elements
					//
				}else{
					//find out per-element speed, retrieve 10
				//	for()
				//	readNextDoublesFromRAF(raf, 1)[0];
				}
				
				
			}
			
			if(theEnd > 32412343){
				result = readNextDoublesFromRAF(raf, result.length);
			}
			
			
		}
		else
		{
			if (this.getTextElementLength() != 0)
			{
				
			}
			else
			{
				
			}
		}
			
		return result;
	}

	/**
	 * Get a submatrix by intersecting indices (from a RandomAccessFile
	 * instance), optimized by reading in blocks of 8 megabyte so get multiple
	 * values at once - unless some colums are further apart than 8 megabyte.
	 * (for variable length text elements this cannot be guessed...)
	 * 
	 * @status: TODO
	 */
	@Override
	public AbstractDataMatrixInstance<Object> getSubMatrix(int[] rowIndices, int[] colIndices) throws MatrixException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Get a submatrix by index-offsets (from a RandomAccessFile instance),
	 * optimized by reading in blocks of 8 megabyte so get multiple values at
	 * once - unless some colums are further apart than 8 megabyte. (for
	 * variable length text elements this cannot be guessed...)
	 * 
	 * @status: TODO
	 */
	@Override
	public AbstractDataMatrixInstance<Object> getSubMatrixByOffset(int row, int nRows, int col, int nCols) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[][] getElements() throws MatrixException
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	public File getAsFile() throws Exception {
		return bin;
	}
	
	@Override
	public void addColumn() throws Exception {
		throw new Exception("Action not possible");
	}

	@Override
	public void addRow() throws Exception {
		throw new Exception("Action not possible");
	}

	@Override
	public void updateElement() throws Exception {
		throw new Exception("Action not possible");
	}

}