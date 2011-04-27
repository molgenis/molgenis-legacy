/* Date:        October 11, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.cluster.demo.dependencymanager;

import java.io.File;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import filehandling.generic.MolgenisFileHandler;

import plugins.cluster.implementations.LocalComputationResource;

public class DependencyManager extends PluginModel<Entity>
{
	private static final long serialVersionUID = 3728283831751229340L;
	private DependencyManagerModel model = new DependencyManagerModel();
	private File usrHomeLibs;

	public DependencyManagerModel getModel()
	{
		return this.model;
	}

	public DependencyManager(String name, ScreenModel<Entity> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugins_cluster_demo_dependencymanager_DependencyManager";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/cluster/demo/dependencymanager/DependencyManager.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		// replace example below with yours
		try
		{

			String action = request.getString("__action");

			LocalComputationResource lcr = null;
			setUsrHomeLibs(null);
			
			if(action.startsWith("install")){
				MolgenisFileHandler xlfh = new MolgenisFileHandler(db);
				lcr = new LocalComputationResource(xlfh);
				setUsrHomeLibs(new File(System.getProperty("user.home")
						+ File.separator + "libs"));
			}
			
			if (action.equals("installQtl"))
			{
				lcr.installQtl();
			}

			else if (action.equals("installRcurl"))
			{
				lcr.installRCurl();
			}

			else if (action.equals("installBitops"))
			{
				lcr.installBitops();
			}

//			else if (action.equals("installClusterJobs"))
//			{
//				lcr.installClusterJobs();
//			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void reload(Database db)
	{
		this.model.setBitops(false);
		this.model.setRcurl(false);
		this.model.setRqtl(false);
		this.model.setClusterjobs(false);
		
		File usrHomeLibs = new File(System.getProperty("user.home")
				+ File.separator + "libs");
		
		File bitopsDir = new File(usrHomeLibs.getAbsolutePath()
				+ File.separator + "bitops");
		File qtlDir = new File(usrHomeLibs.getAbsolutePath()
				+ File.separator + "qtl");
		File rcurlDir = new File(usrHomeLibs.getAbsolutePath()
				+ File.separator + "RCurl");
		File clusterJobsDir = new File(usrHomeLibs.getAbsolutePath()
				+ File.separator + "ClusterJobs");
		
		System.out.println("checking dir: " + bitopsDir.getAbsolutePath());
		System.out.println("checking dir: " + qtlDir.getAbsolutePath());
		System.out.println("checking dir: " + rcurlDir.getAbsolutePath());
		System.out.println("checking dir: " + clusterJobsDir.getAbsolutePath());

		if(bitopsDir.exists()){
			System.out.println("bitopsDir exists");
			this.model.setBitops(true);
		}
		
		if(qtlDir.exists()){
			System.out.println("qtlDir exists");
			this.model.setRqtl(true);
		}
		
		if(rcurlDir.exists()){
			System.out.println("rcurlDir exists");
			this.model.setRcurl(true);
		}
		
		if(clusterJobsDir.exists()){
			System.out.println("clusterJobsDir exists");
			this.model.setClusterjobs(true);
		}

	}

	public void setUsrHomeLibs(File usrHomeLibs) {
		this.usrHomeLibs = usrHomeLibs;
	}

	public File getUsrHomeLibs() {
		return usrHomeLibs;
	}
}
