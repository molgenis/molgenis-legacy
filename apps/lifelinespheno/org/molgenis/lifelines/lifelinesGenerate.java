package org.molgenis.lifelines;

import org.molgenis.Molgenis;
import org.molgenis.generators.db.PersistenceGen;

public class lifelinesGenerate {

    public static void main(String[] args) throws Exception {
        //new Molgenis("excludedFromTest/lifelines/org/molgenis/lifelines/lifelines.molgenis.properties", PersistenceGen.class).generate();
        new Molgenis("apps/lifelinespheno/org/molgenis/lifelines/lifelines.molgenis.properties").generate();
    }
}
