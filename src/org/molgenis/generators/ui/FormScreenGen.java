package org.molgenis.generators.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

import org.apache.log4j.Logger;
import org.molgenis.MolgenisOptions;
import org.molgenis.generators.Generator;
import org.molgenis.generators.GeneratorHelper;
import org.molgenis.model.elements.Entity;
import org.molgenis.model.elements.Form;
import org.molgenis.model.elements.Model;
import org.molgenis.model.elements.UISchema;

import freemarker.template.Template;

public class FormScreenGen extends Generator
{
	public static final transient Logger logger = Logger.getLogger(FormScreenGen.class);

	@Override
	public String getDescription()
	{
		return "Generates form screens.";
	}
	
	
	@Override
	public void generate(Model model, MolgenisOptions options) throws Exception
	{
		generateForm(model, options, model.getUserinterface());
	}
	
	private void generateForm(Model model, MolgenisOptions options, UISchema schema) throws Exception
	{
		Template template = createTemplate( "/"+getClass().getSimpleName()+".java.ftl" );
		Map<String, Object> templateArgs = createTemplateArguments(options);
		
		for(UISchema screen: schema.getChildren())
		{
			if(screen.getClass() == Form.class)
			{
				
				templateArgs.put( "form", screen );
				((Entity) ((Form)screen).getRecord()).getAllFields().firstElement();
				

				UISchema parent = screen.getParent();
				while (parent!=null && !parent.getClass().equals(Form.class)) //gets the parent form
					parent = parent.getParent();
				templateArgs.put( "parent_form", parent);
				templateArgs.put( "model", model );
				templateArgs.put( "package", APP_DIR + ".ui" );
				
				File targetDir = new File( this.getSourcePath(options) + APP_DIR + "/ui/" );
				targetDir.mkdirs();

				File targetFile = new File( targetDir + "/" + GeneratorHelper.firstToUpper( screen.getClassName() ) + "Form.java" );
				OutputStream targetOut = new FileOutputStream( targetFile );

				template.process( templateArgs, new OutputStreamWriter( targetOut ) );
				targetOut.close();

				logger.info("generated " + targetFile);				
			}

			//get children
			generateForm(model, options, screen);
		}
	}
}
