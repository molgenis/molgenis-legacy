package org.molgenis.matrix;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.matrix.convertors.ValueConvertor;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReader;
import org.molgenis.util.CsvReaderListener;
import org.molgenis.util.CsvWriter;
import org.molgenis.util.TupleWriter;
import org.molgenis.util.Tuple;

/**
 * A simple matrix implementation that reads data from a file. Optionally, usage
 * of rownames/colnames during construction will retrieve only a subset of the
 * data.
 * 
 * Caveat: this will cause an out-of-memory error if the data file is too big.
 * (In future versions there will be a 'CsvCachedMatrix to deal with that').
 * 
 * Also, while the matrix is currently editable, the edits are only persisted
 * after calling 'write'. Discussion: should we use separate writer objects? Or
 * a new interface?
 * 
 * Variability of the different E,A,V parameters is sorted used ValueConvertor.
 * Idea is to merge this with the FieldType system of core molgenis.
 * 
 * The idea is to use subclasses to get rid of the E,A,V parameters for often
 * used combinations. See CsvDoubleMatrix for example.
 * 
 * @param <E>
 * @param <A>
 * @param <V>
 */
public class CsvMemoryMatrix<E, A, V> extends MemoryMatrix<E, A, V> {
	// convertor to read the values
	private ValueConvertor<E> rowConvertor;
	private ValueConvertor<A> colConvertor;
	private ValueConvertor<V> valueConvertor;

	/**
	 * Copy constructor for CsvMatrix. Copoies the values out of the other
	 * matrix into this matrix.
	 * 
	 * @param rowConvertor
	 * @param colConvertor
	 * @param valueConvertor
	 * @param values
	 * @throws FileNotFoundException
	 * @throws MatrixException
	 * @throws FileNotFoundException
	 */
	public CsvMemoryMatrix(ValueConvertor<E> rowConvertor,
			ValueConvertor<A> colConvertor, ValueConvertor<V> valueConvertor,
			Matrix<E, A, V> values, File file) throws MatrixException,
			FileNotFoundException {
		this(rowConvertor, colConvertor, valueConvertor,
				new CsvFileReader(file));
	}

	public CsvMemoryMatrix(ValueConvertor<E> rowConvertor,
			ValueConvertor<A> colConvertor, ValueConvertor<V> valueConvertor,
			Matrix<E, A, V> values, CsvReader reader) throws MatrixException {
		super(values);
		this.rowConvertor = rowConvertor;
		this.colConvertor = colConvertor;
		this.valueConvertor = valueConvertor;

	}

	/**
	 * Creates a MemoryMatrix from Csv file. It uses the convertors to convert
	 * rowheader, colunmnheaders and values. All row and column names are used.
	 * 
	 * @param rowConvertor
	 * @param colConvertor
	 * @param valueConvertor
	 * @param file
	 * @throws FileNotFoundException
	 * @throws MatrixException
	 */
	public CsvMemoryMatrix(final ValueConvertor<E> rowConvertor,
			final ValueConvertor<A> colConvertor,
			final ValueConvertor<V> valueConvertor, CsvReader csvReader)
			throws MatrixException {
		// put the rownames and colnames in parent
		try {
			this.rowConvertor = rowConvertor;
			this.colConvertor = colConvertor;
			this.valueConvertor = valueConvertor;

			// load rowNames
			final List<E> rowNames = new ArrayList<E>();
			for (String s : csvReader.rownames())
				rowNames.add(this.rowConvertor.read(s));

			// load colNames
			final List<A> colNames = new ArrayList<A>();
			for (String s : csvReader.colnames().subList(1,
					csvReader.colnames().size()))
				colNames.add(this.colConvertor.read(s));

			// load values
			final V[][] values = this.create(rowNames.size(), colNames.size(),
					valueConvertor.getValueType());
			csvReader.reset();
			csvReader.parse(new CsvReaderListener() {
				@Override
				public void handleLine(int line_number, Tuple tuple)
						throws Exception {
					for (int col = 0; col < tuple.size() - 1; col++) {
						if (col >= colNames.size())
							throw new MatrixException("new "
									+ this.getClass().getSimpleName()
									+ "() failed: csv row longer than colnames");
						if ((line_number - 1) >= rowNames.size())
							throw new MatrixException(
									"new "
											+ this.getClass().getSimpleName()
											+ "() failed:csv rowcount longer than rownames");

						values[line_number - 1][col] = valueConvertor
								.read(tuple.getString(col + 1));
					}

				}
			});

			this.setRowNames(rowNames);
			this.setColNames(colNames);
			this.setValues(values);
		} catch (Exception e) {
			e.printStackTrace();
			throw new MatrixException(e.getMessage());
		}
	}

	public void write(TupleWriter writer) throws Exception {
		// NB this only works if names are unique!!!
		// set headers
		List<String> headers = new ArrayList<String>();
		for (A value : getColNames())
			headers.add(this.colConvertor.write(value));
		writer.setHeaders(headers);
		writer.writeHeader();
		for (E rowName : getRowNames()) {
			writer.writeValue(this.rowConvertor.write(rowName));
			for (V value : getRowByName(rowName)) {
				if (writer instanceof CsvWriter)
					((CsvWriter) writer).writeSeparator();
				writer.writeValue(this.valueConvertor.write(value));
			}
			writer.writeEndOfLine();
		}

		writer.close();
	}

	// getters/setters
	public ValueConvertor<E> getRowConvertor() {
		return rowConvertor;
	}

	public void setRowConvertor(ValueConvertor<E> rowConvertor) {
		this.rowConvertor = rowConvertor;
	}

	public ValueConvertor<A> getColConvertor() {
		return colConvertor;
	}

	public void setColConvertor(ValueConvertor<A> colConvertor) {
		this.colConvertor = colConvertor;
	}

	public ValueConvertor<V> getValueConvertor() {
		return valueConvertor;
	}

	public void setValueConvertor(ValueConvertor<V> valueConvertor) {
		this.valueConvertor = valueConvertor;
	}
}
