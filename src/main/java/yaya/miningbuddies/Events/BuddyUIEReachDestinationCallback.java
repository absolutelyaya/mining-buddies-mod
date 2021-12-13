package yaya.miningbuddies.Events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;
import yaya.miningbuddies.GUI.Hud.BuddyUIElement;

public interface BuddyUIEReachDestinationCallback
{
	Event<BuddyUIEReachDestinationCallback> EVENT = EventFactory.createArrayBacked(BuddyUIEReachDestinationCallback.class,
			(listeners) -> (uiElement) ->
			{
				for (BuddyUIEReachDestinationCallback listener : listeners)
				{
					ActionResult result = listener.interact(uiElement);
					if(result != ActionResult.PASS)
						return result;
				}
				return ActionResult.PASS;
			});
	
	ActionResult interact(BuddyUIElement uiElement);
}
