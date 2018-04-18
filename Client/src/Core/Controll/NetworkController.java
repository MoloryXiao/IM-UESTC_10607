package Core.Controll;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JOptionPane;
import network.NetworkForClient.*;
import network.commonClass.*;
import network.messageOperate.MessageOperate;

public class NetworkController {
	private static NetworkForClient nfc;
//	private final String host_name = "39.108.95.130";	// server location
//	private final String host_name = "192.168.1.103";	// local area for test
	private final String host_name = "127.0.0.1";
	private final int contact_port = 9090;	
	
	/** 
     * 向服务器验证登陆信息
     * @param yhm 用户名信息
     * @param psw 登陆密码
     * @return 服务器连接情况/验证结果
     */
	public boolean verifyInfoWithServer(String yhm,String psw){
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
	public void askFriendList() {
		try {
			String askMessage = MessageOperate.packageAskFriendListMsg();
			nfc.sendToServer(askMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public ArrayList<Account> askFriendListFromServer(){
		ArrayList<Account> friend_info_arraylist;
		try {
			String askMessage = MessageOperate.packageAskFriendListMsg();
			nfc.sendToServer(askMessage);
			friend_info_arraylist = new ArrayList<Account>(MessageOperate.
					unpackFriendListMsg(nfc.recvFromServer()));	// 拿到最新的好友列表
		} catch (IOException e) {
			friend_info_arraylist = null;
			e.printStackTrace();
		}
		return friend_info_arraylist;
	}
	
	public Account askMySelfAccFromServer() {
		Account myself = new Account();
		try {
			nfc.sendToServer(MessageOperate.packageAskMyselfInfoMsg());
			myself = MessageOperate.unpackMyselfInfoMsg(nfc.recvFromServer());
		} catch (IOException e) {
			System.out.println();
			e.printStackTrace();
		}
		return myself;
	}
	
	public Envelope recvEnvelope() {
		Envelope evp = new Envelope();
		try {
			evp = MessageOperate.unpackEnvelope(nfc.recvFromServer());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		
		return evp;
	}
	
	public void sendEnvelope(Envelope evp) {		
		try {
			nfc.sendToServer(MessageOperate.packageEnvelope(evp));
		} catch (IOException e) {
			String message = evp.getText();
			System.out.println("chatError: sendMessageToServer error. Message/"+message);
			e.printStackTrace();
		}
	}
	
	public String recvMessageFromServer() throws IOException {
		return nfc.recvFromServer();
	}
}
