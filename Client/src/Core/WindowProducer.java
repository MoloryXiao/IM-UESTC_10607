package Core;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 窗口生产者 包含消息队列 用于转发窗口创建消息
 * @author Murrey
 * Inital.
 */
public class WindowProducer {
	private static LinkedBlockingQueue<Integer> queue_request;
	
	public static final int LOGIN_WIND 			= 1;
	public static final int FRIEND_LIST_WIND 	= 2;
	public static final int CHAT_WIND 			= 3;
	public static final int ADD_FRIEND_WIND 	= 4;
	public static final int INFO_MINE_WIND 		= 5;
	public static final int INFO_FRIEND_WIND 	= 6;
	public static final int INFO_MODIFY_WIND 	= 7;
	public static final int GROUP_CHAT_WIND 	= 8;
	
	public WindowProducer() {
		queue_request = new LinkedBlockingQueue<Integer>();
	}
	
	public static void addWindowRequest(int WINDOW_VALUE) {
		try {
			queue_request.put(WINDOW_VALUE);
		} catch (InterruptedException e) {
			System.out.println("WindowProducerError: queue_put");
			e.printStackTrace();
		}
	}
	
	public static int getWindowRequest() {
		int type = 0;
		try {
			type = queue_request.take();
		} catch (InterruptedException e) {
			System.out.println("WindowProducerError: queue_take");
			e.printStackTrace();
		}
		return type;
	}
}
