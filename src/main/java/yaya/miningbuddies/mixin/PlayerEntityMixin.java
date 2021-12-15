package yaya.miningbuddies.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yaya.miningbuddies.Buddies.Buddy;
import yaya.miningbuddies.MiningBuddiesMod;
import yaya.miningbuddies.Registries.BuddyManager;
import yaya.miningbuddies.Settings.SettingsStorage;
import yaya.miningbuddies.accessors.PlayerEntityAccessor;
import yaya.miningbuddies.client.MiningBuddiesClientMod;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements PlayerEntityAccessor
{
	@Shadow public abstract void sendMessage(Text message, boolean actionBar);
	
	@Shadow protected abstract void initDataTracker();
	
	@Unique private List<Buddy> ownedBuddies = new ArrayList<>();
	@Unique private UUID activeBuddy = new UUID(0, 0);
	
	@Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
	public void onWriteCustomDataToNbt(NbtCompound nbt, CallbackInfo ci)
	{
		NbtCompound data = new NbtCompound();
		NbtList buddyList = new NbtList();
		for(Buddy buddy : ownedBuddies)
		{
			buddyList.add(Buddy.serialize(buddy));
		}
		data.putUuid("activeBuddy", activeBuddy);
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
			setActiveBuddy(nbt1.getUuid("activeBuddy"));
		}
	}
	
	@Override
	public void setActiveBuddy(UUID id)
	{
		if(id != null)
		{
			Buddy b = getOwnedBuddyByID(id);
			if(b != null)
				MiningBuddiesClientMod.BUDDY_MINI_HUD.setBuddyType(b.getType());
		}
		else
			MiningBuddiesClientMod.BUDDY_MINI_HUD.setBuddyType(null);
		activeBuddy = id;
	}
	
	@Override
	public UUID getActiveBuddy()
	{
		return activeBuddy;
	}
	
	@Override
	public List<Buddy> getOwnedBuddies()
	{
		return this.ownedBuddies;
	}
	
	@Override
	public void setOwnedBuddies(List<Buddy> ownedBuddies)
	{
		if(ownedBuddies.size() > 0)
		{
			this.ownedBuddies = ownedBuddies;
		}
		else
		{
			this.ownedBuddies = List.of(new Buddy(BuddyManager.getBuddyType(new Identifier(MiningBuddiesMod.MOD_ID, "glare"))));
			setActiveBuddy(this.ownedBuddies.get(0).getUuid());
		}
	}
	
	@Override
	public Buddy getOwnedBuddyByID(UUID id)
	{
		for (Buddy b : ownedBuddies)
		{
			if(id.equals(b.getUuid()))
				return b;
		}
		return null;
	}
	
	@Override
	public boolean addBuddy(Buddy b)
	{
		//if(hasBuddyOfType(b.getType().getID()))
		//	return false;
		ownedBuddies.add(b);
		if(SettingsStorage.getBoolean("show-new-buddy-popup"))
			MiningBuddiesClientMod.NEW_BUDDY_POPUP_HUD.onGetNewBuddy(b.getType());
		else
			sendMessage(new TranslatableText("buddy.get", b.getType().name), true);
		return true;
	}
	
	public boolean hasBuddyOfType(Identifier type)
	{
		for(Buddy b : ownedBuddies)
		{
			if(b.getType().getID().equals(type))
				return true;
		}
		return false;
	}
}
