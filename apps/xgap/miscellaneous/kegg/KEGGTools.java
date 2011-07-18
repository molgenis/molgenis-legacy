package miscellaneous.kegg;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.rpc.ServiceException;

import keggapi.Definition;
import keggapi.KEGGLocator;
import keggapi.KEGGPortType;
import keggapi.LinkDBRelation;
import keggapi.SSDBRelation;

public class KEGGTools
{
	
	private static int MAX_RETRIES = 5;
	private static int CUR_RETRIES = 0;

	/**
	 * Finds closest orthologous gene. 1) Find KO entry. If this has one gene, use
	 * this gene and return. 2) If there is no KO entry, search in the SSDB, find
	 * the closest gene and return. 3) If there are multiple genes in the KO
	 * entry, save this list and search in SSDB. The closest gene encountered in
	 * SSDB that is also present in the KO list will be selected. If there are
	 * none found in SSDB, select the first one in the KO list and return with a
	 * warning.
	 * 
	 * @param sourceEntry
	 *          KEGG identifier. Format: organism:number. For example:
	 *          <b>tca:659936</b>.
	 * @param targetOrganism
	 *          KEGG organism code. For example: <b>hsa</b>, <b>sce</b> or
	 *          <b>cel</b>.
	 * @return <b>KEGGOrthologue</b> object, containing source and target KEGG
	 *         identifiers, and an <b>SSDBRelation</b> when SSDB is used.
	 * @throws RemoteException
	 * @throws ServiceException
	 */
	public static KEGGOrthologue getClosestOrthologue(String sourceEntry, String targetOrganism) throws RemoteException, ServiceException
	{
		KEGGOrthologue result = new KEGGOrthologue();
		result.setSourceEntry(sourceEntry);

		// If the function is called by getting an entry code as 'null', save time
		// by exiting the functions here.
		if (sourceEntry.split(":")[1].equals("null"))
		{
			result.setTargetEntry("null");
			return result;
		}

		KEGGLocator locator = new KEGGLocator();
		KEGGPortType serv = locator.getKEGGPort();
		ArrayList<String> multiKOGenes = new ArrayList<String>();
		boolean SSDBSearch = false;

		// Get KO
		String[] koRes = serv.get_ko_by_gene(sourceEntry);

		if (koRes.length == 0)
		{
			// Proceed with SSDB
			SSDBSearch = true;

		} else if (koRes.length == 1)
		{
			// Ene KO entry found
			// Get genes from KO
			Definition[] koGenes = serv.get_genes_by_ko(koRes[0], targetOrganism);

			if (koGenes.length == 0)
			{
				// No genes found for target organism in KO
				// Proceed with SSDB
				SSDBSearch = true;

			} else if (koGenes.length == 1)
			{
				// One gene found, return
				result.setTargetEntry(koGenes[0].getEntry_id());
				return result;
			} else
			{
				// Multiple genes found. Add them to a list.
				// Proceed with SSDB
				SSDBSearch = true;

				for (Definition d : koGenes)
				{
					multiKOGenes.add(d.getEntry_id());
				}
			}
		} else
		{
			// Multiple KO entries found
			// Try to build the list
			// If the list contains more than one or zero elements, proceed with SSDB,
			// else return the one element.

			for (String s : koRes)
			{
				Definition[] koGenes = serv.get_genes_by_ko(s, targetOrganism);
				for (Definition d : koGenes)
				{
					multiKOGenes.add(d.getEntry_id());
				}
			}

			if (multiKOGenes.size() == 0)
			{
				SSDBSearch = true;
			} else if (multiKOGenes.size() == 1)
			{
				result.setTargetEntry(multiKOGenes.get(0));
				return result;
			} else
			{
				SSDBSearch = true;
			}
		}

		if (SSDBSearch)
		{

			int limit = 25;
			int max = 1000;

			for (int i = 1; i <= max; i += limit)
			{
				SSDBRelation[] res = serv.get_best_neighbors_by_gene(sourceEntry, i, limit);
				int find = containsTargetOrganism(res, targetOrganism);
				if (find != -1)
				{
					if (multiKOGenes.size() > 0)
					{
						System.out.print("multiKOGenes: ");
						for (String s : multiKOGenes)
						{
							System.out.print(s + ", ");
						}
						System.out.print("\n");
						System.out.print("res[find].getGenes_id2(): " + res[find].getGenes_id2() + "\n");

						if (multiKOGenes.contains(res[find].getGenes_id2()))
						{
							result.setTargetEntry(res[find].getGenes_id2());
							result.setSSDBRelation(res[find]);
							return result;
						} else
						{
							// SSDB gene not in list, keep searching
						}
					} else
					{
						result.setTargetEntry(res[find].getGenes_id2());
						result.setSSDBRelation(res[find]);
						return result;
					}
				}
			}

			// Nothing found, but there are genes in the multiKOGenes list
			if (multiKOGenes.size() > 0)
			{
				System.out.println("WARNING: Entries in KO gene list but not found in SSDB, picking first non-pseudogene from KO list.");
				result.setTargetEntry(pickNonPseudogene(multiKOGenes, serv));
				return result;
			}
		}

		//System.out.println("WARNING: Last return statement used. Nothing found.");
		return result;
	}

