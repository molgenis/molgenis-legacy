package lifelines.matrix;

public interface PagableMatrix<Col, Row> extends Matrix<Col, Row>  {
	public int getPageSize();
	public void setPageSize(int pageSize);
	
	public SimplePager getColumnPager();
	public void setColumnPager(SimplePager pager);

        public boolean isDirty();
        public void setDirty(boolean dirty);
}