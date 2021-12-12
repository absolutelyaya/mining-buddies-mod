package yaya.miningbuddies.GUI.Hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.util.Identifier;
import yaya.miningbuddies.Buddies.Animation;
import yaya.miningbuddies.Buddies.BuddyType;
import yaya.miningbuddies.MiningBuddiesMod;

public class BuddyMiniHud extends DrawableHelper
{
	float frameTime;
	int frame;
	Animation activeAnimation;
	
	BuddyType buddyType;
	
	public void render(MatrixStack matrices, MinecraftClient client, float deltaTime)
	{
		frameTime += deltaTime;
		if(activeAnimation != null && frameTime > activeAnimation.interval)
		{
			frame++;
			frameTime = 0;
			if(frame > activeAnimation.frames)
				frame = 0;
		}
		float scaledHeight = client.getWindow().getScaledHeight();
		float scaledWidth = client.getWindow().getScaledWidth();
		matrices.push();
		matrices.translate(8, scaledHeight - 64 - 8, -500);
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		RenderSystem.setShaderTexture(0, new Identifier(MiningBuddiesMod.MOD_ID, "textures/backgrounds/bg1.png"));
		drawTexture(matrices, 0, 0, 0, 0, 64, 64, 64, 64);
		if (buddyType != null)
		{
			matrices.translate(16, 16, 1);
			RenderSystem.setShaderTexture(0,
					new Identifier(buddyType.getID().getNamespace(), "textures/buddies/" + buddyType.getID().getPath() + ".png"));
			Vector2f textureSize = buddyType.getTextureSize();
			Vector2f buddySize = buddyType.getBuddySize();
			drawTexture(matrices, 0, 0, frame * buddySize.getX(), activeAnimation.index * buddySize.getY(),
					32, 32, (int)textureSize.getX(), (int)textureSize.getY());
		}
		matrices.pop();
	}
	
	public void setBuddyType(BuddyType buddyType)
	{
		this.frameTime = 0;
		this.buddyType = buddyType;
		setActiveAnimation(AnimationState.IDLE);
	}
	
	public void setActiveAnimation(AnimationState state)
	{
		this.frameTime = 0;
		if(buddyType != null)
			this.activeAnimation = buddyType.getAnimation(state.name().toLowerCase());
	}
	
	///TODO: make positioning modifiable
	///TODO: make buddies move
	///TODO: make buddies react to stuff (like finding diamonds)
	
	public enum AnimationState
	{
		IDLE,
		CHEER,
		MOVE
	}
}
