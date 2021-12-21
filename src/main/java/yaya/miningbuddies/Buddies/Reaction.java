package yaya.miningbuddies.Buddies;

public class Reaction
{
	public final ReactionTrigger type;
	
	final String animation;
	String data;
	int min = Integer.MIN_VALUE;
	int max = Integer.MAX_VALUE;
	
	public Reaction(ReactionTrigger type, String animation)
	{
		this.type = type;
		this.animation = animation;
	}
	
	public void setMax(int max)
	{
		this.max = max;
	}
	
	public void setMin(int min)
	{
		this.min = min;
	}
	
	public void setData(String data)
	{
		this.data = data;
	}
	
	public boolean isAppropriate(String data, int value)
	{
		return value >= min && value <= max;
	}
	
	public enum ReactionTrigger
	{
		PICKUP,
		LIGHTLEVEL,
		NEARBY
	}
}
