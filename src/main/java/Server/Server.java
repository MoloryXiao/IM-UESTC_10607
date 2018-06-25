package Server;

import Server.Database.DatabaseConnector;
import Server.util.AdminUtil;
import Server.util.FileOperator;
import Server.util.LoggerProvider;
import network.commonClass.Account;
import network.commonClass.Message;
import network.networkDataPacketOperate.MessageOperate;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

public class Server {
	
	/*===========================================================================================================*/
	
	// TODO group相关
	
	
	/*======================================= ThreadSingleClient Database =======================================*/
	
	private static Map<String, ThreadSingleClient> singleClientThreadDb = new Hashtable<String, ThreadSingleClient>();
	
	public static Map<String, ThreadSingleClient> getSingleClientThreadDb() {
		
		return singleClientThreadDb;
	}
	
	/*======================================= ThreadSingleClient Manager  =======================================*/
	
	public static boolean newServerThread( Socket clientSocket ) {
		
		try {
			
			ThreadSingleClient singleClientThread = new ThreadSingleClient(clientSocket);
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
	
	public static void regServerThread( ThreadSingleClient singleClientThread ) {
		
		singleClientThreadDb.put(singleClientThread.getAccountId(), singleClientThread);
	}
	
	public static void delServerThread( String id ) {
		
		singleClientThreadDb.remove(id);
	}
	
	/**
	 * 判断该id的用户是否在线状态
	 * 即通过用户服务子线程是否在线程库以及用户在线位是否为true来判断
	 *
	 * @param id 待判断用户的id
	 * @return 用户在线，返回true；否则，返回false.
	 */
	public static boolean isUserOnline( String id ) {
		
		return singleClientThreadDb.containsKey(id)
				       && singleClientThreadDb.get(id).getAccountOnlineStatus();
	}
	
	/**
	 * @return int 在客户服务子线程中的线程数，即在线用户数；
	 */
	public static int getOnlineCounter() {
		
		return singleClientThreadDb.size();
	}
	
	/**
	 * MOD #2 TIME：2018/06/22 15:23 version 0.8.1
	 * DESCRIPTION：转发消息
	 *
	 * @param message 待转发消息
	 * @return 是否成功添加至转发对象的发送队列
	 */
	public static boolean sendToOne( Message message ) {
		
		Boolean result = false;
		
		String targetId = MessageOperate.unpackEnvelope(message).getTargetAccountId();
		
		if (isUserOnline(targetId)) {
			singleClientThreadDb.get(targetId).putMsgToSendQueue(message);
			result = true;
		}
		
		return result;
		
	}
	
	/**
	 * MOD #1 TIME：2018/06/22 15:46 version 0.8.1
	 * DESCRIPTION：转发群消息
	 *
	 * @param message 待转发的群消息
	 */
	public static boolean[] sendToGroup( Message message ) {
		
		String sourceUserId = MessageOperate.unpackEnvelope(message).getSourceAccountId();
		String targetGroupId = MessageOperate.unpackEnvelope(message).getTargetAccountId();
		ArrayList<Account> groupMembers = GroupManager.getGroupDetails(targetGroupId).getGroupMembers();
		
		boolean[] result = new boolean[groupMembers.size()]; // 转发结果数组，默认值为false
		
		for (int i = 0; i < groupMembers.size(); i++) {
			
			if (sourceUserId.equals(groupMembers.get(i).getId())) { // 如果是发送者本人
				result[i] = true;
				continue;
			} else if (isUserOnline(groupMembers.get(i).getId())) { // 如果群成员在线
				singleClientThreadDb.get(groupMembers.get(i).getId()).putMsgToSendQueue(message);
				result[i] = true;
			}
		}
		
		return result;
		
	}
	
	public static void main( String[] args ) {
		
		/*====================================== [ 初始化 ] ====================================== */
		
		LoggerProvider.logger.info("=================== IM-Server Version 0.8.2 ===================");
		
		if (!FileOperator.buildRuntimeEnv()) {
			LoggerProvider.logger.error("[ ERROR ] 无法保证运行文件环境！正在退出……");
			LoggerProvider.logger.info("=========================== goodbye! ===========================");
			System.exit(0);
		}
		
		if (!DatabaseConnector.setupDatabase()) {
			LoggerProvider.logger.error("[ ERROR ] 无法启动服务器！正在退出……");
			LoggerProvider.logger.info("=========================== goodbye! ===========================");
			System.exit(1);
		}
		
		if (!GroupManager.loadGroups(10)) {
			LoggerProvider.logger.error("[ ERROR ] 无法预载入群组信息！");
		}
		
		/* 启动服务器并开始监听端口:'9090' */
		(new ThreadPortListen(9090)).start();
		
		/* 运行时带cmd参数，进入命令行控制模式 */
		if (args.length == 1 && args[0].equals("cmd")) AdminUtil.adminMode();
		
		// System.out.println("[ ATTENTION ] 主线程退出!");
	}
	
}
