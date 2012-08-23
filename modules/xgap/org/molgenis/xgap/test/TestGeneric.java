package org.molgenis.xgap.test;

import generic.CommandExecutor;
import generic.JavaCompiler;
import generic.JavaCompiler.CompileUnit;
import generic.Utils;

import org.testng.annotations.Test;

public class TestGeneric {
	@Test
	public void testWebserver(){
		org.testng.Assert.assertEquals(true, true);
	}
	
	@Test
	void CommandExecutor(){
		org.testng.Assert.assertEquals(true, true);
		CommandExecutor c = new CommandExecutor("cd tests");
		c.addCommand("R CMD BATCH test.R");
		org.testng.Assert.assertEquals(c.getCommands().size(), 2);
		c.clearCommands();
		org.testng.Assert.assertEquals(c.getCommands().size(), 0);
	}
	
	@Test
	void JavaCompiler(){
		JavaCompiler compiler = new JavaCompiler();
		CompileUnit c = compiler.newCompileUnit();
		c.addDependency("c:/test");
		org.testng.Assert.assertEquals(c.getDependencies(), "build;src;c:/test");	
	}
	
	@Test
	void OpenBrowser(){
		org.testng.Assert.assertEquals(true, true);
	}
	
	@Test
	void Utils(){
		double[] doubles = {1.0, 5.9, 10.56};
		org.testng.Assert.assertEquals(Utils.printDoubleArray(doubles), "1.0 5.9 10.56 ");
		org.testng.Assert.assertEquals(Utils.firstLetterUpperCase("wow"), "Wow");
		org.testng.Assert.assertEquals(Utils.firstLetterUpperCase("Tetsing"), "Tetsing");
		org.testng.Assert.assertEquals(Utils.firstLetterLowerCase("Tetsing"), "tetsing");
		org.testng.Assert.assertEquals(Utils.firstLetterLowerCase("tsing"), "tsing");
	}
}
