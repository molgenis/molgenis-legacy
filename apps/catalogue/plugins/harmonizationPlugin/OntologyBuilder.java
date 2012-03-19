package plugins.harmonizationPlugin;

/*
 * The ontologybuilder.java is used to build Coirell cell line ontology automatcially.
 * Input three mapping files: celltypeAndAnatomy100%.txt, Organism100%Similarity.txt,
 * disease100%Similarity.txt
 * Input reference ontologies: DO, EFO, NCBI Taxonomy.
 * Input spreadsheet: Spreadsheet.txt
 *        
 * There are two main steps here. 
 * The first one is to get all the classes from reference ontology. We import cell type and
 * anatomical terms from EFO; import disease terms from Human Disease Ontology and get disease 
 * relationships (super class restriction) from EFO by searching for the Disease ontology ID.
 * For the organism, we import terms from NCBI Taxonomy ontology. 
 * Before the second step starts, we have to add some missing classes manually. For example, 
 * there were 11 anatomical terms that were not mapped to EFO so we manually map those in Bioportal.
 * If the terms cannot be found in Bioportal either, one can create this class concept himself.
 * This work has to be done before adding cell line restriction otherwise some of the cell lines
 * would have broken pattern due to the missing information. 
 * 
 * The second step is to assign cell line nested superclass restriction. The only input is 
 * Spreadsheet.txt which contains all the information about each cell line. The program call
 * make nested-expression and give it to each cell line.
 * 
 * @author Chao Pang
 * @email ChaoPang229@gmail.com
 */


import org.semanticweb.HermiT.Reasoner.ReasonerFactory;
import org.semanticweb.owlapi.util.OWLEntityRemover;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.reasoner.*; 
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.semanticweb.owlapi.model.*;


/**
 *
 * @author Chao Pang
 */
public class OntologyBuilder {
    
    private OWLOntologyManager manager;
    private OWLOntology ontology,saveOntology;
    private OWLDataFactory factory;
    private String prefix;
    private HashMap referenceOntologyURIConvert, chaoClassURIConvert;
    private HashMap mapURI,DOIDConvertClass, DiseaseOntologyURIConvert;
    private String ontologyURI;
    private ReasonerFactory reasonerFactory;
    private OWLReasoner reasoner;
    //These four integers are used to indicate the index of different element 
    //When adding restriction to Cell Line
    private int CellType, Anatomy, Disease,DiseaseTwo,Judgement, Organism, description;
    
    public OntologyBuilder(String base){
        manager = OWLManager.createOWLOntologyManager();
        factory = manager.getOWLDataFactory();
        prefix = base;
    }
    
    
    /*
     * This method is used to save ontology
     * 
     * 
     */ 
    public void OntologySave ( ) throws OWLOntologyStorageException{
        manager.saveOntology(saveOntology);
    }
    /*
     * This method is used to save ontology in a new file
     * 
     * 
     */ 
    public void OntologySave (File savefile) throws OWLOntologyStorageException{
        manager.saveOntology(saveOntology, IRI.create(savefile));
    }
    
    

    /*
     * This method is used to load reference ontology and our own ontology. 
     * 
     * @param              referenceOntology
     * @param              buildingOntology
     */ 
    public void setOntology(String referenceOntology, String buildingOntology) throws OWLOntologyCreationException{
        
        File file = new File(referenceOntology);
        File saveFile = new File(buildingOntology);        
        //IRI efoIRI = IRI.create(referenceOntology);
        //ontology = manager.loadOntology(efoIRI); 
        ontology = manager.loadOntologyFromOntologyDocument(file);
        saveOntology = manager.loadOntologyFromOntologyDocument(saveFile);
        referenceOntologyURIConvert = labelMapURI(ontology);
        chaoClassURIConvert = labelMapURI(saveOntology);
        reasonerFactory = new Reasoner.ReasonerFactory();
        reasoner = reasonerFactory.createReasoner(saveOntology);
        DiseaseOntologyURIConvert = labelMapURI(ontology);
    }//end of setOntology method
    
