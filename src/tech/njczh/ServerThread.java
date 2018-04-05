/**
 * 
 */

package tech.njczh;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

import com.mysql.fabric.xmlrpc.Client;

import tech.njczh.Server.Account;
import tech.njczh.Server.CommunicateWithClient;
import tech.njczh.Server.Login;

/**
 * @author 97njczh
 *
 */
public class ServerThread extends Thread {

	private CommunicateWithClient client;
	private Account account;
	private OnlineUser onlineUser; // = account.id + CommunicateWithClient.socket

	private static int onlineCounter = 0;

	public ServerThread(Socket socket) {
		try {
			client = new CommunicateWithClient(socket);
			// System.out.println(client.getSocketInfo());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getAccountId() {
		return account.getID();
	}

	/**
	 * @return onlineUser
	 */
	public OnlineUser getOnlineUser() {
		return onlineUser;
	}

	private boolean signIn() throws IOException, SQLException {

		DatabaseOperator databaseOperator = new DatabaseOperator();

		Login loginInfo = new Login();
		loginInfo = client.getLoginAccountInfo();

		if (databaseOperator.isLoginInfoCorrect(loginInfo)) { // 登录信息与数据库中比对成功

			client.sendFinishMsg(); // 返回确认信息

			account = databaseOperator.getAccountById(loginInfo.getAccountId());
			account.setOnline(true);
			onlineCounter++;
			System.out.println("ID:\"" + loginInfo.getAccountId() + "\" login successful!");
			System.out.println("The current number of online users is [" + onlineCounter + "]");

			onlineUser = new OnlineUser(account.getID(), client.getSocket());

			ThreadManager.regRecvThread(this); // 在服务器接受子线程数据库中注册该线程
			ServerThread serverThread_Send = new ServerThread(client.getSocket());
			serverThread_Send.start();
			ThreadManager.regSendThread(serverThread_Send); // 在服务器发送子线程数据库中注册该线程

			client.sendFriendList(databaseOperator.getFriendListFromDb());

			return true;

		} else { // 登录信息与数据库中比对失败

			client.sendNotFinishMsg();
			System.out.println("ID:\"" + loginInfo.getAccountId() + "\" login failed!");

			return false;
		}

	}

	public void run() {

		// 判断请求类型
		try {
			String msg = client.recvFromClient();
			switch (client.getMsgType(msg)) {

			case CommunicateWithClient.LOGIN: // 登录请求
				signIn();
//				while (account.getOnlineStatus()) {
//					switch (client.getMsgType(client.recvFromClient())) {
//					case client.CHAT:						
//						
//						break;
//					case client.ADDFRIEND:
//						break;						
//					case client.DELETE:
//						break;
//					case client.SEARCH:
//						break;
//					default:
//						break;
//					}
//					{
//						client.re
//					}
//					// 心跳包检查用户状态
//					if (用户不在线) {
//						account.setOnline(false);
//						ThreadManager.delThread(onlineUser);
//					}
//				}
//				break;

			case CommunicateWithClient.REGISTOR: // 注册请求
				// Account registorInfo = new Account();
				break;

			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("线程退出！");
	}

}
