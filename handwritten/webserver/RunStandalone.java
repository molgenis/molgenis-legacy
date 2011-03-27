import java.io.IOException;


public class RunStandalone
{

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		WWWServer web = new WWWServer();
		Thread t = new Thread(web);
		t.start();
	}

}