    /*
     * This method is used to load mapping result file. And find pass the class to getAllClass method 
     * to get all the parent classes. 
     * 
     * @param               MappintResult is the path of mapping result file.
     */ 
    public void LoadMappingResult(String MappingResult) throws FileNotFoundException, IOException, OWLOntologyStorageException{
        
        ReasonerFactory reasonerfactory;
        
        OWLReasoner reasone;
        
        try{
            reasonerfactory = new Reasoner.ReasonerFactory();
            reasone = reasonerfactory.createReasoner(ontology);
        }catch(Exception e){
            reasone = null;
        }
        FileInputStream mappingFileStream = new FileInputStream(MappingResult);

        DataInputStream in = new DataInputStream(mappingFileStream);
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        
        String eachLine;
        
        OWLEntityRemover remover = new OWLEntityRemover(manager, Collections.singleton(saveOntology));
                
        while((eachLine = reader.readLine()) != null){ 
            
            String element[] = eachLine.split("\t");
            System.out.println(eachLine);
            OWLClass cls = null;
            try{
                
                cls = (OWLClass) referenceOntologyURIConvert.get(element[1]);

            }catch(Exception e){
               System.out.println(element[1]);
            }
            /*
             * delete all the classes that would be imported soon. Otherwise there would be 
             * two same classes in different branches.
             */ 
            
            try{
                OWLClass removeClass = (OWLClass) chaoClassURIConvert.get(element[1]);
                if(!(removeClass.getIRI().equals(cls.getIRI()))){
                    //System.out.println(element[1]);
                    for (OWLAxiom ax : saveOntology.getReferencingAxioms(removeClass)) {
                            RemoveAxiom remove = new RemoveAxiom(saveOntology, ax);
                            manager.applyChange(remove);       
                    }
                    removeClass.accept(remover);
                    manager.applyChanges(remover.getChanges());
                    //System.out.println("The class "+ element[1] + " The IRI is " + removeClass.getIRI());
                    //System.out.println("The class "+ element[1] + " The IRI is " + cls.getIRI());
                }              
            }catch(Exception e){
                //System.out.println(e.getMessage());
            }
            /*
             * get class expression and pass them to getAllClass
             * get equivelent restriction for the imported class
             */
            for (OWLSubClassOfAxiom ax : ontology.getSubClassAxiomsForSubClass(cls)) {
                 OWLClassExpression superCls = ax.getSuperClass();
                 getAllClass(superCls, cls);
            }
            if(reasone != null){
                NodeSet<OWLClass> node = reasone.getSuperClasses(cls, true);
                if(node.isTopSingleton()){
                    OWLClass unspecified = factory.getOWLClass(IRI.create(prefix + "unspecified"));
                    addLabel(unspecified, "unspecified");
                    getAllClass(unspecified, cls);
                }
            }
        }
        removeSuperClass(saveOntology);
        
    }//end of ChangeMappingResult method.
    
    /*
     * This method is used to add subClass restriction to ontology.
     * 
     * @param              SuperClass is the OWLClass getting from reference ontology
     * @param              SubClass   is the OWLClass getting from reference ontology
     */ 
    public void addSuperClassRestriction (OWLClass SuperClass, OWLClass SubClass) throws IOException{
        
        String SublabelValue = getLabel(SubClass, ontology);
        String SuperlabelValue = getLabel(SuperClass, ontology);
        OWLSubClassOfAxiom ax = factory.getOWLSubClassOfAxiom(SubClass, SuperClass);
        
        AddAxiom addAx = new AddAxiom(saveOntology, ax);
        manager.applyChange(addAx);
        
        if(getLabel(SubClass, saveOntology).equals("")){
            addLabel(SubClass, SublabelValue);
        }
        if(getLabel(SuperClass, saveOntology).equals("")){
            addLabel(SuperClass, SuperlabelValue);
        }
        if( !chaoClassURIConvert.containsKey(SublabelValue)){
            chaoClassURIConvert.put(SublabelValue,SubClass);
        }
        if( !chaoClassURIConvert.containsKey(SuperlabelValue)){
            chaoClassURIConvert.put(SuperlabelValue,SuperClass);
        }
    }//end of addSuperClassRestriction method
    /*
     * This is method is used to add label value to our ontology
     *
     * @param       cls is the OWLEntity(such OWLClass or OWLObjectProperty) that we want to give label
     * @parm        labelValue is the label name
     */  
    public void addLabel(OWLEntity cls, String labelValue){
        
        OWLAnnotationProperty label = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
        OWLAnnotation addLabel = factory.getOWLAnnotation(label, factory.getOWLLiteral(labelValue));
        OWLAxiom ax = factory.getOWLAnnotationAssertionAxiom(cls.getIRI(), addLabel);
        manager.applyChange(new AddAxiom(saveOntology,ax)); 
    }//end of addLabel method
    
    /*
     * This method is used to add disjoint restriction to the classes that are imported
     * from other ontologies. It would firstly get the resctriction from EFO or other 
     * reference ontologies. If the disjoint classes are existing in our building ontology,
     * the restriction will be added. If the classes are not existing, it would create the
     * class first and add restriction after.
     * 
     * @param      cls is the class we want to give disjoint restriction
     */ 
    public void addDisjointClass(OWLClass cls){
        
        for(OWLDisjointClassesAxiom ax : ontology.getDisjointClassesAxioms(cls)){
            //System.out.println(getLabel(cls, ontology));
            for(OWLClass expression : ax.getClassesInSignature()){
                for (OWLSubClassOfAxiom axiom : ontology.getSubClassAxiomsForSubClass(expression)) {
                    OWLClassExpression superCls = axiom.getSuperClass();
                    if(!cls.equals(expression) && !expression.equals(superCls)){
                        getAllClass(superCls, expression);
                        //System.out.println(getLabel(expression, ontology));
                        
                    }   
                }
            }
            AddAxiom addAx = new AddAxiom(saveOntology, ax);
            manager.applyChange(addAx);
        }
    }//end of addDisjointClass method
    
