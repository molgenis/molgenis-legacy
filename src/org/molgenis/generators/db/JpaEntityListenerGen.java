package org.molgenis.generators.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.molgenis.MolgenisOptions;
import org.molgenis.generators.ForEachEntityGenerator;
import org.molgenis.generators.GeneratorHelper;
import org.molgenis.generators.sql.MySqlCreateClassPerTableGen;
import org.molgenis.generators.ui.PluginControllerGen;
import org.molgenis.model.MolgenisModel;
import org.molgenis.model.elements.Entity;
import org.molgenis.model.elements.Form;
import org.molgenis.model.elements.Model;
import org.molgenis.model.elements.Plugin;
import org.molgenis.model.elements.UISchema;

import freemarker.template.Template;

public class JpaEntityListenerGen extends ForEachEntityGenerator
{
	public static final transient Logger logger = Logger.getLogger(PluginControllerGen.class);

	public JpaEntityListenerGen() {
		super(true); //include abstract entities
	}
	
	@Override
	public String getDescription()
	{
		return "Generates Entity Listener Skeleton for JPA";
	}
	
	
	@Override
	public String getType()
	{
		return "EntityListener";
	}
}