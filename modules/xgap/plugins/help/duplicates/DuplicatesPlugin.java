/* Date:        May 15, 2009
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.screen.PluginScreenJavaTemplateGen 3.3.0-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.help.duplicates;

import java.util.ArrayList;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import decorators.NameConvention;

public class DuplicatesPlugin extends PluginModel<Entity> {

	private static final long serialVersionUID = 3285351881759871383L;
	public String input;
	public String output;

	// public String unique;

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	// public String getUnique() {
	// return unique;
	// }
	//
	// public void setUnique(String unique) {
	// this.unique = unique;
	// }

	public DuplicatesPlugin(String name, ScreenController<?> parent) {
		super(name, parent);
	}

	@Override
	public String getViewName() {
		return "plugins_help_duplicates_DuplicatesPlugin";
	}

	@Override
	public String getViewTemplate() {
		return "plugins/help/duplicates/DuplicatesPlugin.ftl";
	}
	
	public static ArrayList<String> renameDuplicates(ArrayList<String> inputs){
		ArrayList<String> out = new ArrayList<String>();
		
		for(String s : inputs){
			if(out.contains(s)){
				boolean highestDupFound = false;
				int dupNumber = 1;
				while(!highestDupFound){
					if(out.contains(s + "_DUP" + dupNumber)){
						dupNumber++;
					}else{
						out.add(s + "_DUP" + (dupNumber));
						highestDupFound = true;
					}
				}
			}else{
				out.add(s);
			}
		}
		
		return out;
	}

	@Override
	public void handleRequest(Database db, Tuple request) {
		if (request.getString("__action") != null) {
			if (request.getString("__action").equals("convertNames")) {
				String rawInput = request.getString("input");

				if (rawInput != null) {
					this.setInput(rawInput);

					String[] split = rawInput.split("\\r?\\n");

					output = "";

					ArrayList<String> inputs = new ArrayList<String>();

					for (String s : split) {
						if (s.length() != 0) {
							s = s.trim();
							try
							{
								s = NameConvention.escapeEntityNameStrict(s);
							}
							catch (DatabaseException e)
							{
								e.printStackTrace();
							}
							inputs.add(s);
						}
					}
					
					ArrayList<String> outputs = renameDuplicates(inputs);
					
					for(String s : outputs){
						output += s + "\n";
					}

					this.setOutput(output);
				} else {
					this.setInput(null);
					this.setOutput(null);
				}

			} else if (request.getString("__action").equals("loadExample")) {
				this.setInput(example());
			} else if (request.getString("__action").equals("clear")) {
				this.setInput(null);
				this.setOutput(null);
			}
		}
	}

	private String example() {
		String example = "At5g44840\n";
		example += "At1g31870\n";
		example += "At4g36730\n";
		example += "At1g31870\n";
		example += "At5g44840\n";
		example += "At1g31870\n";
		example += "At4g36730\n";
		example += "At5g44840\n";
		example += "At5g44840\n";
		example += "At1g63660\n";
		example += "At4g24220\n";
		example += "At5g44840\n";
		return example;
	}

	@Override
	public void reload(Database db) {

	}

}
