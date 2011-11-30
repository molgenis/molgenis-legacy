package csvtobin.sources;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class MakeBinary
{
	
	private String nullChar = "\5";	
	
	/**
	 * Convert an input file into a binary matrix
	 * @param data
	 * @param db
	 * @param inputFile
	 * @param totalRows
	 * @param totalCols
	 * @throws Exception 
	 * @throws Exception
	 */
	public File makeBinaryBackend(Data data, File src, File dest, int totalRows, int totalCols) throws Exception
		
	{
		if(dest.exists()){
			throw new IOException("Destination file '" + dest.getName() + "' already exists");
		}
	
		FileOutputStream fos = new FileOutputStream(dest);
		final DataOutputStream dos = new DataOutputStream(fos);

		// 0) write nullCharacter
		dos.writeBytes(this.nullChar);

		// 1) properties belonging to the 'Data' object
		dos.writeByte(data.getName().length());
		dos.writeBytes(data.getName());

		dos.writeByte(data.getInvestigation_Name().length());
		dos.writeBytes(data.getInvestigation_Name());

		dos.writeByte(data.getFeatureType().length());
		dos.writeBytes(data.getFeatureType());

		dos.writeByte(data.getTargetType().length());
		dos.writeBytes(data.getTargetType());

		if (data.getValueType().equals("Decimal"))
		{
			dos.writeBoolean(true);
		}
		else
		{
			dos.writeBoolean(false);
		}

		dos.writeInt(totalCols);
		dos.writeInt(totalRows);

		// 2) matrix content specific properties
		CsvFileReader csvFile = new CsvFileReader(src);

		List<String> colNames = csvFile.colnames();
		List<String> rowNames = csvFile.rownames();

		// hack for xgap matrix datatype
		if (colNames.get(0).equals(""))
		{
			colNames.remove(0);
		}

		for (int i = 0; i < totalCols; i++)
		{
			dos.writeByte(colNames.get(i).length());
		}

		for (int i = 0; i < totalRows; i++)
		{
			dos.writeByte(rowNames.get(i).length());
		}

		for (int i = 0; i < totalCols; i++)
		{
			dos.writeBytes(colNames.get(i));
		}

		for (int i = 0; i < totalRows; i++)
		{
			dos.writeBytes(rowNames.get(i));
		}

		// information about text DataMatrix elements
		int textLength = -1;
		if (data.getValueType().equals("Text"))
		{
			textLength = elementLength(src);
			dos.writeByte(textLength);
			System.out.println("text DataMatrix element length: " + textLength);

			if (textLength == 0)
			{
				System.out.println("length zero, making variable length array");
				// determine lengths and write to binary
				byte[] textElementLenghts = getTextDataElementLengths(src, totalCols * totalRows);
				dos.write(textElementLenghts);
			}
		}
		System.out.println("Writing elements..");
		// writing the actual elements
		if (data.getValueType().equals("Text"))
		{
			writeBinaryTextElements(dos, src, textLength);
		}
		else
		{
			writeBinaryDecimalElements(dos, src);
		}
		
		return dest;
		
	}

	private long writeBinaryTextElements(final DataOutputStream dos, File inputFile, int textLength)
			throws FileNotFoundException, ParseException
	{
		long start = System.currentTimeMillis();
		// adjusting the NA string to text length, if this is a fixed length, it
		// does not break the special treatment that makes fixed length
		// efficient
		String naStringCreate = "";
		if (textLength == 0)
		{
			naStringCreate = nullChar;
		}
		else if (textLength > 0)
		{
			naStringCreate = "";
			for (int i = 0; i < textLength; i++)
			{
				naStringCreate += nullChar;
			}
		}
		final String naString = naStringCreate;
		
		try{
		new CsvFileReader(inputFile).parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple line) throws Exception
			{
				if (line_number != 0)
				{
					for (int columnIndex = 1; columnIndex < line.size(); columnIndex++)
					{
						if (line.getString(columnIndex) == null)
						{ // FIXME: null: because of a parsed missing value indicator..
							dos.writeBytes(naString);
						}
						else if (line.getString(columnIndex).equals(""))
						{
							dos.writeBytes(naString);
						}
						else
						{
							
							//FIXME: little experiment..
//							String str = line.getString(columnIndex);
//							byte[] arr = new byte[str.length()];
//							int count = 0;
//							for(char c : str.toCharArray()){
//								arr[count] = (byte) (c + 100);
//								count++;
//							}
//							dos.write(arr);
							dos.writeBytes(line.getString(columnIndex));
							
						}
					}
				}
			}
		});
		}catch(Exception e){
			throw new ParseException(e.getMessage(), 0);
		}
		long stop = System.currentTimeMillis();
		return stop - start;
	}

	private long writeBinaryDecimalElements(final DataOutputStream dos, File inputFile) throws FileNotFoundException, ParseException
	{
		long start = System.currentTimeMillis();
		try{
		new CsvFileReader(inputFile).parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple line) throws Exception
			{
				if (line_number != 0)
				{
					for (int columnIndex = 1; columnIndex < line.size(); columnIndex++)
					{
						if (line.getDouble(columnIndex) == null)
						{
							dos.writeDouble(Double.MAX_VALUE);
						}
						else
						{
							dos.writeDouble(line.getDouble(columnIndex));
						}
					}
				}
			}
		});
		}catch(Exception e){
			throw new ParseException(e.getMessage(), 0);
		}
		long stop = System.currentTimeMillis();
		return stop - start;
	}

	private byte[] getTextDataElementLengths(File inputFile, int totalElements) throws FileNotFoundException, ParseException
	{
		final byte[] textElementLenghts = new byte[totalElements];
		try{
		new CsvFileReader(inputFile).parse(new CsvReaderListener()
		{
			int index = 0;

			public void handleLine(int line_number, Tuple line) throws Exception
			{
				if (line_number != 0)
				{
					for (int columnIndex = 1; columnIndex < line.size(); columnIndex++)
					{
						if (line.getString(columnIndex) == null)
						{ // FIXME:
							// null??
							textElementLenghts[index] = (byte) 1;
						}
						else if (line.getString(columnIndex).equals(""))
						{
							textElementLenghts[index] = (byte) 1;
						}
						else
						{
							textElementLenghts[index] = (byte) line.getString(columnIndex).length();
						}
						index++;
					}
				}
			}
		});
		}catch (Exception e){
			throw new ParseException(e.getMessage(), 0);
		}
		return textElementLenghts;
	}

	/**
	 * Check if all TEXT elements in a matrix are of equal length. The first
	 * element is used to get the length, then each element after that must have
	 * the same length in order for this length to be returned.
	 * 
	 * @param inputFile
	 * @return
	 * @throws FileNotFoundException
	 * @throws ParseException 
	 * @throws Exception
	 */
	private int elementLength(File inputFile) throws FileNotFoundException, ParseException
	{
		final IntegerWrapper elementLength = new IntegerWrapper(0);

		try
		{
			new CsvFileReader(inputFile).parse(new CsvReaderListener()
			{
				public void handleLine(int line_number, Tuple line) throws Exception
				{
					if (line_number != 0)
					{
						for (int columnIndex = 1; columnIndex < line.size(); columnIndex++)
						{
							// get one element
							if (elementLength.get() == 0)
							{
								elementLength.set(line.getString(columnIndex) != null ? line.getString(columnIndex).length() : 0);
								System.out.println("First element, size: " + elementLength.get());
							}
							else
							{
								if (elementLength.get() != 
									(line.getString(columnIndex) != null ? line.getString(columnIndex).length() : 0)) //nullpointer ???
								{

									System.out.println("Element " + line.getString(columnIndex) + " is not of length "
											+ elementLength.get());
									System.out.println("Element of unequal size found, exiting from function by throwing error");
									elementLength.set(0);
									throw new ElementLengthException("Exiting from CsvFileReader...");
								}
							}
						}
					}
				}
			});
		}
		catch (ElementLengthException e)
		{
			// this is okay..
		}
		catch(Exception ex){
			throw new ParseException(ex.getMessage(), 0);
		}

		return elementLength.get();
	}
}
