package Core;


import java.io.FileNotFoundException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import network.NetworkForClient.NetworkForClient;
import network.commonClass.Account;
import network.commonClass.Envelope;
import network.commonClass.Message;
import network.commonClass.Group;
import network.messageOperate.MessageOperate;
/**
 * 程序入口 聊天软件客户端
 * @author Murrey LeeKadima
 * @version 2.2
 * 【添加】创建个人信息窗口、好友信息窗口、个人信息修改窗口的处理函数
 * 【添加】服务器反馈的个人信息、好友信息的处理函数
 * 【修改】获取的个人信息函数(更改为详细版)
 * 【添加】函数记录将要创建信息窗口的好友ID
 * @version 2.1
 * 【添加】在管理好友中，添加和删除好友
 * @version 2.0
 * 【添加】离线消息功能 并且窗口未打开也可正常接收消息
 * @version 1.0
 * Inital.
 */
public class ChatClient{
	private final String host_name = "39.108.95.130";	// server location
//	private final String host_name = "192.168.1.103";	// local area for test
//	private final String host_name = "127.0.0.1";
	private final int contact_port = 9090;
	
	private volatile boolean isFriendsListWindCreated = false;
	
	private RecvSendController 					rs_controller;				// 收发线程控制器
	private NetworkForClient 					net_controller;				// 通信接口控制器
	private WindowProducer 						wind_controller;			// 窗口创建控制器
	private EnvelopeRepertory 					repertory_envelope;
	
	
	private LoginWindow 						wind_login;					// 登陆窗口
	private FriendsListWindow 					wind_friendsList;			// 好友列表
	private AddFriendWindow 					wind_addfriend;				// 添加好友窗口
	private AccountInfoShowWindow 				wind_mineInfoWindow;		// 自身账户信息窗口
	
	private HashMap<String, ChatWindow> 		hashMap_wind_friendChat;	// 聊天窗口组
	
	private String lnotePath = "resource/lnote.data";						// 登陆信息文件
	private boolean flag_timer1 = false;									// 标记定时任务1 是否被启动过
	private static String infoWindowMark;									// 信息窗口标记位
	
//	private GroupChatWindow						wind_groupList;				// 群组列表
	private HashMap<String, GroupChatWindow> 	hashMap_window_groupChat;	// 聊天组窗口组
	private GroupEnvelopeRepertory 				group_repertory_envelope;	// 聊天窗口组仓库

	public static void main(String []args){
		setPlugin(true);
		ChatClient cc = new ChatClient();
		cc.start();
	}

	/**
	 * 使能 BeautyEye-Swing 皮肤包
	 * @param flag_plugin 皮肤包状态
	 */
	private static void setPlugin(boolean flag_plugin) {
		try
	    {
			if(flag_plugin){
				org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
				UIManager.put("RootPane.setupButtonVisible", false);
				UIManager.put("TabbedPane.tabAreaInsets", new javax.swing.plaf.InsetsUIResource(3,5,2,20));
			}
	    }catch(Exception e){
	    	System.out.println("PluginError: could not load plugin.");
	    	e.printStackTrace();
	    }
	}
	
	/**
	 * 聊天工具客户端构造函数
	 */
	public ChatClient() {
		this.net_controller = new NetworkForClient(host_name,contact_port);
		this.rs_controller = new RecvSendController(this.net_controller);
		this.wind_controller = new WindowProducer();
		this.hashMap_wind_friendChat = new HashMap<String, ChatWindow>();
		this.repertory_envelope = new EnvelopeRepertory();
		
		this.hashMap_window_groupChat = new HashMap<String , GroupChatWindow>();
		this.group_repertory_envelope = new GroupEnvelopeRepertory();
	};
	
