package Server.Group;

import Server.Database.DatabaseOperator;

import java.util.Hashtable;

public class GroupManager {
	
	private static Hashtable<String, Group> groups = new Hashtable<String, Group>();
	
	public static Group getGroupInfo( String groupId ) {
		
		return groups.get(groupId);
	}
	
	public static void putGroupInfo( Group group ) {
		
		groups.put(group.getGroupId(), group);
	}
	
	public static Group createGroup( String ownerId ) {
		
		return DatabaseOperator.createGroup(ownerId);
	}
	
	

}
