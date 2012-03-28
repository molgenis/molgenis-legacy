/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package umcg.genetica.io.text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import umcg.genetica.text.Strings;

/**
 *
 * @author harmjan
 */
public class TextFile {

    public static final Pattern tab = Strings.tab;
    public static final Pattern space = Strings.space;
    public static final Pattern colon = Strings.colon;
    public static final Pattern semicolon = Strings.semicolon;
    public static final Pattern comma = Strings.comma;
    
    protected BufferedReader in;
    protected String loc;
    public static final boolean W = true;
    public static final boolean R = false;
    protected BufferedWriter out;
    protected boolean writeable;
    protected static final String ENCODING = "ISO-8859-1";
    private boolean gzipped;
    private int buffersize = 4096;

    public TextFile(String loc, boolean mode) throws IOException {
	this.writeable = mode;
	this.loc = loc;
	if (loc.endsWith(".gz")) {
	    gzipped = true;
	}
	open();
    }

    public TextFile(String loc, boolean mode, int buffersize) throws IOException {
	this.writeable = mode;
	this.loc = loc;
	this.buffersize = buffersize;
	if (loc.endsWith(".gz")) {
	    gzipped = true;
	}
	open();
    }

    public void open() throws IOException {

	File locHandle = new File(loc);
	if (!locHandle.exists() && !writeable) {
	    System.out.println("Could not find file: " + loc);
	    System.exit(0);
	} else {
	    if (writeable) {
		if (gzipped) {
		    GZIPOutputStream gzipOutputStream = new GZIPOutputStream(new FileOutputStream(loc));
		    out = new BufferedWriter(new OutputStreamWriter(gzipOutputStream), buffersize);
		} else {
		    out = new BufferedWriter(new FileWriter(locHandle), buffersize);
		}
	    } else {
		if (gzipped) {
		    GZIPInputStream gzipInputStream = new GZIPInputStream(new FileInputStream(loc));
		    in = new BufferedReader(new InputStreamReader(gzipInputStream, "US-ASCII"));
		} else {
//                System.out.println("Opening file: "+loc);
		    in = new BufferedReader(new InputStreamReader(new FileInputStream(locHandle), ENCODING), 8096);
		}
	    }
	}

    }

    public String readLine() throws IOException {
	return in.readLine();
    }

    public void write(String line) throws IOException {
	out.write(line);
    }

    public void close() throws IOException {
//        System.out.println("Closing "+loc);
	if (writeable) {
	    out.close();
	} else {
	    in.close();
	}
    }

    public String[] readLineElems(Pattern p) throws IOException {
	return readLineElemsReturnReference(p);
    }

    public String[] readLineElemsReturnReference(Pattern p) throws IOException {
	if (in != null) {
	    String ln = readLine();
	    if (ln != null) {
		String[] elems = p.split(ln);
		ln = null;
		return elems;
	    } else {
		return null;
	    }
	} else {
	    return null;
	}
    }

    // to prevent MemoryOutOfBoundsExceptions when using only a subset of Pattern.split();
    public String[] readLineElemsReturnObjects(Pattern p) throws IOException {
	if (in != null) {
	    String ln = readLine();
	    if (ln != null) {
		String[] origelems = p.split(ln);
		String[] returnelems = new String[origelems.length];
		for (int i = 0; i < origelems.length; i++) {
		    returnelems[i] = new String(origelems[i]);
		}
		ln = null;
		return returnelems;
	    } else {
		return null;
	    }
	} else {
	    return null;
	}


    }

    public int countLines() throws IOException {
	String ln = readLine();
	int ct = 0;
	while (ln != null) {
	    if (ln.trim().length() > 0) {
		ct++;
	    }
	    ln = readLine();
	}
	close();
	open();
	return ct;
    }

    public int countCols(Pattern p) throws IOException {
	String ln = readLine();
	int ct = 0;
	if (ln != null) {
	    String[] elems = p.split(ln);
	    ct = elems.length;
	}
	close();
	open();
	return ct;
    }

    public String[] readAsArray() throws IOException {
	int numLines = countLines();
	String ln = readLine();
	String[] data = new String[numLines];
	int i = 0;
	while (ln != null) {
	    if (ln.trim().length() > 0) {
		data[i] = ln;
		i++;
	    }
	    ln = in.readLine();
	}
	return data;
    }

    public ArrayList<String> readAsArrayList() throws IOException {

	String ln = readLine();
	ArrayList<String> data = new ArrayList<String>();
	int i = 0;
	while (ln != null) {
	    if (ln.trim().length() > 0) {
		data.add(ln);
		i++;
	    }
	    ln = in.readLine();
	}
	return data;
    }

    public void writeln(String line) throws IOException {
	out.write(line + "\n");
    }

    public void writeln() throws IOException {
	out.newLine();
    }

    public void writelnTabDelimited(Object[] vals) throws IOException {
	String delim = "";
	for (Object val : vals) {
	    out.write(delim);
	    out.write(val.toString());
	    delim = "\t";
	}
	writeln();
    }

    public void writelnDelimited(Object[] vals, Pattern p) throws IOException {
	String delim = "";
	for (Object val : vals) {
	    out.write(delim);
	    out.write(val.toString());
	    delim = p.pattern();
	}
	writeln();
    }

    public Map<String, String> readAsHashMap(int col1, int col2) throws IOException {
	HashMap<String, String> output = new HashMap<String, String>();
	String[] elems = readLineElems(tab);
	while (elems != null) {
	    if (elems.length > 1) {
		output.put(elems[col1], elems[col2]);
		elems = readLineElems(tab);
	    }
	}
	return output;
    }

    public void writeList(List l) throws IOException {
	for(Object e: l){
	    this.writeln(e.toString());
	}
    }

}
