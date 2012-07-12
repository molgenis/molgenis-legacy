package plugins.findingProxy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.molgenis.MolgenisOptions;
import org.molgenis.Version;
import org.molgenis.generators.GeneratorHelper;
import org.molgenis.model.MolgenisModel;
import org.molgenis.model.elements.Entity;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Template;

public class RScriptGenerator {
	
	private testModel model = new testModel();

	public static void main (String args[]) throws Exception{
		
		RScriptGenerator test = new RScriptGenerator();
		
		test.start();
		
	}
	
	public testModel getModel()
	{
		return model;
	}

	public void setModel(testModel model)
	{
		this.model = model;
	}

	public void start() throws Exception {
		
		Template template = createTemplate("/"+this.getClass().getSimpleName()+".R.ftl" );
		
		Map<String, Object> templateArgs = createTemplateArguments();
		
		File target = new File("apps/catalogue/plugins/Rscript/generated.R" );
		
		target.getParentFile().mkdirs();
		
		templateArgs.put("model", model);
		
		OutputStream targetOut = new FileOutputStream(target);
		
		template.process(templateArgs, new OutputStreamWriter(targetOut));
		
		targetOut.close();
		
	}
	
	public Template createTemplate(String path) throws Exception
	{
		freemarker.template.Configuration cfg = new freemarker.template.Configuration();
		cfg.setObjectWrapper(new freemarker.template.DefaultObjectWrapper());

		ClassTemplateLoader loader1 = new ClassTemplateLoader(getClass(), "");
		ClassTemplateLoader loader2 = new ClassTemplateLoader(GeneratorHelper.class, "");
		TemplateLoader[] loaders = new TemplateLoader[]
		{ loader1, loader2 };
		MultiTemplateLoader mLoader = new MultiTemplateLoader(loaders);
		// cfg.setClassForTemplateLoading( this.getClass(), "" ); // NOTE:
		// without
		cfg.setTemplateLoader(mLoader);

		// the '/' on
		// either end
		return cfg.getTemplate(path);
	}
	
	public Map<String, Object> createTemplateArguments()
	{
		Map<String, Object> args = new TreeMap<String, Object>();
		// args.put("stringtools", new StringTools());
		Calendar calendar = Calendar.getInstance();
		args.put("year", calendar.get(Calendar.YEAR));
		DateFormat formatter = new SimpleDateFormat("MMMM d, yyyy, HH:mm:ss", Locale.US);
		args.put("datetime", formatter.format(new Date()));
		formatter = new SimpleDateFormat("MMMM d, yyyy", Locale.US);
		args.put("date", formatter.format(new Date()));
		// args.put( "date", calendar.get( Calendar.YEAR ) + "/" +
		// (calendar.get( Calendar.MONTH ) + 1) + "/" + calendar.get(
		// Calendar.DAY_OF_MONTH ) );
		args.put("generator", this.getClass().getName());
		args.put("version", Version.convertToString());
		return args;
	}
	
}
