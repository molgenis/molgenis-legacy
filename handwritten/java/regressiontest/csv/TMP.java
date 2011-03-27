package regressiontest.csv;

import java.io.File;

import plugins.emptydb.emptyDatabase;

public class TMP
{

	public TMP(){
		File path = new File(new File("").getAbsolutePath() + "/handwritten/java/regressiontest/csv/tar");
		File tarBailey = new File(path + "/Bailey_old.tar.gz");
		System.out.println(tarBailey.exists());
		
		File path2 = new File(new File("").getAbsolutePath() + "/handwritten/java/regressiontest/csv/tar/");
		File tarBailey2 = new File(path2 + "Bailey_old.tar.gz");
		System.out.println(tarBailey2.exists()); 

	}
	public static void main(String[] args)
	{
		new TMP();

	}

}
