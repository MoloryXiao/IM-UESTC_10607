package network.Builder;

import network.commonClass.Account;
import network.commonClass.Picture;

/**
 * 描述：AccounteBuilder
 * @author ZiQin
 * @version 1.0.1
 */
public class AccountBuilder {
    /**
     * 描述：用户账号
     */
    protected String id;
    /**
     * 描述：用户的昵称
     */
    protected String name;
    /**
     * 描述：用户是否在线
     */
    protected boolean online;
    /**
     * 描述：用户的个性签名
     */
    protected String signature;
    /**
     * 描述：用户头像
     */
    protected Picture picture;
    /**
     * 描述：手机号码
     */
    protected String mobliePhone;
    /**
     * 描述：个人邮箱
     */
    protected String mail;
    /**
     * 描述：等级
     */
    protected byte stage;
    /**
     * 描述：年龄
     */
    protected int old;
    /**
     * 描述：性别
     */
    protected boolean sex;
    /**
     * 描述：归属地
     */
    protected String home;

    public AccountBuilder() {
        id = null;
        name = new String();
        online = false;
        signature = new String();
        picture = null;
        stage = 0;
        old = 0;
        sex = false;
        home = null;
        mobliePhone = null;
        mail = null;
    }

    public AccountBuilder(String id, String name, String signature) {
        this.id = id;
        this.name = name;
        this.signature = signature;
    }

    public AccountBuilder online(boolean isOnline) {
        this.online = isOnline;
        return this;
    }

    public AccountBuilder id(String id) {
        this.id = id;
        return this;
    }

    public AccountBuilder name(String name) {
        this.name = name;
        return this;
    }

    public AccountBuilder signature(String signature) {
        this.signature = signature;
        return this;
    }

    public AccountBuilder picture(Picture picture) {
        this.picture = picture;
        return this;
    }

    public AccountBuilder mobilePhone(String mobliePhone) {
        this.mobliePhone = mobliePhone;
        return this;
    }

    public AccountBuilder mail(String mail) {
        this.mail = mail;
        return this;
    }

    public AccountBuilder stage(byte stage) {
        this.stage = stage;
        return this;
    }

    public AccountBuilder old(int old) {
        this.old = old;
        return this;
    }

    public AccountBuilder sex(boolean male) {
        this.sex = male;
        return this;
    }

    public AccountBuilder home(String home) {
        this.home = home;
        return this;
    }

    public Account createAccount() {
        return new Account(id, name, mobliePhone, mail, stage, old, sex, home, signature, online, picture);
    }
}
