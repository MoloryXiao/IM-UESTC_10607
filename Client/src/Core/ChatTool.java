package Core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.UIManager;

import network.commonClass.Account;
import network.commonClass.Envelope;
import Core.Controll.*;

/** 
 * 聊天工具模型 程序开始处
 * @author Murrey
 * @version 2.0
 *
 */
public class ChatTool {
	private NetworkController 				netController;
	
	private boolean 						flag_login_success = false;	

	private String 							str_lnotePath = "resource/lnote.data";
	
	private LoginWindow 					wind_login;
	private FriendsListWindow 				wind_friendsList;

	private ArrayList<Account> 				arrayList_account_friendsInfo;
	private HashMap<Integer, ChatWindow> 	hashMap_wind_friendChat;	
	
	public static void main(String []args){
		@SuppressWarnings("unused")
		ChatTool ct = new ChatTool();
	}

	/**
	 * 构造函数
	 */
	public ChatTool(){
		netController = new NetworkController();							// 初始化网络接口控制器
		hashMap_wind_friendChat = new HashMap<Integer, ChatWindow>();		// 初始化好友列表存储器
		
		setPlugin(true);			// 使能皮肤包
		
		String str_fileContent = getContentFromLoginFile();		// 从登陆文件中读取内容
		
		analyzeLoginFile(str_fileContent);						// 根据文件内容 处理记住密码/自动登陆功能
		
		accountLogin();				// 常规登陆操作
		
		createFriendsWindow();		// 创建好友列表窗口
		
		recvMessageThreadStart();
		
		createFriendChatWindow();	// 轮训创建好友聊天窗口
	}
	
