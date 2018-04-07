/**
 * 
 */

package tech.njczh;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;


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

	private static int onlineCounter = 0;

	public ServerThread(Socket socket) throws IOException {

		client = new CommunicateWithClient(socket);
		account = null;
	}

	public String getAccountId() {

		return account.getID();

	}

	private boolean signIn() throws IOException, SQLException {

		DatabaseOperator databaseOperator = new DatabaseOperator();

		Login loginInfo = client.getLoginAccountInfo(); // 从客户端获取登陆信息

		account = databaseOperator.isLoginInfoCorrect(loginInfo); // 在数据库中查询登陆信息

		if (account != null) { // 登录信息与数据库中比对成功

			System.out.println("ID:\"" + loginInfo.getAccountId() + "\" login successful!");
			System.out.println("The current number of online users is [" + onlineCounter + "]");
			onlineCounter++;

			// 在服务器服务子线程数据库中注册该线程
			ThreadManager.regRecvThread(this);

			// 创建该客户对应的转发线程
			SendThread sendThread = new SendThread(client.getSocket(), account);
			sendThread.start();

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
			String msg;
			do {
				msg = client.recvFromClient();

				switch (client.getMsgType(msg)) {

				case CommunicateWithClient.LOGIN: // 登录请求
					signIn();
					break;

				case CommunicateWithClient.CHAT:
					if (account == null){
						System.out.println(client.getSocket()+"未登录，试图进行聊天");
						break;
					}
					break;
				// 登陆成功，持续接受客户端消息，直到判定用户下线

				// // 从客户端获取消息
				// Envelope envelope = client.recvFromUserMsg();
				// // 解析消息的收件人
				// String TargetId = envelope.getTargetAccountId();
				// // 根据收件人地址 转发给对应的发送线程

				// while (account.getOnlineStatus()) {
				// switch (client.getMsgType(client.recvFromClient())) {
				// case client.CHAT:
				//
				// break;
				// case client.ADDFRIEND:
				// break;
				// case client.DELETE:
				// break;
				// case client.SEARCH:
				// break;
				// default:
				// break;
				// }
				// {
				// client.re
				// }
				// // 心跳包检查用户状态
				// if (用户不在线) {
				// account.setOnline(false);
				// ThreadManager.delThread(onlineUser);
				// }
				// }
				// break;

				// case CommunicateWithClient.REGISTOR: // 注册请求
				// // Account registorInfo = new Account();
				// break;

				default:
					break;
				}
			} while (account.getOnlineStatus());

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("线程退出！");
	}

}
