/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package umcg.genetica.io.gwascatalog;

import java.util.HashSet;

/**
 *
 * @author harmjan
 */
public class GWASTrait {

    int id;
    String name;
    HashSet<GWASLocus> loci = new HashSet<GWASLocus>();
    HashSet<GWASPublication> publishedIn = new HashSet<GWASPublication>();
    HashSet<GWASSNP> snps = new HashSet<GWASSNP>();
    GWASSNP[] snpArray = null;

    public String getName() {
        return name;
    }

    public GWASSNP[] getSNPs() {
        if (snpArray == null) {
            snpArray = new GWASSNP[snps.size()];
            snps.toArray(snpArray);
        }
        return snpArray;
    }
}
