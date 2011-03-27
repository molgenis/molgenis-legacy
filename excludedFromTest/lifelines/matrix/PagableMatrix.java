package lifelines.matrix;

import java.util.List;

import org.molgenis.organization.Investigation;

public interface PagableMatrix<Data, Col, Row> {
	public void loadData(int numberOfRows, int offset) throws Exception;
	public void setInvestigation(Investigation investigation);
	public Investigation getInvestigation();
	public List<Integer> getRows();
	public List<Col> getColumns();
	public void setColumns(List<Col> columns);
	public Data[][] getData();
	public int getNumberOfRows();
}
