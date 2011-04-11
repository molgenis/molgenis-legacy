package org.molgenis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.molgenis.util.cmdline.CmdLineException;
import org.molgenis.util.cmdline.CmdLineParser;
import org.molgenis.util.cmdline.Option;

/**
 * Option to parameterize the {@link Molgenis} and the {@link org.molgenis.framework.server.MolgenisServer}
 * @author Morris Swertz
 */
public class MolgenisOptions
{
	final public static String CLASS_PER_TABLE = "class_per_table";
	final public static String SUBCLASS_PER_TABLE = "subclass_per_table";
	final public static String HIERARCHY_PER_TABLE = "hierarchy_per_table";

	/**
	 * Alternative generator cartridges.
	 * @author Morris Swertz
	 */
	public enum MapperImplementation
	{
		MULTIQUERY("multiquery"), JPA("jpa"), PREPARED_STATEMENT("prepared_statement"), UNKNOWN("unknown");

		MapperImplementation(String tag)
		{
			this.tag = tag;
		}

		public String toString()
		{
			return this.tag;
		}

		public static MapperImplementation get(String tag)
		{
			if (tag == null) return UNKNOWN;
			if (tag.equalsIgnoreCase(MULTIQUERY.tag)) return MULTIQUERY;
			if (tag.equalsIgnoreCase(JPA.tag)) return JPA;
			if (tag.equalsIgnoreCase(PREPARED_STATEMENT.tag)) return PREPARED_STATEMENT;
			return UNKNOWN;

		}

		/** The string-representation of the enumeration-type. */
		public final String tag;
	};

	/** relative path to the db.xml file */
	@Option(name = "model_database", param = "filename", type = Option.Type.REQUIRED_ARGUMENT, usage = "File with data structure specification (in MOLGENIS DSL).")
	public Vector<String> model_database = new Vector<String>();

	/** relative path to the ui.xml file */
	@Option(name = "model_userinterface", param = "filename", type = Option.Type.REQUIRED_ARGUMENT, usage = "File with user interface specification (in MOLGENIS DSL). Can be same file as model_database")
	public String model_userinterface = null;
	
	/** directory where example data lives (used for test and documentation)*/
	@Option(name = "example_data_dir", param = "filename", type = Option.Type.REQUIRED_ARGUMENT, usage = "File with user interface specification (in MOLGENIS DSL). Can be same file as model_database")
	public String example_data_dir = "data";
	
	/** Source directory for generated python*/
	@Option(name = "output_python", param = "filename", type = Option.Type.REQUIRED_ARGUMENT, usage = "Output-directory for the generated Python classes.")
	public String output_python = "generated/python";

	/** Source directory for generated java*/
	@Option(name = "output_src", param = "filename", type = Option.Type.REQUIRED_ARGUMENT, usage = "Output-directory for the generated project.")
	public String output_src = "generated/java";

	/** Source directory for handwritten java */
	@Option(name = "output_hand", param = "filename", type = Option.Type.REQUIRED_ARGUMENT, usage = "Output-directory for the generated project.")
	public String output_hand = "handwritten/java";

	/** Source directory for generated sql */
	@Option(name = "output_sql", param = "filename", type = Option.Type.REQUIRED_ARGUMENT, usage = "Output-directory for the generated sql files.")
	public String output_sql = "generated/sql";

	/** Source directory for generated doc */
	@Option(name = "output_doc", param = "filename", type = Option.Type.REQUIRED_ARGUMENT, usage = "Output-directory for the generated documentation.")
	public String output_doc = "WebContent/generated-doc";

//	@Option(name = "output_type", param = "string", type = Option.Type.REQUIRED_ARGUMENT, usage = "Output type of the project, either war (for use in tomcat) or jar (standalone).")
//	public String output_type = "";

	/** Source directory for web content */
	@Option(name = "output_web", param = "string", type = Option.Type.REQUIRED_ARGUMENT, usage = "Output-directory for any generated web resources")
	public String output_web = "WebContent";

