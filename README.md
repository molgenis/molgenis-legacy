MOLGENIS
--------
MOLGENIS is an collaborative open source project on a mission to generate great 
software infrastructure for life science research. Each app in the MOLGENIS 
family comes with rich data management interface and plug-in integration of 
analysis tools in R, Java and web services.

Developing your own
-------------------
Generate generate your own in three steps:

1) Clone the MOLGENIS generator and an empty distro

     git clone https://www.github.com/mswertz/molgenis.git
     git clone https://www.github.com/DannyArends/molgenis_distro.git
     cd molgenis_distro

2) Model what you want for your experiment in a simple XML file example db, example ui

     <editor> molgenis.properties
     <editor> molgenis_db.xml
     <editor> molgenis_ui.xml

3) Run the MOLGENIS generator, after that you're able to use your web 
application.

MOLGENIS applications
---------------------
Many molgenis applications have been developed, a not so short overview:

 - xQTL Workbench for multi-level QTL mapping ([project](http://www.xqtl.nl/ "www.xqtl.nl"))
 - Dystrophic Epidermolysis Bullosa (deb-central) mutation database (project, publication)
 - eXtensible Genotype and Phenotype database (XGAP) (project, publication)
 - Design of Genetical Genomics Experiments (designGG)] (project, publication)
 - BBMRI-NL biobank catalague (project)
 - MAGE-TAB microarray gene experiment object model (MAGETAB-OM)] (project,  demo)
 - Pheno-OM Phenotype observation model (project, demo)
 - Mouse Resource Browser (MRB) project] (project, publication)
 - MOLGENIS as data wrapper in Taverna (publication)
 - Animal observation database (AnimalDB) (project)
 - Nordic GWAS control database (project,  publication)
 - GWAS Central curation tool (project)
 - Finnish disease database (FINDIS) (project)
 - Bacterial microarrays database (MOLGEN-IS) (publication)
 - Human Metabolic Pathway Database (project, publication)

If you think your project should be listed (differently) please let us know
