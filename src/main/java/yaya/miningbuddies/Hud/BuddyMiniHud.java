package yaya.miningbuddies.Hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import yaya.miningbuddies.Buddies.Buddy;
import yaya.miningbuddies.MiningBuddiesMod;
import yaya.miningbuddies.accessors.PlayerEntityAccessor;

public class BuddyMiniHud extends DrawableHelper
{
	public void render(MatrixStack matrices, MinecraftClient client)
	{
		float scaledHeight = client.getWindow().getScaledHeight();
		float scaledWidth = client.getWindow().getScaledWidth();
		matrices.push();
		matrices.translate(8, scaledHeight - 64 - 8, -500);
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		RenderSystem.setShaderTexture(0, new Identifier(MiningBuddiesMod.MOD_ID, "textures/backgrounds/bg1.png"));
		drawTexture(matrices, 0, 0, 0, 0, 64, 64, 64, 64);
		Buddy b = ((PlayerEntityAccessor)client.getServer().getPlayerManager().getPlayer(client.player.getUuid())).getActiveBuddy();
		if (b != null)
		{
			matrices.translate(16, 16, 1);
			RenderSystem.setShaderTexture(0,
					new Identifier(b.getIdentifier().getNamespace(), "textures/buddies/" + b.getIdentifier().getPath() + ".png"));
			drawTexture(matrices, 0, 0, 0, 0, 32, 32, 32, 32);
		}
		matrices.pop();
	}
	
	///TODO: make buddies move/animated
	///TODO: make buddies react to stuff (like finding diamonds)
}