	/** Database driver. For example: "com.mysql.jdbc.Driver" */
	@Option(name = "db_driver", param = "vendor-name", type = Option.Type.REQUIRED_ARGUMENT, usage = "Driver of database. Any JDBC compatible driver should work.")
	public String db_driver = "com.mysql.jdbc.Driver";

	/** Database user */
	@Option(name = "db_user", param = "string", type = Option.Type.OPTIONAL_ARGUMENT, usage = "Username for database. ")
	public String db_user = "";
	
	/** Folder with overrides for decorators */
	@Option(name = "decorator_overriders", param = "string", type = Option.Type.OPTIONAL_ARGUMENT, usage = "Points to an application package with overriding classes for entity decorators, mapped by name. Default: null")
	public String decorator_overriders = null;

	/** Database user password */
	@Option(name = "db_password", param = "password", type = Option.Type.OPTIONAL_ARGUMENT, usage = "Password for database. ")
	public String db_password = "";

	/** Database uri. For example: jdbc:mysql://localhost/molgenis" */
	@Option(name = "db_uri", param = "string", type = Option.Type.REQUIRED_ARGUMENT, usage = "Uri of the database. Default: localhost")
	public String db_uri = "jdbc:mysql://localhost/molgenis?innodb_autoinc_lock_mode=2";

	/** Path where file attachments (&lt;field type="file" ... &gt;) should be stored.*/
	@Option(name = "db_filepath", param = "string", type = Option.Type.REQUIRED_ARGUMENT, usage = "Path where the database should store file attachements. Default: null")
	public String db_filepath = "data";

	/** Advanced option: JNDI name that puts the database into the server context */
	@Option(name = "db_jndiname", param = "string", type = Option.Type.REQUIRED_ARGUMENT, usage = "Used to create a JDBC database resource for the application")
	public String db_jndiname = "molgenis_jndi";

	/** Advanced option: Type of object relational mapping.*/
	@Option(name = "object_relational_mapping", param = "", type = Option.Type.OPTIONAL_ARGUMENT, usage = "Expert option: Choosing OR strategy. Either 'class_per_table', 'subclass_per_table', 'hierarchy_per_table'. Default: class_per_table")
	public String object_relational_mapping = SUBCLASS_PER_TABLE;
	
	/** Advanced option: Type of mapper implementation */
	@Option(name = "mapper_implementation", param = "", type = Option.Type.NO_ARGUMENT, usage = "Expert option: Choosing wether multiquery is used instead of prepared statements. Default: false")
	public MapperImplementation mapper_implementation = MapperImplementation.MULTIQUERY;
	
	@Option(name = "generate_persisitence", param = "", type = Option.Type.OPTIONAL_ARGUMENT, usage = "Expert option: Choosing wether persistence.xml is generated by molgenis or supplied by user. Default: true")
	public boolean generate_persistence = true;	
	

	/** Advanced option: skip entities marked as 'system="true"'*/
	@Option(name = "exclude_system", param = "exclude_system", type = Option.Type.REQUIRED_ARGUMENT, usage = "Expert option: Whether system tables should be excluded from generation. Default: true")
	public boolean exclude_system = true;

//	@Option(name = "force_molgenis_package", param = "force_molgenis_package", type = Option.Type.REQUIRED_ARGUMENT, usage = "Expert option. Whether the generated package should be 'molgenis' or the name specified in the model. Default: false")
//	public boolean force_molgenis_package = false;

	/** Class name that addresses security */
	@Option(name = "auth_loginclass", param = "auth_loginclass", type = Option.Type.REQUIRED_ARGUMENT, usage = "Expert option.")
	public String auth_loginclass = "org.molgenis.framework.security.SimpleLogin";

	/** Name of form/plugin to redirect to after login */
	@Option(name = "auth_redirect", param = "auth_redirect", type = Option.Type.OPTIONAL_ARGUMENT, usage = "Get name of form/plugin to redirect to after login. Default: null")
	public String auth_redirect = null;

