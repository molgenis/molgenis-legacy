/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package umcg.genetica.text;

import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author harmjan
 */
public class Strings {

    public static final Pattern tab = Pattern.compile("\t");
    public static final Pattern comma = Pattern.compile(",");
    public static final Pattern semicolon = Pattern.compile(";");
    public static final Pattern colon = Pattern.compile(":");
    public static final Pattern pipe = Pattern.compile("\\|");
    public static final Pattern forwardslash = Pattern.compile("/");
    public static final Pattern backwardslash = Pattern.compile("\\\\");
    public static final Pattern dot = Pattern.compile("\\.");
    public static final Pattern space = Pattern.compile(" ");

    public static String concat(String[] s, Pattern t) {

	StringBuilder output = new StringBuilder();
	for (int i = 0; i < s.length; i++) {
	    if (i == 0) {
		output.append(s[i]);
	    } else {
		output.append(t.toString()).append(s[i]);
	    }
	}
	return output.toString();
    }

    public static String concat(List<String> s, Pattern t) {
	String[] data = s.toArray(new String[0]);
	return concat(data, t);
    }

    public static String concat(String[] s, Pattern t, int start, int end){
	String[] data = new String[end-start];
	for(int i=start; i<end; i++){
	    data[i-start] = s[i];
	}
	return concat(data, t);
    }


}
