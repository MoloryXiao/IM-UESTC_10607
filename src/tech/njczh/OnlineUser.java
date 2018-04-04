/**
 * 
 */
package tech.njczh;

import java.net.ServerSocket;
import java.net.Socket;

import tech.njczh.Server.Account;

/**
 * @author 97njczh
 *
 */
public class OnlineUser {

	private Account account;
	private Socket socket;

	/**
	 * 
	 */
	public OnlineUser(Account account, Socket socket) {
		this.account = account;
		this.socket = socket;
	}

	/**
	 * @return account
	 */
	public Account getAccount() {
		return account;
	}

	/**
	 * @return socket
	 */
	public Socket getSocket() {
		return socket;
	}

	/**
	 * @param account
	 *            要设置的 account
	 */
	public void setAccount(Account account) {
		this.account = account;
	}

	/**
	 * @param socket
	 *            要设置的 socket
	 */
	public void setSocket(Socket socket) {
		this.socket = socket;
	}

}
