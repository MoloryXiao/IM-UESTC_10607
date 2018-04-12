package Core;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import network.NetworkForClient.NetworkForClient;
import network.commonClass.Account;
import network.messageOperate.MessageOperate;
import java.util.Vector;
import javax.swing.UIManager;
import Core.Controll.*;

public class ChatTool {
	private static boolean plugin_Flag;
	private static LoginWindow login_wind;
	private static FriendsListWindow fd_wind;
	private ArrayList<Account> friend_info_arraylist;
	private boolean login_success_flag = false;
	private NetworkController nkc;
	private Vector vec_friend_orderNum;
	private ArrayList<ChatWindow> arrList_friends_chatWind;
	
	public static void main(String []args){
		@SuppressWarnings("unused")
		ChatTool ct = new ChatTool();
	}
	public ChatTool(){
		nkc = new NetworkController();
		
		plugin_Flag = true;
		try
	    {
			if(plugin_Flag){
				org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
				UIManager.put("RootPane.setupButtonVisible", false);
				UIManager.put("TabbedPane.tabAreaInsets", new javax.swing.plaf.InsetsUIResource(3,5,2,20));
			}
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
		
		String lnotePath = "resource/lnote.data";
		String file_context = "";
		/* 文件读取器 */
		try {
			FileReader fReader = new FileReader(lnotePath);
			int c = fReader.read();
			while(c != -1){
				file_context += (char)c;
				c = fReader.read();
			}
			fReader.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(file_context.isEmpty()){
			System.out.println("LoginInfo: Create a empty login Window");
			login_wind = new LoginWindow(null,null,false,false);
		}
		else{
			String yhm = null,psw = null;
			boolean rmSelected = false,autoSelected = false;
			String []ss = file_context.split("\n");
			if(ss.length == 4){
				yhm = ss[0]; 
				psw = ss[1];
				if(ss[2].equals("true")) rmSelected = true;
				else rmSelected = false;
				if(ss[3].equals("true")) autoSelected = true;
				else autoSelected = false;
				
				login_wind = new LoginWindow(yhm,psw,rmSelected,autoSelected);
				
				if(autoSelected && nkc.verifyInfoWithServer(yhm,psw)){	// 自动登陆
					login_success_flag = true;		
					login_wind.dispose();
				}												
			}else if(ss.length == 1){
				yhm = ss[0]; 
				psw = "";
				login_wind = new LoginWindow(yhm,psw,rmSelected,autoSelected);
			}			
		}
		
		while(!login_success_flag){
			while(!login_wind.isConnectServer()){}		// 检查输入合法后再进行连接服务器
			String login_yhm = login_wind.getYhm();
			String login_psw = login_wind.getPsw();
			boolean login_remember = login_wind.getRememberBtnFlag();
			boolean login_auto = login_wind.getAutoBtnFlag();
			
			boolean verifyRes = false;
			verifyRes = nkc.verifyInfoWithServer(login_yhm,login_psw);
			if(verifyRes){
				login_wind.dispose();
				if(login_wind.getRememberBtnFlag()){	// 记住密码复选框被选中					
					try {	// 将账户密码存入文件中
						FileWriter fWriter = new FileWriter(lnotePath);
						fWriter.write(login_yhm+"\n");
						fWriter.write(login_psw+"\n");
						if(login_remember) fWriter.write("true\n");
						else fWriter.write("false\n");
						if(login_auto) fWriter.write("true");
						else fWriter.write("false");
						fWriter.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else{		// 记住密码复选框没有被选中
					try {	// 只记录登陆账号
						FileWriter fWriter = new FileWriter(lnotePath);
						fWriter.write(login_yhm);
						fWriter.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				login_success_flag = true;
			}else{
				login_wind.setLoginButtonStatus(true); 	// 重新开启按钮
				login_wind.setConnectFlag(false);
			}			
		}
		System.out.println("LoginInfo: obtaining the friendList from server.");
		
		fd_wind = new FriendsListWindow();

		friend_info_arraylist = nkc.askFriendListFromServer();
		printAccountList(friend_info_arraylist);
		
		fd_wind.updateFriendsList(friend_info_arraylist);
		vec_friend_orderNum = new Vector();
		arrList_friends_chatWind = new ArrayList<ChatWindow>();
		
		while(true){
			if(fd_wind.getCreateChatWindFlag()){				
				String str_chatName = (String)fd_wind.getNewWindowValue();
				int i_orderNum = fd_wind.getNewWindowOrderNum();
				if(vec_friend_orderNum.indexOf(i_orderNum) == -1){
					vec_friend_orderNum.add(i_orderNum);
					arrList_friends_chatWind.add(new ChatWindow(str_chatName));
					System.out.println("ChatInfo: open the chating window with "+ str_chatName);
				}else{
					arrList_friends_chatWind.get(i_orderNum).setVisible(true);
					arrList_friends_chatWind.get(i_orderNum).setAlwaysOnTop(true);
					arrList_friends_chatWind.get(i_orderNum).setAlwaysOnTop(false);
				}
				fd_wind.setCreateChatWindFlag(false);
			}
		}		
	}
	public void printAccountList(ArrayList<Account> arrList){
		for(int i=0;i<arrList.size();i++){
			System.out.println("ListInfo: Get the friend "+(i+1)+" - "+arrList.get(i).getNikeName());
			System.out.println("ListInfo: Get the friend "+(i+1)+" - "+arrList.get(i).getSignature());
			System.out.println("ListInfo: Get the friend "+(i+1)+" - "+arrList.get(i).getID());
			System.out.println("ListInfo: Get the friend "+(i+1)+" - "+arrList.get(i).getOnLine());
		}
	}
	
}
