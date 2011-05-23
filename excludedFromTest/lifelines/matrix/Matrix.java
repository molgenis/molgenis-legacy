package lifelines.matrix;

import java.util.List;

import org.molgenis.organization.Investigation;

public interface Matrix<Col, Row> {

    public void loadData(int numberOfRows, int offset) throws Exception;

    public void setInvestigation(Investigation investigation);

    public Investigation getInvestigation();

    public List<Row> getRows();

    public List<Col> getColumns();

    public List<Col> getVisableColumns();

    public void setColumns(List<Col> columns);

    public Object[][] getData();

    public int getNumberOfRows();
}
