package Server;
/**
 *
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import Server.Database.DatabaseOperator;
import Server.util.LoggerProvider;
import network.commonClass.*;
import network.networkDataPacketOperate.*;
import network.NetworkForServer.*;

/**
 * @author 97njczh
 */
public class ThreadSingleClient extends Thread {
	
	private static final int HEARTBEAT_MAX = 3;
	
	private CommunicateWithClient client;
	private Account account;
	private int heartbeat;
	
	public String getAccountId() {
		
		return account.getId();
	}
	
	public void setAccountOnline() {
		
		account.setOnline(true);
	}
	
	public void setAccountOffline() {
		
		account.setOnline(false);
	}
	
	private ThreadRecv recvThread;
	private ThreadSend sendThread;
	
	
	/*===================================== [ related to ThreadRecv ] =====================================*/
	
	/**
	 * 构造函数
	 *
	 * @param socket 连接用的socket
	 * @throws IOException 连接异常
	 */
	ThreadSingleClient( Socket socket ) throws IOException {
		
		client = new CommunicateWithClient(socket);
		recvQueue = new LinkedBlockingDeque<Message>();
		sendQueue = new LinkedBlockingDeque<Message>();
		account = null;
		heartbeat = HEARTBEAT_MAX;
	}
	
	private BlockingDeque<Message> recvQueue;
	
	public boolean isRecvQueueEmpty() {
		
		return recvQueue.isEmpty();
	}
	
	/**
	 * 将一条消息报放入接受对列
	 *
	 * @param message 需要放入消息队列的一条消息报
	 */
	public void putMsgToRecvQueue( Message message ) {
		
		recvQueue.add(message);
	}
	
	/**
	 * 从消息队列中取一条消息，延迟5秒，若无消息则超时返回
	 *
	 * @return 接受队列中的一条消息
	 * @throws InterruptedException 超时中断
	 */
	public Message getMsgFromRecvQueue() throws InterruptedException {
		
		return recvQueue.poll(5, TimeUnit.SECONDS);    // 阻塞取
	}
	
	/**
	 * 用户下线，接受队列中还有消息为被处理，调用该函数获取
	 * 添加好友请求、添加好友反馈、删除好友、聊天，这几种类型的消息
	 *
	 * @return Message 接受队列中一条需要处理的离线消息
	 */
	public Message getMsgFromRecvQueueOffline() {
		
		Message notReqMsg;

label:
		while ((notReqMsg = recvQueue.poll()) != null) {
			
			switch (MessageOperate.getMsgType(notReqMsg)) {
				case MessageOperate.ADDFRIEND:
				case MessageOperate.CHAT:
				case MessageOperate.BACKADD:
				case MessageOperate.DELETE:
					break label;
			}
		}
		
		return notReqMsg;
		
	}
	
	
	
	/*===================================== [ related to sendThread ] =====================================*/
	
	public boolean getAccountOnlineStatus() {
		
		return account.getOnline();
	}
	
	private BlockingDeque<Message> sendQueue;
	
	public boolean isSendQueueEmpty() {
		
		return sendQueue.isEmpty();
	}
	
	public void putMsgToSendQueue( Message message ) {
		
		sendQueue.add(message);
	}
	
	public Message getMsgFromSendQueue() throws InterruptedException {
		
		return sendQueue.take();
	}
	
	
	
	/*================================= [ related to ThreadSingleClient ] ====================================*/
	
	
	private void resetHeartbeat() {
		
		heartbeat = HEARTBEAT_MAX;
	}
	
