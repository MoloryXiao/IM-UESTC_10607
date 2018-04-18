package Server; /**
 *
 */

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import network.commonClass.*;
import network.networkDataPacketOperate.*;
import network.NetworkForServer.*;

/**
 * @author 97njczh
 */
public class ServerThread extends Thread {
	
	private CommunicateWithClient client;
	private Account account;
	private int heartbeat;
	private static final int HEARTBEAT_MAX = 3;
	
	public String getAccountId() {
		
		return account.getId();
	}
	
	public void setAccountOffline() {
		
		account.setOnline(false);
	}
	
	public void resetHeartbeat() {
		
		heartbeat = HEARTBEAT_MAX;
	}
	
	/*======================================= related to RecvThread =======================================*/
	
	private RecvThread recvThread;
	private BlockingDeque<String> recvQueue;
	
	public boolean isRecvQueueEmpty() {
		
		return recvQueue.isEmpty();
	}
	
	public String getMsgFromRecvQueue() throws InterruptedException {
		
		return recvQueue.poll(5, TimeUnit.SECONDS);    // 阻塞取
	}
	
	public void putMsgToRecvQueue( String message ) {
		
		recvQueue.add(message);     // 阻塞存
	}
	
	
	
	/*======================================= related to sendThread =======================================*/
	
	private SendThread sendThread;
	private BlockingDeque<String> sendQueue;
	
	public boolean isSendQueueEmpty() {
		
		return sendQueue.isEmpty();
	}
	
	public String getMsgFromSendQueue() throws InterruptedException {
		
		return sendQueue.take();
	}
	
	public void putMsgToSendQueue( String message ) {
		
		sendQueue.add(message);
	}
	
	
	
	/*====================================== related to ServerThread ======================================*/
	
	public ServerThread( Socket socket ) throws IOException {
		
		client = new CommunicateWithClient(socket);
		recvQueue = new LinkedBlockingDeque<String>();
		sendQueue = new LinkedBlockingDeque<String>();
		account = null;
		heartbeat = HEARTBEAT_MAX;
	}
	
	
	private int signIn() {
		
		String loginMsg = null;
		
		try {
			//用户尚未登录，暂时没有创建收发子线程，调用底层recv函数接收好友登录信息
			loginMsg = client.recvFromClient();
			
		} catch (IOException e) {
			e.printStackTrace();
			ShowDate.showDate();
			System.out.println("[ ERROR ] 接受用户登录请求（及信息）失败！无法处理用户登录！");
			return 1;   // 登录错误代码 1，接受用户登录请求失败
		}
		
		if (MessageOperate.getMsgType(loginMsg) != MessageOperate.LOGIN) {
			
			ShowDate.showDate();
			System.out.println("[ ERROR ] 未登录，试图进行其他操作！");
			return 2;   // 登录错误代码 2，未登录并试图进行其他操作
			
		}
		// 从接收队列取出登录请求，并解析出用户登录信息
		Login loginInfo = MessageOperate.unpackLoginMsg(loginMsg);
		
		if (Server.isUserOnline(loginInfo.getAccountId())) {  // 用户重复登录
			
			// TODO 需要给用户反馈不同的提示
			putMsgToSendQueue(MessageOperate.packageNotFinishMsg());
			ShowDate.showDate();
			System.out.println("[ ERROR ] 用户重复登陆！");
			return Integer.parseInt(loginInfo.getAccountId());   // 登录错误代码 用户id，该用户重复登陆
		}
		
		// 在数据库中查询登陆信息，查询成功返回用户account信息，否则返回null
		account = (new DatabaseOperator()).isLoginInfoCorrect(loginInfo);
		
		if (account != null) { // 登录信息与数据库中比对成功
			
			// 在服务器的服务子线程数据库中注册该线程
			Server.regServerThread(this);
			
			// 客户服务子线程创建其子线程：收线程和发线程，注意：收应该首先创建
			// 由于在发线程中返回登录成功信息，因此放在后面创建以确保收发线程都已创建
			recvThread = new RecvThread(client, account.getId(), this);
			sendThread = new SendThread(client, account.getId(), this);
			
			ShowDate.showDate();
			System.out.println("[ LOGIN ] 用户ID：" + loginInfo.getAccountId() + " login successful!");
			ShowDate.showDate();
			System.out.println("[  O K  ] 当前在线人数【 "
					                   + Server.getOnlineCounter() + " 】");
			
			recvThread.start(); // 负责监听有没有消息到达，有则把这些消息加入到接收队列中，由ServerThread处理
			sendThread.start(); // 负责监听消息发送队列中有没有消息
			
			// TODO 对线程创建失败的处理
			
			putMsgToSendQueue(MessageOperate.packageFinishMsg());
			
			return -1; // 登录成功成功代码 -1
			
		} else { // 登录信息与数据库中比对失败
			
			try {
				// TODO 这里最好加一下返回失败代码
				client.sendToClient(MessageOperate.packageNotFinishMsg()); // 登录失败，收发子线程无法创建
				ShowDate.showDate();
				System.out.println("[  O K  ] 用户ID：" + loginInfo.getAccountId() + " 登录信息验证失败，返回失败反馈！");
				
			} catch (IOException e) {
				System.out.println("[ ERROR ] 用户ID：" + loginInfo.getAccountId() + " 用户登录失败反馈发送失败！");
				ShowDate.showDate();
				e.printStackTrace();
			}
			
			return 0;   // 登录错误代码 0，登录验证失败
		}
		
	}
	
