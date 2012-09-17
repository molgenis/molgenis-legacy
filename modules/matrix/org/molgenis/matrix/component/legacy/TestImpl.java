//package org.molgenis.matrix.component.legacy;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.molgenis.framework.db.QueryRule;
//import org.molgenis.framework.db.QueryRule.Operator;
//import org.molgenis.matrix.MatrixException;
//import org.molgenis.matrix.component.general.MatrixQueryRule;
//import org.molgenis.matrix.component.interfaces.BasicMatrix;
//import org.molgenis.matrix.component.interfaces.SliceableMatrix;
//import org.molgenis.pheno.ObservedValue;
//
//public class TestImpl extends AbstractSliceableMatrix<SomeRowType, SomeColType, SomeValueType> implements
//		BasicMatrix<SomeRowType, SomeColType, SomeValueType>, SourceMatrix<SomeRowType, SomeColType, SomeValueType>, RenderDescriptor<SomeRowType, SomeColType, SomeValueType>
//{
//	
//	public TestImpl()
//	{
//		originalRows = Helper.getSomeRows();
//		originalCols = Helper.getSomeColumns();
//	}
//
//	@Override
//	public SliceableMatrix<SomeRowType, SomeColType, SomeValueType> sliceByColProperty(
//			String property, Operator operator, Object value)
//	{
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public BasicMatrix<SomeRowType, SomeColType, SomeValueType> getResult() throws Exception
//	{
//		return this;
//	}
//
//	@Override
//	public String getRowType() throws Exception
//	{
//		return "Person";
//	}
//
//	@Override
//	public String getColType() throws Exception
//	{
//		return "Gene";
//	}
//
//	@Override
//	public String renderValue(SomeValueType value) throws Exception
//	{
//		return value.getValue().toString();
//	}
//
//	@Override
//	public String renderRow(SomeRowType row)
//	{
//		return "Name: " + row.getFirstName() + " " + row.getLastName() + ", lives in: " + row.getCity();
//	}
//
//	@Override
//	public String renderCol(SomeColType col)
//	{
//		return "Common name: " + col.getCommonName() + ", chromosome: " + col.getChromosome();
//	}
//
//	@Override
//	public String renderRowSimple(SomeRowType row)
//	{
//		return row.getFirstName();
//	}
//
//	@Override
//	public String renderColSimple(SomeColType col)
//	{
//		return col.getCommonName();
//	}
//	
//	@Override
//	public List<String> getRowPropertyNames()
//	{
//		List<String> attr = new ArrayList<String>();
//		attr.add("id");
//		attr.add("firstName");
//		attr.add("lastName");
//		attr.add("city");
//		attr.add("yearOfBirth");
//		return attr;
//	}
//	
//	public List<String> getColPropertyNames()
//	{
//		List<String> attr = new ArrayList<String>();
//		attr.add("id");
//		attr.add("commonName");
//		attr.add("ncbiId");
//		attr.add("keggId");
//		attr.add("bpPos");
//		attr.add("chromosome");
//		return attr;
//	}
//
//	@Override
//	public SomeValueType[][] getValues() throws MatrixException
//	{
//		SomeValueType[][] visibleValues = new SomeValueType[rowIndicesCopy.size()][colIndicesCopy.size()];
//		//equivalent: new List[rowCopy.size()][colCopy.size()];
//		
//		for(int visValRow = 0; visValRow < rowIndicesCopy.size(); visValRow++)
//		{
//			for(int visValCol = 0; visValCol < colIndicesCopy.size(); visValCol++)
//			{
//				SomeValueType val = new SomeValueType(rowCopy.get(visValRow).getId() * colCopy.get(visValCol).getId());
//				visibleValues[visValRow][visValCol] = val;
//			}
//		}
//		
//		return visibleValues;
//	}
//
//	@Override
//	public RenderDescriptor<SomeRowType, SomeColType, SomeValueType> getRenderDescriptor()
//			throws Exception {
//		return this;
//	}
//
//	@Override
//	public SliceableMatrix<SomeRowType, SomeColType, SomeValueType> sliceByRowValues(
//			int index, Operator operator, Object value) throws Exception
//	{
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public SliceableMatrix<SomeRowType, SomeColType, SomeValueType> sliceByColValues(
//			int index, Operator operator, Object value) throws Exception
//	{
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public SliceableMatrix<SomeRowType, SomeColType, SomeValueType> sliceByRowProperty(
//			String property, Operator operator, Object value)
//	{
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public SliceableMatrix<SomeRowType, SomeColType, SomeValueType> sliceByRowValues(
//			SomeRowType row, Operator operator, Object value) throws Exception
//	{
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public SliceableMatrix<SomeRowType, SomeColType, SomeValueType> sliceByColValues(
//			SomeColType col, Operator operator, Object value) throws Exception
//	{
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Integer getColCount() throws MatrixException
//	{
//		return this.getColHeaders().size();
//	}
//
//	@Override
//	public Integer getRowCount() throws MatrixException
//	{
//		return this.getRowHeaders().size();
//	}
//
//	@Override
//	public SliceableMatrix<SomeRowType, SomeColType, SomeValueType> slice(
//			MatrixQueryRule rule) throws MatrixException
//	{
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<String> getValuePropertyNames()
//	{
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void refresh()
//	{
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public SliceableMatrix<SomeRowType, SomeColType, SomeValueType> sliceByColValueProperty(
//			SomeColType col, String property, Operator operator, Object value)
//			throws MatrixException
//	{
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public SliceableMatrix<SomeRowType, SomeColType, SomeValueType> sliceByColValueProperty(
//			int colIndex, String property, Operator operator, Object value)
//			throws MatrixException
//	{
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public int getRowLimit()
//	{
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	@Override
//	public void setRowLimit(int rowLimit)
//	{
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public int getRowOffset()
//	{
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	@Override
//	public void setRowOffset(int rowOffset)
//	{
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public int getColLimit()
//	{
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	@Override
//	public void setColLimit(int colLimit)
//	{
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public int getColOffset()
//	{
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	@Override
//	public void setColOffset(int colOffset)
//	{
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public List<? extends SomeValueType>[][] getValueLists() throws MatrixException
//	{
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public SliceableMatrix<SomeRowType, SomeColType, SomeValueType> sliceByRowValueProperty(
//			SomeRowType row, String property, Operator operator, Object value)
//			throws MatrixException
//	{
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public SliceableMatrix<SomeRowType, SomeColType, SomeValueType> sliceByRowValueProperty(
//			int rowIndex, String property, Operator operator, Object value)
//			throws MatrixException
//	{
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//
//
//
//}