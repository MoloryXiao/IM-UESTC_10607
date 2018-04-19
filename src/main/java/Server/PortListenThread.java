package Server; /**
 *
 */

import network.NetworkForServer.*;

import java.net.Socket;

/**
 * @author 97njczh
 */
public class PortListenThread extends Thread {
	
	private static final int MAX_ERROR = 5;             // 连续创建线程失败次数
	private static final int SLEEP_TIME = 10000;        // 线程睡眠时间
	
	private NetworkForServer loginSock = new NetworkForServer();
	private int listenPort;
	
	public PortListenThread( int port ) {
		
		listenPort = port;
		
	}
	
	public void setListenPort( int listenPort ) {
		
		this.listenPort = listenPort;
	}
	
	public int getListenPort() {
		
		return listenPort;
	}
	
	private boolean setupPortListen( int port ) {
		
		if (loginSock.setServer(port)) {
			
			ShowDate.showDate();
			System.out.println("[  O K  ] 监听线程就绪！正在监听: " + port + " 端口...");
			return true;
			
		} else {
			
			ShowDate.showDate();
			System.out.println("[ ERROR ] 监听线程启动出现错误，正在退出...");
			return false;
			
		}
		
	}
	
	private boolean portListen() {
		
		try {
			
			int errorCounter = 0;
			
			do {
				
				Socket clientSocket = loginSock.waitConnectFromClient();
				
				System.out.println("===============================================================");
				ShowDate.showDate();
				System.out.println("[  NEW  ] 请求传入！远端地址为："
						                   + clientSocket.getRemoteSocketAddress());
				
				if (Server.newServerThread(clientSocket))   // 转交由Server处理
					errorCounter = (errorCounter > 0) ? errorCounter-- : errorCounter;
				else
					errorCounter++;
				
			} while (errorCounter < MAX_ERROR);
			
			// TODO 查一下java线程连接的数量限制
			ShowDate.showDate();
			System.out.println("[ ERROR ] 连续 " + MAX_ERROR + " 次服务线程创建失败，"
					                   + "监听暂停，于" + SLEEP_TIME / 1000 + "秒后重启");
			return true;
			
		} catch (Exception e) {
			
			ShowDate.showDate();
			System.out.println("[ ERROR ] Socket建立失败！监听停止！！");
			e.printStackTrace();
			
			return false;
		}
	}
	
	
	public void closeServer() {
	
	}
	
	@Override
	public void run() {
		
		if (setupPortListen(listenPort))  // 启动监听
		{
			while (portListen()) // 监听端口，并与客户端创建连接
			{
				try {
					
					Thread.sleep(SLEEP_TIME);   // 无法创建连接，等待SLEEP_TIME后重试
					ShowDate.showDate();
					System.out.println("[  O K  ] 监听线程重新启动！");
					
				} catch (InterruptedException e) {
					
					e.printStackTrace();
					ShowDate.showDate();
					System.out.println("[ ERROR ] 监听线程休眠失败！");
					break;
					
				}
			}
		}
		ShowDate.showDate();
		System.out.println("[ ATTENTION ] 监听线程退出！");
	}
	
}
