package plugins.HarmonizationComponent;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.semanticweb.owlapi.model.IRI;


public class levenshteinDistance {

	private OWLFunction owlFunction = null;
	
	private double cutOff = 60.0;

	public static void main(String args[]) throws Exception{

		System.out.println("The mapping has started");
		
		levenshteinDistance test = new levenshteinDistance();

		test.startMatching();
		
		System.out.println("The mapping has been done!");
	}

	public void startMatching() throws Exception{
		
		WritableWorkbook workbook = Workbook.createWorkbook(new File("/Users/pc_iverson/Desktop/Ontology_term_pilot/result2.xls")); 
		
		WritableSheet sheet = workbook.createSheet("result", 0); 
		
		LevenshteinDistanceModel matchingModel = new LevenshteinDistanceModel();
		
		String fileName = "/Users/pc_iverson/Desktop/Ontology_term_pilot/LeidseHPOLijste2.xls";

		tableModel model = new tableModel(fileName, false);

		List<String> originalTerms = model.getColumn("Symptom");
		
		String ontologyFileName = "/Users/pc_iverson/Desktop/Ontology_term_pilot/human-phenotype-ontology.obo";
		
		this.owlFunction = new OWLFunction(ontologyFileName);
		
		owlFunction.labelMapURI();
		
		List<String> listOfOntologyTerms = owlFunction.getAllTerms();
		
		String file2 = "/Users/pc_iverson/Desktop/Ontology_term_pilot/CineasDiagnoses.xls";
		
		tableModel model2 = new tableModel(file2, false);
		
		List<String> listOfSympotoms = model2.getColumn("ZIEKTETEKST");
		
		int rowIndex = 0;
		
		for(String eachTerm : originalTerms){
			
			double bestScore = 0;
			
			String matchedOntologyTerm = "";
			
			String synonymForOntologyTerm = "";
			
			Map<String, Double> candidateMatching = new HashMap<String, Double>();
			
			Map<String, String> candidateHPOId = new HashMap<String, String>();
			
			for(String ontologyTerm : listOfOntologyTerms){
				
				double similarity = matchingModel.stringMatching(eachTerm, ontologyTerm, false);
				
				if(bestScore < similarity){
					bestScore = similarity;
					matchedOntologyTerm = ontologyTerm;
					synonymForOntologyTerm = "";
				}
				
				if(similarity > cutOff){
					candidateMatching.put(ontologyTerm, similarity);
					candidateHPOId.put(ontologyTerm, owlFunction.getOntologyTermID(ontologyTerm));
				}
				
				List<String> synonyms = owlFunction.getAnnotation(ontologyTerm, IRI.create(
						owlFunction.getOntologyIRI().subSequence(0, owlFunction.getOntologyIRI().length() - 2).toString() + "synonym"));
				
				for(String eachSynonym : synonyms){
					
					double similarity2 = matchingModel.stringMatching(eachTerm, eachSynonym, false);
					
					if(bestScore < similarity2){
						bestScore = similarity2;
						synonymForOntologyTerm = eachSynonym;
						matchedOntologyTerm = ontologyTerm;
					}
					
					if(similarity2 > cutOff){
						String output = "Synonym match: " + eachSynonym + "; The ontology term: " + ontologyTerm;
						candidateMatching.put(output, similarity2);
						candidateHPOId.put(output, owlFunction.getOntologyTermID(ontologyTerm));
					}
				}
			}
			
			System.out.println(eachTerm + "\t" + matchedOntologyTerm + "\t" + owlFunction.getOntologyTermID(matchedOntologyTerm) + "\t" + bestScore);
			
			Label originalTermCell = new Label(0, rowIndex, eachTerm);
			
			Label termIDCell = new Label(2, rowIndex, owlFunction.getOntologyTermID(matchedOntologyTerm));
			
			if(!synonymForOntologyTerm.equals("")){
				matchedOntologyTerm = "Synonym match: " + synonymForOntologyTerm + "; The ontology term: " + matchedOntologyTerm;
			}
			
			Label ontologyTermCell = new Label(1, rowIndex, matchedOntologyTerm);
			
			Label scoreCell = new Label(3, rowIndex, "" + bestScore);
			
			sheet.addCell(originalTermCell);
			
			sheet.addCell(ontologyTermCell);
			
			sheet.addCell(termIDCell);
			
			sheet.addCell(scoreCell);
			
			
			
//			if(bestScore < 90){
//				
//				for(Entry<String, Double> eachEntry : candidateMatching.entrySet()){
//					
//					String term = eachEntry.getKey();
//					
//					Double similarity = eachEntry.getValue();
//					
//					String HPOId = candidateHPOId.get(term);
//					
//					originalTermCell = new Label(0, rowIndex, eachTerm);
//					
//					ontologyTermCell = new Label(1, rowIndex, term);
//					
//					termIDCell = new Label(2, rowIndex, HPOId);
//					
//					scoreCell = new Label(3, rowIndex, "" + similarity);
//					
//					//sheet.addCell(originalTermCell);
//					
//					sheet.addCell(ontologyTermCell);
//					
//					sheet.addCell(termIDCell);
//					
//					sheet.addCell(scoreCell);
//					
//					rowIndex++;
//				}
//			}
			
			double bestScoreForSympotom = 0;
			
			String matchedSympotom = "";
			
			for(String sympotom : listOfSympotoms){
				
				double similiarityScore = matchingModel.stringMatching(eachTerm, sympotom, false);
				
				if(bestScoreForSympotom < similiarityScore){
					
					bestScoreForSympotom = similiarityScore;
					
					matchedSympotom = sympotom;
					
				}
			}
			
			Label symptomCell = new Label(4, rowIndex, matchedSympotom);
			
			Label scoreForSymptomCell = new Label(5, rowIndex, "" + bestScoreForSympotom);
			
			sheet.addCell(symptomCell);
			
			sheet.addCell(scoreForSymptomCell);
			
			rowIndex++;
		}
		
		workbook.write();
		workbook.close(); 
	}
}
