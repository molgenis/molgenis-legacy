package lifelines.matrix;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.eclipse.persistence.internal.databaseaccess.DatabaseAccessor;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.sessions.Session;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.organization.Investigation;
import org.richfaces.model.Ordering;

import app.JpaDatabase;
import org.apache.log4j.Logger;

public class RelationalMatrix implements PagableMatrix<Column, Integer> {

    private JpaDatabase db;
    private EntityManager em;
    private Object[][] data;
    private List<Column> columns = new ArrayList<Column>();
    private List<Integer> targets = new ArrayList<Integer>();
    private int pageSize;
    private SimplePager columnPager;

    private Logger logger = Logger.getLogger(RelationalMatrix.class);
    
    public final void initDatabase() throws DatabaseException {
        db = new JpaDatabase();
        em = db.getEntityManager();
    }

    public RelationalMatrix(int pageSize) throws DatabaseException, SQLException {
        initDatabase();
        this.pageSize = pageSize;
        this.columns = retrieveDatabaseColumns();
        this.columnPager = new SimplePager(this, 5);
    }

    private Connection getConnection() {
        Session session = ((EntityManagerImpl) em).getSession();
        UnitOfWorkImpl uow = (UnitOfWorkImpl) session;
        DatabaseAccessor dbAcc = (DatabaseAccessor) uow.getAccessor();
        return dbAcc.getConnection();
    }

    @Override
    public List<Column> getColumns() {
        return columns;
    }

    @Override
    public List<Column> getVisableColumns() {
        int columnOffset = columnPager.getCurrentPageIndex() * columnPager.getPageSize();
        int columnLimit = columnOffset + columnPager.getPageSize();
        if (columnLimit >= columns.size()) {
            columnLimit = columns.size();
        }
        return columns.subList(columnOffset, columnLimit);
    }

    @Override
    public Object[][] getData() {
        return data;
    }

    @Override
    public Investigation getInvestigation() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getNumberOfRows() {
        int numRows = 0;
        if(getVisableColumns().size() > 0) {
            String sql = "SELECT COUNT(*) FROM Joris " + createWhere();
            numRows = ((Long) em.createNativeQuery(sql.toString()).getSingleResult()).intValue();
            logger.debug(String.format("sql = %s", sql));
        }
        logger.debug(String.format("row count: %d", numRows));
        return numRows;
    }
    
    @Override
    public List<Integer> getRows() {
        return targets;
    }

    @Override
    public void loadData(int numberOfRows, int offset) throws Exception {
        StringBuilder sql = new StringBuilder("SELECT ");

        List<Column> visableColumns = null;
        if (offset == -1) {
            sql.append("paid, ");
            visableColumns = getColumns();
        } else {
            visableColumns = getVisableColumns();
            sql.append("paid, ");
        }
        //loadRowCount(visableColumns.size());

        for (Column c : visableColumns) {
            sql.append(getSqlColumnName(c.getName()));
            sql.append(",");
        }

        sql.deleteCharAt(sql.length() - 1); //remove last comma
        sql.append(" FROM Joris ");

        sql.append(createWhere());
        sql.append(createSort());

        logger.debug(String.format("sql = %s", sql));

        Query nq = em.createNativeQuery(sql.toString());
        if (numberOfRows > 0) {
            nq.setMaxResults(numberOfRows);
        }
        if (offset > -1) {
            nq.setFirstResult(offset);
        }

        List x = nq.getResultList();

        targets.clear();

        Object[] rec = x.toArray();
        if (rec.length > 0 && rec[0] instanceof Object[]) {
            data = new Object[x.size()][visableColumns.size()+1];
            for (int i = 0; i < rec.length; ++i) {
                targets.add((Integer) ((Object[]) rec[i])[0]);
                Object[] r = (Object[]) rec[i];
                if (r != null) {
                    for (int j = 0; j < r.length; ++j) {
                        data[i][j] = r[j];
                    }
                }
            }
        } else {
            data = new Object[x.size()][1];
            for (int i = 0; i < rec.length; ++i) {
                targets.add((Integer) rec[i]);
                data[i][0] = rec[i];
            }
        }

//        for (int i = 0; i < rec.length; ++i) {
//            for (int j = 0; j < data[i].length; ++j) {
//                System.out.print(data[i][j] + "\t");
//            }
//            System.out.println();
//        }
    }

//    private void loadRowCount(int columnCount) {
//        if(columnCount <= 0) {
//            numRows = 0;
//        } else {
//            String sql = "SELECT COUNT(*) FROM Joris " + createWhere();
//            numRows = ((Long) em.createNativeQuery(sql.toString()).getSingleResult()).intValue();
//            logger.debug(String.format("sql = %s", sql));
//        }
//        logger.debug(String.format("row count: %d", numRows));
//    }

