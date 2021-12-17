package yaya.miningbuddies.GUI.Widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
import yaya.miningbuddies.Buddies.Buddy;
import yaya.miningbuddies.GUI.Hud.BuddyUIElement;
import yaya.miningbuddies.MiningBuddiesMod;
import yaya.miningbuddies.Utilities.ColorUtil;
import yaya.miningbuddies.accessors.PlayerEntityAccessor;

import java.util.ArrayList;
import java.util.List;

public class BuddyListWidget extends DrawableHelper implements Element, Selectable
{
	final MinecraftClient client;
	final int x, y, width, height, verticalSpacing, horizontalSpacing;
	
	List<BuddyListEntry> entries = new ArrayList<>();
	int nextX, nextY, startX;
	
	public BuddyListWidget(MinecraftClient client, int x, int y, int width, int height, int horizontalSpacing, int verticalSpacing)
	{
		this.client = client;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.horizontalSpacing = horizontalSpacing;
		this.verticalSpacing = verticalSpacing;
		
		startX = x + (width % (128 + horizontalSpacing)) / 2;
		nextX = startX;
		nextY = y + verticalSpacing;
	}
	
	public void addAll(List<Buddy> list)
	{
		for (Buddy b : list)
		{
			entries.add(new BuddyListEntry(client, nextX, nextY, b));
			nextX += 128 + horizontalSpacing;
			if(nextX + 128 - horizontalSpacing > width + (width % (128 + horizontalSpacing)) / 2)
			{
				nextY += 44 + verticalSpacing;
				nextX = startX;
			}
		}
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
	
	@SuppressWarnings("unchecked")
	public <T extends Element & Selectable> List<T> getSelectables()
	{
		List<T> list = new ArrayList<>();
		for (BuddyListEntry entry : entries)
		{
			list.add((T)entry.selectButton);
			list.add((T)entry.nicknameField);
		}
		return list;
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
	
	public static class BuddyListEntry implements Element, Selectable
	{
		BuddyUIElement buddyUIE = new BuddyUIElement(new Vector2f(0, 0), false);
		Buddy buddy;
		MinecraftClient client;
		TextFieldWidget nicknameField;
		ButtonWidget selectButton;
		int x, y;
		
		public BuddyListEntry(MinecraftClient client, int x, int y, Buddy buddy)
		{
			this.client = client;
			this.buddy = buddy;
			this.x = x;
			this.y = y;
			buddyUIE.setBuddyType(buddy.getType());
			nicknameField = new TextFieldWidget(client.textRenderer, x + 42, y + 6, 61, 15, new LiteralText(buddy.getNickName()));
			nicknameField.setText(buddy.getNickName());
			nicknameField.setChangedListener(this::renameBuddy);
			nicknameField.setUneditableColor(ColorUtil.WHITE);
			selectButton = new ButtonWidget(x + 41, y + 22, 63, 17, new LiteralText(""),
					(button ->
					{
						if (client.player != null)
							((PlayerEntityAccessor) client.player).setActiveBuddy(buddy.getUuid());
					}));
		}
		
		public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
		{
			matrices.push();
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, new Identifier(MiningBuddiesMod.MOD_ID, "textures/gui/buddy_selection.png"));
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			drawTexture(matrices, x, y, 0, 0, 128, 44, 128, 128);
			renderButton(matrices, mouseX, mouseY, delta);
			nicknameField.setEditable(nicknameField.isHovered() || nicknameField.isFocused());
			nicknameField.setDrawsBackground(nicknameField.isHovered() || nicknameField.isFocused());
			if(!nicknameField.isHovered() && !nicknameField.isFocused())
				matrices.translate(3, 3, 0);
			nicknameField.render(matrices, mouseX, mouseY, delta);
			
			if(isActiveBuddy())
				buddyUIE.setActiveAnimation(BuddyUIElement.AnimationState.CHEER);
			else
				buddyUIE.setActiveAnimation(BuddyUIElement.AnimationState.IDLE);
			
			matrices.pop();
			matrices.push();
			matrices.translate(x + 6, y + 5, 0);
			buddyUIE.render(matrices, delta);
			RenderSystem.enableBlend();
			matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90));
			client.textRenderer.draw(matrices, "TODO", 5, -112, ColorUtil.DARK_PURPLE);
			matrices.pop();
		}
		
		public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta)
		{
			matrices.push();
			matrices.scale(0, 0, 0);
			selectButton.render(matrices, mouseX, mouseY, delta); //rendering to get if the button is hovered
			selectButton.active = !isActiveBuddy();
			matrices.pop();
			RenderSystem.setShaderTexture(0, new Identifier(MiningBuddiesMod.MOD_ID, "textures/gui/buddy_selection.png"));
			int i = selectButton.active ? (selectButton.isHovered() ? 2 : 1) : 0;
			drawTexture(matrices, this.x + 41, this.y + 22, 65, 44 + 17 * i, selectButton.getWidth(), selectButton.getHeight(),
					128, 128);
			drawCenteredTextWithShadow(matrices, client.textRenderer,
					new TranslatableText(isActiveBuddy() ? "screen.miningbuddies.buddyselection.selected" :
												 "screen.miningbuddies.buddyselection.select").asOrderedText(),
					x + 72, y + 27, ColorUtil.WHITE);
		}
		
		boolean isActiveBuddy()
		{
			if (client.player != null)
				return buddy.getUuid().equals(((PlayerEntityAccessor)client.player).getActiveBuddy());
			else
				return false;
		}
		
		@Override
		public SelectionType getType()
		{
			return SelectionType.NONE;
		}
		
		@Override
		public void appendNarrations(NarrationMessageBuilder builder)
		{
			///TODO: narration?
		}
		
		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button)
		{
			if(!nicknameField.isHovered() && nicknameField.isFocused())
				nicknameField.setTextFieldFocused(false);
			return Element.super.mouseClicked(mouseX, mouseY, button);
		}
		
		void renameBuddy(String nick)
		{
			buddy.setNickName(nick);
		}
	}
}
