package plugins.predictionModel;

import java.util.HashMap;
import java.util.regex.Pattern;

class Element {
	
	public static void main(String args[]){
		
		String first = "NUMBER";
		
		String second = "NUMBER123";
		
		Pattern p = Pattern.compile(".*" + first + ".*");
		
		Pattern p2 = Pattern.compile(".*" + second + ".*");
		
		if(p.pattern().equals(p2.pattern())){
			System.out.println(first);
		}else{
			System.out.println("Sorry!");
		}
		
	}
	
}
