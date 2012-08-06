package org.molgenis.hemodb.plugins;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import matrix.DataMatrixInstance;
import matrix.general.DataMatrixHandler;
import matrix.implementations.binary.BinaryDataMatrixInstance;

import org.apache.commons.lang.StringUtils;
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

public class QuestionsOverview extends EasyPluginController<QuestionsModel> {

	public QuestionsOverview(String name, ScreenController<?> parent) {
		super(name, parent);
		this.setModel(new QuestionsModel(this)); // the default model
		getModel().setState(QuestionState.BEGINNING);

		// create sub plugin
		new MatrixManagerHemodb("QuestionsSub", this);

		// initialiseer alle paginas 1x
		// new QuestionOne("questionOne", this);
		// new QuestionTwo("questionTwo", this);
	}

	public ScreenView getView() {
		return new FreemarkerView("QuestionsView.ftl", getModel());
	}

	@Override
	public void reload(Database db) throws Exception {
		// TODO Auto-generated method stub

		for (ScreenController child : this.getChildren())
			child.reload(db);

	}

	public Show handleRequest(Database db, Tuple request, OutputStream out)
			throws HandleRequestDelegationException {

		try {
			if (QuestionState.QUESTION1.equals(getModel().getState())) {
				// delegate to the module of question 1
				// this.get("questionOne").handleRequest(db, request, out);
				questionOne(db, request);
			}
			if (QuestionState.QUESTION2.equals(getModel().getState())) {
				// delegate to the module of question 2
				// this.get("questionOne").handleRequest(db, request, out);
				questionTwo(db, request);
			}
			if (QuestionState.QUESTION3.equals(getModel().getState())) {
				// delegate to the module of question 3
				// this.get("questionOne").handleRequest(db, request, out);
				conversingBetweenProbesAndGenes(db, request);
			}
			getModel().setAction(request.getAction());

			if ("back".equals(request.getAction())) {
				getModel().setState(QuestionState.BEGINNING);
			}

			// Which question to show?
			String question = request.getString("questions");
			if ("questionOne".equals(question)) {
				getModel().setState(QuestionState.QUESTION1);
				if (getModel().getNames().isEmpty()) {
					// If the list with group names is already filled it doesn't
					// have to be filled again
					selectSampleGroupsForDropdown(db);
				}

				// get the q1
				// QuestionOne q1 = (QuestionOne) this.get("questionOne");

			} else if ("questionTwo".equals(question)) {
				getModel().setState(QuestionState.QUESTION2);

				if (getModel().getNames().isEmpty()) {
					// If the list with group names is already filled it doesn't
					// have to be filled again // groupnames is already
					selectSampleGroupsForDropdown(db);
				}

				// TODO: QuestionTwo q2 = (QuestionTwo) this.get("questionTwo");
			} else if ("questionThree".equals(question)) {
				getModel().setState(QuestionState.QUESTION3);
			} else {
				System.out.println("something bad happened");
				// TODO implement other questions
			}

			// Submit button
			if (getModel().getAction().equals("submitInformation")) {
				System.out
						.println("we handled the information on the site submitted via the submit button");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Show.SHOW_MAIN;

	}

	public void selectSampleGroupsForDropdown(Database db)
			throws DatabaseException {
		// SELECTION OF ALL THE SAMPLE GROUPS IN THE DATABASE TO DISPLAY ON THE
		// SITE (MULTIPLE SELECT)
		List<HemoSampleGroup> sampleGroups = db.find(HemoSampleGroup.class);
		if (sampleGroups != null) {
			for (HemoSampleGroup hsg : sampleGroups) {
				String name = hsg.getName();
				getModel().getNames().add(name);
			}
		} else {
			System.out.println("OOPS! SOMETHING WENT WRONG");
		}
	}

	public List<String> selectSamplesFromSampleGroups(Database db,
			List<String> sampleGroups) throws DatabaseException {
		// SELECTION OF THE SAMPLE NAMES WITHIN EACH GROUP THAT IS SPECIFIED
		// TODO: RETURN SAMPLE NAMES TO HANDLEREQUEST
		List<String> sampleNames = new ArrayList<String>();
		int numberGroups = sampleGroups.size();
		if (numberGroups == 0) {
			System.out.println("there is no selection made");
		} else {
			// System.out.println("\n" + "number of groups: " + numberGroups);
			for (String hsg : sampleGroups) {
				// System.out.println("\n" + "sampleGroup is: " + hsg);
				List<HemoSample> samplesPerGroup = db.find(HemoSample.class,
						new QueryRule(HemoSample.SAMPLEGROUP_NAME,
								Operator.EQUALS, hsg));
				for (HemoSample name : samplesPerGroup) {
					sampleNames.add(name.getName());
				}
			}
			// System.out.println("sampleNames for this group: " + sampleNames
			// + "\n");
		}
		return sampleNames;
	}

	public List<String> selectProbesWithGenes(Database db, String[] genes)
			throws DatabaseException {
		// GETS A STRING WITH GENE NAMES, CONVERTS THEM TO PROBES AND RETURNS
		// THEM

		try {
			List<String> probes = new ArrayList<String>();
			for (String gene : genes) {
				gene = gene.toUpperCase();
				gene = StringUtils.chomp(gene);
				System.out.println("gene is: " + gene);
				List<HemoProbe> probesPerGene = db.find(HemoProbe.class,
						new QueryRule(HemoProbe.REPORTSFOR_NAME,
								Operator.EQUALS, gene));
				for (HemoProbe probe : probesPerGene) {
					System.out.println("name of probe: " + probe.getName());
					probes.add(probe.getName());
					getModel().getResults().add(probe.getName());
				}
			}
			if (!probes.isEmpty()) {
				System.out.println("probes found with this gene(s): " + probes);
				return probes;
			} else if (probes.isEmpty()) {
				System.out
						.println("This gene cannot be found in this database.");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("This gene cannot be found in this database.");
			e.printStackTrace();
		}
		return null;
	}

	public List<String> selectGenesWithProbes(Database db, String[] probes)
			throws DatabaseException {
		// GETS A STRING WITH PROBE NAMES, CONVERTS THEM TO GENES AND RETURNS
		// THEM
		// stringsToConvert = list met probe names
		try {
			List<String> genes = new ArrayList<String>();
			for (String probe : probes) {
				probe = StringUtils.chomp(probe);
				List<HemoProbe> genesForProbe = db.find(HemoProbe.class,
						new QueryRule(HemoProbe.NAME, Operator.EQUALS, probe));
				for (HemoProbe gfp : genesForProbe) {
					genes.add(gfp.getReportsFor_Name());
					getModel().getResults().add(gfp.getReportsFor_Name());
				}
			}
			if (!genes.isEmpty()) {
				System.out.println("gene found with this probe: " + genes);
				return genes;
			} else if (genes.isEmpty()) {
				System.out
						.println("This probe cannot be found in this database.");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("This gene cannot be found in this database.");
			e.printStackTrace();
		}
		return null;
	}

	public List<String> selectAllProbeNames(Database db) {
		List<String> allProbes = new ArrayList<String>();
		List<HemoProbe> probes = db.find(HemoProbe.class, Operator.EQUALS, "*");
		return null;

	}

	public DataMatrixInstance getMatrixInstance(Database db, Data dataSet,
			List<String> sampleNames, List<String> probeNames) throws Exception {
		DataMatrixHandler dmh = new DataMatrixHandler(db);
		BinaryDataMatrixInstance m = (BinaryDataMatrixInstance) dmh
				.createInstance(dataSet, db);

		// filter on sampleNames and on probeNames
		return m.getSubMatrix(probeNames, sampleNames);
	}

	public void questionOne(Database db, Tuple request) {
		try {
			// GENE EXPRESSION DATA MATRIX SELECTION -> shows which type of gene
			// expression will be used
			String geneExp = request.getString("geneExp");

			// LIST OF GENES SELECTION -> Gets all the listed genes from the
			// website
			String genesFromSite = request.getString("geneText");
			String[] genes = genesFromSite.split("\n");
			List<String> probes = null;

			if (genes != null) {
				if (genes.length == 0) {
					System.out
							.println("There are no genes in the inputfield. Try again.");
				} else {
					probes = selectProbesWithGenes(db, genes);
				}
			} else {
				System.out
						.println("There are no genes specified. Please try again");
			}

			// SAMPLE SELECTION BASED ON SAMPLE GROUP NAMES -> Gets the selected
			// groups from the website and retrieves the associated samples
			List<String> groups = request.getStringList("sampleGroups");
			List<String> sampleNames = selectSamplesFromSampleGroups(db, groups);
			System.out.println("Groups are: " + groups);
			System.out.println("Samples selected are: " + sampleNames);

			// SUBMIT BUTTON
			if (request.getAction().equals("submitInfoQ1")) {
				System.out
						.println("Q1!!! We handled the information on the site submitted via the submit button");
				getModel().setState(QuestionState.QUESTION1_RESULT);
			}
			if (QuestionState.QUESTION1_RESULT.equals(getModel().getState())) {

				// get the data set
				Data dataSet = db.query(Data.class).eq(Data.NAME, geneExp)
						.find().get(0);

				// get the Browser which is in the other plugin
				MatrixManagerHemodb matrixManager = (MatrixManagerHemodb) this
						.getApplicationController().get("QuestionsSub");

				// the matrix we want to show
				DataMatrixInstance m = this.getMatrixInstance(db, dataSet,
						sampleNames, probes);

				Browser br = new Browser(dataSet, m,
						this.getApplicationController());
				MatrixManagerModel model = (MatrixManagerModel) matrixManager
						.getMyModel();
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

				if (model.getRowHeaderAttr() == null
						|| model.getColHeaderAttr() == null) {
					matrixManager.setHeaderAttr(db);
				}

				if (model.getAllOperators() == null) {
					matrixManager.setAllOperators();
				}

				if (model.getValueOperators() == null) {
					matrixManager.setValueOperators();
				}

				matrixManager.createHeaders();

				RequestHandler.handle(model, request, db);

			} else {
				System.out.println("Something went wrong, try again.");
			}
		}

		// TODO show selected data
		catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void questionTwo(Database db, Tuple request) throws Exception {

		if ("back".equals(request.getAction())) {
			getModel().setState(QuestionState.BEGINNING);
		}

		// GENE EXPRESSION DATA MATRIX SELECTION (raw/normalized data)
		String geneExp = request.getString("geneExp");

		// Sample combining method selection (Mean/Median)
		String sampleCombine = request.getString("sampleCombine");

		// SAMPLE SELECTION BASED ON SAMPLE GROUP NAMES
		List<String> groups = request.getStringList("sampleGroups");
		int numberGroups = groups.size();
		List<String> sampleNamesGroup1 = new ArrayList<String>();
		List<String> sampleNamesGroup2 = new ArrayList<String>();

		if (numberGroups == 2) {
			List<String> sampleGroup1 = new ArrayList<String>();
			sampleGroup1.add(groups.get(0));
			List<String> sampleGroup2 = new ArrayList<String>();
			sampleGroup2.add(groups.get(1));

			sampleNamesGroup1 = selectSamplesFromSampleGroups(db, sampleGroup1);
			sampleNamesGroup2 = selectSamplesFromSampleGroups(db, sampleGroup2);
		} else {
			System.out
					.println("There can only be a selection of 2 groups. Try again.");
		}

		// Select significance cutoff to be used
		double signifCutoff = request.getDouble("signifCutoff");

		System.out.println("Gene expression dataset is: " + geneExp);
		System.out.println("Method of combination is: " + sampleCombine);
		System.out.println("Samples in group One are: " + sampleNamesGroup1);
		System.out.println("Samples in group Two are: " + sampleNamesGroup2);
		System.out.println("Significancy cutoff is: " + signifCutoff);

		// SUBMIT BUTTON Question 2
		if (request.getAction().equals("submitInfoQ2")) {
			System.out
					.println("Q2!!! We handled the information on the site submitted via the submit button");
			getModel().setState(QuestionState.QUESTION2_RESULT);
		}

		// TODO:
		// Choose binary file based on geneExp
		// make submatrix with sampleNames
		// see pseudocode on paper for (row: rows)

		if (sampleCombine.equals("sampleCombineMean")) {
			// roep mean script aan met alle data
			List<String> significantGenes = CalculateMeanR
					.calculateMean(db, geneExp, sampleNamesGroup1,
							sampleNamesGroup2, signifCutoff);
		} else {
			// roep median script aan met alle data
			List<String> significantGenes = CalculateMedianR.calculateMedian(
					db, geneExp, sampleNamesGroup1, sampleNamesGroup2,
					signifCutoff);
		}

	}

	public void conversingBetweenProbesAndGenes(Database db, Tuple request)
			throws DatabaseException {
		// TODO: make result section
		// write to output
		if ("back".equals(request.getAction())) {
			getModel().setState(QuestionState.BEGINNING);
			getModel().setResults(null);
		}

		// SUBMIT BUTTON Question 3
		if (request.getAction().equals("submitInfoQ3")) {
			System.out
					.println("Q3!!! We handled the information on the site submitted via the submit button");
			getModel().setState(QuestionState.QUESTION3_RESULT);
		}

		String converting = request.getString("convertGP");
		String listFromSite = request.getString("gpText");
		String[] stringsToConvert = listFromSite.split("\n");

		if (converting.equals("convertGenes")) {
			List<String> probes = selectProbesWithGenes(db, stringsToConvert);
			System.out.println("selected probes are: " + probes);
		} else if (converting.equals("convertProbes")) {
			List<String> genes = selectGenesWithProbes(db, stringsToConvert);
			System.out.println("selected genes are: " + genes);
		}
	}

}
