package miscellaneous.kegg;

import java.util.List;
import java.util.Map;


public class KEGGGene {
	
	public static String toStringFullHeader(String sep){
		String header = "";
		header += "entry" + sep;
		header += "name" + sep;
		header += "definition" + sep;
		header += "NCBIGI" + sep;
		header += "NCBIGeneID" + sep;
		header += "AASeq" + sep;
		header += "NTSeq" + sep;
		header += "genBankIDs" + sep;
		header += "pathways";
		return header;
	}
	
	public static String toStringMediumHeader(String sep){
		String header = "";
		header += "entry" + sep;
		header += "name" + sep;
		header += "definition" + sep;
		header += "NCBIGI" + sep;
		header += "NCBIGeneID" + sep;
		header += "genBankIDs" + sep;
		header += "pathways";
		return header;
	}
	
	public static String toStringShortHeader(String sep){
		String header = "";
		header += "entry" + sep;
		header += "name" + sep;
		header += "definition" + sep;
		header += "NCBIGI" + sep;
		header += "NCBIGeneID" + sep;
		return header;
	}
	
	public String toStringFull(String sep){
		String toString = "";
		
		toString += entry + sep;
		toString += name + sep;
		toString += definition + sep;
		toString += NCBIGI + sep;
		toString += NCBIGeneID + sep;
		toString += AASeq + sep;
		toString += NTSeq + sep;
		
		for(String gid : genBankIDs){
			toString += gid + ";";
		}
		toString += sep;
		
		for(String key : pathways.keySet()){
			toString += key + "->" + pathways.get(key) + ";";
		}
		return toString;
	}
	
	public String toStringMedium(String sep){
		String toString = "";
		
		toString += entry + sep;
		toString += name + sep;
		toString += definition + sep;
		toString += NCBIGI + sep;
		toString += NCBIGeneID + sep;

		for(String gid : genBankIDs){
			toString += gid + ";";
		}
		toString += sep;
		
		for(String key : pathways.keySet()){
			toString += key + "->" + pathways.get(key) + ";";
		}
		return toString;
	}
	
	public String toStringShort(String sep){
		String toString = "";
		
		toString += entry + sep;
		toString += name + sep;
		toString += definition + sep;
		toString += NCBIGI + sep;
		toString += NCBIGeneID + sep;
		
		return toString;
	}
	
	String entry;
	String name;
	String definition;
	String NCBIGI;
	String NCBIGeneID;
	String AASeq;
	String NTSeq;
	List<String> genBankIDs;
	Map<String, String> pathways;
	
	
	public String getEntry()
	{
		return entry;
	}
	public void setEntry(String entry)
	{
		this.entry = entry;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getDefinition()
	{
		return definition;
	}
	public void setDefinition(String definition)
	{
		this.definition = definition;
	}
	public String getNCBIGI()
	{
		return NCBIGI;
	}
	public void setNCBIGI(String ncbigi)
	{
		NCBIGI = ncbigi;
	}
	public String getNCBIGeneID()
	{
		return NCBIGeneID;
	}
	public void setNCBIGeneID(String geneID)
	{
		NCBIGeneID = geneID;
	}
	public String getAASeq()
	{
		return AASeq;
	}
	public void setAASeq(String seq)
	{
		AASeq = seq;
	}
	public String getNTSeq()
	{
		return NTSeq;
	}
	public void setNTSeq(String seq)
	{
		NTSeq = seq;
	}
	public List<String> getGenBankIDs()
	{
		return genBankIDs;
	}

	public void setGenBankIDs(List<String> genBankIDs)
	{
		this.genBankIDs = genBankIDs;
	}

	public Map<String, String> getPathways()
	{
		return pathways;
	}
	public void setPathways(Map<String, String> pathways)
	{
		this.pathways = pathways;
	}

}