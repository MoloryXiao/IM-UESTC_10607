package network.commonClass;

import java.util.Date;

/**
 * 描述：Envelope类用于打包发送方和接收方的信息
 * @author 土豆
 * @version 1.1.0
 */
public class Envelope {
	/**
	 * 描述：发送方，AccountBase类型
	 */
	protected AccountBase recv;
	/**
	 * 描述：接收方，AccountBase类型
	 */
	protected AccountBase send;
	/**
	 * 描述：收发的信内容，String类型
	 */
	protected String text;
	/**
	 * 描述：该信封创建或发送的日期（精确到毫秒），long类型
	 */
	protected long time;
	
	public Envelope() {
		recv = null;
		send = null;
		text = null;
	}
	
	/**
	 * 描述：构造函数
	 * @param receId 发送方的id
	 * @param sendId 接收方的id
	 * @param text 信的内容
	 */
	public Envelope(String receId,String sendId,String text) {
		recv = new AccountBase(receId);
		send = new AccountBase(sendId);
		this.text = new String(text);

	}
	/**
	 * 描述：构造函数
	 * @param other 其他的对象
	 */
	public Envelope(Envelope other) {
		recv = new AccountBase(other.getTargetAccountId());
		send = new AccountBase(other.getSourceAccountId());
		text = new String(other.getText());
	}
	/**
	 * 描述：获取接收方的id
	 * @return id 接收方的id
	 */
	public String getTargetAccountId() {
		return new String(recv.getId());
	}
	/**
	 * 描述：获取发送方的id
	 * @return id 发送方的id
	 */
	public String getSourceAccountId() {
		return new String(send.getId());
	}
	/**
	 * 描述：获取信的内容
	 * @return 信的信息
	 */
	public String getText() {
		return new String(text);
	}
	/**
	 * 描述：更新日期
	 */
	public void updateDate() {
		time = System.currentTimeMillis();
	}
	/**
	 * 描述：获取信封日期
	 * @return Date类，信封最新日期。该类可以直接由println直接输出日期。
	 */
	public Date getDate() {
		Date date = new Date();
		date.setTime(time);
		return date;
	}
}
