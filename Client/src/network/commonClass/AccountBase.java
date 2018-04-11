package network.commonClass;

/**
 * 描述：AccountBase类是用户基本信息类
 * @author 土豆
 * @version 1.0.0
 */

public class AccountBase {
	/**
	 * 描述：属性ID是用户ID
	 */
	protected String ID;
	/**
	 * 描述：构造函数
	 */
	public AccountBase() {
		ID = new String();
	}
	/**
	 * 描述：构造函数
	 * @param Id 用户ID
	 */
	public AccountBase(String Id) {
		ID = new String(Id);
	}
	/**
	 * 描述：构造函数，获取新的账号
	 * @param Other 其他对象，是AccountBase类型
	 */
	public AccountBase(AccountBase Other) {
		ID = new String(Other.getID());
	}
	/**
	 * 描述：获取ID
	 * @return ID 账号
	 */
	public String getID() {
		return new String(ID);
	}
}
