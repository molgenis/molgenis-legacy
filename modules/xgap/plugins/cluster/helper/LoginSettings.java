package plugins.cluster.helper;

/**
 * Login settings used for remote cluster computing
 * 
 * @author joerivandervelde
 * 
 */
public class LoginSettings
{

	public String host;
	public String port;
	public String user;
	public String password;

	public String getHost()
	{
		return host;
	}

	public void setHost(String host)
	{
		this.host = host;
	}

	public String getPort()
	{
		return port;
	}

	public void setPort(String port)
	{
		this.port = port;
	}

	public String getUser()
	{
		return user;
	}

	public void setUser(String user)
	{
		this.user = user;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

}