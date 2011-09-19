//package org.molgenis.matrix.component;
//
//import java.util.List;
//
//import org.molgenis.framework.db.QueryRule;
//import org.molgenis.framework.db.QueryRule.Operator;
//import org.molgenis.matrix.component.general.MatrixQueryRule;
//import org.molgenis.matrix.component.interfaces.BasicMatrix;
//import org.molgenis.matrix.component.interfaces.SliceableMatrix;
//
///**
// * This is a pilot of using elastic search to speed up data slicing in large
// * xgap.Data matrices. 
// * 
// * For this to work you must build an index first; a helper
// * method for that is included. Currently it only works with Data.storage =
// * "Database" and Data.valueType = "Text". Final design this should be like a
// * decorator around any Data.storage.
// */
//public class DataMatrixElasticSearch implements SliceableMatrix
//{
//	ObservationMatrix sourceMatrix;
//
//
//	@Override
//	public List getRowHeaders() throws Exception
//	{
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List getColHeaders() throws Exception
//	{
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List getRowIndices() throws Exception
//	{
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List getColIndices() throws Exception
//	{
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	
//
//	@Override
//	public Object[][] getValues() throws Exception
//	{
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//
//	@Override
//	public SliceableMatrix sliceByColIndex(Operator operator, int index)
//			throws Exception
//	{
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public SliceableMatrix sliceByRowIndex(Operator operator, int index)
//			throws Exception
//	{
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public SliceableMatrix sliceByRowOffsetLimit(int limit, int offset)
//			throws Exception
//	{
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public SliceableMatrix sliceByColOffsetLimit(int limit, int offset)
//			throws Exception
//	{
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public SliceableMatrix sliceByRowValues(int index, Operator operator,
//			Object value) throws Exception
//	{
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public SliceableMatrix sliceByRowValues(Object row, Operator operator,
//			Object value) throws Exception
//	{
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//
//	@Override
//	public SliceableMatrix sliceByColValues(int index, Operator operator,
//			Object value) throws Exception
//	{
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public SliceableMatrix sliceByColValues(Object col, Operator operator,
//			Object value) throws Exception
//	{
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	
//
//	@Override
//	public SliceableMatrix sliceByRowProperty(String property,
//			Operator operator, Object value)
//	{
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	
//
//	@Override
//	public SliceableMatrix sliceByColProperty(String property,
//			Operator operator, Object value) throws Exception
//	{
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public BasicMatrix getResult() throws Exception
//	{
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//
//}
