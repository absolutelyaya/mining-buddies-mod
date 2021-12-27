package yaya.miningbuddies.GUI.Hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import yaya.miningbuddies.Buddies.Animation;
import yaya.miningbuddies.Buddies.BuddyType;
import yaya.miningbuddies.Buddies.Reaction;
import yaya.miningbuddies.Events.BuddyUIEReachDestinationCallback;
import yaya.miningbuddies.MiningBuddiesMod;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static java.lang.Math.abs;

@Environment(EnvType.CLIENT)
public class BuddyUIElement extends DrawableHelper
{
	final MinecraftClient client;
	final Vector2f movementBounds;
	final Random random;
	final boolean moveRandomly;
	
	boolean moving, flip, pause;
	int frame, animLoops;
	double pos, destination;
	double speedMultiplier = 1;
	float frameTime, moveCooldown, reactionTime, songTime;
	float alpha = 1f;
	BuddyType buddyType;
	AnimationState state;
	AnimationState beforeLastMove = AnimationState.IDLE;
	Animation activeAnimation;
	TextureManager textureManager;
	Reaction activeReaction;
	BlockingQueue<Integer> song;
	
	public BuddyUIElement(Vector2f movementBounds, boolean moveRandomly, boolean pause)
	{
		this.client = MinecraftClient.getInstance();
		this.movementBounds = movementBounds;
		this.moveRandomly = moveRandomly;
		random = new Random();
		textureManager = MinecraftClient.getInstance().getTextureManager();
		this.pause = pause;
	}
	
	public void render(MatrixStack matrices, float deltaTime)
	{
		matrices.translate(pos, 0, 0);
		if(buddyType != null)
		{
			if(!(pause && MinecraftClient.getInstance().isPaused()))
			{
				frameTime += deltaTime;
				update(deltaTime);
				if(activeAnimation != null && frameTime > activeAnimation.interval())
				{
					frameTime = 0f;
					if(activeAnimation.frames() > 1)
						frame++;
					if(frame > activeAnimation.frames() - 1 || activeAnimation.frames() == 1)
					{
						frame = 0;
						if(activeReaction == null || !activeReaction.type.equals(Reaction.ReactionTrigger.NOTEBLOCK))
							animLoops++;
						if(activeAnimation.loops() != 0 && animLoops > activeAnimation.loops())
						{
							if(activeReaction != null)
								activeReaction = null;
							setActiveAnimation(AnimationState.IDLE);
						}
					}
				}
			}
			
			RenderSystem.setShaderColor(1f, 1f, 1f, alpha);
			Identifier texture = new Identifier(buddyType.getID().getNamespace(), "textures/buddies/" + buddyType.getID().getPath() + ".png");
			Vector2f textureSize;
			Vector2f buddySize;
			if(textureManager == null)
				textureManager = MinecraftClient.getInstance().getTextureManager();
			if (textureManager == null || textureManager.getTexture(texture) == null || activeAnimation == null)
			{
				RenderSystem.setShaderTexture(0, new Identifier(MiningBuddiesMod.MOD_ID, "textures/buddies/missing.png"));
				textureSize = new Vector2f(32, 32);
				buddySize = new Vector2f(32, 32);
			}
			else
			{
				RenderSystem.setShaderTexture(0, texture);
				textureSize = buddyType.getTextureSize();
				buddySize = buddyType.getBuddySize();
			}
			drawTexture(matrices, 0, 0, frame * buddySize.getX() + (flip ? textureSize.getX() - buddySize.getX() : 0),
					activeAnimation == null ? 0 : activeAnimation.index() * buddySize.getY(), 32, 32,
					(int)textureSize.getX() * (flip ? -1 : 1), (int)textureSize.getY());
		}
	}
	
