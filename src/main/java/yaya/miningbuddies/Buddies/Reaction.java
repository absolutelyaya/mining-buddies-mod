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
	
	public int getMax() {
		return max;
	}
	
	public void setMin(int min)
	{
		this.min = min;
	}
	
	public int getMin() {
		return min;
	}
	
	public void setData(String data)
	{
		this.data = data;
	}
	
	public String getData() {
		return data;
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
		boolean b = true;
		if(type.usesData)
			b = data.equals(this.data);
		return value >= min && value <= max && b;
	}
	
	public enum ReactionTrigger
	{
		PICKUP(true),
		LIGHTLEVEL(false),
		NEARBY(true),
		NOTEBLOCK(false);
		
		final boolean usesData;
		ReactionTrigger(boolean usesData)
		{
			this.usesData = usesData;
		}
	}
}
