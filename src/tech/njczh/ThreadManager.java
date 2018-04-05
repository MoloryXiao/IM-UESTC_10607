
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

	public static void regSendThread(ServerThread serverThread) {
		ThreadDatabase.sendThreadDb.put(serverThread.getOnlineUser(), serverThread);
	}

	public static void delSendThread(OnlineUser onlineUser) {
		ThreadDatabase.sendThreadDb.remove(onlineUser);
	}

	public static void regRecvThread(ServerThread serverThread) {
		ThreadDatabase.recvThreadDb.put(serverThread.getOnlineUser(), serverThread);
	}

	public static void delRecvThread(OnlineUser onlineUser) {
		ThreadDatabase.recvThreadDb.remove(onlineUser);
	}

}
