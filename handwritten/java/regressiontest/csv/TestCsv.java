package regressiontest.csv;

public class TestCsv
{

	public TestCsv() throws Exception
	{
		String case1 = new Case1().isResult() == true ? "PASS" : "FAIL";
		String case2 = new Case2().isResult() == true ? "PASS" : "FAIL";
		printTestResults(case1, case2);
	}

	private void printTestResults(String... cases)
	{
		System.out.println("\n***************");
		System.out.println("TestCsv results");
		int count = 1;
		for (String caze : cases)
		{
			System.out.println("Case " + (count++) + ":  [" + caze + "]");
		}
		System.out.println("***************");
	}

	public static void main(String[] args) throws Exception
	{
		new TestCsv();
	}

}
