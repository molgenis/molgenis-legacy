///* Date:        May 26, 2010
// * Template:	PluginScreenJavaTemplateGen.java.ftl
// * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
// * 
// * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
// */
//
//package plugins.tool;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//
//import molgenis.compute.Input;
//import molgenis.compute.Operation;
//import molgenis.compute.Output;
//
//import org.molgenis.framework.db.Database;
//import org.molgenis.framework.db.DatabaseException;
//import org.molgenis.framework.ui.PluginModel;
//import org.molgenis.framework.ui.ScreenMessage;
//import org.molgenis.framework.ui.ScreenModel;
//import org.molgenis.framework.ui.html.ActionInput;
//import org.molgenis.framework.ui.html.HtmlInput;
//import org.molgenis.framework.ui.html.TextInput;
//import org.molgenis.framework.ui.html.XrefInput;
//import org.molgenis.model.MolgenisModelException;
//import org.molgenis.model.elements.Entity;
//import org.molgenis.util.Tuple;
//
//import plugins.tool.computeframework.ComputeJob;
//import plugins.tool.computeframework.ComputeManager;
//import app.JDBCMetaDatabase;
//
//public class ToolRunner extends PluginModel {
//	private Operation selectedOperation = null;
//	private List<Input> inputParams = null;
//	private List<Output> outputParams = null;
//	private List<MyComputeJob> currentJobs = new ArrayList<MyComputeJob>();
//	private ComputeManager computeManager = new SimpleComputeManager();
//	private List<Operation> operations = null;
//
//	public ToolRunner(String name, ScreenModel parent) {
//		super(name, parent);
//	}
//
//	@Override
//	public String getViewName() {
//		return "plugins_tool_ToolRunner";
//	}
//
//	@Override
//	public String getViewTemplate() {
//		return "plugins/tool/ToolRunner.ftl";
//	}
//
//	public List<HtmlInput> getInputs() throws DatabaseException,
//			MolgenisModelException, InstantiationException,
//			IllegalAccessException, ClassNotFoundException {
//		List<HtmlInput> result = new ArrayList<HtmlInput>();
//
//		// show dialog to select operation
//		{
//			XrefInput xrefInput = new XrefInput("operation", null);
//			xrefInput.setXrefEntity(Operation.class.getName());
//			xrefInput.setXrefField("Id");
//			xrefInput.setXrefLabels((Arrays.asList(new String[] { "Tool_name",
//					"Name" })));
//
//			ActionInput action = new ActionInput("action_select",
//					ActionInput.Type.CUSTOM);
//			action.setLabel("Select the operation");
//
//			result.add(xrefInput);
//			result.add(action);
//		}
//
//		// show dialog to run the selected operation
//		if (inputParams != null) {
//			for (Input i : inputParams) {
//				// depending on type we have to show proper types
//
//				// first check the primitive types
//				try {
//					String inputClassName = String.format(
//							"org.molgenis.framework.ui.html.%sInput", i
//									.getType_name().toString());
//					logger.info("Trying to load class " + inputClassName);
//					
//					//new XyzInput(name)
//					HtmlInput input = (HtmlInput) Class.forName(inputClassName)
//							.getConstructor(String.class).newInstance(i.getName());
//					result.add(input);
//				} catch (Exception e) {
//					e.printStackTrace();
//					// obviously didn't work, we will try to get the entity
//					// instead
//
//					// FIXME this is evil^2
//
//					// then check the generated data types
//					try {
//						JDBCMetaDatabase meta = new JDBCMetaDatabase();
//						Entity entity = meta.getEntity(i.getType_name());
//
//						XrefInput xrefInput = new XrefInput(i.getName(), null);
//						xrefInput.setXrefEntity("molgenis.compute."
//								+ entity.getName());
//						xrefInput.setXrefField(entity.getPrimaryKey().getName());
//						xrefInput.setXrefLabels(entity.getXrefLabels());
//
//						result.add(xrefInput);
//					} catch (Exception e2) {
//						logger.debug(e2.getMessage());
//						e2.printStackTrace();
//					}
//				}
//			}
//
//			ActionInput action = new ActionInput("action_run",
//					ActionInput.Type.CUSTOM);
//			action.setLabel("Run this operation");
//			result.add(action);
//
//		}
//
//		// show currently running jobs
//		for (MyComputeJob job : this.currentJobs) {
//			result.add(new TextInput("TEST", job.getId() + "+"
//					+ job.getCommandLine()));
//		}
//
//		return result;
//	}
//
//	@Override
//	public void handleRequest(Database db, Tuple request) {
//		// reload the list of available operations
//		try {
//			List<Operation> operations = db.query(Operation.class).sortASC(
//					"tool").sortASC("Name").find();
//
//			// handle the select operation
//			if ("action_select".equals(request.getAction())) {
//
//				// get the operation
//				List<Operation> oList = db.query(Operation.class).equals("Id",
//						request.getInt("operation")).find();
//				if (oList.size() > 0)
//					selectedOperation = oList.get(0);
//				else
//					throw new Exception("Unknown operation selected");
//
//				// get the inputs
//				this.inputParams = db.query(Input.class).equals("tool",
//						selectedOperation.getTool()).find();
//			} else if ("action_run".equals(request.getAction())) {
//
//				// get the values from the ui
//				Map<String, String> values = new LinkedHashMap<String, String>();
//				for (Input i : this.inputParams) {
//					values.put(i.getName(), request.getString(i.getName()));
//				}
//
//
//				// configure a job
//				MyComputeJob job = new MyComputeJob(selectedOperation,
//						inputParams, outputParams, values);
//
//				// add to the compute service (first locally)
//				computeManager.setJob(job);
//
//			}
//			// else if ("action_monitor".equals(request.getAction())) {
//			this.currentJobs.clear();
//			for (ComputeJob j : computeManager.list())
//				this.currentJobs.add((MyComputeJob) j);
//			// }
//
//		} catch (Exception e) {
//			this.setMessages(new ScreenMessage(e.getMessage(), false));
//		}
//
//		// replace example below with yours
//		// try
//		// {
//		// Database db = this.getDatabase();
//		// String action = request.getString("__action");
//		//		
//		// if( action.equals("do_add") )
//		// {
//		// Experiment e = new Experiment();
//		// e.set(request);
//		// db.add(e);
//		// }
//		// } catch(Exception e)
//		// {
//		// //e.g. show a message in your form
//		// }
//	}
//
//	@Override
//	public void reload(Database db) {
//		// try
//		// {
//		// Database db = this.getDatabase();
//		// Query q = db.query(Experiment.class);
//		// q.like("name", "test");
//		// List<Experiment> recentExperiments = q.find();
//		//			
//		// //do something
//		// }
//		// catch(Exception e)
//		// {
//		// //...
//		// }
//	}
//
//	@Override
//	public boolean isVisible() {
//		// you can use this to hide this plugin, e.g. based on user rights.
//		// e.g.
//		// if(!this.getLogin().hasEditPermission(myEntity)) return false;
//		return true;
//	}
//	
//	public List<Object> getVisibleChildren()
//	{
//		return new ArrayList<Object>();
//	}
//}
