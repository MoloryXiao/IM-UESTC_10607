package Network_Client;

/**
 * Class Name:Account
 * @author ZiQin
 * @version 1.0.0
 * Account类封装用户的基本信息，为AccountBase的子类
 * Created by ZiQin on 2018/3/25.
 */
public class Account extends AccountBase {

    private String nikeName;

    private String signature;

    private boolean isOnline;

    private byte[] picture;

    /**
     * 默认构造函数
     */
    public Account() {
        super();
        nikeName = new String();
        signature = new String();
        isOnline = false;
        picture = null;
    }

    /**
     * 构造函数
     * @param id 用户的ID
     * @param nName 用户的昵称
     * @param sign 用户的个性签名
     */
    public Account(String id, String nName, boolean online, String sign) {
        super(id);
        nikeName = new String(nName);
        signature = new String(sign);
        isOnline = online;
        picture = null;
    }

    /**
     * 设置用户昵称
     * @param nName 用户昵称
     */
    public void setNikeName(String nName) {
        nikeName = new String(nName);
    }

    /**
     * 设置个性签名
     * @param sign 个性签名
     */
    public void setSignature(String sign) {
        signature = new String(sign);
    }

    /**
     * 设置用户在线状态
     * @param online 该用户当前状态
     */
    public void setOnline(boolean online) { isOnline = online; }

    /**
     * 获取用户的ID
     * @return 返回用户ID
     */
    public String getID() {
        return new String(ID);
    }

    /**
     * 获取用户的昵称
     * @return 返回用户昵称
     */
    public String getNikeName() {
        return new String(nikeName);
    }

    /**
     * 获取用户的个性签名
     * @return 返回用户的个性签名
     */
    public String getSignature() {
        return new String(signature);
    }

    /**
     * 检查用户是否在线
     * @return 返回用户是否在线的状态
     */
    public boolean getOnlineStatus() { return isOnline; }

    public String toString() {
        return getClass().getName() + "[ID=" + ID + ",nikeName=" + nikeName + ",signature=" + signature + "]";
    }
}
