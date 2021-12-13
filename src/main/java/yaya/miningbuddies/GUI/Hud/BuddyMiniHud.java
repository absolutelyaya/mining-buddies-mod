package yaya.miningbuddies.GUI.Hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.util.Identifier;
import yaya.miningbuddies.Buddies.BuddyType;
import yaya.miningbuddies.MiningBuddiesMod;

public class BuddyMiniHud extends DrawableHelper
{
	BuddyUIElement buddyWidget;
	
	public BuddyMiniHud()
	{
		buddyWidget = new BuddyUIElement(new Vector2f(-16, 16));
	}
	
	public void render(MatrixStack matrices, MinecraftClient client, float deltaTime)
	{
		float scaledHeight = client.getWindow().getScaledHeight();
		float scaledWidth = client.getWindow().getScaledWidth();
		matrices.push();
		matrices.translate(8, scaledHeight - 64 - 8, -500);
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		RenderSystem.setShaderTexture(0, new Identifier(MiningBuddiesMod.MOD_ID, "textures/backgrounds/bg1.png"));
		drawTexture(matrices, 0, 0, 0, 0, 64, 64, 64, 64);
		if (buddyWidget != null)
		{
			matrices.translate(16, 16, 1);
			buddyWidget.render(matrices, deltaTime);
		}
		matrices.pop();
	}
	
	public void setBuddyType(BuddyType buddyType)
	{
		buddyWidget.setBuddyType(buddyType);
	}
	
	///TODO: make positioning modifiable
	///TODO: make buddies react to stuff (like finding diamonds)
}
