/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package umcg.genetica.io.gwascatalog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import umcg.genetica.io.text.TextFile;

/**
 *
 * @author harmjan
 */
public class GWASCatalog {

    private HashSet<GWASLocus> loci = new HashSet<GWASLocus>();
    private HashSet<GWASSNP> snps = new HashSet<GWASSNP>();
    private HashSet<GWASTrait> traits = new HashSet<GWASTrait>();
    private GWASTrait[] traitArray = null;
    private HashMap<String, GWASPublication> publicationToObj = new HashMap<String, GWASPublication>();
    private HashMap<String, GWASSNP> snpToObj = new HashMap<String, GWASSNP>();
    private HashMap<String, GWASLocus> locusToObj = new HashMap<String, GWASLocus>();
    private HashMap<String, GWASTrait> traitToObj = new HashMap<String, GWASTrait>();

    public void read(String calatogloc) throws IOException {
	TextFile tf = new TextFile(calatogloc, TextFile.R);
	String[] headerelems = tf.readLineElemsReturnReference(TextFile.tab);

	int dateAddedCol = -1;
	int pubMedidCol = -1;
	int firstAuthorCol = -1;
	int publishDateCol = -1;
	int journalCol = -1;
	int studyCol = -1;

	int diseaseCol = -1;
	int samplesizeCol = -1;
	int samplesizeReplicationCol = -1;

	int snpriskallelecol = -1;
	int snpcol = -1;

	int pvalcol = -1;

	int col = 0;
	for (String e : headerelems) {
	    if (e.equals("Date Added to Catalog")) {
		dateAddedCol = col;
	    } else if (e.equals("PUBMEDID")) {
		pubMedidCol = col;
	    } else if (e.equals("First Author")) {
		firstAuthorCol = col;
	    } else if (e.equals("Date")) {
		publishDateCol = col;
	    } else if (e.equals("Journal")) {
		journalCol = col;
	    } else if (e.equals("Study")) {
		studyCol = col;
	    } else if (e.equals("Disease/Trait")) {
		diseaseCol = col;
	    } else if (e.equals("Initial Sample Size")) {
		samplesizeCol = col;
	    } else if (e.equals("Replication Sample Size")) {
		samplesizeReplicationCol = col;
	    } else if (e.equals("Strongest SNP-Risk Allele")) {
		snpriskallelecol = col;
	    } else if (e.equals("SNPs")) {
		snpcol = col;
	    } else if (e.equals("p-Value")) {
		pvalcol = col;
	    }
	    col++;
	}

	String[] elems = tf.readLineElemsReturnReference(TextFile.tab);
	int numtraits = 0;
	int numsnps = 0;
	int numpubs = 0;
	while (elems != null) {
	    if (elems.length > 11) {
		String pubname = elems[pubMedidCol] + "; " + elems[firstAuthorCol] + "; " + elems[publishDateCol] + "; " + elems[journalCol] + "; " + elems[studyCol];
//	    int studysize = Integer.parseInt(elems[samplesizeCol]);
//	    int studySizeReplication = Integer.parseInt(elems[samplesizeReplicationCol]);
		String trait = elems[diseaseCol];
		String snp = elems[snpcol];
		String[] snpelems = elems[snpriskallelecol].split("-");
		String riskallele = null;

		if (snpelems.length > 1) {
		    riskallele = snpelems[1];
		    if (riskallele.equals("?")) {
			riskallele = null;
		    }
		}


		if (snp.equals("NR")) {
		    // System.out.println(snp + "\t" + riskallele + "\t" + trait + "\t" + pubname);
		} else {
		    GWASPublication pub = publicationToObj.get(pubname);
		    if (pub == null) {
			pub = new GWASPublication();
			pub.id = numpubs;
			pub.name = pubname;

			numpubs++;
		    }

		    GWASTrait gwasTraitObj = traitToObj.get(trait);
		    if (gwasTraitObj == null) {
			gwasTraitObj = new GWASTrait();
			gwasTraitObj.name = trait;
			gwasTraitObj.id = numtraits;
			traitToObj.put(trait, gwasTraitObj);
			traits.add(gwasTraitObj);
			numtraits++;
		    }

		    String[] separatesnps = snp.split(",");
		    for (int s = 0; s < separatesnps.length; s++) {

			String snpname = separatesnps[s].trim();
			while (snpname.startsWith(" ")) {
			    snpname = snpname.substring(1);
			}

			GWASSNP gwasSNPObj = snpToObj.get(snpname);
			if (gwasSNPObj == null) {
			    gwasSNPObj = new GWASSNP();
			    gwasSNPObj.setName(snpname);
			    gwasSNPObj.setId(numsnps);
			    snpToObj.put(snpname, gwasSNPObj);
			    snps.add(gwasSNPObj);
			    numsnps++;
			}

			Double pval = null;
			try {
			    pval = Double.parseDouble(elems[pvalcol]);
			} catch (NumberFormatException e) {
			    System.out.println("P-value unparseable for trait: " + gwasTraitObj.getName() + " associated with SNP " + gwasSNPObj.getName() + ": " + elems[pvalcol]);
			}
			gwasSNPObj.getAssociatedTraits().add(gwasTraitObj);
			gwasSNPObj.setPValueAssociatedWithTrait(gwasTraitObj, pval);
			gwasSNPObj.getRiskAllele().put(gwasTraitObj, riskallele);

			gwasTraitObj.snps.add(gwasSNPObj);
			pub.snps.add(gwasSNPObj);
			gwasSNPObj.getPublishedIn().add(pub);
		    }



		    gwasTraitObj.publishedIn.add(pub);


		    pub.traits.add(gwasTraitObj);


		}



	    }

	    elems = tf.readLineElemsReturnReference(TextFile.tab);
	}

	System.out.println(numpubs + "pubs, " + numsnps + "snps, " + numtraits + "traits");
	tf.close();
    }

