package network.commonClass;

import java.util.ArrayList;

/**
 * 描述：群组类
 * @author ZiQin
 * @version 1.0.0
 */
public class Group {

    private String id;

    private String name;

    private String description;

    private Account owner;

    private ArrayList<Account> member;

    private String message;

    public Group() {
        id = null;
        name = null;
        description = null;
        member = null;
        message = null;
        owner = null;
    }

    public Group(String id, String name, String description, String message) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.message = message;
        this.member = null;
    }

    public Group(String id, String name, String description, Account owner, ArrayList<Account> member, String message) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.member = member;
        this.message = message;
    }

    public String getId() {
        return (id == null) ? null : new String(id);
    }

    public String getName() {
        return (name == null) ? null : new String(name);
    }

    public String getDescription() {
        return (description == null) ? null : new String(description);
    }

    public String getMessage() {
        return (message == null) ? null : new String(message);
    }

    public ArrayList<Account> getMember() {
        return (member == null) ? null : new ArrayList<Account>(member);
    }

    public Account getOwner() {
        return owner;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMember(String message) {
        this.message = message;
    }

    public void setMember(ArrayList<Account> member) {
        this.member = new ArrayList<Account>(member);
    }

    public void addMemeber(ArrayList<Account> person) {
        for (int i = 0; i < person.size(); i++) {
            if (!isExist(person.get(i)))
                member.add(person.get(i));
        }
    }

    private boolean isExist(Account account) {
        for (int i = 0; i < this.member.size(); i++) {
            if (member.get(i).getId() == account.getId()) {
                return true;
            }
        }
        return false;
    }
}