package yaya.miningbuddies.GUI.Widgets;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.text.Text;
import yaya.miningbuddies.Buddies.Buddy;
import yaya.miningbuddies.GUI.Hud.BuddyUIElement;

public class BuddyListEntryWidget extends ButtonWidget
{
	BuddyUIElement buddyUIE = new BuddyUIElement(new Vector2f(0, 0), false);
	Buddy buddy;
	
	public BuddyListEntryWidget(int x, int y, Text message, PressAction onPress, Buddy buddy)
	{
		super(x, y, 36, 36, message, onPress);
		this.buddy = buddy;
		buddyUIE.setBuddyType(buddy.getType());
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
	{
		super.render(matrices, mouseX, mouseY, delta);
		matrices.push();
		matrices.translate(2, 2, 0);
		renderButton(matrices, mouseX, mouseY, delta);
		matrices.pop();
		
		if(isHovered() && buddyUIE.getState() != BuddyUIElement.AnimationState.CHEER)
			buddyUIE.setActiveAnimation(BuddyUIElement.AnimationState.CHEER);
		else if(buddyUIE.getState() != BuddyUIElement.AnimationState.IDLE)
			buddyUIE.setActiveAnimation(BuddyUIElement.AnimationState.IDLE);
		
		buddyUIE.render(matrices, delta);
	}
}
