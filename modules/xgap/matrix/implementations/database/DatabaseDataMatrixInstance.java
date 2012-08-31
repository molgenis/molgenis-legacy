package matrix.implementations.database;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import matrix.AbstractDataMatrixInstance;
import matrix.implementations.memory.MemoryDataMatrixInstance;

import org.molgenis.data.Data;
import org.molgenis.data.DecimalDataElement;
import org.molgenis.data.TextDataElement;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.matrix.MatrixException;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import decorators.NameConvention;

public class DatabaseDataMatrixInstance extends AbstractDataMatrixInstance<Object>
{
	private String dataElement = "DataElement";
	private String dimensionElement = "DimensionElement";
	private String rowString = "Target";
	private String rowIndexString = "TargetIndex";
	private String colString = "Feature";
	private String colIndexString = "FeatureIndex";
	private String valueString = "Value";
	private String dataString = "Data";
	private String decimalTypeString = "Decimal";
	private String textTypeString = "Text";

	private Database db;
	private String type;
	private int matrixId;

	public void setDatabase(Database db)
	{
		this.db = db;
	}

	public DatabaseDataMatrixInstance(Database db, Data data) throws Exception
	{

		this.db = db;
		this.type = data.getValueType();
		this.matrixId = data.getId().intValue();
		this.setData(data);

		// queryrules to get the right elements: match on data.id, row/col index
		// = 0, and sort by row/col
		QueryRule whereData = new QueryRule(dataString, Operator.EQUALS, data.getId());
		QueryRule whereRowIndex = new QueryRule(rowIndexString, Operator.EQUALS, "0");
		QueryRule orderByColIndex = new QueryRule(Operator.SORTASC, colIndexString);
		QueryRule whereColIndex = new QueryRule(colIndexString, Operator.EQUALS, "0");
		QueryRule orderByRowIndex = new QueryRule(Operator.SORTASC, rowIndexString);

		// dynamic query on type, eg. Text/Decimal
		List<? extends Entity> colDataElements = db.find(db.getClassForName(type + "DataElement"), whereData,
				whereRowIndex, orderByColIndex);
		List<? extends Entity> rowDataElements = db.find(db.getClassForName(type + "DataElement"), whereData,
				whereColIndex, orderByRowIndex);

		// grab the colnames and add to list
		List<String> colNames = new ArrayList<String>();
		for (Entity de : colDataElements)
		{
			colNames.add(de.get("feature_name").toString());
		}

		// grab the rownames and add to list
		List<String> rowNames = new ArrayList<String>();
		for (Entity de : rowDataElements)
		{
			rowNames.add(de.get("target_name").toString());
		}

		// set row/colnames and number of rows/cols
		this.setColNames(colNames);
		this.setNumberOfCols(colNames.size());

		this.setRowNames(rowNames);
		this.setNumberOfRows(rowNames.size());

		this.setData(data);
	}

	@Override
	public Object[] getCol(int colIndex) throws Exception
	{
		Object[] result = new Object[this.getRowNames().size()];
		if (type.equals(decimalTypeString))
		{
			List<DecimalDataElement> dbResult = db.query(DecimalDataElement.class).equals(dataString, matrixId)
					.equals(colIndexString, colIndex).sortASC(rowIndexString).find();
			for (int j = 0; j < dbResult.size(); j++)
			{
				result[j] = (Object) dbResult.get(j).getValue();
			}
		}
		else
		{
			List<TextDataElement> dbResult = db.query(TextDataElement.class).equals(dataString, matrixId)
					.equals(colIndexString, colIndex).sortASC(rowIndexString).find();
			for (int j = 0; j < dbResult.size(); j++)
			{
				result[j] = (Object) dbResult.get(j).getValue();
			}
		}
		return result;
	}

	@Override
	public Object getElement(int rowIndex, int colIndex) throws Exception
	{
		Object result;
		if (type.equals(decimalTypeString))
		{
			DecimalDataElement dbResult = db.query(DecimalDataElement.class).equals(dataString, matrixId)
					.equals(colIndexString, colIndex).equals(rowIndexString, rowIndex).find().get(0);
			result = (Object) dbResult.getValue();
		}
		else
		{
			TextDataElement dbResult = db.query(TextDataElement.class).equals(dataString, matrixId)
					.equals(colIndexString, colIndex).equals(rowIndexString, rowIndex).find().get(0);
			result = (Object) dbResult.getValue();
		}
		return result;
	}

	@Override
	public Object[][] getElements() throws MatrixException
	{
		try
		{
			String maxRowSql = String.format("SELECT MAX(" + rowIndexString + ") AS maxrow FROM " + type + dataElement
					+ " WHERE " + dataString + "=%s", matrixId);
			List<Tuple> rsList = db.sql(maxRowSql);
			int maxRow = -1;
			for (Tuple rs : rsList)
			{
				maxRow = rs.getInt("maxrow") + 1;
			}

			String maxColSql = String.format("SELECT MAX(" + colIndexString + ") AS maxcol FROM " + type + dataElement
					+ " WHERE " + dataString + "=%s", matrixId);
			rsList = db.sql(maxColSql);
			int maxCol = -1;
			for (Tuple rs : rsList)
			{
				maxCol = rs.getInt("maxcol") + 1;
			}

			String sql = String.format("SELECT " + rowIndexString + "," + colIndexString + "," + valueString + " FROM "
					+ type + dataElement + " WHERE " + dataString + "=%s", matrixId);
			rsList = db.sql(sql);

			Object[][] data = new Object[maxRow][maxCol];
			if (type.equals("Decimal"))
			{
				for (Tuple rs : rsList)
				{
					data[rs.getInt(rowIndexString)][rs.getInt(colIndexString)] = rs.getDouble(valueString);
				}
			}
			else
			{
				for (Tuple rs : rsList)
				{
					data[rs.getInt(rowIndexString)][rs.getInt(colIndexString)] = rs.getString(valueString);
				}
			}

			return data;
		}
		catch (Exception e)
		{
			throw new MatrixException(e);
		}
	}

