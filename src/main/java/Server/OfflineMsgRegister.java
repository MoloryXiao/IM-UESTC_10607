package Server;

import java.util.HashSet;
import java.util.Hashtable;

public class OfflineMsgRegister {
	
	/* ==================================== [ Chat Msg ] ==================================== */
	
	// 记录对于某一个用户("key")，有哪些好友("key value")给其发送了离线消息
	private static Hashtable<String, HashSet<String>> offlineChatMsgStatus = new Hashtable<String, HashSet<String>>();
	
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
	
	private static Hashtable<String, HashSet<String>> offlineReqMsgStatus = new Hashtable<String, HashSet<String>>();
	
	/**
	 * 获取给id为<bold>targetId</bold>的用户发送了离线消息的用户列表
	 *
	 * @param targetId 离线消息接受者id
	 * @return 所有给targetId用户发送了离线消息的用户列表
	 */
	public static HashSet<String> getChatMsgSourceIds( String targetId ) {
		
		return offlineChatMsgStatus.get(targetId);
	}
	
	/**
	 * 记录有ID为<bold>sourceId</bold>的用户给ID为<bold>targetId</bold>的用户发送了离线聊天消息
	 *
	 * @param targetId 离线消息接受者id
	 * @param sourceId 离线消息发送者id
	 */
	public static void regOfflineChatMsg( String targetId, String sourceId ) {

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
	
	/* ==================================== [ Req  Msg ] ==================================== */
	
	public static void removeOfflineChatMsg( String targetId, String sourceId ) {
		
		offlineChatMsgStatus.get(targetId).remove(sourceId);
	}
	
	/**
	 * 判断是否有离线请求类消息
	 *
	 * @param targetId 离线消息接收者id
	 * @return 是否有未收离线请求类消息
	 */
	public static boolean isAnyOfflineReqMsg( String targetId ) {
		
		boolean result = false;
		
		if (offlineReqMsgStatus.containsKey(targetId))
			if (!offlineReqMsgStatus.get(targetId).isEmpty())
				result = true;
		
		return result;
	}
	
	public static void regOfflineReqMsg( String targetId, String sourceId ) {
		
		HashSet<String> status = offlineReqMsgStatus.computeIfAbsent(targetId, k -> new HashSet<String>());
		status.add(sourceId);
	}
	
	public static HashSet<String> getReqMsgSourceIds( String targetId ) {
		
		return offlineReqMsgStatus.get(targetId);
	}
	
	public static void removeOfflineReqMsg( String targetId, String sourceId ) {
		
		if (offlineReqMsgStatus.containsKey(targetId))
			offlineReqMsgStatus.get(targetId).remove(sourceId);
	}
	
}
