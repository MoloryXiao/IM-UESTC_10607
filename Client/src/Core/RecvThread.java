package Core;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import network.NetworkForClient.NetworkForClient;

/**
 * 收线程 包含接收队列 用于接收从服务器发来的信息
 * @author Murrey
 * @version 1.0
 * Inital.
 */
public class RecvThread extends Thread {
	private NetworkForClient net_controller;		// 在收发控制器中初始化
	private BlockingQueue<String> queue_str_recv;	// 接收消息队列
	private volatile boolean flag_recv;
	
	public RecvThread(NetworkForClient net_controller) {
		this.net_controller = net_controller;
		this.queue_str_recv = new LinkedBlockingQueue<String>();
		flag_recv = false;
	}
	
	@Override
	public void run() {
		while(true) {
			if(flag_recv) {
				try {
					queue_str_recv.put(net_controller.recvFromServer());
				} catch (IOException e) {
					// 服务器断开了连接
					System.out.println("RecvThreadError: net_recv. May be socket close.");
					net_controller.endConnect();
					// e.printStackTrace();
				} catch (InterruptedException e) {
					System.out.println("RecvThreadError: queue_put.");
					e.printStackTrace();
				}
			}			
		}
	}
	
	public String getFromRecvQueue() {
		String message = null;
		try {
			message = queue_str_recv.take();
		} catch (InterruptedException e) {
			System.out.println("RecvThreadError: queue_take.");
			e.printStackTrace();
		}
		return message;
	}
	
	public void setFlagRecv(boolean flag) {
		this.flag_recv = flag;
	}
	
	public boolean getFlagRecv() {
		return this.flag_recv;
	}
}