	@Override
	public Object[] getRow(int rowIndex) throws Exception
	{
		Object[] result = new Object[this.getColNames().size()];
		if (type.equals(decimalTypeString))
		{
			List<DecimalDataElement> dbResult = db.query(DecimalDataElement.class).equals(dataString, matrixId)
					.equals(rowIndexString, rowIndex).sortASC(colIndexString).find();
			for (int j = 0; j < dbResult.size(); j++)
			{
				result[j] = (Object) dbResult.get(j).getValue();
			}
		}
		else
		{
			List<TextDataElement> dbResult = db.query(TextDataElement.class).equals(dataString, matrixId)
					.equals(rowIndexString, rowIndex).sortASC(colIndexString).find();
			for (int j = 0; j < dbResult.size(); j++)
			{
				result[j] = (Object) dbResult.get(j).getValue();
			}
		}
		return result;
	}

	@Override
	public AbstractDataMatrixInstance<Object> getSubMatrix(int[] rowIndices, int[] colIndices) throws MatrixException
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
				return getSubMatrixByOffset(rowIndices[0], rowIndices.length, colIndices[0], colIndices.length);
			}

			// regular way of retrieval, not too bad for database matrix (single
			// query)
			HashMap<Integer, Integer> rowIndexPositions = new HashMap<Integer, Integer>();
			HashMap<Integer, Integer> colIndexPositions = new HashMap<Integer, Integer>();

			Integer[] rowIndicesCastable = new Integer[rowIndices.length];
			Integer[] colIndicesCastable = new Integer[colIndices.length];

			for (int i = 0; i < rowIndices.length; i++)
			{
				rowIndexPositions.put(rowIndices[i], i);
				rowIndicesCastable[i] = rowIndices[i];
			}
			for (int i = 0; i < colIndices.length; i++)
			{
				colIndexPositions.put(colIndices[i], i);
				colIndicesCastable[i] = colIndices[i];
			}

			String sql = "SELECT " + rowIndexString + ", " + colIndexString + ", " + valueString + " FROM " + type
					+ dataElement + "";
			List<Tuple> rsList = db.sql(sql, new QueryRule(rowIndexString, Operator.IN, rowIndicesCastable),
					new QueryRule(colIndexString, Operator.IN, colIndicesCastable), new QueryRule(dataString,
							Operator.EQUALS, matrixId));

			Object[][] data = new Object[rowIndices.length][colIndices.length];

			if (type.equals(decimalTypeString))
			{
				for (Tuple rs : rsList)
				{
					data[rowIndexPositions.get(rs.getInt(rowIndexString))][colIndexPositions.get(rs
							.getInt(colIndexString))] = rs.getDouble(valueString);
				}
			}
			else
			{
				for (Tuple rs : rsList)
				{
					data[rowIndexPositions.get(rs.getInt(rowIndexString))][colIndexPositions.get(rs
							.getInt(colIndexString))] = rs.getString(valueString);
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

			return new MemoryDataMatrixInstance<Object>(rowNames, colNames, data, this.getData());
		}
		catch (Exception e)
		{
			throw new MatrixException(e);
		}
	}

	@Override
	public AbstractDataMatrixInstance<Object> getSubMatrixByOffset(int row, int rows, int col, int cols)
			throws Exception
	{

		String sql = String.format("SELECT " + rowIndexString + "," + colIndexString + "," + valueString + " FROM "
				+ type + dataElement + " WHERE " + rowIndexString + ">=%s AND " + rowIndexString + "<%s AND "
				+ colIndexString + ">=%s AND " + colIndexString + "<%s AND " + dataString + "=%s", row, row + rows,
				col, col + cols, matrixId);
		List<Tuple> rsList = db.sql(sql);

		Object[][] data = new Object[rows][cols];
		if (type.equals(decimalTypeString))
		{
			for (Tuple rs : rsList)
			{
				data[rs.getInt(rowIndexString) - row][rs.getInt(colIndexString) - col] = rs.getDouble(valueString);
			}
		}
		else
		{
			for (Tuple rs : rsList)
			{
				data[rs.getInt(rowIndexString) - row][rs.getInt(colIndexString) - col] = rs.getString(valueString);
			}
		}

		return new MemoryDataMatrixInstance<Object>(this.getRowNames().subList(row, row + rows), this.getColNames()
				.subList(col, col + cols), data, this.getData());
	}

	@Override
	public File getAsFile() throws Exception
	{
		File tmp = new File(System.getProperty("java.io.tmpdir") + File.separator
				+ NameConvention.escapeFileName(this.getData().getInvestigation_Name()) + "_"
				+ NameConvention.escapeFileName(this.getData().getName()) + ".txt");

		if (tmp.exists())
		{
			boolean deleteSuccess = tmp.delete();
			if (!deleteSuccess)
			{
				throw new Exception("Deletion of tmp file " + tmp.getAbsolutePath() + " failed.");
			}
		}

		boolean createTmp = tmp.createNewFile();
		if (!createTmp)
		{
			throw new Exception("Creation of tmp file " + tmp.getAbsolutePath() + " failed.");
		}
		PrintWriter out = new PrintWriter(tmp);
		this.writeToCsvWriter(out);
		out.close(); // FIXME: close 'out'?
		return tmp;
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
