package yaya.miningbuddies.accessors;

import yaya.miningbuddies.Buddies.Buddy;

import java.util.List;

public interface PlayerEntityAccessor
{
	void setActiveBuddy(Buddy b);
	
	Buddy getActiveBuddy();
	
	List<Buddy> getOwnedBuddies();
	
	void setOwnedBuddies(List<Buddy> values);
}
