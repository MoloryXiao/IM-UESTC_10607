package Server;

import java.util.ArrayList;

class PrivateSession {
	
	private String idA;
	private String idB;
	
	private ArrayList<String> chatMsgList;
	
	private int chatCursorA;    // 已读位置
	private int chatCursorB;
	
	PrivateSession( String idA, String idB ) {
		
		this.idA = idA;
		this.idB = idB;
		chatMsgList = new ArrayList<String>();
		chatCursorA = chatCursorB = 0;
	}
	
	public void setChatCursorA( int chatCursorA ) {
		
		this.chatCursorA = chatCursorA;
	}
	
	public void setChatCursorB( int chatCursorB ) {
		
		this.chatCursorB = chatCursorB;
	}
	
	public void updateBothChatCursor() {
		
		updateChatCursorA();
		updateChatCursorB();
	}
	
	public void updateChatCursor( String id ) {
		
		if (id.equals(idA)) {
			updateChatCursorA();
		} else {
			updateChatCursorB();
		}
	}
	
	private void updateChatCursorA() {
		
		this.chatCursorA = chatMsgList.size();
	}
	
	private void updateChatCursorB() {
		
		this.chatCursorB = chatMsgList.size();
	}
	
	private int getNext( String id ) {
		
		int next = -1;
		
		if (id.equals(idA)) {
			if (chatCursorA < chatMsgList.size()) next = chatCursorA++;
		} else {
			if (chatCursorB < chatMsgList.size()) next = chatCursorB++;
		}
		
		return next;
	}
	
	public String getNextUnreadMsg( String targetId ) {
		
		int next = getNext(targetId);
		
		if (next != -1)
			return chatMsgList.get(next);
		else
			return null;
		
	}
	
	public void addToChatMsg( String msg ) {
		
		chatMsgList.add(msg);
	}
	
}
