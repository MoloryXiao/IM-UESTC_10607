package Server;
/**
 *
 */

/**
 * 管理登陆的用户进程，添加或删除
 *
 * @author 97njczh
 */
public class ThreadManager {
	
	/*======================================= Server.ServerThread =======================================*/
	
	public static void regServerThread( ServerThread serverThread ) {
		
		ThreadDatabase.serverThreadDb.put(serverThread.getAccountId(), serverThread);
	}
	
	public static void delServerThread( String id ) {
		
		ThreadDatabase.serverThreadDb.remove(id);
	}
	
}

//	/*======================================= Server.SendThread =======================================*/
//
//	public static void regSendThread(Server.SendThread sendThread) {
//		Server.ThreadDatabase.sendThreadDb.put(sendThread.getAccountId(), sendThread);
//	}
//
//	public static void delSendThread(String id) {
//		Server.ThreadDatabase.sendThreadDb.remove(id);
//	}
