import java.awt.HeadlessException;
import java.io.IOException;


public class RunStandalone {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args)
	{
		try{
		try{
			new WebserverGui();
		}catch(HeadlessException e){
			System.out.println("No GUI available going into commandline mode");
			new Thread(new WebserverCmdLine()).start();
		}
		}catch(IOException e){
			System.out.println("IO exception bubbled up to main\nSomething went wrong: " + e.getMessage());
		}
	}
}