	// @Option(name = "force_lowercase_names", param = "force_lowercase_names",
	// type = Option.Type.REQUIRED_ARGUMENT, usage =
	// "Expert option. Wether all names should be converted to lowercase. Default: true"
	// )
	// public boolean force_lowercase_names = false;

//	@Option(name = "verbose", param = "", type = Option.Type.NO_ARGUMENT, usage = "This switch turns the verbose-mode on.")
//	public boolean verbose = true;

//	@Option(name = "compile", param = "c", type = Option.Type.NO_ARGUMENT, usage = "This switch makes the factory also compile (usefull outside IDE).")
//	public boolean compile = false;
	
	/** email adress used to send emails with */
	@Option(name = "mail_smtp_from", param = "mail_smtp_from", type = Option.Type.OPTIONAL_ARGUMENT, usage = "Sets the email adress used to send emails from. Default: null")
	public String mail_smtp_from = "";
	
	/** email protocol to be used. For example: smtp or asmpt */
	@Option(name = "mail_smtp_protocol", param = "mail_smtp_protocol", type = Option.Type.OPTIONAL_ARGUMENT, usage = "Sets the email protocol, either smtp, smtps or null. Default: smtps")
	public String mail_smtp_protocol = "smtps";
	
	/** email server name. For example: localhost */
	@Option(name = "mail_smtp_hostname", param = "mail_smtp_hostname", type = Option.Type.OPTIONAL_ARGUMENT, usage = "SMTP host server. Default: gmail")
	public String mail_smtp_hostname = "smtp.gmail.com";
	
	/** email server port. For example: 25 */
	@Option(name = "mail_smtp_port", param = "mail_smtp_port", type = Option.Type.OPTIONAL_ARGUMENT, usage = "SMTP host server port. Default: 465")
	public String mail_smtp_port = "465";
	
	/** email user name. Keep empty for anonymous */
	@Option(name = "mail_smtp_user", param = "mail_smtp_user", type = Option.Type.OPTIONAL_ARGUMENT, usage = "SMTP user for authenticated emailing. Default: molgenis.")
	public String mail_smtp_user = "molgenis";
	
	@Option(name = "mail_smtp_au", param = "mail_smtp_au", type = Option.Type.OPTIONAL_ARGUMENT, usage = "SMTP auth. Default: null")
	public String mail_smtp_au = "";
	
	@Option(name = "generate_R", param = "string", type = Option.Type.OPTIONAL_ARGUMENT, usage = "Should R-interface be generated. Default: true.")
	public String generate_R = "true";
	
	@Option(name = "linkout_overlay", param = "string", type = Option.Type.OPTIONAL_ARGUMENT, usage = "Applies an optional overlay of your HTML with linkouts for popular identifier to online databases, default is false.")
	public String linkout_overlay = "false";
	
	@Option(name = "generate_doc", param = "string", type = Option.Type.OPTIONAL_ARGUMENT, usage = "Should documentation be generated. Default: true.")
	public String generate_doc = "true";
	
	@Option(name = "generate_csv", param = "string", type = Option.Type.OPTIONAL_ARGUMENT, usage = "Should CsvReaders be generated. Default: true.")
	public String generate_csv = "true";
	
	@Option(name = "generate_Python", param = "string", type = Option.Type.OPTIONAL_ARGUMENT, usage = "Should Python-interface be generated. Default: false.")
	public String generate_Python = "false";
	
	@Option(name = "generate_tests", param = "string", type = Option.Type.OPTIONAL_ARGUMENT, usage = "Should run-time testing be generated. Default: true.")
	public String generate_tests = "true";
	
	@Option(name = "generate_ExcelImport", param = "string", type = Option.Type.OPTIONAL_ARGUMENT, usage = "Should Excel file importing be generated. Default: true.")
	public String generate_ExcelImport = "true";
	
	@Option(name = "generate_MolgenisServlet", param = "string", type = Option.Type.OPTIONAL_ARGUMENT, usage = "Should The molgenisServlet be generated or does the user supply ones own. Default: true.")
	public String generate_MolgenisServlet = "true";
	
