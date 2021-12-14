package yaya.miningbuddies;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import yaya.miningbuddies.GUI.SettingsScreen;

public class ModMenu implements ModMenuApi
{
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory()
	{
		return SettingsScreen::new;
	}
}
