/* Date:        March 30, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.mapping;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.util.Tuple;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.query.Query;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.DC;
import com.itextpdf.text.pdf.hyphenation.TernaryTree.Iterator;

import de.fuberlin.wiwiss.d2rq.GraphD2RQ;
import de.fuberlin.wiwiss.d2rq.ModelD2RQ;
import de.fuberlin.wiwiss.d2rq.vocab.D2RQ;
import de.fuberlin.wiwiss.d2rq.map.Mapping;
import de.fuberlin.wiwiss.d2rq.mapgen.MappingGenerator;

public class GenerateN3andRDF extends PluginModel
{
	public GenerateN3andRDF(String name, ScreenModel parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugins_mapping_GenerateN3andRDF";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/mapping/GenerateN3andRDF.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		if ("GenerateN3".equals(request.getAction())) {
				
				Object d2rq = new D2RQ(); 
				
				Object mapping = new Mapping();
				
				String tmp = null;
				Object MappingGenerator = new MappingGenerator(tmp);
			
				// Set up the ModelD2RQ using a mapping file
				Model m = new ModelD2RQ("/Users/despoina/Documents/d2r-server-0.7/molgenis_rdf_mapping.n3");
				Model JenaMapping = FileManager.get().loadModel("/Users/despoina/Documents/d2r-server-0.7/molgenis_rdf_mapping.n3");
				// Set up the GraphD2RQ
				GraphD2RQ g = new GraphD2RQ((Model) JenaMapping, "http://localhost:2020/");

				// Create a find(spo) pattern 
				Node subject = Node.ANY;
				Node predicate = DC.date.asNode();
				Node object = Node.createLiteral("2003", null, XSDDatatype.XSDgYear);
				Triple pattern = new Triple(subject, predicate, object);

				// Query the graph
				Iterator it = (Iterator) g.find(pattern);

				// Output query results
				/*
				while (it.hasNext()) {
				    Triple t = (Triple) it.next();
				    System.out.println("Published in 2003: " + t.getSubject());
				}
				*/
				
				String sparql = 
				    "PREFIX dc: <http://purl.org/dc/elements/1.1/>" +
				    "PREFIX foaf: <http://xmlns.com/foaf/0.1/>" +
				    "SELECT ?Investigation ?Investigation_name WHERE {" +
				    "    ?Investigation_name dc:Investigation.name 'Hippocrates' . " +
				    "}";
				
				com.hp.hpl.jena.query.Query q = QueryFactory.create(sparql); 
				ResultSet rs = QueryExecutionFactory.create(q, m).execSelect();
				while (rs.hasNext()) {
				    QuerySolution row = rs.nextSolution();
				    System.out.println("Title: " + row.getLiteral("paperTitle").getString());
				    System.out.println("Author: " + row.getLiteral("authorName").getString());
				}
		
			}
			
		
	}

	@Override
	public void reload(Database db)
	{

	}
	
}