	// internal
	public String path = "";
	@Option(name = "db_mode", param = "string", type = Option.Type.OPTIONAL_ARGUMENT, usage = "Which mode should the molgenisServlet use when contacting the dabase. Default: servlet.")
	public String db_mode = "servlet";
	
	@Option(name = "output_cpp", param = "filename", type = Option.Type.REQUIRED_ARGUMENT, usage = "Output-directory for the generated CPP classes.")
	public String output_cpp = "generated/cpp";
	
	@Option(name = "generate_cpp", param = "", type = Option.Type.OPTIONAL_ARGUMENT, usage = "Generate CPP. Default: false")
	public boolean generate_cpp = false;
	
	@Option(name = "generate_imdb", param = "", type = Option.Type.OPTIONAL_ARGUMENT, usage = "Generate the in memory database classes. Default: true")
	public boolean generate_imdb = true;
	
	@Option(name = "generate_sql", param = "", type = Option.Type.OPTIONAL_ARGUMENT, usage = "Generate any SQL related classes. Default: true")
	public boolean generate_sql = true;
	
	@Option(name = "copy_resources", param = "", type = Option.Type.OPTIONAL_ARGUMENT, usage = "Copy resources to generated-res. Default: true")
	public boolean copy_resources = true;
	
	@Option(name = "generate_html", param = "", type = Option.Type.OPTIONAL_ARGUMENT, usage = "generate HTML. Default: true")
	public boolean generate_html = true;

	@Option(name = "generate_rdf", param = "", type = Option.Type.OPTIONAL_ARGUMENT, usage = "generate the RDF API. Default: true")
	public boolean generate_rdf =true;
	
	@Option(name = "generate_rest", param = "", type = Option.Type.OPTIONAL_ARGUMENT, usage = "generate the REST API. Default: true")
	public boolean generate_rest=true;
	
	@Option(name = "generate_soap", param = "", type = Option.Type.OPTIONAL_ARGUMENT, usage = "generate the SOAP API. Default: true")
	public boolean generate_soap=true;
	
	@Option(name = "generate_plugins", param = "", type = Option.Type.OPTIONAL_ARGUMENT, usage = "generate the Molgenis plugin API API. Default: true")
	public boolean generate_plugins=true;

	/**
	 * Initialize with the defaults
	 */
	public MolgenisOptions()
	{

	}

	/**
	 * Initialize options from properties file
	 * 
	 * @param propertiesFile the path string to molgenis.properties file
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws CmdLineException
	 */
	public MolgenisOptions(String propertiesFile) throws FileNotFoundException, IOException, CmdLineException
	{
		Properties props = new Properties();
		try
		{
			//try to load from local files
			props.load(new FileInputStream(propertiesFile));
		}
		catch (FileNotFoundException e)
		{
			try
			{
				//try to load from classpath
				props.load(ClassLoader.getSystemResourceAsStream(propertiesFile));
			}
			catch(Exception e2)
			{
				throw new IOException("couldn't find file " + new File(propertiesFile).getAbsolutePath());
			}
			
		}

		CmdLineParser parser = new CmdLineParser(this);
		parser.parse(props);
		if (props.getProperty("mapper_implementation") != null) {
			this.mapper_implementation = MapperImplementation.get(props.getProperty("mapper_implementation"));
		}
		//System.out.println("Mapper implementation molgenis name: " + this.mapper_implementation.name());

//		if (new File(propertiesFile).getParentFile() != null)
//		{
//			this.path = new File(propertiesFile).getParentFile().getAbsolutePath() + "/";
//		}
		Logger.getLogger(this.getClass().getSimpleName()).debug("parsed properties file.");
	}

	/**
	 * Initialize options from properties object
	 * 
	 * @param properties
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws CmdLineException
	 */
	public MolgenisOptions(Properties properties) throws CmdLineException
	{
		CmdLineParser parser = new CmdLineParser(this);
		parser.parse(properties);
		Logger.getLogger(this.getClass().getSimpleName()).debug("parsed properties file.");
	}

