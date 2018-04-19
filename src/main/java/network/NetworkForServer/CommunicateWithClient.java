package network.NetworkForServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * 服务器端的网络通信
 * @author ZiQin
 * @version 1.1.1
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
    public String recvFromClient() throws IOException {
        
        return new String(recvDataFromClient());
    }
    
    /**
     * 将信息发送给客户端
     * @param msg 要被发送的信息
     * @throws IOException IO异常，一般情况下的Socket引发的异常
     */
    public void sendToClient(String msg) throws IOException {
        sendDataToClient(msg);
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
     * 发送数据给客户端
     * @param msg 信封内容
     * @throws IOException IO异常，一般情况下的Socket引发的异常
     */
    private void sendDataToClient(String msg) throws IOException {
        out.writeUTF(msg);
    }

    /**
     * 接收客户端发送过来的信息
     * @return 返回客户端发来的信息
     * @throws IOException IO异常，一般情况下的Socket引发的异常
     */
    private String recvDataFromClient() throws IOException {
        return in.readUTF();
    }

    /**
     * 获取连接客户端的Socket
     * @return 连接客户端的Socket
     */
    public Socket getClientSocket() {
        
        return client;
    }
}
