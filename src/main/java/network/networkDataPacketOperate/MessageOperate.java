package network.networkDataPacketOperate;

import java.util.ArrayList;

import network.NetworkForServer.RegistorAccount;
import network.commonClass.*;

/**
 * 标准通信协议
 * @author ZiQin
 * @version v1.0.1
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
	 * @param msg 收到的信息
	 * @return 信息类型
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
	 * 打包反馈信息
	 * 协议格式：Mmsg
	 * @param msg 反馈信息的内容
	 * @return 信息标准通信协议
	 */
	public static String packageFeedbackMsg(String msg ) {
		return new String("M" + msg);
	}

	/**
	 * 打包成功信息
	 * 协议格式：Mok
	 * @return 信息标准通信协议
	 */
	public static String packageFinishMsg() {
		return new String("M" + OK);
	}

	/**
	 * 打包失败信息
	 * 协议格式：Mfalse
	 * @return 信息标准通信协议
	 */
	public static String packageNotFinishMsg() {
		
		return new String("M" + FALSE);
	}

	/**
	 * 解析客户端发送过来的登录信息
	 * 协议格式：LID\fpassword
	 * @param msg 接收到的协议信息
	 * @return Login对象，解析的结果
	 */
	public static Login unpackLoginMsg(String msg ) {

		String loginMsg = msg.substring(1);
		String[] item = loginMsg.split("\f");
		return new Login(item[0], item[1]);
	}


	/**
	 * 打包个人基本信息
	 * 协议格式：IID \f Nickname \f true \f signature
	 * @param ac 个人基本信息对象
	 * @return 标准通信协议
	 */
	public static String packageUserInfo(Account ac ) {

		return new String("I" + ac.getId() + "\f" + ac.getNikeName() + "\ftrue\f" + ac.getSignature());
	}

	/**
	 * 打包多个好友的基本个人信息
	 * 协议格式：Fnumber(\f Id \n nickname \n online \n signature) * number
	 * @param list 多个好友的基本个人信息
	 * @return 标准通信协议
	 */
	public static String packageFriendList(ArrayList<Account> list ) {

		String msg = new String();
		msg += "F";
		int number = list.size();

		msg += Integer.toString(number);
		for (int i = 0; i < number; i++) {
			Account ac = list.get(i);
			msg += "\f" + ac.getId() + "\n" + ac.getNikeName() + "\n" + ac.getOnline() + "\n" + ac.getSignature();
		}

		return msg;
	}

	/**
	 * 解析客户端发送过来的信封协议
	 * 协议格式：CtargetId sourceId msg
	 * @param msg 标准通信协议（内含信封）
	 * @return 信封
	 */
	public static Envelope unpackEnvelope(String msg ) {
		
		String targetAccount = new String();
		String sourceAccount = new String();
		String message = new String();
		int k = 1;
		for (int i = 1; i < msg.length(); i++) {
			if (msg.charAt(i) == ' ' && k != 3) {
				++k;
				continue;
			}
			switch (k) {
				case 1:
					targetAccount += msg.charAt(i);
					break;
				case 2:
					sourceAccount += msg.charAt(i);
					break;
				case 3:
					message += msg.charAt(i);
					break;
				default:
					break;
			}
		}
		return new Envelope(targetAccount, sourceAccount, message);
	}

	/**
	 * 打包信封
	 * 协议格式：CtargetID sourceID msg
	 * @param targetAccount 收件人
	 * @param sourceAccount 发件人
	 * @param msg 信封内容
	 * @return 标准协议格式
	 */
	public static String packageEnvelope(String targetAccount, String sourceAccount, String msg ) {
		
		return new String("C" + targetAccount + " " + sourceAccount + " " + msg);
	}

	/**
	 * 打包信封
	 * 协议格式：CtargetID sourceID msg
	 * @param envelope 信封
	 * @return 标准协议格式
	 */
	public static String packageEnvelope(Envelope envelope ) {
		
		return new String("C" + envelope.getTargetAccountId() + " " + envelope.getSourceAccountId() + " " + envelope.getText());
	}

	/**
	 * 解析包含好友请求的协议
	 * 协议格式：AtargetId sourceId
	 * @param msg 标准通信协议
	 * @return 装有好友请求的信封
	 */
	public static Envelope unpackAddFriendMsg(String msg ) {
		String target = new String();
		String source = new String();
		int k = 1;
		for (int i = 1; i < msg.length(); i++) {
			if (msg.charAt(i) == ' ') {
				++k;
				continue;
			}
			switch (k) {
				case 1:
					target += msg.charAt(i);
					break;
				case 2:
					source += msg.charAt(i);
					break;
				default:
					break;
			}
		}
		return new Envelope(target, source, "");
	}

	/**
	 * 打包添加好友的信息
	 * 协议格式：AtargetId sourceId
	 * @param envelope 存有好友信息的信封
	 * @return 标准通信协议
	 */
	public static String packageAddFriendMsg(Envelope envelope) {
		return new String("A" + envelope.getTargetAccountId() + " " + envelope.getSourceAccountId());
	}

	/**
	 * 解析对方反馈添加好友的信息
	 * 协议格式：BtargetId sourceId ok
	 * @param msg 标准通信协议
	 * @return 存有反馈信息以及双方ID的信封
	 */
	public static Envelope unpackAddFriendFeedbackMsg(String msg) {
		
		String target = new String();
		String source = new String();
		String ok = new String();
		int k = 1;
		for (int i = 1; i < msg.length(); i++) {
			if (msg.charAt(i) == ' ') {
				++k;
				continue;
			}
			switch (k) {
				case 1:
					target += msg.charAt(i);
					break;
				case 2:
					source += msg.charAt(i);
					break;
				case 3:
					ok += msg.charAt(i);
					break;
				default:
					break;
			}
		}
		return new Envelope(target, source, ok);
	}

	/**
	 * 打包添加好友反馈的信息
	 * 协议格式：BtargetId sourceId ok
	 * @param envelope 存有反馈信息以及双方ID的信封
	 * @return 标准通信协议
	 */
	public static String packageAddFriendFeedbackMsg(Envelope envelope) {
		return new String("B" + envelope.getTargetAccountId() + " " + envelope.getSourceAccountId() + " " + envelope.getText());
	}

	/**
	 * 解析查找用户ID的协议包
	 * 协议格式：BtargetId sourceId ok
	 * @param msg 标准通信协议
	 * @return 存有查找用户Id的信封（targetId即是用户查找的目标ID）
	 */
	public static Envelope unpackSearchUserIdMsg(String msg ) {
		
		String target = new String();
		String source = new String();
		int k = 1;
		for (int i = 1; i < msg.length(); i++) {
			if (msg.charAt(i) == ' ') {
				++k;
				continue;
			}
			switch (k) {
				case 1:
					target += msg.charAt(i);
					break;
				case 2:
					source += msg.charAt(i);
					break;
				default:
					break;
			}
		}
		return new Envelope(target, source, "");
	}

	/**
	 * 打包搜索到的好友基本信息（如果没有找到好友，则参数应该为空引用，即null）
	 * 协议格式：SId\fnickName\fisOnline\fsignature
	 * @param account 被搜索的好友
	 * @return 标准通信协议
	 */
	public static String packageSearchResultMsg(Account account ) {
		if (account == null) {
			return new String ("Snull");
		}
		else {
			return new String("S" + account.getId() + "\f" + account.getNikeName() + "\f" +
					account.getOnline() + "\f" + account.getSignature());
		}
	}

	/**
	 * 解析删除好友信息
	 * 协议格式：DtargetId sourceId
	 * @param msg 标准通信协议
	 * @return 保存有删除目标好友ID的信封
	 */
	public static Envelope unpackDelFriendMsg(String msg ) {
		
		String target = new String();
		String source = new String();
		int k = 1;
		for (int i = 1; i < msg.length(); i++) {
			if (msg.charAt(i) == ' ') {
				++k;
				continue;
			}
			switch (k) {
				case 1:
					target += msg.charAt(i);
					break;
				case 2:
					source += msg.charAt(i);
					break;
				default:
					break;
			}
		}
		return new Envelope(target, source, "");
	}

	/**
	 * 打包删除好友信息
	 * 协议格式：DtargetId sourceId result
	 * @param envelope 装有删除好友信息的信封
	 * @param result 删除结果
	 * @return 标准通信协议
	 */
	public static String packageDelFriendMsg(Envelope envelope, boolean result ) {
		
		return new String("D" + envelope.getTargetAccountId() + result);
	}

	/**
	 * 解析注册消息
	 * 协议格式：Rnickname\fpassword
	 * @param msg 标准通信协议
	 * @return 注册账户对象
	 */
	public static RegistorAccount unpackRegistorMsg(String msg ) {

		String registorInfo = msg.substring(1);
		String[] item = registorInfo.split("\f");
		return new RegistorAccount(item[0], item[1]);
	}

	/**
	 * 打包注册后得到的ID
	 * 协议格式：RnewId
	 * @param newAccountId 新注册的ID号
	 * @return 标准通信协议
	 */
	public static String sendRegistorId( String newAccountId ) {
		
		return new String("R" + newAccountId);
	}
	
	/**
	 * 判断结果是否可行
	 * @param ok 结果
	 * @return 判断结果
	 */
	private boolean isOk( String ok ) {
		
		if (ok.equals(OK) || ok.equals("true")) {
			return true;
		} else {
			return false;
		}
	}
	
}