	private static String pickNonPseudogene(ArrayList<String> geneIdList, KEGGPortType serv) throws RemoteException, ServiceException
	{
		if (geneIdList.size() == 0)
		{
			System.out.println("WARNING: Empty geneIDList, returning NULL.");
			return null;
		} else if (geneIdList.size() == 1)
		{
			return geneIdList.get(0);
		} else
		{

			for (String geneId : geneIdList)
			{
				KEGGGene gene = getKeggGene(geneId);
				Pattern pattern = Pattern.compile(".+pseudogene.+");
				Matcher matcher = pattern.matcher(gene.getDefinition().toLowerCase());

				if (matcher.find() == false)
				{
					return geneId;
				}
			}
			
			System.out.println("WARNING: Forced to pick a KO gene that is described as PSEUDOGENE.");
			return geneIdList.get(0);
		}

	}

	private static int containsTargetOrganism(SSDBRelation[] res, String target)
	{
		for (int i = 0; i < res.length; i++)
		{
			if (res[i].getGenes_id2().substring(0, target.length()).equals(target))
			{
				return i;
			}
		}
		return -1;
	}

	/**
	 * 
	 * @param sourceEntry
	 *          KEGG identifier. Format: organism:number. For example:
	 *          <b>tca:659936</b>.
	 * 
	 * @return <b>KEGGGene</b> object containing most information about this KEGG gene entry in a structured way.
	 * @throws RemoteException
	 * @throws ServiceException
	 */
	public static KEGGGene getKeggGene(String organismGeneIdentifier) throws RemoteException, ServiceException
	{		
		KEGGLocator locator = new KEGGLocator();
		KEGGPortType serv = locator.getKEGGPort();
		String bget = serv.bget(organismGeneIdentifier);
		String orgCode = organismGeneIdentifier.split(":")[0];
		return parseKeggGene(orgCode, bget, serv);
	}

