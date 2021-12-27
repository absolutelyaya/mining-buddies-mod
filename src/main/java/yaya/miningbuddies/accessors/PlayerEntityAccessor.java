package yaya.miningbuddies.accessors;

import net.minecraft.util.Identifier;
import yaya.miningbuddies.Buddies.Buddy;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PlayerEntityAccessor
{
	void setActiveBuddy(UUID id);
	
	UUID getActiveBuddy();
	
	List<Buddy> getOwnedBuddies();
	
	Buddy getOwnedBuddyByID(UUID id);
	
	void setOwnedBuddies(List<Buddy> values);
	
	boolean addBuddy(Buddy b);
	
	boolean hasBuddyOfType(Identifier type);
	
	void buddyReactionTick();
	
	void setWatchEntityTypes(Map<String, Integer> list);
	
	void useNoteBlock(int note);
}
