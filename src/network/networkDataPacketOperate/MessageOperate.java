package network.networkDataPacketOperate;

import java.io.IOException;
import java.util.ArrayList;

import jdk.internal.org.objectweb.asm.tree.IincInsnNode;
import network.NetworkForServer.RegistorAccount;
import network.commonClass.*;

import javax.sound.midi.Soundbank;

/**
 * Created by ZiQin on 2018/4/11.
 */
public class MessageOperate {
	
	/**
	 * 符号常量
	 */
	public static final int LOGIN = 1;
	public static final int CHAT = 2;
	public static final int FRIENDLIST = 3;
	public static final int ERROR = 4;
	public static final int MSG = 5;
	public static final int ADDFRIEND = 6;
	public static final int DELETE = 7;
	public static final int SEARCH = 8;
	public static final int BACKADD = 9;
	public static final int REGISTOR = 10;
	public static final int MYSELF = 11;
	public static final String OK = "ok";
	public static final String FALSE = "fasle";
	
	/**
	 * 获取信息的类型
	 *
	 * @param msg 收到的信息
	 * @return 返回判断的结果
	 */
	public static int getMsgType( String msg ) {
		
		switch (msg.charAt(0)) {
			case 'C':
				return CHAT;
			case 'L':
				return LOGIN;
			case 'F':
				return FRIENDLIST;
			case 'M':
				return MSG;
			case 'A':
				return ADDFRIEND;
			case 'S':
				return SEARCH;
			case 'D':
				return DELETE;
			case 'B':
				return BACKADD;
			case 'R':
				return REGISTOR;
			case 'I':
				return MYSELF;
			default:
				return ERROR;
		}
	}
	
	/**
	 * 将结果反馈给客户端
	 * 协议格式：
	 * send1:Mmsg
	 *
	 * @param msg 要发送的反馈内容
	 * @throws IOException
	 */
	public static String sendFeedbackToClient( String msg ) throws IOException {
		
		return new String("M" + msg);
	}
	
	/**
	 * 将成功的结果发送给客户端
	 * 协议格式：
	 * send1:Mmsg = ok
	 *
	 * @throws IOException
	 */
	public static String sendFinishMsg() throws IOException {
		
		return new String("M" + OK);
	}
	
	/**
	 * 将失败的结果发送给客户端
	 * 协议格式：
	 * send1:M
	 * send2:msg = false
	 *
	 * @throws IOException
	 */
	public static String sendNotFinishMsg() throws IOException {
		
		return new String("M" + FALSE);
	}
	
	/**
	 * 获取客户端发送过来的登录请求
	 * 接收处理格式：
	 * recv1: ID password
	 *
	 * @return 返回用户的ID号码和密码
	 * @throws IOException
	 */
	public static Login getLoginAccountInfo( String account ) throws IOException {
		
		String ID = new String();
		String password = new String();
		int k = 1;
		for (int i = 1; i < account.length(); i++) {
			if (account.charAt(i) == ' ' && k != 2) {
				++k;
				continue;
			}
			switch (k) {
				case 1:
					ID += account.charAt(i);
					break;
				case 2:
					password += account.charAt(i);
					break;
				default:
					break;
			}
		}
		Login login = new Login(ID, password);
		return login;
	}
	
	public static String getTargetId( String msg ) {
		
		String targetId = "";
		for (int i = 1; msg.charAt(i) != ' '; i++)
			targetId += msg.charAt(i);
		return targetId;
	}
	
	
	/**
	 * 发送用户个人信息给客户端
	 * 发送格式：IID NickName true signature
	 *
	 * @param ac 个人信息
	 */
	public static String sendUserInfo( Account ac ) throws IOException {
		
		return new String("I" + ac.getID() + " " + ac.getNikeName() + " true " + ac.getSignature());
	}
	
