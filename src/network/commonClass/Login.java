package network.commonClass;

/**
 * 描述：Login类用于封装登录信息
 * @author 土豆
 * @version 1.0.1
 */
public class Login {
	/**
	 * 描述：登录的账号
	 */
	protected AccountBase account;
	/**
	 * 描述：登录的密码
	 */
	protected String password;
	/**
	 * 描述：构造函数
	 * @param acc 账户
	 * @param pw 密码
	 */
	public Login(AccountBase acc,String pw) {
		account = new AccountBase(acc);
		password = new String(pw);
	}
	/**
	 * 描述：构造函数
	 * @param id 账户的账号
	 * @param pw 账户的密码
	 */
	public Login(String id,String pw) {
		account = new AccountBase(new String(id)); //
		password = new String(pw);
	}
	/**
	 * 描述：获取账户的账号
	 * @return id 账号
	 */
	public String getAccountId() {
		return new String(account.getId());
	}
	/**
	 * 描述：获取账户的密码
	 * @return password 密码
	 */
	public String getPassword() {
		return new String(password);
	}
}
