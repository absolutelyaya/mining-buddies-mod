package yaya.miningbuddies.GUI.Hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.util.Identifier;
import yaya.miningbuddies.Buddies.BuddyType;
import yaya.miningbuddies.MiningBuddiesMod;
import yaya.miningbuddies.Settings.Settings;
import yaya.miningbuddies.Settings.SettingsStorage;

@Environment(EnvType.CLIENT)
public class BuddyMiniHud extends DrawableHelper
{
	BuddyUIElement buddyUIE;
	
	public BuddyMiniHud()
	{
		buddyUIE = new BuddyUIElement(new Vector2f(-16, 16), true, true);
	}
	
	public void render(MatrixStack matrices, MinecraftClient client, float deltaTime)
	{
		float scaledHeight = client.getWindow().getScaledHeight();
		float scaledWidth = client.getWindow().getScaledWidth();
		matrices.push();
		switch(SettingsStorage.getEnum("mini-buddy-hud-pos", Settings.PositionEnum.class))
		{
			case TOP_LEFT -> matrices.translate(8, 8, -500);
			case BOTTOM_LEFT -> matrices.translate(8, scaledHeight - 64 - 8, -500);
			case TOP_RIGHT ->matrices.translate(scaledWidth - 64 - 8, 8, -500);
			case BOTTOM_RIGHT -> matrices.translate(scaledWidth - 64 - 8, scaledHeight - 64 - 8, -500);
		}
		RenderSystem.setShaderColor(1f, 1f, 1f, (float)SettingsStorage.getDouble("mini-buddy-hud-bg-alpha"));
		RenderSystem.enableBlend();
		RenderSystem.setShaderTexture(0, new Identifier(MiningBuddiesMod.MOD_ID, "textures/backgrounds/bg1.png"));
		drawTexture(matrices, 0, 0, 0, 0, 64, 64, 64, 64);
		if (buddyUIE != null)
		{
			matrices.translate(16, 16, 1);
			buddyUIE.render(matrices, deltaTime);
		}
		matrices.pop();
	}
	
	public void setBuddyType(BuddyType buddyType)
	{
		buddyUIE.setBuddyType(buddyType);
	}
}
