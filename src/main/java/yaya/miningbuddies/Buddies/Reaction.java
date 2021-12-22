package yaya.miningbuddies.Buddies;

public class Reaction
{
	public final ReactionTrigger type;
	public final String animation;
	
	String data;
	int min = Integer.MIN_VALUE;
	int max = Integer.MAX_VALUE;
	int weight = 0;
	
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
	
	public void setWeight(int weight)
	{
		this.weight = weight;
	}
	
	public int getWeight()
	{
		return weight;
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
