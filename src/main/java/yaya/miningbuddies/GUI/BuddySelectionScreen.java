package yaya.miningbuddies.GUI;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import yaya.miningbuddies.GUI.Widgets.BuddyListWidget;
import yaya.miningbuddies.Utilities.ColorUtil;
import yaya.miningbuddies.accessors.PlayerEntityAccessor;

@Environment(EnvType.CLIENT)
public class BuddySelectionScreen extends Screen
{
	final Text subtitle;
	
	BuddyListWidget list;
	MinecraftClient client;
	
	public BuddySelectionScreen(MinecraftClient client)
	{
		super(new TranslatableText("screen.miningbuddies.buddyselection.title"));
		subtitle = new TranslatableText("screen.miningbuddies.buddyselection.subtitle");
		this.client = client;
	}
	
	@Override
	protected void init()
	{
		list = new BuddyListWidget(client, width, height,  32, height - 32, 49, 133);
		if(client.player != null && client.getServer() != null)
		{
			ServerPlayerEntity player = client.getServer().getPlayerManager().getPlayer(client.player.getUuid());
			if(player != null)
				list.addAll(((PlayerEntityAccessor)player).getOwnedBuddies());
			this.addSelectableChild(list);
			this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20,
					ScreenTexts.DONE, (button) -> this.client.setScreen(null)));
		}
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
	{
		fill(matrices, 0, 32, width, height, this.client.options.getTextBackgroundColor(0.8F));
		list.render(matrices, mouseX, mouseY, delta);
		renderBorders();
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 6, ColorUtil.WHITE);
		drawCenteredText(matrices, this.textRenderer, this.subtitle, this.width / 2, 15, ColorUtil.GRAY);
	}
	
	public void renderBorders()
	{
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		RenderSystem.setShaderTexture(0, new Identifier("textures/block/moss_block.png"));
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		int col = 128;
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex(0, 32, 0).texture(0, 1).color(col, col, col, 255).next();
		bufferBuilder.vertex(this.width, 32, 0).texture((float)this.width / 32.0F, 1).color(col, col, col, 255).next();
		bufferBuilder.vertex(this.width, 0, 0).texture((float)this.width / 32.0F, 0).color(col, col, col, 255).next();
		bufferBuilder.vertex(0, 0, 0).texture(0, 0).color(col, col, col, 255).next();
		bufferBuilder.vertex(0, height, 0).texture(0, 0).color(col, col, col, 255).next();
		bufferBuilder.vertex(this.width, height, 0).texture((float)this.width / 32.0F, 0).color(col, col, col, 255).next();
		bufferBuilder.vertex(this.width, height - 32, 0).texture((float)this.width / 32.0F, 1).color(col, col, col, 255).next();
		bufferBuilder.vertex(0, height - 32, 0).texture(0, 1).color(col, col, col, 255).next();
		tessellator.draw();
	}
	
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount)
	{
		list.mouseScrolled(mouseX, mouseY, amount);
		return true;
	}
}