	private static KEGGGene parseKeggGene(String targetOrgCode, String bget, KEGGPortType serv) throws RemoteException
	{

		KEGGGene kg = new KEGGGene();
		Map<String, String> recordByAttributes = new HashMap<String, String>();

		// build map with concatenated lines per attribute
		// ignore trailing backslashes
		// eg. NAME => NAME DDX39
		String attribute = "";
		String[] splitBget = bget.split("\n");
		for (String s : splitBget)
		{
			if (!s.startsWith(" "))
			{
				attribute = s.split(" ")[0];
				if (!attribute.equals("///"))
				{
					recordByAttributes.put(attribute, s);
				}
			} else
			{
				recordByAttributes.put(attribute, recordByAttributes.get(attribute) + s);
			}
		}

		// clean up map:
		// remove leading key in value
		// replace tab indentation by newlines for later splitting
		// remove all superfluous whitespaces
		for (String key : recordByAttributes.keySet())
		{
			String newVal = recordByAttributes.get(key).substring(key.length());
			newVal = newVal.replace("            ", "\n");
			newVal = trim(newVal);
			recordByAttributes.put(key, newVal);
		}

		// iterate through map and build gene object
		for (String key : recordByAttributes.keySet())
		{
			if (key.equals("ENTRY"))
			{
				kg.setEntry(targetOrgCode + ":" + recordByAttributes.get(key).split("\n")[0].split(" ")[0]);
			} else if (key.equals("NAME"))
			{
				kg.setName(recordByAttributes.get(key));
			} else if (key.equals("DEFINITION"))
			{
				String concat = "";
				for (String line : recordByAttributes.get(key).split("\n"))
				{
					concat += line;
				}
				kg.setDefinition(concat);
			} else if (key.equals("DBLINKS"))
			{
				for (String s : recordByAttributes.get(key).split("\n"))
				{
					if (s.startsWith("NCBI-GI:"))
					{
						kg.setNCBIGI(s.replace("NCBI-GI: ", ""));
					}
					if (s.startsWith("NCBI-GeneID:"))
					{
						kg.setNCBIGeneID(s.replace("NCBI-GeneID: ", ""));
					}
				}
			} else if (key.equals("NTSEQ"))
			{
				// ignore first line, concat rest
				String[] split = recordByAttributes.get(key).split("\n");
				String res = "";
				for (int i = 1; i < split.length; i++)
				{
					res += split[i];
				}
				kg.setNTSeq(res);

			} else if (key.equals("AASEQ"))
			{
				// ignore first line, concat rest
				String[] split = recordByAttributes.get(key).split("\n");
				String res = "";
				for (int i = 1; i < split.length; i++)
				{
					res += split[i];
				}
				kg.setAASeq(res);
			}

		}

		// GENBANK
		kg.setGenBankIDs(getGenBankIDs(kg.getEntry(), serv));

		// PATHWAYS
		kg.setPathways(getPathways(kg.getEntry(), serv));

		return kg;

	}

	private static Map<String, String> getPathways(String entry, KEGGPortType serv) throws RemoteException
	{
		Map<String, String> res = new HashMap<String, String>();
		try
		{
			for (String s : serv.get_pathways_by_genes(new String[] { entry }))
			{
				res.put(s, trim(serv.btit(s)));
			}
		}
		catch(Exception e){
			CUR_RETRIES++;
			System.out.println("Caught exception at try "+CUR_RETRIES+"..");
			if(CUR_RETRIES == MAX_RETRIES+1){
				System.out.println("Maximum amount of retries ("+MAX_RETRIES+" reached, exiting..");
			}else{
				return getPathways(entry, serv);
			}
		}
		return res;
	}

	private static List<String> getGenBankIDs(String entry, KEGGPortType serv) throws RemoteException
	{
		LinkDBRelation[] linkDB = serv.get_linkdb_by_entry(entry, "GenBank", 1, 100);
		List<String> res = new ArrayList<String>();
		for (LinkDBRelation lr : linkDB)
		{
			res.add(lr.getEntry_id2());
		}
		return res;
	}

	/* remove leading whitespace */
	private static String ltrim(String source)
	{
		return source.replaceAll("^\\s+", "");
	}

	/* remove trailing whitespace */
	private static String rtrim(String source)
	{
		return source.replaceAll("\\s+$", "");
	}

	/* replace multiple whitespaces between words with single blank */
	private static String itrim(String source)
	{
		return source.replaceAll("\\b\\s{2,}\\b", " ");
	}

	/* remove all superfluous whitespaces in source string */
	private static String trim(String source)
	{
		return itrim(ltrim(rtrim(source)));
	}

}
