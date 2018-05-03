package Core;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.xml.internal.ws.api.pipe.Tube;

import network.NetworkForClient.NetworkForClient;
import network.commonClass.Account;
import network.commonClass.Envelope;
import network.commonClass.Login;
import network.messageOperate.MessageOperate;
/**
 * 程序入口 聊天软件客户端
 * @author Murrey
 * @version 1.0
 * Inital.
 */
public class ChatClient{
	private final String host_name = "39.108.95.130";	// server location
//	private final String host_name = "192.168.1.103";	// local area for test
//	private final String host_name = "127.0.0.1";
	private final int contact_port = 9090;
	
	private RecvSendController rs_controller;	// 收发线程控制器
	private NetworkForClient net_controller;	// 通信接口控制器
	private WindowProducer wind_controller;		// 窗口创建控制器
	
	private LoginWindow wind_login;				// 登陆窗口
	private FriendsListWindow wind_friendsList;	// 好友列表
	private AddFriendWindow wind_addfriend;		// 添加好友窗口
	private HashMap<String, ChatWindow> hashMap_wind_friendChat;	// 聊天窗口组
	
	
	private String lnotePath = "resource/lnote.data";	// 登陆信息文件
	private boolean flag_timer1 = false;				// 定时任务1 是否被启动过
	
	public static void main(String []args){
		setPlugin(true);
		@SuppressWarnings("unused")
		ChatClient cc = new ChatClient();
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
					break;
					
				case WindowProducer.CHAT_WIND:				// 创建好友聊天窗口
					createChatWindow();
					break;
					
				case WindowProducer.ADD_FRIEND_WIND:
					wind_addfriend = new AddFriendWindow(LoginWindow.getLoginInfoResource());
					createAddFriendWindow();
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
				String str_newMessage = RecvSendController.getFromRecvQueue();		// 从接收队列取出一条新消息
				int type_newMessage = MessageOperate.getMsgType(str_newMessage);	// 解析消息头
				
				switch(type_newMessage) {
				case MessageOperate.LOGIN:			// 处理服务器反馈的登陆验证
					gainLoginResult(str_newMessage);
					break;
					
				case MessageOperate.MYSELF:			// 处理服务器反馈的个人信息
					gainMineAccInfo(str_newMessage);
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
				case MessageOperate.ADDFRIEND:
					gainAddFriendRequest(str_newMessage);
					break;
				case MessageOperate.BACKADD:		//处理服务器反馈的添加好友信息
					gainAddFriendInfo(str_newMessage);
				
				default: 
					break;
						
				}
			}
		};
		Thread thd_message = new Thread(rnb_message);
		thd_message.start();		
	};
	private void createAddFriendWindow() {
		wind_addfriend.setDeleteFriendPanel(wind_friendsList.getFriendList());
	}
	
	private void gainAddFriendRequest(String str){
		String friend_id = MessageOperate.unpackAddFriendMsg(str);
		wind_friendsList.setNewFriendID(friend_id);
		wind_friendsList.setNewFriendRequesttBottonVisible(true);
	}
	private void gainSearchFriendInfo(String str) {
		wind_addfriend.setFriendAccount(MessageOperate.unpackSearchResultMsg(str));
		
	}
	
	/**
	 * 处理服务器反馈的添加好友结果
	 */
	private void gainAddFriendInfo(String str) {
		System.out.println("【 Add Result】"+MessageOperate.unpackAddFriendResultMsg(str));
		if(MessageOperate.unpackAddFriendResultMsg(str)) {
			System.out.println("AddFriendInfo: add the friend success... - OK");
			wind_friendsList.addFriendSuccessHint();
		}
		else { 
			System.out.println("AddFriendInfo: add the friend faliure... - OK");
			wind_friendsList.addFriendFailureHing();
		}
	}
	
	/**
	 * 创建聊天窗口 并加入聊天窗口的 hashMap 中
	 */
	private void createChatWindow() {		
		Account account_chatFriend = new Account();
		account_chatFriend = wind_friendsList.getNewWindowResource();
		String friend_ID = account_chatFriend.getId();
		
		if(!hashMap_wind_friendChat.containsKey(friend_ID)){
			hashMap_wind_friendChat.put(friend_ID,
					new ChatWindow(account_chatFriend,wind_friendsList.getMineAccount()));
			System.out.println("ChatInfo: open the chating window with " + 
					account_chatFriend.getNikeName());
		}else{
			// 此处有bug friend_id值不是索引号 考虑键值模板类
			hashMap_wind_friendChat.get(friend_ID).setVisible(true);
			hashMap_wind_friendChat.get(friend_ID).setAlwaysOnTop(true);
			hashMap_wind_friendChat.get(friend_ID).setAlwaysOnTop(false);
		}
	}
	
	/**
	 * 根据服务器反馈的消息 判断【登陆结果】
	 * @param message
	 */
	private void gainLoginResult(String message) {
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
	private void gainMineAccInfo(String message) {
		Account account_mine = new Account();
		account_mine = MessageOperate.unpackMyselfInfoMsg(message);
		
		printMineAccountInfo(account_mine);
		wind_friendsList.updateMineInfo(account_mine);
		System.out.println("LoginInfo: obtaining the personal account from server... - OK");
	}
	
	/**
	 * 根据服务器反馈的消息 获取【好友列表】并更新到好友列表窗口中
	 * @param message
	 */
	private void gainFriendslistInfo(String message) {
		ArrayList<Account> arrayList_account_friendsInfo = new ArrayList<Account>();
		arrayList_account_friendsInfo = MessageOperate.unpackFriendListMsg(message);
		
//		printFriendAccountsList(arrayList_account_friendsInfo);
		wind_friendsList.updateFriendsList(arrayList_account_friendsInfo);
//		System.out.println("LoginInfo: obtaining the friendList from server... - OK");
		
		if(!flag_timer1) {
			flag_timer1 = true;
			timerFriendsList();			// 启动定时任务 - 定时拉取好友列表
		}
	}
	
	/**
	 * 根据服务器反馈的消息 解析【信封】的收件人 找到对应的窗口并显示
	 * @param str
	 */
	private void gainChatEnvelope(String str) {
		Envelope evp = MessageOperate.unpackEnvelope(str);
		String sourceID = evp.getSourceAccountId();
		String sendID = evp.getTargetAccountId();
		String message = evp.getText();
		hashMap_wind_friendChat.get(sourceID).
			sendMessageToShowtextfield(message);	// 根据发送方的ID定位到好友窗口并显示
		System.out.println("chatInfoRecv: " + sourceID + " send “" + message + "” to " + sendID);
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
					wind_login.verifyInfoWithServer();
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
