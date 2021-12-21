package yaya.miningbuddies.Buddies;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.util.Identifier;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class BuddyType
{
	private final Identifier id;
	public final String name;
	private final Vector2f textureSize, buddySize;
	private final Map<String, Animation> animations;
	private final double moveSpeed;
	
	public BuddyType(String name, Identifier identifier, Vector2f textureSize, Vector2f buddySize, Map<String, Animation> animations, double movementSpeed)
	{
		this.name = name;
		this.id = identifier;
		this.buddySize = buddySize;
		this.textureSize = textureSize;
		this.animations = ImmutableMap.copyOf(animations);
		this.moveSpeed = movementSpeed;
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
	
	public double getMoveSpeed()
	{
		return moveSpeed;
	}
}
