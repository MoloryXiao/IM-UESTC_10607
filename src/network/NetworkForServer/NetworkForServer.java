package network.NetworkForServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 监听客户端发起连接的服务器类
 * @author ZiQin
 * @version 1.0.1
 */
public class NetworkForServer {

    /**
     * 服务器用于监听的socket
     */
    private ServerSocket server;

    /**
     * 默认构造函数
     */
    public NetworkForServer() {
        server = null;
    }

    /**
     * 设置服务器
     * @param port 监听端口
     * @return 返回设置结果
     */
    public boolean setServer(int port) {
        try {
            server = new ServerSocket(port);
            return true;
        }
        catch (IOException e) {
            return false;
        }
    }

    /**
     * 等待客户端发起连接
     * @return 返回Socket
     * @throws IOException IO异常，一般情况下的Socket引发的异常
     */
    public Socket waitConnectFromClient() throws IOException {
        return server.accept();
    }
}
