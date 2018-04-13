package Server; /**
 *
 */

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import network.NetworkForServer.*;
import sun.plugin2.message.Message;

/**
 * 服务器负责接收来自服务子线程的数据 并发送给目标客户端
 *
 * @author 97njczh
 */
public class SendThread extends Thread {
	
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
	
	public SendThread( CommunicateWithClient client, String userId, ServerThread serverThread ) {
		
		this.client = client;
		this.userId = userId;
		this.serverThread = serverThread;
	}
	
	public void run() {
		
		System.out.println("[ READY ] 用户ID：" + userId + " 发送子线程已创建！");
		
		while (!exit) {
			
			String message = null;
			
			try {
//				    //************************************************
//					String msg = serverThread.getMsgFromSendQueue();
//					System.out.println("send  to  " + userId + ": " + msg);
//					System.out.flush();
//					client.sendToClient(msg);
//				    //************************************************
				
				/* 从待发送队列中取出一条消息发送给客户端 */
				message = serverThread.getMsgFromSendQueue();
				client.sendToClient(message);
				
			} catch (IOException e) {
				
				serverThread.putMsgToSendQueue(message);
				System.out.println("[ ERROR ] 消息发送失败！等待重试！");
				
			} catch (InterruptedException e) {
				/* 服务子线程发送中断信号，中断阻塞读，结束监听发送队列，结束发送子线程 */
				break;
			}
			
		}
		
		while (!exit) ;
		System.out.println("[ READY ] 用户ID：" + userId + " 发送子线程已结束！");
		
	}
	
}
