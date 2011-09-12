package gui;

import java.awt.Color;
import java.awt.Graphics2D;

public class Button {
	
	private String text;
	private int x;
	private int y;
	private Graphics2D g2d;
	
	private int xStop;
	private int yStop;
	
	public Button(String text, int x, int y, int length, Graphics2D g2d)
	{
		super();
		this.text = text;
		this.x = x;
		this.y = y;
		this.g2d = g2d;
		this.xStop = x + length;
		this.yStop = y + 22;
	}



	public void render(){
		g2d.setColor(Color.BLACK);
		g2d.draw3DRect(x, y, xStop-x, yStop-y, true);
		g2d.setColor(Color.LIGHT_GRAY);
		g2d.fillRect(x+1, y+1, xStop-x-2, yStop-y-2);
		g2d.setColor(Color.BLACK);
		g2d.drawString(text,x+5,y+15);
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public int getXstop()
	{
		return xStop;
	}

	public int getYstop()
	{
		return yStop;
	}



	
}
