package com.qq.NetworkForClient;

import com.qq.BaseClass.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Class Name:NetworkForClient
 * 客户端的网络通信
 * Created by ZiQin on 2018/4/10.
 * @author ZiQin
 * @version 1.1.0
 */
public class NetworkForClient {

    /**
     * 远程服务器的地址
     */
    private String hostName;

    /**
     * 远程服务器的端口号
     */
    private int port;

    /**
     * 连接远程服务器的socket
     */
    private Socket client;

    /**
     * 连接远程服务器的输出流
     */
    private DataOutputStream out;

    /**
     * 连接远程服务器的输入流
     */
    private DataInputStream in;

    /**
     * 使用该客户端的用户ID号码
     */
    private String ID;

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
     * 构造函数
     * @param ht 远程服务器地址
     * @param pr 远程服务器端口号
     */
    public NetworkForClient(String ht, int pr) {
        hostName = ht;
        port = pr;
        client = null;
        out = null;
        ID = null;
    }

    /**
     * 对远程服务器发起TCP/IP连接
     * @return 返回连接结果
     */
    public boolean connectToServer() {
        try {
            client = new Socket(hostName, port);
            out = new DataOutputStream(client.getOutputStream());
            in = new DataInputStream(client.getInputStream());
            return true;
        }
        catch (IOException e) {
            return false;
        }
    }

    /**
     * 获取远程服务器发来的信息
     * @return 返回远程服务器发来的信息
     * @throws IOException
     */
    public String recvFromServer() throws IOException {
        return new String(recvDataFromServer());
    }

    /**
     * 检查远端服务器将要发来的协议的类型
     * @param msg 远端服务器发来的信息
     * @return 返回检查类型的结果
     */
    public int getMsgType(String msg) {
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
     * 用户登陆函数
     * 协议格式：
     * send1: L
     * send2: ID password
     * @param account 用户ID号码
     * @param password 该用户的密码
     * @return 返回密码校验结果
     */
    public boolean login(String account, String password) {
        try {
            sendDataToServer("L" + account + " " + password);
            if (isOk(recvDataFromServer())) {
                ID = account;
                return true;
            }
            else {
                return false;
            }
        }
        catch (IOException e) {
            return false;
        }
    }

    /**
     * 请求服务器发送自己的个人信息
     * send1: I
     */
    public void AskMyself() throws IOException {
        sendDataToServer("I");
    }

    /**
     * 接受服务器发来的个人信息
     * @return 个人信息
     */
    public Account getMyself(String temp) throws IOException {
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
    public void askFriendListFromServer() throws IOException {
        sendDataToServer("F");
    }

    /**
     * 从远端服务器获取好友列表
     * 协议格式：
     * recv1: Number
     * recvN: ID nikeName isOnline signature
     * @return 返回好友列表
     * @throws IOException
     */
    public ArrayList<Account> getFriendList() throws IOException {
        String temp = recvDataFromServer();
        int number = Integer.valueOf(temp);
        int k;
        ArrayList<Account> list = new ArrayList<Account>();
        for (int i = 0; i < number; i++) {
            temp = recvDataFromServer();
            k = 1;
            String ID = new String();
            String nikeName = new String();
            String isOnline = new String();
            String signature = new String();
            for (int j = 0; j < temp.length(); j++) {
                if (temp.charAt(j) == ' ' && k != 4) {
                    ++k;
                    continue;
                }
                switch (k) {
                    case 1:
                        ID += temp.charAt(j);
                        break;
                    case 2:
                        nikeName += temp.charAt(j);
                        break;
                    case 3:
                        isOnline += temp.charAt(j);
                        break;
                    case 4:
                        signature += temp.charAt(j);
                        break;
                    default:
                        break;
                }
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
    public void sendMsgToFriend(String friendId, String msg) throws IOException {
        sendDataToServer("C" + friendId + " " + ID + " " + msg);
    }

    /**
     * 向好友发送信息（重载）
     * 协议格式：
     * send1: CtargetID sourceID text
     * @param envelope 信封封装
     * @throws IOException
     */
    public void sendMsgToFriend(Envelope envelope) throws IOException {
        sendDataToServer("C" + envelope.getTargetAccountId() + " " + envelope.getSourceAccountId() + " " + envelope.getText());
    }

    /**
     * 获取好友发来的信息
     * 支持格式：
     * recv: CtargetID sourceID text
     * @return 返回信封
     * @throws IOException
     */
    public Envelope getMsgFromFriend(String temp) throws IOException {
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
    public void addFriend(String targetID) throws IOException {
        sendDataToServer("A" + targetID + " " + ID);
    }

    /**
     * 对对方添加好友的请求的处理结果进行回应
     * 协议格式：
     * send1: BtargetId sourceId
     * @param envelope 信封
     * @param ok 对方好友请求结果
     * @throws IOException
     */
    public void sendAddFriendResult(Envelope envelope, boolean ok) throws IOException {
        sendDataToServer("B" + envelope.getTargetAccountId() + " " +  envelope.getSourceAccountId() + " " + ok);
    }

    /**
     * 获取添加好友结果
     * 接收格式：
     * recv1: targetId sourceId result
     * @return 返回添加好友结果
     * @throws IOException
     */
    public boolean addFriendResult(String temp) throws IOException {
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
    public void sendSearchUser(String AccountId) throws IOException {
        sendDataToServer("S" + AccountId + " " + ID);
    }

    /**
     * 获取查找用户的结果
     * 接收类型：
     * recv1: BId nickName signature
     * @return Account类型，用户信息，若未找到则返回null
     * @throws IOException
     */
    public Account getSearchResult(String temp) throws IOException {
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
    public void sendDelFriend(String account) throws IOException {
        sendDataToServer("D" + account + " " + ID);
    }

    /**
     * 从服务器端接收删除好友的结果
     * 接收格式：
     * recv1: DtargetId sourceId
     * @return 返回信封，信封内容为删除结果
     * @throws IOException
     */
    public Envelope recvDelResult(String temp) throws IOException {
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
    public void sendRegistor(String nickName, String password) throws IOException {
        sendDataToServer("R" + nickName + " " + password);
    }

    /**
     * 接收新注册的ID
     * 接收格式：
     * recv1: newID
     * @return 返回注册的ID号码
     * @throws IOException
     */
    public String recvRegistorId() throws IOException {
        return new String(recvDataFromServer());
    }

    /**
     * 与远程服务器断开连接
     * @throws IOException
     */
    public void endConnect() throws IOException {
        in.close();
        out.close();
        client.close();
    }

    /**
     * 向远程服务器发送数据
     * @param msg 发送的信息内容
     * @throws IOException
     */
    private void sendDataToServer(String msg) throws IOException {
        out.writeUTF(msg);
    }

    /**
     * 从远程服务器获取数据
     * @return 返回从服务器收到的数据
     * @throws IOException
     */
    private String recvDataFromServer() throws IOException {
        return in.readUTF();
    }

    /**
     * 判断结果是否可行
     * @param ok 结果
     * @return
     */
    private boolean isOk(String ok) {
        if (ok.equals(OK) || ok.equals("true")) {
            return true;
        }
        else  {
            return false;
        }
    }
}
