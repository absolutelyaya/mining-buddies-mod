package yaya.miningbuddies.Registries;

import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yaya.miningbuddies.Buddies.Animation;
import yaya.miningbuddies.Buddies.BuddyType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuddyManager extends JsonDataLoader
{
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
	private static Map<Identifier, BuddyType> buddies = ImmutableMap.of();
	private boolean errored;
	
	public BuddyManager()
	{
		super(GSON, "buddies");
	}
	
	@Override
	protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler)
	{
		this.errored = false;
		ImmutableMap.Builder<Identifier, BuddyType> builder = ImmutableMap.builder();
		
		for (Map.Entry<Identifier, JsonElement> identifierJsonElementEntry : prepared.entrySet())
		{
			Identifier identifier = identifierJsonElementEntry.getKey();
			
			try
			{
				BuddyType type = deserialize(JsonHelper.asObject(identifierJsonElementEntry.getValue(), "top element"), identifier);
				builder.put(identifier, type);
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
	
	public static BuddyType getBuddyType(Identifier id)
	{
		System.out.println(id.toString());
		return buddies.get(id);
	}
	
	public static List<Identifier> getBuddyTypes()
	{
		return new ArrayList<>(buddies.keySet());
	}
	
	public static BuddyType deserialize(JsonObject json, Identifier identifier)
	{
		String name = JsonHelper.getString(json, "name");
		JsonObject textureSize = JsonHelper.getObject(json, "textureSize");
		Vector2f totalSize = new Vector2f(JsonHelper.getInt(textureSize, "totalX"), JsonHelper.getInt(textureSize, "totalY"));
		Vector2f buddySize = new Vector2f(JsonHelper.getInt(textureSize, "buddyX"), JsonHelper.getInt(textureSize, "buddyY"));
		JsonArray animationArray = JsonHelper.getArray(json, "animations");
		Map<String, Animation> animations = new HashMap<>();
		for (int i = 0; i < animationArray.size(); i++)
		{
			JsonObject animObject = animationArray.get(i).getAsJsonObject();
			String animId = JsonHelper.getString(animObject, "id");
			int frames = JsonHelper.getInt(animObject, "frames");
			double interval = JsonHelper.getDouble(animObject, "interval");
			animations.put(animId, new Animation(i, frames, interval));
		}
		double moveSpeed = JsonHelper.getDouble(json, "moveSpeed");
		return new BuddyType(name, identifier, totalSize, buddySize, animations, moveSpeed);
	}
}
