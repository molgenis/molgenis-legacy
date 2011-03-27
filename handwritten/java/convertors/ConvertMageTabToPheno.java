package convertors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.molgenis.core.Ontology;
import org.molgenis.core.OntologyTerm;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReader;
import org.molgenis.util.CsvReaderListener;
import org.molgenis.util.Tuple;

import app.JDBCDatabase;

/*
 * Changelog:
 * Treat Characteristic[Individual] as observable feature to make this generic not only for MOLPAGE but for all ArrayExpress.
 */
public class ConvertMageTabToPheno
{
	public static void main(String[] args) throws FileNotFoundException, IOException, DatabaseException
	{
		// should become commandline parameter taking IDF first and loading that
		// in Investigation.
		File sdrfFile = new File("data/ArrayExpress/E-TABM-325.sdrf.txt");

		final Database db = new JDBCDatabase("molgenis.properties");

		String INVESTIGATION_NAME = "MOLPAGE";

		try
		{
			db.beginTx();

			// Investigation
			final Investigation inv = new Investigation();
			inv.setName(INVESTIGATION_NAME);
			db.add(inv);

			// add ontology term for species if needed
			// EFO,http://www.ebi.ac.uk/efo
			Ontology termSource = new Ontology();
			termSource.setName("EFO");
			termSource.setOntologyURI("http://www.ebi.ac.uk/efo");
			// db.add(termSource);

			final OntologyTerm species = new OntologyTerm();
			species.setName("human");
			// species.setTermLabel(termSource.getName() + ":human");
			species.setTermAccession("http://www.ebi.ac.uk/efo/EFO_0001994");
			species.setOntology(termSource.getId());
			db.add(species);

			// parsing
			CsvReader reader = new CsvFileReader(sdrfFile);
			reader.setSeparator('\t');

			final Map<String, Individual> iMap = new LinkedHashMap<String, Individual>();

			// load all features, optionally adding terms
			final Map<String, ObservableFeature> fMap = new LinkedHashMap<String, ObservableFeature>();
			
			for (String annotation : reader.colnames().subList(1, reader.colnames().indexOf("Sample Name")))
			{
				if (annotation.startsWith("Characteristics"))
				{
					String characteristic = annotation.substring(annotation.indexOf('[') + 1, annotation.indexOf(']'));

					// exclude 'Individual'
					//if (!characteristic.equalsIgnoreCase("Individual"))
					//
						ObservableFeature f = new ObservableFeature();
						f.setInvestigation(inv.getId());
						f.setName(characteristic);
						f.setInvestigation(inv.getId());
						fMap.put(characteristic, f);
					//}
				}
				else if (annotation.equals("Term Source REF"))
				{
					// ah, it is a term too, dirty even
					

				}
			}
			for (ObservableFeature f : fMap.values())
				System.out.println(f);
			db.add(new ArrayList<ObservableFeature>(fMap.values()));

			// add individuals at first parse
			reader.parse(new CsvReaderListener()
			{

				@Override
				public void handleLine(int lineNo, Tuple line) throws Exception
				{
					//String individual = line.getString("Characteristics [Individual]");
					String individual = line.getString("Source Name");

					if (!iMap.containsKey(individual))
					{
						Individual i = new Individual();
						i.setName(individual);
						i.setInvestigation(inv.getId());
						// i.setSpecies(species.getId());
						// need special characteristics like 'sex'
						// i.setSex("unknown");
						iMap.put(individual, i);
					}
				}
			});
			for (Individual i : iMap.values())
				System.out.println(i);
			db.add(new ArrayList<Individual>(iMap.values()));

			// add the values, and optionally ontology references, by parsing
			// again
			final Map<String, ObservedValue> vMap = new LinkedHashMap<String, ObservedValue>();
			final Map<String, OntologyTerm> tMap = new LinkedHashMap<String, OntologyTerm>();
			final Map<String, Ontology> sMap = new LinkedHashMap<String, Ontology>();
			reader.reset();
			reader.parse(new CsvReaderListener()
			{

				@Override
				public void handleLine(int lineNo, Tuple line) throws Exception
				{
					//String individualName = line.getString("Characteristics [Individual]");
					String individualName = line.getString("Source Name");
					Individual i = iMap.get(individualName);
					if (i == null)
						throw new Exception("Source unknown: " + individualName);
					ObservedValue value = null;

					// add all characteristics properly,skip first column
					// describing source
					for (int j = 1; j < line.size(); j++)
					{
						String fieldName = line.getFields().get(j).trim();

						// until
						if ("Sample Name".equals(fieldName))
							break;

						// characteristic == ObservedValue
						if (line.getString(j) != null)
						{
							String fieldValue = line.getString(j).trim();
							if (fieldName.startsWith("Characteristics"))
							{
								String characteristic = fieldName.substring(fieldName.indexOf('[') + 1, fieldName
										.indexOf(']'));
								//if (!characteristic.equalsIgnoreCase("Individual"))
								//{

									ObservableFeature f = fMap.get(characteristic);

									// exception for specific characteristics
									// 'sex',

									value = new ObservedValue();
									value.setInvestigation(inv.getId());
									value.setTarget(i.getId());
									value.setFeature(f.getId());
									value.setValue(fieldValue);

									// removes duplicates
									vMap.put(i.getName() + "_" + f.getName(), value);
								//}
							}
							else if (fieldName.equals("Term Source REF") && !fieldValue.equals(""))
							{
								Ontology ontology = sMap.get(fieldValue);
								if (ontology == null)
								{
									ontology = new Ontology();
									ontology.setName(fieldValue);
									ontology.setOntologyAccession(fieldValue);
									System.out.println("adding source: " + ontology);
									db.add(ontology);
									sMap.put(fieldValue, ontology);
								}

								// see if characteristic is known as phenotype
								OntologyTerm term = tMap.get(ontology.getName() + "__" + value.getValue());
								if (term == null)
								{
									term = new OntologyTerm();
									term.setName(value.getValue());
									term.setOntology(ontology.getId());
									System.out.println("adding term: " + term);
									db.add(term);
									tMap.put(ontology.getName() + "__" + value.getValue(), term);
								}
								value.setOntologyReference(term.getId());
							}
						}
					}
				}
			});
			// for(ObservedValue v: vMap.values()) System.out.println(v);
			db.add(new ArrayList<ObservedValue>(vMap.values()));

			db.commitTx();

		}
		catch (Exception e)
		{
			db.rollbackTx();
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

	}
}
