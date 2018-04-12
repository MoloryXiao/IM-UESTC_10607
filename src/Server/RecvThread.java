package Server;

import network.NetworkForServer.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecvThread extends Thread {
	
	private volatile boolean exit = false;
	
	private String userId;
	private CommunicateWithClient client;
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

//				//************************************************
//				String msg = client.recvFromClient();
//				System.out.println("recv from client : "+ msg);
//				System.out.flush();
//				serverThread.putMsgToRecvQueue(msg);
//				//**************************************************
				
				// 从客户端接受一条消息，并加入接受消息队列，交给ServerThread解析及处理
				serverThread.putMsgToRecvQueue(client.recvFromClient());
				
			} catch (IOException e) {
				
				if (e.getMessage().equals("Connection reset")) {
					serverThread.setAccountOffline();
					System.out.println("[ ERROR ] 客户端已断开连接！");
					break;
				} else {
					System.out.println("[ ERROR ] 接受客户端信息发生错误！");
				}
			}
		}
		
		while(!exit);
		System.out.println("[ READY ] 用户ID：" + userId + " 接受子线程已结束！");
	}
	
}
