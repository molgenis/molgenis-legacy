package plugins.AddIndividual;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;

public class SelectFamily {
	
	Integer newFamilyNumber = 0;
	String newFamily = "";
	private List<Individual> listIndiv = new ArrayList<Individual>();
	Integer lastFamilyNumber = 0;
	
	public String family(Database db, String project){
		ReferenceList ref = new ReferenceList();
		try {
			List<QueryRule> filterRules = new ArrayList<QueryRule>();
			filterRules.add(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "id_family"));
			filterRules.add(new QueryRule(ObservedValue.INVESTIGATION_NAME, Operator.EQUALS, project));
			int size =db.find(ObservedValue.class, new QueryRule(filterRules)).size();
			//Select last family from the specific project
			String lastFamily = db.find(ObservedValue.class, new QueryRule(filterRules)).get(size-1).getValue();
			//cut off the 2 first chars
			lastFamilyNumber = Integer.valueOf(lastFamily.substring(2)).intValue();
			//Create new number 
			newFamilyNumber = lastFamilyNumber+1;
			
			
			
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Paste the project chars together with new familyID and return value
		return ref.Reference(project)+newFamilyNumber;
	}
	
	
	public Integer getNewFamilyNumber() {
		return newFamilyNumber;
	}


	public void setNewFamilyNumber(Integer newFamilyNumber) {
		this.newFamilyNumber = newFamilyNumber;
	}


	public String getNewFamily() {
		return newFamily;
	}


	public void setNewFamily(String newFamily) {
		this.newFamily = newFamily;
	}
	
	public List<Individual> getListIndiv() {
		return listIndiv;
	}


	public void setListIndiv(List<Individual> listIndiv) {
		this.listIndiv = listIndiv;
	}
	
}
