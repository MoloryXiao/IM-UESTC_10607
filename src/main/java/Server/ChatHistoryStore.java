package Server;

import java.util.ArrayList;
import java.util.Hashtable;

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

