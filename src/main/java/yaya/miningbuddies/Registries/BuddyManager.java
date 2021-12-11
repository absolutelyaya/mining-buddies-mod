package yaya.miningbuddies.Registries;

import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yaya.miningbuddies.Buddies.Buddy;

import java.util.Map;

public class BuddyManager extends JsonDataLoader
{
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
	private static Map<Identifier, Buddy> buddies = ImmutableMap.of();
	private boolean errored;
	
	public BuddyManager()
	{
		super(GSON, "buddies");
	}
	
	@Override
	protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler)
	{
		this.errored = false;
		ImmutableMap.Builder<Identifier, Buddy> builder = ImmutableMap.builder();
		
		for (Map.Entry<Identifier, JsonElement> identifierJsonElementEntry : prepared.entrySet())
		{
			Identifier identifier = identifierJsonElementEntry.getKey();
			
			try
			{
				Buddy buddy = deserialize(JsonHelper.asObject(identifierJsonElementEntry.getValue(), "top element"), identifier);
				builder.put(identifier, buddy);
			}
			catch (IllegalArgumentException | JsonParseException e)
			{
				LOGGER.error("Parsing error loading buddy {}", identifier, e);
			}
		}
		buddies = builder.build();
		LOGGER.info("Loaded {} Buddies!", buddies.size());
	}
	
	public boolean isErrored() {
		return this.errored;
	}
	
	public static Buddy getBuddy(Identifier id)
	{
		return buddies.get(id);
	}
	
	public static Buddy deserialize(JsonObject json, Identifier identifier)
	{
		return new Buddy(JsonHelper.getString(json, "name"), identifier);
	}
}
