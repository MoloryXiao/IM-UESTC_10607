package network.commonClass;

/**
 * 描述：Account类是用户信息的封装，继承AccountBase类
 * @author 土豆
 * @version 1.0.1
 */
public class Account extends AccountBase{
	/**
	 * 描述：用户的昵称
	 */
	protected String name;
	/**
	 * 描述：用户是否在线
	 */
	protected boolean online;
	/**
	 * 描述：用户的个性签名
	 */
	protected String signature;
	/**
	 * 描述：构造函数
	 */
	public Account(){
		super();
		name = new String();
		online = false;
		signature = new String();
	}
	/**
	 * 描述：构造函数
	 * @param id 用户账号
	 * @param name 昵称
	 * @param online 是否在线
	 * @param sign 个性签名
	 */
	public Account(String id,String name,boolean online,String sign) {
		super(id);
		this.name = new String(name);
		this.online = online;
		signature = new String(sign);
	}

	/**
	 * 描述：获取ID
	 * @return 返回ID号
	 */
	public String getId() {
		return new String(super.id);
	}
	/**
	 * 描述：设置昵称
	 * @param nName 新昵称
	 */
	public void setNickName(String nName) {
		name = nName;
	}
	/**
	 * 描述：获取昵称
	 * @return name 昵称
	 */
	public String getNikeName() {
		return new String(name);
	}
	/**
	 * 描述：设置个性签名
	 * @param sign 个性签名
	 */
	public void setSignature(String sign) {
		signature = sign;
	}
	/**
	 * 描述：获取个性签名
	 * @return signature 个性签名
	 */
	public String getSignature() {
		return new String(signature);
	}
	/**
	 * 描述：设置是否在线
	 * @param online 当前状态
	 */
	public void setOnline(boolean online) {
		this.online = online;
	}
	/**
	 * 描述：获取是否在线
	 * @return 状态
	 */
	public boolean getOnline() {
		return online;
	}

}
