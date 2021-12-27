package yaya.miningbuddies.Buddies;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.util.Identifier;
import yaya.miningbuddies.GUI.Hud.BuddyUIElement;

import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class BuddyType
{
	private final Identifier id;
	public final String name;
	private final Vector2f textureSize, buddySize;
	private final Map<String, Animation> animations;
	private final Map<Reaction.ReactionTrigger, List<Reaction>> reactions;
	private final double moveSpeed;
	
	public BuddyType(String name, Identifier identifier, Vector2f textureSize, Vector2f buddySize, Map<String, Animation> animations, double movementSpeed, Map<Reaction.ReactionTrigger, List<Reaction>> reactions)
	{
		this.name = name;
		this.id = identifier;
		this.buddySize = buddySize;
		this.textureSize = textureSize;
		this.animations = ImmutableMap.copyOf(animations);
		this.moveSpeed = movementSpeed;
		this.reactions = reactions;
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
	
	public Animation getAnimation(BuddyUIElement.AnimationState anim)
	{
		if(anim == null)
			return null;
		if(animations.containsKey(anim.name().toLowerCase()))
			return animations.get(anim.name().toLowerCase());
		else
			return getAnimation(anim.getDodgeAnim());
	}
	
	public double getMoveSpeed()
	{
		return moveSpeed;
	}
	
	public Map<Reaction.ReactionTrigger, List<Reaction>> getReactions()
	{
		return reactions;
	}
	
	public static class MissingAnimationException extends Exception
	{
		public MissingAnimationException(String message, Throwable err)
		{
			super(message, err);
		}
	}
}
