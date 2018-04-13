package network.commonClass;

/**
 * 描述：Envelope类用于打包发送方和接收方的信息
 * @author 土豆
 * @version 1.0.0
 */
public class Envelope {
	/**
	 * 描述：发送方，AccountBase类型
	 */
	protected AccountBase Rece;
	/**
	 * 描述：接收方，AccountBase类型
	 */
	protected AccountBase Send;
	/**
	 * 描述：收发的信内容，String类型
	 */
	protected String Text;
	
	public Envelope() {
		Rece = null;
		Send = null;
		Text = null;
	}
	
	/**
	 * 描述：构造函数
	 * @param receId 发送方的id
	 * @param sendId 接收方的id
	 * @param text 信的内容
	 */
	public Envelope(String receId,String sendId,String text) {
		Rece = new AccountBase(receId);
		Send = new AccountBase(sendId);
		Text = new String(text);

	}
	/**
	 * 描述：构造函数
	 * @param other 其他的对象
	 */
	public Envelope(Envelope other) {
		Rece = new AccountBase(other.getTargetAccountId());
		Send = new AccountBase(other.getSourceAccountId());
		Text = new String(other.getText());
	}
	/**
	 * 描述：获取接收方的id
	 * @return id 接收方的id
	 */
	public String getTargetAccountId() {
		return new String(Rece.getID());
	}
	/**
	 * 描述：获取发送方的id
	 * @return id 发送方的id
	 */
	public String getSourceAccountId() {
		return new String(Send.getID());
	}
	/**
	 * 描述：获取信的内容
	 * @return 信的信息
	 */
	public String getText() {
		return new String(Text);
	}
}