	public String toString()
	{
		try
		{
			return new CmdLineParser(this).toString(this);
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
		}
		catch (CmdLineException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public Vector<String> getModelDatabase()
	{
		return model_database;
	}

	public void setModelDatabase(Vector<String> model_database)
	{
		this.model_database = model_database;
	}
	
	public void setModelDatabase(String model_database)
	{
		Vector<String> v = new Vector<String>();
		v.add(model_database);
		this.model_database = v;
	}

	public String getModelUserinterface()
	{
		return model_userinterface;
	}

	public void setModelUserinterface(String model_userinterface)
	{
		this.model_userinterface = model_userinterface;
	}

	public String getOutputSrc()
	{
		return output_src;
	}

	public void setOutputSrc(String output_src)
	{
		this.output_src = output_src;
	}

	public String getOutputHand()
	{
		return output_hand;
	}

	public void setOutputHand(String output_hand)
	{
		this.output_hand = output_hand;
	}

	public String getOutputSql()
	{
		return output_sql;
	}

	public void setOutputSql(String output_sql)
	{
		this.output_sql = output_sql;
	}

	public String getOutputDoc()
	{
		return output_doc;
	}

	public void setOutputDoc(String output_doc)
	{
		this.output_doc = output_doc;
	}

//	public String getOutput_type()
//	{
//		return output_type;
//	}
//
//	public void setOutput_type(String output_type)
//	{
//		this.output_type = output_type;
//	}

	public String getOutputWeb()
	{
		return output_web;
	}

	public void setOutputWeb(String output_web)
	{
		this.output_web = output_web;
	}

	public String getDbDriver()
	{
		return db_driver;
	}

	public void setDbDriver(String db_driver)
	{
		this.db_driver = db_driver;
	}

	public String getDbUser()
	{
		return db_user;
	}

	public void setDbUser(String db_user)
	{
		this.db_user = db_user;
	}

	public String getDbPassword()
	{
		return db_password;
	}

	public void setDbPassword(String db_password)
	{
		this.db_password = db_password;
	}

	public String getDbUri()
	{
		return db_uri;
	}

	public void setDbUri(String db_uri)
	{
		this.db_uri = db_uri;
	}

	public String getDbFilepath()
	{
		return db_filepath;
	}

	public void setDbFilepath(String db_filepath)
	{
		this.db_filepath = db_filepath;
	}

	public String getDbJndiname()
	{
		return db_jndiname;
	}

	public void setDbJndiname(String db_jndiname)
	{
		this.db_jndiname = db_jndiname;
	}

	public String getObjectRelationalMapping()
	{
		return object_relational_mapping;
	}

	public void setObjectRelationalMapping(String object_relational_mapping)
	{
		this.object_relational_mapping = object_relational_mapping;
	}

	public MapperImplementation getMapperImplementation()
	{
		return mapper_implementation;
	}

	public void setMapperImplementation(MapperImplementation mapper_implementation)
	{
		this.mapper_implementation = mapper_implementation;
	}

	public boolean isExcludeSystem()
	{
		return exclude_system;
	}

	public void setExcludeSystem(boolean exclude_system)
	{
		this.exclude_system = exclude_system;
	}

//	public boolean isForce_molgenis_package()
//	{
//		return force_molgenis_package;
//	}
//
//	public void setForce_molgenis_package(boolean force_molgenis_package)
//	{
//		this.force_molgenis_package = force_molgenis_package;
//	}

	public String getAuthLoginclass()
	{
		return auth_loginclass;
	}

	public void setAuthLoginclass(String auth_loginclass)
	{
		this.auth_loginclass = auth_loginclass;
	}

//	public boolean isVerbose()
//	{
//		return verbose;
//	}
//
//	public void setVerbose(boolean verbose)
//	{
//		this.verbose = verbose;
//	}

//	public boolean isCompile()
//	{
//		return compile;
//	}
//
//	public void setCompile(boolean compile)
//	{
//		this.compile = compile;
//	}

	public String getPath()
	{
		return path;
	}

	public void setPath(String path)
	{
		this.path = path;
	}
}
