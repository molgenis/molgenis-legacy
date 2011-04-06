package org.molgenis.generators.cpp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.molgenis.MolgenisOptions;
import org.molgenis.generators.sql.MySqlCreateClassPerTableGen;
import org.molgenis.model.MolgenisModel;
import org.molgenis.model.elements.Entity;
import org.molgenis.model.elements.Model;

import freemarker.template.Template;

public class MakeFileGen extends MySqlCreateClassPerTableGen
{
	public static final transient Logger logger = Logger.getLogger(MakeFileGen.class);
	
	@Override
	public String getDescription()
	{
		return "Generates CPP cmake file";
	}
	
	@Override
	public void generate(Model model, MolgenisOptions options)
			throws Exception
	{
		Template template = createTemplate( "/"+this.getClass().getSimpleName()+".cmake.ftl" );
		Map<String, Object> templateArgs = createTemplateArguments(options);
		
		List<Entity> entityList = model.getEntities();
		MolgenisModel.sortEntitiesByDependency(entityList,model);
		File target = new File( this.getCPPSourcePath(options) + "/CMakeLists.txt" );
		target.getParentFile().mkdirs();
		
		templateArgs.put("model", model );
		templateArgs.put("entities",entityList);
		templateArgs.put("JavaHome",System.getProperty("java.home").toString());
		templateArgs.put("UserHome",System.getProperty("user.dir").replace("\\", "/").toString());
		templateArgs.put("EXECNAME","${EXECNAME}".toString());
		templateArgs.put("EXECUTABLE","${EXECUTABLE}".toString());
		OutputStream targetOut = new FileOutputStream( target );
		template.process( templateArgs, new OutputStreamWriter( targetOut ) );
		targetOut.close();
		logger.info("generated " + target);
	}
}
