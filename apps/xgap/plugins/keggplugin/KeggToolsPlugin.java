/* Date:        July 24, 2009
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.screen.PluginScreenJavaTemplateGen 3.3.0-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.keggplugin;

import miscellaneous.kegg.KEGGGene;
import miscellaneous.kegg.KEGGOrthologue;
import miscellaneous.kegg.KEGGTools;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

public class KeggToolsPlugin extends PluginModel<Entity>
{

	private static final long serialVersionUID = 4365180891730331426L;

	public KeggToolsPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	public String	sourceOrganism;
	public String	targetOrganism;
	public String	input;

	public String	outputSimple		= "";
	public String	outputAdvanced	= "";

	public String getSourceOrganism()
	{
		return sourceOrganism;
	}

	public void setSourceOrganism(String sourceOrganism)
	{
		this.sourceOrganism = sourceOrganism;
	}

	public String getTargetOrganism()
	{
		return targetOrganism;
	}

	public void setTargetOrganism(String targetOrganism)
	{
		this.targetOrganism = targetOrganism;
	}

	public String getInput()
	{
		return input;
	}

	public void setInput(String input)
	{
		this.input = input;
	}

	public String getOutputSimple()
	{
		return outputSimple;
	}

	public void setOutputSimple(String outputSimple)
	{
		this.outputSimple = outputSimple;
	}

	public String getOutputAdvanced()
	{
		return outputAdvanced;
	}

	public void setOutputAdvanced(String outputAdvanced)
	{
		this.outputAdvanced = outputAdvanced;
	}

	@Override
	public String getViewName()
	{
		return "plugins_keggplugin_KeggToolsPlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/keggplugin/KeggToolsPlugin.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{

		String sourceOrganism = request.getString("sourceOrganism");
		String targetOrganism = request.getString("targetOrganism");

		this.setSourceOrganism(sourceOrganism);
		this.setTargetOrganism(targetOrganism);
		this.setInput(input);

		String action = request.getString("__action");
		if (action.equals("example"))
		{
			this.setSourceOrganism("sce");
			this.setTargetOrganism("hsa");
			this.setInput("PMR1\nIME2\nCCC1");
		}

		if (action.equals("doAnnotation"))
		{
			String outputSimple = "source" + "\t" + "entry" + "\n";
			String outputAdvanced = "source" + "\t" + KEGGGene.toStringMediumHeader("\t");

			String input = request.getString("inputIdList");
			
			if(input == null)
			{
				//shoudl throw/display exception
				return;
			}
			
			input = input.replace(" ", "");
			String[] ids = input.split("\\r?\\n");

			for (String s : ids)
			{
				String id = s.replace("\r", "");
				try
				{
					KEGGGene sourceGene = KEGGTools.getKeggGene(sourceOrganism + ":" + id);
	
					outputSimple += id + "\t" + sourceGene.getEntry() + "\n";
					outputAdvanced += id + "\t" + sourceGene.toStringMedium("\t");
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			
			this.setOutputSimple(outputSimple);
			this.setOutputAdvanced(outputAdvanced);
		}

		if (action.equals("doOrthology"))
		{
			String outputSimple = "source" + "\t" + "entry" + "\n";
			String outputAdvanced = "source" + "\t" + KEGGGene.toStringMediumHeader("\t");

			String input = request.getString("inputIdList");
			
			if(input == null)
			{
				//shoudl throw/display exception
				return;
			}
			
			input = input.replace(" ", "");
			String[] ids = input.split("\\r?\\n");

			for (String s : ids)
			{
				String id = s.replace("\r", "");

				try
				{

					KEGGGene sourceGene = KEGGTools.getKeggGene(sourceOrganism + ":" + id);
					KEGGOrthologue orthology = KEGGTools.getClosestOrthologue(sourceGene.getEntry(), targetOrganism);
					KEGGGene targetGene = KEGGTools.getKeggGene(orthology.getTargetEntry());

					outputSimple += id + "\t" + orthology.getTargetEntry() + "\n";
					// outputAdvanced += id + "\t" + orthology.getTargetEntry() + "\t" +
					// sourceGene.getDefinition() + "\t" + targetGene.getDefinition() +
					// "\n";

					outputAdvanced += id + "\t" + targetGene.toStringMedium("\t");

				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}

			this.setOutputSimple(outputSimple);
			this.setOutputAdvanced(outputAdvanced);

		}
	}

	@Override
	public void reload(Database db)
	{
		// try
		// {
		// Database db = this.getDatabase();
		// Query q = db.query(Experiment.class);
		// q.like("name", "test");
		// List<Experiment> recentExperiments = q.find();
		//			
		// //do something
		// }
		// catch(Exception e)
		// {
		// //...
		// }
	}

}
