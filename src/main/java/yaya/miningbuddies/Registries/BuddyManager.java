package yaya.miningbuddies.Registries;

import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yaya.miningbuddies.Buddies.Animation;
import yaya.miningbuddies.Buddies.BuddyType;
import yaya.miningbuddies.Buddies.Reaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuddyManager extends JsonDataLoader
{
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
	private static Map<Identifier, BuddyType> buddies = ImmutableMap.of();
	
	public BuddyManager()
	{
		super(GSON, "buddies");
	}
	
	@Override
	protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler)
	{
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
	
	public static BuddyType getBuddyType(Identifier id)
	{
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
			//0 loops = infinite
			int loops = 0;
			if(JsonHelper.hasNumber(animObject, "loops"))
				loops = JsonHelper.getInt(animObject, "loops");
			animations.put(animId, new Animation(i, frames, interval, loops));
		}
		double moveSpeed = JsonHelper.getDouble(json, "moveSpeed");
		JsonArray reactionArray = JsonHelper.getArray(json, "reactions");
		Map<Reaction.ReactionTrigger, List<Reaction>> reactions = new HashMap<>();
		for (int i = 0; i < reactionArray.size(); i++)
		{
			JsonObject reactionObject = reactionArray.get(i).getAsJsonObject();
			if(JsonHelper.hasString(reactionObject, "type"))
			{
				Reaction.ReactionTrigger type = Reaction.ReactionTrigger.valueOf(JsonHelper.getString(reactionObject, "type").toUpperCase());
				if(!reactions.containsKey(type))
					reactions.put(type, new ArrayList<>());
				Reaction r = deserializeReaction(reactionObject, type, identifier);
				if(r != null)
					reactions.get(type).add(r);
			}
			else
				System.err.println("Failed loading Reaction " + (i + 1) + " for " + identifier.toString() + " due to missing argument 'type'.");
		}
		return new BuddyType(name, identifier, totalSize, buddySize, animations, moveSpeed, reactions);
	}
	
	static Reaction deserializeReaction(JsonObject object, Reaction.ReactionTrigger type, Identifier id)
	{
		String anim = JsonHelper.getString(object, "animation");
		Reaction reaction = new Reaction(type, anim);
		if(JsonHelper.hasNumber(object, "weight"))
			reaction.setWeight(JsonHelper.getInt(object, "weight"));
		switch(type)
		{
			case PICKUP -> {
				Identifier itemID = Identifier.tryParse(JsonHelper.getString(object, "item"));
				if(itemID == null || Registry.ITEM.getOrEmpty(itemID).isEmpty())
				{
					reactionError(type, id, "Item '" + itemID + "' invalid.");
					return null;
				}
				reaction.setData(itemID.toString());
			}
			case LIGHTLEVEL -> setIntValue(reaction, object);
			case NEARBY -> {
				JsonObject data = JsonHelper.getObject(object, "data");
				if(JsonHelper.hasNumber(data, "distance") && JsonHelper.hasString(data, "entity"))
				{
					reaction.setMax(JsonHelper.getInt(data, "distance"));
					String entity = JsonHelper.getString(data, "entity");
					Identifier entityID = Identifier.tryParse(entity);
					if(entityID != null)
					{
						var e = Registry.ENTITY_TYPE.getOrEmpty(entityID);
						if(e.isPresent())
							reaction.setData(entity);
						else
						{
							reactionError(type, id,"Entity Type '" + entity + "' not found.");
							return null;
						}
					}
					else
					{
						reactionError(type, id, "Entity Type '" + entity + "' couldn't be parsed to an Identifier.");
						return null;
					}
				}
				else
				{
					List<String> missing = new ArrayList<>();
					if(!JsonHelper.hasNumber(data, "distance"))
						missing.add("Missing Argument 'distance'.");
					if(!JsonHelper.hasString(data, "entity"))
						missing.add("Missing Argument 'entity'.");
					reactionError(type, id, missing.toArray(String[]::new));
					return null;
				}
			}
		}
		return reaction;
	}
	
	static void reactionError(Reaction.ReactionTrigger type, Identifier id, String... reasons)
	{
		StringBuilder error = new StringBuilder("Failed to load Reaction of type " + type.name() + " in Buddy '" + id.toString() + "'.");
		for (String r : reasons)
		{
			error.append("\n\t- ").append(r);
		}
		System.err.println(error);
	}
	
	static void setIntValue(Reaction reaction, JsonObject object)
	{
		if(JsonHelper.hasNumber(object, "value"))
		{
			int value = JsonHelper.getInt(object, "value");
			reaction.setMin(value);
			reaction.setMax(value);
			return;
		}
		if(JsonHelper.hasNumber(object, "min"))
			reaction.setMin(JsonHelper.getInt(object, "min"));
		if(JsonHelper.hasNumber(object, "max"))
			reaction.setMax(JsonHelper.getInt(object, "max"));
	}
}
