package yaya.miningbuddies.client;

import net.fabricmc.api.ClientModInitializer;
import yaya.miningbuddies.Hud.BuddyMiniHud;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class MiningBuddiesClientMod implements ClientModInitializer
{
	public static BuddyMiniHud BUDDY_MINI_HUD;
	
	@Override
	public void onInitializeClient()
	{
		BUDDY_MINI_HUD = new BuddyMiniHud();
	}
}