    public GWASTrait[] getTraits() {
	if (traitArray == null) {
	    traitArray = new GWASTrait[traits.size()];
	    traits.toArray(traitArray);
	}
	return traitArray;
    }

    /**
     * @return the loci
     */
    public HashSet<GWASLocus> getLoci() {
	return loci;
    }

    /**
     * @param loci the loci to set
     */
    public void setLoci(HashSet<GWASLocus> loci) {
	this.loci = loci;
    }

    /**
     * @return the snps
     */
    public HashSet<GWASSNP> getSnps() {
	return snps;
    }

    /**
     * @param snps the snps to set
     */
    public void setSnps(HashSet<GWASSNP> snps) {
	this.snps = snps;
    }

    /**
     * @param traits the traits to set
     */
    public void setTraits(HashSet<GWASTrait> traits) {
	this.traits = traits;
    }

    /**
     * @return the publicationToObj
     */
    public HashMap<String, GWASPublication> getPublicationToObj() {
	return publicationToObj;
    }

    /**
     * @param publicationToObj the publicationToObj to set
     */
    public void setPublicationToObj(HashMap<String, GWASPublication> publicationToObj) {
	this.publicationToObj = publicationToObj;
    }

    /**
     * @return the snpToObj
     */
    public HashMap<String, GWASSNP> getSnpToObj() {
	return snpToObj;
    }

    /**
     * @param snpToObj the snpToObj to set
     */
    public void setSnpToObj(HashMap<String, GWASSNP> snpToObj) {
	this.snpToObj = snpToObj;
    }

    /**
     * @return the locusToObj
     */
    public HashMap<String, GWASLocus> getLocusToObj() {
	return locusToObj;
    }

    /**
     * @param locusToObj the locusToObj to set
     */
    public void setLocusToObj(HashMap<String, GWASLocus> locusToObj) {
	this.locusToObj = locusToObj;
    }

    /**
     * @return the traitToObj
     */
    public HashMap<String, GWASTrait> getTraitToObj() {
	return traitToObj;
    }

    /**
     * @param traitToObj the traitToObj to set
     */
    public void setTraitToObj(HashMap<String, GWASTrait> traitToObj) {
	this.traitToObj = traitToObj;
    }

    public GWASSNP[] getSnpsArray() {
	GWASSNP[] snpsr = new GWASSNP[snps.size()];
	snpsr = snps.toArray(snpsr);
	return snpsr;
    }

    public GWASSNP[] getSNPsForTraitContainingKey(String key) {
	System.out.println("Looking for " + key + " snps");
	HashSet<GWASSNP> s = new HashSet<GWASSNP>();
	key = key.toLowerCase();
	for (GWASTrait t : traits) {
	    if (t.getName().toLowerCase().contains(key)) {
		System.out.println("Found trait: " + t.getName());
		GWASSNP[] traitsnps = t.getSNPs();
		s.addAll(Arrays.asList(traitsnps));
	    }
	}

	return s.toArray(new GWASSNP[s.size()]);
    }

    public GWASTrait[] getTraitsForCertainKey(String key) {
	key = key.toLowerCase();
	ArrayList<GWASTrait> selected = new ArrayList<GWASTrait>();
	for (GWASTrait t : traits) {
	    if (t.getName().toLowerCase().contains(key)) {
		selected.add(t);
	    }
	}

	return selected.toArray(new GWASTrait[selected.size()]);
    }
}


/*
Date Added to Catalog
PUBMEDID
First Author
Date
Journal
Link
Study
Disease/Trait
Initial Sample Size
Replication Sample Size
Region
Chr_id
Chr_pos
Reported Gene(s)
Mapped_gene
Upstream_gene_id
Downstream_gene_id
Snp_gene_ids
Upstream_gene_distance
Downstream_gene_distance
Strongest SNP-Risk Allele
SNPs
Merged
Snp_id_current
Context
Intergenic
Risk Allele Frequency
p-Value
Pvalue_mlog
p-Value (text)
OR or beta
95% CI (text)
Platform [SNPs passing QC]
CNV
 */