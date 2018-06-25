package Server;

import Server.Database.DatabaseOperator;
import network.commonClass.Group;

import java.util.Hashtable;

public class GroupManager {
	
	private static Hashtable<String, Group> groups = new Hashtable<String, Group>();
	
	public static boolean loadGroups( int num ) {
		
		if (DatabaseOperator.loadGroupDetails(num) != null)
			return true;
		else
			return false;
	}
	
	
	public static Group getGroupDetails( String groupId ) {
		
		if (groupId.charAt(0) == 'g')
			groupId = groupId.substring(1);
		
		Group group = groups.get(groupId);
		
		if (null == group) {
			group = DatabaseOperator.getGroupDetails(groupId);
			if (null != group)
				regGroup(group);
		}
		
		return group;
	}

//	public static Group getGroupInfo( String groupId ) {
//
//		Group group = groups.get(groupId);
//
//		if (null == group) {
//			group = DatabaseOperator.getGroupInfo(groupId);
//			if (null != group)
//				regGroup(group);
//		}
//
//		return group;
//	}
	
	public static void regGroup( Group group ) {
		
		groups.put(group.getGid(), group);
	}
	
	public static Group createGroup( String ownerId ) {
		
		Group newGroup = DatabaseOperator.createGroup(ownerId);
		regGroup(newGroup);
		return newGroup;
	}
	
	public static boolean modGroupInfo( Group group ) {
		
		return true;
	}
	
	
}
