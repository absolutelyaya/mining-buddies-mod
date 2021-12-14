package yaya.miningbuddies.GUI;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.text.OrderedText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import yaya.miningbuddies.GUI.Hud.BuddyUIElement;
import yaya.miningbuddies.Registries.BuddyManager;
import yaya.miningbuddies.Settings.Settings;
import yaya.miningbuddies.Settings.SettingsManager;

import java.util.List;
import java.util.Random;

@Environment(EnvType.CLIENT)
public class SettingsScreen extends GameOptionsScreen
{
	private final Screen previous;
	private final BuddyUIElement buddyUIE;
	
	private ButtonListWidget list;
	
	public SettingsScreen(Screen parent)
	{
		super(parent, MinecraftClient.getInstance().options, new TranslatableText("screen.miningbuddies.options.title"));
		previous = parent;
		List<Identifier> types = BuddyManager.getBuddyTypes();
		buddyUIE = new BuddyUIElement(new Vector2f(0, 0), false);
		if(types.size() > 0)
		{
			Random r = new Random();
			buddyUIE.setBuddyType(BuddyManager.getBuddyType(types.get(r.nextInt(types.size()))));
			buddyUIE.setActiveAnimation(BuddyUIElement.AnimationState.CHEER);
		}
	}
	
	@Override
	protected void init()
	{
		this.list = new ButtonListWidget(this.client, this.width, this.height, 32, this.height - 32, 25);
		this.list.addAll(Settings.getAsOptions());
		this.addSelectableChild(this.list);
		this.list.setRenderBackground(false);
		this.list.setRenderHorizontalShadows(false);
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20, ScreenTexts.DONE, (button) ->
		{
			SettingsManager.save();
			this.client.setScreen(this.previous);
		}));
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
	{
		renderBackgroundTexture(0);
		this.list.render(matrices, mouseX, mouseY, delta);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 10, 0xffffff);
		matrices.push();
		matrices.translate(this.width - 34, 2, 100);
		buddyUIE.render(matrices, delta);
		matrices.pop();
		super.render(matrices, mouseX, mouseY, delta);
		List<OrderedText> list = getHoveredButtonTooltip(this.list, mouseX, mouseY);
		if (list != null)
		{
			this.renderOrderedTooltip(matrices, list, mouseX, mouseY);
		}
	}
	
	@Override
	public void removed()
	{
		SettingsManager.save();
	}
	
	@Override
	public void renderBackgroundTexture(int vOffset)
	{
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.enableDepthTest();
		//BG
		int col = 64;
		RenderSystem.depthFunc(519);
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		RenderSystem.setShaderTexture(0, new Identifier("textures/block/moss_block.png"));
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex(0.0D, this.height, -100.0D).texture(0.0F, (float)this.height / 32.0F + (float)vOffset).color(col, col, col, 255).next();
		bufferBuilder.vertex(this.width, this.height, -100.0D).texture((float)this.width / 32.0F, (float)this.height / 32.0F + (float)vOffset).color(col, col, col, 255).next();
		bufferBuilder.vertex(this.width, 0.0D, -100.0D).texture((float)this.width / 32.0F, (float)vOffset).color(col, col, col, 255).next();
		bufferBuilder.vertex(0.0D, 0.0D, -100.0D).texture(0.0F, (float)vOffset).color(col, col, col, 255).next();
		tessellator.draw();
		//Header
		col = 128;
		RenderSystem.depthFunc(515);
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex(0, 32, 0).texture(0, 1 + (float)vOffset).color(col, col, col, 255).next();
		bufferBuilder.vertex(this.width, 32, 0).texture((float)this.width / 32.0F, 1 + (float)vOffset).color(col, col, col, 255).next();
		bufferBuilder.vertex(this.width, 0, 0).texture((float)this.width / 32.0F, vOffset).color(col, col, col, 255).next();
		bufferBuilder.vertex(0, 0, 0).texture(0, vOffset).color(col, col, col, 255).next();
		bufferBuilder.vertex(0, this.height, 0).texture(0, 1 + (float)vOffset).color(col, col, col, 255).next();
		bufferBuilder.vertex(this.width, this.height, 0).texture((float)this.width / 32.0F, 1 + (float)vOffset).color(col, col, col, 255).next();
		bufferBuilder.vertex(this.width, this.height - 32, 0).texture((float)this.width / 32.0F, vOffset).color(col, col, col, 255).next();
		bufferBuilder.vertex(0, this.height - 32, 0).texture(0, vOffset).color(col, col, col, 255).next();
		tessellator.draw();
		//Shadow
		RenderSystem.disableDepthTest();
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA,
				GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
		RenderSystem.disableTexture();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(0, 32 + 4, 0.0D).color(0, 0, 0, 0).next();
		bufferBuilder.vertex(this.width, 32 + 4, 0.0D).color(0, 0, 0, 0).next();
		bufferBuilder.vertex(this.width, 32, 0.0D).color(0, 0, 0, 255).next();
		bufferBuilder.vertex(0, 32, 0.0D).color(0, 0, 0, 255).next();
		bufferBuilder.vertex(0,this.height - 32, 0.0D).color(0, 0, 0, 255).next();
		bufferBuilder.vertex(this.width,this.height - 32, 0.0D).color(0, 0, 0, 255).next();
		bufferBuilder.vertex(this.width, this.height - 36, 0.0D).color(0, 0, 0, 0).next();
		bufferBuilder.vertex(0, this.height - 36, 0.0D).color(0, 0, 0, 0).next();
		tessellator.draw();
	}
}