	/**
	 * 发送给好友列表给客户端
	 * 协议格式：
	 * send1: F
	 * send2: number
	 * sendN: ID NickName online signature
	 *
	 * @param list 好友列表
	 * @throws IOException
	 */
	public static String sendFriendList( ArrayList<Account> list ) throws IOException {
		
		String msg = new String();
		msg += "F";
		int number = list.size();
		
		msg += Integer.toString(number);
		for (int i = 0; i < number; i++) {
			Account ac = list.get(i);
			msg += " " + ac.getID() + " " + ac.getNikeName() + " " + ac.getOnLine() + " " + ac.getSignature();
		}
		
		return msg;
	}
	
	/**
	 * 获取客户端发来的信息
	 * 接收格式：
	 * recvN: target source text
	 *
	 * @return 信封
	 * @throws IOException
	 */
	public static Envelope recvFromUserMsg( String temp ) throws IOException {
		
		String targetAccount = new String();
		String sourceAccount = new String();
		String msg = new String();
		int k = 1;
		for (int i = 1; i < temp.length(); i++) {
			if (temp.charAt(i) == ' ' && k != 3) {
				++k;
				continue;
			}
			switch (k) {
				case 1:
					targetAccount += temp.charAt(i);
					break;
				case 2:
					sourceAccount += temp.charAt(i);
					break;
				case 3:
					msg += temp.charAt(i);
					break;
				default:
					break;
			}
		}
		Envelope envelope = new Envelope(targetAccount, sourceAccount, msg);
		return envelope;
	}
	
	/**
	 * 将信封发送给客户端
	 * 协议格式：
	 * send1: Ctarget source text
	 *
	 * @param targetAccount 收件人
	 * @param sourceAccount 发件人
	 * @param msg           信封内容
	 * @throws IOException
	 */
	public static String sendToClient( String targetAccount, String sourceAccount, String msg ) throws IOException {
		
		return new String("C" + targetAccount + " " + sourceAccount + " " + msg);
	}
	
	/**
	 * 将信封发送给客户端
	 * 协议格式：
	 * send2: Ctarget source text
	 *
	 * @param envelope 信封
	 * @throws IOException
	 */
	public static String sendToClient( Envelope envelope ) throws IOException {
		
		return new String("C" + envelope.getTargetAccountId() + " " + envelope.getSourceAccountId() + " " + envelope.getText());
	}
	
	/**
	 * 处理客户端发送来的添加好友请求
	 * 接收格式：
	 * recv1: targetId sourceId
	 *
	 * @return 返回信封
	 * @throws IOException
	 */
	public static Envelope getRequestAddFriend( String temp ) throws IOException {
		
		String target = new String();
		String source = new String();
		int k = 1;
		for (int i = 1; i < temp.length(); i++) {
			if (temp.charAt(i) == ' ') {
				++k;
				continue;
			}
			switch (k) {
				case 1:
					target += temp.charAt(i);
					break;
				case 2:
					source += temp.charAt(i);
					break;
				default:
					break;
			}
		}
		Envelope envelope = new Envelope(target, source, "");
		return envelope;
	}
	
	/**
	 * 转发添加好友请求给目的用户
	 * 协议格式：
	 * send2: AtargetId sourceId
	 *
	 * @param envelope 信封
	 * @throws IOException
	 */
	public static String transforAddFriend( Envelope envelope ) throws IOException {
		
		return new String("A" + envelope.getTargetAccountId() + " " + envelope.getSourceAccountId());
	}
	
	/**
	 * 接收添加好友的请求结果
	 * 接收格式：
	 * recv1: targetId sourceId result
	 *
	 * @return 返回信封（信封内容为添加结果：true或者false）
	 * @throws IOException
	 */
	public static Envelope transforAddFriendResult( String temp ) throws IOException {
		
		String target = new String();
		String source = new String();
		String ok = new String();
		int k = 1;
		for (int i = 1; i < temp.length(); i++) {
			if (temp.charAt(i) == ' ') {
				++k;
				continue;
			}
			switch (k) {
				case 1:
					target += temp.charAt(i);
					break;
				case 2:
					source += temp.charAt(i);
					break;
				case 3:
					ok += temp.charAt(i);
					break;
				default:
					break;
			}
		}
		return new Envelope(target, source, ok);
	}
	
