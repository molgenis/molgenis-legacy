/* Date:        May 15, 2009
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.screen.PluginScreenJavaTemplateGen 3.3.0-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.help.naming;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import decorators.NameConvention;

public class NamingPlugin extends PluginModel<Entity> {

	private static final long serialVersionUID = -5003817917974881648L;
	public String input;
	public String output;
	//public String unique;

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
	

//	public String getUnique() {
//		return unique;
//	}
//
//	public void setUnique(String unique) {
//		this.unique = unique;
//	}

	public NamingPlugin(String name, ScreenController<?> parent) {
		super(name, parent);
	}

	@Override
	public String getViewName() {
		return "plugins_help_naming_NamingPlugin";
	}

	@Override
	public String getViewTemplate() {
		return "plugins/help/naming/NamingPlugin.ftl";
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
					for (String s : split) {
						try {
							String escape = NameConvention.escapeEntityNameStrict(s);
							if(escape.length() == 0){
								output += "INVALID NAME: ALL CHARACTERS ARE ESCAPED\n";
							}else{
								output += escape + "\n";
							}
						} catch (DatabaseException e) {
							output = e.toString();
							e.printStackTrace();
							break;
						}
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
		String example = "P11/M54-248e\n";
		example += "TG68_12.14\n";
		example += "CT120bp_11.x/12.3\n";
		example += "P11/M48-292p\n";
		example += "P11/M48-84e\n";
		example += "E36/M54-11e\n";
		example += "P11/M48-185p\n";
		example += "CT156_12.72\n";
		example += "TG473_12.91\n";
		example += "E35/M57-3e\n";
		return example;
	}

	@Override
	public void reload(Database db) {

	}


}
