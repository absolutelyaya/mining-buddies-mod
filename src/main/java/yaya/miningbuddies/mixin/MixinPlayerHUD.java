package yaya.miningbuddies.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yaya.miningbuddies.Settings.SettingsStorage;
import yaya.miningbuddies.client.MiningBuddiesClientMod;

@Mixin(InGameHud.class)
public class MixinPlayerHUD extends DrawableHelper
{
	@Shadow @Final private MinecraftClient client;
	
	long lastFrameTime;
	
	@Inject(method="render", at=@At("HEAD"))
	public void onRender(MatrixStack matrices, float tickDelta, CallbackInfo ci)
	{
		float deltaTime = (float)(System.currentTimeMillis() - lastFrameTime) / 1000;
		if(SettingsStorage.getBoolean("show-mini-buddy-hud"))
			MiningBuddiesClientMod.BUDDY_MINI_HUD.render(matrices, client, deltaTime);
		MiningBuddiesClientMod.NEW_BUDDY_POPUP_HUD.render(matrices, client, deltaTime);
		lastFrameTime = System.currentTimeMillis();
	}
}
