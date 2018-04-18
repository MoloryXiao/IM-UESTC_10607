package network.commonClass;

/**
 * 描述：AccountBase类是用户基本信息类
 * @author 土豆
 * @version 1.0.1
 */

public class AccountBase {
	/**
	 * 描述：属性ID是用户ID
	 */
	protected String id;
	/**
	 * 描述：构造函数
	 */
	public AccountBase() {
		id = new String();
	}
	/**
	 * 描述：构造函数
	 * @param ID 用户ID
	 */
	public AccountBase(String ID) {
		id = ID;
	}
	/**
	 * 描述：构造函数，获取新的账号
	 * @param other 其他对象，是AccountBase类型
	 */
	public AccountBase(AccountBase other) {
		id = new String(other.getId());
	}
	/**
	 * 描述：获取ID
	 * @return id 账号
	 */
	public String getId() {
		return new String(id);
	}
	/**
	 * 描述：设置ID
	 * @param id 新ID
	 */
	public void setId(String id) {
		
		this.id = id;
	}
}