	/**
	 * 使能 Swing 皮肤包
	 * @param flag_plugin
	 */
	private void setPlugin(boolean flag_plugin) {
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
	 * 从登陆文件中获取内容存到字符串中返回
	 * @return 登陆文件内容
	 */
	private String getContentFromLoginFile() {
		String str_fileContent = "";
		
		/* 从登陆文件读取内容 */
		try {
			FileReader fReader = new FileReader(str_lnotePath);
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
			wind_login = new LoginWindow(null,null,false,false);
		}else{
			String yhm = null,psw = null;
			boolean rmSelected = false,autoSelected = false;
			String []ss = str_fileContent.split("\n");
			
			if(ss.length == 4){
				yhm = ss[0]; 
				psw = ss[1];
				if(ss[2].equals("true")) rmSelected = true;
				else rmSelected = false;
				if(ss[3].equals("true")) autoSelected = true;
				else autoSelected = false;
				
				wind_login = new LoginWindow(yhm,psw,rmSelected,autoSelected);
				
				if(autoSelected && netController.verifyInfoWithServer(yhm,psw)){	// 自动登陆
					flag_login_success = true;		// 成功登陆标志位置位  跳过常规登陆
					wind_login.dispose();
				}												
			}else if(ss.length == 1){
				yhm = ss[0]; 
				psw = "";
				wind_login = new LoginWindow(yhm,psw,rmSelected,autoSelected);
			}			
		}
	}
	
	/**
	 * 常规登陆操作
	 */
	private void accountLogin() {
		while(!flag_login_success){
			while(!wind_login.isConnectServer()){}		// 窗口触发登陆事件
			wind_login.setLoginButtonStatus(false); 	// 禁用登陆按钮
			
			String login_yhm = wind_login.getYhm();
			String login_psw = wind_login.getPsw();
			boolean login_auto = wind_login.getAutoBtnFlag();
			boolean login_remember = wind_login.getRememberBtnFlag();
						
			boolean verifyRes = false;
			verifyRes = netController.verifyInfoWithServer(login_yhm,login_psw);	// 与服务器进行交互验证 返回登陆结果
			
			if(verifyRes){	// 服务器验证成功
				wind_login.dispose();
				if(wind_login.getRememberBtnFlag()){	// 记住密码复选框被选中					
					try {	// 将账户密码存入文件中
						FileWriter fWriter = new FileWriter(str_lnotePath);
						fWriter.write(login_yhm+"\n");
						fWriter.write(login_psw+"\n");
						if(login_remember) fWriter.write("true\n");
						else fWriter.write("false\n");
						if(login_auto) fWriter.write("true");
						else fWriter.write("false");
						fWriter.close();
					} catch (IOException e) {
						System.out.println("LoginFileError: could not write to login file.");
						e.printStackTrace();
					}
				}else{		// 记住密码复选框没有被选中
					try {	// 只记录登陆账号
						FileWriter fWriter = new FileWriter(str_lnotePath);
						fWriter.write(login_yhm);
						fWriter.close();
					} catch (IOException e) {
						System.out.println("LoginFileError: could not write to login file.");
						e.printStackTrace();
					}
				}
				flag_login_success = true;				// 完成登陆操作
			}else{			// 服务器验证失败
				wind_login.setConnectFlag(false);		// 否决连接请求
			}
			wind_login.setLoginButtonStatus(true); 		// 重新开启登陆按钮
		}
	}
	
	/**
	 * 创建好友列表窗口
	 * 包括拉取自身信息、好友列表信息
	 */
	private void createFriendsWindow() {
		/* 拉取自身账号信息 */
		Account myselfAccount = new Account();
		System.out.print("LoginInfo: obtaining the personal account from server...");
		myselfAccount = netController.askMySelfAccFromServer();
		System.out.println(" - OK.");
		
		/* 创建好友列表窗口 */
		wind_friendsList = new FriendsListWindow(myselfAccount);
		
		/* 获取好友列表 */
		System.out.print("LoginInfo: obtaining the friendList from server...");
		arrayList_account_friendsInfo = netController.askFriendListFromServer();
		System.out.println(" - OK.");
		printFriendAccountsList(arrayList_account_friendsInfo);
		
		/* 将好友列表更新到好友列表窗口中 */
		wind_friendsList.updateFriendsList(arrayList_account_friendsInfo);
	}
	
	/**
	 * 轮训好友列表是否触发聊天窗口新建事件 并创建新的聊天窗口
	 */
	private void createFriendChatWindow() {
		while(true){
			if(wind_friendsList.getCreateChatWindFlag()){				
				Account friend_account = new Account();
				friend_account = wind_friendsList.getNewWindowResource();
				String friend_ID = friend_account.getID();
				int friend_id = Integer.parseInt(friend_ID);
				
				if(!hashMap_wind_friendChat.containsKey(friend_id)){
					hashMap_wind_friendChat.put(friend_id,new ChatWindow(friend_account,wind_friendsList.getMine_ID()));
					System.out.println("ChatInfo: open the chating window with " + 
							friend_account.getNikeName());
				}else{
					// 此处有bug friend_id值不是索引号 考虑键值模板类
					hashMap_wind_friendChat.get(friend_id).setVisible(true);
					hashMap_wind_friendChat.get(friend_id).setAlwaysOnTop(true);
					hashMap_wind_friendChat.get(friend_id).setAlwaysOnTop(false);
				}
				wind_friendsList.setCreateChatWindFlag(false);
			}
		}
	}
	
	/**
	 * 开启接收好友信息线程
	 */
	private void recvMessageThreadStart() {
		Runnable rnb = ()->{
			while(true) {
				Envelope evp = new Envelope();
				evp = netController.recvEnvelope();
				String sourceID = evp.getSourceAccountId();
				String sendID = evp.getTargetAccountId();
				String message = evp.getText();
				hashMap_wind_friendChat.get(Integer.parseInt(sourceID)).
					sendMessageToShowtextfield(message);	// 根据发送方的ID定位到好友窗口并显示
				System.out.println("chatInfoRecv: " + sourceID + " send “" + message + "” to " + sendID);
			}
		};
		Thread thd = new Thread(rnb);
		thd.start();
	}
	
	/**
	 * 根据好友信息列表打印相关信息
	 * @param arrList
	 */
	public void printFriendAccountsList(ArrayList<Account> arrList){
		for(int i=0;i<arrList.size();i++){
			System.out.println("ListInfo: Get the friend "+(i+1)+" 's nickname - "+arrList.get(i).getNikeName());
			System.out.println("ListInfo: Get the friend "+(i+1)+" 's signature - "+arrList.get(i).getSignature());
			System.out.println("ListInfo: Get the friend "+(i+1)+" 's ID - "+arrList.get(i).getID());
			System.out.println("ListInfo: Get the friend "+(i+1)+" 's online - "+arrList.get(i).getOnLine());
		}
	}
}
