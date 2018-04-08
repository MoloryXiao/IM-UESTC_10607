package Network_Client;

/**
 * Class Name:Envelope
 * @author ZiQin
 * @version 1.0.0
 * 信封，封装用户发送的信息，其中包括发送人，收件人和内容文本
 * <em>该类不允许使用默认构造函数</em>
 * Created by ZiQin on 2018/3/25.
 */
public class Envelope {

    private AccountBase target;

    private AccountBase source;

    private String text;

    /**
     * 构造函数
     * @param tar 收件人的ID
     * @param src 发件人的ID
     * @param txt 信的文本内容
     */
    public Envelope(String tar, String src, String txt) {
        target = new AccountBase(tar);
        source = new AccountBase(src);
        text = new String(txt);
    }

    /**
     * 构造函数，用于复制其他Envelope对象
     * @param other 其他Envelope对象
     */
    public Envelope(Envelope other) {
        target = new AccountBase(other.target);
        source = new AccountBase(other.source);
        text = new String(other.text);
    }

    /**
     * 获取收件人的ID
     * @return 返回收件人的ID
     */
    public String getTargetAccountId() {
        return new String(target.getID());
    }

    /**
     * 获取发件人的ID
     * @return 返回发件人的ID
     */
    public String getSourceAccountId() {
        return new String(source.getID());
    }

    /**
     * 获取邮件文本
     * @return 返回文本内容
     */
    public String getText() {
        return new String(text);
    }
}
