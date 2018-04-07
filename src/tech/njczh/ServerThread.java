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
	// TODO 思考一下这里有没有必要为Account类型，还是说只需要一个String ID即可
	private Account account;
	private SendThread sendThread;

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

			onlineCounter++;

			System.out.println("ID:\"" + loginInfo.getAccountId() + "\" login successful!");
			System.out.println("The current number of online users is [" + onlineCounter + "]");

			// 在服务器服务子线程数据库中注册该线程
			ThreadManager.regServerThread(this);

			// 创建该客户对应的转发线程
			sendThread = new SendThread(client.getSocket(), account);
			sendThread.start();

			return true;

		} else { // 登录信息与数据库中比对失败
			// TODO 这里最好加一下返回失败代码
			client.sendNotFinishMsg();
			System.out.println("ID:\"" + loginInfo.getAccountId() + "\" login failed!");

			return false;
		}

	}

	private boolean logout() {

		try {
			onlineCounter--;
			System.out.println("ID:\"" + account.getID() + "\" logout!");
			System.out.println("The current number of online users is [" + onlineCounter + "]");

			// 在服务器服务子线程数据库中注销该线程
			ThreadManager.delServerThread(account.getID());

			sendThread.setExit(true);
			sendThread.join();

			System.out.println(account.getID() + "：SendThread 线程结束！");

		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
		return true;

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
					if (account == null) {
						System.out.println(client.getSocket() + "未登录，试图进行聊天");
						break;
					}
					// client.recvFromUserMsg();
				
					break;

				default:
					break;
				}
				// 登陆成功用户为登录状态，线程持续接受客户端消息，直到判定用户下线
			} while (account.getOnlineStatus());
			
			logout();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(account.getID() + "：ServerThread 线程结束！");
	}

}
