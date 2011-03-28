package miscellaneous.kegg;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.LogManager;

public class RunDanny
{

	/**
	 * @param args
	 * @throws ServiceException
	 * @throws RemoteException
	 */
	public static void main(String[] args) throws RemoteException, ServiceException
	{
		LogManager.shutdown();
		// path:eco00020

		String sourceOrganism = "hsa";

	//	KEGGLocator locator = new KEGGLocator();
	//	KEGGPortType serv = locator.getKEGGPort();

	//	String info = serv.bget("path:eco00020");
	//	System.out.println(info);

		for (String id : new String[]
		{ "RP11-151A6DAB1", "AFMID", "EID2B" })
		{

			KEGGGene gene = KEGGTools.getKeggGene(sourceOrganism + ":" + id);

			if (gene.getEntry() != null)
			{
				System.out.println("id: " + id + " -> entry: " + gene.getPathways().toString());
			}

		}

	}
}
