MOLGENIS Apps
--------
MOLGENIS apps is an collaborative open source project build on the MOLGENIS generator platform on a mission to generate great 
software infrastructure for life science research. Each app in the MOLGENIS 
family comes with rich data management interface and plug-in integration of 
analysis tools in R, Java and web services.

Generating and running the apps:
-------------------
Generate your app in two steps:

1) Clone the MOLGENIS generator and MOLGENIS 'apps' suite

     git clone https://www.github.com/molgenis/molgenis.git
     git clone https://www.github.com/molgenis/molgenis_apps.git
     cd molgenis_apps


2) Use one of the build scripts to generate your app
	ant -f <app>_build.xml <command>
	
	e.g.
	ant -f xqtl_build.xml clean-generate-compile-run
	
	commands:
	clean = remove the generated code
	generate = generate the code
	compile = compile the code (needs generate)
	run	= run on standalone server
	update-eclipse = update Eclipse IDE to use source folders matching your app
	clean-generate-compile-run = clean, generate, compile, run
	
About
-----
[MOLGENIS](http://www.molgenis.org/ "Molgenis.org - a collaborative open source project") is a collaborative open 
source project on a mission to generate great software infrastructure for life science research. Each app in the
[MOLGENIS](http://www.molgenis.org/ "Molgenis.org - a collaborative open source project") family comes with rich 
data management interface and plug-in integration of analysis tools in R, Java and Web services.

For more information visit: [MOLGENIS](http://www.molgenis.org/ "Molgenis.org - a collaborative open source project")
=======
Editing the apps
---------------------

Each app has its root in apps/name, e.g. apps/animaldb.
Inside there is typically a package with the same name, e.g. apps/animaldb/org/molgenis/animaldb

Edit
animaldb_ui.xml to change user interface elements
animaldb.properties to change database settings

Editing the modules
---------------------

MOLGENIS apps have modules shared between them. These are in modules/name, e.g., molgenis/datamodel
Typical packages include 'datamodel' (containing xml files describing the data models) and 'auth' enabling login.

MOLGENIS applications in this package
---------------------
Many molgenis applications have been developed, some included are:

 - xQTL Workbench for multi-level QTL mapping ([project](h	ttp://www.xqtl.nl/ "www.xqtl.nl"))
 - Dystrophic Epidermolysis Bullosa (deb-central) mutation database ([project](http://www.deb-central.org/ "www.deb-central.org/"), publication)
 - Design of Genetical Genomics Experiments (designGG)] ([project](http://gbic.biol.rug.nl/designGG "DesignGG"), publication)
 - BBMRI-NL biobank catalague ([project](http://www.phenoflow.org/wiki/BiobankCatalog "BBMRI")) 
 - MAGE-TAB microarray gene experiment object model (MAGETAB-OM)] ([project](http://www.phenoflow.org/wiki/PhenoFlow "MAGETAB"),  demo)
 - Pheno-OM Phenotype observation model ([project](http://www.phenoflow.org/wiki/PhenoFlow "Pheno-OM"), demo)
 - Animal observation database (AnimalDB) ([project](http://www.animaldb.org/ "www.animaldb.org"))
 
If you think your project should be listed (differently) please let us know
