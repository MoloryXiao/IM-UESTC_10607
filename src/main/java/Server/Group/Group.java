package Server.Group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Server.*;

public class Group {
	
	private String groupId;                 // 群号
	private ArrayList<String> groupMembers; // 群成员id
	private int Owner;                      // 群主id在群成员id数组内的下标
	private int[] AdminId;                  // 群管理员id在群成员数组内的下标
	
	public Group() {
		// initialize group
	}
	
	public boolean joinGroup() {
		
		return false;
	}
	
	public boolean quitGroup() {
		
		return false;
	}
	
}
