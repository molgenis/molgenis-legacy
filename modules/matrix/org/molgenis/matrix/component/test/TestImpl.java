package org.molgenis.matrix.component.test;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.matrix.component.general.AbstractSliceableMatrix;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.matrix.component.interfaces.BasicMatrix;
import org.molgenis.matrix.component.interfaces.RenderDescriptor;
import org.molgenis.matrix.component.interfaces.SliceableMatrix;
import org.molgenis.matrix.component.interfaces.SourceMatrix;

public class TestImpl extends AbstractSliceableMatrix<SomeRowType, SomeColType, SomeValueType> implements
		BasicMatrix<SomeRowType, SomeColType, SomeValueType>, SourceMatrix<SomeRowType, SomeColType, SomeValueType>, RenderDescriptor<SomeRowType, SomeColType, SomeValueType>
{
	
	public TestImpl()
	{
		originalRows = Helper.getSomeRows();
		originalCols = Helper.getSomeColumns();
	}

	@Override
	@Deprecated
	public SliceableMatrix<SomeRowType, SomeColType, SomeValueType> sliceByRowValues(QueryRule rule) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Deprecated
	public SliceableMatrix<SomeRowType, SomeColType, SomeValueType> sliceByColValues(MatrixQueryRule rule)
			throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Deprecated
	public SliceableMatrix<SomeRowType, SomeColType, SomeValueType> sliceByRowHeader(MatrixQueryRule rule)
			throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Deprecated
	public SliceableMatrix<SomeRowType, SomeColType, SomeValueType> sliceByColHeader(MatrixQueryRule rule)
			throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public SliceableMatrix<SomeRowType, SomeColType, SomeValueType> sliceByColProperty(
			String property, Operator operator, Object value)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BasicMatrix<SomeRowType, SomeColType, SomeValueType> getResult() throws Exception
	{
		return this;
	}

	@Override
	public String getRowType() throws Exception
	{
		return "Person";
	}

	@Override
	public String getColType() throws Exception
	{
		return "Gene";
	}

	@Override
	public String renderValue(SomeValueType value) throws Exception
	{
		return value.getValue().toString();
	}

	@Override
	public String renderRow(SomeRowType row)
	{
		return "Name: " + row.getFirstName() + " " + row.getLastName() + ", lives in: " + row.getCity();
	}

	@Override
	public String renderCol(SomeColType col)
	{
		return "Common name: " + col.getCommonName() + ", chromosome: " + col.getChromosome();
	}

	@Override
	public String renderRowSimple(SomeRowType row)
	{
		return row.getFirstName();
	}

	@Override
	public String renderColSimple(SomeColType col)
	{
		return col.getCommonName();
	}

	@Override
	@Deprecated
	public List<String> getRowHeaderFilterAttributes()
	{
		return this.getRowPropertyNames();
	}
	
	@Override
	public List<String> getRowPropertyNames()
	{
		List<String> attr = new ArrayList<String>();
		attr.add("id");
		attr.add("firstName");
		attr.add("lastName");
		attr.add("city");
		attr.add("yearOfBirth");
		return attr;
	}

	@Override
	@Deprecated
	public List<String> getColHeaderFilterAttributes()
	{
		return this.getColPropertyNames();
	}
	
	public List<String> getColPropertyNames()
	{
		List<String> attr = new ArrayList<String>();
		attr.add("id");
		attr.add("commonName");
		attr.add("ncbiId");
		attr.add("keggId");
		attr.add("bpPos");
		attr.add("chromosome");
		return attr;
	}

	@Override
	public SomeValueType[][] getValues() throws Exception
	{
		SomeValueType[][] visibleValues = new SomeValueType[rowIndicesCopy.size()][colIndicesCopy.size()];
		//equivalent: new List[rowCopy.size()][colCopy.size()];
		
		for(int visValRow = 0; visValRow < rowIndicesCopy.size(); visValRow++)
		{
			for(int visValCol = 0; visValCol < colIndicesCopy.size(); visValCol++)
			{
				SomeValueType val = new SomeValueType(rowCopy.get(visValRow).getId() * colCopy.get(visValCol).getId());
				visibleValues[visValRow][visValCol] = val;
			}
		}
		
		return visibleValues;
	}
	
	@Override
	@Deprecated
	public SomeValueType[][] getVisibleValues() throws Exception
	{
		return this.getValues();
	}

	@Override
	public RenderDescriptor<SomeRowType, SomeColType, SomeValueType> getRenderDescriptor()
			throws Exception {
		return this;
	}

	@Override
	public SliceableMatrix<SomeRowType, SomeColType, SomeValueType> sliceByRowValues(
			int index, Operator operator, Object value) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SliceableMatrix<SomeRowType, SomeColType, SomeValueType> sliceByColValues(
			int index, Operator operator, Object value) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SliceableMatrix<SomeRowType, SomeColType, SomeValueType> sliceByRowProperty(
			String property, Operator operator, Object value)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SliceableMatrix<SomeRowType, SomeColType, SomeValueType> sliceByRowValues(
			SomeRowType row, Operator operator, Object value) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SliceableMatrix<SomeRowType, SomeColType, SomeValueType> sliceByColValues(
			SomeColType col, Operator operator, Object value) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}





}