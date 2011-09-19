package mydas.examples;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.xgap.Gene;

import uk.ac.ebi.mydas.exceptions.BadReferenceObjectException;
import uk.ac.ebi.mydas.exceptions.DataSourceException;
import uk.ac.ebi.mydas.model.DasAnnotatedSegment;
import uk.ac.ebi.mydas.model.DasComponentFeature;
import uk.ac.ebi.mydas.model.DasFeature;
import uk.ac.ebi.mydas.model.DasMethod;
import uk.ac.ebi.mydas.model.DasType;
import app.DatabaseFactory;

public class EnsemblTestManager2 {
	private ArrayList<DasType> types;
	private DasType geneType, transcriptType, exonType;
	private DasMethod method;
	private Connection connection;
	private Database db;

	public EnsemblTestManager2() throws DataSourceException, FileNotFoundException, IOException, DatabaseException {
		// Initialize types
		geneType = new DasType("Gene", null, "SO:0000704", "Gene");
		transcriptType = new DasType("Transcript", null, "SO:0000673",
				"Transcript");
		exonType = new DasType("Exon", null, "SO:0000147", "Exon");
		types = new ArrayList<DasType>();
		types.add(geneType);
		types.add(transcriptType);
		types.add(exonType);
		method = new DasMethod("not_recorded", "not_recorded", "ECO:0000037");

		connection = null;
		//TODO: Danny: Use or loose
		/*String userName = "anonymous";
		String password = "";*/
		//String url = "jdbc:mysql://ensembldb.ensembl.org:5306/homo_sapiens_core_56_37a";
		db = DatabaseFactory.create("xgap.properties");
		
		/**
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection(url, userName, password);
		} catch (InstantiationException e) {
			throw new DataSourceException("Problems loading the MySql driver",
					e);
		} catch (IllegalAccessException e) {
			throw new DataSourceException("Problems loading the MySql driver",
					e);
		} catch (ClassNotFoundException e) {
			throw new DataSourceException("Problems loading the MySql driver",
					e);
		} catch (SQLException e) {
			throw new DataSourceException(
					"Problems conecting to the ensembl database", e);
		}**/
	}

	public void close() {
		try {
			connection.close();
		} catch (Exception e) { /* ignore close errors */
		}
	}
	public  Collection<DasAnnotatedSegment> getSubmodel() throws DataSourceException{
	
		Collection<DasAnnotatedSegment> segments = null;
		// Get genes		
		/**List<Gene> genes = db.find(Gene.class);
		Iterator<Gene> genesIt = genes.iterator();
		// Loop through genes
		while (genesIt.hasNext()) {			
			Gene gene = genesIt.next();			
			if (segments == null)
				segments = new ArrayList<DasAnnotatedSegment>();
			
			DasAnnotatedSegment segment = this.getSegment(segments, gene.getChromosome_name());
			DasComponentFeature geneDas = this.getGene(gene.getIdField(),gene.getBpStart().intValue(),gene.getBpEnd().intValue(),segment); 
			//DasComponentFeature transcript = this.getTranscript(gene. gene);
			//this.getExon(rs.getString("exon_id"), rs.getInt("exon_start"),
				//	rs.getInt("exon_end"), transcript);
		}**/
		return segments;
		
		
	}
	public Collection<DasAnnotatedSegment> getSubmodelBySQL(String sql)
			throws DataSourceException {
		Collection<DasAnnotatedSegment> segments = null;
		try {
			Statement s = connection.createStatement();
			s.executeQuery(sql);
			ResultSet rs = s.getResultSet();
			while (rs.next()) {
				if (segments == null)
					segments = new ArrayList<DasAnnotatedSegment>();
				DasAnnotatedSegment segment = this.getSegment(segments, rs
						.getString("chr"));
				DasComponentFeature gene = this.getGene(
						rs.getString("gene_id"), rs.getInt("gene_start"), rs
								.getInt("gene_end"), segment);
				DasComponentFeature transcript = this.getTranscript(rs
						.getString("trascript_id"), rs
						.getInt("transcript_start"), rs
						.getInt("transcript_end"), gene);
				this.getExon(rs.getString("exon_id"), rs.getInt("exon_start"),
						rs.getInt("exon_end"), transcript);
			}
			rs.close();
			s.close();
		} catch (SQLException e) {
			throw new DataSourceException("Problems executing the sql query", e);
		}
		return segments;
	}

