package yaya.miningbuddies.Settings;

import net.fabricmc.loader.api.FabricLoader;
import yaya.miningbuddies.MiningBuddiesMod;

import java.io.*;

public class SettingsManager
{
	private static File file;
	
	private static void prepareConfigFile() {
		if (file != null) {
			return;
		}
		file = new File(FabricLoader.getInstance().getConfigDir().toFile(), MiningBuddiesMod.MOD_ID + ".txt");
	}
	
	public static void load()
	{
		prepareConfigFile();
		
		Settings.applyDefaults();
		
		try
		{
			if(!file.exists())
				save();
			if(file.exists())
			{
				BufferedReader br = new BufferedReader(new FileReader(file));
				for (String line : br.lines().toList())
				{
					if(line.contains("#") && line.contains(":"))
					{
						String[] type = line.split("#");
						String[] id = type[1].split(":");
						String value = id[1];
						switch(type[0])
						{
							case "E" -> SettingsStorage.setEnum(id[0], EnumSetting.deserialize(value, id[0]));
							case "B" -> SettingsStorage.setBoolean(id[0], Boolean.parseBoolean(value));
							case "D" -> SettingsStorage.setDouble(id[0], Double.parseDouble(value));
						}
					}
				}
				br.close();
			}
		}
		catch (IOException e)
		{
			System.err.println("Failed to load Mining Buddies settings file. Reverting to defaults");
			e.printStackTrace();
		}
	}
	
	public static void save()
	{
		prepareConfigFile();
		
		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			for (AbstractSetting setting : Settings.SETTINGS)
			{
				bw.write(setting.serialize());
				bw.newLine();
			}
			bw.close();
		}
		catch (IOException e)
		{
			System.err.println("Failed to save Mining Buddies settings file.");
			e.printStackTrace();
		}
	}
}
