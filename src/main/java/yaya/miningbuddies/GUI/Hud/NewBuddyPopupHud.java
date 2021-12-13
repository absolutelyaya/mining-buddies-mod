package yaya.miningbuddies.GUI.Hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import yaya.miningbuddies.Buddies.BuddyType;
import yaya.miningbuddies.Events.BuddyUIEReachDestinationCallback;
import yaya.miningbuddies.MiningBuddiesMod;
import yaya.miningbuddies.Utilities.ColorUtil;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NewBuddyPopupHud extends DrawableHelper
{
	public Queue<BuddyType> queue = new ConcurrentLinkedQueue<>();
	float animTime = -1;
	BuddyType currentlyShowing;
	BuddyUIElement buddyUIE;
	
	public NewBuddyPopupHud()
	{
		buddyUIE = new BuddyUIElement(new Vector2f(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY), false);
		buddyUIE.setSpeedMultiplier(3);
		BuddyUIEReachDestinationCallback.EVENT.register((b) ->
		{
			if(b == buddyUIE)
			{
				animTime = 0;
				buddyUIE.setActiveAnimation(BuddyUIElement.AnimationState.CHEER);
				return ActionResult.PASS;
			}
			return ActionResult.FAIL;
		});
	}
	
	public void render(MatrixStack matrices, MinecraftClient client, float deltaTime)
	{
		float scaledHeight = client.getWindow().getScaledHeight();
		float scaledWidth = client.getWindow().getScaledWidth();
		
		if(currentlyShowing == null && queue.size() > 0)
		{
			currentlyShowing = queue.poll();
			buddyUIE.setBuddyType(currentlyShowing);
			buddyUIE.setPos(scaledWidth / 3 + 32);
			buddyUIE.setDestination(-16);
			buddyUIE.setAlpha(1);
		}
		if(currentlyShowing != null)
		{
			if(animTime >= 0)
			{
				matrices.push();
				matrices.translate(scaledWidth / 2, scaledHeight / 6, 99);
				matrices.scale(1.5f, 1.5f, 1.5f);
				RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
				RenderSystem.setShaderTexture(0, new Identifier(MiningBuddiesMod.MOD_ID, "textures/gui/newbuddy/default.png"));
				drawTexture(matrices, -17, -1, 15, 15, 34, 34, 64, 64);
				drawTexture(matrices, -32, 32, 0, 0, 64, 11, 64, 64);
				drawCenteredTextWithShadow(matrices, client.textRenderer, OrderedText.styledForwardsVisitedString("New Buddy!", Style.EMPTY),
						0, -16, ColorUtil.GOLD);
				matrices.scale(0.66f, 0.66f, 0.66f);
				drawCenteredTextWithShadow(matrices, client.textRenderer, OrderedText.styledForwardsVisitedString(currentlyShowing.name, Style.EMPTY),
						0, 54, ColorUtil.DARK_RED);
				matrices.pop();
				animTime += deltaTime;
			}
			matrices.push();
			matrices.translate(scaledWidth / 2, scaledHeight / 6, 100);
			matrices.scale(1.5f, 1.5f, 1.5f);
			if(buddyUIE.buddyType != null)
				buddyUIE.render(matrices, deltaTime);
			matrices.pop();
			if(animTime > 5)
			{
				currentlyShowing = null;
				buddyUIE.setBuddyType(null);
				buddyUIE.setActiveAnimation(BuddyUIElement.AnimationState.IDLE);
				animTime = -1f;
			}
		}
	}
	
	public void onGetNewBuddy(BuddyType type)
	{
		if(type != null)
		{
			queue.offer(type);
		}
	}
}
