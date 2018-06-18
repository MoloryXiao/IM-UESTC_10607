package Core;

import network.NetworkForClient.NetworkForClient;
import network.commonClass.Message;

public class RecvSendController {	
	
	private static RecvThread thread_recv;
	private static SendThread thread_send;
	private static NetworkForClient net_controller;
	
	public RecvSendController(NetworkForClient net_controller) {
		RecvSendController.net_controller = net_controller;
		thread_recv = new RecvThread(RecvSendController.net_controller);
		thread_send = new SendThread(RecvSendController.net_controller);
		thread_recv.start();
		thread_send.start();
	}
	
	public static void addToSendQueue(Message message) {
		thread_send.addToSendQueue(message);
	}
	
	public static Message getFromRecvQueue() {
		return thread_recv.getFromRecvQueue();
	}
	
	public static boolean connectToServer() {
		boolean flag = net_controller.connectToServer();
		if(flag) {
			thread_recv.setFlagRecv(true);
			thread_send.setFlagSend(true);
		}
		return flag;
	}
	public static void closeSocket() {
		closeRecvSendThread();
		net_controller.endConnect();
	}
	
	public static void closeRecvSendThread() {
		thread_recv.setFlagRecv(false);
		thread_send.setFlagSend(false);
	}
}
