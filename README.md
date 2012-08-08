MOLGENIS
--------
MOLGENIS is an collaborative open source project on a mission to generate great 
software infrastructure for life science research. Each app in the MOLGENIS 
family comes with rich data management interface and plug-in integration of 
analysis tools in R, Java and web services.

Developing your own
-------------------
Generate your own in three steps:

1) Clone the MOLGENIS generator and an empty distro:

     git clone https://www.github.com/molgenis/molgenis.git
     git clone https://www.github.com/molgenis/molgenis_distro.git
     cd molgenis_distro

2) Model what you want for your experiment in a simple XML file example db, example ui:

     <editor> molgenis.properties
     <editor> molgenis_db.xml
     <editor> molgenis_ui.xml

3) Run the MOLGENIS generator, after that you're able to use your web 
application.

MOLGENIS applications
---------------------
Many molgenis applications have been developed, a not so short overview:

 - xQTL Workbench for multi-level QTL mapping ([project](http://www.xqtl.nl/ "www.xqtl.nl"))
 - Dystrophic Epidermolysis Bullosa (deb-central) mutation database ([project](http://www.deb-central.org/ "www.deb-central.org/"), publication)
 - eXtensible Genotype and Phenotype database (XGAP) ([project](http://www.xqap.nl/ "www.xqap.nl"), publication)
 - Design of Genetical Genomics Experiments (designGG)] ([project](http://gbic.biol.rug.nl/designGG "DesignGG"), publication)
 - BBMRI-NL biobank catalague ([project](http://www.phenoflow.org/wiki/BiobankCatalog "BBMRI")) 
 - MAGE-TAB microarray gene experiment object model (MAGETAB-OM)] ([project](http://www.phenoflow.org/wiki/PhenoFlow "MAGETAB"),  demo)
 - Pheno-OM Phenotype observation model ([project](http://www.phenoflow.org/wiki/PhenoFlow "Pheno-OM"), demo)
 - Mouse Resource Browser (MRB) project] (project, publication)
 - MOLGENIS as data wrapper in Taverna (publication)
 - Animal observation database (AnimalDB) ([project](http://www.animaldb.org/ "www.animaldb.org"))
 - Nordic GWAS control database (project,  publication)
 - GWAS Central curation tool (project)
 - Finnish disease database (FINDIS) (project)
 - Bacterial microarrays database (MOLGEN-IS) (publication)
 - Human Metabolic Pathway Database (project, publication)

If you think your project should be listed (differently) please let us know.
