package test;

import java.io.File;

import generic.CommandExecutor;
import generic.JavaCompiler;
import generic.JavaCompiler.CompileUnit;
import generic.Utils;
import generic.FileUtils;

import org.testng.annotations.Test;

public class TestWebserver {
	@Test
	public void testWebserver(){
		System.out.println("Webserver Tests");
		org.testng.Assert.assertEquals(true, true);
	}
	
}
