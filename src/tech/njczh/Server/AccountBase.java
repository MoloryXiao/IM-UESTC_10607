package tech.njczh.Server;

/**
 * Created by ZiQin on 2018/3/25.
 */
public class AccountBase {
    protected String ID;

    public AccountBase() {
        ID = new String();
    }

    public AccountBase(String Id) {
        ID = new String(Id);
    }

    public AccountBase(AccountBase other) {
        ID = new String(other.ID);
    }

    public String getID() {
        return new String(ID);
    }
}
