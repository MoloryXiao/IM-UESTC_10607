package network.NetworkForClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Class Name:NetworkForClient
 * 客户端的网络通信
 * Created by ZiQin on 2018/4/10.
 * @author ZiQin
 * @version 1.1.1
 */
public class NetworkForClient {

    public static final String OK = "ok";

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
     * 构造函数
     * @param ht 远程服务器地址
     * @param pr 远程服务器端口号
     */
    public NetworkForClient(String ht, int pr) {
        hostName = ht;
        port = pr;
        client = null;
        out = null;
        in = null;
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
            sendDataToServer("L" + account + "\f" + password);
            String temp = recvDataFromServer();
            String res = new String();
            int i = 1;
            while (i < temp.length()) {
            	res += temp.charAt(i);
            	++i;
            }
            if (isOk(res)) {
                ID = account;
                return true;
            }
            else {
                return false;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取远程服务器发来的信息
     * @return 返回远程服务器发来的信息
     * @throws IOException IO异常，一般情况下的Socket引发的异常
     */
    public String recvFromServer() throws IOException {
        return new String(recvDataFromServer());
    }
    /**
     * 将信息发送给远程服务器
     * @param msg 将要发送的信息
     * @throws IOException IO异常，一般情况下的Socket引发的异常
     */
    public void sendToServer(String msg) throws IOException {
    	sendDataToServer(msg);
    }

    /**
     * 获取ID号
     * @return ID号
     */
    public String getId() {
        return new String(ID);
    }

    /**
     * 与远程服务器断开连接
     * @throws IOException IO异常，一般情况下的Socket引发的异常
     */
    public void endConnect(){
        try {
			in.close();
	        out.close();
	        client.close();
		} catch (IOException e) {
			in = null;
			out = null;
			client = null;
			e.printStackTrace();
		}
    }

    /**
     * 向远程服务器发送数据
     * @param msg 发送的信息内容
     * @throws IOException IO异常，一般情况下为Socket引发的IO错误
     */
    private void sendDataToServer(String msg) throws IOException {
        out.writeUTF(msg);
    }

    /**
     * 从远程服务器获取数据
     * @return 返回从服务器收到的数据
     * @throws IOException IO异常，一般情况下为Socket引发的IO错误
     */
    private String recvDataFromServer() throws IOException {
        return in.readUTF();
    }

    /**
     * 判断结果是否可行
     * @param ok 结果
     * @return 判断结果
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
