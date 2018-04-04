
/**
 * 
 */
package tech.njczh;

/**
 * 管理登陆的用户进程，添加或删除
 * 
 * @author 97njczh
 *
 */
public class ThreadManager {
	
	private static void regThread(ServerThread serverThread) {
		ThreadDatabase.threadDb.put(serverThread.getOnlineUser(), serverThread);
	}

	private static void delThread(OnlineUser onlineUser) {
		ThreadDatabase.threadDb.remove(onlineUser);
	}
	
}
