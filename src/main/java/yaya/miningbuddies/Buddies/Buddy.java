package yaya.miningbuddies.Buddies;

import net.minecraft.client.util.math.Vector2f;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import yaya.miningbuddies.Registries.BuddyManager;

import java.util.UUID;

public class Buddy
{
	public final String name;
	private String nickName;
	private final UUID uuid;
	private final Identifier type;
	private final Vector2f textureSize;
	
	public Buddy(String name, Identifier identifier, Vector2f textureSize)
	{
		this.name = name;
		this.nickName = name;
		this.type = identifier;
		this.uuid = UUID.randomUUID();
		this.textureSize = textureSize;
	}
	
	private Buddy(Identifier identifier, String name, String nickName, UUID id, Vector2f textureSize)
	{
		this.name = name;
		this.nickName = nickName;
		this.uuid = id;
		this.type = identifier;
		this.textureSize = textureSize;
	}
	
	public static NbtCompound serialize(Buddy b)
	{
		NbtCompound buddyData = new NbtCompound();
		if(b == null)
		{
			buddyData.putString("type", "null");
			return buddyData;
		}
		buddyData.putString("nickname", b.getNickName());
		buddyData.putUuid("id", b.getUuid());
		buddyData.putString("type", b.getType().toString());
		return buddyData;
	}
	
	public static Buddy deserialize(NbtCompound nbt)
	{
		if(nbt == null)
			return null;
		String type = nbt.getString("type");
		if(type.matches("null"))
			return null;
		String[] parts = type.split(":");
		System.out.println("deserializing " + nbt.asString());
		Identifier identifier = new Identifier(parts[0], parts[1]);
		Buddy base = BuddyManager.getBuddy(identifier);
		return new Buddy(identifier, base.name, nbt.getString("nickname"), nbt.getUuid("id"), base.getTextureSize());
	}
	
	public String getNickName()
	{
		return nickName;
	}
	
	public void setNickName(String nickName)
	{
		this.nickName = nickName;
	}
	
	public UUID getUuid()
	{
		return uuid;
	}
	
	public Identifier getType()
	{
		return type;
	}
	
	public Vector2f getTextureSize()
	{
		return textureSize;
	}
	
	///TODO: Store spritecheet information (texture size, animation data)
	///TODO: Finding requirements (Biome, height, temperature, broken blocks, chance, ...)
	///TODO: Add Cosmetics (like bows you can put on your Buddy)
}
