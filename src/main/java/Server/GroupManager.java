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
	
	public static Group getGroupDetails( String gid ) {
		
		if (gid.charAt(0) == 'g')
			gid = gid.substring(1);
		
		Group group = groups.get(gid);
		
		if (null == group) {
			group = DatabaseOperator.getGroupDetails(gid);
			if (null != group)
				regGroup(group);
		}
		
		return group;
	}
	
	public static void updateGroupDetails( String gid ) {
		
		if (gid.charAt(0) == 'g')
			gid = gid.substring(1);
		
		regGroup(DatabaseOperator.getGroupDetails(gid));
		
	}
	
	public static void regGroup( Group group ) {
		
		groups.put(group.getGid(), group);
	}
	
	public static Group createGroup( Group initGroup ) {
		
		Group newGroup = DatabaseOperator.createGroup(initGroup);
		if (newGroup != null)
			regGroup(newGroup);
		return newGroup;
	}
	
	public static boolean modGroupInfo( Group group ) {
		
		boolean result = false;
		
		if (DatabaseOperator.modGroupInfo(group)) {
			updateGroupDetails(group.getGid());
			result = true;
		}
		
		return result;
	}
	
	public static boolean joinGroup( String gid, String uid ) {
		
		boolean result = false;
		
		if (DatabaseOperator.joinGroup(gid, uid)) {
			updateGroupDetails(gid);
			result = true;
		}
		
		return result;
	}
	
	public static boolean quitGroup( String gid, String uid ) {
		
		boolean result = false;
		
		if (DatabaseOperator.quitGroup(gid, uid)) {
			updateGroupDetails(gid);
			result = true;
		}
		
		return result;
	}
	
	
}