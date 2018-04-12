package network.commonClass;

/**
 * 描述：Account类是用户信息的封装，继承AccountBase类
 * @author 土豆
 * @version 1.0.0
 */
public class Account extends AccountBase{
	/**
	 * 描述：用户的昵称
	 */
	protected String Name;
	/**
	 * 描述：用户是否在线
	 */
	protected boolean onLine;
	/**
	 * 描述：用户的个性签名
	 */
	protected String Sign;
	/**
	 * 描述：构造函数
	 */
	public Account(){
		super();
		Name = new String();
		onLine = false;
		Sign = new String();
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
		Name = new String(name);
		onLine = online;
		Sign = new String(sign);
	}

	/**
	 * 描述：获取ID
	 * @return 返回ID号
	 */
	public String getID() {
		return new String(super.ID);
	}
	/**
	 * 描述：设置昵称
	 * @param nName 新昵称
	 */
	public void setNickName(String nName) {
		Name = nName;
	}
	/**
	 * 描述：获取昵称
	 * @return Name 昵称
	 */
	public String getNikeName() {
		return new String(Name);
	}
	/**
	 * 描述：设置个性签名
	 * @param sign 个性签名
	 */
	public void setSignature(String sign) {
		Sign = sign;
	}
	/**
	 * 描述：获取个性签名
	 * @return Sign 个性签名
	 */
	public String getSignature() {
		return new String(Sign);
	}
	/**
	 * 描述：设置是否在线
	 * @param online 当前状态
	 */
	public void setOnLine(boolean online) {
		onLine = online;
	}
	/**
	 * 描述：获取是否在线
	 * @return 状态
	 */
	public boolean getOnLine() {
		return onLine;
	}

}
