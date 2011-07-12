package org.molgenis.matrix;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.matrix.convertors.ValueConvertor;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReaderListener;
import org.molgenis.util.CsvWriter;
import org.molgenis.util.Tuple;

public class CsvMemoryMatrix<E,A,V> extends MemoryMatrix<E,A,V>
{
	// convertor to read the values
	private ValueConvertor<E> rowConvertor;
	private ValueConvertor<A> colConvertor;
	private ValueConvertor<V> valueConvertor;
	
	/**
	 * Copy constructor for CsvMatrix
	 * 
	 * @param rowConvertor
	 * @param colConvertor
	 * @param valueConvertor
	 * @param values
	 * @throws FileNotFoundException
	 * @throws MatrixException
	 */
	public CsvMemoryMatrix(ValueConvertor<E> rowConvertor,
			ValueConvertor<A> colConvertor, ValueConvertor<V> valueConvertor,
			Matrix<E, A, V> values) throws MatrixException
	{
		super(values);
		this.rowConvertor = rowConvertor;
		this.colConvertor = colConvertor;
		this.valueConvertor = valueConvertor;
	}

	/**
	 * Creates a MemoryMatrix from Csv file. It uses the convertors to convert
	 * rowheader, colunmnheaders and values
	 * 
	 * @param rowConvertor
	 * @param colConvertor
	 * @param valueConvertor
	 * @param f
	 * @throws FileNotFoundException
	 * @throws MatrixException
	 */
	public CsvMemoryMatrix(ValueConvertor<E> rowConvertor,
			ValueConvertor<A> colConvertor, ValueConvertor<V> valueConvertor,
			File f) throws FileNotFoundException, MatrixException
	{
		// put the rownames and colnames in parent
		try
		{
			this.rowConvertor = rowConvertor;
			this.colConvertor = colConvertor;
			this.valueConvertor = valueConvertor;
			CsvFileReader csvReader = new CsvFileReader(f);

			//load rowNames
			final List<E> rowNames = new ArrayList<E>();
			for (String s : csvReader.rownames())
				rowNames.add(this.rowConvertor.read(s));
			
			//load colNames
			final List<A> colNames = new ArrayList<A>();
			for (String s : csvReader.colnames().subList(1,
					csvReader.colnames().size()))
				colNames.add(this.colConvertor.read(s));
			
			//load values
			final V[][] values = this.create(rowNames.size(), colNames.size());
			csvReader.reset();
			csvReader.parse(new CsvReaderListener()
			{
				@Override
				public void handleLine(int line_number, Tuple tuple)
						throws Exception
				{
					for (int col = 0; col < tuple.size() - 1; col++)
					{
						if(col >= colNames.size()) throw new MatrixException("csv row longer than colnames");
						if( (line_number - 1) >= rowNames.size()) throw new MatrixException("csv rowcount longer than rownames");
						
						values[line_number - 1][col] = getValue(tuple, col + 1);
					}

				}
			});

			this.setRowNames(rowNames);
			this.setColNames(colNames);
			this.setValues(values);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new MatrixException(e.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	/** Helper method for unchecked cast*/
	private V getValue(Tuple tuple, int index)
	{
		return valueConvertor.read(tuple.getString(index));
	}
	
	public void write(CsvWriter writer) throws MatrixException
	{
		// NB this only works if names are unique!!!
		// set headers
		List<String> headers = new ArrayList<String>();
		for (A value : getColNames())
			headers.add(this.colConvertor.write(value));
		writer.setHeaders(headers);
		writer.writeHeader();
		for (E rowName : getRowNames())
		{
			writer.writeValue(this.rowConvertor.write(rowName));
			for (V value : getRowByName(rowName))
			{
				writer.writeSeparator();
				writer.writeValue(this.valueConvertor.write(value));
			}
			writer.writeEndOfLine();
		}

		writer.close();
	}
}
