package plugins.snpannotator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SnpAnnotator {

	public SnpAnnotator() throws IOException, InterruptedException{
		//String[] testIds = {"rs4637157"};
		
		String[] testIds = RsList.getRsList();
		
		System.out.println("SNP RS identifier\tClinical Association");
		for(String rs : testIds){
			System.out.println(rs + "\t" + getNCBIannotation(rs));
			Thread.sleep(100);
		}
		
		
//		Pattern p = Pattern.compile(".+jaap.+");
//	    Matcher m = p.matcher("henkjaapbert");
//	    System.out.println(m.matches());
		
	}
	
	private String getNCBIannotation(String snpRsId) throws IOException{
		 URL url = new URL("http://www.ncbi.nlm.nih.gov/projects/SNP/snp_ref.cgi?rs=" + snpRsId);
		 String bla = "";
	//	 URL yahoo = new URL("http://www.yahoo.com/");
		    URLConnection yahooConnection = url.openConnection();
		    BufferedReader in = new BufferedReader(new InputStreamReader(
		        yahooConnection.getInputStream()));
		    String inputLine;

		    while ((inputLine = in.readLine()) != null){
		//      System.out.println(inputLine);
		  //  	bla += inputLine;
		    	
		    	Pattern p = Pattern.compile(".+<strong>Clinical Association:</strong></td><td  class=\"text10\" bgcolor=\"#f1f1f1\">(.+?)</td></TR>.+");
				    
				    Matcher m = p.matcher(inputLine);
				    if(m.matches()){
//				    	System.out.println("MATCH DURING PARSE\n"+inputLine);
				    	bla = m.group(1);
				    	break;
				    }
				    
		    	
		    }
		    in.close();
		 
		  //  Pattern p = Pattern.compile("<strong>Clinical Association:</strong></td><td  class=\"text10\" bgcolor=\"#f1f1f1\">(.+)</td></TR>");
//		    Pattern p = Pattern.compile("Clinical(.+)td");
//		    
//		    Matcher m = p.matcher(bla);
//		    
//		    if(m.matches()){
//		    	bla = m.group();
//		    }else{
//		    	System.out.println("No match for " + snpRsId);
//		    }
		    
		    
		 return bla;
	}
	
	
	public static void main(String[] args) throws IOException, InterruptedException {
		new SnpAnnotator();
	}

}
