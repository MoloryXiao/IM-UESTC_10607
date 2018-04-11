package Server; /**
 *
 */

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

import Network.Server.BaseClass.Account;
import Network.Server.NetworkForServer.CommunicateWithClient;
import Network.Server.BaseClass.Envelope;

/**
 * 服务器负责接收来自服务子线程的数据 并发送给目标客户端
 *
 * @author 97njczh
 */
public class SendThread extends Thread {
	
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
	
	public SendThread( CommunicateWithClient client, String userId, ServerThread serverThread ) throws IOException {
		
		this.client = client;
		this.userId = userId;
		this.serverThread = serverThread;
	}
	
	public void run() {
		
		System.out.println("[ READY ] 用户ID：" + userId + " 发送子线程已创建！");
		
		try {
			
			DatabaseOperator databaseOperator = new DatabaseOperator();
			
			client.sendFinishMsg(); // 返回登陆确认信息
			
			client.sendFriendList(databaseOperator.getFriendListFromDb(account.getID()));
			
			while (!exit) {
				if (!serverThread.sendQueue.isEmpty()) {
					try {
						Message message = serverThread.sendQueue.firstElement();
						serverThread.sendQueue.removeElementAt(0);
						client.sendToClient(message.getEnvelope());
					} catch (IOException e) {
						System.out.println("[ ERROR ] 消息发送失败！");
					}
					
				}
			}
			
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
		
		System.out.println("[ READY ] 用户ID：" + userId + " 发送子线程已结束！");
	}
	
}
