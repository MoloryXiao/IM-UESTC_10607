package Server;

import java.util.HashSet;

public class AddFriendReqManager {
	
	/* ==================================================================================== */
	
	private static HashSet<String> addFriendReqSet = new HashSet<String>();
	
	
	/* ==================================================================================== */
	
	public synchronized static boolean isAddReqContain( String String ) {
		
		return addFriendReqSet.contains(String);
	}
	
	public synchronized static boolean regAddFriendReq( String String ) {
		
		return addFriendReqSet.add(String);
	}
	
	public synchronized static boolean delAddFriendReq( String AddFriendReq ) {
		
		return addFriendReqSet.remove(AddFriendReq);
	}
	
	public static boolean delAddFriendReq( String targetId, String sourceId ) {
		
		return delAddFriendReq(targetId + sourceId + "true")
				       && delAddFriendReq(targetId + sourceId + "false");
	}
}