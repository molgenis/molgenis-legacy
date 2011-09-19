//package org.molgenis.matrix.component.test;
//
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.util.List;
//
//import org.molgenis.MolgenisOptions;
//import org.molgenis.framework.db.DatabaseException;
//import org.molgenis.framework.db.QueryRule.Operator;
//import org.molgenis.matrix.component.ObservedValueMatrix;
//import org.molgenis.pheno.ObservableFeature;
//import org.molgenis.pheno.ObservationElement;
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
//		//set row properties
//		m.sliceByRowProperty(SequenceVariant.CHR, Operator.EQUALS, 1);
//		//end of variant must be in start of range
//		m.sliceByRowProperty(SequenceVariant.ENDBP, Operator.GREATER_EQUAL, 1000);
//		//start of variant must be in end of range
//		m.sliceByRowProperty(SequenceVariant.STARTBP, Operator.LESS_EQUAL, 2000);
//		
//		//set column proporty, which is actual column
//		ObservationElement e = new ObservationElement();
//		e.setId(1);
//		e.setName("AlleleCount");
//		m.sliceByColValues(e, Operator.GREATER_EQUAL, 2);
//		
//		List<SequenceVariant> rows = m.getRowHeaders();
//	}
//}
