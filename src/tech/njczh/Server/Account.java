package tech.njczh.Server;

/**
 * Created by ZiQin on 2018/3/25.
 */
public class Account extends AccountBase {

	private String nikeName;

	private String signature;

	private boolean isOnline;

	private byte[] picture;

	public Account() {
		super();
		nikeName = new String();
		signature = new String();
		isOnline = false;
		picture = null;
	}

	public Account(String id, String nName, boolean online, String sign) {
		super(id);
		nikeName = new String(nName);
		signature = new String(sign);
		isOnline = online;
		picture = null;
	}

	public void setNikeName(String nName) {
		nikeName = new String(nName);
	}

	public void setSignature(String sign) {
		signature = new String(sign);
	}

	public void setOnline(boolean online) {
		isOnline = online;
	}

	public String getID() {
		return new String(ID);
	}

	public String getNikeName() {
		return new String(nikeName);
	}

	public String getSignature() {
		return new String(signature);
	}

	public boolean getOnlineStatus() {
		return isOnline;
	}
}
