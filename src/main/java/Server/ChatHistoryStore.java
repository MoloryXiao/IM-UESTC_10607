package Server;

import java.util.Hashtable;

//消息记录
//最近会话列表
//消息历史记录
//离线消息
//消息漫游
//聊天记录搜索

public class ChatHistoryStore {
	
	// <private session id , private session>
	private static Hashtable<String, PrivateSession> privateSessionRepository
			= new Hashtable<String, PrivateSession>();
	
	public static PrivateSession getPrivateSession( String targetId, String sourceId ) {
		
		String idA;
		String idB;
		
		if (targetId.compareToIgnoreCase(sourceId) < 0) {
			idA = targetId;
			idB = sourceId;
		} else {
			idA = sourceId;
			idB = targetId;
		}
		
		PrivateSession privateSession = privateSessionRepository.get(idA + idB);
		
		if (privateSession == null) {
			privateSession = new PrivateSession(idA, idB);
			privateSessionRepository.put(idA + idB, privateSession);
		}
		
		return privateSession;
	}
}

