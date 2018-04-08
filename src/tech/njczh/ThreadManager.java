
/**
 *
 */
package tech.njczh;

/**
 * 管理登陆的用户进程，添加或删除
 *
 * @author 97njczh
 */
public class ThreadManager {
	
	/*======================================= ServerThread =======================================*/
	
	public static void regServerThread( ServerThread serverThread ) {
		
		ThreadDatabase.serverThreadDb.put(serverThread.getAccountId(), serverThread);
	}
	
	public static void delServerThread( String id ) {
		
		ThreadDatabase.serverThreadDb.remove(id);
	}
	
}

//	/*======================================= SendThread =======================================*/
//
//	public static void regSendThread(SendThread sendThread) {
//		ThreadDatabase.sendThreadDb.put(sendThread.getAccountId(), sendThread);
//	}
//
//	public static void delSendThread(String id) {
//		ThreadDatabase.sendThreadDb.remove(id);
//	}