    private String createSort() {
        String res = "";

        for (Column c : columns) {
            if (c.getOrdering() == Ordering.ASCENDING) {
                res = " ORDER BY " + getSqlColumnName(c.getName()) + " ASC ";
            } else if (c.getOrdering() == Ordering.DESCENDING) {
                res = " ORDER BY " + getSqlColumnName(c.getName()) + " DESC ";
            }
        }

        return res;
    }

    private String createWhere() {
        String where = "";
        for (Column c : columns) {
            if (c.getFilter() != null && c.getFilter() instanceof String) {
                if (!c.getFilter().toString().isEmpty()) {
                    if (!where.isEmpty()) {
                        where += " AND ";
                    }
                    String condition = FilterParser.parseExpr(c.getFilter().toString());
                    if (condition != null) {
                        where += condition.replace("?column?", getSqlColumnName(c.getName()));
                    }
                }
            }
        }
        if (!where.isEmpty()) {
            where = " WHERE " + where;
        }
        return where;
    }

    private String getSqlColumnName(String columnName) {
        columnName = columnName.replace("_", "");
        if (columnName.indexOf('.') != -1) {
            columnName = columnName.substring(columnName.indexOf('.') + 1);
        }
        return columnName;
    }

    @Override
    public void setColumns(List<Column> columns) {
        this.columns = columns;
        for (Column c : columns) {
            if (c.getName().contains("paid")) {
                columns.remove(c);
                break;
            }
        }
        //columns.add(new Column("paid", ColumnType.integer, ""));
    }

    @Override
    public void setInvestigation(Investigation investigation) {
        throw new UnsupportedOperationException();
    }

    public final List<Column> retrieveDatabaseColumns() throws SQLException {
        List<Column> dbColumns = new ArrayList<Column>();

        DatabaseMetaData dbMetaData = getConnection().getMetaData();
        ResultSet rsColumns = dbMetaData.getColumns(null, null, "Joris", null);
        while (rsColumns.next()) {
            String columnName = rsColumns.getString("COLUMN_NAME");
            String columnType = rsColumns.getString("TYPE_NAME");
            //int size = rsColumns.getInt("COLUMN_SIZE");
//            boolean isNull = false;
//            int nullable = rsColumns.getInt("NULLABLE");
//            if (nullable == DatabaseMetaData.columnNullable) {
//                isNull = true;
//            } else {
//                isNull = false;
//            }
            //int position = rsColumns.getInt("ORDINAL_POSITION"); // column pos

            dbColumns.add(new Column(columnName, Column.getColumnType(columnType), null));
        }

        return dbColumns;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public SimplePager getColumnPager() {
        return columnPager;
    }

    @Override
    public void setColumnPager(SimplePager pager) {
        columnPager = pager;
    }

    @Override
    public boolean isDirty() {
        if (columnPager != null) {
            return columnPager.isDirty();
        }
        return false;
    }

    @Override
    public void setDirty(boolean dirty) {
        if (columnPager != null) {
            columnPager.setDirty(dirty);
        }
    }
}
