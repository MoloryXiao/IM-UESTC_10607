package network.networkDataPacketOperate;

import java.util.ArrayList;

import network.NetworkForServer.ConvertTypeTool;
import network.commonClass.*;

/**
 * 标准通信协议
 * @author ZiQin
 * @version v1.1.0
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
	public static int getMsgType(Message msg ) {
		
		switch (msg.getText().charAt(0)) {
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
	public static Message packageFeedbackMsg(String msg ) {
		return new Message(new String("M" + msg), null);
	}

	/**
	 * 打包成功信息
	 * 协议格式：Mok
	 * @return 信息标准通信协议
	 */
	public static Message packageFinishMsg() {
		return new Message(new String("L" + OK), null);
	}

	/**
	 * 打包失败信息
	 * 协议格式：Mfalse
	 * @return 信息标准通信协议
	 */
	public static Message packageNotFinishMsg() {
		return new Message(new String("L" + FALSE), null);
	}

	/**
	 * 解析客户端发送过来的登录信息
	 * 协议格式：LID\fpassword
	 * @param msg 接收到的协议信息
	 * @return Login对象，解析的结果
	 */
	public static Login unpackLoginMsg(Message msg) {
		String text = msg.getText();
		String loginMsg = text.substring(1);
		String[] item = loginMsg.split("\f");
		return new Login(item[0], item[1]);
	}


	/**
	 * 打包个人基本信息
	 * 协议格式：IID \f Nickname \f true \f signature
	 * @param ac 个人基本信息对象
	 * @return 标准通信协议
	 */
	public static Message packageUserInfo(Account ac ) {
		String text = new String("I" + ac.getId() + "\f" + ac.getNikeName() + "\ftrue\f" + ac.getSignature());
		byte[] stream = ac.getPicture().getPictureBytes();
		return new Message(text, stream);
	}

	/**
	 * 打包多个好友的基本个人信息
	 * 协议格式：Fnumber(\f Id \n nickname \n online \n signature) * number
	 * @param list 多个好友的基本个人信息
	 * @return 标准通信协议
	 */
	public static Message packageFriendList(ArrayList<Account> list ) {
		String text = new String();
		byte[] pictureList = new byte[0];
		text += "F";
		int number = list.size();
		text += Integer.toString(number);
		for (int i = 0; i < number; i++) {
			Account ac = list.get(i);
			text += "\f" + ac.getId() + "\n" + ac.getNikeName() + "\n" + ac.getOnline() + "\n" + ac.getSignature();
			pictureList = addPictureToList(pictureList, ac.getPicture());
		}
		return new Message(text, pictureList);
	}

	/**
	 * 解析客户端发送过来的信封协议
	 * 协议格式：CtargetId sourceId msg
	 * @param msg 标准通信协议（内含信封）
	 * @return 信封
	 */
	public static Envelope unpackEnvelope(Message msg ) {
		String text = msg.getText();
		String targetAccount = new String();
		String sourceAccount = new String();
		String message = new String();
		int k = 1;
		for (int i = 1; i < text.length(); i++) {
			if (text.charAt(i) == ' ' && k != 3) {
				++k;
				continue;
			}
			switch (k) {
				case 1:
					targetAccount += text.charAt(i);
					break;
				case 2:
					sourceAccount += text.charAt(i);
					break;
				case 3:
					message += text.charAt(i);
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
	public static Message packageEnvelope(String targetAccount, String sourceAccount, String msg ) {
		String text = new String("C" + targetAccount + " " + sourceAccount + " " + msg);
		return new Message(text, null);
	}

	/**
	 * 打包信封
	 * 协议格式：CtargetID sourceID msg
	 * @param envelope 信封
	 * @return 标准协议格式
	 */
	public static Message packageEnvelope(Envelope envelope ) {
		String text = new String("C" + envelope.getTargetAccountId() + " " + envelope.getSourceAccountId() + " " + envelope.getText());
		return new Message(text, null);
	}

	/**
	 * 解析包含好友请求的协议
	 * 协议格式：AtargetId sourceId
	 * @param msg 标准通信协议
	 * @return 装有好友请求的信封
	 */
	public static Envelope unpackAddFriendMsg(Message msg ) {
		String text = msg.getText();
		String target = new String();
		String source = new String();
		int k = 1;
		for (int i = 1; i < text.length(); i++) {
			if (text.charAt(i) == ' ') {
				++k;
				continue;
			}
			switch (k) {
				case 1:
					target += text.charAt(i);
					break;
				case 2:
					source += text.charAt(i);
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
	public static Message packageAddFriendMsg(Envelope envelope) {
		String text = new String("A" + envelope.getTargetAccountId() + " " + envelope.getSourceAccountId());
		return new Message(text, null);
	}

	/**
	 * 解析对方反馈添加好友的信息
	 * 协议格式：BtargetId sourceId ok
	 * @param msg 标准通信协议
	 * @return 存有反馈信息以及双方ID的信封
	 */
	public static Envelope unpackAddFriendFeedbackMsg(Message msg) {
		String text = msg.getText();
		String target = new String();
		String source = new String();
		String ok = new String();
		int k = 1;
		for (int i = 1; i < text.length(); i++) {
			if (text.charAt(i) == ' ') {
				++k;
				continue;
			}
			switch (k) {
				case 1:
					target += text.charAt(i);
					break;
				case 2:
					source += text.charAt(i);
					break;
				case 3:
					ok += text.charAt(i);
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
	public static Message packageAddFriendFeedbackMsg(Envelope envelope) {
		String text = new String("B" + envelope.getTargetAccountId() + " " + envelope.getSourceAccountId() + " " + envelope.getText());
		return new Message(text, null);
	}

	/**
	 * 解析查找用户ID的协议包
	 * 协议格式：BtargetId sourceId ok
	 * @param msg 标准通信协议
	 * @return 存有查找用户Id的信封（targetId即是用户查找的目标ID）
	 */
	public static Envelope unpackSearchUserIdMsg(Message msg ) {
		String text = msg.getText();
		String target = new String();
		String source = new String();
		int k = 1;
		for (int i = 1; i < text.length(); i++) {
			if (text.charAt(i) == ' ') {
				++k;
				continue;
			}
			switch (k) {
				case 1:
					target += text.charAt(i);
					break;
				case 2:
					source += text.charAt(i);
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
	public static Message packageSearchResultMsg(Account account ) {
		if (account == null) {
			return new Message("Snull", null);
		}
		else {
			String text = new String("S" + account.getId() + "\f" + account.getNikeName() + "\f" +
					account.getOnline() + "\f" + account.getSignature());
			byte[] stream = account.getPicture().getPictureBytes();
			return new Message(text, stream);
		}
	}

	/**
	 * 解析删除好友信息
	 * 协议格式：DtargetId sourceId
	 * @param msg 标准通信协议
	 * @return 保存有删除目标好友ID的信封
	 */
	public static Envelope unpackDelFriendMsg(Message msg ) {
		String text = msg.getText();
		String target = new String();
		String source = new String();
		int k = 1;
		for (int i = 1; i < text.length(); i++) {
			if (text.charAt(i) == ' ') {
				++k;
				continue;
			}
			switch (k) {
				case 1:
					target += text.charAt(i);
					break;
				case 2:
					source += text.charAt(i);
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
	public static Message packageDelFriendMsg(Envelope envelope, boolean result ) {
		String text = new String("D" + envelope.getTargetAccountId() + result);
		return new Message(text, null);
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

	private static byte[] addPictureToList(byte[] pictureListStream, Picture picture) {
		byte[] picSize = ConvertTypeTool.intToByteArray(picture.getPictureSize());
		byte[] picStream = picture.getPictureBytes();
		byte[] stream = byteMerger(picSize, picStream, picStream.length);
		pictureListStream = byteMerger(pictureListStream, stream, stream.length);
		return pictureListStream;
	}

	private static Picture getOnePicture(byte[] pictureList) {
		// 获取图片在字节流中占的字节大小
		byte[] pictureSizeBytes = new byte[4];
		System.arraycopy(pictureList, 0, pictureSizeBytes, 0, 4);
		int pictureSize = ConvertTypeTool.byteArrayToInt(pictureSizeBytes);
		// 根据大小读取图片字节流
		byte[] picture = new byte[pictureSize];
		System.arraycopy(pictureList, 4, picture, 0, pictureSize);
		// 掐掉已经读取的图片字节流
		System.arraycopy(pictureList, 0, pictureList, 0, pictureSize + 4);
		return new Picture(picture);
	}

	/**
	 * 字节数组合并
	 * @param bt1 字节数组1
	 * @param bt2 字节数组2
	 * @return 字节数组1和字节数组2合并后的结果
	 */
	private static byte[] byteMerger(byte[] bt1, byte[] bt2, int bt2Size){
		byte[] bt3 = new byte[bt1.length + bt2Size];
		System.arraycopy(bt1, 0, bt3, 0, bt1.length);
		System.arraycopy(bt2, 0, bt3, bt1.length, bt2Size);
		return bt3;
	}
	
}
