/* Date:        June 2, 2009
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.screen.PluginScreenJavaTemplateGen 3.3.0-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.cluster;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import matrix.AbstractDataMatrixInstance;
import matrix.general.DataMatrixHandler;

import org.molgenis.cluster.Analysis;
import org.molgenis.cluster.DataName;
import org.molgenis.cluster.DataSet;
import org.molgenis.cluster.DataValue;
import org.molgenis.cluster.Job;
import org.molgenis.cluster.ParameterName;
import org.molgenis.cluster.ParameterSet;
import org.molgenis.cluster.ParameterValue;
import org.molgenis.cluster.SelectedData;
import org.molgenis.cluster.SelectedParameter;
import org.molgenis.cluster.Subjob;
import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.util.DetectOS;
import org.molgenis.util.Entity;
import org.molgenis.util.HtmlTools;
import org.molgenis.util.Tuple;

import app.servlet.MolgenisServlet;

import plugins.cluster.demo.Millipede;
import plugins.cluster.helper.Command;
import plugins.cluster.helper.HelperFunctions;
import plugins.cluster.helper.LoginSettings;
import plugins.cluster.implementations.ClusterComputationResource;
import plugins.cluster.implementations.DatabaseJobManager;
import plugins.cluster.implementations.LocalComputationResource;
import plugins.cluster.interfaces.ComputationResource;
import decorators.NameConvention;
import filehandling.generic.MolgenisFileHandler;


/**
 * ClusterPlugin can run Rqtl analysis in parallel on a PBS cluster or locally.
 * 
 * @author joerivandervelde
 * 
 */
public class ClusterPlugin extends PluginModel<Entity>
{

	private static final long serialVersionUID = -3695891745215105075L;

	private DatabaseJobManager djm = null;
	private ComputationResource cr = null;
	private DataMatrixHandler dmh = null;
	private ClusterPluginModel model = new ClusterPluginModel();

	public ClusterPlugin(String name, ScreenModel<Entity> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugins_cluster_ClusterPlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/cluster/ClusterPlugin.ftl";
	}

	@Override
	public String getCustomHtmlHeaders()
	{
		String refresh = null;
		if (this.getModel().getRefreshRate().equals("off")){
			refresh = "";
		}else{
			refresh = "\n<meta http-equiv=\"refresh\" content=\"" + this.getModel().getRefreshRate() + "\">";
		}
		return "<script src=\"res/scripts/overlib.js\" language=\"javascript\"></script>" + refresh;
	}

	public ClusterPluginModel getModel(){
		return model;
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		try
		{
			// SCREEN 0 -> Create new job and proceed to SCREEN 1, or go to job
			// manager
			String action = request.getString("__action");
			if (action.equals("newClusterJob"))
			{
				// setServerSettings(request, db);
				String selectedComputeResource = request.getString("computeResource");
				model.setSelectedComputeResource(selectedComputeResource);
				model.setState("newjob1");
			}
			if (action.equals("viewJobManager"))
			{
				// setServerSettings(request, db);
				model.setState("jobmanager");
			}

			// SCREEN 1 -> Go back to SCREEN 0, or configure job/compute
			// properties and proceed to SCREEN 2
			if (action.equals("goBack"))
			{
				model.setLs(null);
				model.setState("main");
			}
			if (action.equals("toStep2"))
			{
				if (model.getSelectedComputeResource().equals("cluster")
						|| model.getSelectedComputeResource().equals("cloud"))
				{
					setServerSettings(request, db);
				}
				makeCandidateJob(db, request);
				model.setNrOfJobs(request.getInt("nJobs"));
				model.setState("newjob2");
			}

			// SCREEN 2 -> Select parameters and start the job, or go back to
			// SCREEN 1
			if (action.equals("startClusterJob"))
			{
				clusterJob(db, request);
				model.setState("jobmanager");
			}
			if (action.equals("toStep1"))
			{
				model.setState("newjob1");
			}

			// JOB MANAGER -> Go back to first screen, or delete job, or
			// resubmit subjob
			// NOTE: Reuses action.equals("goBack")
			if (action.equals("deleteJob"))
			{
				deleteJob(db, request);
			}
			if (action.equals("resubmitSubjob"))
			{
				resubmitSubjob(db, request);
			}
			if (action.equals("changeRefresh"))
			{
				this.getModel().setRefreshRate(request.getString("chosenRefresh"));
			}
		}
		catch (Exception e)
		{
			// e.g. show a message in your form
		}
	}