	/**
	 * 转发添加好友的结果给请求方
	 * 协议格式：
	 * send1: B
	 * send2: targetId sourceId result
	 *
	 * @param envelope 信封
	 * @throws IOException
	 */
	public static String transforAddFriendResult( Envelope envelope ) throws IOException {
		
		return new String("B" + envelope.getTargetAccountId() + " " + envelope.getSourceAccountId() + " " + envelope.getText());
	}
	
	/**
	 * 获取查找用户的请求
	 * 接收格式：
	 * recv1: Starget source
	 *
	 * @return 返回信封
	 * @throws IOException
	 */
	public static Envelope getSearchedUserId( String temp ) throws IOException {
		
		String target = new String();
		String source = new String();
		int k = 1;
		for (int i = 1; i < temp.length(); i++) {
			if (temp.charAt(i) == ' ') {
				++k;
				continue;
			}
			switch (k) {
				case 1:
					target += temp.charAt(i);
					break;
				case 2:
					source += temp.charAt(i);
					break;
				default:
					break;
			}
		}
		return new Envelope(target, source, "");
	}
	
	/**
	 * 发送查找用户结果
	 * 协议格式：
	 * send1: SID nickName Signature
	 * 如果用户不存在，请account中全部填false
	 *
	 * @param account 查找到的用户信息
	 * @throws IOException
	 */
	public static String sendSearchResult( Account account ) throws IOException {
		
		return new String("S" + account.getID() + " " + account.getNikeName() + " " + account.getSignature());
	}
	
	/**
	 * 接收删除用户的请求
	 * 接收格式：
	 * recv1: Dtarget source
	 *
	 * @return 返回信封
	 * @throws IOException
	 */
	public static Envelope recvDelFriend( String temp ) throws IOException {
		
		String target = new String();
		String source = new String();
		int k = 1;
		for (int i = 1; i < temp.length(); i++) {
			if (temp.charAt(i) == ' ') {
				++k;
				continue;
			}
			switch (k) {
				case 1:
					target += temp.charAt(i);
					break;
				case 2:
					source += temp.charAt(i);
					break;
				default:
					break;
			}
		}
		return new Envelope(target, source, "");
	}
	
	/**
	 * 将删除的结果返回给客户端
	 * 协议格式：
	 * send2: DtargetId result
	 *
	 * @param envelope 信封
	 * @param result   删除的结果
	 * @throws IOException
	 */
	public static String sendDelResult( Envelope envelope, boolean result ) throws IOException {
		
		return new String("D" + envelope.getTargetAccountId() + result);
	}
	
	/**
	 * 接收发送的注册账户请求
	 * 接收格式：
	 * recv1: RnikeName password
	 *
	 * @return 返回一个注册账户类
	 * @throws IOException
	 */
	public static RegistorAccount recvRegistor( String temp ) throws IOException {
		
		String account = new String();
		String password = new String();
		int k = 1;
		for (int i = 1; i < temp.length(); i++) {
			if (temp.charAt(i) == ' ') {
				++k;
				continue;
			}
			switch (k) {
				case 1:
					account += temp.charAt(i);
					break;
				case 2:
					password += temp.charAt(i);
					break;
				default:
					break;
			}
		}
		return new RegistorAccount(account, password);
	}
	
	/**
	 * 发送新注册用户的ID号码
	 * 协议格式：
	 * send1: R
	 * send2: accountId
	 *
	 * @param newAccountId 新获得的ID号码
	 * @throws IOException
	 */
	public static String sendRegistorId( String newAccountId ) throws IOException {
		
		return new String("R" + newAccountId);
	}
	
	/**
	 * 判断结果是否可行
	 *
	 * @param ok 结果
	 * @return
	 */
	private boolean isOk( String ok ) {
		
		if (ok.equals(OK) || ok.equals("true")) {
			return true;
		} else {
			return false;
		}
	}
	
}
