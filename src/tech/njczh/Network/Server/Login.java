package tech.njczh.Network.Server;

/**
 * Created by ZiQin on 2018/3/25.
 */
public class Login {
    private AccountBase account;

    private String password;
    
    public Login() {
    }

    public Login(AccountBase ac, String passwd) {
        account = new AccountBase(ac);
        password = new String(passwd);
    }

    public Login(String id, String passwd) {
        account = new AccountBase(id);
        password = new String(passwd);
    }

    public String getAccountId() {
        return new String(account.getID());
    }

    public String getPassword() {
        return new String(password);
    }
}
