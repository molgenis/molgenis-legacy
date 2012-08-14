package org.molgenis.hemodb.plugins;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import matrix.DataMatrixInstance;
import matrix.general.DataMatrixHandler;
import matrix.implementations.binary.BinaryDataMatrixInstance;

import org.apache.commons.lang.StringUtils;
import org.molgenis.auth.MolgenisUser;
import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.hemodb.HemoProbe;
import org.molgenis.hemodb.HemoSample;
import org.molgenis.hemodb.HemoSampleGroup;
import org.molgenis.hemodb.plugins.QuestionsModel.QuestionState;
import org.molgenis.util.HandleRequestDelegationException;
import org.molgenis.util.Tuple;

import plugins.matrix.manager.Browser;
import plugins.matrix.manager.MatrixManagerModel;
import plugins.matrix.manager.RequestHandler;

/**
 * @author JvD
 * 
 */
@SuppressWarnings("serial")
public class QuestionsOverview extends EasyPluginController<QuestionsModel>
{

	/**
	 * @param name
	 * @param parent
	 */
	public QuestionsOverview(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.setModel(new QuestionsModel(this)); // the default model
		getModel().setState(QuestionState.BEGINNING);

		// create sub plugin
		new MatrixManagerHemodb("QuestionsSub", this);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.molgenis.framework.ui.EasyPluginController#getView()
	 */
	public ScreenView getView()
	{
		return new FreemarkerView("QuestionsView.ftl", getModel());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.molgenis.framework.ui.SimpleScreenController#reload(org.molgenis.
	 * framework.db.Database)
	 */
	@Override
	public void reload(Database db) throws Exception
	{
		for (ScreenController child : this.getChildren())
			child.reload(db);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.molgenis.framework.ui.EasyPluginController#handleRequest(org.molgenis
	 * .framework.db.Database, org.molgenis.util.Tuple, java.io.OutputStream)
	 */
	public Show handleRequest(Database db, Tuple request, OutputStream out) throws HandleRequestDelegationException
	{
		try
		{
			if (QuestionState.QUESTION1.equals(getModel().getState()))
			{
				geneExpressionSearch(db, request);
			}
			if (QuestionState.QUESTION2.equals(getModel().getState()))
			{
				significantGenesBetweenTwoGroups(db, request);
			}
			if (QuestionState.QUESTION3.equals(getModel().getState()))
			{
				conversingBetweenProbesAndGenes(db, request);
			}
			getModel().setAction(request.getAction());

			if ("back".equals(request.getAction()))
			{
				getModel().setState(QuestionState.BEGINNING);
			}

			// Which question to show?
			String question = request.getString("questions");
			if ("questionOne".equals(question))
			{
				getModel().setState(QuestionState.QUESTION1);
				if (getModel().getNames().isEmpty())
				{
					selectSampleGroupsForDropdown(db);
				}
			}
			else if ("questionTwo".equals(question))
			{
				getModel().setState(QuestionState.QUESTION2);

				if (getModel().getNames().isEmpty())
				{
					/**
					 * If the list with group names is already filled it doesn't
					 * need to be filled again
					 */
					selectSampleGroupsForDropdown(db);
				}
			}
			else if ("questionThree".equals(question))
			{
				getModel().setState(QuestionState.QUESTION3);
			}
			else
			{
				System.out.println("something bad happened");
			}

			// Submit button
			if (getModel().getAction().equals("submitInformation"))
			{
				System.out.println("we handled the information on the site submitted via the submit button");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return Show.SHOW_MAIN;

	}

	/**
	 * @param db
	 * @throws DatabaseException
	 */
	public void selectSampleGroupsForDropdown(Database db) throws DatabaseException
	{
		/**
		 * Selection of all the (Hemo)Sample groups in the database. This will
		 * be used to fill the multiple select box on the site
		 */
		List<HemoSampleGroup> sampleGroups = db.find(HemoSampleGroup.class);
		if (sampleGroups != null)
		{
			for (HemoSampleGroup hsg : sampleGroups)
			{
				String name = hsg.getName();
				getModel().getNames().add(name);
			}
		}
		else
		{
			System.out.println("OOPS! SOMETHING WENT WRONG");
		}
	}

	/**
	 * @param db
	 * @param sampleGroups
	 * @return
	 * @throws DatabaseException
	 */
	public List<String> selectSamplesFromSampleGroups(Database db, List<String> sampleGroups) throws DatabaseException
	{
		/**
		 * Selection of the (Hemo) sample names within each group that is
		 * specified
		 */
		List<String> sampleNames = new ArrayList<String>();
		int numberGroups = sampleGroups.size();
		if (numberGroups == 0)
		{
			System.out.println("there is no selection made");
		}
		else
		{
			// for (String hsg : sampleGroups)
			// {
			// List<HemoSample> samplesPerGroup = db.find(HemoSample.class, new
			// QueryRule(HemoSample.SAMPLEGROUP_NAME,
			// Operator.EQUALS, hsg));
			List<HemoSample> samplesPerGroup = db.find(HemoSample.class, new QueryRule(HemoSample.SAMPLEGROUP_NAME,
					Operator.IN, sampleGroups));
			for (HemoSample name : samplesPerGroup)
			{
				sampleNames.add(name.getName());
			}
			// }
		}
		return sampleNames;
	}

	/**
	 * @param db
	 * @param genes
	 * @return
	 * @throws DatabaseException
	 */
	public List<String> selectProbesWithGenes(Database db, String[] genes) throws DatabaseException
	{
		/**
		 * selectProbesWithGenes gets a string with (Hemo) gene names, converts
		 * them to probes (by querying the database) and returns those probes
		 */
		try
		{
			List<String> probes = new ArrayList<String>();
			List<String> genesUpper = new ArrayList<String>();
			for (String gene : genes)
			{
				gene = gene.toUpperCase();
				genesUpper.add(gene.trim());
			}
			System.out.println("genesUpper: " + genesUpper);
			List<HemoProbe> probesPerGene = db.find(HemoProbe.class, new QueryRule(HemoProbe.REPORTSFOR_NAME,
					Operator.IN, genesUpper));
			for (HemoProbe probe : probesPerGene)
			{
				// System.out.println("name of probe: " + probe.getName());
				probes.add(probe.getName());
				getModel().getResults().add(probe.getName());
			}
			if (!probes.isEmpty())
			{
				// System.out.println("probes found with this gene(s): " +
				// probes);
				return probes;
			}
			else if (probes.isEmpty())
			{
				System.out.println("This gene cannot be found in this database.");
			}

		}
		catch (Exception e)
		{
			System.out.println("This gene cannot be found in this database.");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param db
	 * @param probes
	 * @return
	 * @throws DatabaseException
	 */
	public List<String> selectGenesWithProbes(Database db, String[] probes) throws DatabaseException
	{
		/**
		 * selectGenesWithProbes gets a String with (Hemo)probe names, converts
		 * those to (Hemo)genes (by querying the database) and returns the genes
		 */
		try
		{
			List<String> chompedProbes = new ArrayList<String>();
			for (String probe : probes)
			{
				chompedProbes.add(probe.trim());
			}

			List<HemoProbe> genesForProbe = db.find(HemoProbe.class, new QueryRule(HemoProbe.NAME, Operator.IN,
					chompedProbes));
			List<String> genes = new ArrayList<String>();
			for (HemoProbe gfp : genesForProbe)
			{
				if (!genes.contains(gfp.getReportsFor_Name()))
				{
					genes.add(gfp.getReportsFor_Name());
					getModel().getResults().add(gfp.getReportsFor_Name());
				}
			}
			if (!genes.isEmpty())
			{
				// System.out.println("gene found with this probe: " + genes);
				return genes;
			}
			else if (genes.isEmpty())
			{
				System.out.println("This probe cannot be found in this database.");
			}
		}
		catch (Exception e)
		{
			System.out.println("This gene cannot be found in this database.");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param db
	 * @return
	 * @throws DatabaseException
	 */
	public List<String> selectAllProbeNames(Database db) throws DatabaseException
	{
		/**
		 * Selects the names of all the (Hemo)Probes available in the database
		 */
		List<String> allProbes = new ArrayList<String>();
		List<HemoProbe> probes = db.find(HemoProbe.class);
		for (HemoProbe probe : probes)
		{
			allProbes.add(probe.getName());
		}
		return allProbes;
	}

	/**
	 * @param db
	 * @param dataSet
	 * @param sampleNames
	 * @param probeNames
	 * @return
	 * @throws Exception
	 */
	public DataMatrixInstance getMatrixInstance(Database db, Data dataSet, List<String> sampleNames,
			List<String> probeNames) throws Exception
	{

		DataMatrixHandler dmh = new DataMatrixHandler(db);
		BinaryDataMatrixInstance m = (BinaryDataMatrixInstance) dmh.createInstance(dataSet, db);

		return m.getSubMatrix(probeNames, sampleNames);
	}

	/**
	 * @param db
	 * @param request
	 */
	public void geneExpressionSearch(Database db, Tuple request)
	{
		/**
		 * geneExpressionSearch generates a subMatrix with the genes and samples
		 * selected on the website (via request)
		 */
		try
		{
			/**
			 * Gene expression data matrix selection (raw/normalized data)
			 */
			String geneExp = request.getString("geneExp");

			/**
			 * Gets the list of genes from the website (request)
			 */
			String genesFromSite = request.getString("geneText");
			String[] genes = genesFromSite.split(",");
			List<String> probes = null;

			if (genes != null)
			{
				if (genes.length == 0)
				{
					System.out.println("There are no genes in the inputfield. Try again.");
				}
				else
				{
					probes = selectProbesWithGenes(db, genes);
				}
			}
			else
			{
				System.out.println("There are no genes specified. Please try again");
			}

			/**
			 * Gets the selected groups from the website (request) and retrieves
			 * the associated samples
			 */
			List<String> groups = request.getStringList("sampleGroups");
			List<String> sampleNames = selectSamplesFromSampleGroups(db, groups);
			System.out.println("Groups are: " + groups);
			System.out.println("Samples selected are: " + sampleNames);

			/**
			 * Submit button
			 */
			if (request.getAction().equals("submitInfoQ1"))
			{
				System.out.println("Q1!!! We handled the information on the site submitted via the submit button");
				getModel().setState(QuestionState.QUESTION1_RESULT);
			}
			if (QuestionState.QUESTION1_RESULT.equals(getModel().getState()))
			{

				// get the data set
				Data dataSet = db.query(Data.class).eq(Data.NAME, geneExp).find().get(0);

				// get the Browser which is in the other plugin
				MatrixManagerHemodb matrixManager = (MatrixManagerHemodb) this.getApplicationController().get(
						"QuestionsSub");

				// the matrix we want to show
				DataMatrixInstance m = this.getMatrixInstance(db, dataSet, sampleNames, probes);

				Browser br = new Browser(dataSet, m, this.getApplicationController());
				MatrixManagerModel model = (MatrixManagerModel) matrixManager.getMyModel();
				model.setBrowser(br);

				// set nice label
				matrixManager.setLabel("Results of your selection");

				// refresh attributes, operators, filter
				model.setRowHeaderAttr(null);
				model.setColHeaderAttr(null);
				model.setAllOperators(null);
				model.setValueOperators(null);
				model.setFilter(null);

				model.setUploadMode(false);

				model.setSelectedData(dataSet);

				matrixManager.createOverLibText(db);

				if (model.getRowHeaderAttr() == null || model.getColHeaderAttr() == null)
				{
					matrixManager.setHeaderAttr(db);
				}

				if (model.getAllOperators() == null)
				{
					matrixManager.setAllOperators();
				}

				if (model.getValueOperators() == null)
				{
					matrixManager.setValueOperators();
				}

				matrixManager.createHeaders();

				RequestHandler.handle(model, request, db);

			}
			else
			{
				System.out.println("Something went wrong, try again.");
			}
		}

		catch (Exception e1)
		{
			e1.printStackTrace();
		}
	}

	/**
	 * @param db
	 * @param request
	 * @throws Exception
	 */
	public void significantGenesBetweenTwoGroups(Database db, Tuple request) throws Exception
	{
		/**
		 * significantGenesBetweenTwoGroups calculates significantly
		 * differentially expressed genes between two groups that are specified
		 * on the website. This calculation will be performed by R scripts
		 */
		if ("back".equals(request.getAction()))
		{
			getModel().setState(QuestionState.BEGINNING);
			getModel().setResults(null);
		}

		/**
		 * Gene expression data matrix selection (raw/normalized data)
		 */
		String geneExp = request.getString("geneExp");

		/**
		 * The method which will be used for combining all the samples in each
		 * group (Mean/Median)
		 */
		String sampleCombine = request.getString("sampleCombine");

		/**
		 * Select the samples based on the sample group names
		 */
		List<String> groups = request.getStringList("sampleGroups");
		int numberGroups = groups.size();
		List<String> sampleNamesGroup1 = new ArrayList<String>();
		List<String> sampleNamesGroup2 = new ArrayList<String>();

		if (numberGroups == 2)
		{
			List<String> sampleGroup1 = new ArrayList<String>();
			sampleGroup1.add(groups.get(0));
			List<String> sampleGroup2 = new ArrayList<String>();
			sampleGroup2.add(groups.get(1));

			sampleNamesGroup1 = selectSamplesFromSampleGroups(db, sampleGroup1);
			sampleNamesGroup2 = selectSamplesFromSampleGroups(db, sampleGroup2);
		}
		else
		{
			System.out.println("There can only be a selection of 2 groups. Try again.");
		}

		/**
		 * Select the significance cutoff to be used (non log2 value)
		 */
		double signifCutoff = request.getDouble("signifCutoff");

		/**
		 * Get all the (hemo)probe names present in the database
		 */
		List<String> allProbes = selectAllProbeNames(db);
		System.out.println("Gene expression dataset is: " + geneExp);
		System.out.println("Method of combination is: " + sampleCombine);
		System.out.println("Groups are: " + groups);
		System.out.println("Samples in group One are: " + sampleNamesGroup1);
		System.out.println("Samples in group Two are: " + sampleNamesGroup2);
		System.out.println("Significancy cutoff is: " + signifCutoff);

		/**
		 * Submit button
		 */
		if (request.getAction().equals("submitInfoQ2"))
		{
			System.out.println("Q2!!! We handled the information on the site submitted via the submit button");
			getModel().setState(QuestionState.QUESTION2_RESULT);
		}

		/**
		 * Converts the cutoff to a log2 value if the datamatrix choosen is the
		 * log2 normalized dataset
		 */
		if (geneExp.equals("expDataLog2Quan"))
		{
			signifCutoff = Math.log(signifCutoff) / Math.log(2);
		}

		MolgenisUser user = db.find(MolgenisUser.class,
				new QueryRule(MolgenisUser.ID, Operator.EQUALS, db.getLogin().getUserId())).get(0);

		if (StringUtils.isEmpty(user.getEmail()))
		{
			throw new DatabaseException("No email address provided.");
		}

		String email = user.getEmail();

		if (sampleCombine.equals("sampleCombineMean"))
		{
			// roep mean script aan met alle data
			new CalculateMeanR(getModel(), geneExp, sampleNamesGroup1, sampleNamesGroup2, groups, signifCutoff,
					allProbes, this.getEmailService(), email);
		}
		else
		{
			// roep median script aan met alle data
			new CalculateMedianR(getModel(), geneExp, sampleNamesGroup1, sampleNamesGroup2, groups, signifCutoff,
					allProbes, this.getEmailService(), email);
		}

	}

	/**
	 * @param db
	 * @param request
	 * @throws DatabaseException
	 */
	public void conversingBetweenProbesAndGenes(Database db, Tuple request) throws DatabaseException
	{
		/**
		 * This is a method to convert between (hemo)probes and (hemo)genes.
		 * This is done by querying the database. The results are handled by the
		 * QuestionsView.ftl.
		 */
		if ("back".equals(request.getAction()))
		{
			getModel().setState(QuestionState.BEGINNING);
			getModel().setResults(null);
		}

		/**
		 * Submit button
		 */
		if (request.getAction().equals("submitInfoQ3"))
		{
			System.out.println("Q3!!! We handled the information on the site submitted via the submit button");
			getModel().setState(QuestionState.QUESTION3_RESULT);
		}

		String converting = request.getString("convertGP");
		String listFromSite = request.getString("gpText");
		String[] stringsToConvert = listFromSite.split(",");

		if (converting.equals("convertGenes"))
		{
			List<String> probes = selectProbesWithGenes(db, stringsToConvert);
			System.out.println("selected probes are: " + probes);
		}
		else if (converting.equals("convertProbes"))
		{
			List<String> genes = selectGenesWithProbes(db, stringsToConvert);
			System.out.println("selected genes are: " + genes);
		}
	}

}
