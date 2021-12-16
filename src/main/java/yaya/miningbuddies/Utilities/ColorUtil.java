package yaya.miningbuddies.Utilities;

public class ColorUtil
{
	public static final int WHITE = getColorFromRGBA(255, 255, 255, 255);
	public static final int GRAY = getColorFromRGBA(170, 170, 170, 255);
	public static final int DARK_GRAY = getColorFromRGBA(85, 85, 85, 255);
	public static final int BLACK = getColorFromRGBA(0, 0, 0, 255);
	public static final int YELLOW = getColorFromRGBA(255, 255, 85, 255);
	public static final int GREEN = getColorFromRGBA(85, 255, 85, 255);
	public static final int DARK_GREEN = getColorFromRGBA(0, 170, 0, 255);
	public static final int AQUA = getColorFromRGBA(85, 255, 255, 255);
	public static final int DARK_AQUA = getColorFromRGBA(0, 170, 170, 255);
	public static final int BLUE = getColorFromRGBA(85, 85, 255, 255);
	public static final int DARK_BLUE = getColorFromRGBA(0, 0, 170, 255);
	public static final int LIGHT_PURPLE = getColorFromRGBA(255, 85, 255, 255);
	public static final int DARK_PURPLE = getColorFromRGBA(170, 0, 170, 255);
	public static final int RED = getColorFromRGBA(255, 85, 85, 255);
	public static final int DARK_RED = getColorFromRGBA(170, 0, 0, 255);
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
