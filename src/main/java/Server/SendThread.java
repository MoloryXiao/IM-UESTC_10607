package Server;

import java.io.IOException;

import Server.util.LoggerProvider;
import network.NetworkForServer.*;

/**
 * 服务器负责接收来自服务子线程的数据 并发送给目标客户端
 *
 * @author 97njczh
 */
public class SendThread extends Thread {
	
	private volatile boolean exit = false;
	
	private String userId;
	private CommunicateWithClient client;
	private SingleClientThread singleClientThread;
	
	/**
	 * @param exit 要设置的 exit
	 */
	public void setExit( boolean exit ) {
		
		this.exit = exit;
	}
	
	public SendThread( CommunicateWithClient client, String userId, SingleClientThread singleClientThread ) {
		
		this.client = client;
		this.userId = userId;
		this.singleClientThread = singleClientThread;
	}
	
	public void run() {
		
		LoggerProvider.logger.info("[  O K  ] 用户ID：" + userId + " 发送子线程已创建！");
		
		while (!exit) {
			
			String message = null;
			
			try {
//				    //************************************************
//					String msg = singleClientThread.getMsgFromSendQueue();
//					System.out.println("send  to  " + userId + ": " + msg);
//					System.out.flush();
//					client.sendToClient(msg);
//				    //************************************************
				
				/* 从待发送队列中取出一条消息发送给客户端 */
				message = singleClientThread.getMsgFromSendQueue();
				client.sendToClient(message);
				
			} catch (IOException e) {
				
				singleClientThread.putMsgToSendQueue(message);
				
				LoggerProvider.logger.error("[ ERROR ] 消息发送失败！等待重试！");
				
			} catch (InterruptedException e) {
				/* 服务子线程发送中断信号，中断阻塞读，结束监听发送队列，结束发送子线程 */
				break;
			}
			
		}
		
		if (!singleClientThread.isSendQueueEmpty()) {
			
			// TODO 保存发送队列消息(除请求好友列表等请求类消息)，由OffineMessageStore来管理
		}
		
		while (!exit) ;
		
		LoggerProvider.logger.info("[  O K  ] 用户ID：" + userId + " 发送子线程已结束！");
	}
	
}