	private Message chatWithAi( Message msg ) {
		
		String sourceId = MessageOperate.unpackEnvelope(msg).getSourceAccountId();
		
		try {
			String info = URLEncoder.encode(MessageOperate.unpackEnvelope(msg).getText(), "utf-8");
			String userid = URLEncoder.encode(sourceId, "utf-8");
			String getURL = "http://www.tuling123.com/openapi/api?key=a8c67fb54b6a4648a92d7e84c6f4c20a"
					                + "&info=" + info + "&userid=" + userid;
			
			HttpURLConnection conn = (HttpURLConnection) (new URL(getURL)).openConnection();
			conn.connect();
			
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(conn.getInputStream(), "utf-8"));
			StringBuffer sBuffer = new StringBuffer();
			
			String temp;
			while ((temp = reader.readLine()) != null)
				sBuffer.append(temp);
			
			msg = MessageOperate.packageEnvelope(sourceId, "9999", sBuffer.substring(23, sBuffer.length() - 2));
			
			reader.close();
			conn.disconnect();
			
		} catch (IOException e) {
			
			msg = MessageOperate.packageEnvelope(sourceId, "9999", "小A不在!");
			e.printStackTrace();
		}
		
		return msg;
	}
	
	
	/**
	 * DESCRIPTION：将待发送的好友列表信息加入发送队列
	 */
	private void disposeFriendsListReq() {
		
		putMsgToSendQueue(
				MessageOperate.packageFriendList(
						DatabaseOperator.getFriendsList(
								account.getId())));
		
	}
	
	/**
	 * #1 ADD TIME：2018/06/22 22:29 Version 0.8.1
	 * DESCRIPTION：将待发送的好友列表信息加入发送队列
	 */
	private void disposeGroupsListReq() {
		
		putMsgToSendQueue(
				MessageOperate.packageGroupList(
						DatabaseOperator.getGroupsList(
								account.getId())));
	}
	
	/**
	 * DESCRIPTION：将待发送的用户个人信息加入发送队列
	 *
	 * @param msg 个人信息数据报，如果msg长度等于1，则按请求个人信息处理；
	 *            否则按修改个人信息处理。
	 */
	private void disposeMyselfInfoReq( Message msg ) {
		
		if (msg.getText().length() > 1) { // 修改个人信息
			
			if (DatabaseOperator.modifyMyInfo(MessageOperate.unpackageUserDetail(msg))) {
				
				account = MessageOperate.unpackageUserDetail(msg);
			}// TODO 修改个人信息失败
			
		} else { // 请求个人信息
			
			putMsgToSendQueue(MessageOperate.packageUserDetail(account));
		}
		
	}
	
	/**
	 * #1 ADD TIME：2018/06/22 22:22 Version 0.8.1
	 * DESCRIPTION：处理群组信息数据报
	 *
	 * @param msg 群组信息数据报，如果msg长度等于1，则按请求群组信息处理；
	 *            否则按修改群组信息处理，只有群所有者和群管理员可以修改群信息。
	 */
	private void disposeGroupInfoReq( Message msg ) {
		
		if (msg.getText().length() > 6) { // TODO 通过长度判断有bug
			// TODO 修改群组信息
//			// 修改群组信息，从msg中取出群组修改后的信息 DatabaseOperator.modifyMyInfo(account);
//			if (DatabaseOperator.modifyMyInfo(MessageOperate.unpackageUserDetail(msg))) {
//				account = MessageOperate.unpackageUserDetail(msg);
//			} else {
//				// TODO 修改个人信息失败
//			}
		
			
		} else { // 请求群组信息
			putMsgToSendQueue(
					MessageOperate.packageUpdateGroup(
							GroupManager.getGroupDetails(
									MessageOperate.unpackageAskUpdateGroup(msg))));
			
		}
	}
	
	/**
	 * 将客户端发来的查询好友信息请求的结果加入发送队列
	 *
	 * @param msg 好友信息请求数据包
	 */
	private void disposeFriendDetailsReq( Message msg ) {
		
		Account accountInfo = DatabaseOperator.getUserDetailsById(
				MessageOperate.unpackageOtherUserDetailAsk(msg).getTargetAccountId());
		
		if (Server.isUserOnline(accountInfo.getId()))
			accountInfo.setOnline(true);
		
		putMsgToSendQueue(
				MessageOperate.packageOtherUserDetail(accountInfo));
		
	}
	
	// TODO 与添加、删除、查找群请求复用
	
	/**
	 * 2018/04/29 10:39
	 * 2018/05/01 23:39 v0.2 尝试实现离线好友请求
	 * 处理添加好友请求数据报，判断该好友关系是否已经存在，存在则给添加者返回反馈
	 *
	 * @param msg 添加好友请求数据报
	 */
	private void disposeAddReq( Message msg ) {
		
		Envelope envelope = MessageOperate.unpackEnvelope(msg);
		String targetId = envelope.getTargetAccountId();
		String sourceId = envelope.getSourceAccountId();
		
		if (DatabaseOperator.isFriendAlready(sourceId, targetId) || targetId.equals(sourceId)) {
			// 如果已经是好友，则向好友添加请求发起者返回失败反馈
			putMsgToSendQueue(
					MessageOperate.packageAddFriendFeedbackMsg(
							new Envelope(targetId, sourceId, "false")));
			return;
		}
		
		/*=================================================================================================*/
		
		/* A给B发送好友请求，记录一次申请 */
		if (!AddFriendReqManager.regAddFriendReq(targetId + sourceId))
			return; // 防止重复保存
		
		/* 转发添加好友请求 */
		Server.sendToOne(msg);
		
		/* 保存未处理的好友请求，在用户上线时推送给该用户 */
		OfflineMsgRegister.regOfflineReqMsg(targetId, sourceId);
		
	}
	
	/**
	 * 读取用户好友请求反馈，如果被添加者同意添加，则在数据库中添加该关系
	 *
	 * @param msg 添加好友反馈数据报
	 */
	private void disposeAddFeedback( Message msg ) {
		
		Envelope envelope = MessageOperate.unpackAddFriendFeedbackMsg(msg);
		
		String targetId = envelope.getTargetAccountId();
		String sourceId = envelope.getSourceAccountId();
		
		// A给B发送好友请求，记录了一次申请；B在给A发送好友请求反馈时，需要将申请撤销
		if (!AddFriendReqManager.delAddFriendReq(sourceId, targetId))
			return;
		
		if (envelope.getText().equals("true")) { // 被加好友同意添加申请
			
			if (!DatabaseOperator.addFriend(targetId, sourceId)) { // 在数据库中添加好友关系失败，则给双方都发送失败反馈
				
				LoggerProvider.logger.error("[ ERROR ] 在数据库中加入好友失败！好友关系："
						                            + targetId + " and " + sourceId);
				
				msg = MessageOperate.packageAddFriendFeedbackMsg(
						new Envelope(targetId, sourceId, "false"));
				
				putMsgToSendQueue(msg); // 给好友添加请求接受者发送反馈
			}
		}
		
		OfflineMsgRegister.removeOfflineReqMsg(sourceId, targetId); // 该好友请求已处理，删除该记录
		
		Server.sendToOne(msg);  // 给好友添加请求发起者转发该反馈
	}
	
	/**
	 * 处理删除好友请求，从数据库中删除此好友关系
	 *
	 * @param msg 删除好友请求
	 */
	private void disposeDelReq( Message msg ) {
		
		Envelope envelope = MessageOperate.unpackDelFriendMsg(msg);
		
		if (DatabaseOperator.delFriend(envelope.getSourceAccountId(), envelope.getTargetAccountId())) {
			// TODO 删除好友成功
		}
	}
	
	/**
	 * 处理查找好友请求，转发查找到好友信息；
	 * 没查找到，转发空字符串
	 *
	 * @param msg 查找好友请求
	 */
	private void disposeSearchReq( Message msg ) {
		
		Envelope envelope = MessageOperate.unpackSearchUserIdMsg(msg);
		
		putMsgToSendQueue(
				MessageOperate.packageSearchResultMsg(
						DatabaseOperator.searchUserById(
								envelope.getTargetAccountId())));
		
	}
	
	/**
	 * #1 MOD TIME：2018/06/22 15:26 version 0.8.1
	 * DESCRIPTION：处理私聊与群聊消息
	 *
	 * @param msg 私聊与群聊消息
	 */
	private void disposeChatMsg( Message msg ) {
		
		Envelope envelope = MessageOperate.unpackEnvelope(msg);
		String targetId = envelope.getTargetAccountId();
		String sourceId = envelope.getSourceAccountId();
		
		if (targetId.charAt(0) == 'g') {
			
			/*-------------------------------------- [ 群聊消息 ] --------------------------------------*/
			
			SessionGroup groupSession = SessionStore.getGroupSession(targetId);
			
			groupSession.addToChatMsgList(msg); // 保存群聊消息
			
			boolean[] result = Server.sendToGroup(msg); // 转发群消息，并记录转发情况
			
			groupSession.updateChatCursor(result);  // 更新消息阅读游标
			
			ArrayList<Account> memberIds = GroupManager.getGroupDetails(targetId).getGroupMembers();
			for (int i = 0; i < result.length; i++)
				if (!result[i])
					// 记录群离线记录时，应该将消息发送人SourceId替换为群，而接受者Target替换为群成员
					OfflineMsgRegister.regOfflineChatMsg(memberIds.get(i).getId(), targetId);
			
		} else {
			
			/*-------------------------------------- [ 私聊消息 ] --------------------------------------*/
			
			if (targetId.equals("9999")) {    // 与AI聊天
				Server.sendToOne(chatWithAi(msg));
				return;
			}
			
			SessionPrivate privateSession = SessionStore.getPrivateSession(targetId, sourceId);
			
			privateSession.addToChatMsgList(msg);
			
			if (Server.sendToOne(msg)) { // 转发聊天消息，判断是否为离线消息
				
				privateSession.updateBothChatCursor();  // 消息接受者在线，则更新双方阅读游标
				
			} else {
				
				privateSession.updateChatCursor(sourceId);  // 消息接受者不在线，则仅更新发送者阅读游标
				OfflineMsgRegister.regOfflineChatMsg(targetId, sourceId);   // 记录有离线消息
				
			}
			
		}
		
	}
	
	// TODO
	private void disposeCreateGroupReq() {
	
	}

