package plugins.cluster.implementations;

import plugins.cluster.helper.Command;
import app.JDBCDatabase;
import filehandling.generic.MolgenisFileHandler;

public class TestLocalExecution
{

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		JDBCDatabase db = new JDBCDatabase("xgap.properties");
		MolgenisFileHandler xf = new MolgenisFileHandler(db);
		LocalComputationResource lc = new LocalComputationResource(xf);

		// project dir
		Command command = new Command("dir", true, false, false);

		// tmp dir -> fails for Vista
		// but NOT when the command is pasted into cmd manually... (ie. cd
		// C:\Users\Joeri\AppData\Local\Temp\ && dir)
		Command command2 = new Command("dir", true, false, true);

		lc.executeCommand(command);

	}

}
