//package org.molgenis.matrix;
//
//import java.io.DataInputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.RandomAccessFile;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.regex.Pattern;
//
//import org.apache.log4j.Logger;
//
//public class BinaryMatrix<E,A,V> extends MemoryMatrix<E,A,V>
//{
//	Logger logger = Logger.getLogger(getClass().getSimpleName());
//
//	private Class type = Double.class;
//	private int textElementLength;
//	private int startOfElementsPointer;
//	private byte[] textDataElementLengths;
//	private File bin;
//	private String nullChar;
//	private Pattern nullCharPattern;
//	String name;
//	String investigation_name;
//	String feature_type;
//	String target_type;
//	String value_type;
//
//	public BinaryMatrix(File bin) throws Exception
//	{
//		this.setBin(bin);
//
//		FileInputStream fis;
//		DataInputStream dis;
//
//		fis = new FileInputStream(bin);
//		dis = new DataInputStream(fis);
//
//		int startOfElements = 0;
//
//		// first 'Data' object metadata contained in the bin file
//
//		this.setNullChar(readNextChars(dis, 1));
//		
//		name = readNextChars(dis, dis.readUnsignedByte());
//		investigation_name = readNextChars(dis, dis.readUnsignedByte());
//		feature_type = readNextChars(dis, dis.readUnsignedByte());
//		target_type = readNextChars(dis, dis.readUnsignedByte());
//		value_type = dis.readBoolean() == true ? "Decimal"
//				: "Text";
//		int rowCount = dis.readInt();
//		int colCount = dis.readInt();
//
//		startOfElements += 1;
//		startOfElements += name.length() + 1;
//		startOfElements += investigation_name.length() + 1;
//		startOfElements += feature_type.length() + 1;
//		startOfElements += target_type.length() + 1;
//		startOfElements += 1 + 4 + 4;
//
//		// now the information contained within the actual matrix file
//
//		int[] colNameLengths = new int[colCount];
//		int[] rowNameLengths = new int[rowCount];
//
//		for (int i = 0; i < colNameLengths.length; i++)
//		{
//			colNameLengths[i] = dis.readUnsignedByte();
//		}
//
//		for (int i = 0; i < rowNameLengths.length; i++)
//		{
//			rowNameLengths[i] = dis.readUnsignedByte();
//		}
//
//		startOfElements += colNameLengths.length;
//		startOfElements += rowNameLengths.length;
//
//		ArrayList<String> colNames = new ArrayList<String>(this.getColCount());
//		ArrayList<String> rowNames = new ArrayList<String>(this.getRowCount());
//
//		for (int i = 0; i < this.getColCount(); i++)
//		{
//			colNames.add(i, readNextChars(dis, colNameLengths[i]));
//			startOfElements += colNameLengths[i];
//		}
//
//		for (int i = 0; i < this.getRowCount(); i++)
//		{
//			rowNames.add(i, readNextChars(dis, rowNameLengths[i]));
//			startOfElements += rowNameLengths[i];
//		}
//
//		this.setColNames(colNames);
//		this.setRowNames(rowNames);
//
//		if (getValueType().equals(String.class))
//		{
//			this.setTextElementLength(dis.readUnsignedByte());
//			logger.debug("this.getTextElementLength() = "
//					+ this.getTextElementLength());
//			startOfElements += 1;
//			if (this.getTextElementLength() == 0)
//			{
//				byte[] textDataElementLengths = new byte[this.getColCount()
//						* this.getRowCount()];
//				dis.read(textDataElementLengths);
//				startOfElements += textDataElementLengths.length;
//				this.setTextDataElementLengths(textDataElementLengths);
//
//			}
//		}
//
//		// now prepare for random access querying
//		this.startOfElementsPointer = startOfElements;
//		this.setNullCharPattern(Pattern.compile(this.getNullChar() + "+"));
//
//	}
//
//	public Double readNextDoubleFromRAF(RandomAccessFile raf)
//			throws IOException
//	{
//		byte[] arr = new byte[8];
//		raf.read(arr);
//		double d = byteArrayToDouble(arr);
//		if (d == Double.MAX_VALUE)
//		{
//			return null;
//		}
//		return d;
//	}
//
//	public Double[] readNextDoublesFromRAF(RandomAccessFile raf, int nr)
//			throws IOException
//	{
//		byte[] arr = new byte[nr * 8];
//		raf.read(arr);
//		return byteArrayToDoubles(arr);
//	}
//
//	public static Double[] byteArrayToDoubles(byte[] arr)
//	{
//		int nr = arr.length / 8;
//		Double[] res = new Double[nr];
//		for (int i = 0; i < arr.length; i += 8)
//		{
//			long longBits = 0;
//			for (int j = 0; j < 8; j++)
//			{
//				longBits <<= 8;
//				longBits |= (long) arr[i + j] & 255;
//			}
//			double d = Double.longBitsToDouble(longBits);
//			if (d == Double.MAX_VALUE)
//			{
//				res[i / 8] = null;
//			}
//			else
//			{
//				res[i / 8] = d;
//			}
//		}
//		return res;
//	}
//
//	public static double byteArrayToDouble(byte[] arr)
//	{
//		long longBits = 0;
//		for (int i = 0; i < arr.length; i++)
//		{
//			longBits <<= 8;
//			longBits |= (long) arr[i] & 255;
//		}
//		return Double.longBitsToDouble(longBits);
//	}
//
//	public String readNextCharsFromRAF(RandomAccessFile raf, int stringLength)
//			throws IOException
//	{
//		byte[] string = new byte[stringLength];
//		raf.read(string);
//
//		// FIXME: little experiment..
//		// byte[] newStr = new byte[string.length];
//		// int count = 0;
//		// for(byte b : string){
//		// newStr[count] = (byte) (b - 100);
//		// count++;
//		// }
//		// String result = new String(newStr);
//		String result = new String(string);
//
//		if (this.getNullCharPattern().matcher(result).matches())
//		{
//			result = "";
//		}
//		return result;
//	}
//
//	public String readNextChars(DataInputStream dis, int stringLength)
//			throws IOException
//	{
//		byte[] string = new byte[stringLength];
//		dis.read(string);
//		return new String(string);
//	}
//
//	@Override
//	public V[] getCol(int colindex) throws MatrixException
//	{
//		V[] result = create(this.getRowCount());
//
//		try
//		{
//			RandomAccessFile raf;
//			raf = new RandomAccessFile(this.getBin(), "r");
//
//			if (this.getValueType().equals("Decimal"))
//			{
//				for (int i = 0; i < result.length; i++)
//				{
//					raf.seek(this.startOfElementsPointer + (colindex * 8)
//							+ (i * 8 * this.getRowCount()));
//					// result[i] = raf.readDouble();
//					result[i] = (E) readNextDoubleFromRAF(raf);
//				}
//			}
//			else
//			{
//				if (this.getTextElementLength() != 0)
//				{
//					for (int i = 0; i < result.length; i++)
//					{
//						raf.seek(this.startOfElementsPointer
//								+ (colindex * this.getTextElementLength())
//								+ (i * this.getTextElementLength() * this
//										.getColCount()));
//						result[i] = (E) readNextCharsFromRAF(raf, this
//								.getTextElementLength());
//					}
//				}
//				else
//				{
//					long bytePos = 0;
//					int nextindex = 0;
//					int lastindex = 0;
//					for (int i = 0; i < result.length; i++)
//					{
//						nextindex = colindex + (i * this.getColCount());
//						for (int j = lastindex; j < nextindex; j++)
//						{
//							bytePos += this.getTextDataElementLengths()[j];
//						}
//						lastindex = nextindex + 1;
//						raf.seek(this.startOfElementsPointer + bytePos);
//						byte elementLength = this.getTextDataElementLengths()[colindex
//								+ (i * this.getColCount())];
//						result[i] = (E) readNextCharsFromRAF(raf, elementLength);
//						bytePos += elementLength;
//					}
//				}
//			}
//			raf.close();
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//			throw new MatrixException(e.getMessage());
//		}
//
//		return result;
//	}
//
//	@Override
//	public E[] getRow(int rowindex) throws MatrixException
//	{
//		E[] result = create(this.getColCount());
//
//		try
//		{
//			RandomAccessFile raf = new RandomAccessFile(this.getBin(), "r");
//
//			if (this.getValueType().equals("Decimal"))
//			{
//				raf.seek(this.startOfElementsPointer
//						+ (rowindex * this.getColCount() * 8));
//				for (int i = 0; i < result.length; i++)
//				{
//					// result[i] = raf.readDouble();
//					result[i] = (E) readNextDoubleFromRAF(raf);
//				}
//			}
//			else
//			{
//				if (this.getTextElementLength() != 0)
//				{
//					raf.seek(this.startOfElementsPointer
//							+ (rowindex * this.getColCount() * this
//									.getTextElementLength()));
//					for (int i = 0; i < result.length; i++)
//					{
//						result[i] = (E) readNextCharsFromRAF(raf, this
//								.getTextElementLength());
//					}
//				}
//				else
//				{
//					int startIndex = rowindex * this.getColCount();
//					long byteOffset = 0;
//					for (int i = 0; i < startIndex; i++)
//					{
//						byteOffset += this.getTextDataElementLengths()[i];
//					}
//					raf.seek(this.startOfElementsPointer + byteOffset);
//					for (int i = 0; i < result.length; i++)
//					{
//						result[i] = (E) readNextCharsFromRAF(raf, this
//								.getTextDataElementLengths()[startIndex + i]);
//					}
//				}
//			}
//			raf.close();
//
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//			throw new MatrixException(e.getMessage());
//		}
//		return result;
//	}
//
//	@Override
//	public E getValue(int rowindex, int colindex) throws MatrixException
//	{
//
//		E result = null;
//
//		try
//		{
//			RandomAccessFile raf = new RandomAccessFile(this.getBin(), "r");
//
//			int startIndex = (rowindex * this.getColCount()) + colindex;
//			if (this.getValueType().equals("Decimal"))
//			{
//				raf.seek(this.startOfElementsPointer + (startIndex * 8));
//				// result = raf.readDouble();
//				result = (E) readNextDoubleFromRAF(raf);
//			}
//			else
//			{
//				if (this.getTextElementLength() != 0)
//				{
//					raf.seek(this.startOfElementsPointer
//							+ (startIndex * this.getTextElementLength()));
//					result = (E) readNextCharsFromRAF(raf, this
//							.getTextElementLength());
//				}
//				else
//				{
//					long byteOffset = 0;
//					for (int i = 0; i < startIndex; i++)
//					{
//						byteOffset += this.getTextDataElementLengths()[i];
//					}
//					raf.seek(this.startOfElementsPointer + byteOffset);
//					result = (E) readNextCharsFromRAF(raf, this
//							.getTextDataElementLengths()[startIndex]);
//				}
//			}
//			raf.close();
//
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//			throw new MatrixException(e.getMessage());
//		}
//
//		return result;
//	}
//
//	@Override
//	public Matrix<E> getSubMatrixByIndex(List<Integer> rowIndices, List<Integer> colIndices)
//			throws MatrixException
//	{
//
//		E[][] elements = create(rowIndices.size(), colIndices.size());
//
//		// fill elements
//		try
//		{
//			RandomAccessFile raf = new RandomAccessFile(this.getBin(), "r");
//
//			int rowCount = 0;
//			int colCount = 0;
//
//			if (this.getValueType().equals(Double.class))
//			{
//				for (int rowindex : rowIndices)
//				{
//					for (int colindex : colIndices)
//					{
//						int index = (rowindex * this.getColCount()) + colindex;
//						raf.seek(this.startOfElementsPointer + (index * 8));
//						elements[rowCount][colCount] = (E) readNextDoubleFromRAF(raf);
//						colCount++;
//					}
//					rowCount++;
//					colCount = 0;
//				}
//			}
//			else
//			{
//				if (this.getTextElementLength() != 0)
//				{
//					for (int rowIndex : rowIndices)
//					{
//						for (int colIndex : colIndices)
//						{
//							int index = (rowIndex * this.getColCount())
//									+ colIndex;
//							raf.seek(this.startOfElementsPointer
//									+ (index * this.getTextElementLength()));
//							elements[rowCount][colCount] = (E) readNextCharsFromRAF(
//									raf, this.getTextElementLength());
//							colCount++;
//						}
//						rowCount++;
//						colCount = 0;
//					}
//				}
//				else
//				{
//					for (int rowIndex : rowIndices)
//					{
//						for (int colIndex : colIndices)
//						{
//							int index = (rowIndex * this.getColCount())
//									+ colIndex;
//							long byteOffset = 0;
//							for (int i = 0; i < index; i++)
//							{
//								byteOffset += this.getTextDataElementLengths()[i];
//							}
//							raf.seek(this.startOfElementsPointer + byteOffset);
//							elements[rowCount][colCount] = (E) readNextCharsFromRAF(
//									raf,
//									this.getTextDataElementLengths()[index]);
//							colCount++;
//						}
//						rowCount++;
//						colCount = 0;
//					}
//				}
//			}
//
//			// end fill elements
//
//			List<String> rowNames = new ArrayList<String>();
//			List<String> colNames = new ArrayList<String>();
//
//			for (int rowIndex : rowIndices)
//			{
//				rowNames.add(this.getRowNames().get(rowIndex).toString());
//			}
//
//			for (int colIndex : colIndices)
//			{
//				colNames.add(this.getColNames().get(colIndex).toString());
//			}
//
//			raf.close();
//
//			return new MemoryMatrix<E,A,V>(rowNames, colNames, elements);
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//			throw new MatrixException(e.getMessage());
//		}
//	}
//
//	@Override
//	public Matrix<E,A,V> getSubMatrixByOffset(int row, int nrows, int col, int ncols)
//			throws MatrixException
//	{
//		V[][] elements = create(nrows, ncols);
//
//		// fill elements
//
//		try
//		{
//			RandomAccessFile raf = new RandomAccessFile(this.getBin(), "r");
//
//			int rowCount = 0;
//			int colCount = 0;
//
//			if (this.getValueType().equals(Double.class))
//			{
//				if (ncols == getColCount())
//				{
//					// no seeking between rows
//					int startIndex = (row * this.getColCount());
//					int amountOfDoubles = nrows * ncols;
//					raf.seek(this.startOfElementsPointer + (startIndex * 8));
//					Double[] res = readNextDoublesFromRAF(raf, amountOfDoubles);
//					for (int i = 0; i < res.length; i += ncols)
//					{
//						Double[] chunk = new Double[ncols];
//						for (int j = 0; j < ncols; j++)
//						{
//							chunk[j] = res[i + j];
//						}
//						elements[i / ncols] = (V[]) chunk;
//					}
//
//				}
//				else
//				{
//					// must use some seeking here!!! this is the old way still
//					// (like
//					// the rest of this implementation)
//					for (int rowIndex = row; rowIndex < row + nrows; rowIndex++)
//					{
//						int startIndex = (rowIndex * this.getColCount()) + col;
//						raf
//								.seek(this.startOfElementsPointer
//										+ (startIndex * 8));
//						for (int colIndex = col; colIndex < col + ncols; colIndex++)
//						{
//							elements[rowCount][colCount] = (E) readNextDoubleFromRAF(raf);
//							colCount++;
//						}
//						rowCount++;
//						colCount = 0;
//					}
//				}
//
//			}
//			else
//			{
//				if (this.getTextElementLength() != 0)
//				{
//					for (int rowIndex = row; rowIndex < row + nrows; rowIndex++)
//					{
//						int startIndex = (rowIndex * this.getColCount()) + col;
//						raf.seek(this.startOfElementsPointer
//								+ (startIndex * this.getTextElementLength()));
//						for (int colIndex = col; colIndex < col + ncols; colIndex++)
//						{
//							elements[rowCount][colCount] = (E) readNextCharsFromRAF(
//									raf, this.getTextElementLength());
//							colCount++;
//						}
//						rowCount++;
//						colCount = 0;
//					}
//				}
//				else
//				{
//					long byteOffset = 0;
//					int nextIndex = 0;
//					int lastIndex = 0;
//					int currentIndex = 0;
//					for (int rowIndex = row; rowIndex < row + nrows; rowIndex++)
//					{
//						nextIndex = (rowIndex * this.getColCount()) + col;
//						for (int i = lastIndex; i < nextIndex; i++)
//						{
//							byteOffset += this.getTextDataElementLengths()[i];
//						}
//						lastIndex = nextIndex + ncols;
//						raf.seek(this.startOfElementsPointer + byteOffset);
//						for (int colIndex = col; colIndex < col + ncols; colIndex++)
//						{
//							currentIndex = (rowIndex * this.getColCount())
//									+ colIndex;
//							byte elementLength = this
//									.getTextDataElementLengths()[currentIndex];
//							elements[rowCount][colCount] = (E) readNextCharsFromRAF(
//									raf, elementLength);
//							byteOffset += elementLength;
//							colCount++;
//						}
//						rowCount++;
//						colCount = 0;
//					}
//				}
//			}
//
//			// end fill elements
//
//			List<String> rowNames = getRowNames().subList(row, row + nrows);
//			List<String> colNames = getColNames().subList(col, col + ncols);
//
//			raf.close();
//
//			return new MemoryMatrix<E>(rowNames, colNames, elements);
//
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//			throw new MatrixException(e.getMessage());
//		}
//	}
//
//	public byte[] getTextDataElementLengths()
//	{
//		return textDataElementLengths;
//	}
//
//	private void setTextDataElementLengths(byte[] textDataElementLengths)
//	{
//		this.textDataElementLengths = textDataElementLengths;
//	}
//
//	public int getTextElementLength()
//	{
//		return textElementLength;
//	}
//
//	private void setTextElementLength(int textElementLength)
//	{
//		this.textElementLength = textElementLength;
//	}
//
//	// redundant with getAsFile(), but used internally
//	private File getBin()
//	{
//		return bin;
//	}
//
//	private void setBin(File bin)
//	{
//		this.bin = bin;
//	}
//
//	public String getNullChar()
//	{
//		return nullChar;
//	}
//
//	private void setNullChar(String nullChar)
//	{
//		this.nullChar = nullChar;
//	}
//
//	public Pattern getNullCharPattern()
//	{
//		return nullCharPattern;
//	}
//
//	private void setNullCharPattern(Pattern nullCharPattern)
//	{
//		this.nullCharPattern = nullCharPattern;
//	}
//
//	/**
//	 * TODO: Make generic getElements function that is used by this (or is this)
//	 * and by 'Matrix get(int,int,int,int)' Because the code is pretty much
//	 * duplicate right now. (done for safety)
//	 */
//	@Override
//	public E[][] getValues() throws MatrixException
//	{
//		int row = 0;
//		int col = 0;
//		int nrows = this.getRowCount();
//		int ncols = this.getColCount();
//		E[][] elements = create(nrows, ncols);
//
//		// fill elements
//
//		try
//		{
//			RandomAccessFile raf = new RandomAccessFile(this.getBin(), "r");
//
//			int rowCount = 0;
//			int colCount = 0;
//
//			if (this.getValueType().equals("Decimal"))
//			{
//				for (int rowIndex = row; rowIndex < row + nrows; rowIndex++)
//				{
//					int startIndex = (rowIndex * this.getColCount()) + col;
//					raf.seek(this.startOfElementsPointer + (startIndex * 8));
//					for (int colIndex = col; colIndex < col + ncols; colIndex++)
//					{
//						elements[rowCount][colCount] = (E) readNextDoubleFromRAF(raf);
//						colCount++;
//					}
//					rowCount++;
//					colCount = 0;
//				}
//			}
//			else
//			{
//				if (this.getTextElementLength() != 0)
//				{
//					for (int rowIndex = row; rowIndex < row + nrows; rowIndex++)
//					{
//						int startIndex = (rowIndex * this.getColCount()) + col;
//						raf.seek(this.startOfElementsPointer
//								+ (startIndex * this.getTextElementLength()));
//						for (int colIndex = col; colIndex < col + ncols; colIndex++)
//						{
//							elements[rowCount][colCount] = (E) readNextCharsFromRAF(
//									raf, this.getTextElementLength());
//							colCount++;
//						}
//						rowCount++;
//						colCount = 0;
//					}
//				}
//				else
//				{
//					long byteOffset = 0;
//					int nextIndex = 0;
//					int lastIndex = 0;
//					int currentIndex = 0;
//					for (int rowIndex = row; rowIndex < row + nrows; rowIndex++)
//					{
//						nextIndex = (rowIndex * this.getColCount()) + col;
//						for (int i = lastIndex; i < nextIndex; i++)
//						{
//							byteOffset += this.getTextDataElementLengths()[i];
//						}
//						lastIndex = nextIndex + ncols;
//						raf.seek(this.startOfElementsPointer + byteOffset);
//						for (int colIndex = col; colIndex < col + ncols; colIndex++)
//						{
//							currentIndex = (rowIndex * this.getColCount())
//									+ colIndex;
//							byte elementLength = this
//									.getTextDataElementLengths()[currentIndex];
//							elements[rowCount][colCount] = (E) readNextCharsFromRAF(
//									raf, elementLength);
//							byteOffset += elementLength;
//							colCount++;
//						}
//						rowCount++;
//						colCount = 0;
//					}
//				}
//			}
//
//			raf.close();
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//			throw new MatrixException(e.getMessage());
//		}
//
//		return elements;
//	}
//
//	public Class getValueType()
//	{
//		return type;
//	}
//}
