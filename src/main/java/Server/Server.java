package Server;

import Server.Database.DatabaseConnector;
import Server.util.AdminUtil;
import Server.util.LoggerProvider;
import network.commonClass.Message;
import network.networkDataPacketOperate.MessageOperate;

import java.io.IOException;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Map;

public class Server {
	
	/*===========================================================================================================*/
	
	// TODO group相关
	
	
	/*======================================= SingleClientThread Database =======================================*/
	
	private static Map<String, SingleClientThread> singleClientThreadDb = new Hashtable<String, SingleClientThread>();
	
	public static Map<String, SingleClientThread> getSingleClientThreadDb() {
		
		return singleClientThreadDb;
	}
	
	/*======================================= SingleClientThread Manager  =======================================*/
	
	public static boolean newServerThread( Socket clientSocket ) {
		
		try {
			
			SingleClientThread singleClientThread = new SingleClientThread(clientSocket);
			singleClientThread.start();
			LoggerProvider.logger.info("[  O K  ] 用户服务子线程创建成功！");
			return true;
			
		} catch (IOException e) {
			
			LoggerProvider.logger.error("[ ERROR ] 用户服务子线程创建失败，"
					                            + "无法为来自：" + clientSocket.getRemoteSocketAddress()
					                            + "的用户创建服务线程！");
			return false;
			
		}
		
	}
	
	public static void regServerThread( SingleClientThread singleClientThread ) {
		
		singleClientThreadDb.put(singleClientThread.getAccountId(), singleClientThread);
	}
	
	public static void delServerThread( String id ) {
		
		singleClientThreadDb.remove(id);
	}
	
	/**
	 * 判断该id的用户是否在线状态，即通过查询是否在客户服务子线程中来判断
	 *
	 * @param id 待判断用户的id
	 * @return 在客户服务子线程数据库中即在线，返回true；否则，返回false.
	 */
	public static boolean isUserOnline( String id ) {
		
		return singleClientThreadDb.containsKey(id);
		
	}
	
	/**
	 * @return int 在客户服务子线程中的线程数，即在线用户数；
	 */
	public static int getOnlineCounter() {
		
		return singleClientThreadDb.size();
	}
	
	public static void sendToAll() {
	
	}
	
	/**
	 * 转发消息
	 *
	 * @param message 待转发消息
	 * @return 是否成功添加至转发对象的发送队列
	 */
	public static boolean sendToOne( Message message ) {
		
		Boolean result = true;
		
		String targetId = MessageOperate.unpackEnvelope(message).getTargetAccountId();
		
		if (isUserOnline(targetId))
			singleClientThreadDb.get(targetId).putMsgToSendQueue(message);
		else
			result = false;
		
		return result;
		
	}
	
	/**
	 * 转发群消息
	 *
	 * @param message 待转发的群消息
	 */
	public static void sendToGroup( String message ) {
		
		// TODO
		
		// String targetGroupId = MessageOperate.unpackEnvelope(message).getTargetAccountId();
		
		// GroupManager.getMember();
		// for(string memberId : members){
		//      singleClient
		
	
	}
	
	public static void main( String[] args ) {
		
		LoggerProvider.logger.info("=================== IM-Server Version 0.7.0 ===================");
		
		if (!DatabaseConnector.setupDatabase()) {
			LoggerProvider.logger.error("[ ERROR ] 无法启动服务器！正在退出……");
			LoggerProvider.logger.info("=========================== goodbye! ===========================");
			System.exit(0);
		}
		
		// 启动服务器并开始监听端口:'9090'
		(new PortListenThread(9090)).start();
		
		// 运行时带cmd参数，进入命令行控制模式
		if (args.length == 1 && args[0].equals("cmd")) AdminUtil.adminMode();
		
		// System.out.println("[ ATTENTION ] 主线程退出!");
	}
	
}
