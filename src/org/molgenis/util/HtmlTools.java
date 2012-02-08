package org.molgenis.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Helper functions for HTML interaction
 * 
 * @author joerivandervelde
 * 
 */
public class HtmlTools
{
	/**
	 * Get the complete project URL that others can use to access the
	 * application. Can be used as database path in R, amongst other things.
	 * 
	 * @param request
	 *            This is your HttpServletRequestTuple.
	 * @param hostIP
	 *            Get this by using HtmlTools.getExposedIP().
	 * @param molgenisVariantID
	 *            Get this by using
	 *            app.servlet.MolgenisServlet.getMolgenisVariantID().
	 * @return Complete project URL path. Use URL.toString() to get the string
	 *         version.
	 * @throws MalformedURLException
	 */
	public static URL getExposedProjectURL(Tuple request, String hostIP, String molgenisVariantID)
			throws MalformedURLException
	{
		String protocol = ((HttpServletRequestTuple) request).getRequest().getScheme();
		int port = ((HttpServletRequestTuple) request).getRequest().getServerPort();
		URL reconstructedURL = new URL(protocol, hostIP, port, "/" + molgenisVariantID);
		return reconstructedURL;
	}

	/**
	 * Get the IP address that you are using to connect to services. Uses the
	 * gbic.target.rug.nl server. If the server is down or any other problem
	 * occurs, the result will be null.
	 * 
	 * @return The IP address if successful. Otherwise null.
	 * @throws Exception
	 */
	public static String getExposedIPAddress() throws Exception
	{
		String host = null;
		try
		{
			URL u = new URL("http://gbicdev.target.rug.nl:8080/user/ip");
			InputStream is = u.openStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(new BufferedInputStream(is)));
			host = br.readLine();
			br.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "localhost";
		}
		return host;
	}

	/**
	 * Helper function to convert any string into URL-safe encoding.
	 * 
	 * @param input
	 * @return
	 */
	public static String toSafeUrlString(String input)
	{
		if(input.length() == 0){
			return "";
		}
		String enc = "";
		for (char c : input.toCharArray())
		{
			enc += (int) c + ".";
		}
		enc = enc.substring(0, enc.length() - 1);
		return enc;
	}
	
	/**
	 * Helper function to convert any string into URL-safe encoding.
	 * Output string is less easy to translate back to the original.
	 * 
	 * @param input
	 * @return
	 */
	public static String toSafeUrlStringO_b_f(String input)
	{
		if(input.length() == 0){
			return "";
		}
		String enc = "";
		for (char c : input.toCharArray())
		{
			enc += (int) (Math.pow((c), 2) - Math.pow(10, 4)) + ".";
		}
		enc = enc.substring(0, enc.length() - 1);
		return enc;
	}
	
	/**
	 * Helper function to convert an URL-safe string (passed from eg. a REST
	 * interface) back to the original string. Input string passes an
	 * extra translation step.
	 * 
	 * @param input
	 * @return
	 */
	public static String fromSafeUrlStringO_b_f(String input)
	{
		String dec = "";
		for (String nr : input.split("\\."))
		{
			int i = (int) Math.sqrt(Integer.parseInt(nr) + Math.pow(10, 4));
			char c = (char) i;
			dec += c;
		}
		return dec;
	}
	
	/**
	 * Helper function to convert any string into URL-safe encoding.
	 * 
	 * @param input
	 * @return
	 */
	public static String toSafeUrlStringObv(String input)
	{
		if(input.length() == 0){
			return "";
		}
		String enc = "";
		for (char c : input.toCharArray())
		{
			enc +=((int)(Math.pow((c), 2)-4321))+ ".";
		}
		enc = enc.substring(0, enc.length() - 1);
		return enc;
	}

	/**
	 * Helper function to convert an URL-safe string (passed from eg. a REST
	 * interface) back to the original string.
	 * 
	 * @param input
	 * @return
	 */
	public static String fromSafeUrlString(String input)
	{
		String dec = "";
		for (String nr : input.split("\\."))
		{
			int i = Integer.parseInt(nr);
			char c = (char) i;
			dec += c;
		}
		return dec;
	}

	/**
	 * Example for toSafeUrlString() and fromSafeUrlString():
	 * 
	 * String example = "abcabc!@#$%^&*(){}:,./;|'\"<>"; String enc =
	 * HtmlTools.toSafeUrlString(example); System.out.println(enc); if
	 * (example.equals(HtmlTools.fromSafeUrlString(enc))) {
	 * System.out.println("success"); }
	 */

}
