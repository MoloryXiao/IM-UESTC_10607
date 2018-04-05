/**
 * 
 */
package tech.njczh;

import java.io.IOException;
import java.net.Socket;

import tech.njczh.Server.Account;
import tech.njczh.Server.CommunicateWithClient;

/**
 * 服务器负责接收来自服务子线程的数据 并发送给目标客户端
 * 
 * @author 97njczh
 */
public class SendThread extends Thread {

	private CommunicateWithClient client;
	private Account account;

	/**
	 * @throws IOException
	 * 
	 */
	public SendThread(Socket socket) throws IOException {

		client = new CommunicateWithClient(socket);

		// 在服务器发送子线程数据库中注册该线程
		ThreadManager.regSendThread(this);

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

	public void run() {
		
		try {
			client.sendFinishMsg();			// 返回登陆确认信息			
		} catch (IOException e) {			
			e.printStackTrace();			
		}

	}

}
