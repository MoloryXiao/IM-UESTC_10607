package Core;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import Core.Controll.NetworkController;

public class Producter implements Runnable{
	protected BlockingQueue<String> queue = null;
	protected NetworkController nkc;
	
	public Producter(BlockingQueue<String> queue) {
		this.queue = queue;
		this.nkc = new NetworkController();
	}
	
	public void run() {
		while(true) {
			try {
				queue.put(nkc.recvMessageFromServer());
			} catch (IOException e) {
				System.out.println("ProducterError: recvMessage from Server Error.");
				e.printStackTrace();
			} catch (InterruptedException e) {
				System.out.println("ProducterError: BlockingQueue is full.");
				e.printStackTrace();
			}
		}
	}
}
