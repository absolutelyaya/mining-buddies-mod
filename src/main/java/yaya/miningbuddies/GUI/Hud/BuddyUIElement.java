package yaya.miningbuddies.GUI.Hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.util.Identifier;
import yaya.miningbuddies.Buddies.Animation;
import yaya.miningbuddies.Buddies.BuddyType;

import java.util.Random;

import static java.lang.Math.abs;

public class BuddyUIElement extends DrawableHelper
{
	final Vector2f movementBounds;
	final Random random;
	
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
	
	public BuddyUIElement(Vector2f movementBounds)
	{
		this.movementBounds = movementBounds;
		random = new Random();
	}
	
	public void render(MatrixStack matrices, float deltaTime)
	{
		update(deltaTime);
		matrices.translate(pos, 0, 0);
		if(buddyType != null)
		{
			frameTime += deltaTime;
			if(activeAnimation != null && frameTime > activeAnimation.interval())
			{
				frame++;
				frameTime = 0;
				if(frame > activeAnimation.frames() - 1)
					frame = 0;
			}
			
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
		if(abs(destination - pos) > moveSpeed * deltaTime)
		{
			flip = pos < destination;
			pos = pos + (flip ? 1 : -1) * moveSpeed * deltaTime;
			moving = true;
		}
		else if(moving && state.equals(AnimationState.MOVE))
		{
			moving = false;
			moveCooldown = random.nextFloat() * 6;
		}
		
		if(moving && !state.equals(AnimationState.MOVE))
			setActiveAnimation(AnimationState.MOVE);
		if(!moving && state.equals(AnimationState.MOVE))
			setActiveAnimation(AnimationState.IDLE);
		
		if(!moving && state.equals(AnimationState.IDLE))
		{
			if(moveCooldown > 0)
				moveCooldown -= deltaTime;
			else
			{
				destination = random.nextFloat() * movementBounds.getY() * 2 + movementBounds.getX();
			}
		}
	}
	
	public void setDestination(double destination)
	{
		this.destination = destination;
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
	
	public enum AnimationState
	{
		IDLE,
		CHEER,
		MOVE
	}
}
