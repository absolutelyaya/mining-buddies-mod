package yaya.miningbuddies.Buddies;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import yaya.miningbuddies.Registries.BuddyManager;

import java.util.UUID;

public class Buddy
{
	private final UUID uuid;
	private final BuddyType type;
	
	private String nickName;
	
	public Buddy(BuddyType type)
	{
		this.type = type;
		this.nickName = type.name;
		this.uuid = UUID.randomUUID();
	}
	
	private Buddy(BuddyType type, String nickName, UUID id)
	{
		this.nickName = nickName;
		this.uuid = id;
		this.type = type;
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
		buddyData.putString("type", b.getType().getID().toString());
		return buddyData;
	}
	
	public static Buddy deserialize(NbtCompound nbt)
	{
		if(nbt == null)
			return null;
		String typeId = nbt.getString("type");
		if(typeId.matches("null"))
			return null;
		String[] parts = typeId.split(":");
		System.out.println("deserializing " + nbt.asString());
		Identifier identifier = new Identifier(parts[0], parts[1]);
		BuddyType type = BuddyManager.getBuddyType(identifier);
		return new Buddy(type, nbt.getString("nickname"), nbt.getUuid("id"));
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
	
	public BuddyType getType()
	{
		return type;
	}
	
	///TODO: Store spritecheet information (texture size, animation data)
	///TODO: Finding requirements (Biome, height, temperature, broken blocks, chance, ...)
	///TODO: Add Cosmetics (like bows you can put on your Buddy)
}
