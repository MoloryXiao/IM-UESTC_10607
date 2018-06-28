package network.commonClass;

import java.util.ArrayList;

/**
 * 描述：群组类
 *
 * @author ZiQin
 * @version 1.0.0
 */
public class Group {
	
	private static final int maxNumOfMem = 100;
	
	private String gid;
	
	private String name;
	
	private String description;
	
	private Account owner;
	
	private ArrayList<Account> groupMembers;
	
	private String message;
	
	public Group() {
		
		gid = null;
		name = null;
		description = null;
		groupMembers = null;
		message = null;
		owner = null;
	}
	
	public Group( String gid, String name, String description, String message ) {
		
		this.gid = gid;
		this.name = name;
		this.description = description;
		this.message = message;
		this.groupMembers = null;
	}
	
	public Group( String gid, String name, String description, Account owner, ArrayList<Account> groupMembers, String message ) {
		
		this.gid = gid;
		this.name = name;
		this.description = description;
		this.owner = owner;
		this.groupMembers = groupMembers;
		this.message = message;
	}
	
	public String getGid() {
		
		return (gid == null) ? null : new String(gid);
	}
	
	public void setGid( String gid ) {
		
		this.gid = gid;
	}
	
	public String getName() {
		
		return (name == null) ? null : new String(name);
	}
	
	public void setName( String name ) {
		
		this.name = name;
	}
	
	public String getDescription() {
		
		return (description == null) ? null : new String(description);
	}
	
	public void setDescription( String description ) {
		
		this.description = description;
	}
	
	public String getMessage() {
		
		return (message == null) ? null : new String(message);
	}
	
	public ArrayList<Account> getGroupMembers() {
		
		return (groupMembers == null) ? null : new ArrayList<Account>(groupMembers);
	}
	
	public void setGroupMembers( String message ) {
		
		this.message = message;
	}
	
	public void setOwner( Account owner ) {
		
		this.owner = owner;
	}
	
	
	public Account getOwner() {
		
		return owner;
	}
	
	public void setMember( ArrayList<Account> members ) {
		
		this.groupMembers = new ArrayList<Account>(members);
	}
	
	public boolean joinGroup( Account newMember ) {
		
		boolean result = false;
		
		if (!(groupMembers.contains(newMember) || groupMembers.size() >= maxNumOfMem)) {
			groupMembers.add(newMember);
			result = true;
		}
		
		return result;
	}
	
	public boolean quitGroup( Account member ) {
		
		return groupMembers.remove(member);
	}
	
	public void addMemeber( ArrayList<Account> person ) {
		
		for (int i = 0; i < person.size(); i++) {
			if (!groupMembers.contains(person.get(i)))
				groupMembers.add(person.get(i));
		}
	}
}