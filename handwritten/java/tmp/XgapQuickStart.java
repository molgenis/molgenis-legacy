package tmp;
import java.util.ArrayList;

import org.apache.log4j.LogManager;

import regressiontest.cluster.DataLoader;
import app.JDBCDatabase;

public class XgapQuickStart
{
	public XgapQuickStart() throws Exception
	{
		JDBCDatabase db = new JDBCDatabase("xgap.properties");

		ArrayList<String> result = new DataLoader().load(db);
		for (String s : result)
		{
			System.out.println(s);
		}
	}

	public static void main(String[] args) throws Exception
	{
		LogManager.shutdown();
		new XgapQuickStart();
	}

}
