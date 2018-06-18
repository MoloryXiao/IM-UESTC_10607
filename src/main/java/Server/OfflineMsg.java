package Server;

import java.util.HashSet;
import java.util.Hashtable;

public class OfflineMsg {
	
	/* ==================================== [ Chat Msg ] ==================================== */
	
	// 记录对于某一个用户("key")，有多少好友("key value")给其发送了离线消息
	private static Hashtable<String, HashSet<String>> offlineChatMsgStatus
			= new Hashtable<String, HashSet<String>>();
	private static Hashtable<String, HashSet<String>> offlineReqMsgStatus
			= new Hashtable<String, HashSet<String>>();
	
	/**
	 * 判断是否有离线聊天消息
	 *
	 * @param targetId 离线消息接收者id
	 * @return 是否有未收离线聊天消息
	 */
	public static boolean isAnyOfflineChatMsg( String targetId ) {
		
		boolean result = false;
		
		if (offlineChatMsgStatus.containsKey(targetId))
			if (!offlineChatMsgStatus.get(targetId).isEmpty())
				result = true;
		
		return result;
	}
	
	public static void putOfflineChatMsg( String targetId, String sourceId ) {

//		HashSet<String> status = offlineChatMsgStatus.get(targetId);
//		if (status == null) {    // 说明还没有发给targetId的离线消息
//			status = new HashSet<String>();
//			offlineChatMsgStatus.put(targetId, status);
//		}
		// 若尚没有建立发给targetId的记录信息，则建立之
		HashSet<String> status = offlineChatMsgStatus.computeIfAbsent(targetId, k -> new HashSet<String>());
		// 记录某一好友给targetID用户发送过消息
		status.add(sourceId);
	}
	
	public static void removeOfflineChatMsg( String targetId, String sourceId ) {
		
		offlineChatMsgStatus.get(targetId).remove(sourceId);
	}
	
	/* ==================================== [ Req  Msg ] ==================================== */
	
	public static HashSet<String> getChatMsgSourceIds( String targetId ) {
		
		return offlineChatMsgStatus.get(targetId);
	}
	
	/**
	 * 判断是否有离线请求类消息
	 *
	 * @param targetId 离线消息接收者id
	 * @return 是否有未收离线请求类消息
	 */
	public static boolean isAnyOfflineRqeMsg( String targetId ) {
		
		boolean result = false;
		
		if (offlineReqMsgStatus.containsKey(targetId))
			if (!offlineReqMsgStatus.get(targetId).isEmpty())
				result = true;
		
		return result;
	}
	
	public static void putOfflineReqMsg( String targetId, String sourceId ) {
		
		HashSet<String> status = offlineReqMsgStatus.computeIfAbsent(targetId, k -> new HashSet<String>());
		status.add(sourceId);
	}
	
	public static HashSet<String> getReqMegSourceIds( String targetId ) {
		
		return offlineReqMsgStatus.get(targetId);
	}
	
}