//	private void dispose
	
	
	/* ============================================== [ 主逻辑部分 ] ========================================== */
	
	private int signIn() {
		
		Message loginMsg;
		
		try {
			//用户尚未登录，暂时没有创建收发子线程，调用底层recv函数接收好友登录信息
			loginMsg = client.recvFromClient();
			
		} catch (IOException e) {
			
			LoggerProvider.logger.error("[ ERROR ] 接受用户登录请求（及信息）失败！无法处理用户登录！");
			return 1;   // 登录错误代码 1，接受用户登录请求失败
		}
		
		if (MessageOperate.getMsgType(loginMsg) != MessageOperate.LOGIN) {
			
			LoggerProvider.logger.error("[ ERROR ] 未登录，试图进行其他操作！");
			return 2;   // 登录错误代码 2，未登录并试图进行其他操作
			
		}
		
		Login loginInfo = MessageOperate.unpackLoginMsg(loginMsg);    // 解析出用户登录信息
		
		if (Server.isUserOnline(loginInfo.getAccountId())) {  // 用户重复登录
			
			try {
				
				client.sendToClient(MessageOperate.packageNotFinishMsg());    // TODO 需要给用户反馈不同的提示
				LoggerProvider.logger.error("[ ERROR ] 用户ID：" + loginInfo.getAccountId() + " 重复登陆！已发送反馈！");
				
			} catch (IOException e) {
				
				LoggerProvider.logger.error("[ ERROR ] 用户ID：" + loginInfo.getAccountId() + " 用户重复登陆反馈发送失败！"
						                            + "异常：" + e.getMessage());
			}
			// 登录错误代码 用户id，该用户重复登陆
			return Integer.parseInt(loginInfo.getAccountId());
		}
		
		// 在数据库中查询登陆信息，查询成功返回用户account信息，否则返回null
		account = DatabaseOperator.getUserDetailsByLoginInfo(loginInfo);
		
		if (account != null) { // 登录信息与数据库中比对成功
			
			// 设置用户在线
			setAccountOnline();
			
			// 在服务器的服务子线程数据库中注册该线程
			Server.regServerThread(this);
			
			// 客户服务子线程创建其子线程：收线程和发线程，注意：收应该首先创建
			// 由于在发线程中返回登录成功信息，因此放在后面创建以确保收发线程都已创建
			recvThread = new ThreadRecv(client, account.getId(), this);
			sendThread = new ThreadSend(client, account.getId(), this);
			
			LoggerProvider.logger.info("[ LOGIN ] 用户ID：" + loginInfo.getAccountId() + " login successful!");
			LoggerProvider.logger.info("[  O K  ] 当前在线人数【 "
					                           + Server.getOnlineCounter() + " 】");
			
			recvThread.start(); // 负责监听有没有消息到达，有则把这些消息加入到接收队列中，由ServerThread处理
			sendThread.start(); // 负责监听消息发送队列中有没有消息
			
			// TODO 对线程创建失败的处理
			
			putMsgToSendQueue(MessageOperate.packageFinishMsg());
			
			return -1; // 登录成功成功代码 -1
			
		} else { // 登录信息与数据库中比对失败
			
			try {
				
				client.sendToClient(MessageOperate.packageNotFinishMsg()); // 登录失败，收发子线程无法创建，因此需要使用底层发送函数
				LoggerProvider.logger.info("[  O K  ] 用户ID：" + loginInfo.getAccountId() + " 登录信息验证失败，返回失败反馈！");
				
			} catch (IOException e) {
				
				LoggerProvider.logger.error("[ ERROR ] 用户ID：" + loginInfo.getAccountId() + " 用户登录失败反馈发送失败！异常：" + e.getMessage());
			}
			
			return 0;   // 登录错误代码 0，登录验证失败
		}
		
	}
	
	/**
	 * 2018.05.02
	 */
	private void getOfflineMsg() {
		
		String targetId = account.getId();
		
		/* *** 是否有发给该用户的离线请求类消息 *** */
		if (OfflineMsgRegister.isAnyOfflineReqMsg(targetId)) {
			
			HashSet<String> sourceIds = OfflineMsgRegister.getReqMsgSourceIds(targetId); // 获取发给该用户离线请求的用户列表
			Iterator<String> it = sourceIds.iterator();
			while (it.hasNext()) {
				
				String sourceId = it.next();
				putMsgToSendQueue(
						MessageOperate.packageAddFriendMsg(
								new Envelope(targetId, sourceId, "")));
				
			}
			if (!it.hasNext()) {
				LoggerProvider.logger.info("[  O K  ] 用户ID：" + targetId + " 离线请求类消息已全部发送！");
			}
			
		}
		
		/* *** 是否有离线聊天消息，没有返回 *** */
		if (OfflineMsgRegister.isAnyOfflineChatMsg(targetId)) {
			
			HashSet<String> sourceIds = OfflineMsgRegister.getChatMsgSourceIds(targetId);
			Iterator<String> it = sourceIds.iterator();
			while (it.hasNext()) {
				
				String sourceId = it.next();
				
				if (sourceId.charAt(0) == 'g') {
					
					SessionGroup groupSession = SessionStore.getGroupSession(sourceId);
					Message msg = groupSession.getNextUnreadMsg(targetId);
					while (msg != null) {
						putMsgToSendQueue(msg);
						msg = groupSession.getNextUnreadMsg(targetId);
					}
					
				} else {
					
					SessionPrivate privateSession = SessionStore.getPrivateSession(targetId, sourceId);
					Message msg = privateSession.getNextUnreadMsg(targetId);
					while (msg != null) {
						putMsgToSendQueue(msg);
						msg = privateSession.getNextUnreadMsg(targetId);
					}
				}
				
				it.remove();
				
			}
			if (sourceIds.isEmpty()) {
				LoggerProvider.logger.info("[  O K  ] 用户ID：" + targetId + " 离线聊天消息已全部发送！");
			}
		}
		
	}
	
	private void logicService() {
		
		/* 登陆成功用户为登录状态，线程持续接受客户端消息，直到判定用户下线 */
		while (true) {
			
			Message message = null;
			
			try {
				if (account.getOnline()) {
					/* 用户在线，阻塞接受用户消息，直到被中断 */
					message = getMsgFromRecvQueue();
					if (message == null) {
						if (heartbeat-- != 0) {
							continue;
						} else {    // 心跳包失败
							sendThread.interrupt();
							setAccountOffline();
							return;
						}
					}
				} else {
					/* 用户掉线，将接收队列中非请求消息处理完 */
					message = getMsgFromRecvQueueOffline();
					if (message == null) {
						return;
					}
				}
				
			} catch (InterruptedException e) {
				sendThread.interrupt();
				setAccountOffline();
				continue;
			}
			
			/* 判断请求类型 */
			switch (MessageOperate.getMsgType(message)) {
				
				case MessageOperate.FRIENDLIST:             // 请求好友列表
					disposeFriendsListReq();
					break;
				
				case MessageOperate.GET_GROUP_LIST:         // 获取群列表
					disposeGroupsListReq();
					break;
				
				case MessageOperate.CHAT:                   // 转发聊天消息
					disposeChatMsg(message);
					break;
				
				case MessageOperate.USER_DETAIL:            // 修改个人信息数据报
				case MessageOperate.MYSELF:                 // 处理请求个人信息数据报：返回/设置个人信息
					disposeMyselfInfoReq(message);
					break;
				
				case MessageOperate.CHANGE_GROUP:           // 修改群信息请求
				case MessageOperate.UPDATE_GROUP:           // 处理更新群信息请求
					disposeGroupInfoReq(message);
					break;
				
				case MessageOperate.GET_OTHER_USER_DETAIL:  // 处理请求他人信息的数据报
					disposeFriendDetailsReq(message);
					break;
				
				case MessageOperate.ADDFRIEND:              // 转发添加好友请求
					disposeAddReq(message);
					break;
				
				case MessageOperate.BACKADD:                // 处理并转发添加好友反馈
					disposeAddFeedback(message);
					break;
				
				case MessageOperate.DELETE:                 // 处理删除好友请求
					disposeDelReq(message);
					break;
				
				case MessageOperate.SEARCH:                 // 处理查找好友请求
					disposeSearchReq(message);
					break;
				
				case MessageOperate.CREATE_GROUP:              // 创建群组请求
					// TODO disposeAddReq();
					break;
				
				default:
					LoggerProvider.logger.warn("[ ERROR ] 无法识别的数据报！内容为：\'" + message + "\'");
					break;
			} // end switch
			
			resetHeartbeat();
			
		} // end while;
	}
	
	private void logout() {
		
		try {
			
			/* 在服务器用户服务子线程数据库中注销该线程 */
			Server.delServerThread(account.getId());
			
			LoggerProvider.logger.info("[ LOGIN ] 用户ID：" + account.getId() + " logout!");
			LoggerProvider.logger.info("[  O K  ] 当前在线人数【 "
					                           + Server.getOnlineCounter() + " 】");
			
			recvThread.setExit(true);
			sendThread.setExit(true);
			recvThread.join();
			sendThread.join();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	private void closeSingleClientThread( int signInStatus ) {
		
		try {
			while (sendQueue.size() != 0) ;
			client.endConnect();    // 关闭套接字
			
		} catch (IOException e) {
			
			LoggerProvider.logger.error("[ ERROR ] 客户端地址：" + client.getClientSocket() + "套接字关闭失败！异常：" + e.getMessage());
		}
		
		if (signInStatus >= 10000) {
			LoggerProvider.logger.info("[  O K  ] 重复登录用户ID：" + signInStatus + " 服务子线程已结束！");
		} else if (signInStatus == -1) {
			LoggerProvider.logger.info("[  O K  ] 用户ID：" + account.getId() + " 服务子线程已结束！");
		} else {
			LoggerProvider.logger.info("[  O K  ] 用户服务子线程已结束！");
		}
		System.out.println("===============================================================");
		
	}
	
	/* =============================================== [ run ] =============================================== */
	
	public void run() {
		
		int signInStatus = signIn();
		
		if (signInStatus < 0) {
			
			getOfflineMsg();
			
			logicService();
			
			logout();
		}
		
		closeSingleClientThread(signInStatus);
	}
	
}
