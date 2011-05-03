import generic.OpenBrowser;
import generic.Utils;

import ircbot.IRCHandler;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import app.servlet.MolgenisServlet;

public class RunStandalone extends JFrame implements MouseListener{
	private static final long serialVersionUID = -4617569886547354084L;
	JTextArea output;
	WWWServer web;
	IRCHandler irc;
	Thread webserverthread;
	OpenBrowser browser = new OpenBrowser();
	String title =  MolgenisServlet.getMolgenisVariantID() + " powered by Molgenis Webserver";
	final String url = "http://localhost:" + WWWServer.DEF_PORT + "/" + MolgenisServlet.getMolgenisVariantID();

	RunStandalone() throws IOException{
		setSize(300,150); 
		setTitle(title);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	    setVisible(true);
	    addMouseListener(this);
		
		new Thread(irc = new IRCHandler()).start();
	    (webserverthread = new Thread(web = new WWWServer())).start();
	}
	
	public void paint(Graphics g) {
		paintComponent(g);
	}
	
	protected void clear(Graphics g) {
		super.paintComponents(g);
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, getWidth(),getHeight());
		g2d.setColor(Color.WHITE);
		//CLICK to Molgenis
		g2d.drawString("Please open a browser and go to:",25,50);
		g2d.setColor(Color.LIGHT_GRAY);
		g2d.fillRect(25, 60, 200, 20);
		g2d.setColor(Color.white);
		g2d.draw3DRect(24, 59, 202, 22, true);
		g2d.setColor(Color.WHITE);
		g2d.drawString(url,25,75);
		g2d.setColor(Color.LIGHT_GRAY);
		//STOP
		g2d.fillRect(90, 85, 35, 20);
		g2d.setColor(Color.white);
		g2d.draw3DRect(89, 84, 37, 22, true);
		g2d.setColor(Color.RED);
		g2d.drawString("STOP",91,100);
		g2d.setColor(Color.LIGHT_GRAY);
		//START
		g2d.fillRect(130, 85, 40, 20);
		g2d.setColor(Color.white);
		g2d.draw3DRect(129, 84, 42, 22, true);
		g2d.setColor(Color.GREEN);
		g2d.drawString("START",131,100);
		if(webserverthread != null && webserverthread.isAlive()){
			g2d.setColor(Color.GREEN);
		}else{
			g2d.setColor(Color.RED);
		}
		g2d.drawString("Webserver:",25,100);
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		new RunStandalone();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Utils.console(""+e.getX()+" "+e.getY());
		if(e.getX() > 25 && e.getX() < 225){
			if(e.getY() > 60 && e.getY() < 80){
				Utils.console("GOTO Molgenis clicked");
				browser.openURL(url);
			}
		}
		if(e.getY() > 85 && e.getY() < 105){
			if(e.getX() > 90 && e.getX() < 125){
				Utils.console("STOP webserver clicked");
				if(webserverthread.isAlive()){
					web.shutdown();
					try {
						web.acceptor.destroy();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					web.threadPool.setMaxThreads(-1);
				}
				
			}else if(e.getX() > 130 && e.getX() < 170){
				Utils.console("START webserver clicked");
				if(!webserverthread.isAlive()){
				    try {
						web = new WWWServer();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				    webserverthread = new Thread(web);
				    webserverthread.start();
				}
			}
		}
		paint(getGraphics());
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		paint(getGraphics());
	}

	@Override
	public void mouseExited(MouseEvent e) {
		paint(getGraphics());
	}

	@Override
	public void mousePressed(MouseEvent e) {
		paint(getGraphics());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		paint(getGraphics());
	}

}
