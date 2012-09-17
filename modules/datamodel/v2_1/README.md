Discussion items:

# data model thoughts
We need:
* flexible 'name,value,type' to attach at runtime additional columns
* explain how sublclass of Target/Feature can map to Protocol/ProtocolApplication

# Addressbook
We need:
* a place to store researcher information such as Person and Institute and Project
* 1 Person can be a member of many institutes or projetc
* we have a primary Institute? And affliated (=reference to role + institute)
* Institute has name, department, 
* ORCID references – Ontology Term?
* Ontology terms for title
* Add people here to enable them to be assigned to a study
* We will have option to copy this information locally (for search) but will also link to ORCID (if it gets live)

Proposed solution: take the ORCID researcher-profile model, but remove details on start-end. 
If possible link to external ORCID records.

# Species
We need:
* Each species connected to ontology term
* Each species linked to many GenomeBuild
* Each GenomeBuild has Chromosome
* Use HGVS notation to name chromosome incl genome build; we will assign a Identifier + Accession to external
* Using mutilizer to create the names?


# Ontology
We need:
* E.g. MESH, uri to MESH website. Each term we use will be copied to OntologTerm
* Also introduce 'local' terms such as GWAS central ontology, e.g. 'AssayedPanel'
* We would treat our 'own' defined terms just the same as external
* So: ontology identifier + term identifier + some term name. Example: "MeSH: Bipolar Disorder (D001714)"
* Each term also has a definition
* If the term is from external we want hyperlink to the source

Proposed solution: OntologyTerm(ontology identifier, accession, name, definition)

# Study Details:
We need:
* some validation on start and end dates? E.g embargos?
* need decorator to validate that 'end' is after 'start'
* Accession – should be the id of the study in another resource / paper?

# Citations:
We need:
* to capture publications AND other resources => should these be in the same table??
* Look up on citation table instead of identifier, for long run need to "table lookup", for short: identifier (default: pmid:1234), title, authors
* Identifier – should this just be autoid?
* Publication status – ontology or database options

Proposed solution: we give Citations their own identifier which may or may not be the same as the pmid; we will check for duplicate pmids.

# Samplepanel 
* Ethnicity / Geographical location – ontology lookup and if these don’t fit then free text?
* Rkh: remove duplicate number of individuals
* Is there a way to represent age range and year range? Could solve using categories (= each range = OntologyTerm)
* Source of DNA – Ontology or drop list

Proposed solution: create subclass of Panel that has 'hardcoded' columns to fix what information is collected per panel.
Try: can we implement this using ProtocolApplication subclass {target=panel, each column is a measurement}

# Range
We need:
* store ranges, e.g. age of diagnosis = 40 -45

Proposed solution: short term: include min and max fields; for long term introduce a 'range' data type on Measurement.

# License
We need:
* enable people to share their meta data and data under license that clarifies use
* we could use http://www.openarchives.org/OAI/openarchivesprotocol.html
* this details which resources one can access under what agreements; this links into the 'permission' model

# Federation
We need:
* search accross omicsconnect systems

Proposed solution: can we use the new biomart to do the federated search?

# Study Samplepanels: 
We need to:
* link sample panels submitted in one study
* many-to-many relation
* so I can see in what study this panel was used, or vice versa
* want to keep sample panel table because additional properties, countFemale, countMale, geography
* In study I want to see the panels and have 'link existing' or 'add new' next to it.

Proposed solution: model SamplePanel extends Panel with the additional properties.

# PhenotypeValue table
We need:
* table with as rows the 'Method' and as columns 'Average, SD, etc'.
* translated to Observ-OM this means 'Methods' == Target and 'Average' == Feature
* One of the Feature is link to the Panel this applies to (and optionally the data set)
* Also one can make subclasses of ProtocolApplication to hardcode these columns, such as in PhenotypeValue table

Proposed solution: create PhenotypeValue as subclass of protocolApplication (targetType=Method) to test this solution.

