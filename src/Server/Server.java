package Server;

import java.io.IOException;
import java.net.Socket;

public class Server {
	
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
	
	private static void sendToAll() {
	
	}
	
	private static void sendToOne() {
	
	}
	
	private static void sendToGroup() {
	
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
