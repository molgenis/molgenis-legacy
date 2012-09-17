package plugins.AddIndividual;

public class ReferenceList {

	public String Reference(String project){
		if(project.equals("CeliacSprue")){
			return "CD";
		}
		if(project.equals("preventCD")){
			return "CP";
		}
		else{
			return "";
		}
		
	}
}
