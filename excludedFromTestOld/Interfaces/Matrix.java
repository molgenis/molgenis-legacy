//package Interfaces;
//
//import java.io.OutputStream;
//import java.util.List;
//
//public interface Matrix<Row extends ObservableColumn, Column extends ObservableColumn> {
//	public Object[][] getData();
//	public MetaDataHelper getMetaData();
//	
//	public List<Column> getColumns();
//	public void setColumns(List<Column> columns);
//	
//	public List<Row> getRows();
//	public void setRows(List<Row> rows);
//
//	public int getRowCount();
//	public int getColumnCount(); 
//	
//	public void loadData();
//}
//
//public interface MetaDataHelper {
//	
//	public MetaData get(int row, int column);
//	public MetaData get(String row, String column);
//}
//
////flyweigth pattern 
//public class MetaImpl implements MetaDataHelper {
//	List<Column> columns;
//	List<Row> rows;
//	
//	public static MetaData get(int row, int column) {
//		return new MataData(rows colums.get(row).getName(), );
//	}
//}
//
//public void MetaData {
//	Timestamp t0;
//	Timestamp t1;
//
//	ObservableElement row;
//	ObservableElement column;
//}
//
////To support column pageing
//public interface ColumnPageable<Data, Column, Row> extends Matrix<Data, Column, Row> {
//	void setColumnPageSize(int pageSize);
//	void setColumnPage(int index);
//	int getCurrentColumnPageIndex();
//	void nextColumnPage();
//	void prevColumnPage();
//	void getFirstColumnPage();
//	void getLastColumnPage();
//}
//
////To support row pageing
//public interface RowPageable<Data, Column, Row> extends Matrix<Data, Column, Row> {
//	public void load(int rowOffset, int rowLimit);
//	void setColumnPageSize(int pageSize);
//	void setColumnPage(int index);
//	int getCurrentRowPageIndex();
//	void nextColumnPage();
//	void prevColumnPage();
//	void getColumnFirstPage();
//	void getColumnLastPage();	
//}
//
////To support both row and column pageing
//public interface RowAndColumnPage implements ColumnPagable, RowPagable {
//	public void load(int rowOffset, int rowLimit, int columnOffset, int columnLimit);
//}
//
////Interface to support Matrix Operations
//public interface MatrixOperations<Data, Column, Row> {
//	public Matrix<Data, Column, Row> getSubMatrix(List<Column> columns, List<Row> rows);
//	public Matrix<Data, Column, Row> Union(Matrix<Data, Column, Row> matrix);
//	public Matrix<Data, Column, Row> Intersection(Matrix<Data, Column, Row> matrix);
//	public Matrix<Data, Column, Row> Difference(Matrix<Data, Column, Row> matrix);
//	public Matrix<Data, Column, Row> Transpose();
//	public Matrix<Data, Column, Row> performExclusion(Matrix<Data, Column, Row> matrix);
//}
//
////interface to export to different formats
//interface MatrixExporter {
//	export(Matrix m, OutputStream out);
//	String getContentType();
//	String getFileExtension();
//}
//
////Implementations of Exporter
//public ExcelExporter implements MatrixExporter {}
//public SpssExporter implements MatrixExporter {}
//public CSVExporter implements MatrixExporter {}
//
////Factory to create the Exporter
//public class MatrixFactory {
//	enum ExportType {
//		EXCEL,
//		CSV, 
//		SPSS
//	};
//	
//	MatrixExporter create(ExportType type);
//	MatrixExporter create(String type);
//};
//
////more a generalisation of ObservableFeature(MeasureMent) but also target towards UI
////to support differnent Datamodel and backend that extract data.
//public interface ObservableColumn {
//	public String getName(); 
//	public void setName();
//
//	public String getColumnGroup();
//	public void setColumnGroup(String columnGroup);
//	
//	public Object getFilter(); //search condition
//	public void setFilter(Object filter);
//
//	enum SortOrder {UNKOWN, ASC, DESC}; 
//	
//	public SortOrder getSort();
//	public void setSort(SortOrder order);
//}
//
//
////PSEUDO Code
////For example the EAVMatrix backend implements all the functionality to load data
//public class EAVMatrix<Data, Column, Row> implements Matrix<Data, Column, Row> {...}
//public class Column implements ObservableColumn {...}
//
////to use it
//EAVMatrix<String, Column, Column> matrix = new EAVMatrix(investigationId);
//Column name = new Column("name");
//Column age = new Column("age");
//matrix.setColumn({name, age});
//matrix.loadData();
//String[][] data = matrix.getData();
//for(String[] d : data) {
//	for(String e : d) {
//		System.out.println(e);
//	}
//}
////matrix sort by age
//age.setSort(Sort.Desc);
//age.setFilter("> 18");
//matrix.loadData();
////print out name, age to excel sorted by age
//OutputStream os = MatrixExport.export(matrix);
//FileStream fs = new FileStream(os, "text.xls");
//fs.flush();
//fs.close();
//
////load Next Column and rowPage
//matrix.nextRowPage();
//matrix.nextColumnPage();
//matrix.load();
//Object[] data = matrix.getData();
////do something with data
//
