package yaya.miningbuddies.GUI.Widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.*;
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

import java.util.List;

@Environment(EnvType.CLIENT)
public class BuddyListWidget extends ElementListWidget<HorizontalListListEntry<BuddyListWidget.BuddyListEntry>>
{
	int itemWidth;
	
	public BuddyListWidget(MinecraftClient client, int width, int height, int top, int bottom, int entryHeight, int itemWidth)
	{
		super(client, width, height, top, bottom, entryHeight);
		this.itemWidth = itemWidth;
		setRenderBackground(false);
		setRenderHeader(false, 32);
		setRenderHorizontalShadows(false);
		this.centerListVertically = false;
	}
	
	public void addAll(List<Buddy> list)
	{
		int offsetX = itemWidth / 2 + getRowWidth() % itemWidth / 2;
		int objectsPerLine = (getRowWidth() - offsetX - 16) / itemWidth - 1;
		for (int line = 0; line < list.size() / objectsPerLine; line++)
		{
			HorizontalListListEntry<BuddyListEntry> hList = new HorizontalListListEntry<>(client, itemWidth, offsetX);
			for(int i = line * objectsPerLine; i < line * objectsPerLine + objectsPerLine + 1 && i < list.size(); i++)
			{
				hList.add(new BuddyListEntry(client, list.get(i)));
			}
			super.addEntry(hList);
		}
	}
	
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount)
	{
		super.mouseScrolled(mouseX, mouseY, amount);
		return true;
	}
	
	@Override
	public int getRowWidth()
	{
		return width;
	}
	
	@Override
	protected int getScrollbarPositionX()
	{
		return width - 16;
	}
	
	public static class BuddyListEntry extends Entry<BuddyListEntry>
	{
		MinecraftClient client;
		BuddyUIElement buddyUIE = new BuddyUIElement(new Vector2f(0, 0), false, false);
		Buddy buddy;
		TextFieldWidget nicknameField;
		ButtonWidget selectButton;
		
		int lastY = -1;
		
		public BuddyListEntry(MinecraftClient client, Buddy buddy)
		{
			this.client = client;
			this.buddy = buddy;
			buddyUIE.setBuddyType(buddy.getType());
			nicknameField = new TextFieldWidget(client.textRenderer, 42, 6, 61, 15, new LiteralText("a"));
			nicknameField.setText(buddy.getNickName());
			nicknameField.setChangedListener(this::renameBuddy);
			nicknameField.setUneditableColor(ColorUtil.WHITE);
			selectButton = new ButtonWidget(41, 22, 63, 17, new LiteralText(""),
					(button ->
					{
						if (client.player != null)
							((PlayerEntityAccessor) client.player).setActiveBuddy(buddy.getUuid());
					}));
		}
		
		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta)
		{
			float delta = tickDelta / 20;
			
			if(lastY != y)
				repositionSubWidgets(x, y);
			
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
		
		void repositionSubWidgets(int x, int y)
		{
			nicknameField.x = x + 42;
			nicknameField.y = y + 6;
			selectButton.x = x + 41;
			selectButton.y = y + 22;
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
			drawTexture(matrices, selectButton.x, selectButton.y, 65, 44 + 17 * i, selectButton.getWidth(), selectButton.getHeight(),
					128, 128);
			drawCenteredTextWithShadow(matrices, client.textRenderer,
					new TranslatableText(isActiveBuddy() ? "screen.miningbuddies.buddyselection.selected" :
												 "screen.miningbuddies.buddyselection.select").asOrderedText(),
					selectButton.x + 32, selectButton.y + 4, ColorUtil.WHITE);
		}
		
		boolean isActiveBuddy()
		{
			if (client.player != null)
				return buddy.getUuid().equals(((PlayerEntityAccessor)client.player).getActiveBuddy());
			else
				return false;
		}
		
		@Override
		public List<? extends Element> children()
		{
			return List.of(nicknameField, selectButton);
		}
		
		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button)
		{
			if(!nicknameField.isHovered() && nicknameField.isFocused())
				nicknameField.setTextFieldFocused(false);
			return super.mouseClicked(mouseX, mouseY, button);
		}
		
		void renameBuddy(String nick)
		{
			buddy.setNickName(nick);
		}
		
		@Override
		public List<? extends Selectable> selectableChildren()
		{
			return List.of(nicknameField, selectButton);
		}
	}
}
