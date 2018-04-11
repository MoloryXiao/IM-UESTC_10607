package network.NetworkForServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Class Name:CommunicateWithClient
 * 服务器端的网络通信
 * Created by ZiQin on 2018/4/10.
 * @author ZiQin
 * @version 1.1.0
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
     * @throws IOException
     */
    public CommunicateWithClient(Socket socket) throws IOException {
        client = socket;
        in = new DataInputStream(client.getInputStream());
        out = new DataOutputStream(client.getOutputStream());
    }

    /**
     * 从客户端接收信息
     * @return 返回客户端发来的信息
     * @throws IOException
     */
    public String recvFromClient() throws IOException {
        return new String(recvDataFromClient());
    }

    /**
     * 与客户端结束连接
     * @throws IOException
     */
    public void endConnect() throws IOException {
        in.close();
        out.close();
        client.close();
    }

    /**
     * 发送数据给客户端
     * @param msg 信封内容
     * @throws IOException
     */
    private void sendDataToClient(String msg) throws IOException {
        out.writeUTF(msg);
    }

    /**
     * 接收客户端发送过来的信息
     * @return 返回客户端发来的信息
     * @throws IOException
     */
    private String recvDataFromClient() throws IOException {
        return in.readUTF();
    }
}
