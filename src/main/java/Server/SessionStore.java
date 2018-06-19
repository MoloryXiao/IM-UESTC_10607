package Server;

import java.util.Hashtable;

//消息记录
//最近会话列表
//消息历史记录
//离线消息
//消息漫游
//聊天记录搜索

public class SessionStore {
	
	/* ================================= [ private session ] ================================= */
	// <private session id , private session>
	private static Hashtable<String, SessionPrivate> privateSessionRepository
			= new Hashtable<String, SessionPrivate>();
	/* ================================= [  group  session ] ================================= */
	// <group session id , private session>
	private static Hashtable<String, SessionGroup> groupSessionRepository
			= new Hashtable<String, SessionGroup>();
	
	public static SessionPrivate getPrivateSession( String targetId, String sourceId ) {
		
		String idA;
		String idB;
		
		if (targetId.compareToIgnoreCase(sourceId) < 0) {
			idA = targetId;
			idB = sourceId;
		} else {
			idA = sourceId;
			idB = targetId;
		}
		
		SessionPrivate privateSession = privateSessionRepository.get(idA + idB);
		
		if (privateSession == null) {
			privateSession = new SessionPrivate(idA, idB);
			privateSessionRepository.put(idA + idB, privateSession);
		}
		
		return privateSession;
	}
	
	public static SessionGroup getGroupSession( String groupId ) {
		
		SessionGroup groupSession = groupSessionRepository.get(groupId);
		
		if (groupSession == null) {
			groupSession = new SessionGroup(groupId);
			groupSessionRepository.put(groupId, groupSession);
		}
		
		return groupSession;
	}
	
	
}

