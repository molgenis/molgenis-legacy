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

package org.molgenis.catalogue;
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
    
    
    public OntologyBuilder(OWLOntologyManager manager,String base) {

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
        //referenceOntologyURIConvert = labelMapURI(ontology);
        //chaoClassURIConvert = labelMapURI(saveOntology);
        reasonerFactory = new Reasoner.ReasonerFactory();
        reasoner = reasonerFactory.createReasoner(saveOntology);
        //DiseaseOntologyURIConvert = labelMapURI(ontology);
    }//end of setOntology method
    
    
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
     * This method is used to create hashmap store URI and label of classes. The label is taken as key
     * and URI as value so we can get class URI by class label
     *
     * @param      owlontology is the ontology that we want to create the hashmap
     * @return     mapURI is the hashmap 
     *  
     */
    public HashMap labelMapURI(OWLOntology owlontology, OWLDataFactory factory){
        
        HashMap mapURI = new HashMap();
        OWLAnnotationProperty label = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
            for (OWLClass cls : owlontology.getClassesInSignature()) {
                // Get the annotations on the class that use the label property
                for (OWLAnnotation annotation : cls.getAnnotations(owlontology, label)) {
                    if (annotation.getValue() instanceof OWLLiteral) {
                        OWLLiteral val = (OWLLiteral) annotation.getValue();
                        String labelString = val.getLiteral();
                        mapURI.put(cls, labelString);
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
}