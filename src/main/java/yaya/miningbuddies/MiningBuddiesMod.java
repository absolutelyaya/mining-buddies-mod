package yaya.miningbuddies;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import yaya.miningbuddies.Commands.GiveBuddyCommand;
import yaya.miningbuddies.Registries.BuddyManager;
import yaya.miningbuddies.Settings.SettingsManager;

public class MiningBuddiesMod implements ModInitializer
{
	public static String MOD_ID = "miningbuddies";
	
	@Override
	public void onInitialize()
	{
		SettingsManager.load();
		new BuddyManager();
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> GiveBuddyCommand.register(dispatcher));
	}
}
