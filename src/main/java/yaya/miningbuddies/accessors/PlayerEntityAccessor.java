package yaya.miningbuddies.accessors;

import yaya.miningbuddies.Buddies.Buddy;

import java.util.List;
import java.util.UUID;

public interface PlayerEntityAccessor
{
	void setActiveBuddy(UUID id);
	
	UUID getActiveBuddy();
	
	List<Buddy> getOwnedBuddies();
	
	Buddy getOwnedBuddyByID(UUID id);
	
	void setOwnedBuddies(List<Buddy> values);
	
	boolean addBuddy(Buddy b);
}
