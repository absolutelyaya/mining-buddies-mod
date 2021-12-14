package yaya.miningbuddies.Settings;

import net.minecraft.text.Text;
import yaya.miningbuddies.Utilities.TranslationUtil;

public abstract class AbstractSetting implements SettingsOption
{
	public String id, translationKey;
	
	public AbstractSetting(String id)
	{
		this.id = id;
		this.translationKey = TranslationUtil.getTranslationKey("setting", id);
	}
	
	public abstract Text getButtonText();
	
	public abstract String serialize();
}
