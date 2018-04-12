package Server; /**
 *
 */

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import network.commonClass.*;
import network.networkDataPacketOperate.*;
import network.NetworkForServer.*;

/**
 * @author 97njczh
 */
public class ServerThread extends Thread {
	
	private CommunicateWithClient client;
	// TODO 思考一下这里有没有必要为Account类型，还是说只需要一个String ID即可
	private Account account;
	
	public String getAccountId() {
		
		return account.getID();
	}
	
	public void setAccountOffline() {
		
		account.setOnLine(false);
	}
	
	/*======================================= related to RecvThread =======================================*/
	
	private RecvThread recvThread;
	private Vector<String> recvQueue;
	
	public boolean isRecvQueueEmpty() {
		
		return recvQueue.isEmpty();
	}
	
	public synchronized String getMsgFromRecvQueue() {
		
		String message = recvQueue.firstElement();
		recvQueue.removeElementAt(0);
		return message;
	}
	
	public synchronized void putMsgToRecvQueue( String message ) {
		
		recvQueue.add(message);
	}
	
	
	
	/*======================================= related to sendThread =======================================*/
	
	private SendThread sendThread;
	private Vector<String> sendQueue;
	
	public boolean isSendQueueEmpty() {
		
		return sendQueue.isEmpty();
	}
	
	public synchronized String getMsgFromSendQueue() {
		
		String message = this.sendQueue.firstElement();
		this.sendQueue.removeElementAt(0);
		return message;
	}
	
	public synchronized void putMsgToSendQueue( String message ) {
		
		sendQueue.add(message);
	}
	
	
	
	/*====================================== related to ServerThread ======================================*/
	
	public ServerThread( Socket socket ) throws IOException {
		
		client = new CommunicateWithClient(socket);
		recvQueue = new Vector<String>();
		sendQueue = new Vector<String>();
		account = null;
	}
	
	
	private int signIn() throws IOException {
		
		String loginMsg = null;
		
		
		loginMsg = client.recvFromClient();
		
		if (MessageOperate.getMsgType(loginMsg) != MessageOperate.LOGIN) {
			
			System.out.println("[ ERROR ] 未登录，试图进行其他操作！");
			return 1;   // 未登录，并试图进行其他操作
			
		}
		
		Login loginInfo = MessageOperate.getLoginAccountInfo(loginMsg); // 从客户端获取登陆信息
		
		if (Server.isUserOnline(loginInfo.getAccountId())) {  // 用户重复登录
			
			// TODO 需要给用户反馈不同的提示
			putMsgToSendQueue(MessageOperate.sendNotFinishMsg());
			System.out.println("[ ERROR ] 用户重复登陆！");
			return Integer.parseInt(loginInfo.getAccountId());   // 重复登录错误代码
		}
		
		account = (new DatabaseOperator()).isLoginInfoCorrect(loginInfo); // 在数据库中查询登陆信息
		
		if (account != null) { // 登录信息与数据库中比对成功
			
			// 在服务器的服务子线程数据库中注册该线程
			Server.regServerThread(this);
			
			// 客户服务子线程创建其子线程：收线程和发线程，注意：收应该首先创建
			// 由于在发线程中返回登录成功信息，因此放在后面创建以确保，收发线程都已创建
			recvThread = new RecvThread(client, account.getID(), this);
			sendThread = new SendThread(client, account.getID(), this);
			
			System.out.println("[ LOGIN ] 用户ID：" + loginInfo.getAccountId() + " login successful!");
			System.out.println("[ READY ] 当前在线人数【 "
					                   + Server.getOnlineCounter() + " 】");
			
			recvThread.start(); // 负责监听有没有消息到达，有则把这些消息加入到接受队列中，由ServerThread处理
			sendThread.start(); // 负责监听消息发送队列中有没有消息
			
			// TODO 对线程创建失败的处理
			
			putMsgToSendQueue(MessageOperate.sendFinishMsg());
			
			return -1; // 登录成功
			
		} else { // 登录信息与数据库中比对失败
			
			// TODO 这里最好加一下返回失败代码
			
			client.sendToClient(MessageOperate.sendNotFinishMsg());
			
			System.out.println("[ READY ] ID:" + loginInfo.getAccountId() + " 登录信息验证失败，返回失败反馈！");
			
			return 0;   // 登录验证失败
		}
		
	}
	
	private boolean logout() {
		
		try {
			
			// 在服务器服务子线程数据库中注销该线程
			Server.delServerThread(account.getID());
			
			System.out.println("[ LOGIN ] 用户ID：" + account.getID() + " logout!");
			System.out.println("[ READY ] 当前在线人数【 "
					                   + Server.getOnlineCounter() + " 】");
			
			recvThread.setExit(true);
			sendThread.setExit(true);
			recvThread.join();
			sendThread.join();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
		return true;
		
	}
	
	private void sendFriendsList() {
		
		try {
			putMsgToSendQueue(
					MessageOperate.sendFriendList(
							new DatabaseOperator().getFriendListFromDb(
									account.getID())));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void run() {
		
		int signInStatus = 0;
		
		try {
			signInStatus = signIn();
			if (signInStatus < 0) {
				
				// 登陆成功用户为登录状态，线程持续接受客户端消息，直到判定用户下线
				while (account.getOnLine()) {
					
					if (!recvQueue.isEmpty()) {
						
						String message = getMsgFromRecvQueue();
						
						switch (MessageOperate.getMsgType(message)) {        // 判断请求类型
							
							case MessageOperate.FRIENDLIST:
								sendFriendsList();
								break;
							
							case MessageOperate.CHAT:
								Server.sendToOne(MessageOperate.getTargetId(message), message);
								break;
							
							default:
								break;
						} // end switch
					} // end if - !recvQueue.isEmpty()
					
				} // end while;
				
				logout();
			} // end if - sign in
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			
			try {
				client.endConnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (signInStatus >= 10000)
				System.out.println("[ READY ] 重复用户ID：" + signInStatus + " 服务子线程结束！");
			else if(signInStatus == -1 )
				System.out.println("[ READY ] 用户ID：" + account.getID() + " 服务子线程结束！");
			else
				System.out.println("[ READY ] 用户服务子线程结束！");
			
		}
	}
	
}
