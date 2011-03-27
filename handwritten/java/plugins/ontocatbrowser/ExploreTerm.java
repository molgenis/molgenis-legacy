package plugins.ontocatbrowser;

import java.util.List;

import org.molgenis.organization.Investigation;

public class ExploreTerm {
	String accession;
	String name;
	String term;
	List<String> metaData;
	List<String> xRefs;
	List<String> parents;
	List<String> relations;
	String path;
	Integer category;
	Investigation inv;
	String graphURI;
	



	public Investigation getInv()
	{
		return inv;
	}

	public void setInv(Investigation inv)
	{
		this.inv = inv;
	}

	public String getAccession() {
		return accession;
	}

	public void setAccession(String accession) {
		this.accession = accession;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public List<String> getMetaData() {
		return metaData;
	}

	public void setMetaData(List<String> metaData) {
		this.metaData = metaData;
	}

	public List<String> getXRefs() {
		return xRefs;
	}

	public void setXRefs(List<String> refs) {
		xRefs = refs;
	}

	public List<String> getParents() {
		return parents;
	}

	public void setParents(List<String> parents) {
		this.parents = parents;
	}

	public List<String> getRelations() {
		return relations;
	}

	public void setRelations(List<String> relations) {
		this.relations = relations;
	}

	public Integer getCategory() {
		return category;
	}

	public void setCategory(Integer category) {
		this.category = category;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getGraphURI() {
		return graphURI;
	}

	public void setGraphURI(String graphURI) {
		this.graphURI = graphURI;
	}
	

}
