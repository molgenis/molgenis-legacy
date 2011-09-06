//package org.molgenis.matrix.component.test;
//
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.util.List;
//
//import org.molgenis.MolgenisOptions;
//import org.molgenis.framework.db.DatabaseException;
//import org.molgenis.matrix.component.ObservedValueMatrix;
//import org.molgenis.pheno.ObservableFeature;
//import org.molgenis.util.cmdline.CmdLineException;
//import org.molgenis.variant.SequenceVariant;
//import org.testng.annotations.BeforeClass;
//import org.testng.annotations.Test;
//
//import app.JDBCDatabase;
//
//public class TestObservedValueMatrix
//{
//	ObservedValueMatrix m;
//	
//	@BeforeClass
//	public void setup() throws DatabaseException, FileNotFoundException, IOException, CmdLineException
//	{
//		//create am empty database with example filling
//		
//		//create instance of the matrix ready for the unit test
//		m = new ObservedValueMatrix(new JDBCDatabase(new MolgenisOptions("apps/patho/patho.properties")), SequenceVariant.class, ObservableFeature.class);
//	}
//	
//	
//	@Test
//	public void testRowHeaders() throws Exception
//	{
//		List<SequenceVariant> rows = m.getRowHeaders();
//	}
//}
