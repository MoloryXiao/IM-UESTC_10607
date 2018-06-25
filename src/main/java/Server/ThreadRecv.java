package Server;

import Server.util.LoggerProvider;
import network.NetworkForServer.*;

import java.io.IOException;

public class ThreadRecv extends Thread {
	
	private volatile boolean exit = false;
	
	private String userId;
	private CommunicateWithClient client;
	private ThreadSingleClient singleClientThread;
	
	/**
	 * @param exit 要设置的 exit
	 */
	public void setExit( boolean exit ) {
		
		this.exit = exit;
	}
	
	public ThreadRecv( CommunicateWithClient client, String userId, ThreadSingleClient singleClientThread ) {
		
		this.client = client;
		this.userId = userId;
		this.singleClientThread = singleClientThread;
	}
	
	@Override
	public void run() { // 负责监听有没有消息到达，有则把这些消息加入到接收队列中，由ServerThread处理
		
		LoggerProvider.logger.info("[  O K  ] 用户ID：" + userId + " 接收子线程已创建！");
		
		while (!exit) {
			
			try {

//				//************************************************
//				Message msg = client.recvFromClient();
//				System.out.println("recv from " + userId + ": " + msg.getType() + msg.getText());
//				System.out.flush();
//				singleClientThread.putMsgToRecvQueue(msg);
//				//**************************************************
				
				/* 从客户端接受一条消息，并加入接受消息队列，交给ServerThread解析及处理 */
				singleClientThread.putMsgToRecvQueue(client.recvFromClient());
				
			} catch (IOException e) {
				
				if (e.getMessage().equals("Connection reset")) {
					
					singleClientThread.interrupt();
					
					LoggerProvider.logger.error("[ ERROR ] 用户ID：" + userId + " 客户端已断开连接！");
					break;
					
				} else {
					
					System.out.println(e.getMessage());
					
					LoggerProvider.logger.error("[ ERROR ] 接受客户端信息发生错误！");
					
				}
			}
		}
		
		while (!exit) ;    // 等待主线程发送结束信号
		
		LoggerProvider.logger.info("[  O K  ] 用户ID：" + userId + " 接受子线程已结束！");
	}
	
}
