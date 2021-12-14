package yaya.miningbuddies.GUI.Widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

import java.util.Random;

import static java.lang.Math.abs;

public class FlowerWidget extends ButtonWidget
{
	Random random = new Random();
	float rot;
	float targetRot;
	
	public FlowerWidget(int x, int y, int width, int height, PressAction action)
	{
		super(x, y, width, height, null, action);
		rot = random.nextFloat() * 360f;
		targetRot = rot;
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
	{
		if(abs(rot - targetRot) > 0.01f)
			rot = rot + (targetRot > rot ? 1 : -1) * abs(rot - targetRot) * delta / 2;
		matrices.push();
		matrices.translate(x, y, 666);
		matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(rot));
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, new Identifier("textures/block/spore_blossom_base.png"));
		RenderSystem.setShaderColor(1, 1, 1, 1);
		drawTexture(matrices, -width / 2, -height / 2, 0, 0, width, height, width, height);
		matrices.translate(0, 0, 1);
		matrices.scale(0.66f, 0.66f, 0.66f);
		RenderSystem.setShaderTexture(0, new Identifier("textures/block/spore_blossom.png"));
		drawTexture(matrices, -width / 2, -height, 0, 0, width, height, width, height);
		matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90));
		drawTexture(matrices, -width / 2, -height, 0, 0, width, height, width, height);
		matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90));
		drawTexture(matrices, -width / 2, -height, 0, 0, width, height, width, height);
		matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90));
		drawTexture(matrices, -width / 2, -height, 0, 0, width, height, width, height);
		matrices.pop();
	}
	
	@Override
	protected boolean clicked(double mouseX, double mouseY)
	{
		if(isMouseOver(mouseX, mouseY))
			rotateRandom();
		return super.clicked(mouseX, mouseY);
	}
	
	@Override
	public boolean isMouseOver(double mouseX, double mouseY)
	{
		return this.active && this.visible && mouseX >= (double)this.x - width / 2f && mouseY >= (double)this.y - height / 2f &&
					   mouseX < (double)(this.x + this.width / 2) && mouseY < (double)(this.y + this.height / 2);
	}
	
	public void rotateRandom()
	{
		targetRot = rot + random.nextFloat() * 90f - 45f;
	}
}
