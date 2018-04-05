/**
 * 
 */

package tech.njczh;

import tech.njczh.Server.*;
import java.net.Socket;

/**
 * @author 97njczh
 *
 */
public class LoginServer {

	private NetworkForServer loginSock = new NetworkForServer();

	public boolean connectDbTest() {

		DatabaseOperator connectDatabase = new DatabaseOperator();

		if (connectDatabase.setupDatabase() && connectDatabase.connectTest()) {
			System.out.println("[ READY ] Database Connection");
			return true;
		} else {
			System.out.println("[ ERROR ] Database Connection");
			return false;
		}

	}

	public void setupServer(int port) {

		if (loginSock.setServer(port)) {
			System.out.println("[ READY ] Server Status");
			System.out.println("Listening port: [ " + port + " ]");
		} else {
			System.out.println("[ ERROR ] Server Status");
			return;
		}

		try {
			while (true) {

				Socket clientSocket = loginSock.waitConnectFromClient();
				System.out.println("Incoming request， the client address is：" + clientSocket.getRemoteSocketAddress());
				ServerThread serverThread = new ServerThread(clientSocket);
				serverThread.start();

			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	public void closeServer() {

	}

	public static void main(String[] args) {
		LoginServer loginServer = new LoginServer();

		/* 检查数据库连接 */
		if (!loginServer.connectDbTest())
			return;

		/* 启动服务器并开始监听端口:'9090' */
		loginServer.setupServer(9090);

		System.out.println("goodbye!");
	}
}
