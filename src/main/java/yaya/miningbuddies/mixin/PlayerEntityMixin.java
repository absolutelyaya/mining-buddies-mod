package yaya.miningbuddies.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yaya.miningbuddies.Buddies.Buddy;
import yaya.miningbuddies.MiningBuddiesMod;
import yaya.miningbuddies.Registries.BuddyManager;
import yaya.miningbuddies.accessors.PlayerEntityAccessor;

import java.util.ArrayList;
import java.util.List;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements PlayerEntityAccessor
{
	@Unique private List<Buddy> ownedBuddies = new ArrayList<>();
	@Unique private Buddy activeBuddy;
	
	@Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
	public void onWriteCustomDataToNbt(NbtCompound nbt, CallbackInfo ci)
	{
		NbtCompound data = new NbtCompound();
		NbtList buddyList = new NbtList();
		for(Buddy buddy : ownedBuddies)
		{
			buddyList.add(Buddy.serialize(buddy));
		}
		data.put("activeBuddy", Buddy.serialize(activeBuddy));
		data.put("ownedBuddies", buddyList);
		nbt.put(MiningBuddiesMod.MOD_ID, data);
	}
	
	@Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
	public void onReadCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci)
	{
		if(nbt.contains(MiningBuddiesMod.MOD_ID))
		{
			NbtCompound nbt1 = (NbtCompound)nbt.get(MiningBuddiesMod.MOD_ID);
			NbtList buddyList = nbt1.getList("ownedBuddies", 10);
			List<Buddy> buddies = new ArrayList<>();
			for (NbtElement buddy : buddyList)
			{
				buddies.add(Buddy.deserialize((NbtCompound)buddy));
			}
			setOwnedBuddies(buddies);
			setActiveBuddy(Buddy.deserialize((NbtCompound)nbt1.get("activeBuddy")));
		}
	}
	
	@Override
	public void setActiveBuddy(Buddy b)
	{
		activeBuddy = b;
	}
	
	@Override
	public Buddy getActiveBuddy()
	{
		return activeBuddy;
	}
	
	@Override
	public List<Buddy> getOwnedBuddies()
	{
		return ownedBuddies;
	}
	
	@Override
	public void setOwnedBuddies(List<Buddy> ownedBuddies)
	{
		if(ownedBuddies.size() > 0)
			this.ownedBuddies = ownedBuddies;
		else
		{
			this.ownedBuddies = List.of(BuddyManager.getBuddy(new Identifier(MiningBuddiesMod.MOD_ID, "glare")));
			setActiveBuddy(this.ownedBuddies.get(0));
			System.out.println("applied default buddies");
		}
	}
}
