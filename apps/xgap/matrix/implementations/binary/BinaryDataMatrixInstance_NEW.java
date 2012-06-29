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
	 */
	public Object[] getCol(int colIndex) throws Exception
	{
		Object[] result = new Object[this.getNumberOfRows()];
		
		long halfOfFreeMem = (Runtime.getRuntime().freeMemory()/2);
		long totalElementSize = this.getEndOfElementsPointer() - this.getStartOfElementsPointer();
		
		if(halfOfFreeMem > totalElementSize)
		{
			System.out.println("enough memory to read complete file");
		}
		else
		{
			long sizeOfOneRow = (totalElementSize/this.getNumberOfRows()); //note: this is an estimation for variable text length

			if(halfOfFreeMem > sizeOfOneRow)
			{
				System.out.println("enough memory ("+halfOfFreeMem+") to hold one row ("+sizeOfOneRow+")");
				
				long howMany = halfOfFreeMem / sizeOfOneRow;
				System.out.println("--> in fact, " + howMany + " rows will fit in memory at once ("+((((double)howMany)/((double)this.getNumberOfRows()))*100)+"% of total)");
				
				if(howMany > this.getNumberOfRows())
				{
					System.out.println("that means we can get everything (ERROR, file didn't fit at first?!?!)");
				}

			}
			else
			{
				System.out.println("not enough memory: must seek for each col element");
			}
		}

		
		//516594840 = 516 meg

		
//		raf.seek(this.getEndOfElementsPointer()-8);
//		byte[] read = new byte[8];
//		raf.read(read);
//		System.out.println("READ: " + byteArrayToDoubles(read)[0]);
			
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
		//return readBlock(0, this.getNumberOfRows() * this.getNumberOfCols(), true);
		return null;
	}

}