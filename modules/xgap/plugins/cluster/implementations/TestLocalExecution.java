package plugins.cluster.implementations;

import org.molgenis.framework.db.Database;

import plugins.cluster.helper.Command;
import app.DatabaseFactory;

public class TestLocalExecution
{

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		Database db = DatabaseFactory.create("handwritten/apps/org/molgenis/xgap/xqtlworkbench_standalone/xqtl.properties");
		LocalComputationResource lc = new LocalComputationResource();

		// project dir
		Command command = new Command("dir", true, false, false);

		// tmp dir -> fails for Vista
		// but NOT when the command is pasted into cmd manually... (ie. cd
		// C:\Users\Joeri\AppData\Local\Temp\ && dir)
		Command command2 = new Command("dir", true, false, true);

		System.out.print(lc.executeCommand(command));
		lc.executeCommand(command2);

	}

}
