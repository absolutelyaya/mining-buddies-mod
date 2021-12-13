package yaya.miningbuddies.client;

import net.fabricmc.api.ClientModInitializer;
import yaya.miningbuddies.GUI.Hud.BuddyMiniHud;
import yaya.miningbuddies.GUI.Hud.NewBuddyPopupHud;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class MiningBuddiesClientMod implements ClientModInitializer
{
	public static BuddyMiniHud BUDDY_MINI_HUD;
	public static NewBuddyPopupHud NEW_BUDDY_POPUP_HUD;
	
	@Override
	public void onInitializeClient()
	{
		BUDDY_MINI_HUD = new BuddyMiniHud();
		NEW_BUDDY_POPUP_HUD = new NewBuddyPopupHud();
	}
}
