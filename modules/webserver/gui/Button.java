package gui;

import java.awt.Color;
import java.awt.Graphics2D;

public class Button {
	public static void render(String text, int x, int y,Graphics2D g2d){
		g2d.setColor(Color.BLACK);
		g2d.draw3DRect(x, y, (7*text.length())+2, 22, true);
		g2d.setColor(Color.LIGHT_GRAY);
		g2d.fillRect(x+1, y+1, 7*text.length(), 20);
		g2d.setColor(Color.BLACK);
		g2d.drawString(text,x+5,y+15);
	}
}
