package yaya.miningbuddies.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import yaya.miningbuddies.GUI.BuddySelectionScreen;
import yaya.miningbuddies.GUI.Hud.BuddyMiniHud;
import yaya.miningbuddies.GUI.Hud.NewBuddyPopupHud;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class MiningBuddiesClientMod implements ClientModInitializer
{
	public static BuddyMiniHud BUDDY_MINI_HUD;
	public static NewBuddyPopupHud NEW_BUDDY_POPUP_HUD;
	public static Identifier MENU_BLOCK;
	
	@Override
	public void onInitializeClient()
	{
		BUDDY_MINI_HUD = new BuddyMiniHud();
		NEW_BUDDY_POPUP_HUD = new NewBuddyPopupHud();
		
		MENU_BLOCK = new Identifier("textures/block/moss_block.png");
		
		initKeybinds();
	}
	
	void initKeybinds()
	{
		KeyBinding keybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.miningbuddies.buddymenu",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_B,
				"category.miningbuddies.keybinds"
		));
		ClientTickEvents.END_CLIENT_TICK.register(client ->
		{
			while (keybind.wasPressed() && client.currentScreen == null)
			{
				client.setScreen(new BuddySelectionScreen(client));
			}
		});
	}
}
