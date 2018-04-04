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
public class LoginServer
{
	
	NetworkForServer loginSock = new NetworkForServer();
	
	public void connectDbTest()
	{
		
		DatabaseOperator connectDatabase = new DatabaseOperator();
		connectDatabase.setupDatabase();
		if (connectDatabase.connectTest()) System.out.println("Datebase is ready!");
		
	}
	
	public void setupServer(int port)	{
		
		try {
			
			if (loginSock.setServer(port)) System.out.println("Server started successfully! Listening port: [ " + port + " ]");
			
			while (true) {
				
				Socket clientSocket = loginSock.waitConnectFromClient();
				System.out.println("Incoming request， the client address is：" + clientSocket.getRemoteSocketAddress());
				ServerThread sThread_Client = new ServerThread(clientSocket);
				sThread_Client.start();
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("oops! 请检查一下端口号：）");
		}
	}
	
	public void closeServer() {
		
	}
	
	public static void main(String[] args)
	{
		LoginServer loginServer = new LoginServer();
		
		/* 检查数据库连接 */
		loginServer.connectDbTest();
		
		/* 启动服务器并开始监听端口:'9090' */
		loginServer.setupServer(9090);
	}
}