	/**
	 * 客户端运行入口
	 */
	private void start() {
		/************************************** 从文件读取登陆信息 **************************************/
		analyzeLoginFile(getContentFromLoginFile(lnotePath));
		
		/************************************** 窗口创建监听线程 **************************************/
		Runnable rnb_wind = ()->{
			while(true) {
				int type_window = WindowProducer.getWindowRequest();
				switch(type_window) {
				
				case WindowProducer.LOGIN_WIND:				// 创建登陆窗口
					wind_login = new LoginWindow(LoginWindow.getLoginInfoResource());
					break;
					
				case WindowProducer.FRIEND_LIST_WIND:		// 创建好友列表
					wind_friendsList = new FriendsListWindow();		
					isFriendsListWindCreated = true;
					break;
					
				case WindowProducer.CHAT_WIND:				// 创建好友聊天窗口
					createChatWindow();
					break;
					
				case WindowProducer.ADD_FRIEND_WIND:		// 创建添加好友窗口
					createAddFriendWindow();
					break;
					
				case WindowProducer.INFO_MINE_WIND:			// 创建个人信息窗口
					createMineInfoWindow();
					break;
					
				case WindowProducer.INFO_FRIEND_WIND:		// 创建好友信息窗口
					createFriendInfoWindow();
					break;
					
				case WindowProducer.INFO_MODIFY_WIND:		// 创建修改信息窗口
					createInfoModifyWindow();
					break;
					
				case WindowProducer.GROUP_CHAT_WIND:		//创建群组聊天窗口
					createGroupChatWindow();
					break;
					
				default:
					break;
				}
			}			
		};
		Thread thd_wind = new Thread(rnb_wind);
		thd_wind.start();
		
		/************************************** 收发消息监听线程 **************************************/
		Runnable rnb_message = ()->{
			while(true) {
				Message str_newMessage = RecvSendController.getFromRecvQueue();		// 从接收队列取出一条新消息
				int type_newMessage = MessageOperate.getMsgType(str_newMessage);	// 解析消息头
				
				switch(type_newMessage) {
				case MessageOperate.LOGIN:			// 处理服务器反馈的登陆验证
					gainLoginResult(str_newMessage);
					break;

				case MessageOperate.MYSELF:			// 处理服务器反馈的个人信息
					gainMineAccInfo(str_newMessage);
					break;
					
				case MessageOperate.GET_OTHER_USER_DETAIL:	// 处理服务器反馈的好友账户信息
					gainFriendAccInfo(str_newMessage);
					break;
					
				case MessageOperate.FRIENDLIST:		// 处理服务器反馈的好友列表
					gainFriendslistInfo(str_newMessage);
					break;

				case MessageOperate.CHAT:			// 处理服务器转发的聊天内容
					gainChatEnvelope(str_newMessage);
					break;
				
				case MessageOperate.SEARCH:			// 处理服务器反馈的搜索好友结果
					gainSearchFriendInfo(str_newMessage);
					break;
					
				case MessageOperate.ADDFRIEND:		// 处理好友的添加请求
					gainAddFriendRequest(str_newMessage);
					break;
				
				case MessageOperate.BACKADD:		// 处理添加好友结果
					gainAddFriendInfo(str_newMessage);
					break;
					
				case MessageOperate.GET_GROUP_LIST:		// 处理服务器反馈的群组列表
					gainGroupFriendslistInfo(str_newMessage);
					break;
					
				case MessageOperate.UPDATE_GROUP:
					gainGroupDetailedInfo(str_newMessage);
					
//				case MessageOperate.GROUPCHAT:		// 处理服务器转发的群聊内容
//					//gainGroupChatEnvelope(str_newMessage);
//					break;
				default: 
					break;
						
				}
			}
		};
		Thread thd_message = new Thread(rnb_message);
		thd_message.start();
	};
	
