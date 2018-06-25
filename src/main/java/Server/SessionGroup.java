package Server;

import Server.util.LoggerProvider;
import network.commonClass.Account;
import network.commonClass.Group;
import network.commonClass.Message;

import java.util.Hashtable;
import java.util.Vector;

public class SessionGroup {
	
	private String groupId;
	
	private Vector<Message> chatMsgList;
	
	private Hashtable<String, Integer> chatCursors;
	
	/**
	 * MOD #1 TIME：2018/06/22 15:35 Version 0.8.1
	 * DESCRIPTION：构造函数
	 *
	 * @param groupId
	 */
	SessionGroup( String groupId ) {
		
		this.groupId = groupId;
		chatMsgList = new Vector<>();
		chatCursors = new Hashtable<>();
		for (Account member : GroupManager.getGroupDetails(groupId).getGroupMembers()) {
			chatCursors.put(member.getId(), 0);
		}
	}
	
	public void addToChatMsgList( Message msg ) {
		
		chatMsgList.add(msg);
	}
	
	public boolean updateChatCursor( boolean[] result ) {
		
		Group group = GroupManager.getGroupDetails(groupId);
		
		if (result.length != group.getGroupMembers().size()) {
			LoggerProvider.logger.error("[ ERROR ] Group Session，GroupID："
					                            + groupId + " 中群人数不匹配，无法更新消息游标");
			return false;
		}
		
		for (int i = 0; i < result.length; i++)
			if (result[i])
				chatCursors.put(group.getGroupMembers().get(i).getId(), chatMsgList.size());
		
		return true;
		
	}
	
	private int getNext( String id ) {
		
		int next = -1;
		
		if (chatCursors.get(id) < chatMsgList.size()) {
			next = chatCursors.get(id);
			chatCursors.put(id, next + 1);
		}
		
		return next;
	}
	
	public Message getNextUnreadMsg( String targetId ) {
		
		int next = getNext(targetId);
		
		if (next != -1)
			return chatMsgList.get(next);
		else
			return null;
		
	}
	
}
