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
	RandomAccessFile raf;
	long[] textDataElementLenghtsCumulative;

	/***********/
	/** CONSTRUCTOR */
	/***********/
	public BinaryDataMatrixInstance_NEW(File bin) throws Exception
	{
		super(bin);
		this.raf = new RandomAccessFile(this.getBin(), "r");
		
		if(this.getTextDataElementLengths() != null){
			this.textDataElementLenghtsCumulative = new long[this.getTextDataElementLengths().length+1];
			textDataElementLenghtsCumulative[0] = 0; //convenient when adding to pointer position
			long cumulative = 0;
			for(int i = 0; i < this.getTextDataElementLengths().length; i++)
			{
				cumulative = cumulative + this.getTextDataElementLengths()[i];
				textDataElementLenghtsCumulative[i+1] = cumulative;
			}
		}
	}


	/**
	 * General purpose function to read a chunk of data from a RandomAccessFile.
	 * @param startElement The element to start reading from
	 * @param elementAmount The amount of elements to read, sequentially, row-based
	 * @param replaceNulls If true, replace the nullChar with empty in Text data.
	 * @return Object[] The resulting list of elements
	 * @throws IOException
	 */
	private Object[] readBlock(int startElement, int elementAmount, boolean replaceNulls) throws IOException
	{
		Object[] result = new Object[elementAmount];
		
		if(this.getData().getValueType().equals("Decimal"))
		{
			int startPointer = this.getStartOfElementsPointer() + (startElement * 8);
		
			if(startPointer != raf.getFilePointer())
			{
				raf.seek(startPointer);
			}
			
			byte[] arr = new byte[elementAmount * 8];
			raf.read(arr);
			return byteArrayToDoubles(arr);
		}
		else
		{
			if(this.getTextElementLength() != 0)
			{
				int startPointer = this.getStartOfElementsPointer() + (startElement * this.getTextElementLength());
				
				if(startPointer != raf.getFilePointer())
				{
					raf.seek(startPointer);
				}
				
				//allocate array of the exact size
				int totalBytes = elementAmount * this.getTextElementLength();
				byte[] bytes = new byte[totalBytes];

				//single read action from raf
				raf.read(bytes);

				//cut up the result 
				for (int i = 0; i < elementAmount; i++)
				{
					int start = i * this.getTextElementLength();
					int stop = start + this.getTextElementLength();
					char[] subArr = new char[this.getTextElementLength()];
					int count = 0;
					for (int j = start; j < stop; j++)
					{
						subArr[count] = (char) bytes[j];
						count++;
					}
					String fromChars = new String(subArr);
					result[i] = fromChars;
					if(replaceNulls && result[i].equals(this.getNullChar()))
					{
						result[i] = "";
					}
				}
				return result;
			}
			else
			{
				
				long startPointer = this.getStartOfElementsPointer() + this.textDataElementLenghtsCumulative[startElement];
				
				if(startPointer != raf.getFilePointer())
				{
					raf.seek(startPointer);
				}
				
				//find out how many bytes we're going to read
				//allocate array of the exact size
				int totalBytes = (int) (this.textDataElementLenghtsCumulative[startElement+elementAmount] - this.textDataElementLenghtsCumulative[startElement]);
				byte[] bytes = new byte[totalBytes];
	
				
				//single read action from raf
				raf.read(bytes);
				
				//cut up the result 
				int stop = 0;
				for (int i = 0; i < elementAmount; i++)
				{
					int start = stop;
					stop = start + this.getTextDataElementLengths()[startElement+i];
					char[] subArr = new char[stop-start];
					int count = 0;
					for (int j = start; j < stop; j++)
					{
						subArr[count] = (char) bytes[j];
						count++;
					}
					String fromChars = new String(subArr);
					result[i] = fromChars;
					if(replaceNulls && result[i].equals(this.getNullChar()))
					{
						result[i] = "";
					}
				}
				return result;
			}
		}
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
	 * Get one element. Still fast if the elements are sequentially retrieved. (index increment of 1, by row)
	 */
	public Object getElement(int rowindex, int colindex) throws Exception
	{
		return readBlock((rowindex * this.getNumberOfCols()) + colindex, 1, true)[0];
	}

	@Override
	/**
	 * Get one row. Still fast if the rows are sequentially retrieved. (index increment of 1)
	 */
	public Object[] getRow(int rowIndex) throws Exception
	{
		return readBlock((rowIndex * this.getNumberOfCols()), this.getNumberOfCols(), true);
	}

	@Override
	/**
	 * Get one column.
	 * Special case of getSubMatrix(int[] rowIndices, int[] colIndices) where we want all rows and only 1 column.
	 */
	public Object[] getCol(int colIndex) throws Exception
	{
		int[] cols = new int[]{ colIndex };
		int[] rows = new int[this.getNumberOfRows()];
		for(int r = 0; r < this.getNumberOfRows(); r++)
		{
			rows[r] = r;
		}

		// submatrix should have only 1 column: get this
		// (from in memory implementation) and return
		return getSubMatrix(rows, cols).getCol(0);
	}

	/**
	 * Get a submatrix by 
	 */
	@Override
	public AbstractDataMatrixInstance<Object> getSubMatrix(int[] rowIndices, int[] colIndices) throws MatrixException
	{
		
		//find out the lowest row index in the list
		//in case it is not incremental
		int lowestRowIndex = Integer.MAX_VALUE;
		int highestRowIndex = Integer.MIN_VALUE;
		for(int i = 0; i < rowIndices.length; i ++)
		{
			if(rowIndices[i] < lowestRowIndex)
			{
				lowestRowIndex = rowIndices[i];
			}
			if(rowIndices[i] >= highestRowIndex) //TODO: correct?
			{
				highestRowIndex = rowIndices[i];
			}
		}
		
		//find out the lowest row index in the list
		//in case it is not incremental
		int lowestColIndex = Integer.MAX_VALUE;
		int highestColIndex = Integer.MIN_VALUE;
		for(int i = 0; i < colIndices.length; i ++)
		{
			if(colIndices[i] < lowestColIndex)
			{
				lowestColIndex = colIndices[i];
			}
			if(colIndices[i] >= highestColIndex) //TODO: correct?
			{
				highestColIndex = colIndices[i];
			}
		}
		
		int firstElement = (this.getNumberOfCols() * lowestRowIndex) + lowestColIndex;
		int lastElement = (this.getNumberOfCols() * highestRowIndex) + highestColIndex;
		
//		System.out.println("lowestRowIndex: " + lowestRowIndex);
//		System.out.println("lowestColIndex: " + lowestColIndex);
//		System.out.println("highestRowIndex: " + highestRowIndex);
//		System.out.println("highestColIndex: " + highestColIndex);
//		
//		System.out.println("firstElement: " + firstElement);
//		System.out.println("lastElement: " + lastElement);
		
		//int howManyBytesToRead = lastElement - firstElement;
		
		int howManyBytesToRead = -1;
		
		if(this.getData().getValueType().equals("Decimal"))
		{
			howManyBytesToRead = (lastElement - firstElement) * 8;
		}
		else
		{
			if(this.getTextElementLength() != 0)
			{
				howManyBytesToRead = (lastElement - firstElement) * this.getTextElementLength();
			}
			else
			{
				howManyBytesToRead = (int) (this.textDataElementLenghtsCumulative[lastElement] - this.textDataElementLenghtsCumulative[firstElement]); //TODO: correct?
			}
		}
		System.out.println("need to get " + howManyBytesToRead + " bytes");
		
		long halfOfFreeMem = (Runtime.getRuntime().freeMemory()/2);
		
		

		
		//long totalElementSize = this.getEndOfElementsPointer() - this.getStartOfElementsPointer();
		
		double readActions = 1;
		if(halfOfFreeMem > howManyBytesToRead)
		{
			System.out.println("enough memory to read complete chunk");
		}
		else
		{
			readActions = (double)howManyBytesToRead/(double)halfOfFreeMem;
		}
		
		System.out.println("need to read " + readActions + " times using " + halfOfFreeMem + " bytes of memory");
		
		//perform read actions
		//figure out if we are going to retrieve a requested element by performing this action
		//if not: skip RAF to the next element to be read
		//smart when: getting 1 column from a file with very long rows
		//or when getting e.g. the first and last row of a big file
		
		return null;
	}

	/**
	 * Get a submatrix by starts plus offsets.
	 * Special case of getSubMatrix(int[] rowIndices, int[] colIndices) where all indices are sequential.
	 */
	@Override
	public AbstractDataMatrixInstance<Object> getSubMatrixByOffset(int row, int nRows, int col, int nCols) throws Exception
	{
		int[] rows = new int[nRows];
		int[] cols = new int[nCols];
		
		int counter = 0;
		for(int r = row; r < row + nRows; r++)
		{
			rows[counter] = r;
			counter++;
		}
		counter = 0;
		for(int c = col; c < col + nCols; c++)
		{
			cols[counter] = c;
			counter++;
		}
		
		return this.getSubMatrix(rows, cols);
	}

	@Override
	public Object[][] getElements() throws MatrixException
	{
		int[] rows = new int[this.getNumberOfRows()];
		int[] cols = new int[this.getNumberOfCols()];
		
		for(int r = 0; r < this.getNumberOfRows(); r++)
		{
			rows[r] = r;
		}
		for(int c = 0; c < this.getNumberOfCols() ; c++)
		{
			cols[c] = c;
		}
		
		//should be a memorymatrix, meaning the getElements just returns current memory location
		return this.getSubMatrix(rows, cols).getElements();
	}

}