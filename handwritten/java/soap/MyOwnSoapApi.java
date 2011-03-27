package soap;
import javax.jws.WebMethod;

import org.molgenis.framework.db.Database;

import app.servlet.SoapApi;


public class MyOwnSoapApi extends SoapApi
{

	public MyOwnSoapApi(Database database)
	{
		super(database);
		// TODO Auto-generated constructor stub
	}
	
	@WebMethod
	public String helloWorld(String name)
	{
		return "hello "+name;
	}

}