	/**
	 * 创建聊天窗口 并将窗口加入聊天窗口的 hashMap 中 若有离线消息则打到窗口中
	 */
	private void createGroupChatWindow( ) {	
		
		Group group_createInfo = new Group();
		group_createInfo = wind_friendsList.getNewChatWindowResource();
		
		// 查看该窗口是否被创建过 若已存在则将该窗口显示 若不存在则新建并加入聊天窗口hashMap中
		
		if(!hashMap_window_groupChat.containsKey(group_createInfo.getId())){
			
			GroupChatWindow groupChatWindowchatWind = new GroupChatWindow(wind_friendsList.getMineAccount() , 
																		  group_createInfo , 
																		  wind_friendsList);
			hashMap_window_groupChat.put(group_createInfo.getId(), groupChatWindowchatWind);
			// 检查是否有未读消息
			if(GroupEnvelopeRepertory.isContainsKey(group_createInfo.getId())) {
				ArrayList<Envelope> arrlist_envelope = new ArrayList<Envelope>();
				arrlist_envelope = GroupEnvelopeRepertory.getFromBox(group_createInfo.getId());
				for(int i=0;i<arrlist_envelope.size();i++) 
					groupChatWindowchatWind.sendMessageToGroupShowTextField(arrlist_envelope.get(i).getText(), 
																			arrlist_envelope.get(i).getSourceAccountId());
			}
		}
		else{
			// 显示窗口 并提升到页面顶部
			hashMap_window_groupChat.get(group_createInfo.getId()).setVisible(true);
			hashMap_window_groupChat.get(group_createInfo.getId()).setAlwaysOnTop(true);
			hashMap_window_groupChat.get(group_createInfo.getId()).setAlwaysOnTop(false);
		}		
	}
	

	
	/**
	 *  创建添加好友面板，并设置删除好友面板内的好友列表
	 */
	private void createAddFriendWindow() {
		wind_addfriend = new AddFriendWindow((LoginWindow.getLoginInfoResource()).getLoginYhm());
		
		//将好友列表添加到管理好友-删除好友面板中，为删除好友面板提供好友列表
		wind_addfriend.setDeleteFriendPanelList(wind_friendsList.getFriendList());					
	}
	
	/**
	 * 创建聊天窗口 并将窗口加入聊天窗口的 hashMap 中 若有离线消息则打到窗口中
	 */
	private void createChatWindow() {	
		// 获取需要创建的好友账户信息
		Account account_chatFriend = new Account();
		account_chatFriend = wind_friendsList.getNewWindowResource();
		String friend_ID = account_chatFriend.getId();
		
		// 查看该窗口是否被创建过 若已存在则将该窗口显示 若不存在则新建并加入聊天窗口hashMap中
		if(!hashMap_wind_friendChat.containsKey(friend_ID)){
			ChatWindow chatWind = new ChatWindow(account_chatFriend,wind_friendsList.getMineAccount());
			hashMap_wind_friendChat.put(friend_ID,chatWind);
			System.out.println("ChatInfo: open the chating window with " + 
					account_chatFriend.getNikeName());

			RecvSendController.addToSendQueue(MessageOperate.packageAskOtherUserDetail
					(friend_ID,wind_friendsList.getMineAccount().getId()));	// 请求好友详细信息
			
			// 检查是否有未读消息
			if(EnvelopeRepertory.isContainsKey(friend_ID)) {
				ArrayList<Envelope> arrlist_envelope = new ArrayList<Envelope>();
				arrlist_envelope = EnvelopeRepertory.getFromBox(friend_ID);
				for(int i=0;i<arrlist_envelope.size();i++) 
					chatWind.sendMessageToShowtextfield(arrlist_envelope.get(i).getText());
			}
		}else{
			// 显示窗口 并提升到页面顶部
			hashMap_wind_friendChat.get(friend_ID).setVisible(true);
			hashMap_wind_friendChat.get(friend_ID).setAlwaysOnTop(true);
			hashMap_wind_friendChat.get(friend_ID).setAlwaysOnTop(false);
		}		
	}
	
	/**
	 * 创建个人信息窗口
	 */
	private void createMineInfoWindow() {
		boolean isModification = true;
		wind_mineInfoWindow = new AccountInfoShowWindow(this.wind_friendsList.getMineAccount(),isModification);
	}
	
	/**
	 * 创建好友信息窗口
	 */
	private void createFriendInfoWindow() {
		boolean isModification = false;
		// 根据事先做好的标记 找到需要显示的好友的信息窗口
		new AccountInfoShowWindow(hashMap_wind_friendChat.get(this.getFriendInfoMark()).
				getFriendAccountInfo(),isModification);
	}
	
	/**
	 * 创建信息修改窗口
	 */
	private void createInfoModifyWindow() {
		new InfoModificationWindow(wind_mineInfoWindow,this.wind_friendsList.getMineAccount());
	}
	
