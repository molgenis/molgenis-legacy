package miscellaneous.kegg;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.LogManager;

public class Run
{

	/**
	 * This tool tries to map any gene identifier to a KEGG entry for this gene,
	 * followed by an attempt to find the closest orthologous gene in a
	 * specified target organism. Uses a combination of the KEGG Orthology and
	 * KEGG Sequence Similarity Database to find the best match.
	 * 
	 * @author joeri van der velde
	 * 
	 * @param args
	 * @throws ServiceException
	 * @throws RemoteException
	 */
	public static void main(String[] args) throws RemoteException, ServiceException
	{
		// shut down axis logger
		LogManager.shutdown();

		// source organism code and gene list for this organism
		// see: http://www.genome.jp/kegg/catalog/org_list.html
		String sourceOrganism = "ath";
		String[] ids = ExampleIDList.getIDs();

		// target organism code to map orthologous genes towards
		String targetOrganism = "hsa";

		// iterate over the genes
		for (String id : ids)
		{
			// query gene and attempt to find the corresponding KEGG entry
			String entry = KEGGTools.getKeggGene(sourceOrganism + ":" + id).getEntry();

			if (entry != null)
			{
				System.out.println("Gene name '" + id + "' mapped to KEGG entry '" + entry + "'.");
			}
			else
			{
				System.out.println("Gene name '" + id + "' could not be mapped to a KEGG entry.");
				System.out.println("*****");
				continue;
			}

			// find the entry name of the closest ortholog
			String ortho = KEGGTools.getClosestOrthologue(entry, targetOrganism).getTargetEntry();

			if (ortho != null)
			{
				// print the definition of this gene
				System.out.println("Best ortholog in " +targetOrganism + " is: ");
				System.out.println(KEGGGene.toStringFullHeader("; "));
				System.out.println(KEGGTools.getKeggGene(ortho).toStringFull("; "));
			}else{
				System.out.println("No ortholog could be found.");
			}
			System.out.println("*****");
		}
	}

}
