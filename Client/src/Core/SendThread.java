package Core;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import network.commonClass.Message;
import java.util.concurrent.LinkedBlockingQueue;

import network.NetworkForClient.NetworkForClient;

/**
 * 发线程 包含发送队列 用于向服务器发送队列中的信息
 * @author Murrey
 * @version 1.0
 * Inital.
 */
public class SendThread extends Thread{
	private NetworkForClient net_controller;		// 在收发控制器中初始化
	private BlockingQueue<Message> queue_str_send;	// 等待发送消息队列
	private volatile boolean flag_send;
	
	public SendThread(NetworkForClient net_controller) {
		this.net_controller = net_controller;
		this.queue_str_send = new LinkedBlockingQueue<Message>();
		flag_send = false;
	}
	
	@Override
	public void run() {
		while(true) {
			if(flag_send) {
				try {
					this.net_controller.sendToServer(this.queue_str_send.take());
				} catch (IOException e) {
					System.out.println("SendThreadError: net_sendToServer.");
					flag_send = false;
					// e.printStackTrace();
				} catch (InterruptedException e) {
					System.out.println("SendThreadError: queue_take.");
					e.printStackTrace();
				}
			}			
		}
	}
	
	public void addToSendQueue(Message message) {
		try {
			queue_str_send.put(message);
		} catch (InterruptedException e) {
			System.out.println("SendThreadError: queue_put.");
			e.printStackTrace();
		}
	}
	
	public void setFlagSend(boolean flag) {
		this.flag_send = flag;
	}
	
	public boolean getFlagSend() {
		return this.flag_send;
	}
}
