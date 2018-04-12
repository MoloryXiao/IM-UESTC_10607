package Server; /**
 *
 */

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
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
	private ServerThread serverThread;
	
	/**
	 * @param exit 要设置的 exit
	 */
	public void setExit( boolean exit ) {
		
		this.exit = exit;
	}
	
	public SendThread( CommunicateWithClient client, String userId, ServerThread serverThread ) throws IOException {
		
		this.client = client;
		this.userId = userId;
		this.serverThread = serverThread;
	}
	
	public void run() {
		
		System.out.println("[ READY ] 用户ID：" + userId + " 发送子线程已创建！");
		
		while (!exit) {
			
			if (!serverThread.isSendQueueEmpty()) {
				
				try {
//				    //************************************************
//					String msg = serverThread.getMsgFromSendQueue();
//					System.out.println("send to client : "+ msg);
//					System.out.flush();
//					client.sendToClient(msg);
//				    //************************************************
					
					// 从待发送队列中取出一条消息发送给客户端
					client.sendToClient(serverThread.getMsgFromSendQueue());
					
				} catch (IOException e) {
					
					System.out.println("[ ERROR ] 消息发送失败！");
				}
			} else {
				
				try {
					sleep(200);
				} catch (InterruptedException e) {
					System.out.println("[ ERROR ]  用户ID：" + userId + "发送线程休眠失败！");
				}
				
			}
		}
		
		System.out.println("[ READY ] 用户ID：" + userId + " 发送子线程已结束！");
	}
	
}
