package Server;

import Network.Server.BaseClass.Account;
import Network.Server.NetworkForServer.CommunicateWithClient;

import java.io.IOException;

public class RecvThread extends Thread {
	
	private volatile boolean exit = false;
	
	private CommunicateWithClient client;
	private String userId;
	private ServerThread serverThread;
	
	/**
	 * @param exit 要设置的 exit
	 */
	public void setExit( boolean exit ) {
		
		this.exit = exit;
	}
	
	public RecvThread( CommunicateWithClient client, String userId, ServerThread serverThread ) throws IOException {
		
		this.client = client;
		this.userId = userId;
		this.serverThread = serverThread;
	}
	
	@Override
	public void run() { // 负责监听有没有消息到达，有则把这些消息加入到接受队列中，由ServerThread处理
		
		System.out.println("[ READY ] 用户ID：" + userId + " 接受子线程已创建！");
		
		while (!exit) {
			
			try {
				
				serverThread.putMsgToRecvQueue(client.recvFromClient());
				
			} catch (IOException e) {
				
				e.printStackTrace();
				System.out.println(" [ ERROR ] 接受客户端信息发生错误！");
				
			}
		}
		System.out.println("[ READY ] 用户ID：" + userId + " 接受子线程已结束！");
	}
	
}
