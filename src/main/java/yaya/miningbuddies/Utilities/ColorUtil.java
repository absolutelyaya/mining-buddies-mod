package yaya.miningbuddies.Utilities;

public class ColorUtil
{
	public static final int DARK_RED = getColorFromRGBA(145, 42, 42, 255);
	public static final int GOLD = getColorFromRGBA(255, 170, 0, 255);
	
	public static int getColorFromRGBA(int r, int g, int b, int a)
	{
		int color = a;
		color = (color << 8) + r;
		color = (color << 8) + g;
		color = (color << 8) + b;
		return color;
	}
}
