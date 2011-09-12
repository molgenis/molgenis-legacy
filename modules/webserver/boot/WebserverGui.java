package boot;

import generic.OpenBrowser;
import generic.Utils;
import gui.Button;
import ircbot.IRCHandler;

import java.awt.Color;
import java.awt.Event;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import org.molgenis.MolgenisOptions;

import app.servlet.MolgenisServlet;
import app.servlet.UsedMolgenisOptions;
import core.WWWServer;

public class WebserverGui extends JFrame implements MouseListener
{
	private static final long serialVersionUID = -4617569886547354084L;
	JTextArea output;
	WWWServer web;
	static Thread webserverthread;
	static IRCHandler botentry = new IRCHandler();
	MolgenisOptions usedOptions = new UsedMolgenisOptions();
	OpenBrowser browser = new OpenBrowser();
	String variant = MolgenisServlet.getMolgenisVariantID();
	String title = variant + " powered by Molgenis Webserver";
	final String url;
	boolean init = false;

	public Map<String, Button> buttons = new HashMap<String, Button>();

	WebserverGui(Integer port) throws IOException
	{
		WWWServer.DEF_PORT = port;
		setSize(300, 150);
		setTitle(title);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		addMouseListener(this);
		(webserverthread = new Thread(web = new WWWServer(variant))).start();
		if (usedOptions.generate_BOT)
		{
			web.setAttribute("bot", botentry);
			new Thread(botentry).start();
		}
		url = "http://localhost:" + WWWServer.DEF_PORT + "/" + variant;
		init = true;
		this.repaint();
	}

	public void paint(Graphics g)
	{
		paintComponent(g);
	}

	protected void clear(Graphics g)
	{
		super.paintComponents(g);
	}

	public void paintComponent(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		
		Font f = new Font(Font.MONOSPACED,Font.BOLD, 12);
		g2d.setFont(f);

		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, getWidth(), getHeight());

		if (init)
		{
			buttons.put("url", new Button(url, 25, 60, url.length()*8, g2d));
			buttons.put("stop", new Button("Stop", 25, 85, 60, g2d));
			buttons.put("start", new Button("Start", 25, 85, 60, g2d));
			buttons.put("restart", new Button("Restart", 90, 85, 60, g2d));

			g2d.setColor(Color.BLACK);
			g2d.drawString("Please open a browser and go to:", 25, 48);

			buttons.get("url").render();
			buttons.get("restart").render();

			if (webserverthread != null && webserverthread.isAlive())
			{
				buttons.get("stop").render();
			}
			else
			{
				buttons.get("start").render();
			}

			g2d.setColor(Color.BLACK);
			g2d.drawString("Webserver status:", 25, 130);

			if (webserverthread != null && webserverthread.isAlive())
			{
				g2d.setColor(Color.GREEN);
				g2d.fillRect(150, 119, 44, 12);
				g2d.setColor(Color.BLACK);
				g2d.drawString("Online", 150, 130);

			}
			else
			{
				g2d.setColor(Color.RED);
				g2d.fillRect(150, 119, 50, 12);
				g2d.setColor(Color.BLACK);
				g2d.drawString("Offline", 150, 130);
			}
		}
		else
		{
			g2d.setColor(Color.BLACK);
			g2d.drawString("Webserver starting, please wait...", 25, 48);
		}
	}

	void startWebServer()
	{
		try
		{
			web = new WWWServer(variant);
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
		webserverthread = new Thread(web);
		webserverthread.start();
	}

	void stopWebServer()
	{
		web.shutdown();
		try
		{
			web.getAcceptor().destroy();
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
		web.getThreadPool().setMaxThreads(-1);
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		Utils.console("" + e.getX() + " " + e.getY());

		for (String key : buttons.keySet())
		{
			Button b = buttons.get(key);
			
			if (e.getX() > b.getX() && e.getX() < b.getXstop() && e.getY() > b.getY()
					&& e.getY() < b.getYstop())
			{
				if (key.equals("url"))
				{
					Utils.console("GOTO Molgenis clicked");
					browser.openURL(url);
				}
				if (key.equals("start"))
				{
					if (!webserverthread.isAlive())
					{
						Utils.console("START webserver clicked");
						startWebServer();
					}
				}
				if (key.equals("stop"))
				{
					if (webserverthread.isAlive())
					{
						Utils.console("STOP webserver clicked");
						stopWebServer();
					}
				}
				if (key.equals("restart"))
				{
					Utils.console("RESTART webserver clicked");
					if (webserverthread.isAlive())
					{
						stopWebServer();
					}
					startWebServer();
				}

			}
		}
		repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		repaint();
	}

	@Override
	public boolean mouseMove(Event e, int x, int y)
	{
		repaint();
		return true;
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		repaint();
	}

}
