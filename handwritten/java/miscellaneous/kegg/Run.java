package miscellaneous.kegg;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.LogManager;

public class Run
{

	/**
	 * @param args
	 * @throws ServiceException
	 * @throws RemoteException
	 */
	public static void main(String[] args) throws RemoteException, ServiceException
	{
		//NOTE: Removed a warning logger.shutdown is wrong see: http://logging.apache.org/log4j/1.2/apidocs/deprecated-list.html
		LogManager.shutdown();

		String sourceOrganism = "sce";
		String targetOrganism = "hsa";

		//ExampleIDList.getIDs()
		for (String id : new String[]{"IME2"})
		{

			// query for the gene identifier of choice and get the entry, which is the
			// common kegg identifier
			String entry = KEGGTools.getKeggGene(sourceOrganism + ":" + id).getEntry();
			System.out.println("id: " + id + " -> entry: " + entry);

			// get the entry name of the closest orthologue by querying the
			// organism+entry, and the target organism code
			String res = KEGGTools.getClosestOrthologue(entry, targetOrganism).getTargetEntry();

			// print the definition of this gene
			System.out.println(targetOrganism + " orthologue: " + KEGGTools.getKeggGene(res).getDefinition());
			System.out.println();
		}
	}

}
