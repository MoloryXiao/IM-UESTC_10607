package Network.Server;

/**
 * Created by ZiQin on 2018/3/25.
 */
public class Envelope {
    private AccountBase target;

    private AccountBase source;

    private String text;

    public Envelope(String tar, String src, String txt) {
        target = new AccountBase(tar);
        source = new AccountBase(src);
        text = new String(txt);
    }

    public Envelope(Envelope other) {
        target = new AccountBase(other.target);
        source = new AccountBase(other.source);
        text = new String(other.text);
    }

    public String getTargetAccountId() {
        return new String(target.getID());
    }

    public String getSourceAccountId() {
        return new String(source.getID());
    }

    public String getText() {
        return new String(text);
    }
}