	private void resubmitSubjob(Database db, Tuple request) throws Exception
	{
		int sjId = request.getInt("resubmitSubjob");

		Subjob sj = db.find(Subjob.class, new QueryRule("id", Operator.EQUALS, sjId)).get(0);
		sj.setStatusCode(0);
		sj.setStatusText("Resubmitted");
		db.update(sj);

		Job parent = db.find(Job.class, new QueryRule("id", Operator.EQUALS, sj.getJob())).get(0);

		Command command = null;

		ComputationResource cr = null;

		if (parent.getComputeResource().equals("local"))
		{
			cr = new LocalComputationResource(new MolgenisFileHandler(db));
			// commands.add(new Command("R CMD BATCH ./run" + sj.getJob() +
			// "/run"+sj.getNr()+".R", false, false));
			command = new Command("R CMD BATCH ./run" + sj.getJob() + "/subjob" + sj.getNr() + ".R", false, false, true);
		}
		else if (parent.getComputeResource().equals("cluster"))
		{
			cr = new ClusterComputationResource(this.model.getLs());
			// commands.add(new Command("nohup qsub ~/run" + sj.getJob() +
			// "/run" + sj.getNr() + ".sh &", false, false));
			command = new Command("nohup qsub ~/run" + sj.getJob() + "/run" + sj.getNr() + ".sh &", false, false, true);
		}
		else if (parent.getComputeResource().equals("cloud"))
		{
			throw new Exception("Unimplemented for cloud!");
		}
		else if (parent.getComputeResource().equals("image"))
		{
			throw new Exception("Unimplemented for image!");
		}

		cr.executeCommand(command);
	}

	private void setServerSettings(Tuple request, Database db)
	{
		LoginSettings ls = new LoginSettings();
		ls.host = request.getString("serverAdress");
		ls.port = request.getString("serverPort");
		ls.user = request.getString("serverUser");
		ls.password = request.getString("serverPassword");
		model.setLs(ls);
	}

	private void makeCandidateJob(Database db, Tuple request) throws DatabaseException
	{
		// DatabaseJob job = new DatabaseJob();
		Job job = new Job();

		Date timestamp = new Date();
		String putativeName = request.getString("outputDataName");
		putativeName = NameConvention.escapeEntityNameStrict(putativeName);

		System.out.println("checking name: " + putativeName);
		if (HelperFunctions.checkIfNameExists(db, putativeName))
		{
			putativeName = NameConvention.escapeEntityNameStrict(putativeName + " at "
					+ HelperFunctions.dateTimeToMysqlFormat(timestamp));
		}

		job.setOutputDataName(putativeName);

		job.setTimestamp(HelperFunctions.dateTimeToMysqlFormat(timestamp));
		job.setAnalysis(request.getInt("selectedAnalysis"));
		job.setComputeResource(this.getModel().getSelectedComputeResource());

		model.setCandidateJob(job);
	}

	private void deleteJob(Database db, Tuple request) throws Exception
	{
		System.out.println("deleteJob called");

		int jobID = Integer.parseInt(request.getString("jobToDelete"));

		cr.cleanupJob(jobID);
		djm.deleteJob(jobID);

		System.out.println("deleteJob ended");
	}

