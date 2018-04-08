package Network_Client;

/**
 * Class Name:Login
 * @author ZiQin
 * @version 1.0.0
 * 用户登录时所需基本信息的封装
 * <em>该类不允许使用默认构造函数</em>
 * Created by ZiQin on 2018/3/25.
 */
public class Login {

    private AccountBase account;

    private String password;

    /**
     * 构造函数
     * @param ac 基本用户类
     * @param passwd 该用户的登录密码
     */
    public Login(AccountBase ac, String passwd) {
        account = new AccountBase(ac);
        password = new String(passwd);
    }

    /**
     * 构造函数
     * @param id 用户的ID号码
     * @param passwd 该用户的登录密码
     */
    public Login(String id, String passwd) {
        account = new AccountBase(id);
        password = new String(passwd);
    }

    /**
     * 获取用户的ID号码
     * @return 返回用户的ID号码
     */
    public String getAccountId() {
        return new String(account.getID());
    }

    /**
     * 获取用户的密码
     * @return 返回用户的密码
     */
    public String getPassword() {
        return new String(password);
    }
}
