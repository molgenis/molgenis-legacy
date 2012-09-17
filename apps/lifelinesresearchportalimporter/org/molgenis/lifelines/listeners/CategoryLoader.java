//package org.molgenis.lifelines.listeners;
//
//import static ch.lambdaj.Lambda.filter;
//import static ch.lambdaj.Lambda.having;
//import static ch.lambdaj.Lambda.on;
//import static org.hamcrest.Matchers.equalToIgnoringCase;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.zip.DataFormatException;
//
//import javax.persistence.EntityManager;
//
//import org.apache.commons.lang.ArrayUtils;
//import org.molgenis.organization.Investigation;
//import org.molgenis.pheno.Category;
//import org.molgenis.pheno.Measurement;
//import org.molgenis.pheno.ObservableFeature;
//import org.molgenis.protocol.Protocol;
//
//import au.com.bytecode.opencsv.CSVReader;
//
////import static org.hamcrest.text.IsEqualIgnoringCase.*;
//
//public class CategoryLoader {
//	
//	private final EntityManager em;a
//	private final Map<String, Protocol> protocols;
//	private final List<Category> categories = new ArrayList<Category>();
//	private final Investigation investigation;
//	private final boolean shareMeasurements;
//	private List<String> protocolsToImport;
//	private final CSVReader csvFileReader;
//	
//	public CategoryLoader(final File file, final String encoding, Map<String, Protocol> protocols,
//			Investigation investigation, EntityManager em,
//			boolean shareMeasurements, List<String> protocolsToImport) throws IOException, DataFormatException {
//		this.em = em;
//		this.protocols = protocols;
//		this.investigation = investigation;
//		this.shareMeasurements = shareMeasurements;
//		
//		this.protocolsToImport = protocolsToImport;
//		
//		this.csvFileReader = 
//			new CSVReader(
//				new BufferedReader(
//						new InputStreamReader(
//								new FileInputStream(file), encoding)
//				)
//			);
//	}
//
//	final private HashSet<String> uniqueLabelWithinCode = new HashSet<String>();
//	
//	public void load() throws Exception {
//		final String[] headers = csvFileReader.readNext();
//		
//		int tabNaamIdx = ArrayUtils.indexOf(headers, "TABNAAM");
//		int veldIdx = ArrayUtils.indexOf(headers, "VELD");
//		int valLabelValIdx = ArrayUtils.indexOf(headers, "VALLABELVAL");
//		int valLabelLabelIdx = ArrayUtils.indexOf(headers, "VALLABELABEL");
//		
//		String[] row = null;
//		while((row = csvFileReader.readNext()) != null)
//		{
//			final String tableName = row[tabNaamIdx];
//			final String fieldName = shareMeasurements ? row[veldIdx] : tableName + "_" + row[veldIdx];
//			final String valLabelVal = row[valLabelValIdx];
//			final String valLabelLabel = row[valLabelLabelIdx]; 
//			
//			if(protocolsToImport != null) {
//				if(!protocolsToImport.contains(tableName.toUpperCase())) {
//					continue;
//				}
//			}
//			
//			final Protocol protocol = protocols.get(tableName);
//			if(protocol == null) {
//				continue;
//			}
//	
//			final List<ObservableFeature> measurements = (List<ObservableFeature>) protocol.getFeatures();
//			final List<ObservableFeature> filterMeasurements = filter(
//					having(on(Measurement.class).getName(),
//							equalToIgnoringCase(fieldName)), measurements);
//			final Measurement measurement = (Measurement) filterMeasurements.get(0);
//	
//			final Category category = new Category();
//			category.setInvestigation(investigation);
//			category.setCode_String(valLabelVal);
//			category.setLabel(valLabelLabel);
//			category.setDescription(valLabelLabel);
//			category.getCategoriesMeasurementCollection().add(measurement); // add to
//	
//			measurement.getCategories().add(category); // measurment
//			
//			category.setName(fieldName + "_" + category.getLabel());
//			category.setName(fieldName + "_" + valLabelVal);
//	
//			//check to see if label is Unique
//			final String uniqueLabel = fieldName + "_" + category.getLabel(); 
//			if(uniqueLabelWithinCode.contains(uniqueLabel)) {
//				throw new IllegalArgumentException(String.format("Non unique in category Label: %s", uniqueLabel));
//			}
//			uniqueLabelWithinCode.add(uniqueLabel);
//			
//			categories.add(category);
//		}
//		commit();
//	}
//
//	public void close() throws IOException {
//		csvFileReader.close();
//	}
//	
//	private void commit() throws Exception {
//		try {
//			em.getTransaction().begin();
//			for (Category c : categories) {
//				em.persist(c);
//			}
//			em.flush();
//			em.getTransaction().commit();
//		} catch (Exception ex) {
//			throw ex;
//		}
//	}
// }
