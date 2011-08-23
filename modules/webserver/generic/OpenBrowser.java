package generic;

import javax.swing.JOptionPane;

public class OpenBrowser {
	public void openURL(String url) {
		String osName = System.getProperty("os.name");
		try {
			if (osName.startsWith("Windows")){
				Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
			}else {
                String[] browsers = { "firefox-bin", "firefox", "iceweasel", "opera", "konqueror", "epiphany", "mozilla", "netscape", "safari", "camino" };
                String browser = null;
                for (int count = 0; count < browsers.length && browser == null; count++)
                {
                	try{
                		Runtime.getRuntime().exec(new String[] { browsers[count] }).waitFor();
                		 browser = browsers[count];	
                		 Runtime.getRuntime().exec(new String[] { browser, url });
                	}catch(Exception e){
                		//e.printStackTrace();
                		//export PATH=/Applications/Firefox.app/Contents/MacOS:$PATH  
                	}
				}
                JOptionPane.showMessageDialog(null, "No browser found on your path, please open it yourself.");
			}
		}
			catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error in opening browser:\n" + e.getLocalizedMessage());
        }
	}
}