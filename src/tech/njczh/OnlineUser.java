/**
 * 
 */
package tech.njczh;

import java.net.Socket;

/**
 * @author 97njczh
 *
 */
public class OnlineUser {

	private String id;
	private Socket socket;

	/**
	 * 
	 */
	public OnlineUser(String id, Socket socket) {
		this.id = id;
		this.socket = socket;
	}

	/**
	 * @return id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return socket
	 */
	public Socket getSocket() {
		return socket;
	}

	/**
	 * @param id
	 *            要设置的 id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @param socket
	 *            要设置的 socket
	 */
	public void setSocket(Socket socket) {
		this.socket = socket;
	}

}