	private boolean logout() {
		
		try {
			
			/* 在服务器用户服务子线程数据库中注销该线程 */
			Server.delServerThread(account.getId());
			
			ShowDate.showDate();
			System.out.println("[ LOGIN ] 用户ID：" + account.getId() + " logout!");
			ShowDate.showDate();
			System.out.println("[  O K  ] 当前在线人数【 "
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
	
	/**
	 * 将待发送的好友列表信息加入发送队列
	 */
	private void sendFriendsList() {
		
		putMsgToSendQueue(
				MessageOperate.packageFriendList(
						new DatabaseOperator().getFriendListFromDb(
								account.getId())));
		
	}
	
	
	/**
	 * 将待发送的用户个人信息加入发送队列
	 */
	public void sendMyselfInfo() {
		
		putMsgToSendQueue(
				MessageOperate.packageUserInfo(account));
		
	}
	
	public void run() {
		
		int signInStatus = signIn();
		
		if (signInStatus < 0) {
			
			/* 登陆成功用户为登录状态，线程持续接受客户端消息，直到判定用户下线 */
			while (account.getOnline()) {
				
				String message = null;
				
				try {
					/* 阻塞接受用户消息，直到被中断 */
					message = getMsgFromRecvQueue();
					
				} catch (InterruptedException e) {
					// TODO 如果 sendQueue 不为空，需要保存这些消息
					sendThread.interrupt();
					setAccountOffline();
					break;
				}
				
				if (message == null) {
					if (heartbeat-- != 0) {
						continue;
					} else {
						setAccountOffline();
						break;
					}
				}
				/* 判断请求类型 */
				switch (MessageOperate.getMsgType(message)) {
					
					case MessageOperate.FRIENDLIST: // 请求好友列表
						sendFriendsList();
						break;
					
					case MessageOperate.MYSELF:     // 请求个人信息
						sendMyselfInfo();
						break;
					
					case MessageOperate.CHAT:       // 转发消息
						Server.sendToOne(message);
						break;
					
					default:
						break;
				} // end switch
				
				resetHeartbeat();
				
			} // end while;
			
			logout();
		} // end if - sign in
		
		try {
			
			client.endConnect();    // 关闭套接字
			
		} catch (IOException e) {
			e.printStackTrace();
			ShowDate.showDate();
			System.out.println("[ ERROR ] 客户端地址：" + client.getClientSocket() + "套接字关闭失败！");
		}
		
		if (signInStatus >= 10000) {
			ShowDate.showDate();
			System.out.println("[  O K  ] 重复登录用户ID：" + signInStatus + " 服务子线程已结束！");
		} else if (signInStatus == -1) {
			ShowDate.showDate();
			System.out.println("[  O K  ] 用户ID：" + account.getId() + " 服务子线程已结束！");
		} else {
			ShowDate.showDate();
			System.out.println("[  O K  ] 用户服务子线程已结束！");
		}
		System.out.println("===============================================================");
		
	}
	
}
