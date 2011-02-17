package org.molgenis.generators.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

import org.apache.log4j.Logger;
import org.molgenis.MolgenisOptions;
import org.molgenis.generators.ForEachEntityGenerator;
import org.molgenis.generators.GeneratorHelper;
import org.molgenis.model.elements.Entity;
import org.molgenis.model.elements.Form;
import org.molgenis.model.elements.Model;
import org.molgenis.model.elements.UISchema;

import freemarker.template.Template;

public class HtmlFormGen extends ForEachEntityGenerator
{
	public static final transient Logger logger = Logger.getLogger(HtmlFormGen.class);

	@Override
	public String getDescription()
	{
		return "Generates html forms for each entity.";
	}
	
	@Override
	public String getType()
	{
		return "Form";
	}
}
