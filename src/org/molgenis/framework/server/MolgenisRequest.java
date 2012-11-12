package org.molgenis.framework.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.molgenis.framework.db.Database;
import org.molgenis.util.HttpServletRequestTuple;

public class MolgenisRequest extends HttpServletRequestTuple
{

	Database db;
	String servicePath; // e.g. "/api/R/"
	String requestPath; // e.g. "/api/R/source.R"
	String appLocation; // e.g. "http://localhost:8080/xqtl"

	public MolgenisRequest(HttpServletRequest request) throws Exception
	{
		super(request);

	}

	public MolgenisRequest(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		super(request, response);

	}

	public Database getDatabase()
	{
		return db;
	}

	public void setDatabase(Database db)
	{
		this.db = db;
	}

	public String getServicePath()
	{
		return servicePath;
	}

	public void setServicePath(String servicePath)
	{
		this.servicePath = servicePath;
	}

	public String getRequestPath()
	{
		return requestPath;
	}

	public void setRequestPath(String requestPath)
	{
		this.requestPath = requestPath;
	}

	public String getAppLocation()
	{
		return appLocation;
	}

	public void setAppLocation(String appLocation)
	{
		this.appLocation = appLocation;
	}

	/**
	 * Special toString for MolgenisRequest Will break off large values, and
	 * hide passwords
	 */
	@Override
	public String toString()
	{
		if (this.getNrColumns() == 0) return "NONE";
		StringBuilder strBuilder = new StringBuilder();
		for (int columnIndex = 0; columnIndex < this.getNrColumns(); columnIndex++)
		{

			// take care of the value
			// String value = "NULL";
			// if (getObject(columnIndex) != null)
			// {
			// value = getObject(columnIndex).toString();
			// value = value.length() > 25 ? value.substring(0, 25) + ".." :
			// value;
			// }

			// take care of the name
			String name = columnIndex + "";
			if (getColName(columnIndex) != null)
			{
				name = getColName(columnIndex);
				strBuilder.append(name).append(' ');
			}

			// print name + value
			// if (name.toLowerCase().contains("password")
			// ||
			// name.equals(MolgenisServiceAuthenticationHelper.LOGIN_PASSWORD))
			// {
			// result += name + "='*****' ";
			// }
			// else
			// {
			// result += name + "='" + value + "' ";
			// }

		}
		return strBuilder.toString();
	}

}
