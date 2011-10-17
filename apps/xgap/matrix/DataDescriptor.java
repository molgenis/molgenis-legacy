//package matrix;
//
//import java.util.List;
//
//public interface DataDescriptor
//{
//
//	/**
//	 * Name of the matrix. In XGAP, this corresponds to Data.name. In AnimalDB
//	 * there is no name for it, but it would be useful to save selections under
//	 * a name.
//	 * 
//	 * @return
//	 */
//	public String getName();
//
//	/**
//	 * Name of the container of this matrix. For example, 'investigation' or
//	 * 'project'. In both XGAP and AnimalDB is would be 'Investigation'.
//	 * 
//	 * @return
//	 */
//	public String getContainerName();
//
//	/**
//	 * Value types of the columns. For example: String, String, Integer, XREF,
//	 * String. Special behaviour: if size = 1, and colSize > 1, type is applied
//	 * to ALL columns In XGAP, you would only have size = 1 with either String
//	 * or Double. In AnimalDB, you would have size > 1, with ANY molgenis field
//	 * type. (support for all is not tested) DISCUSSION: How to type this???
//	 */
//	public List<String> getColValueTypes();
//
//	/**
//	 * SEE getColValueTypes(). Except read 'rows'.
//	 */
//	public List<String> getRowValueTypes();
//
//	/**
//	 * Get the type of the columns
//	 */
//	public String getColType();
//
//	/**
//	 * Get the type of the rows. In AnimalDB is this an ObservationTarget, or
//	 * specific subclasses thereof such as Animal, or Actor. But if you have all
//	 * ObservationTargets, you can still apply a filter on the '__Type' field to
//	 * get the same result. In XGAP, this is ALSO one type, but usually not a
//	 * generic type such as ObservationTarget (but Marker, Individual, Sample,
//	 * etc)
//	 */
//	public String getRowType();
//
//}
