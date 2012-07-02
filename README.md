Advanced Application and Computation Framework
----------------------------------------------
Users can build and start an app on the commandline like this:

     git clone https://www.github.com/molgenis/molgenis.git
     git clone https://www.github.com/molgenis/molgenis_distro.git
     cd molgenis_distro
     ant <appname>.build clean-generate-compile-test
     ant <appname>.build run
     
Then browse to http://<yourhost>:8080/<appname>/

APPNAME
-------

     - xQTL       - Workbench for multi-level QTL mapping
     - DebCentral - Dystrophic Epidermolysis Bullosa mutation database
     - XGAP       - eXtensible Genotype and Phenotype database
     - designGG   - Design of Genetical Genomics Experiments
     - BBMRI      - BBMRI-NL biobank catalague
     - MAGETAB    - Microarray Gene experiment object model
     - AnimalDB   - Animal observation database
     - PhenoOM    - Phenotype observation model
     - MRB        - Mouse Resource Browser
     - FINDIS     - Findis Finnish disease database
     - NORDIC     - Nordic GWAS control database
     - GWASCHK    - GWAS Central curation tool
     - HMPD       - Human Metabolic Pathway Database

About
-----
[MOLGENIS](http://www.molgenis.org/ "Molgenis.org - a collaborative open source project") is a collaborative open 
source project on a mission to generate great software infrastructure for life science research. Each app in the
[MOLGENIS](http://www.molgenis.org/ "Molgenis.org - a collaborative open source project") family comes with rich 
data management interface and plug-in integration of analysis tools in R, Java and Web services.

For more information visit: [MOLGENIS](http://www.molgenis.org/ "Molgenis.org - a collaborative open source project")
