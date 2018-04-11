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

public class ChatTool {
	private static boolean plugin_Flag;
	private static LoginWindow login_wind;
	private static FriendsListWindow fd_wind;
	private ArrayList<Account> friend_info_arraylist;
	private boolean login_success_flag = false;
	private NetworkForClient nfc;
	private final String host_name = "39.108.95.130";	// server location
//	private final String host_name = "192.168.1.106";	// local area for test
	private final int contact_port = 9090;
	
	public static void main(String []args){
		@SuppressWarnings("unused")
		ChatTool ct = new ChatTool();
	}
	public ChatTool(){
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
				
				if(autoSelected && verifyInfoWithServer(yhm,psw)){	// 自动登陆
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
			verifyRes = verifyInfoWithServer(login_yhm,login_psw);
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
		try {
			friend_info_arraylist = new ArrayList<Account>(MessageOperate.
					getFriendList(nfc.recvFromServer()));		// 拉取最新的好友列表
			printAccountList(friend_info_arraylist);
			fd_wind.updateFriendsList(friend_info_arraylist);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("FriendsListError: can not get the friends list.");
		}
		while(!fd_wind.isCreateChatWind()){
			
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
	/** 
     * 向服务器验证登陆信息
     * @param yhm 用户名信息
     * @param psw 登陆密码
     * @return 服务器连接情况/验证结果
     */
	private boolean verifyInfoWithServer(String yhm,String psw){
		nfc = new NetworkForClient(host_name,contact_port);
		if(!nfc.connectToServer()){
			System.out.println("Connection error.");
			return false;
		}else{
			System.out.println("LoginInfo: send to server. yhm: "+yhm);
			System.out.println("LoginInfo: send to server. psw: "+psw);
			if(nfc.login(yhm,psw)){
				System.out.println("Login: successful.");
				return true;
			}else{
				System.out.println("LoginError: Verification does not pass.");
				JOptionPane.showMessageDialog(null, "信息验证失败！请检查输入的账号与密码！",
						"Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
	}
}
