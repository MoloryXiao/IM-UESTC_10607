package network.messageOperate;

import network.commonClass.*;

import java.io.IOException;
import java.util.ArrayList;

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
     * 请求服务器发送自己的个人信息
     * send1: I
     */
    public static String AskMyself() throws IOException {
        return new String("I");
    }

    /**
     * 接受服务器发来的个人信息
     * @return 个人信息
     */
    public static Account getMyself(String temp) throws IOException {
        int k = 1;
        String ID = new String();
        String nickName = new String();
        String signature = new String();
        for (int j = 1; j < temp.length(); j++) {
            if (temp.charAt(j) == ' ' && k != 4) {
                ++k;
                continue;
            }
            switch (k) {
                case 1:
                    ID += temp.charAt(j);
                    break;
                case 2:
                    nickName += temp.charAt(j);
                    break;
                case 3:
                    break;
                case 4:
                    signature += temp.charAt(j);
                    break;
                default:
                    break;
            }
        }
        return new Account(ID, nickName, true, signature);
    }

    /**
     * 向远程服务器请求好友列表
     * 协议格式：
     * send1: F
     * @throws IOException
     */
    public static String askFriendListFromServer() throws IOException {
        return new String("F");
    }

    /**
     * 从远端服务器获取好友列表
     * 协议格式：
     * recv1: Number
     * recvN: ID nikeName isOnline signature
     * @return 返回好友列表
     * @throws IOException
     */
    public static ArrayList<Account> getFriendList(String temp) throws IOException {
        int i;
        String numberString = new String();
        for (i = 1; temp.charAt(i) != ' '; i++) {
            numberString += temp.charAt(i);
        }
        ++i;
        int number = Integer.valueOf(numberString);
        int k;
        ArrayList<Account> list = new ArrayList<Account>();
        while (i < temp.length()) {
            k = 1;
            String ID = new String();
            String nikeName = new String();
            String isOnline = new String();
            String signature = new String();
            while (k < 5) {
                if (temp.charAt(i) == ' ') {
                    ++k;
                    ++i;
                    continue;
                }
                switch (k) {
                    case 1:
                        ID += temp.charAt(i);
                        break;
                    case 2:
                        nikeName += temp.charAt(i);
                        break;
                    case 3:
                        isOnline += temp.charAt(i);
                        break;
                    case 4:
                        signature += temp.charAt(i);
                        break;
                    default:
                        break;
                }
                ++i;
            }
            boolean online;
            if (isOnline.equals("true")) {
                online = true;
            }
            else {
                online = false;
            }
            Account ac = new Account(ID, nikeName, online, signature);
            list.add(ac);
        }
        return list;
    }

    /**
     * 向好友发送信息
     * 协议格式：
     * send1: CtargetID sourceID text
     * @param friendId 接收方ID号码
     * @param msg 信息内容
     * @throws IOException
     */
    public static String sendMsgToFriend(String friendId, String sourceID,String msg) throws IOException {
        return new String("C" + friendId + " " + sourceID + " " + msg);
    }

    /**
     * 向好友发送信息（重载）
     * 协议格式：
     * send1: CtargetID sourceID text
     * @param envelope 信封封装
     * @throws IOException
     */
    public static String sendMsgToFriend(Envelope envelope) throws IOException {
        return new String("C" + envelope.getTargetAccountId() + " " + envelope.getSourceAccountId() + " " + envelope.getText());
    }

    /**
     * 获取好友发来的信息
     * 支持格式：
     * recv: CtargetID sourceID text
     * @return 返回信封
     * @throws IOException
     */
    public static Envelope getMsgFromFriend(String temp) throws IOException {
        String friendId = new String();
        String me = new String();
        String msg = new String();
        int k = 1;
        for (int i = 1; i < temp.length(); i++) {
            if (temp.charAt(i) == ' ' && k != 3) {
                ++k;
                continue;
            }
            switch (k) {
                case 1:
                    me += temp.charAt(i);
                    break;
                case 2:
                    friendId += temp.charAt(i);
                    break;
                case 3:
                    msg += temp.charAt(i);
                    break;
                default:
                    break;
            }
        }
        return new Envelope(me, friendId, msg);
    }

    /**
     * 发送添加好友请求
     * 协议格式:
     * send1: AtargetId sourceId
     * @param targetID 要添加的好友ID
     * @throws IOException
     */
    public static String addFriend(String targetID, String sourceID) throws IOException {
        return new String("A" + targetID + " " + sourceID);
    }

    /**
     * 对对方添加好友的请求的处理结果进行回应
     * 协议格式：
     * send1: BtargetId sourceId
     * @param envelope 信封
     * @param ok 对方好友请求结果
     * @throws IOException
     */
    public static String sendAddFriendResult(Envelope envelope, boolean ok) throws IOException {
        return new String("B" + envelope.getTargetAccountId() + " " +  envelope.getSourceAccountId() + " " + ok);
    }

    /**
     * 获取添加好友结果
     * 接收格式：
     * recv1: targetId sourceId result
     * @return 返回添加好友结果
     * @throws IOException
     */
    public static boolean addFriendResult(String temp) throws IOException {
        String result = new String();
        int k = 1;
        for (int i = 1; i < temp.length(); i++) {
            if (temp.charAt(i) == ' ') {
                ++k;
                continue;
            }
            switch (k) {
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    result += temp.charAt(i);
                    break;
                default:
                    break;
            }
        }
        return isOk(result);
    }

    /**
     * 发送查找用户的请求
     * 协议格式：
     * send2: StargetId sourceId
     * @param AccountId 查找用户的ID号码
     * @throws IOException
     */
    public static String sendSearchUser(String AccountId, String myselfID) throws IOException {
        return new String("S" + AccountId + " " + myselfID);
    }

    /**
     * 获取查找用户的结果
     * 接收类型：
     * recv1: BId nickName signature
     * @return Account类型，用户信息，若未找到则返回null
     * @throws IOException
     */
    public static Account getSearchResult(String temp) throws IOException {
        String target = new String();
        String name = new String();
        String signature = new String();
        int k = 1;
        for (int i = 0; i < temp.length(); i++) {
            if (temp.charAt(i) == ' ') {
                ++k;
                continue;
            }
            switch (k) {
                case 1:
                    target += temp.charAt(i);
                    break;
                case 2:
                    name += temp.charAt(i);
                    break;
                case 3:
                    signature += temp.charAt(i);
                    break;
                default:
                    break;
            }
        }
        if (target.equals("false")) {
            return null;
        }
        else {
            return new Account(target, name, false, signature);
        }
    }

    /**
     * 发送删除好友请求
     * 协议格式：
     * send2: DtargetId sourceId
     * @param account 将要删除的好友ID号码
     * @throws IOException
     */
    public static String sendDelFriend(String account, String sourceID) throws IOException {
        return new String("D" + account + " " + sourceID);
    }

    /**
     * 从服务器端接收删除好友的结果
     * 接收格式：
     * recv1: DtargetId sourceId
     * @return 返回信封，信封内容为删除结果
     * @throws IOException
     */
    public static Envelope recvDelResult(String temp) throws IOException {
        String target = new String();
        String result = new String();
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
                    result += temp.charAt(i);
                    break;
                default:
                    break;
            }
        }
        return new Envelope(target, "", result);
    }

    /**
     * 发送给注册账户的请求
     * 协议格式：
     * send2: RnickName password
     * @param nickName 昵称
     * @throws IOException
     */
    public static String sendRegistor(String nickName, String password) throws IOException {
        return new String("R" + nickName + " " + password);
    }

    /**
     * 接收新注册的ID
     * 接收格式：
     * recv1: newID
     * @return 返回注册的ID号码
     * @throws IOException
     */
    public static String recvRegistorId(String temp) throws IOException {
        return temp;
    }

    /**
     * 判断结果是否可行
     * @param ok 结果
     * @return
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
