package Server;

import network.commonClass.Message;

import java.util.Hashtable;
import java.util.Vector;

public class SessionGroup {
	
	private String groupId;
	
	private Vector<Message> chatMsgList;
	
	private Hashtable chatCursors;
	
	SessionGroup( String groupId ) {
		
		this.groupId = groupId;
		chatMsgList = new Vector<Message>();
	}
	
	public void addToChatMsgList( Message msg ) {
		
		chatMsgList.add(msg);
	}
	
	
}
