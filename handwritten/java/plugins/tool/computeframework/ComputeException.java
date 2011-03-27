package plugins.tool.computeframework;


public class ComputeException extends Exception
{
    private static final long serialVersionUID = 3938068253732140125L;

	public ComputeException(String message)
	{
		super(message);
	}

	public ComputeException(Exception e)
	{
		super(e);
	}
}
