package Server;

import javax.sound.midi.Soundbank;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Server {
	
	/*======================================= ServerThread Database =======================================*/
	
	private static Map<String, ServerThread> serverThreadDb = new HashMap<String, ServerThread>();
	
	
	
	/*======================================= ServerThread Manager  =======================================*/
	
	public static boolean newServerThread( Socket clientSocket ) {
		
		try {
			
			ServerThread serverThread = new ServerThread(clientSocket);
			serverThread.start();
			System.out.println("[ READY ] 用户服务子线程创建成功！");
			return true;
			
		} catch (IOException e) {
			
			System.out.println("[ ERROR ] 用户服务子线程创建失败，"
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
	
	public static void showOnlineUser() {
		
		System.out.println("[ ADMIN ] 当前在线人数【 "
				                   + Server.getOnlineCounter() + " 】");
		for (String key : serverThreadDb.keySet()) {
			System.out.print("\t" + key);
		}
		System.out.println();
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
	public static void sendToOne( String targetId, String message ) {
		
		serverThreadDb.get(targetId).putMsgToSendQueue(message);
	}
	
	public static void sendToGroup() {
	
	}
	
	
	public static void main( String[] args ) {
		
		/* 检查数据库连接 */
		if (!DatabaseOperator.connectDbTest())
			return;
		
		System.out.println("[ READY ] 服务器主线程正在初始化监听线程...");
		
		/* 启动服务器并开始监听端口:'9090' */
		PortListenThread portListenThread = new PortListenThread(9090);
		portListenThread.start();
		
		Scanner scanner = new Scanner(System.in);
		while (true) {
			String s = scanner.nextLine();
			if (s.equals("exit")) {
				System.exit(0);
			} else if (s.equals("show")) {
				System.out.println("[ ADMIN ] 当前在线人数【 "
						                   + Server.getOnlineCounter() + " 】");
			} else if (s.equals("show -a") || s.equals("show --all")) {
				showOnlineUser();
			}
			
		}
		
		// System.out.println("[ ATTENTION ] 主线程退出!");
		// System.out.println("********************** goodbye! **********************");
	}
	
}
