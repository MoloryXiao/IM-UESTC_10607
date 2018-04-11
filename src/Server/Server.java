package Server;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {
	
	/*======================================= ServerThread Database =======================================*/
	
	private static Map<String, ServerThread> serverThreadDb = new HashMap<String, ServerThread>();
	
	/*======================================= ServerThread Manager  =======================================*/
	
	public static boolean newServerThread( Socket clientSocket ) {
		
		try {
			
			ServerThread serverThread = new ServerThread(clientSocket);
			serverThread.start();
			System.out.println("[ READY ] 客户服务线程创建成功！");
			return true;
			
		} catch (IOException e) {
			
			System.out.println("[ ERROR ] 客户服务线程创建失败，"
					                   + "无法为来自：" + clientSocket.getRemoteSocketAddress()
					                   + "的用户创建服务线程！");
			return false;
			
		}
		
	}
	
	public static void regServerThread( ServerThread serverThread ) {
		
		serverThreadDb.put(serverThread.getAccountId(), serverThread);
	}
	
	public static void delServerThread( String id ) {
		
		serverThreadDb.remove(id);
	}
	
	/**
	 * @return int 在客户服务子线程中的线程数，即在线用户数；
	 */
	public static int getOnlineCounter() {
		
		return serverThreadDb.size();
	}
	
	/**
	 * 判断该id的用户是否在线状态，即通过查询是否在客户服务子线程中来判断
	 *
	 * @param id
	 * @return 在客户服务子线程数据库中即在线，返回true；否则，返回false.
	 */
	public static boolean isUserOnline( String id ) {
		
		return serverThreadDb.containsKey(id);
		
	}
	
	public static void sendToAll() {
	
	}
	
	// 参数
	public static void sendToOne( String targetId ,Message message) {
	
		serverThreadDb.get(targetId).putMsgToSendQueue(message);
	
	}
	
	public static void sendToGroup() {
	
	}
	
	
	public static void main( String[] args ) {
		
		/* 检查数据库连接 */
		if (!DatabaseOperator.connectDbTest())
			return;
		
		/* 启动服务器并开始监听端口:'9090' */
		PortListenThread portListenThread = new PortListenThread(9090);
		portListenThread.start();
		
		//while (true) {
		
		//}
		System.out.println("[ READY ] 主线程退出!");
		//System.out.println("********************** goodbye! **********************");
		
	}
	
}
