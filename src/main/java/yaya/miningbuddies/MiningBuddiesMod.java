package yaya.miningbuddies;

import net.fabricmc.api.ModInitializer;
import yaya.miningbuddies.Registries.BuddyManager;

public class MiningBuddiesMod implements ModInitializer
{
	public static String MOD_ID = "miningbuddies";
	
	@Override
	public void onInitialize()
	{
		new BuddyManager();
	}
}