	/**
	 * 根据服务器反馈的消息 判断【登陆结果】
	 * @param message
	 */
	private void gainLoginResult(Message message) {
		wind_login.setWaitingStatus(false);
		if(MessageOperate.unpackIsFinish(message)) {
			System.out.println("Login: successful.");
			wind_login.dispose();
			WindowProducer.addWindowRequest(WindowProducer.FRIEND_LIST_WIND);	
			/* 根据"记住密码"与"自动登陆"按钮的情况 重写登陆文件的信息  */
			rewriteLoginFile();
		}else {
			// 由于登陆失败会被关闭套接字 故需要关闭与服务器交流的收发线程 
			RecvSendController.closeSocket();			
			System.out.println("LoginError: Verification does not pass.");
			JOptionPane.showMessageDialog(null, "信息验证失败！请检查输入的账号与密码！",
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * 根据服务器反馈的消息 获取【个人账户信息】并更新到好友列表窗口中
	 * @param message
	 */
	private void gainMineAccInfo(Message message) {
		Account account_mine = new Account();
		account_mine = MessageOperate.unpackageUserDetail(message);
		
//		printMineAccountInfo(account_mine);
		wind_friendsList.updateMineInfo(account_mine);
		System.out.println("Info: obtaining the personal account from server... - OK");
	}
	
	/**
	 * 根据服务器反馈的消息 获取【好友账户信息】并更新到好友账户信息窗口中
	 */
	private void gainFriendAccInfo(Message message) {
		Account account_friend = new Account();
		account_friend = MessageOperate.unpackageUserDetail(message);
		
		hashMap_wind_friendChat.get(account_friend.getId()).setFriendAccountInfo(account_friend);	// 设置好友的详细账号信息
		System.out.println("Info: obtaining the friend account from server... - OK");
	}
	
	/**
	 * 根据服务器反馈的消息 获取【好友列表】并更新到好友列表窗口中
	 * @param message
	 */
	private void gainFriendslistInfo(Message message) {
		ArrayList<Account> arrayList_account_friendsInfo = new ArrayList<Account>();
		arrayList_account_friendsInfo = MessageOperate.unpackFriendListMsg(message);
		
		wind_friendsList.updateFriendsList(arrayList_account_friendsInfo);

		
		if(!flag_timer1) {
			flag_timer1 = true;
			timerFriendsList();			// 启动定时任务 - 定时拉取好友列表
		}
	}
	
	/**
	 * 根据服务器反馈的消息 获取【群组列表】并更新到群组列表窗口中
	 * @param message
	 */
	private void gainGroupFriendslistInfo(Message message) {
		
		ArrayList<Group> groups_Info =  MessageOperate.unpackageGroupList(message);    //待修改  解包群组列表	
		wind_friendsList.updateGroupsList(groups_Info);	
//		System.out.println("LoginInfo: obtaining the friendList from server... - OK");
		
		if(!flag_timer1) {
			flag_timer1 = true;
			timerFriendsList();			// 启动定时任务 - 定时拉取群组列表
		}
	}
	
	private void gainGroupDetailedInfo(Message message) {
		
		Group groups_Info =  MessageOperate.unpackageUpdateGroup(message);    //待修改  解包群组列表	
//		System.out.println("group detailed info:" + groups_Info.size());
		ArrayList<Account> accounts = groups_Info.getMember();
		for(int i =0;i<accounts.size();i++) {
			System.out.println(accounts.get(i).getNikeName());
		}
		System.out.println("hash group size:" + hashMap_window_groupChat.size());
		
		while(!hashMap_window_groupChat.containsKey(groups_Info.getId()));						//阻塞等待群窗口创建出来，才设置群窗口的成员
		hashMap_window_groupChat.get(groups_Info.getId()).setGroupFriendName(groups_Info.getMember());
//		wind_friendsList.asdfasdfupdateGroupsList(groups_Info);	 // here we need to debug
	}
	/**
	 * 根据服务器反馈的消息 解析【信封】的收件人 找到对应的窗口并显示
	 * @param str
	 */
	public void gainChatEnvelope(Message str) {
		Envelope evp = new Envelope();
		evp = MessageOperate.unpackEnvelope(str);
		String friendID = evp.getSourceAccountId();	// 信封源地址即为好友地址/群地址
		String destID = evp.getTargetAccountId();
		String message = evp.getText();
		
		System.out.println("chat friend_ID:" + friendID);
		System.out.println("chat dest_ID:" + destID);
		System.out.println("chat message:" + message);
		
		//看ID前有没有G标识群消息
		if(destID.contains("g")) {
			destID = destID.substring(1);
			System.out.println("Gourp ID:" + destID);
			if(!hashMap_window_groupChat.containsKey(destID))
				GroupEnvelopeRepertory.addToBox(destID, evp);
			else {
				hashMap_window_groupChat.get(destID).sendMessageToGroupShowTextField(message, friendID);
				System.out.println("chatInfoRecv: " + friendID + " send “" + message + "” to " + destID);	//群发送消息给了个人
			}
		}
		else {
			if(!hashMap_wind_friendChat.containsKey(friendID))	// 若窗口还没有创建 则存入信封仓库中
				EnvelopeRepertory.addToBox(friendID, evp);
			else {		// 若已创建则直接打到对应窗口上
				hashMap_wind_friendChat.get(friendID).sendMessageToShowtextfield(message);	// 根据发送方的ID定位到好友窗口并显示
				System.out.println("chatInfoRecv: " + friendID + " send “" + message + "” to " + destID);
			}
		}
	}
	
	/**
	 * 处理来自群聊的消息
	 */
	private void gainGroupChatEnvelope(Message str)			//GroupChatEnvelope!!!
	{
		Envelope evp = new Envelope();
		evp = MessageOperate.unpackEnvelope(str);
		String friendID = evp.getSourceAccountId();			// 信发送消息的ID	，		   待修改！！！
		String groupID = evp.getTargetAccountId();			//	封源地址即为群号ID ， 待修改！！！
		String message = evp.getText();
		
		if(!hashMap_window_groupChat.containsKey(groupID))	// 若groupID窗口还没有创建 则存入信封仓库中
			GroupEnvelopeRepertory.addToBox(groupID, evp);
		else {		// 若已创建则直接打到对应窗口上
			hashMap_window_groupChat.get(groupID).sendMessageToGroupShowTextField(message , friendID);	// 根据发送方的ID定位到好友窗口并显示
			System.out.println("chatInfoRecv: " + groupID + " send “" + message + "” to " + friendID);	//群发送消息给了个人
		}

	}
	/**
	 * 获取好友请求
	 * @param str
	 */
	private void gainAddFriendRequest(Message str){
		while(!isFriendsListWindCreated) ;
		String friend_id = MessageOperate.unpackAddFriendMsg(str);
		wind_friendsList.setNewFriendID(friend_id);
		wind_friendsList.setNewFriendRequesttBottonVisible(true);
	}
	
	/**
	 * 处理服务器反馈的搜索好友结果
	 * @param str
	 */
	private void gainSearchFriendInfo(Message str) {
		wind_addfriend.showFriendInfotInSearchFriendPanel(MessageOperate.unpackSearchResultMsg(str));
	}
	
	/**
	 * 处理服务器反馈的添加好友结果
	 * @param str
	 */
	private void gainAddFriendInfo(Message str) {
		System.out.println("【 Add Result】"+MessageOperate.unpackAddFriendResultMsg(str));
		if(MessageOperate.unpackAddFriendResultMsg(str)) {
			System.out.println("AddFriendInfo: add the friend success... - OK");
			RecvSendController.addToSendQueue(MessageOperate.packageAskFriendListMsg());
			wind_friendsList.addFriendSuccessHint();
		}
		else { 
			System.out.println("AddFriendInfo: add the friend faliure... - OK");
			wind_friendsList.addFriendFailureHing();
		}
	}

	/**
	 * 定时任务1：定时拉取好友列表
	 * 前提：好友列表窗口已被创建
	 */
	private void timerFriendsList() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				RecvSendController.addToSendQueue(MessageOperate.packageAskFriendListMsg());
				RecvSendController.addToSendQueue(MessageOperate.packageAskGetGroupList());
			}
		}, 0 ,5000);
	}
	
	/**
	 * 从登陆文件中获取内容存到字符串中返回
	 * @return 登陆文件内容
	 */
	private String getContentFromLoginFile(String lnoePath) {
		String str_fileContent = "";
		
		/* 从登陆文件读取内容 */
		try {
			FileReader fReader = new FileReader(lnoePath);
			int c = fReader.read();
			while(c != -1){
				str_fileContent += (char)c;
				c = fReader.read();
			}
			fReader.close();
		} catch (FileNotFoundException e1) {
			System.out.println("LoginError: loginInfo file of yhm/psw could not find.");
			e1.printStackTrace();
		} catch (IOException e) {
			System.out.println("LoginError: loginInfo file of yhm/psw could not read.");
			e.printStackTrace();
		}
		return str_fileContent;
	}
	
	/**
	 * 根据文件内容 处理登陆窗口 实现记住密码/自动登陆功能
	 * @param str_fileContent 文件内容
	 */
	private void analyzeLoginFile(String str_fileContent) {
		if(str_fileContent.isEmpty()){
			System.out.println("LoginInfo: Create a empty login Window.");
		}else{
			String yhm = null,psw = null;
			boolean rmSelected = false,autoSelected = false;
			String []ss = str_fileContent.split(" ");
			
			if(ss.length == 4){
				yhm = ss[0]; 
				psw = ss[1];
				if(ss[2].equals("true")) rmSelected = true;
				else rmSelected = false;
				if(ss[3].equals("true")) autoSelected = true;
				else autoSelected = false;
				
				LoginWindow.setLoginInfoResource(yhm, psw, rmSelected, autoSelected);
				
				if(autoSelected){	// 自动登陆
//					wind_login.verifyInfoWithServer();
				}												
			}else if(ss.length == 1){
				yhm = ss[0]; 
				psw = "";
				LoginWindow.setLoginInfoResource(yhm, psw, rmSelected, autoSelected);
			}			
		}
		WindowProducer.addWindowRequest(WindowProducer.LOGIN_WIND);
	}
	
	/**
	 * 登陆成功后重写登陆信息文件
	 */
	private void rewriteLoginFile() {
		if(wind_login.getRememberBtnFlag()){	// 记住密码复选框被选中					
			try {	// 将账户密码存入文件中
				FileWriter fWriter = new FileWriter(lnotePath);
				fWriter.write(wind_login.getYhm() + " ");
				fWriter.write(wind_login.getPsw() + " ");
				if(wind_login.getRememberBtnFlag()) fWriter.write("true ");
				else fWriter.write("false ");
				if(wind_login.getAutoBtnFlag()) fWriter.write("true");
				else fWriter.write("false");
				fWriter.close();
			} catch (IOException e) {
				System.out.println("LoginFileError: could not write to login file.");
				e.printStackTrace();
			}
		}else{		// 记住密码复选框没有被选中
			try {	// 只记录登陆账号
				FileWriter fWriter = new FileWriter(lnotePath);
				fWriter.write(wind_login.getYhm());
				fWriter.close();
			} catch (IOException e) {
				System.out.println("LoginFileError: could not write to login file.");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 用于标识需要创建的好友信息窗口
	 * @param id 好友的ID值
	 */
	public static void setFriendInfoMark(String id) {
		infoWindowMark = id;
	}
	public String getFriendInfoMark() {
		return infoWindowMark;
	}
	
	/**
	 * 打印好友账户信息列表
	 * @param arrList
	 */
	@SuppressWarnings("unused")
	private void printFriendAccountsList(ArrayList<Account> arrList){
		for(int i=0;i<arrList.size();i++){
			System.out.println("ListInfo: Get the friend "+(i+1)+" 's nickname - "+arrList.get(i).getNikeName());
			System.out.println("ListInfo: Get the friend "+(i+1)+" 's signature - "+arrList.get(i).getSignature());
			System.out.println("ListInfo: Get the friend "+(i+1)+" 's ID - "+arrList.get(i).getId());
			System.out.println("ListInfo: Get the friend "+(i+1)+" 's online - "+arrList.get(i).getOnline());
		}
	}
	
	/**
	 * 打印自身账户信息
	 * @param account
	 */
	private void printMineAccountInfo(Account account) {
		System.out.println("Account Info - ID: " + account.getId());
		System.out.println("Account Info - nickName: " + account.getNikeName());
		System.out.println("Account Info - online: " + account.getOnline());
		System.out.println("Account Info - signature: " + account.getSignature());
	}
}
