/**
 * 
 */
package tech.njczh;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

import tech.njczh.Server.Account;
import tech.njczh.Server.CommunicateWithClient;

/**
 * 服务器负责接收来自服务子线程的数据 并发送给目标客户端
 * 
 * @author 97njczh
 */
public class SendThread extends Thread {

	private volatile boolean exit = false;

	private CommunicateWithClient client;
	private Account account;

	/**
	 * @param exit
	 *            要设置的 exit
	 */
	public void setExit(boolean exit) {
		this.exit = exit;
	}

	/**
	 * @return account
	 */
	public Account getAccount() {
		return account;
	}

	/**
	 * @return account ID
	 */
	public String getAccountId() {
		return account.getID();
	}

	/**
	 * @throws IOException
	 * 
	 */
	public SendThread(Socket socket, Account account) throws IOException {

		client = new CommunicateWithClient(socket);
		this.account = account;

		// 在服务器发送子线程数据库中注册该线程
		ThreadManager.regSendThread(this);

	}

	public void run() {

		try {

			DatabaseOperator databaseOperator = new DatabaseOperator();

			client.sendFinishMsg(); // 返回登陆确认信息

			client.sendFriendList(databaseOperator.getFriendListFromDb(account.getID()));

			while (!exit) {

			}
			
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}

		ThreadManager.delSendThread(getAccountId());
	}

}