	void update(float deltaTime)
	{
		double moveSpeed = buddyType.getMoveSpeed();
		if(abs(destination - pos) > moveSpeed * speedMultiplier * deltaTime && state.canMove)
		{
			flip = pos < destination;
			pos = pos + (flip ? 1 : -1) * moveSpeed * speedMultiplier * deltaTime;
			moving = true;
		}
		else if(moving && state.equals(AnimationState.MOVE))
		{
			moving = false;
			moveCooldown = random.nextFloat() * 9f + 1f;
			BuddyUIEReachDestinationCallback.EVENT.invoker().interact(this);
		}
		
		if(moving)
			setActiveAnimation(AnimationState.MOVE);
		if(!moving && state.canMove)
		{
			if(state == AnimationState.MOVE)
				setActiveAnimation(beforeLastMove);
			if(moveCooldown > 0)
				moveCooldown -= deltaTime;
			else if(moveRandomly)
			{
				destination = random.nextFloat() * movementBounds.getY() * 2 + movementBounds.getX();
			}
		}
		
		if(buddyType.getReactions().containsKey(Reaction.ReactionTrigger.NOTEBLOCK) && !song.isEmpty())
		{
			songTime += deltaTime;
			if(activeReaction != null && activeReaction.type.equals(Reaction.ReactionTrigger.NOTEBLOCK))
			{
				if(songTime > 0.2f)
				{
					if(client.player != null)
					{
						try
						{
							client.player.playSound(Registry.SOUND_EVENT.get(new Identifier("block.note_block.flute")), 1f, (float)Math.pow(2.0D,
									(double)(song.take() - 12) / 12.0D));
						}
						catch (InterruptedException e)
						{
							e.printStackTrace();
						}
					}
					reactionTime = 0f;
					songTime = 0f;
				}
			}
			else if(songTime > 2f)
			{
				if (updateReaction(Reaction.ReactionTrigger.NOTEBLOCK, "", 0))
					songTime = 0f;
			}
		}
		if(song.isEmpty() && activeReaction != null && activeReaction.type.equals(Reaction.ReactionTrigger.NOTEBLOCK))
		{
			activeReaction = null;
			setActiveAnimation(AnimationState.IDLE);
		}
		
		if(activeReaction != null)
		{
			//If reaction animation loops infinitely, check if the reaction hasn't been updated in longer than a second.
			//If true, stop the reaction and go back to idling.
			if(activeAnimation.loops() == 0 && activeReaction.animation.equalsIgnoreCase(state.name()) && reactionTime > 1f)
			{
				activeReaction = null;
				setActiveAnimation(AnimationState.IDLE);
			}
			reactionTime += deltaTime;
		}
	}
	
	public void setDestination(double destination)
	{
		this.destination = destination;
	}
	
	public void setPos(double pos)
	{
		this.pos = pos;
	}
	
	public void setBuddyType(BuddyType buddyType)
	{
		this.buddyType = buddyType;
		this.frameTime = 0;
		this.frame = 0;
		this.pos = 0;
		this.destination = 0;
		this.animLoops = 0;
		setActiveAnimation(AnimationState.IDLE);
		if(buddyType != null && buddyType.getReactions().containsKey(Reaction.ReactionTrigger.NOTEBLOCK))
		{
			Reaction r = buddyType.getReactions().get(Reaction.ReactionTrigger.NOTEBLOCK).get(0);
			song = new ArrayBlockingQueue<>(r.getMax());
		}
	}
	
	public void setActiveAnimation(AnimationState state)
	{
		if(this.state != state && buddyType != null)
		{
			this.activeAnimation = buddyType.getAnimation(state);
			if(activeAnimation == null && client.player != null)
				client.player.sendMessage(new TranslatableText("error.miningbuddies.missing.animation",
						buddyType.getID()), false);
			if(state == AnimationState.MOVE)
				beforeLastMove = this.state;
			this.state = state;
			this.frameTime = 0;
			this.frame = 0;
			this.animLoops = 0;
		}
	}
	
	public boolean updateReaction(Reaction.ReactionTrigger type, String data, int value)
	{
		if(buddyType != null)
		{
			for(Reaction r : buddyType.getReactions().get(type))
			{
				if(r.isAppropriate(data, value) && (activeReaction == null || activeReaction.getWeight() <= r.getWeight()))
				{
					activeReaction = r;
					setActiveAnimation(AnimationState.valueOf(r.animation.toUpperCase()));
					reactionTime = 0;
					return true;
				}
			}
		}
		return false;
	}
	
	public void queueNote(int note)
	{
		if(buddyType.getReactions().containsKey(Reaction.ReactionTrigger.NOTEBLOCK))
		{
			if(song.offer(note))
				songTime = 0;
		}
	}
	
	public void setAlpha(float alpha)
	{
		this.alpha = alpha;
	}
	
	public void setSpeedMultiplier(double speedMultiplier)
	{
		this.speedMultiplier = speedMultiplier;
	}
	
	@SuppressWarnings("unused")
	public enum AnimationState
	{
		IDLE(true, null),
		CHEER(false, IDLE),
		MOVE(true, IDLE),
		PANIC(true, IDLE),
		GRUMPY(false, IDLE),
		SING(false, CHEER);
		
		final boolean canMove;
		final AnimationState dodgeAnim;
		
		/**
		 * @param canMove Whether the Buddy can move in this state. When it does it will switch to the move state, but will switch back afterwards.
		 * @param dodgeAnim If this buddy doesn't have an animation for this state, use this one instead.
		 */
		AnimationState(boolean canMove, AnimationState dodgeAnim)
		{
			this.canMove = canMove;
			this.dodgeAnim = dodgeAnim;
		}
		
		public AnimationState getDodgeAnim()
		{
			return dodgeAnim;
		}
	}
}
