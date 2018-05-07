package network.messageOperate;

import network.commonClass.*;

import java.util.ArrayList;


/**
 * 标准通信协议处理类
 * @author ZiQin
 * @version V1.0.1
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
    public static final String FALSE = "false";

    /**
     * 检查远端服务器将要发来的协议的类型
     * @param msg 远端服务器发来的信息
     * @return 返回检查类型的结果
     */
    public static int getMsgType(String msg) {
        switch (msg.charAt(0)) {
            case 'C':
                return CHAT;
            case 'L':
                return LOGIN;
            case 'M':
                return MSG;
            case 'F':
                return FRIENDLIST;
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
     * 解析反馈消息，获取服务器发来的信息
     * 协议格式：Mmsg
     * @param msg 标准通信协议
     * @return 反馈内容
     */
    public static String unpackFeedbackMsg(String msg) {
        return msg.substring(1);
    }

    /**
     * 该方法功能与unpackIsFinish方法相同，区别在与使用该方法之前需要先调用
     * unpackFeedbackMsg方法对数据报进行解析。
     * 调用者可以依个人喜好选择不同的方法
     * 协议格式：ok
     * @param msg
     * @return
     */
    public static boolean isFinish(String msg) {
        if (isOk(msg)) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 解析反馈报，检查服务器处理结果是否ok。功能与isFinish方法相同，区别仅是该方法有解析数据报的功能。
     * 注意：该方法仅仅处理服务器是否同意之类的反馈（packageFinishMsg），对于其他反馈则不能进行有效处理
     * 协议格式：Lok
     * @param msg 标准通信协议
     * @return 服务同意请求与否
     */
    public static boolean unpackIsFinish(String msg) {
        if (isOk(msg.substring(1))) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 打包用户登录信息
     * 协议格式：LId Password
     * @param Id 要登录的用户ID
     * @param passwd 要登录的用户密码
     * @return 标准通信协议
     */
    public static String packageLoginMsg(String Id, String passwd) {
        return new String("L" + Id + "\f" + passwd);
    }

    /**
     * 打包用户登录信息
     * 协议格式：LId Password
     * @param accountBase 要登录的用户ID
     * @param passwd 要登录的用户密码
     * @return 标准通信协议
     */
    public static String packageLoginMsg(AccountBase accountBase, String passwd) {
        return new String("L" + accountBase.getId() + "\f" + passwd);
    }

    /**
     * 请求服务器发送自己的个人信息
     * 协议格式：I
     * @return 标准通信协议
     */
    public static String packageAskMyselfInfoMsg() {
        return new String("I");
    }

    /**
     * 接受服务器发来的个人信息
     * 协议格式：IID\fnickname\ftrue\signature
     * @return Account对象，保存个人基本信息
     */
    public static Account unpackMyselfInfoMsg(String msg) {
        String myselfInfo = msg.substring(1);
        String[] item = myselfInfo.split("\f");
        return new Account(item[0], item[1], true, item[3]);
    }

    /**
     * 打包请求好友列表信息
     * @return 标准通信协议
     */
    public static String packageAskFriendListMsg() {
        return new String("F");
    }

    /**
     * 解析收到的好友列表
     * @param msg
     * @return 好友列表
     */
    public static ArrayList<Account> unpackFriendListMsg(String msg) {
        int i;
        String numberString = new String();
        ArrayList<Account> list = new ArrayList<Account>();
        if (msg.length() <= 2) return list;
        for (i = 1; msg.charAt(i) != '\f'; i++) {
            numberString += msg.charAt(i);
        }
        int number = Integer.valueOf(numberString);
        String friendListMsg = msg.substring(++i);
        String[] friendMsg = friendListMsg.split("\f");
        for (i = 0; i < number; i++) {
            boolean isOnline = false;
            String[] friendInfo = friendMsg[i].split("\n");
            if (friendInfo[2].equals("true")) {
                isOnline = true;
            }
            Account account = new Account(friendInfo[0], friendInfo[1], isOnline, friendInfo[3]);
            list.add(account);
        }
        return list;
    }

    /**
     * 打包信封
     * 协议格式：CtargetID sourceID text
     * @param targetID 收件人
     * @param sourceID 发件人
     * @param msg 信封内容
     * @return 标准通信协议
     */
    public static String packageEnvelope(String targetID, String sourceID, String msg) {
        return new String("C" + targetID + " " + sourceID + " " + msg);
    }

    /**
     * 打包信封
     * 协议格式：CtargetId sourceId text
     * @param envelope 信封
     * @return 标准通信协议
     */
    public static String packageEnvelope(Envelope envelope) {
        return new String("C" + envelope.getTargetAccountId() + " " + envelope.getSourceAccountId() + " " + envelope.getText());
    }

    /**
     * 解析信封协议
     * 协议格式：CtargetId sourceId text
     * @param msg 标准通信协议（包含信封）
     * @return 信封
     */
    public static Envelope unpackEnvelope(String msg) {
        String friendId = new String();
        String me = new String();
        String message = new String();
        int k = 1;
        for (int i = 1; i < msg.length(); i++) {
            if (msg.charAt(i) == ' ' && k != 3) {
                ++k;
                continue;
            }
            switch (k) {
                case 1:
                    me += msg.charAt(i);
                    break;
                case 2:
                    friendId += msg.charAt(i);
                    break;
                case 3:
                    message += msg.charAt(i);
                    break;
                default:
                    break;
            }
        }
        return new Envelope(me, friendId, message);
    }

    /**
     * 打包添加好友信息
     * 协议格式：AtargetID sourceID
     * @param targetID 目标ID
     * @param sourceID 发起ID
     * @return 标准通信协议
     */
    public static String packageAddFriendMsg(String targetID, String sourceID) {
        return new String("A" + targetID + " " + sourceID);
    }
    
    /**
     * 解包添加好友请求
     * 协议格式：AtargetID sourceID
     * @param msg 标准通信协议
     * @return 发起人ID 
     */
    public static String unpackAddFriendMsg(String msg) {
		String sourceId = new String();
		int k = 1;
        for (int i = 1; i < msg.length(); i++) {
            if (msg.charAt(i) == ' ') {
                ++k;
                continue;
            }
            switch (k) {
                case 1:
                    break;
                case 2:
                	sourceId += msg.charAt(i);
                    break;
                default:
                    break;
            }
        }
        return sourceId;
	}

    /**
     * 打包添加好友结果信息
     * 协议格式：BtargetId sourceId ok
     * @param envelope 双方ID
     * @param ok 添加结果
     * @return 标准通信协议
     */
    public static String packageAddFriendResultMsg(Envelope envelope, boolean ok) {
        return new String("B" + envelope.getTargetAccountId() + " " +  envelope.getSourceAccountId() + " " + ok);
    }

    /**
     * 解析添加好友结果协议
     * 协议格式：BtargetId sourceId ok
     * @param msg 标准通信协议
     * @return 添加结果
     */
    public static boolean unpackAddFriendResultMsg(String msg) {
        String result = new String();
        int k = 1;
        for (int i = 1; i < msg.length(); i++) {
            if (msg.charAt(i) == ' ') {
                ++k;
                continue;
            }
            switch (k) {
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    result += msg.charAt(i);
                    break;
                default:
                    break;
            }
        }
        return isOk(result);
    }

    /**
     * 打包查找好友信息
     * 协议格式：StargetId sourceId
     * @param accountID 查找目标ID
     * @param myselfID 发起人ID
     * @return 标准通信协议
     */
    public static String packageSearchFriendMsg(String accountID, String myselfID) {
        return new String("S" + accountID + " " + myselfID);
    }

    /**
     * 解析查找好友结果的协议
     * 协议格式：StargetId\fnickname\fisOnline\fsignature
     * @param msg 标准通信协议
     * @return 查找的用户，Account对象,若服务器未找到，则返回null
     */
    public static Account unpackSearchResultMsg(String msg) {

        if (msg.equals("Snull")) {
            return null;
        }
        else {
            String accountInfo = msg.substring(1);
            String[] item = accountInfo.split("\f");
            boolean online = false;
            if (item[2].equals("true")) {
                online = true;
            }
            return new Account(item[0], item[1], online, item[3]);
        }
    }

    /**
     * 打包删除好友的信息
     * 协议格式：DtargetId sourceId
     * @param target 目标ID
     * @param sourceID 发起人ID
     * @return 标准通信协议
     */
    public static String packageDelFriendMsg(String target, String sourceID) {
        return new String("D" + target + " " + sourceID);
    }

    /**
     * 解析删除好友协议
     * 协议格式：DtargetId sourceId result
     * @param msg
     * @return 信封（包含删除结果）
     */
    public static Envelope unpackDelFriendMsg(String msg) {
        String target = new String();
        String result = new String();
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
                    result += msg.charAt(i);
                    break;
                default:
                    break;
            }
        }
        return new Envelope(target, "", result);
    }

    /**
     * 打包注册信息
     * 协议格式：Rnickname\fpassword
     * @param nickName 昵称
     * @param password 密码
     * @return 标准通信协议
     */
    public static String packageRegistorMsg(String nickName, String password) {
        return new String("R" + nickName + "\f" + password);
    }

    /**
     * 解析注册协议
     * 协议格式：RID
     * @param msg 标准通信协议
     * @return 新的ID
     */
    public static String unpackRegistorMsg(String msg) {
        return msg.substring(1);
    }

    /**
     * 判断结果是否可行
     * @param ok 结果
     * @return 判断结果
     */
    private static boolean isOk(String ok) {
        if (ok.equals(OK) || ok.equals("true")) {
            return true;
        }
        else  {
            return false;
        }
    }
}
