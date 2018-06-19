package network.Builder;

import network.commonClass.Account;
import network.commonClass.Group;

import java.util.ArrayList;

/**
 * 描述：GroupBuilder
 * @author ZiQin
 * @version 1.0.0
 */
public class GroupBuilder {

    private String id;

    private String name;

    private String description;

    private Account owner;

    private ArrayList<Account> member;

    private String message;

    public GroupBuilder() {
        id = null;
        name = null;
        description = null;
        owner = null;
        member = null;
        message = null;
    }

    public GroupBuilder id(String id) {
        this.id = id;
        return this;
    }

    public GroupBuilder name(String name) {
        this.name = name;
        return this;
    }

    public GroupBuilder description(String description) {
        this.description = description;
        return this;
    }

    public GroupBuilder owner(Account account) {
        this.owner = new Account(account);
        return this;
    }

    public GroupBuilder member(ArrayList<Account> member) {
        this.member = new ArrayList<Account>(member);
        return this;
    }

    public GroupBuilder message(String message) {
        this.message = message;
        return this;
    }

    public Group createGroup() {
        return new Group(id, name, description, owner, member, message);
    }
}