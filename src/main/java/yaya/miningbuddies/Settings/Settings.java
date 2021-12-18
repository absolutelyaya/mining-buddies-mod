package yaya.miningbuddies.Settings;

import com.google.gson.annotations.SerializedName;
import net.minecraft.client.option.Option;

import java.util.ArrayList;
import java.util.List;

public class Settings
{
	public static final EnumSetting<PositionEnum> MINI_BUDDY_HUD_POS = new EnumSetting<>("mini-buddy-hud-pos", PositionEnum.TOP_LEFT);
	public static final BooleanSetting SHOW_MINI_BUDDY_HUD = new BooleanSetting("show-mini-buddy-hud", true);
	public static final SliderSetting MINI_BUDDY_HUD_BG_ALPHA = new SliderSetting("mini-buddy-hud-bg-alpha", 1D, 0D, 1D, 1f / 256f);
	public static final BooleanSetting SHOW_NEW_BUDDY_POPUP = new BooleanSetting("show-new-buddy-popup", true);
	
	static final List<AbstractSetting> SETTINGS = new ArrayList<>();
	
	static
	{
		SETTINGS.add(MINI_BUDDY_HUD_POS);
		SETTINGS.add(SHOW_MINI_BUDDY_HUD);
		SETTINGS.add(MINI_BUDDY_HUD_BG_ALPHA);
		SETTINGS.add(SHOW_NEW_BUDDY_POPUP);
	}
	
	public static Option[] getAsOptions()
	{
		List<Option> options = new ArrayList<>();
		for (SettingsOption so : SETTINGS)
		{
			options.add(so.asOption());
		}
		return options.toArray(Option[]::new);
	}
	
	public static void applyDefaults()
	{
		for(AbstractSetting as : SETTINGS)
		{
			if(as instanceof EnumSetting<?>)
				SettingsStorage.setEnum(as.id, ((EnumSetting<?>)as).getDefaultValue());
			else if(as instanceof BooleanSetting)
				SettingsStorage.setBoolean(as.id, ((BooleanSetting)as).getDefaultValue());
			else if(as instanceof SliderSetting)
				SettingsStorage.setDouble(as.id, ((SliderSetting)as).getDefaultValue());
		}
	}
	
	public enum PositionEnum
	{
		@SerializedName("top_left")
		TOP_LEFT,
		@SerializedName("top_right")
		TOP_RIGHT,
		@SerializedName("bottom_left")
		BOTTOM_LEFT,
		@SerializedName("bottom_right")
		BOTTOM_RIGHT
	}
}
