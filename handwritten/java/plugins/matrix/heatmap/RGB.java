package plugins.matrix.heatmap;

public class RGB
{
	private int R;
	private int G;
	private int B;

	/**
	 * Instantiates an RGB descriptor. Values must be 0 through 255. Int was
	 * used instead of byte for general convenience.
	 * 
	 * @param R
	 * @param G
	 * @param B
	 */
	public RGB(int R, int G, int B)
	{
		this.R = R;
		this.G = G;
		this.B = B;
	}

	public int getR()
	{
		return R;
	}

	public int getG()
	{
		return G;
	}

	public int getB()
	{
		return B;
	}

}