	private DasComponentFeature getExon(String exonID, int startI, int stopI,
			DasComponentFeature transcript) throws DataSourceException {
		for (DasComponentFeature feature : transcript
				.getReportableSubComponents())
			if (feature.getFeatureId().equals(exonID))
				return feature;
		return transcript.addSubComponent(exonID, startI, stopI, startI, stopI,
				exonID, this.exonType, exonID, exonID, method, null, null,
				null, null, null);
	}

	private DasComponentFeature getTranscript(String transcriptID, int startI,
			int stopI, DasComponentFeature gene) throws DataSourceException {

		for (DasComponentFeature feature : gene.getReportableSubComponents())
			if (feature.getFeatureId().equals(transcriptID))
				return feature;
		return gene.addSubComponent(transcriptID, startI, stopI, startI, stopI,
				transcriptID, this.transcriptType, transcriptID, transcriptID,
				method, null, null, null, null, null);
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
				1, "testKJ", segmentId, new ArrayList<DasFeature>());
		segments.add(newSegment);
		return newSegment;
	}

	public DasAnnotatedSegment getSubmodelBySegmentId(String segmentId,
			int start, int stop) throws DataSourceException,
			BadReferenceObjectException {
		
		
		
		String sql = "SELECT " + " sr.name AS chr, "
				+ " gsi.stable_id AS gene_id, "
				+ " g.seq_region_start AS gene_start, "
				+ " g.seq_region_end AS gene_end, "
				+ " tsi.stable_id AS trascript_id, "
				+ " t.seq_region_start AS transcript_start, "
				+ " t.seq_region_end AS transcript_end, "
				+ " esi.stable_id AS exon_id, "
				+ " e.seq_region_start AS exon_start, "
				+ " e.seq_region_end AS exon_end " + "FROM  "
				+ " seq_region sr, " + " gene_stable_id gsi, " + " gene g, "
				+ " transcript t, " + " transcript_stable_id tsi, "
				+ " exon_transcript et, " + " exon e, "
				+ " exon_stable_id esi  " + "WHERE  "
				+ " gsi.gene_id = g.gene_id and "
				+ " g.gene_id = t.gene_id and  "
				+ " t.transcript_id = tsi.transcript_id and "
				+ " t.transcript_id = et.transcript_id and  "
				+ " et.exon_id = e.exon_id and  "
				+ " e.exon_id = esi.exon_id and  "
				+ " g.seq_region_id = sr.seq_region_id and "
				+ " sr.coord_system_id = 2 and " + " sr.name ='" + segmentId
				+ "' ";
		if (start != -1 && stop != -1)
			sql += " and g.seq_region_start>" + start
					+ " and g.seq_region_end<" + stop;
		Collection<DasAnnotatedSegment> segments = getSubmodelBySQL(sql);
		if (segments != null && segments.size() > 0)
			return segments.iterator().next();
		else
			throw new BadReferenceObjectException("Unknown Chromosome",
					segmentId);
	}

	public Collection<DasAnnotatedSegment> getSubmodelByFeatureId(
			Collection<String> featureIdCollection) throws DataSourceException {
		Collection<DasAnnotatedSegment> segments = null;
						
		for (String featureId : featureIdCollection) {
			if (segments == null)
				segments = new ArrayList<DasAnnotatedSegment>();
			Gene gene;
			try {
				gene = db.findById(Gene.class, featureId);
				DasAnnotatedSegment segment = this.getSegment(segments, gene.getChromosome_Name());
				//NOTE: Danny: Use or loose
				/*DasComponentFeature geneDas = */this.getGene(gene.getIdField(),gene.getBpStart().intValue(),gene.getBpEnd().intValue(),segment);
			} catch (DatabaseException e) {
				e.printStackTrace();
			}
			
			
			 
		}
		
		
		return segments;
	}

	public ArrayList<DasType> getTypes() {
		return types;
	}

	public Integer getTotalCountForType(String typeId)
			throws DataSourceException {
		String sql = "";
		Integer count = 0;
		if (typeId.equalsIgnoreCase("Gene"))
			sql = "SELECT count(stable_id) as num FROM gene_stable_id;";
		else if (typeId.equalsIgnoreCase("Transcript"))
			sql = "SELECT count(stable_id) as num FROM transcript_stable_id;";
		else if (typeId.equalsIgnoreCase("Exon"))
			sql = "SELECT count(stable_id) as num FROM exon_stable_id;";
		try {
			Statement s = connection.createStatement();
			s.executeQuery(sql);
			ResultSet rs = s.getResultSet();
			if (rs.next()) {
				count = rs.getInt("num");
			}
			rs.close();
			s.close();
		} catch (SQLException e) {
			throw new DataSourceException("Problems executing the sql query", e);
		}
		return count;
	}
}