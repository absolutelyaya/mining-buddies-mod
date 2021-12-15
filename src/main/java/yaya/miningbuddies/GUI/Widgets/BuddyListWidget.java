package yaya.miningbuddies.GUI.Widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import yaya.miningbuddies.Buddies.Buddy;
import yaya.miningbuddies.GUI.Hud.BuddyUIElement;
import yaya.miningbuddies.MiningBuddiesMod;
import yaya.miningbuddies.accessors.PlayerEntityAccessor;

import java.util.ArrayList;
import java.util.List;

public class BuddyListWidget extends DrawableHelper implements Element, Selectable
{
	final MinecraftClient client;
	final int x, y, width, height, verticalSpacing, horizontalSpacing;
	
	List<BuddyListEntry> entries = new ArrayList<>();
	int nextX, nextY = 0;
	
	public BuddyListWidget(MinecraftClient client, int x, int y, int width, int height, int horizontalSpacing, int verticalSpacing)
	{
		this.client = client;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.horizontalSpacing = horizontalSpacing;
		this.verticalSpacing = verticalSpacing;
	}
	
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
	{
		matrices.push();
		for (BuddyListEntry entry: entries)
		{
			entry.render(matrices, mouseX, mouseY, delta / 20);
		}
		matrices.pop();
	}
	
	public void addAll(List<Buddy> list)
	{
		nextX = x;
		nextY = y + verticalSpacing;
		for (Buddy b : list)
		{
			entries.add(new BuddyListEntry(client, nextX, nextY, b));
			nextX += 36 + horizontalSpacing;
			if(nextX > width)
			{
				nextY += 36 + verticalSpacing;
				nextX = 0;
			}
		}
	}
	
	@Override
	public SelectionType getType()
	{
		return SelectionType.HOVERED;
	}
	
	@Override
	public void appendNarrations(NarrationMessageBuilder builder)
	{
		///TODO: narration?
	}
	
	public static class BuddyListEntry extends ButtonWidget
	{
		BuddyUIElement buddyUIE = new BuddyUIElement(new Vector2f(0, 0), false);
		Buddy buddy;
		
		public BuddyListEntry(MinecraftClient client, int x, int y, Buddy buddy)
		{
			//Click doesn't actually register. Gotta fix that tomorrow
			super(x, y, 36, 36, new LiteralText(buddy.getNickName()),
					(button ->
					{
						ServerPlayerEntity player = client.getServer().getPlayerManager().getPlayer(client.player.getUuid());
						((PlayerEntityAccessor)player).setActiveBuddy(buddy.getUuid());
					}));
			this.buddy = buddy;
			buddyUIE.setBuddyType(buddy.getType());
		}
		
		@Override
		public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
		{
			super.render(matrices, mouseX, mouseY, delta);
			matrices.push();
			renderButton(matrices, mouseX, mouseY, delta);
			
			if(this.isMouseOver(mouseX, mouseY))
				buddyUIE.setActiveAnimation(BuddyUIElement.AnimationState.CHEER);
			else
				buddyUIE.setActiveAnimation(BuddyUIElement.AnimationState.IDLE);
			
			matrices.translate(x + 2, y + 2, 0);
			buddyUIE.render(matrices, delta);
			matrices.pop();
		}
		
		public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta)
		{
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, new Identifier(MiningBuddiesMod.MOD_ID, "textures/gui/square_button.png"));
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
			int i = this.getYImage(this.isMouseOver(mouseX, mouseY));
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			drawTexture(matrices, this.x, this.y, 0, (i - 1) * 36, this.width / 2, this.height, 36, 72);
			drawTexture(matrices, this.x + this.width / 2, this.y, this.width / 2f, (i - 1) * 36, this.width / 2, this.height,
					36, 72);
		}
	}
}
