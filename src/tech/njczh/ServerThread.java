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
	private OnlineUser onlineUser;

	private static int onlineCounter = 0;

	/**
	 * 
	 */
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

		if (databaseOperator.isLoginInfoCorrect(loginInfo)) {

			// 登录信息与数据库中比对成功
			client.sendFinishMsg();
			account = databaseOperator.getAccountById(loginInfo.getAccountId());
			account.setOnline(true);
			onlineCounter++;
			System.out.println("ID:\"" + loginInfo.getAccountId() + "\" login successful!");
			System.out.println("The current number of online users is [" + onlineCounter + "]");
			
			onlineUser = new OnlineUser(account, client.getSocketInfo());
			
			client.sendFriendList(databaseOperator.getFriendListFromDb());
			
			return true;

		} else {

			// 登录信息与数据库中比对失败
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
				while (account.getOnlineStatus()) {
					// 心跳包检查用户状态
				}
				break;

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
