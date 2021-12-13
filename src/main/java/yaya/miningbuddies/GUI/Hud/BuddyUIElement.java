package yaya.miningbuddies.GUI.Hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.util.Identifier;
import yaya.miningbuddies.Buddies.Animation;
import yaya.miningbuddies.Buddies.BuddyType;
import yaya.miningbuddies.Events.BuddyUIEReachDestinationCallback;

import java.util.Random;

import static java.lang.Math.abs;

public class BuddyUIElement extends DrawableHelper
{
	final Vector2f movementBounds;
	final Random random;
	final boolean moveRandomly;
	
	float frameTime;
	int frame;
	double pos;
	double destination;
	BuddyType buddyType;
	AnimationState state = AnimationState.IDLE;
	Animation activeAnimation;
	boolean moving;
	boolean flip;
	float moveCooldown;
	float alpha = 1f;
	double speedMultiplier = 1;
	
	public BuddyUIElement(Vector2f movementBounds, boolean moveRandomly)
	{
		this.movementBounds = movementBounds;
		this.moveRandomly = moveRandomly;
		random = new Random();
	}
	
	public void render(MatrixStack matrices, float deltaTime)
	{
		matrices.translate(pos, 0, 0);
		if(buddyType != null)
		{
			update(deltaTime);
			frameTime += deltaTime;
			if(activeAnimation != null && frameTime > activeAnimation.interval())
			{
				frame++;
				frameTime = 0;
				if(frame > activeAnimation.frames() - 1)
					frame = 0;
			}
			
			RenderSystem.setShaderColor(1f, 1f, 1f, alpha);
			RenderSystem.setShaderTexture(0,
					new Identifier(buddyType.getID().getNamespace(), "textures/buddies/" + buddyType.getID().getPath() + ".png"));
			Vector2f textureSize = buddyType.getTextureSize();
			Vector2f buddySize = buddyType.getBuddySize();
			drawTexture(matrices, 0, 0, frame * buddySize.getX(), activeAnimation.index() * buddySize.getY(),
					32, 32, (int)textureSize.getX() * (flip ? -1 : 1), (int)textureSize.getY());
		}
	}
	
	void update(float deltaTime)
	{
		double moveSpeed = buddyType.getMoveSpeed();
		if(abs(destination - pos) > moveSpeed * speedMultiplier * deltaTime)
		{
			flip = pos < destination;
			pos = pos + (flip ? 1 : -1) * moveSpeed * speedMultiplier * deltaTime;
			moving = true;
		}
		else if(moving && state.equals(AnimationState.MOVE))
		{
			moving = false;
			moveCooldown = random.nextFloat() * 6;
			BuddyUIEReachDestinationCallback.EVENT.invoker().interact(this);
		}
		
		if(moving && !state.equals(AnimationState.MOVE))
			setActiveAnimation(AnimationState.MOVE);
		if(!moving && state.equals(AnimationState.MOVE))
			setActiveAnimation(AnimationState.IDLE);
		
		if(!moving && state.equals(AnimationState.IDLE))
		{
			if(moveCooldown > 0)
				moveCooldown -= deltaTime;
			else if(moveRandomly)
			{
				destination = random.nextFloat() * movementBounds.getY() * 2 + movementBounds.getX();
			}
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
		this.frameTime = 0;
		this.buddyType = buddyType;
		this.pos = 0;
		this.destination = 0;
		setActiveAnimation(AnimationState.IDLE);
	}
	
	public void setActiveAnimation(AnimationState state)
	{
		this.state = state;
		this.frameTime = 0;
		if(buddyType != null)
			this.activeAnimation = buddyType.getAnimation(state.name().toLowerCase());
	}
	
	public void setAlpha(float alpha)
	{
		this.alpha = alpha;
	}
	
	public void setSpeedMultiplier(double speedMultiplier)
	{
		this.speedMultiplier = speedMultiplier;
	}
	
	public enum AnimationState
	{
		IDLE,
		CHEER,
		MOVE
	}
}
