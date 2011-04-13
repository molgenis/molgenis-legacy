import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import org.molgenis.MolgenisOptions;

import app.servlet.MolgenisServlet;

public class RunStandalone extends JFrame{
	private static final long serialVersionUID = -4617569886547354084L;
	JTextArea output;

	RunStandalone() throws IOException{
		setSize(250,100); 
		setTitle("Molgenis Webserver");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	    setVisible(true);
		WWWServer web = new WWWServer();
		Thread t = new Thread(web);
		t.start();
	}
	
	public void paint(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(),getHeight());
		g.setColor(Color.white);
		g.drawString("Please open a browser and go to:",25,50);
		g.drawString("http://localhost:" + WWWServer.DEF_PORT + "/" + MolgenisServlet.getMolgenisVariantID(),25,75);
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		new RunStandalone();
	}

}
