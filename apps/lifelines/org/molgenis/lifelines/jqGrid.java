package org.molgenis.lifelines;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lifelines.matrix.Column;
import lifelines.matrix.Exporter.ExportExcelSimple;

import org.apache.commons.lang.StringUtils;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.util.HandleException;

import app.DatabaseFactory;

/**
 * Servlet implementation class jqGrid
 */
public class jqGrid extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public jqGrid() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String schema = request.getParameter("schema");
		String tableName = request.getParameter("tableName");
		String operation = request.getParameter("op");
		
		@SuppressWarnings("unchecked")
		Enumeration<String> en = request.getParameterNames();
		while (en.hasMoreElements()) {
			String name = en.nextElement();
			System.out.println("name: " + name + " value: "
					+ request.getParameter(name));
		}

		if (StringUtils.isNotEmpty(operation) && operation.equals("tree")) {
			treeView(request, response);
			return;
		}
		
		Database db = null;
		try {
			db = DatabaseFactory.create();
		} catch (DatabaseException e1) {
			HandleException.handle(e1);
		}
		EntityManager em = db.getEntityManager();
		List<Column> columns = new ArrayList<Column>();
		try {
			columns = getColumns(schema, tableName,
					((app.JpaDatabase) db).createJDBCConnection());
		} catch (Exception ex) {
			HandleException.handle(ex);
		}

		if (StringUtils.isNotEmpty(operation) && operation.equals("getColumns")) {
			PrintWriter out = response.getWriter();
			out.println("{");
			out.print("\"columns\": [");
			for (int i = 0; i < columns.size(); ++i) {
				out.print(String.format("\"%s\"", columns.get(i).getName()));
				if (i + 1 < columns.size()) {
					out.print(",");
				}
			}
			out.println("]");

			out.println("}");
			out.flush();
			return;
		}

		String pageParam = request.getParameter("page");
		String limitParam = request.getParameter("rows");
		String sidx = request.getParameter("sidx");
		String sord = request.getParameter("sord");

		int page = 1;
		if (StringUtils.isNotEmpty(pageParam)) {
			page = Integer.parseInt(pageParam);
		}
		int limit = 10;
		if (StringUtils.isNotEmpty(limitParam)) {
			limit = Integer.parseInt(limitParam);
		}

		if (StringUtils.isEmpty(sidx)) {
			sidx = "PA_ID";
		}
		if (StringUtils.isEmpty(sord)) {
			sord = "DESC";
		}

		String whereCondition = "";
		String searchOn = request.getParameter("_search");
		if (StringUtils.isNotEmpty(searchOn) && Boolean.parseBoolean(searchOn)) {
			for (int i = 0; i < columns.size(); ++i) {
				String column = columns.get(i).getName();
				String value = request.getParameter(column);
				if (value != null) {
					String condition = String
							.format(" %s = %s ", column, value);
					if (i + 1 < columns.size() && !whereCondition.equals("")) {
						whereCondition += " AND ";
					}
					whereCondition += condition;
				}
			}
		}

		if (!whereCondition.equals("")) {
			whereCondition = " WHERE " + whereCondition;
		}

		String countQuery = String.format("SELECT COUNT(*) FROM %s %s",
				tableName, whereCondition);

		int count = -1;
		try {
			count = ((BigDecimal) em.createNativeQuery(countQuery)
					.getSingleResult()).intValue();

			int totalPages = 0;
			if (count > 0) {
				totalPages = (int) Math.ceil(count / limit);
			}

			if (page > totalPages) {
				page = totalPages;
			}

			int start = limit * page - limit;

			String sql = "SELECT * FROM %s %s ORDER BY %s %s";
			sql = String.format(sql, tableName, whereCondition, sidx, sord);

			@SuppressWarnings("unchecked")
			List<Object[]> rs = em.createNativeQuery(sql).setFirstResult(start)
					.setMaxResults(limit).getResultList();

			if (StringUtils.isNotEmpty(operation)
					&& operation.equals("excelExport")) {
				response.setContentType(ExportExcelSimple.getContentType());
				response.setHeader(
						"Content-disposition",
						"attachment; filename=Matrix."
								+ ExportExcelSimple.getFileExtenstion());
				OutputStream out = response.getOutputStream();
				ExportExcelSimple.export(columns, out, db, sql);
				out.flush();
			} else {
				PrintWriter out = response.getWriter();
				out.println("<?xml version='1.0' encoding='utf-8'?>");
				out.println("<rows>");

				out.println(String.format("<page>%s</page>", page));
				out.println(String.format("<total>%s</total>", totalPages));
				out.println(String.format("<records>%s</records>", count));

				for (Object[] objs : rs) {
					out.println(String.format("<row id=\"%s\">",
							objs[0].toString()));
					for (Object obj : objs) {
						if (obj != null) {
							out.println(String.format("<cell>%s</cell>",
									obj.toString()));
						} else {
							out.println(String.format("<cell>%s</cell>", ""));
						}
					}
					out.println("</row>");
				}
				out.println("</rows>");
				out.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
			HandleException.handle(e);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	private static List<Column> getColumns(String schema, String tableName,
			Connection connection) throws Exception {
		List<Column> columns = new ArrayList<Column>();
		DatabaseMetaData metaData = connection.getMetaData();
		ResultSet rs = metaData.getColumns(null, schema.toUpperCase(),
				tableName.toUpperCase(), null);
		while (rs.next()) {
			String columName = rs.getString("COLUMN_NAME");
			String columnType = rs.getString("TYPE_NAME");
			// int size = rsColumns.getInt("COLUMN_SIZE");
			// boolean isNull = false;
			// int nullable = rsColumns.getInt("NULLABLE");
			// if (nullable == DatabaseMetaData.columnNullable) {
			// isNull = true;
			// } else {
			// isNull = false;
			// }
			// int position = rsColumns.getInt("ORDINAL_POSITION"); // column
			// pos
			columns.add(new Column(columName, Column.getColumnType(columnType), null));
		}
		return columns;
	}

	private static void treeView(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		out.println("<rows>");
		out.println("<page>1</page>");
		out.println("<total>1</total>");
		out.println("<records>1</records>");
		out.println("<row>");
		out.println("<cell>1</cell>");
		out.println("<cell>Cash</cell>");
		out.println("<cell>100</cell>");
		out.println("<cell>400.00</cell>");
		out.println("<cell>250.00</cell>");
		out.println("<cell>150.00</cell>");
		out.println("<cell>0</cell>");
		out.println("<cell>1</cell>");
		out.println("<cell>8</cell>");
		out.println("<cell>false</cell>");
		out.println("<cell>false</cell>");
		out.println("</row>");
		out.println("<row>");
		out.println("<cell>5</cell>");
		out.println("<cell>Bank's</cell>");
		out.println("<cell>200</cell>");
		out.println("<cell>1500.00</cell>");
		out.println("<cell>1000.00</cell>");
		out.println("<cell>500.00</cell>");
		out.println("<cell>0</cell>");
		out.println("<cell>9</cell>");
		out.println("<cell>14</cell>");
		out.println("<cell>false</cell>");
		out.println("<cell>false</cell>");
		out.println("</row>");
		out.println("<row>");
		out.println("<cell>8</cell>");
		out.println("<cell>Fixed asset</cell>");
		out.println("<cell>300</cell>");
		out.println("<cell>0.00</cell>");
		out.println("<cell>1000.00</cell>");
		out.println("<cell>-1000.00</cell>");
		out.println("<cell>0</cell>");
		out.println("<cell>15</cell>");
		out.println("<cell>16</cell>");
		out.println("<cell>true</cell>");
		out.println("<cell>false</cell>");
		out.println("</row>");
		out.println("</rows>");		
		out.flush();
	}
}
