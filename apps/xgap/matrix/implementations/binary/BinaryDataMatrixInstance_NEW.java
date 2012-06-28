package matrix.implementations.binary;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import matrix.AbstractDataMatrixInstance;

import org.apache.log4j.Logger;
import org.molgenis.matrix.MatrixException;

public class BinaryDataMatrixInstance_NEW<E> extends BinaryDataMatrixInstance
{
	Logger logger = Logger.getLogger(getClass().getSimpleName());
	int HD_BLOCK_SIZE = 8000000; //FIXME: how to determine? whats best general size?

	/***********/
	/** CONSTRUCTOR */
	/***********/
	public BinaryDataMatrixInstance_NEW(File bin) throws Exception
	{
		super(bin);
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
	 * Output includes the nullcharacters so it is parseable with the text lenghts.
	 * @param raf
	 * @param elementAmount
	 * @param isDecimal
	 * @param elementLength
	 * @return
	 * @throws IOException
	 */
	public Object[] readBlock(RandomAccessFile raf, int elementAmount, boolean isDecimal, int elementLength) throws IOException
	{
		Object[] result = new Object[elementAmount];
		
		if(isDecimal)
		{
			byte[] arr = new byte[elementAmount * 8];
			raf.read(arr);
			return byteArrayToDoubles(arr);
		}
		else
		{
			if(elementLength == 0)
			{
				int totalBytes = elementAmount * elementLength;

				byte[] bytes = new byte[totalBytes];
				char[] chars = new char[totalBytes];

				raf.read(bytes);

				for (int i = 0; i < bytes.length; i++)
				{
					chars[i] = (char) bytes[i];
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
					result[i] = fromChars;
				}
				return result;
			}
			else
			{
				//read a big chunk and parse it out!!
				
				
			}
		}
	
		return result;
	}
	
	/**
	 * Primary function to read strings. Performs one read action from the hard drive.
	 */
	public Object[] readStringsFromRAF(RandomAccessFile raf, int elementAmount, int elementLength) throws IOException
	{
		Object[] result = new Object[elementAmount];
		int totalBytes = elementAmount * elementLength;

		byte[] bytes = new byte[totalBytes];
		char[] chars = new char[totalBytes];

		raf.read(bytes);

		for (int i = 0; i < bytes.length; i++)
		{
			chars[i] = (char) bytes[i];
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
//			if (this.getNullCharPattern().matcher(fromChars).matches())
//			{
//				result[i] = "";
//			}else{
				result[i] = fromChars;
//			}
		}
		return result;
	}

	/**
	 * Convert bytes to doubles
	 * @param arr
	 * @return
	 */
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
			raf.seek(this.getStartOfElementsPointer() + (startIndex * 8));
			result = readNextDoublesFromRAF(raf, 1)[0];
		}
		else
		{
			if (this.getTextElementLength() != 0)
			{
				raf.seek(this.getStartOfElementsPointer() + (startIndex * this.getTextElementLength()));
				result = readStringsFromRAF(raf, 1, this.getTextElementLength())[0];
			}
			else
			{
				long byteOffset = 0;
				for (int i = 0; i < startIndex; i++)
				{
					byteOffset += this.getTextDataElementLengths()[i];
				}
				raf.seek(this.getStartOfElementsPointer() + byteOffset);
				result = readStringsFromRAF(raf, 1, this.getTextDataElementLengths()[startIndex])[0];
				if(result.equals(this.getNullChar()))
				{
					result = "";
				}
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
			raf.seek(this.getStartOfElementsPointer() + (rowIndex * this.getNumberOfCols() * 8));
			result = readNextDoublesFromRAF(raf, result.length);
		}
		else
		{
			if (this.getTextElementLength() != 0)
			{
				raf.seek(this.getStartOfElementsPointer()
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
				raf.seek(this.getStartOfElementsPointer() + byteOffset);
				
				int diff = (int) (this.getEndOfElementsPointer() - (this.getStartOfElementsPointer() + byteOffset));
			
				// read the row as 1 big element
				Object[] tmpResult = readStringsFromRAF(raf, 1, diff);
				String rowLine = tmpResult[0].toString();
				System.out.println("ROWLINE: '" + rowLine+"'");
				//cut up the result
				int startAt = 0;
				for (int i = 0; i <  this.getNumberOfCols(); i++)
				{
					int elementLength = this.getTextDataElementLengths()[i+startIndex];
					result[i] = rowLine.substring(startAt, startAt+elementLength);
					if(this.getNullCharPattern().matcher(result[i].toString()).matches())
					{
						result[i] = "";
					}
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
			raf.seek(this.getStartOfElementsPointer() + (colIndex * 8));
			
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

}