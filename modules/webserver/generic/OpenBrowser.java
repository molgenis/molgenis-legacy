package generic;

import javax.swing.JOptionPane;

import org.molgenis.util.DetectOS;

public class OpenBrowser
{
	public void openURL(String url)
	{
		try
		{
			if (DetectOS.getOS().startsWith("windows"))
			{
				Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
			}
			else if (DetectOS.getOS().equals("mac"))
			{
				Runtime.getRuntime().exec("open " + url);
			}
			else
			{
				String[] browsers =
				{ "firefox", "iceweasel", "opera", "konqueror", "epiphany", "mozilla", "netscape", "safari" };
				boolean browserStarted = false;
				for (int index = 0; index < browsers.length; index++)
				{
					try
					{
						Runtime.getRuntime().exec(new String[]
						{ browsers[index], url });
						browserStarted = true;
						break;
					}
					catch (Exception e)
					{
						// try next browser
					}
				}
				if (!browserStarted)
				{
					JOptionPane.showMessageDialog(null, "No browser found on your path, please open it yourself.");
				}
			}
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(null, "Error in opening browser:\n" + e.getLocalizedMessage());
		}
	}
}
