package network.NetworkForServer;

import network.commonClass.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

/**
 * 服务器端的网络通信
 * @author ZiQin
 * @version 2.0.0
 */
public class CommunicateWithClient {

    /**
     * 与客户端连接的socket
     */
    private Socket client;

    /**
     * 连接客户端的输入流
     */
    private DataInputStream in;

    /**
     * 连接客户端的输出流
     */
    private DataOutputStream out;

    /**
     * 构造函数
     * @param socket 连接客户端的套接字
     * @throws IOException IO异常，一般情况下的Socket引发的异常
     */
    public CommunicateWithClient(Socket socket) throws IOException {
        client = socket;
        in = new DataInputStream(client.getInputStream());
        out = new DataOutputStream(client.getOutputStream());
    }

    /**
     * 从客户端接收信息
     * @return 返回客户端发来的信息
     * @throws IOException IO异常，一般情况下的Socket引发的异常
     */
    public Message recvFromClient() throws IOException {
        // 先接收文本字节流
        byte[] textBytes = recvDataFromClient();
        String text = ConvertTypeTool.byteArrayToStr(textBytes);
        // 再接收字节流（图片或其他）
        byte[] stream = recvDataFromClient();
        return new Message(text, stream);
    }

    /**
     * 将信息发送给客户端
     * @param msg 要被发送的信息
     * @throws IOException IO异常，一般情况下的Socket引发的异常
     */
    public void sendToClient(Message msg) throws IOException {
        sendDataToClient(msg.getText(), msg.getStream());
    }

    /**
     * 与客户端结束连接
     * @throws IOException IO异常，一般情况下的Socket引发的异常
     */
    public void endConnect() throws IOException {
        in.close();
        out.close();
        client.close();
    }

    /**
     * 获取连接客户端的Socket
     * @return 连接客户端的Socket
     */
    public Socket getClientSocket() {

        return client;
    }

    /**
     * 向远程服务器发送数据
     * @param msg 发送的信息内容
     * @throws IOException IO异常，一般情况下为Socket引发的IO错误
     */
    private void sendDataToClient(String msg, byte[] stm) throws IOException {
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
    private byte[] recvDataFromClient() throws IOException {
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