    /*
     * This method is used to give Equivelent class restriction to defined classes that
     * we import from other ontologies. It will simply get EquivelentClass axiom from EFO
     * or onther reference ontologies, if all the classes in axiom are exising, it will 
     * add this axiom to our ontology. If the class are not existing in our ontology, it
     * will create the class and then the axiom will be added.
     * 
     * 
     * @param the    cls is the class we want to give defined class restriction
     */ 
    public void addEquivalentClass(OWLClass cls){
        //System.out.println(getLabel(cls, ontology));
        OWLClass SuperClass = null;
        for (OWLEquivalentClassesAxiom ax : ontology.getEquivalentClassesAxioms(cls)) {  
            for(OWLClassExpression expression : ax.getClassExpressions()){
                for(OWLClass owlClass : expression.getClassesInSignature()){
                     SuperClass = owlClass;
                }
                if(!SuperClass.equals(cls)){
                    for (OWLSubClassOfAxiom axiom : ontology.getSubClassAxiomsForSubClass(SuperClass)) {
                         OWLClassExpression superCls = axiom.getSuperClass();
                         getAllClass(superCls, SuperClass); 
                    }
                }
            }
            AddAxiom addAx = new AddAxiom(saveOntology, ax);
            manager.applyChange(addAx);
        }
    }//end of addEquivalentClass method
    
    /*
     * This method is used to give annoatation to the classes that we import from other
     * ontologies. As long as the class is imported, the corresponding annotation will
     * be collected from reference ontology and added into our building ontology
     * 
     * 
     * @param  cls is the imported class that we want to give annotation
     */ 
    public void addAnnotation(OWLClass cls){
        
        String label = getLabel(cls, ontology);
        String suffix = label.replace(" ", "_");
        OWLAnnotationProperty labelProperty = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
        OWLAnnotationProperty URL = factory.getOWLAnnotationProperty(IRI.create(prefix + "URL"));
        OWLAnnotationValue value = factory.getOWLLiteral(prefix + suffix);
        OWLAnnotation owlAnnotation = factory.getOWLAnnotation(URL, value);
        OWLAxiom ax = factory.getOWLAnnotationAssertionAxiom(cls.getIRI(), owlAnnotation);
        manager.applyChange(new AddAxiom(saveOntology, ax)); 
        for(OWLAnnotation annotation : cls.getAnnotations(ontology)){
            ax = factory.getOWLAnnotationAssertionAxiom(cls.getIRI(), annotation);
            manager.applyChange(new AddAxiom(saveOntology, ax));
        }
    }//end of addAnnotation method
    
    /*
     * This method is used to give annoatation to the classes that we import from other
     * ontologies. The different from addAnnotation method is that we don`t import class
     * label as a label in building ontology but import as a new synonymous in the ontology. 
     * In some cases, the class in our ontology has a label already so we don`t have to import
     * the class label.
     * 
     * @param  cls is the imported class that we want to give annotation
     */ 
    public void addAnnotationExceptLabel(OWLClass cls){
        
        String label = getLabel(cls, ontology);
        String suffix = label.replace(" ", "_");
        OWLAnnotationProperty labelProperty = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
        OWLAnnotationProperty synonym = factory.getOWLAnnotationProperty(IRI.create("http://www.ebi.ac.uk/cellline#Synonyms"));
        OWLAnnotationProperty URL = factory.getOWLAnnotationProperty(IRI.create(prefix + "URL"));
        OWLAnnotationValue value = factory.getOWLLiteral(prefix + suffix);
        OWLAnnotation owlAnnotation = factory.getOWLAnnotation(URL, value);
        OWLAxiom ax = factory.getOWLAnnotationAssertionAxiom(cls.getIRI(), owlAnnotation);
        manager.applyChange(new AddAxiom(saveOntology, ax)); 
        for(OWLAnnotation annotation : cls.getAnnotations(ontology)){
            if(!annotation.getProperty().equals(labelProperty)){
                ax = factory.getOWLAnnotationAssertionAxiom(cls.getIRI(), annotation);
                manager.applyChange(new AddAxiom(saveOntology, ax));
            }else{
                OWLLiteral Synonyms = factory.getOWLLiteral(label);
                annotation = factory.getOWLAnnotation(synonym, Synonyms);
                ax = factory.getOWLAnnotationAssertionAxiom(cls.getIRI(), annotation);
                manager.applyChange(new AddAxiom(saveOntology, ax));
            }
        }
    }//end of addAnnotation method
    /*
     * This method is used to add class restriction to the imported class. When the class
     * is imported, its class expressions are imported as well. Therefore it would get
     * class expression from EFO or reference ontology and add them into our ontology
     * 
     * 
     * @param  SubClass is the imported class that we add class expression
     * @param  hasProperty is the class expression that we collect from reference ontologies
     */ 
    public void addClassRestriction(OWLClass SubClass, OWLClassExpression hasProperty){   
        
        for( OWLObjectProperty Property: hasProperty.getObjectPropertiesInSignature()){
            
            addLabel(Property, getLabel(Property, ontology));
        }
        OWLSubClassOfAxiom ax = factory.getOWLSubClassOfAxiom(SubClass, hasProperty);
        AddAxiom addAx = new AddAxiom(saveOntology, ax);
        manager.applyChange(addAx);
    }//end of addClassRestriction
    