# Protocols:
Where can I add details about the method – variable measured, units etc? – phenotype value?
* You can find all details in Measurement.

# Protocols cannot be changed after they are used!
Otherwise data sets will break.

# DataSet
We need:
* DataSet links to Protocol
* DataSet is a uniform system for one row and multi row datasets
* Therefore the protocolApplication is dropped and the protocol reference is pulled up to DataSet
* Remainder of ProtocolApplication is renamed to 'row'.
* In the user interface we hide this.
* Actually: should become a component like 'EntityTuple', mixture of Entity (hardcode columns) and Tuple (which is dynamic columns)
* We need 'DataRow' to store each row.
* DataTable implemements 'previousSteps"
* Experiment = bundle of links to Protocols used, the DataSets used and the Panels and Phenotypes

Significances, subclass of DataSet
Effectsize, subclass of DataSet
Frequencies??, subclass of DataSet

# unique rows in data set?
We need:
* non unique rows in DataSet
* should be flagged as this influences R

# Markers:
Need work – datasource – dbsnp – should be a hotlink?
Should I record genome build here?
Should we be recording alleles here?

We will NOT allow coordinates against multiple builds. 
We want a lift over tool to allow people to work with multiple

Coordinates
xref to genome and chromosome?
Duplicate ontology term

Allele

Genotype
Problem with this table

Marker: Add validation for flanking sequence
Remove Ontology and or feature type
Validation code should be mref – validation codes can be defined in the ontologyTerms?
Remove Label?

Allele : should we remove Genome build YES

Genome Build: needed to specify coordinate of feature on chr

Questions:
How can I get molgenis to auto assign ids?
How do I use commands?
Questions about freemarker: how to I add to the model to generate on the fly data in the template
Ontology adding – bioportal widget? Or Ontocat?
Pubmed lookup / Autolook ups - how
We need to look at links – so called hotlinks in GWAS Central (url prefix, suffix)
Citation is subclass of hotlink
Would like to see how sequencing will fit into model – Just Variants and links to sequencing files? Is this a good example
Do we want to store Gene features in a feature database for reference (during analysis), e.g. like a GFF, DAS consume
We don't want to add each study by hand

Browser – Jbrowse, webappollo  - curation – where would these curaions be stored.

SNV of 10 genomes – use as an example sequencing experiment
http://www.sequenceontology.org/resources/10Gen.html
Sequencing of healthy individuals

If the data you are looking for does not need to include effected individuals, Complete Genomics has sequenced a 3 generation 17 member family (CEPH). Files here:
ftp://ftp2.completegenomics.com/Pedigree_1463/ASM_Build37_2.0.0


Personal public exome

http://manuelcorpas.com/2012/01/23/my-personal-exome-now-publicly-released/
http://blog.personalgenomes.org/2012/05/03/pgp18-a-23andme-exome/


Should we try a model a VCF file?

We want an overiew of all plugins.

Future:
Analysis tools – what types of analysis would you perform on Individual level data? (Data Sheild?) Galaxy style tools for both individual and summary-level tools
Should we have an administration page/ wizard to set up the data base and ui with all the modules you need?
Biomart – could this be a way to link different omics connect platforms in the future – could become the public face of the labs datasharing ?

Types of data
•	Subjects
•	Anonymous individuals
•	Family history
•	Age
•	Sex
•	Ethnicity
•	Pools of people
•	Environmental data
•	Location (zip code would be great)
•	Medications they are taking
•	Exercise, nutrition.
•	Genotype info
•	Microarray based
•	SNPs
•	Haplotype blocks
•	Copy number polymorphism
•	Sequence based
•	Random reads
•	PCR products
•	larger clones
•	single haplotype vs. diploid
•	Phenotype
•	Disease presence/absence or severity
•	ADR - Adverse Drug Reaction
•	Single physiological measure
•	Enzyme activity, measure of amount of substance
•	Parallel Measures
•	Microarray measurements, etc...


