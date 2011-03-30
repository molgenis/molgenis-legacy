package mydas.examples;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.molgenis.MolgenisOptions;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.util.cmdline.CmdLineException;
import org.molgenis.xgap.Chromosome;
import org.molgenis.xgap.Gene;

import uk.ac.ebi.mydas.exceptions.BadReferenceObjectException;
import uk.ac.ebi.mydas.exceptions.DataSourceException;
import uk.ac.ebi.mydas.model.DasAnnotatedSegment;
import uk.ac.ebi.mydas.model.DasComponentFeature;
import uk.ac.ebi.mydas.model.DasFeature;
import uk.ac.ebi.mydas.model.DasMethod;
import uk.ac.ebi.mydas.model.DasType;
import app.JDBCDatabase;

public class XgapTestManager {
	private ArrayList<DasType> types;
	private DasType geneType;
	private DasMethod method;
	
	private JDBCDatabase db;

	public XgapTestManager() throws DataSourceException {
		// Initialize types
		geneType = new DasType("Gene", null, "SO:0000704", "Gene");
		types = new ArrayList<DasType>();
		types.add(geneType);
		method = new DasMethod("not_recorded", "not_recorded", "ECO:0000037");
		// Create database
		try {
			db = new JDBCDatabase(new MolgenisOptions("xgap.properties"));
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CmdLineException e) {
			e.printStackTrace();		
		}
		
		
		
		
	}

	public void close() {
		try {
			db.close();
		} catch (Exception e) { /* ignore close errors */
		}
	}

	private DasComponentFeature getGene(String geneID, int startI, int stopI,
			DasAnnotatedSegment segment) throws DataSourceException {
		for (DasFeature feature : segment.getFeatures())
			if (feature.getFeatureId().equals(geneID))
				return (DasComponentFeature) feature;
		return segment.getSelfComponentFeature().addSubComponent(geneID,
				startI, stopI, startI, stopI, geneID, geneType, geneID, geneID,
				method, null, null, null, null, null);
	}

	private DasAnnotatedSegment getSegment(
			Collection<DasAnnotatedSegment> segments, String segmentId)
			throws DataSourceException {
		for (DasAnnotatedSegment segment : segments)
			if (segment.getSegmentId().equals(segmentId))
				return segment;
		DasAnnotatedSegment newSegment = new DasAnnotatedSegment(segmentId, 1,
				1, "test_version", segmentId, new ArrayList<DasFeature>());
		segments.add(newSegment);
		return newSegment;
	}

	public DasAnnotatedSegment getSubmodelBySegmentId(String segmentId,
			int start, int stop) throws DataSourceException,
			BadReferenceObjectException {
		DasAnnotatedSegment segment = null;
		try {
			// Convert to DAS Segment.
			segment = new DasAnnotatedSegment(segmentId, 1,
					1, "test_version", segmentId, new ArrayList<DasFeature>());
			
			// Get chromosome from DB.
			QueryRule rule1 = new QueryRule("name", QueryRule.Operator.EQUALS, segmentId);
			List<Chromosome> chroms = db.find(Chromosome.class, rule1);
			if (chroms.size()>0){
			
				Chromosome chrom = chroms.get(0);
				
				
				
				// Take all genes across this segment (= the entire chromosome if start and end are not given)
				QueryRule rule_chrom = new QueryRule("Chromosome", QueryRule.Operator.EQUALS, chrom.getId());
				QueryRule rule2;
				if((start == -1)&&(stop==-1)){
					rule2 = rule_chrom;
				} else {
					
					QueryRule rule_start = new QueryRule("bpEnd", QueryRule.Operator.GREATER, start);
					QueryRule rule_stop = new QueryRule("bpStart", QueryRule.Operator.LESS, stop);
					rule2 = new QueryRule(rule_chrom, rule_start, rule_stop);
				}
				List<Gene> genes = db.find(Gene.class, rule2);
				for (Gene gene: genes) {
					int bpStart = gene.getBpStart().intValue();
					int bpEnd = gene.getBpEnd().intValue();
					// Convert to DAS Feature.
					getGene(gene.getIdField(), bpStart, bpEnd, segment);
					
					
				}
			}
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		
		
		
		return segment;
	}

	public Collection<DasAnnotatedSegment> getSubmodelByFeatureId(
			Collection<String> featureIdCollection) throws DataSourceException {

		Collection<DasAnnotatedSegment> segments = null;
		try {
			
			for (String featureId : featureIdCollection) {
				// Get gene with this gene symbol
				QueryRule rule = new QueryRule("Symbol", QueryRule.Operator.EQUALS, featureId);				
				List<Gene> genes = db.find(Gene.class, rule);
				if (genes.size()>0){
					Gene gene = genes.get(0);
					if (segments == null)
						segments = new ArrayList<DasAnnotatedSegment>();
					DasAnnotatedSegment segment = this.getSegment(segments, gene.getChromosome_Name());
					this.getGene(
							featureId, gene.getBpStart().intValue(), gene.getBpEnd().intValue(), segment);
				}
			}
		} catch (DatabaseException e) {
			e.printStackTrace();
		}	
		return segments;
	}

	public ArrayList<DasType> getTypes() {
		return types;
	}

	public Integer getTotalCountForType(String typeId)
			throws DataSourceException {
				
		Integer count = 0;
		if (typeId.equalsIgnoreCase("Gene")){ 
			try {
				count = db.count(Gene.class);
			} catch (DatabaseException e1) {
				e1.printStackTrace();
			}
		}		
		return count;
	}
}