//package org.molgenis.matrix.component.legacy;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//import org.molgenis.framework.db.QueryRule.Operator;
//import org.molgenis.matrix.component.general.MatrixQueryRule;
//import org.molgenis.matrix.component.general.MatrixQueryRule.Type;
//
//
//public class MatrixRendererHelper<R, C, V> {
//	
//	public static final String MATRIX_COMPONENT_REQUEST_PREFIX = "matrix_component_request_prefix_";
//	public static final int ROW_STOP_DEFAULT = 10;
//	public static final int COL_STOP_DEFAULT = 5;
//	
//	public static final HashMap<String, String> operators() {
//		HashMap<String, String> ops = new HashMap<String, String>();
//		ops.put("GREATER", "&gt;");
//		ops.put("GREATER_EQUAL", "&gt;=");
//		ops.put("LESS", "&lt;");
//		ops.put("LESS_EQUAL", "&lt;=");
//		ops.put("EQUALS", "equals");
//		ops.put("SORTASC", "sort asc");
//		ops.put("SORTDESC", "sort desc");
//		ops.put("NOT", "is not");
//		//ops.put("LIMIT", "sort desc");
//		//ops.put("OFFSET", "sort desc");  ->  don't add here, just do by paging?
//		return ops;
//	}
//
//	public static List<MatrixQueryRule> copyMatrixQueryRuleList(List<MatrixQueryRule> original){
//		List<MatrixQueryRule> copy = new ArrayList<MatrixQueryRule>();
//		for(MatrixQueryRule f : original){
//			MatrixQueryRule q = new MatrixQueryRule(f.getFilterType(), f.getField(), f.getOperator(), f.getValue());
//			copy.add(q);
//		}
//		return copy;
//	}
//	
//	public static MatrixQueryRule getFilterWhere(List<MatrixQueryRule> filters, MatrixQueryRule.Type type, String field, Operator operator){
//		for (int filterIndex = 0; filterIndex < filters.size(); filterIndex++)
//		{
//			MatrixQueryRule f = filters.get(filterIndex);
//			if(f.getFilterType().equals(type) && f.getField().equals(field) && f.getOperator().equals(operator))
//			{
//				return f;
//			}
//		}
//		return null;
//	}
//	
//	public String colHeaderToStringForTest(RenderableMatrix<R, C, V> rm) throws Exception
//	{
//		StringBuffer result = new StringBuffer();
//		for (C col : rm.getColHeaders())
//		{
//			result.append(rm.getRenderDescriptor().renderColSimple(col)+" ");
//		}
//		return result.toString();
//	}
//	
//	public String rowHeaderToStringForTest(RenderableMatrix<R, C, V> rm) throws Exception
//	{
//		StringBuffer result = new StringBuffer();
//		for (R row : rm.getRowHeaders())
//		{
//			result.append(rm.getRenderDescriptor().renderRowSimple(row)+" ");
//		}
//		return result.toString();
//	}
//	
//	public String valuesToStringForTest(RenderableMatrix<R, C, V> rm) throws Exception
//	{
//		StringBuffer result = new StringBuffer();
//		V[][] elements = rm.getValues();
//		for (int rowIndex = 0; rowIndex < elements.length; rowIndex++)
//		{
//			for (int colIndex = 0; colIndex < elements[rowIndex].length; colIndex++)
//			{
//				result.append(rm.getRenderDescriptor().renderValue(elements[rowIndex][colIndex])+" ");
//			}
//		}
//		return result.toString();
//	}
//	
//	public String matrixToString(RenderableMatrix<R, C, V> rm)
//	{
//		StringBuffer result = new StringBuffer();
//		try
//		{
//			for (C col : rm.getColHeaders())
//			{
//				result.append("\t" + rm.getRenderDescriptor().renderColSimple(col));
//			}
//			result.append("\n");
//			V[][] elements = rm.getValues();
//			for (int rowIndex = 0; rowIndex < elements.length; rowIndex++)
//			{
//				result.append(rm.getRenderDescriptor().renderRowSimple(rm.getRowHeaders().get(rowIndex)));
//				for (int colIndex = 0; colIndex < elements[rowIndex].length; colIndex++)
//				{
//					if (elements[rowIndex][colIndex] == null)
//					{
//						result.append("\t");
//					}
//					else
//					{
//						result.append("\t" + rm.getRenderDescriptor().renderValue(elements[rowIndex][colIndex]));
//					}
//				}
//				result.append("\n");
//			}
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//		return result.toString();
//	}
//
//	
//}