# Identifiers
We need:
* internal id number (automatic database id, hidden), using AutoId superclass
* externally stable identifier (we assigned)
* optionally also accessions (identifiers others assigned and which we can use to link)
* for data import/export to work we always need a secondary key (so if not Identifier, then a 'name' or ...)
* Are there rules about accessions? dbsnp:rs1234, pmid:123456, 
* Do we need multiple accessions


Proposed solution: have internalId(autoid,hidden) and identifier(unique,required). We follow LSID pattern of <authority><id> e.g. HGVM849163, dbSNP:rs1005511
Next to that have an 'Accession' table which looks very similar to OntologyTerm, having 'sourceID' + ' accession', e.g {dbsnp,rs1234}

# Investigation
We need:
* link to protocols (e.g. affymetrix array platform), panels (e.g. assayed panel), data sets, annotation sets (e.g. phenotypes, markers)
* we want use of investigation to be optional

Proposed solution: similar to 'role' only use references to these resources (instead of other way around, as in current solution).

# AlternativeID
We need
* able to add multiple external ids to an object

Proposed solution: use LSID type of reference (so merge with HotLink)

# Annotation sets
We need:
* ways to group annotations, for example all probes of one array
* ways to set permissions on this whole group (edit, view, admin)
* annotation sets may be used by multiple studies
* Discussion: is this the same as a protocol??? Or do we need something special, analogous to Panel?

Proposed solution: introduce new type 'AnnotationSet'? Or can we use Protocol for a group of features? Or both?	

# Parameterizable Measurement
We need:
* details on 'ProbeSet', 'Marker', etc (currently exists)
* definition on the role of such annotation inside a Measurement (doesn't exist)
* E.g. ProtocolApplication = { Target="Patient1", Genotype(Marker=rs1234) = AB,  Genotype(Marker=rs1235) = BB), ...}

Proposed solution: 

# Set of protocolApplication aka Matrix
We need:
* group multiple ProtocolApplication together 
ways to represent Target * Feature 
Proposed solution: 

Organization of data sets into studies. We need:

# Data files. 
We need a method to link to data files.
* Files have an md5 to check correctness
* Files may have more than one physical replicates
* Files may need annotations of some sort.

Proposed implementation: use current MolgenisFile structure.

# Free form lookup labels
We need:
* custom lookup labels: xref_label="${name} (${identifier})"
* currently we can only do: xref_label="identifier,name"

Proposed solution: add the 'nice' lookup labels using freemarker snippet

#Hotlink
We need to:
* link to external objects
* link to internal objects

Proposed solution: use LSID hyperlinks which go internal and external (using identifier resolution).
Whole hyperlinks is a special case. Also literature references is a special case?

#Panels
We need to
* create a set of individuals as panel
* inclusion/exclusion criteria (which is a filter statement of feature, value, operator)
* number of individuals in the set (if individuals not known)

Proposed solution: use 'protocolApplication' to attach properties to a panel?

#Contributions
We need to:
* Trace contributions (= role of a person on an object, e.g. submitter, reviewer, technician, coordinator, principle investigator)
* Types of objects are: Studies, Protocol, ProtocolApplication, DataSets (= set protocolApplication)
* (annotation sets?)

Contributions: 
use ontology or options?

Proposed solutions: Contributions have a link to 

#Citations
We need to:
* list citations 

Proposed solutions:

#Tags/annotations/ontology terms
We need to:
* link any feature to an ontology:term
* hyperlink to a description

#Protocol
We need to:
* store definitions of platforms, lab protocols, computation protocols

Proposed solution: keep current Protocol statement

#Sharing permissions
We need to:
* indicate read/write permissions on objects
* not have to set permissions on every little object
* we don't want permission to change the data model

Proposed solutions: make permission structure cleanly inherit. E.g. DataSet inherits from Investigation, unless DataSet has its own permission set attached to it.

#Versioning
For now we decide not to do versioning. Users can use identifiers themselves to version.
