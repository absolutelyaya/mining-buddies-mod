package yaya.miningbuddies.Buddies;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class Buddy
{
	public final String name;
	private String nickName;
	private final UUID uuid;
	private final Identifier identifier;
	
	public Buddy(String name, Identifier identifier)
	{
		this.name = name;
		this.nickName = name;
		this.identifier = identifier;
		this.uuid = UUID.randomUUID();
	}
	
	private Buddy(Identifier identifier, String name, String nickName, UUID id)
	{
		this.name = name;
		this.nickName = nickName;
		this.uuid = id;
		this.identifier = identifier;
	}
	
	public static NbtCompound serialize(Buddy b)
	{
		NbtCompound buddyData = new NbtCompound();
		if(b == null)
		{
			buddyData.putString("type", "null");
			return buddyData;
		}
		buddyData.putString("name", b.name);
		buddyData.putString("nickname", b.getNickName());
		buddyData.putUuid("id", b.getUuid());
		buddyData.putString("type", b.getIdentifier().toString());
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
		return new Buddy(new Identifier(parts[0], parts[1]), nbt.getString("name"), nbt.getString("nickname"), nbt.getUuid("id"));
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
	
	public Identifier getIdentifier()
	{
		return identifier;
	}
}
