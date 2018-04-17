package Server;

import network.NetworkForServer.*;

import java.io.IOException;

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
	
	public RecvThread( CommunicateWithClient client, String userId, ServerThread serverThread ) {
		
		this.client = client;
		this.userId = userId;
		this.serverThread = serverThread;
	}
	
	@Override
	public void run() { // 负责监听有没有消息到达，有则把这些消息加入到接收队列中，由ServerThread处理
		
		ShowDate.showDate();
		System.out.println("[  O K  ] 用户ID：" + userId + " 接收子线程已创建！");
		
		while (!exit) {
			
			try {

//				//************************************************
//				String msg = client.recvFromClient();
//				System.out.println("recv from " + userId + ": " + msg);
//				System.out.flush();
//				serverThread.putMsgToRecvQueue(msg);
//				//**************************************************
				
				/* 从客户端接受一条消息，并加入接受消息队列，交给ServerThread解析及处理 */
				serverThread.putMsgToRecvQueue(client.recvFromClient());
				
			} catch (IOException e) {
				
				if (e.getMessage().equals("Connection reset")) {
					serverThread.interrupt();
					ShowDate.showDate();
					System.out.println("[ ERROR ] 客户端已断开连接！");
					break;
				} else {
					System.out.println(e.getMessage());
					ShowDate.showDate();
					System.out.println("[ ERROR ] 接受客户端信息发生错误！");
				}
			}
		}
		
		
		if (!serverThread.isRecvQueueEmpty()) {
			// TODO 保存接收队列消息
		}
		
		while (!exit) ; // 等待主线程发送结束信号
		ShowDate.showDate();
		System.out.println("[  O K  ] 用户ID：" + userId + " 接受子线程已结束！");
	}
	
}
