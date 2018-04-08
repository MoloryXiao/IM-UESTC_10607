package Server;

import Network.Server.Account;
import Network.Server.CommunicateWithClient;

import java.io.IOException;
import java.net.Socket;

public class RecvThread extends Thread {
	
	private volatile boolean exit = false;
	
	private CommunicateWithClient client;
	private Account account;
	
	/**
	 * @param exit 要设置的 exit
	 */
	public void setExit( boolean exit ) {
		
		this.exit = exit;
	}
	
	/**
	 * @return account
	 */
	public Account getAccount() {
		
		return account;
	}
	
	/**
	 * @return account ID
	 */
	public String getAccountId() {
		
		return account.getID();
	}
	
	public RecvThread( Socket socket, Account account ) throws IOException {
		
		client = new CommunicateWithClient(socket);
		this.account = account;
		
	}
	
	@Override
	public void run() {
		
		System.out.println("[ READY ] 用户ID：" + account.getID() + " 接受子线程已创建！");
		
	}
	
}
