package yaya.miningbuddies.Utilities;

import yaya.miningbuddies.MiningBuddiesMod;

public class TranslationUtil
{
	public static String getTranslationKey(String key, String id)
	{
		return key + "." + MiningBuddiesMod.MOD_ID + "." + id;
	}
}
