package yaya.miningbuddies.Buddies;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.util.Identifier;

import java.util.Map;

public class BuddyType
{
	private final Identifier id;
	public final String name;
	private final Vector2f textureSize;
	private final Vector2f buddySize;
	private final Map<String, Animation> animations;
	
	public BuddyType(String name, Identifier identifier, Vector2f textureSize, Vector2f buddySize, Map<String, Animation> animations)
	{
		this.name = name;
		this.id = identifier;
		this.buddySize = buddySize;
		this.textureSize = textureSize;
		this.animations = ImmutableMap.copyOf(animations);
	}
	
	public Identifier getID()
	{
		return id;
	}
	
	public Vector2f getTextureSize()
	{
		return textureSize;
	}
	
	public Vector2f getBuddySize()
	{
		return buddySize;
	}
	
	public Animation getAnimation(String id)
	{
		return animations.getOrDefault(id, animations.get("idle"));
	}
}
