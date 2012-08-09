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
import matrix.implementations.memory.MemoryDataMatrixInstance;

import org.apache.log4j.Logger;
import org.molgenis.data.Data;
import org.molgenis.matrix.MatrixException;

public class BinaryDataMatrixInstance extends
		AbstractDataMatrixInstance<Object>
{
	Logger logger = Logger.getLogger(getClass().getSimpleName());
	
	private int textElementLength;
	private int startOfElementsPointer;
	private int endOfElementsPointer; //FIXME: is this big enough? whats the approx. file size then?
	private byte[] textDataElementLengths;
	private File bin;
	private String nullChar;
	private Pattern nullCharPattern;

	public BinaryDataMatrixInstance(File bin) throws Exception
	{
		this.setBin(bin);

		FileInputStream fis;
		DataInputStream dis;

		fis = new FileInputStream(bin);
		dis = new DataInputStream(fis);

		Data dataDescription = new Data();

		int startOfElements = 0;

		// first 'Data' object metadata contained in the bin file

		this.setNullChar(readNextChars(dis, 1));
		dataDescription.setName(readNextChars(dis, dis.readUnsignedByte()));
		dataDescription.setInvestigation_Name(readNextChars(dis, dis.readUnsignedByte()));
		dataDescription.setFeatureType(readNextChars(dis, dis.readUnsignedByte()));
		dataDescription.setTargetType(readNextChars(dis, dis.readUnsignedByte()));
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
			colNames.add(i, readNextChars(dis, colNameLengths[i]));
			startOfElements += colNameLengths[i];
		}

		for (int i = 0; i < this.getNumberOfRows(); i++)
		{
			rowNames.add(i, readNextChars(dis, rowNameLengths[i]));
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
	}

	private Double readNextDoubleFromRAF(RandomAccessFile raf)
			throws IOException
	{
		byte[] arr = new byte[8];
		raf.read(arr);
		double d = byteArrayToDouble(arr);
		if (d == Double.MAX_VALUE)
		{
			return null;
		}
		return d;
	}

	private Double[] readNextDoublesFromRAF(RandomAccessFile raf, int nr)
			throws IOException
	{
		byte[] arr = new byte[nr * 8];
		raf.read(arr);
		return byteArrayToDoubles(arr);
	}

	private Double[] byteArrayToDoubles(byte[] arr)
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

	private double byteArrayToDouble(byte[] arr)
	{
		long longBits = 0;
		for (int i = 0; i < arr.length; i++)
		{
			longBits <<= 8;
			longBits |= (long) arr[i] & 255;
		}
		return Double.longBitsToDouble(longBits);
	}

	private String readNextCharsFromRAF(RandomAccessFile raf, int stringLength)
			throws IOException
	{
		byte[] string = new byte[stringLength];
		raf.read(string);

		// FIXME: little experiment..
		// byte[] newStr = new byte[string.length];
		// int count = 0;
		// for(byte b : string){
		// newStr[count] = (byte) (b - 100);
		// count++;
		// }
		// String result = new String(newStr);
		String result = new String(string);

		if (this.getNullCharPattern().matcher(result).matches())
		{
			result = "";
		}
		return result;
	}

	private String readNextChars(DataInputStream dis, int stringLength)
			throws IOException
	{
		byte[] string = new byte[stringLength];
		dis.read(string);
		return new String(string);
	}

	@Override
	public Object[] getCol(int colindex) throws Exception
	{
		RandomAccessFile raf;

		Object[] result = new Object[this.getNumberOfRows()];

		raf = new RandomAccessFile(this.getBin(), "r");

		if (this.getData().getValueType().equals("Decimal"))
		{
			for (int i = 0; i < result.length; i++)
			{
				raf.seek(this.startOfElementsPointer + (colindex * 8)
						+ (i * 8 * this.getNumberOfCols()));
				// result[i] = raf.readDouble();
				result[i] = readNextDoubleFromRAF(raf);
			}
		}
		else
		{
			if (this.getTextElementLength() != 0)
			{
				for (int i = 0; i < result.length; i++)
				{
					raf.seek(this.startOfElementsPointer
							+ (colindex * this.getTextElementLength())
							+ (i * this.getTextElementLength() * this
									.getNumberOfCols()));
					result[i] = readNextCharsFromRAF(raf,
							this.getTextElementLength());
				}
			}
			else
			{
				long bytePos = 0;
				int nextindex = 0;
				int lastindex = 0;
				for (int i = 0; i < result.length; i++)
				{
					nextindex = colindex + (i * this.getNumberOfCols());
					for (int j = lastindex; j < nextindex; j++)
					{
						bytePos += this.getTextDataElementLengths()[j];
					}
					lastindex = nextindex + 1;
					raf.seek(this.startOfElementsPointer + bytePos);
					byte elementLength = this.getTextDataElementLengths()[colindex
							+ (i * this.getNumberOfCols())];
					result[i] = readNextCharsFromRAF(raf, elementLength);
					bytePos += elementLength;
				}
			}
		}
		raf.close();
		return result;
	}

	@Override
	public Object[] getRow(int rowindex) throws Exception
	{
		Object[] result = new Object[this.getNumberOfCols()];
		RandomAccessFile raf = new RandomAccessFile(this.getBin(), "r");

		if (this.getData().getValueType().equals("Decimal"))
		{
			raf.seek(this.startOfElementsPointer
					+ (rowindex * this.getNumberOfCols() * 8));
			for (int i = 0; i < result.length; i++)
			{
				// result[i] = raf.readDouble();
				result[i] = readNextDoubleFromRAF(raf);
			}
		}
		else
		{
			if (this.getTextElementLength() != 0)
			{
				raf.seek(this.startOfElementsPointer
						+ (rowindex * this.getNumberOfCols() * this
								.getTextElementLength()));
				for (int i = 0; i < result.length; i++)
				{
					result[i] = readNextCharsFromRAF(raf,
							this.getTextElementLength());
				}
			}
			else
			{
				int startIndex = rowindex * this.getNumberOfCols();
				long byteOffset = 0;
				for (int i = 0; i < startIndex; i++)
				{
					byteOffset += this.getTextDataElementLengths()[i];
				}
				raf.seek(this.startOfElementsPointer + byteOffset);
				for (int i = 0; i < result.length; i++)
				{
					result[i] = readNextCharsFromRAF(raf,
							this.getTextDataElementLengths()[startIndex + i]);
				}
			}
		}
		raf.close();
		return result;
	}

	@Override
	public Object getElement(int rowindex, int colindex) throws Exception
	{
		Object result = new Object();

		RandomAccessFile raf = new RandomAccessFile(this.getBin(), "r");

		int startIndex = (rowindex * this.getNumberOfCols()) + colindex;
		if (this.getData().getValueType().equals("Decimal"))
		{
			raf.seek(this.startOfElementsPointer + (startIndex * 8));
			// result = raf.readDouble();
			result = readNextDoubleFromRAF(raf);
		}
		else
		{
			if (this.getTextElementLength() != 0)
			{
				raf.seek(this.startOfElementsPointer
						+ (startIndex * this.getTextElementLength()));
				result = readNextCharsFromRAF(raf, this.getTextElementLength());
			}
			else
			{
				long byteOffset = 0;
				for (int i = 0; i < startIndex; i++)
				{
					byteOffset += this.getTextDataElementLengths()[i];
				}
				raf.seek(this.startOfElementsPointer + byteOffset);
				result = readNextCharsFromRAF(raf,
						this.getTextDataElementLengths()[startIndex]);
			}
		}
		raf.close();

		return result;
	}

	@Override
	public AbstractDataMatrixInstance getSubMatrix(int[] rowIndices,
			int[] colIndices) throws MatrixException
	{
		try
		{
			// the optimized way: find out of indices form a single block
			// if so, used offset retrieval instead
			boolean offsetAble = true;
			for (int i = 0; i < rowIndices.length - 1; i++)
			{
				if (rowIndices[i] != (rowIndices[i + 1] + 1))
				{
					offsetAble = false;
					break;
				}

			}
			if (offsetAble)
			{
				for (int i = 0; i < colIndices.length - 1; i++)
				{
					if (colIndices[i] != (colIndices[i + 1] + 1))
					{
						offsetAble = false;
						break;
					}
				}
			}
			if (offsetAble)
			{
				return getSubMatrixByOffset(rowIndices[0], rowIndices.length,
						colIndices[0], colIndices.length);
			}

			// the usual way: get single elements at the crossing sections of
			// indices
			// very inefficient but always works
			AbstractDataMatrixInstance<Object> result = null;
			Object[][] elements = new Object[rowIndices.length][colIndices.length];
			RandomAccessFile raf = new RandomAccessFile(this.getBin(), "r");

			int rowCount = 0;
			int colCount = 0;

			if (this.getData().getValueType().equals("Decimal"))
			{
				for (int rowindex : rowIndices)
				{
					for (int colindex : colIndices)
					{
						int index = (rowindex * this.getNumberOfCols())
								+ colindex;
						raf.seek(this.startOfElementsPointer + (index * 8));
						elements[rowCount][colCount] = readNextDoubleFromRAF(raf);
						colCount++;
					}
					rowCount++;
					colCount = 0;
				}
			}
			else
			{
				if (this.getTextElementLength() != 0)
				{
					for (int rowIndex : rowIndices)
					{
						for (int colIndex : colIndices)
						{
							int index = (rowIndex * this.getNumberOfCols())
									+ colIndex;
							raf.seek(this.startOfElementsPointer
									+ (index * this.getTextElementLength()));
							elements[rowCount][colCount] = readNextCharsFromRAF(
									raf, this.getTextElementLength());
							colCount++;
						}
						rowCount++;
						colCount = 0;
					}
				}
				else
				{
					for (int rowIndex : rowIndices)
					{
						for (int colIndex : colIndices)
						{
							int index = (rowIndex * this.getNumberOfCols())
									+ colIndex;
							long byteOffset = 0;
							for (int i = 0; i < index; i++)
							{
								byteOffset += this.getTextDataElementLengths()[i];
							}
							raf.seek(this.startOfElementsPointer + byteOffset);
							elements[rowCount][colCount] = readNextCharsFromRAF(
									raf,
									this.getTextDataElementLengths()[index]);
							colCount++;
						}
						rowCount++;
						colCount = 0;
					}
				}
			}

			// end fill elements

			List<String> rowNames = new ArrayList<String>();
			List<String> colNames = new ArrayList<String>();

			for (int rowIndex : rowIndices)
			{
				rowNames.add(this.getRowNames().get(rowIndex).toString());
			}

			for (int colIndex : colIndices)
			{
				colNames.add(this.getColNames().get(colIndex).toString());
			}

			result = new MemoryDataMatrixInstance(rowNames, colNames, elements,
					this.getData());

			raf.close();

			return result;
		}
		catch (Exception e)
		{
			throw new MatrixException(e);
		}
	}

	@Override
	public AbstractDataMatrixInstance<Object> getSubMatrixByOffset(int row,
			int nrows, int col, int ncols) throws Exception
	{
		AbstractDataMatrixInstance<Object> result = null;
		Object[][] elements = new Object[nrows][ncols];

		// fill elements

		RandomAccessFile raf = new RandomAccessFile(this.getBin(), "r");

		int rowCount = 0;
		int colCount = 0;

		if (this.getData().getValueType().equals("Decimal"))
		{
			if (ncols == getNumberOfCols())
			{
				// no seeking between rows
				int startIndex = (row * this.getNumberOfCols());
				int amountOfDoubles = nrows * ncols;
				raf.seek(this.startOfElementsPointer + (startIndex * 8));
				Double[] res = readNextDoublesFromRAF(raf, amountOfDoubles);
				for (int i = 0; i < res.length; i += ncols)
				{
					Double[] chunk = new Double[ncols];
					for (int j = 0; j < ncols; j++)
					{
						chunk[j] = res[i + j];
					}
					elements[i / ncols] = chunk;
				}

			}
			else
			{
				// must use some seeking here!!! this is the old way still (like
				// the rest of this implementation)
				for (int rowIndex = row; rowIndex < row + nrows; rowIndex++)
				{
					int startIndex = (rowIndex * this.getNumberOfCols()) + col;
					raf.seek(this.startOfElementsPointer + (startIndex * 8));
					for (int colIndex = col; colIndex < col + ncols; colIndex++)
					{
						elements[rowCount][colCount] = readNextDoubleFromRAF(raf);
						colCount++;
					}
					rowCount++;
					colCount = 0;
				}
			}

		}
		else
		{
			if (this.getTextElementLength() != 0)
			{
				for (int rowIndex = row; rowIndex < row + nrows; rowIndex++)
				{
					int startIndex = (rowIndex * this.getNumberOfCols()) + col;
					raf.seek(this.startOfElementsPointer
							+ (startIndex * this.getTextElementLength()));
					for (int colIndex = col; colIndex < col + ncols; colIndex++)
					{
						elements[rowCount][colCount] = readNextCharsFromRAF(
								raf, this.getTextElementLength());
						colCount++;
					}
					rowCount++;
					colCount = 0;
				}
			}
			else
			{
				long byteOffset = 0;
				int nextIndex = 0;
				int lastIndex = 0;
				int currentIndex = 0;
				for (int rowIndex = row; rowIndex < row + nrows; rowIndex++)
				{
					nextIndex = (rowIndex * this.getNumberOfCols()) + col;
					for (int i = lastIndex; i < nextIndex; i++)
					{
						byteOffset += this.getTextDataElementLengths()[i];
					}
					lastIndex = nextIndex + ncols;
					raf.seek(this.startOfElementsPointer + byteOffset);
					for (int colIndex = col; colIndex < col + ncols; colIndex++)
					{
						currentIndex = (rowIndex * this.getNumberOfCols())
								+ colIndex;
						byte elementLength = this.getTextDataElementLengths()[currentIndex];
						elements[rowCount][colCount] = readNextCharsFromRAF(
								raf, elementLength);
						byteOffset += elementLength;
						colCount++;
					}
					rowCount++;
					colCount = 0;
				}
			}
		}

		// end fill elements

		List<String> rowNames = getRowNames().subList(row, row + nrows);
		List<String> colNames = getColNames().subList(col, col + ncols);

		result = new MemoryDataMatrixInstance(rowNames, colNames, elements,
				this.getData());

		raf.close();
		return result;
	}

	byte[] getTextDataElementLengths()
	{
		return textDataElementLengths;
	}

	void setTextDataElementLengths(byte[] textDataElementLengths)
	{
		this.textDataElementLengths = textDataElementLengths;
	}

	int getTextElementLength()
	{
		return textElementLength;
	}

	void setTextElementLength(int textElementLength)
	{
		this.textElementLength = textElementLength;
	}

	// redundant with getAsFile(), but used internally
	File getBin()
	{
		return bin;
	}

	void setBin(File bin)
	{
		this.bin = bin;
	}

	String getNullChar()
	{
		return nullChar;
	}

	void setNullChar(String nullChar)
	{
		this.nullChar = nullChar;
	}

	Pattern getNullCharPattern()
	{
		return nullCharPattern;
	}

	void setNullCharPattern(Pattern nullCharPattern)
	{
		this.nullCharPattern = nullCharPattern;
	}
	
	int getStartOfElementsPointer()
	{
		return startOfElementsPointer;
	}

	void setStartOfElementsPointer(int startOfElementsPointer)
	{
		this.startOfElementsPointer = startOfElementsPointer;
	}

	int getEndOfElementsPointer()
	{
		return endOfElementsPointer;
	}

	void setEndOfElementsPointer(int endOfElementsPointer)
	{
		this.endOfElementsPointer = endOfElementsPointer;
	}

	/**
	 * TODO: Make generic getElements function that is used by this (or is this)
	 * and by 'Matrix get(int,int,int,int)' Because the code is pretty much
	 * duplicate right now. (done for safety)
	 */
	@Override
	public Object[][] getElements() throws MatrixException
	{
		try
		{
			int row = 0;
			int col = 0;
			int nrows = this.getNumberOfRows();
			int ncols = this.getNumberOfCols();
			Object[][] elements = new Object[nrows][ncols];

			// fill elements

			RandomAccessFile raf = new RandomAccessFile(this.getBin(), "r");

			int rowCount = 0;
			int colCount = 0;

			if (this.getData().getValueType().equals("Decimal"))
			{
				for (int rowIndex = row; rowIndex < row + nrows; rowIndex++)
				{
					int startIndex = (rowIndex * this.getNumberOfCols()) + col;
					raf.seek(this.startOfElementsPointer + (startIndex * 8));
					for (int colIndex = col; colIndex < col + ncols; colIndex++)
					{
						elements[rowCount][colCount] = readNextDoubleFromRAF(raf);
						colCount++;
					}
					rowCount++;
					colCount = 0;
				}
			}
			else
			{
				if (this.getTextElementLength() != 0)
				{
					for (int rowIndex = row; rowIndex < row + nrows; rowIndex++)
					{
						int startIndex = (rowIndex * this.getNumberOfCols())
								+ col;
						raf.seek(this.startOfElementsPointer
								+ (startIndex * this.getTextElementLength()));
						for (int colIndex = col; colIndex < col + ncols; colIndex++)
						{
							elements[rowCount][colCount] = readNextCharsFromRAF(
									raf, this.getTextElementLength());
							colCount++;
						}
						rowCount++;
						colCount = 0;
					}
				}
				else
				{
					long byteOffset = 0;
					int nextIndex = 0;
					int lastIndex = 0;
					int currentIndex = 0;
					for (int rowIndex = row; rowIndex < row + nrows; rowIndex++)
					{
						nextIndex = (rowIndex * this.getNumberOfCols()) + col;
						for (int i = lastIndex; i < nextIndex; i++)
						{
							byteOffset += this.getTextDataElementLengths()[i];
						}
						lastIndex = nextIndex + ncols;
						raf.seek(this.startOfElementsPointer + byteOffset);
						for (int colIndex = col; colIndex < col + ncols; colIndex++)
						{
							currentIndex = (rowIndex * this.getNumberOfCols())
									+ colIndex;
							byte elementLength = this
									.getTextDataElementLengths()[currentIndex];
							elements[rowCount][colCount] = readNextCharsFromRAF(
									raf, elementLength);
							byteOffset += elementLength;
							colCount++;
						}
						rowCount++;
						colCount = 0;
					}
				}
			}

			raf.close();

			return elements;
		}
		catch (Exception e)
		{
			throw new MatrixException(e);
		}
	}

	@Override
	public File getAsFile() throws Exception
	{
		return bin;
	}

	@Override
	public void addColumn() throws Exception
	{
		throw new Exception("Action not possible");
	}

	@Override
	public void addRow() throws Exception
	{
		throw new Exception("Action not possible");
	}

	@Override
	public void updateElement() throws Exception
	{
		throw new Exception("Action not possible");
	}


}
