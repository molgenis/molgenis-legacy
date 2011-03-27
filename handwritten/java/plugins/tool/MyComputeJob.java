//package plugins.tool;
//
//import java.io.IOException;
//import java.io.StringReader;
//import java.io.StringWriter;
//import java.util.List;
//import java.util.Map;
//
//import molgenis.compute.Input;
//import molgenis.compute.Operation;
//import molgenis.compute.Output;
//import plugins.tool.computeframework.ComputeJobImp;
//import freemarker.template.Configuration;
//import freemarker.template.Template;
//import freemarker.template.TemplateException;
//
//public class MyComputeJob extends ComputeJobImp {
//
//	
//	public MyComputeJob(Operation selectedOperation, List<Input> inputParams,
//			List<Output> outputParams, Map<String,String> values) throws IOException, TemplateException {
//		
//		//string to store the command
//		StringWriter commandline = new StringWriter();
//		
//		//merge the template and the values to produce commandline
//		String templateStr=selectedOperation.getCommandTemplate();
//		Template t = new Template("name", new StringReader(templateStr),
//		               new Configuration());
//		t.process(values, commandline);
//		
//		this.setCommandLine(commandline.toString());
//		
//		//TODO files
//	}
//
//}
