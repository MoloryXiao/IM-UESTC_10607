package Core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

import network.commonClass.Account;
import network.messageOperate.MessageOperate;

public class Consumer implements Runnable{
	protected BlockingQueue<String> queue = null;
	private ChatTool ctool = null;
	
	public Consumer(BlockingQueue<String> queue,ChatTool ctool) {
		this.queue = queue;
		this.ctool = ctool;
	}
	
	public void run() {
		while(true) {
			try {
				String message = queue.take();
				switch(MessageOperate.getMsgType(message)) {
					case MessageOperate.CHAT:
						ctool.transmitEnvelope(MessageOperate.unpackEnvelope(message));
						break;
					case MessageOperate.FRIENDLIST:
						ArrayList<Account> friend_info_arraylist = new ArrayList<Account>
							(MessageOperate.unpackFriendListMsg(message));	// 拿到最新的好友列表
						ctool.transmitFriendsList(friend_info_arraylist);
//						System.out.println("answer");
						break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
