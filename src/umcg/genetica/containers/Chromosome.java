/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package umcg.genetica.containers;

import java.util.HashMap;

/**
 *
 * @author harmjan
 */
public class Chromosome {
    private String name;
    private HashMap<String, Gene> genes;

    public Chromosome(String name){
        this.name = name;
    }
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the genes
     */
    public HashMap<String, Gene> getGenes() {
        return genes;
    }

    /**
     * @param genes the genes to set
     */
    public void setGenes(HashMap<String, Gene> genes) {
        this.genes = genes;
    }

    public void addGene(Gene currGen) {
        if(genes == null){
            genes = new HashMap();
        }
        genes.put(currGen.getName(), currGen);
    }
}
