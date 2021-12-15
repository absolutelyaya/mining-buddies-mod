package yaya.miningbuddies.GUI;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import yaya.miningbuddies.GUI.Widgets.BuddyListWidget;
import yaya.miningbuddies.accessors.PlayerEntityAccessor;

public class BuddySelectionScreen extends Screen
{
	BuddyListWidget list;
	MinecraftClient client;
	
	public BuddySelectionScreen(MinecraftClient client)
	{
		super(new TranslatableText("screen.miningbuddies.buddyselection.title"));
		this.client = client;
	}
	
	@Override
	protected void init()
	{
		list = new BuddyListWidget(client, 36, 32,  width - 72, height - 32, 5, 5);
		ServerPlayerEntity player = client.getServer().getPlayerManager().getPlayer(client.player.getUuid());
		list.addAll(((PlayerEntityAccessor)player).getOwnedBuddies());
		this.addSelectableChild(list);
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20,
				ScreenTexts.DONE, (button) -> this.client.setScreen(null)));
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
	{
		renderBackground(matrices);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 10, 0xffffff);
		list.render(matrices, mouseX, mouseY, delta);
	}
	
	@Override
	public void renderBackground(MatrixStack matrices)
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
		tessellator.draw();
		
		//probably to be removed
		/*matrices.push();
		matrices.translate(this.width / 2D, this.height / 2D, 0);
		matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90));
		matrices.translate(-this.height / 2D + 32, -this.width / 2D, 0);
		this.fillGradient(matrices, 0, 0, height - 32, width / 2,
				ColorUtil.getColorFromRGBA(0,0,0,255), ColorUtil.getColorFromRGBA(0,0,0,0));
		matrices.translate(0, this.width / 2D, 0);
		this.fillGradient(matrices, 0, 0, height - 32, width / 2,
				ColorUtil.getColorFromRGBA(0,0,0,0), ColorUtil.getColorFromRGBA(0,0,0,255));
		matrices.pop();*/
	}
}
