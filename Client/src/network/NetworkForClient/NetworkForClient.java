package network.NetworkForClient;

import network.commonClass.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

/**
 * 客户端的网络通信
 * @author ZiQin
 * @version 2.0.0
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
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 获取远程服务器发来的信息
     * @return 返回远程服务器发来的信息
     * @throws IOException IO异常，一般情况下的Socket引发的异常
     */
    public Message recvFromServer() throws IOException {
        byte[] textBytes = recvDataFromServer();
        String text = ConvertTypeTool.byteArrayToStr(textBytes);
        byte[] stream = recvDataFromServer();
        return new Message(text, stream);
    }

    /**
     * 将信息发送给远程服务器
     * @param msg 将要发送的信息
     * @throws IOException IO异常，一般情况下的Socket引发的异常
     */
    public void sendToServer(Message msg) throws IOException {
        sendDataToServer(msg.getText(), msg.getStream());
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
    private void sendDataToServer(String msg, byte[] stm) throws IOException {
        // 文本部分转换字节流并打包成适合传输协议的格式
        byte[] textSize = ConvertTypeTool.intToByteArray(msg.getBytes().length);
        byte[] dataPackage = ConvertTypeTool.strToByteArray(msg);
        byte[] text = byteMerger(textSize, dataPackage, dataPackage.length);
        // 字节流部分打包成适合传输协议的格式
        byte[] streamSize = ConvertTypeTool.intToByteArray(stm.length);
        byte[] stream = byteMerger(streamSize, stm, stm.length);
        // 两者合并并发送
        out.write(byteMerger(text, stream, stream.length));
        out.flush();
    }

    /**
     * 从远程服务器获取数据
     * @return 返回从服务器收到的数据
     * @throws IOException IO异常，一般情况下为Socket引发的IO错误
     */
    private byte[] recvDataFromServer() throws IOException {
        byte[] sizeByte = new byte[4];
        byte[] msgByte = new byte[1024];
        byte[] msg = new byte[0];
        // 解析一个流的大小
        in.read(sizeByte, 0, 4);
        int size = ConvertTypeTool.byteArrayToInt(sizeByte);
        // 根据大小读取余下的字节流
        while (size > 0) {
            Arrays.fill(msgByte, (byte)0);
            int readBytesNumber = in.read(msgByte, 0, size>1024?1024:size);
            msg = byteMerger(msg, msgByte, readBytesNumber);
            size -= readBytesNumber;
        }
        return msg;
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
