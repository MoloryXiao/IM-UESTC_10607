package Network.Server;

/**
 * Class Name: RegistorAccount
 * @author ZiQin
 * @version 1.0.0
 * 账户注册类
 * Created by ZiQin on 2018/3/26.
 */
public class RegistorAccount {

    private String nickName;

    private String password;

    /**
     * 默认构造函数
     */
    public RegistorAccount() {
        nickName = new String();
        password = new String("");
    }

    /**
     * 构造函数
     * @param nickN 新用户昵称
     * @param passwd 密码
     */
    public RegistorAccount(String nickN, String passwd) {
        nickName = new String(nickN);
        password = new String(passwd);
    }

    /**
     * 获取昵称
     * @return 返回昵称
     */
    public String getNickName() {
        return new String(nickName);
    }

    /**
     * 获取密码
     * @return 返回密码
     */
    public String getPassword() {
        return new String(password);
    }

    /**
     * 设置昵称
     * @param nickN 新的昵称
     */
    public void setNickName(String nickN) {
        nickName = new String(nickN);
    }

    /**
     * 设置密码
     * @param passwd 新的密码
     */
    public void setPassword(String passwd) {
        password = passwd;
    }
}
