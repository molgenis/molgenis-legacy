Discussion items:

# Identifiers
We need:
* internal id number (automatic database id, hidden)
* externally stable identifier

Proposed solution: have internalId(autoid,hidden) and identifier(unique,required)

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