	// TODO: dont use Tuple but list of parameters
	// then use function inbetween to map tuple-to-params
	// so the function is usable in regression tests :)
	private void clusterJob(Database db, Tuple request) throws IOException, DatabaseException, InstantiationException,
			IllegalAccessException
	{

		boolean dbSucces = false;
		int jobId = -1;

		// DatabaseJob startedJob = model.getCandidateJob();
		Job startedJob = model.getCandidateJob();
		Analysis analysis = null;
		try
		{
			// job toevoegen
			db.beginTx();
			db.add(startedJob);
			jobId = startedJob.getId();

			// used later on...
			analysis = db.find(Analysis.class, new QueryRule("id", Operator.EQUALS, startedJob.getAnalysis())).get(0);

			// subjobs aanmaken
			for (int i = 0; i <= model.getNrOfJobs(); i++)
			{
				Subjob subjob = new Subjob();
				subjob.setJob(jobId);
				subjob.setStatusCode(0);
				subjob.setStatusText("Submitted to cluster");
				subjob.setNr(i);
				db.add(subjob);
			}

			for (ParameterName pn : model.getParameternames())
			{
				String value = request.getString(pn.getName());
				SelectedParameter sp = new SelectedParameter();
				sp.setParameterName(pn.getName());
				sp.setParameterValue(value);
				sp.setJob(jobId);
				db.add(sp);
			}

			for (DataName dn : model.getDatanames())
			{
				String value = request.getString(dn.getName());
				SelectedData dp = new SelectedData();
				dp.setDataName(dn.getName());
				dp.setDataValue(value);
				dp.setJob(jobId);
				db.add(dp);
			}

			db.commitTx();
			dbSucces = true;
		}
		catch (Exception e)
		{
			db.rollbackTx();
			e.printStackTrace();
		}

		if (dbSucces)
		{
			try
			{

				// TODO! SelectedData are xrefs?? should be name probably? like
				// SelectedParameters!

				List<SelectedParameter> sp = db.find(SelectedParameter.class, new QueryRule("job", Operator.EQUALS,
						jobId));
				List<SelectedData> sd = db.find(SelectedData.class, new QueryRule("job", Operator.EQUALS, jobId));

				// special for QTL with 'phenotypes'
				Integer phenoRef = null;
				int totalitems = model.getNrOfJobs(); //default
				
				for (SelectedData s : sd)
				{
					if (s.getDataName().equals("phenotypes"))
					{
						phenoRef = Integer.parseInt(s.getDataValue());

						Data phenoMatrix = db.find(Data.class, new QueryRule("id", Operator.EQUALS, phenoRef)).get(0);
						AbstractDataMatrixInstance<Object> instance = dmh.createInstance(phenoMatrix);
						Class<?> cols = db.getClassForName(phenoMatrix.getFeatureType());
					
						if (cols.newInstance() instanceof ObservableFeature)
						{
							// FIXME
							// froegah totalitems = phenoMatrix.getTotalCols();
							totalitems = instance.getNumberOfCols();
						}
						else
						{
							// FIXME
							// totalitems = phenoMatrix.getTotalRows();
							totalitems = instance.getNumberOfRows();
						}
						break;
					}
				}

				// make toServer string
				String toServer = "";
				String host = null;

				if (startedJob.getComputeResource().equals("local") || startedJob.getComputeResource().equals("image"))
				{
					host = "localhost";
				}
				else if ((startedJob.getComputeResource().equals("cluster") || startedJob.getComputeResource().equals(
						"cloud")))
				{
					host = HtmlTools.getExposedIPAddress();
				}

				URL reconstructedURL = HtmlTools.getExposedProjectURL(request, host, app.servlet.MolgenisServlet
						.getMolgenisVariantID());

				String db_path = reconstructedURL.toString();

				//get Inv ref from the first matrix in the input set
				Investigation inv = null;
				try{
				Integer invRef = db.find(Data.class, new QueryRule("id", Operator.EQUALS, sd.get(0).getDataValue()))
						.get(0).getInvestigation_Id();
					inv = db.find(Investigation.class, new QueryRule("id", Operator.EQUALS, invRef)).get(0);
				}catch(Exception e){
					inv = db.find(Investigation.class).get(0); //BAD, but works for ClusterDemo...... FIXME
				}

				// NOT VARIABLE
				toServer += "name = '" + NameConvention.escapeEntityNameStrict(startedJob.getOutputDataName()) + "',";
				// FIXME
				toServer += "investigation = '" + inv.getName() + "',";
				toServer += "totalitems = '" + totalitems + "',";
				toServer += "njobs = '" + model.getNrOfJobs() + "',";
				toServer += "dbpath = '" + db_path + "',";
				toServer += "jobid = '" + jobId + "',";
				toServer += "job = '" + analysis.getTargetFunctionName() + "',";

				File usrHomeLibs = new File(System.getProperty("user.home") + File.separator + "libs");
				String OS = DetectOS.getOS();

				if (startedJob.getComputeResource().equals("local"))
				{
					toServer += "libraryloc = '" + usrHomeLibs.getAbsolutePath().replace("\\", "/") + "',";
				}
				else
				{
					//Utils.console("blala lib loc = ~libs");
					toServer += "libraryloc = '~/libs', ";
				}

				toServer += "jobparams = list(";
				// VARIABLE
				for (SelectedData s : sd)
				{
					String dataValueName = db.find(Data.class, new QueryRule("id", Operator.EQUALS, s.getDataValue()))
							.get(0).getName();
					toServer += "c('" + s.getDataName() + "' , '" + dataValueName + "'),";
				}

				for (SelectedParameter s : sp)
				{
					toServer += "c('" + s.getParameterName() + "' , '" + s.getParameterValue() + "'),";
				}

				toServer = toServer.substring(0, toServer.length() - 1);
				toServer += ")";

				List<Command> commands = generateNullJobCommandList(jobId, toServer, startedJob.getComputeResource(),
						usrHomeLibs, OS, db_path);

				// submit R job that will distribute the subjobs
				if (startedJob.getComputeResource().equals("local"))
				{
					cr = new LocalComputationResource(new MolgenisFileHandler(db));
					commands.add(new Command("R CMD BATCH runmij" + jobId + ".R", false, false, true));
				}
				else if ((startedJob.getComputeResource().equals("cluster")))
				{
					if (this.getModel().getLs().getUser() == null)
					{
						this.getModel().getLs().setUser(Millipede.z);
						this.getModel().getLs().setPassword(Millipede.k);
					}
					cr = new ClusterComputationResource(this.getModel().getLs());

					commands.add(new Command("nohup qsub runmij" + jobId + ".sh &", false, false, true));
				}
				else if ((startedJob.getComputeResource().equals("cloud")))
				{
					throw new Exception("Cloud is unsupported!");
				}
				else if ((startedJob.getComputeResource().equals("image")))
				{
					throw new Exception("Image is unsupported!");
				}

				// File curWorkingDir = new
				// File(System.getProperty("user.dir"));
				// File tmpWorkingDir = new File(curWorkingDir + File.separator
				// + "tmp");
				// if(!tmpWorkingDir.exists()){
				// boolean createSuccess = tmpWorkingDir.mkdir();
				// if(!createSuccess){
				// throw new Exception("Could not create tmp working dir at " +
				// tmpWorkingDir.getAbsolutePath());
				// }
				// }
				// System.setProperty("user.dir",
				// tmpWorkingDir.getAbsolutePath());

				cr.installDependencies();

				cr.executeCommands(commands);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public Map<Integer, List<Job>> makeJobSubjobMap(List<Job> jobs, List<Subjob> subjobs)
	{

		Map<Integer, List<Job>> JobSubjobMap = new HashMap<Integer, List<Job>>();

		for (Job j : jobs)
		{
			List<Job> tmp = new ArrayList<Job>();
			for (Subjob sj : subjobs)
			{
				if (sj.getJob().equals(sj.getId()))
				{

					tmp.add(j);
				}
			}
			JobSubjobMap.put(j.getId(), tmp);
		}

		return JobSubjobMap;
	}

	public List<Command> generateNullJobCommandList(int jobId, String toServer, String computeResource,
			File usrHomeLibs, String OS, String db_path)
	{
		List<Command> commands = new ArrayList<Command>();

		if (OS.startsWith("windows") && !computeResource.equals("cluster"))
		{

			// runmijJOBID.R
			commands.add(new Command("echo rm(list=ls()) > runmij" + jobId + ".R", false, false, true));
			// no longer needed: commands.add(new
			// Command("echo library(ClusterJobs,lib.loc=\'" +
			// usrHomeLibs.getAbsolutePath().replace("\\", "/") +
			// "\') >> runmij" + jobId + ".R", false, false, true));
			commands.add(new Command("echo \"library('bitops', lib.loc='"
					+ usrHomeLibs.getAbsolutePath().replace("\\", "/") + "')\" >> runmij" + jobId + ".R", false, false,
					true));
			commands.add(new Command("echo \"library('RCurl', lib.loc='"
					+ usrHomeLibs.getAbsolutePath().replace("\\", "/") + "')\" >> runmij" + jobId + ".R", false, false,
					true));
			commands.add(new Command("echo source(\"" + db_path + "/api/R/\") >> runmij" + jobId + ".R", false, false,
					true));
			commands.add(new Command("echo run_cluster_new_new(" + toServer + ") >> runmij" + jobId + ".R", false,
					false, true));
			commands.add(new Command("echo q('no') >> runmij" + jobId + ".R", false, false, true));
		}else{
			// runmijJOBID.R
			commands.add(new Command("echo \"rm(list=ls())\" > runmij" + jobId + ".R", false, false, true));
			if (computeResource.equals("local"))
			{
				// no longer needed: commands.add(new
				// Command("echo \"library(ClusterJobs,lib.loc=\'" +
				// usrHomeLibs.getAbsolutePath().replace("\\", "/") +
				// "\')\" >> runmij" + jobId + ".R", false, false, true));

				commands.add(new Command("echo \"library('bitops', lib.loc='"
						+ usrHomeLibs.getAbsolutePath().replace("\\", "/") + "')\" >> runmij" + jobId + ".R", false,
						false, true));
				commands.add(new Command("echo \"library('RCurl', lib.loc='"
						+ usrHomeLibs.getAbsolutePath().replace("\\", "/") + "')\" >> runmij" + jobId + ".R", false,
						false, true));
				commands.add(new Command("echo \"source('" + db_path + "/api/R/')\" >> runmij" + jobId + ".R", false,
						false, true));
				commands.add(new Command("echo \"run_cluster_new_new(" + toServer + ")\" >> runmij" + jobId + ".R",
						false, false, true));
				commands.add(new Command("echo \"q('no')\" >> runmij" + jobId + ".R", false, false, true));
			}
			else if (computeResource.equals("cluster"))
			{
				// no longer needed: commands.add(new
				// Command("echo \"library(ClusterJobs,lib.loc=\'~/libs\')\" >> runmij"
				// + jobId + ".R", false, false, false));
				commands.add(new Command("echo \"library('bitops', lib.loc='~/libs')\" >> runmij" + jobId + ".R",
						false, false, true));
				commands.add(new Command("echo \"library('RCurl', lib.loc='~/libs')\" >> runmij" + jobId + ".R", false,
						false, true));
				commands.add(new Command("echo \"source('" + db_path + "/api/R/')\" >> runmij" + jobId + ".R", false,
						false, true));
				commands.add(new Command("echo \"run_cluster_new_new(" + toServer + ")\" >> runmij" + jobId + ".R",
						false, false, false));
				commands.add(new Command("echo \"q('no')\" >> runmij" + jobId + ".R", false, false, false));
			}
			else
			{

				// unsupported!!
			}

			if (computeResource.equals("cluster"))
			{
				// runmijJOBID.sh
				commands.add(new Command("echo \"#!/bin/sh\" > runmij" + jobId + ".sh", false, false, false));
				commands.add(new Command("echo \"#PBS -N myjob\" > runmij" + jobId + ".sh", false, false, false));
				commands.add(new Command("echo \"#PBS -l nodes=1\" >> runmij" + jobId + ".sh", false, false, false));
				commands.add(new Command("echo \"#PBS -l walltime=01:30:00\" >> runmij" + jobId + ".sh", false, false,
						false));
				//TODO Figure out how to in the new way
				//commands.add(new Command("echo \"cd $home\" >> runmij" + jobId + ".sh", false, false, false));
				commands.add(new Command("echo \"R CMD BATCH runmij" + jobId + ".R\" >> runmij" + jobId + ".sh", false,
						false, false));
			}
		}
		return commands;
	}

	@Override
	public void reload(Database db)
	{
		
		if(this.model.getDeployName() == null){
			this.model.setDeployName(MolgenisServlet.getMolgenisVariantID());
		}

		if (djm == null)
		{
			djm = new DatabaseJobManager(db);
		}
		
		if (dmh == null)
		{
			dmh = new DataMatrixHandler(db);
		}

		if (model.getState() == null)
		{
			model.setState("main");
		}
		else if (model.getState() == "newjob1")
		{
			try
			{
				Query<Analysis> q = db.query(Analysis.class);
				List<Analysis> analysis = q.find();
				model.setAnalysis(analysis);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else if (model.getState() == "newjob2")
		{
			try
			{
				Analysis analysis = db.find(Analysis.class,
						new QueryRule("id", Operator.EQUALS, model.getCandidateJob().getAnalysis())).get(0);

				ParameterSet paramset = db.find(ParameterSet.class,
						new QueryRule("id", Operator.EQUALS, analysis.getParameterSet())).get(0);
				DataSet dataset = db.find(DataSet.class, new QueryRule("id", Operator.EQUALS, analysis.getDataSet()))
						.get(0);

				List<ParameterName> parameternames = db.find(ParameterName.class, new QueryRule("parameterset",
						Operator.EQUALS, paramset.getId()));
				model.setParameternames(parameternames);

				List<ParameterValue> parametervalues = new ArrayList<ParameterValue>();
				for (ParameterName pn : parameternames)
				{
					List<ParameterValue> results = db.find(ParameterValue.class, new QueryRule("parametername",
							Operator.EQUALS, pn.getId()));
					parametervalues.addAll(results);
				}
				model.setParametervalues(parametervalues);

				List<DataName> datanames = db.find(DataName.class, new QueryRule("dataset", Operator.EQUALS, dataset
						.getId()));
				model.setDatanames(datanames);

				List<DataValue> datavalues = new ArrayList<DataValue>();
				for (DataName dn : datanames)
				{
					List<DataValue> results = db.find(DataValue.class, new QueryRule("dataname", Operator.EQUALS, dn
							.getId()));
					datavalues.addAll(results);
				}
				model.setDatavalues(datavalues);

			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else if (model.getState() == "jobmanager")
		{
			try
			{
				Query<Job> q = db.query(Job.class);
				List<Job> jobList = q.sortASC("timestamp").find();

				Query<Subjob> q1 = db.query(Subjob.class);
				List<Subjob> subjobList = q1.sortASC("nr").find();

				// make html tags
				for (Subjob sj : subjobList)
				{
					sj.setStatusText(sj.getStatusText().replace("'", "").replace("\"", ""));
				}

				HashMap<String, String> jobParamMap = new HashMap<String, String>();

				List<SelectedParameter> sp = db.find(SelectedParameter.class);
				List<SelectedData> sd = db.find(SelectedData.class);

				for (SelectedParameter s : sp)
				{
					String value = s.getParameterName() + " = " + s.getParameterValue() + "<br>";
					if (jobParamMap.get(s.getJob().toString()) == null)
					{
						jobParamMap.put(s.getJob().toString(), value);
					}
					else
					{
						jobParamMap.put(s.getJob().toString(), jobParamMap.get(s.getJob().toString()) + value);
					}
				}
				for (SelectedData s : sd)
				{
					String value = s.getDataName() + " = " + s.getDataValue() + "<br>";
					if (jobParamMap.get(s.getJob().toString()) == null)
					{
						jobParamMap.put(s.getJob().toString(), value);
					}
					else
					{
						jobParamMap.put(s.getJob().toString(), jobParamMap.get(s.getJob().toString()) + value);
					}
				}

				for (Job j : jobList)
				{
					String value = "<b>Job:</b>" + "<br>";
					value += "analysis" + " = " + j.getAnalysis_Name() + "<br>";
					value += "timestamp" + " = " + j.getTimestamp() + "<br>";
					value += "outputdataname" + " = " + j.getOutputDataName() + "<br>";
					value += "computeresource" + " = " + j.getComputeResource() + "<br>";
					value += "id" + " = " + j.getId() + "<br>";
					jobParamMap.put(j.getId().toString(), jobParamMap.get(j.getId().toString()) + value);
				}

				model.setJobParamMap(jobParamMap);

				model.setSubjobs(subjobList);
				model.setJobs(jobList);
				model.setMaxSubjobs(HelperFunctions.countMaxSubjobs(subjobList, jobList));

			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

}
