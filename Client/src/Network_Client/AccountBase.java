package Network_Client;

/**
 * Class Name:AccountBase
 * @author ZiQin
 * @version 1.0.0
 * 用户基类，仅封装了用户的ID号码
 * Created by ZiQin on 2018/3/25.
 */
public class AccountBase {

    protected String ID;

    /**
     * 默认构造函数
     */
    public AccountBase() {
        ID = new String();
    }

    /**
     * 构造函数
     * @param Id 用户的ID
     */
    public AccountBase(String Id) {
        ID = new String(Id);
    }

    /**
     * 构造函数，用于克隆
     * @param other 另外一个AccountBase对象
     */
    public AccountBase(AccountBase other) {
        ID = new String(other.ID);
    }

    /**
     * 获取用户ID
     * @return 返回用户ID
     */
    public String getID() {
        return new String(ID);
    }
}