    /*
     * This method is used to collect all the parent classes by using recursive call.
     * Once this method is called for one specific class, all the relationship about
     * this class would be found in EFO or reference ontologies, including parent class
     * and anonymous class(superclass expression). Apart from that, if some classes in 
     * anonymous class expression are not existing in our ontology. It will create these
     * classes first and then add the anonymous class restriction afterwards. 
     * 
     * @param   desc are OWLClassExpressions for imported classes
     * @parm    cls is the imported class from reference ontologies.
     */ 
    public void getAllClass(OWLClassExpression desc, OWLClass cls){
        
        //give annotation to the imported class
        addAnnotation(cls);
        //To check whether the class expression is anonymous class. If it`s not, that means this expression
        //is the parent class. 
        if (!desc.isAnonymous()) {
            //change expression to OWLClass
            OWLClass SuperClass = desc.asOWLClass();
            addEquivalentClass(SuperClass);
            addEquivalentClass(cls);
            /*
             * This part is to give a range of the search. It means that when the searching meets one of following 
             * four classes and then it would stop the recursive call. As the information we want are anatomy, disease
             * cellType and organism, there`s no need to get more extra information. It only collects all the parent 
             * classes of under these four classes and fit thess "tree structure" into our ontology.
             * 
             */ 
            OWLClass anatomy = factory.getOWLClass(IRI.create("http://www.ebi.ac.uk/efo/EFO_0000787"));
            OWLClass disease = factory.getOWLClass(IRI.create("http://www.ebi.ac.uk/efo/EFO_0000408"));
            OWLClass cellType = factory.getOWLClass(IRI.create("http://www.ebi.ac.uk/efo/EFO_0000324"));
            OWLClass organism = factory.getOWLClass(IRI.create("http://purl.org/obo/owlapi/ncbi_taxonomy#NCBITaxon_32523"));
            
            /*
             * Multiple if-else statement implements the range searching. When the parent class is equal to
             * any of them(e.g. anatomy, organism). It would stop the recursive call and place the information
             * in our ontology.
             */ 
            
            try {
                if(SuperClass.equals(anatomy)){
                    try {
                        addSuperClassRestriction((OWLClass) chaoClassURIConvert.get("organism part"), cls);
                    } catch (IOException ex) {
                        Logger.getLogger(OntologyBuilder.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }else if(SuperClass.equals(disease)){
                    try {
                        addSuperClassRestriction((OWLClass) chaoClassURIConvert.get("disease"), cls);
                        
                    } catch (IOException ex) {
                        Logger.getLogger(OntologyBuilder.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }else if(SuperClass.equals(cellType)){
                    try {
                        addSuperClassRestriction((OWLClass) chaoClassURIConvert.get("cell type"), cls);
                    } catch (IOException ex) {
                        Logger.getLogger(OntologyBuilder.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }else if(SuperClass.equals(organism)){
                    try {
                        addSuperClassRestriction((OWLClass) chaoClassURIConvert.get("organism"), cls);
                    } catch (IOException ex) {
                        Logger.getLogger(OntologyBuilder.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }else{
                    
                    /*
                     * The last else statement is used to find the next parent class by using recursive call.
                     * At same time, it calls addEquivalentClass to check the defined class axiom.
                     */ 
                    addSuperClassRestriction(SuperClass, cls);
                    for (OWLSubClassOfAxiom ax : ontology.getSubClassAxiomsForSubClass(SuperClass)) {
                        OWLClassExpression superCls = ax.getSuperClass();
                        getAllClass(superCls, SuperClass);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(OntologyBuilder.class.getName()).log(Level.SEVERE, null, ex);
            }
        /*
         * If the Class expression is anonymous class. Then it will check whether our ontology contains
         * all the classes in the expression. If it`s not, it would create the class first and then get
         * class axiom to add into our ontology.
         */     
        }else{
            
            for(OWLClass owlClass : desc.getClassesInSignature()){
                
                for (OWLSubClassOfAxiom ax : ontology.getSubClassAxiomsForSubClass(owlClass)) {
                    OWLClassExpression superCls = ax.getSuperClass();
                    getAllClass(superCls, owlClass);    
                }
                addEquivalentClass(owlClass);
            }
            addEquivalentClass(cls);
            addClassRestriction(cls, desc);
        }    
    }//end of getAllClass method

    /*
     * The method is used to remove the wrong superClass axiom from some of the classes,
     * what I mean by wrong superClass axiom is that during the adding class process some 
     * of the classes may add themselves as superclasses, such as disease is subClass of disease.
     * We have to remove this kind of wrong axioms. 
     * 
     * @param        ontology is OWLOntology that we want remove the wrong super class
     */ 
    public void removeSuperClass(OWLOntology ontology){
        
        for(OWLClass cls : ontology.getClassesInSignature()){
            for(OWLSubClassOfAxiom axiom : ontology.getSubClassAxiomsForSubClass(cls)){
                OWLClassExpression supercls = axiom.getSuperClass();
                if(!supercls.isAnonymous()){
                    if(cls.equals(supercls.asOWLClass())){
                        RemoveAxiom remove = new RemoveAxiom(saveOntology, axiom);
                        manager.applyChange(remove);
                    }
                } 
            }
        }    
    }//end of removeSuperClass
    
    /*
     * This method is used to create hashmap store URI and label of classes. The label is taken as key
     * and URI as value so we can get class URI by class label
     *
     * @param      owlontology is the ontology that we want to create the hashmap
     * @return     mapURI is the hashmap 
     *  
     */
    public HashMap labelMapURI(OWLOntology owlontology){
        HashMap mapURI = new HashMap();
        OWLAnnotationProperty label = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
            for (OWLClass cls : owlontology.getClassesInSignature()) {
                // Get the annotations on the class that use the label property
                for (OWLAnnotation annotation : cls.getAnnotations(owlontology, label)) {
                    if (annotation.getValue() instanceof OWLLiteral) {
                        OWLLiteral val = (OWLLiteral) annotation.getValue();
                        String labelString = val.getLiteral();
                        mapURI.put(labelString, cls);
                    }
                }
            }
        return mapURI;
    }//end of labelMapURI method
    
    /*
     * This method is used to get a label of corresponding OWLClass. 
     * @param      cls is the class we want to get label 
     * @return     the label of the class
     */ 
    public String getLabel(OWLEntity cls, OWLOntology owlontology){
        String labelValue = "";
        try{
            OWLAnnotationProperty label = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
            for (OWLAnnotation annotation : cls.getAnnotations(owlontology, label)) {
                if (annotation.getValue() instanceof OWLLiteral) {
                    OWLLiteral val = (OWLLiteral) annotation.getValue();
                    labelValue = val.getLiteral().toString();
                }      
            }       
        }catch(Exception e){
            System.out.println("The annotation is null!");
        }
        return labelValue;
    }//end of the getLabel method
    
    //---------------------------cell line restriction methods------------------------
    
    /*
     * This method is used to add annotation to each cell line. 
     * 
     * @param filePath           the file path contains all cell line with corresponding OMIM numbers
     */ 
    public void addAnnotationToCellLine(String filePath) throws IOException, OWLOntologyStorageException{
        
        FileInputStream spreadsheet = new FileInputStream(filePath);
        DataInputStream in = new DataInputStream(spreadsheet);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String eachLine;
        int number = 0;
        while((eachLine = reader.readLine()) != null){
            String splitarray[]= eachLine.split("\t");
            number = splitarray.length;
            OWLClass CellLineClass = (OWLClass) chaoClassURIConvert.get(splitarray[0]);
            for(int i = 1; i < number ; i++ ){
                try{
                    String sampleDescription = splitarray[i];
                    OWLAnnotationProperty annotationProperty = factory.getOWLAnnotationProperty(IRI.create("http://www.ebi.ac.uk/cellline/OMIM"));
                    OWLLiteral value = factory.getOWLLiteral(sampleDescription);
                    OWLAnnotation annotation = factory.getOWLAnnotation(annotationProperty, value);
                    OWLAxiom axiom = factory.getOWLAnnotationAssertionAxiom(CellLineClass.getIRI(), annotation);
                    manager.applyChange(new AddAxiom(saveOntology, axiom)); 
                }catch(Exception e){
                    System.out.println(splitarray[0]);
                }
            }
        }
        manager.saveOntology(saveOntology);
    }//end of addAnnotationToCellLine method
    /*
     * This method is used to read in spreadsheet.
     * 
     * @param        fileAddress is the file path on the computer
     */
    public void readInSpreadSheet(String fileAddress) throws IOException{
        
        FileInputStream spreadsheet = new FileInputStream(fileAddress);
        DataInputStream in = new DataInputStream(spreadsheet);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String eachLine;
        int iteratation = 0;
        
        mapURI = labelMapURI(saveOntology);
        
        while((eachLine = reader.readLine()) != null){
            String splitarray[]= eachLine.split("\t");
            
            if(iteratation == 0){
                
                for(int i = 1; i < 8; i++){
                    if(splitarray[i].equalsIgnoreCase("cell type")){
                        CellType = i;
                    }else if(splitarray[i].equalsIgnoreCase("organism")){
                        Organism = i;
                    }else if(splitarray[i].equalsIgnoreCase("disease")){
                        Disease = i;
                    }else if(splitarray[i].equalsIgnoreCase("diseaseTwo")){
                        DiseaseTwo = i;
                    }else if(splitarray[i].equalsIgnoreCase("judgement")){
                        Judgement = i;
                    }else if(splitarray[i].equalsIgnoreCase("sample description")){
                        description = i;
                    }else if(splitarray[i].equalsIgnoreCase("organism part")){
                        Anatomy = i;
                    }
                }
                iteratation = 100;
            }else{
                AddCellLineProperty(splitarray);
            }
        }    
    }
    /*
     * This is method is used to add restriction for Cell Line in this specific case. It uses 
     * a nested expression for cell line. This method needs to be modified in other use cases.
     * 
     * @param      []element is string array used for storing the header of spreadsheet.
     */ 
    public void AddCellLineProperty(String []element){
        OWLClassExpression expression;
        OWLSubClassOfAxiom ax;
        String cellLine = element[0];
        String celltype = element[CellType];
        String anatomy = element[Anatomy];
        String organism = element[Organism];
        
        if(chaoClassURIConvert.containsKey(element[0])){
            OWLClass CellLineClass = (OWLClass) chaoClassURIConvert.get(element[0]);
            for(OWLSubClassOfAxiom cellAxiom : saveOntology.getSubClassAxiomsForSubClass(CellLineClass)){
                OWLClassExpression cellExpression = cellAxiom.getSuperClass();
                if(cellExpression.isAnonymous()){
                    RemoveAxiom remove = new RemoveAxiom(saveOntology, cellAxiom);
                    manager.applyChange(remove);
                }
            }
        }
        OWLObjectProperty derivedFrom = factory.getOWLObjectProperty(IRI.create("http://www.obofoundry.org/ro/ro.owl#derives_from"));
        OWLObjectProperty partOf = factory.getOWLObjectProperty(IRI.create("http://www.obofoundry.org/ro/ro.owl#part_of"));
        OWLObjectProperty bearerOf = factory.getOWLObjectProperty(IRI.create(prefix + "has_pathology"));
        OWLClass ClassSet[] = new OWLClass[3];
        
        int index = 0;
        
        if(mapURI.containsKey(organism)){
            ClassSet[index] = (OWLClass) mapURI.get(organism);
            index++;
        }
        
        if(mapURI.containsKey(anatomy)){
            ClassSet[index] = (OWLClass) mapURI.get(anatomy);
            index++;
        }
        if(mapURI.containsKey(celltype)){
            ClassSet[index] = (OWLClass) mapURI.get(celltype);
            index++;
        }
        
        if(ClassSet[0] != null){
        expression = factory.getOWLObjectSomeValuesFrom(derivedFrom, ClassSet[0]);
        
        if(!getLabel(ClassSet[0], saveOntology).equals(celltype)){
            expression = factory.getOWLObjectSomeValuesFrom(partOf, ClassSet[0]);
        }else{
            expression = factory.getOWLObjectSomeValuesFrom(derivedFrom, ClassSet[0]);
        }
        System.out.println(element[0]);        
        for(int i = 1; i < index ; i++){
            if(!getLabel(ClassSet[i], saveOntology).equals(celltype)){
                expression = factory.getOWLObjectIntersectionOf(ClassSet[i], expression);
                expression = factory.getOWLObjectSomeValuesFrom(partOf, expression);
            }else{
                expression = factory.getOWLObjectIntersectionOf(ClassSet[i], expression);
                expression = factory.getOWLObjectSomeValuesFrom(derivedFrom, expression);
            }
        }
        
        ax = factory.getOWLSubClassOfAxiom((OWLClass)mapURI.get(cellLine), expression);
        manager.applyChange(new AddAxiom(saveOntology,ax));
        }
        if(element[Judgement].equalsIgnoreCase("yes")){
            //System.out.println(cellLine);
            try{
                String disease = element[Disease];
                OWLClass DISEASE = null;
                if(mapURI.containsKey(disease)){
                    DISEASE = (OWLClass) mapURI.get(disease);
                    expression = factory.getOWLObjectSomeValuesFrom(bearerOf, DISEASE);
                    ax = factory.getOWLSubClassOfAxiom((OWLClass)mapURI.get(cellLine), expression);
                    manager.applyChange(new AddAxiom(saveOntology,ax));
                }
            }catch(Exception e){

            }
            try{
                String disease = element[Disease + 1];
                OWLClass DISEASE = null;
                if(mapURI.containsKey(disease)){
                    DISEASE = (OWLClass) mapURI.get(disease);
                    expression = factory.getOWLObjectSomeValuesFrom(bearerOf, DISEASE);
                    ax = factory.getOWLSubClassOfAxiom((OWLClass)mapURI.get(cellLine), expression);
                    manager.applyChange(new AddAxiom(saveOntology,ax));
                }
            }catch(Exception e){

            }
        }
        try{
            String sampleDescription = element[description];
            OWLAnnotationProperty annotationProperty = factory.getOWLAnnotationProperty(IRI.create("http://www.ebi.ac.uk/cellline/definition"));
            OWLLiteral value = factory.getOWLLiteral(sampleDescription);
            OWLAnnotation annotation = factory.getOWLAnnotation(annotationProperty, value);
            OWLClass CellLineClass = (OWLClass) chaoClassURIConvert.get(element[0]);
            OWLAxiom axiom = factory.getOWLAnnotationAssertionAxiom(CellLineClass.getIRI(), annotation);
            manager.applyChange(new AddAxiom(saveOntology, axiom)); 
            OWLAnnotationProperty annotationEditor = factory.getOWLAnnotationProperty(IRI.create("http://www.ebi.ac.uk/cellline/definition_editor"));
            value = factory.getOWLLiteral("PangChao");
            annotation = factory.getOWLAnnotation(annotationEditor, value);
            CellLineClass = (OWLClass) chaoClassURIConvert.get(element[0]);
            axiom = factory.getOWLAnnotationAssertionAxiom(CellLineClass.getIRI(), annotation);
            manager.applyChange(new AddAxiom(saveOntology, axiom)); 
            
        }catch(Exception e){
            System.out.println(element[0]);
        }
    }
    
    
    
    //---------------------------optional methods-------------------------------------
    
    /*
     * This method is used to create a Hashmap for the URL of disease ontology.
     * 
     */
    public void DiseaseOntologyURI (String filePath) throws OWLOntologyCreationException{
        File file = new File(filePath);
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
        ontologyURI = ontology.getOntologyID().toString();
        //System.out.println(ontologyURI);
    }
    
    /*
     * This method is especially used to search DOID in the EFO class. It creates 
     * a hashmap and once the class has annotation about DOID, it would save this
     * information. The user can make different choice on whether DOID is key or
     * the class is key.
     * 
     * @param ontology                      we only use EFO
     * @param choice                        choice to decide the key in hashmap
     * 
     */ 
    public HashMap setDiseaseOntologyID(OWLOntology ontology, int choice){
        
        HashMap hash = new HashMap();
        
        OWLAnnotationProperty citation = factory.getOWLAnnotationProperty(IRI.create("http://www.ebi.ac.uk/efo/definition_citation"));
        
        for(OWLClass cls : ontology.getClassesInSignature()){
            for(OWLAnnotation annotation : cls.getAnnotations(ontology, citation)){
                OWLLiteral val = (OWLLiteral) annotation.getValue();
                Pattern pattern = Pattern.compile("DOID");
                Matcher m = pattern.matcher(val.getLiteral());
                while(m.find()){
                    if(choice == 0){
                        hash.put(val.getLiteral(), cls);
                    }else{
                        hash.put(cls, val.getLiteral());
                    }
                }
            } 
        }
        return hash;
    }//end of setDiseaseOntologyID method.
    
    
    
    /*
     * This method is used to get all the disease classes in Coriell cell line ontology.
     * It will search every disease class in EFO by DOID. If the class can be found, it
     * is replaced with the EFO disease class and therefore all the relevant restrictions
     * can be imported into Coriell cell line ontology.
     * 
     * @param className                It is disease in this case.
     * 
     */ 
    public void getAllDiseaseClass(String className) throws IOException, OWLOntologyCreationException{
        
        DOIDConvertClass = setDiseaseOntologyID(ontology,0);
        
        OWLClass disease = (OWLClass) chaoClassURIConvert.get(className);
        
        mapURI = labelMapURI(saveOntology);
        
        NodeSet<OWLClass> set = reasoner.getSubClasses(disease, false);
        
        Set<OWLClass> clses = set.getFlattened();
        
        for(OWLClass cls : clses){
            
            int classLength = cls.getIRI().toString().length();
            if(classLength > ontologyURI.length()){
                
                String ID = cls.getIRI().toString().substring(ontologyURI.length() - 1);
                String ClassID = ID.replace("_", ":");
                
                if(DOIDConvertClass.containsKey(ClassID)){
                    System.out.println(getLabel(cls, saveOntology));
                    OWLClass referenceClass = (OWLClass) DOIDConvertClass.get(ClassID);
                    getRestrictionDisease(cls, referenceClass);
                }
            }
        }
        
        set = reasoner.getSubClasses(disease, true);
        
        clses = set.getFlattened();
        /*
         * To test whether the classes under disease only has one parent class (disease class).
         * It would delete other parent class axiom except disease.
         */ 
        for(OWLClass cls : clses){
            
            String Label = getLabel(cls, ontology);
            int size = saveOntology.getSubClassAxiomsForSubClass(cls).size();
            for(OWLSubClassOfAxiom subAxiom : saveOntology.getSubClassAxiomsForSubClass(cls)){
                OWLClassExpression parentExpression = subAxiom.getSuperClass();
                if(!parentExpression.equals(disease) && size > 1){
                     RemoveAxiom remove = new RemoveAxiom(saveOntology, subAxiom);
                     manager.applyChange(remove);           
                }
            }
            if(!Label.equals("")){
                if(DiseaseOntologyURIConvert.containsKey(Label)){
                    OWLClass referenceClass = (OWLClass) DiseaseOntologyURIConvert.get(Label);
                    reasoner = reasonerFactory.createReasoner(saveOntology);
                    NodeSet<OWLClass> node = reasoner.getSubClasses(referenceClass, true);
                    if(!node.isBottomSingleton()){
                        Set<OWLClass> setofClass = node.getFlattened();
                        for(OWLClass subClass : setofClass){
                            addClassRestriction(subClass, cls);
                        }
                    }
                    for(OWLSubClassOfAxiom subAxiom : saveOntology.getSubClassAxiomsForSubClass(referenceClass)){
                        OWLClassExpression parentExpression = subAxiom.getSuperClass();
                        addClassRestriction(cls, parentExpression.asOWLClass());
                    }
                    for (OWLAxiom axiom : saveOntology.getAxioms(referenceClass)) {
                        RemoveAxiom remove = new RemoveAxiom(saveOntology, axiom);
                        manager.applyChange(remove);       
                    }
                    OWLEntityRemover remover = new OWLEntityRemover(manager, Collections.singleton(saveOntology));
                    referenceClass.accept(remover);
                    manager.applyChanges(remover.getChanges());
                }
            }
        }
    }
    /*
     * There is no restriction for disease terms in Disease Ontology, but we would
     * like to reuse the knowledge about where the disease takes place so we need to
     * know the relationship between anatomical terms and these disease terms. EFO
     * has provided this kind of information. Where there is a disease imported 
     * in EFO and it also attaches the DOID by which we can find whether such diseaes
     * is existing in EFO or not. 
     * Once the disease class in Coirell cell line ontology can be found in EFO.
     * It will be replaced with the EFO one and all the restrictions will imported
     * as well.
     * 
     * @param myClass                     the class in Coriell
     * @param efo                         the class in efo
     * 
     */ 
    public void getRestrictionDisease(OWLClass myClass, OWLClass efo){
        
        try{
            //System.out.println("The replaced class is " + myClass + " and the replacing class is " + efo);
            
            //ontology = manager.loadOntologyFromOntologyDocument(EFOfile);
            
            reasoner = reasonerFactory.createReasoner(saveOntology);
            
            NodeSet<OWLClass> node = reasoner.getSubClasses(myClass, true);
            
            if(!node.isBottomSingleton()){
                Set<OWLClass> setofClass = node.getFlattened();
                for(OWLClass subClass : setofClass){
                    addClassRestriction(subClass, efo);
                }
            }
            
            for(OWLSubClassOfAxiom subAxiom : saveOntology.getSubClassAxiomsForSubClass(myClass)){
                OWLClassExpression parentExpression = subAxiom.getSuperClass();
                addClassRestriction(efo, parentExpression.asOWLClass());
            }
            addLabel(efo, getLabel(myClass, saveOntology));
            addAnnotationExceptLabel(efo);
            addEquivalentClass(efo);
            
            for(OWLSubClassOfAxiom ax : ontology.getSubClassAxiomsForSubClass(efo)){
                OWLClassExpression expression = ax.getSuperClass();
                if(!expression.isAnonymous()){
                    
                }else{
                    for(OWLClass cls : expression.getClassesInSignature()){
                        if(!cls.isDefined(saveOntology)){
                            for(OWLSubClassOfAxiom axiom : ontology.getSubClassAxiomsForSubClass(cls)){
                                OWLClassExpression desc = axiom.getSuperClass();
                                getAllClass(desc, cls);
                            }
                        }
                    }
                    addClassRestriction(efo, expression);
                }
            }

        }catch(Exception e){
            //System.out.println(e.getMessage());
        }
    }
}