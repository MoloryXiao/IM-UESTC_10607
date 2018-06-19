package Server.Group;

import java.util.*;

import Server.*;

enum Priority {
	OWNER, ADMIN, NORMAL
}

/* 该类对象可以放入redis管理 */
public class Group {
	
	private String groupId;                 // 群号
	private String owner;                   // 群主id
	private Vector<String> adminId;         // 群管理员id
	private Vector<String> groupMembers;    // 群成员id（包括群主和群管理员）
	
	public Group() {
	
	}
	
	public Group( String groupId, String owner, Vector<String> adminId, Vector<String> groupMembers ) {        // initialize group
		this.groupId = groupId;
		this.owner = owner;
		this.adminId = adminId;
		this.groupMembers = groupMembers;
	}
	
	public String getGroupId() {
		
		return groupId;
	}
	
	public Vector<String> getGroupMembers() {
		
		return groupMembers;
	}
	
	
	public boolean joinGroup() {
		
		return false;
	}
	
	public boolean quitGroup() {
		
		return false;
	}
	
}
