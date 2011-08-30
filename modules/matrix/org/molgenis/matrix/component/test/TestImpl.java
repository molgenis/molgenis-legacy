package org.molgenis.matrix.component.test;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.QueryRule;
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
	public SliceableMatrix<SomeRowType, SomeColType, SomeValueType> sliceByRowValues(QueryRule rule) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SliceableMatrix<SomeRowType, SomeColType, SomeValueType> sliceByColValues(MatrixQueryRule rule)
			throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SliceableMatrix<SomeRowType, SomeColType, SomeValueType> sliceByRowHeader(MatrixQueryRule rule)
			throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SliceableMatrix<SomeRowType, SomeColType, SomeValueType> sliceByColHeader(MatrixQueryRule rule)
			throws Exception
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
	public List<String> getRowHeaderFilterAttributes()
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
	public List<String> getColHeaderFilterAttributes()
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
	public SomeValueType[][] getVisibleValues() throws Exception
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
	public RenderDescriptor<SomeRowType, SomeColType, SomeValueType> getRenderDescriptor()
			throws Exception {
		return this;
	}

	
	

}