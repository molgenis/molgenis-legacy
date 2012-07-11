package matrix.implementations.binary;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import matrix.AbstractDataMatrixInstance;
import matrix.DataMatrixInstance;
import matrix.implementations.memory.MemoryDataMatrixInstance;

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
	 * 
	 * @param arr
	 * @param indexInArr
	 * @param indexInMatrix
	 * @return
	 */
	private Object parseFromByteArr(byte[] arr, int indexInArr, int indexInMatrix)
	{
		
		if(this.getData().getValueType().equals("Decimal"))
		{
			long longBits = 0;
			for (int j = 0; j < 8; j++)
			{
				longBits <<= 8;
				longBits |= (long) arr[indexInArr + j] & 255;
			}
			double d = Double.longBitsToDouble(longBits);
			if (d == Double.MAX_VALUE)
			{
				return null;
			}
			else
			{
				return d;
			}
		}
		else
		{
			char[] subArr;
			if(this.getTextElementLength() != 0)
			{
				int start = indexInArr * this.getTextElementLength();
				int stop = start + this.getTextElementLength();
				subArr = new char[this.getTextElementLength()];
				int count = 0;
				for (int j = start; j < stop; j++)
				{
					subArr[count] = (char) arr[j];
					count++;
				}
				
			}
			else
			{
				subArr = new char[this.getTextDataElementLengths()[indexInMatrix]];
				int firstArrElementIndex = indexInMatrix-indexInArr;
				long arrayStartPos = this.textDataElementLenghtsCumulative[firstArrElementIndex];
				int elementStartPos =  (int) (this.textDataElementLenghtsCumulative[indexInMatrix] - arrayStartPos);
				int elementStopPos = elementStartPos+this.getTextDataElementLengths()[indexInMatrix];
				int count = 0;
				for (int j = elementStartPos; j < elementStopPos; j++)
				{
					subArr[count] = (char) arr[j];
					count++;
				}
				
			}
			String fromChars = new String(subArr);
			if(fromChars.equals(this.getNullChar()))
			{
				return "";
			}
			else
			{
				return fromChars;
			}
		}
	}
	
	/**
	 * Could be used for optimizing: skip parsing of ALL results when retrieving sparse data
	 * @param startElement
	 * @param elementAmount
	 * @param replaceNulls
	 * @return
	 * @throws IOException
	 */
	private byte[] readChunkNoParsing(int startElement, int elementAmount) throws IOException
	{
		long startPointer;
		int totalBytes;
		if(this.getData().getValueType().equals("Decimal"))
		{
			startPointer = this.getStartOfElementsPointer() + (startElement * 8);
			totalBytes = elementAmount * 8;
		}
		else
		{
			if(this.getTextElementLength() != 0)
			{
				startPointer = this.getStartOfElementsPointer() + (startElement * this.getTextElementLength());
				totalBytes = elementAmount * this.getTextElementLength();
				
			}
			else
			{
				startPointer = this.getStartOfElementsPointer() + this.textDataElementLenghtsCumulative[startElement];
				totalBytes = (int) (this.textDataElementLenghtsCumulative[startElement+elementAmount] - this.textDataElementLenghtsCumulative[startElement]);
			}
		}
		byte[] bytes = new byte[totalBytes];
		if(startPointer != raf.getFilePointer())
		{
			raf.seek(startPointer);
		}
		raf.read(bytes);
		return bytes;
	}

	/**
	 * General purpose function to read a chunk of data from a RandomAccessFile.
	 * @param startElement The element to start reading from
	 * @param elementAmount The amount of elements to read, sequentially, row-based
	 * @param replaceNulls If true, replace the nullChar with empty in Text data.
	 * @return Object[] The resulting list of elements
	 * @throws IOException
	 */
	private Object[] readChunk(int startElement, int elementAmount, boolean replaceNulls) throws IOException
	{
		Object[] result = new Object[elementAmount];
		
		if(this.getData().getValueType().equals("Decimal"))
		{
			int startPointer = this.getStartOfElementsPointer() + (startElement * 8);
		
			if(startPointer != raf.getFilePointer())
			{
				raf.seek(startPointer);
				System.out.println("ADJUSTED RAF POINTER TO " + startPointer);
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
					System.out.println("ADJUSTED RAF POINTER TO " + startPointer);
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
					System.out.println("ADJUSTED RAF POINTER TO " + startPointer);
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
		return readChunk((rowindex * this.getNumberOfCols()) + colindex, 1, true)[0];
	}

	@Override
	/**
	 * Get one row. Still fast if the rows are sequentially retrieved. (index increment of 1)
	 */
	public Object[] getRow(int rowIndex) throws Exception
	{
		return readChunk((rowIndex * this.getNumberOfCols()), this.getNumberOfCols(), true);
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
		//result
		Object[][] result = new Object[rowIndices.length][colIndices.length];
		
		//keys: the indices to retrieve, values: the original location of this index in the provided array
		//we sort the keys from low to high
		TreeMap<Integer, Integer> rowIndexPositions = new TreeMap<Integer, Integer>(new sortInt());
		TreeMap<Integer, Integer> colIndexPositions = new TreeMap<Integer, Integer>(new sortInt());
		
		for(int i = 0; i < rowIndices.length; i ++)
		{
			rowIndexPositions.put(rowIndices[i], i);
		}

		for(int i = 0; i < colIndices.length; i ++)
		{
			colIndexPositions.put(colIndices[i], i);
		}
		
		int lowestRowIndex = rowIndexPositions.firstKey();
		int highestRowIndex = rowIndexPositions.lastKey();
		
		int lowestColIndex = colIndexPositions.firstKey();
		int highestColIndex = colIndexPositions.lastKey();
		
		int firstElement = (this.getNumberOfCols() * lowestRowIndex) + lowestColIndex; //inclusive
		int lastElement = (this.getNumberOfCols() * highestRowIndex) + highestColIndex + 1; //exclusive
		
		int totalElements = lastElement - firstElement;
		
		System.out.println("row indices + return position:");
		for(Integer key : rowIndexPositions.keySet())
		{
			System.out.print(key + "->" + rowIndexPositions.get(key) + " ");
		}
		System.out.println();
		System.out.println("col indices + return position:");
		for(Integer key : colIndexPositions.keySet())
		{
			System.out.print(key + "->" + colIndexPositions.get(key) + " ");
		}
		System.out.println();
		System.out.println("lowestRowIndex: " + lowestRowIndex);
		System.out.println("highestRowIndex: " + highestRowIndex);
		System.out.println("lowestColIndex: " + lowestColIndex);
		System.out.println("highestColIndex: " + highestColIndex);
		System.out.println("nr of rows: " + this.getNumberOfRows());
		System.out.println("nr of columns: " + this.getNumberOfCols());
		System.out.println("in 2D: firstElement (incluse): " + firstElement);
		System.out.println("in 2D: lastElement (exclusive): " + lastElement);
		System.out.println("total elements we're going to read over (==last-first, but we might skip 'empty' chunks in the middle): " + totalElements);
		
		long memAlloc = (Runtime.getRuntime().freeMemory()/4); //25% of available memory for reading
		
		System.out.println("bytes of memory reserved for reading chunks: " + memAlloc);
		
		
		int elementLength;
		
		if(this.getData().getValueType().equals("Decimal"))
		{
			elementLength = 8;
		}
		else
		{
			if(this.getTextElementLength() != 0)
			{
				elementLength = this.getTextElementLength();
			}
			else
			{
				elementLength = -1; //we don't know yet for variable text element size
			}
		}
		
		int maxElementsToRead = (int)(memAlloc / elementLength);
		
		System.out.println("we can hold " + maxElementsToRead + " elements in memory");
				
		if(maxElementsToRead > totalElements)
		{
			System.out.println("OPTIMIZATION: maxElementsToRead ("+maxElementsToRead+") > totalElements ("+totalElements+"), adjusting maxElementsToRead to " + totalElements);
			maxElementsToRead = totalElements;
		}
	
		boolean done = false;
		int iterationCounter = 0;
		int currentStartElement = firstElement;
		
		while(!done)
		{
			System.out.println("iteration nr " + iterationCounter + ", currentStartElement: " + currentStartElement);
			iterationCounter++;
			
			//find out if we're going to get elements we want in the next read action
			//if not: seek the RAF and adjust start element!
			boolean skipChunkAndSeek = true;
			
			if(elementLength == -1)
			{
				System.out.println("VARTEXT: finding out maxElementsToRead range!");
				int cumuLength = 0;
				int iter = 0;
				for(int i = currentStartElement; i < this.getTextDataElementLengths().length; i ++)
				{
					if(cumuLength > memAlloc)
					{
						System.out.println("VARTEXT: cumuLength > memAlloc at cumuLength = " + cumuLength + ", iter/maxElementsToRead = " + iter + " ; BREAKING");
						break;
					}
					else if(iter > totalElements)
					{
						System.out.println("VARTEXT: iter > totalElements at cumuLength = " + cumuLength + ", iter/maxElementsToRead = " + iter + " ; BREAKING");
						break;
					}
					cumuLength += this.getTextDataElementLengths()[i];
					iter++;
				}
				maxElementsToRead = iter;
				System.out.println("VARTEXT: maxElementsToRead = " + maxElementsToRead);
			}
			
			for(int elementIndex = currentStartElement; elementIndex < currentStartElement+maxElementsToRead; elementIndex++)
			{
				int checkCol = elementIndex % this.getNumberOfCols();
				int checkRow = (currentStartElement - checkCol) / this.getNumberOfCols();
				
				//e.g. if we're at element 12 in a 5-col matrix, we check if colIndex 2 if part of the result, and so on
				//if there is at least one, we'll get the chunk
				if(colIndexPositions.containsKey(checkCol) && rowIndexPositions.containsKey(checkRow))
				{
					System.out.println("the coming chunk has data we want (col " + checkCol +", row "+checkRow+")");
					skipChunkAndSeek = false;
					break;
				}
			}
			
			try
			{
				if(skipChunkAndSeek)
				{
					System.out.println("NO DATA IN NEXT CHUNK - SKIPPING AND SEEKING");
					
					int currentColPos = currentStartElement % this.getNumberOfCols();
					int currentRowPos = (currentStartElement - currentColPos) / this.getNumberOfCols();
					
					int newColPos = -1;
					int newRowPos = -1;
					
					//special case: we need to get a column from the current row still
					if(currentColPos <= colIndexPositions.lastKey() && rowIndexPositions.containsKey(currentRowPos))
					{
						//skip columns (or the same column using ceilingKey() if currentColPos == colIndexPositions.lastKey()
						newColPos = colIndexPositions.ceilingKey(currentColPos);
						newRowPos = currentRowPos;
					}
					else
					{
						//skip rows (using higherKey()) and start at first wanted column in that row
						newColPos = colIndexPositions.firstKey();
						newRowPos = rowIndexPositions.higherKey(currentRowPos);
					}
					
					currentStartElement = (this.getNumberOfCols() * newRowPos) + newColPos;
					int newPointer = this.getStartOfElementsPointer() + (currentStartElement * elementLength);
					
					if(elementLength == -1)
					{
						newPointer = (int) (this.getStartOfElementsPointer() + this.textDataElementLenghtsCumulative[currentStartElement]);
						System.out.println("VARTEXT skip pointer to " + newPointer);
					}
					
					System.out.println("new currentStartElement = " + currentStartElement + " (at row " + newRowPos +", col " + newColPos+") seeking to " + newPointer);
					
					raf.seek(newPointer);

				}
				else
				{						
					//read the chunk
					System.out.println("reading from " + currentStartElement + " to " + (currentStartElement+maxElementsToRead));
					
					//Object[] elements = readChunk(currentStartElement, maxElementsToRead, false);
					byte[] elements = readChunkNoParsing(currentStartElement, maxElementsToRead);

					for(int i = 0; i < elements.length; i ++)
					{

						int inChunkColPos = (currentStartElement + i) % this.getNumberOfCols();
						int inChunkRowPos = (currentStartElement + i - inChunkColPos) / this.getNumberOfCols();
						
						if(colIndexPositions.containsKey(inChunkColPos) && rowIndexPositions.containsKey(inChunkRowPos))
						{
							
							System.out.println("match on colPos " + inChunkColPos + ", rowPos " + inChunkRowPos + " - assigning " + parseFromByteArr(elements, i, currentStartElement + i) + " to " + rowIndexPositions.get(inChunkRowPos) + "," + colIndexPositions.get(inChunkColPos) + " in result matrix");
							
							//map to the correct position in the output (usually the same, but could be different!)
							result[rowIndexPositions.get(inChunkRowPos)][colIndexPositions.get(inChunkColPos)] = parseFromByteArr(elements, i, currentStartElement + i);
						}
					}
					
					currentStartElement = currentStartElement + maxElementsToRead;
					
				}
			} 
			catch (IOException e)
			{
				throw new MatrixException(e);
			}
			
			if(currentStartElement >= lastElement)
			{
				System.out.println("quitting: " + currentStartElement + " >= " + lastElement);
				done = true;
			}
			else
			{
				System.out.println("not quitting: " + currentStartElement + " < " + lastElement);
			}
		}
		
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
		
		AbstractDataMatrixInstance dm = new MemoryDataMatrixInstance(rowNames, colNames, result,
				this.getData());
		
		return dm;
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
	
	private String printObjArr(Object[] arr)
	{
		String printMe = "";
		for(Object o : arr)
		{
			printMe += "'" + o.toString() + "', ";
		}
		return printMe;
	}

}

class sortInt implements Comparator<Integer>
{
	public int compare(Integer a, Integer b)
	{
		if (a.intValue() < b.intValue())
		{
			return -1;
		}
		else if (a.intValue() == b.intValue())
		{
			return 0;
		}
		else
		{
			return 1;
		}
	}
}