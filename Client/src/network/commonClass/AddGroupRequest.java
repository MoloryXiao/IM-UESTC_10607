package network.commonClass;

/**
 * 描述：添加群请求
 * @author ZiQin
 * @version 1.0.0
 */
public class AddGroupRequest {
    /**
     * 希望添加的群id
     */
    private String targetGroupId;

    /**
     * 希望进群的用户
     */
    private Account wantToAddGroupUser;

    public AddGroupRequest(String targetGroupId, Account wantToAddGroupUser) {
        this.targetGroupId = new String(targetGroupId);
        this.wantToAddGroupUser = new Account(wantToAddGroupUser);
    }

    public Account getWantToAddGroupUser() {
        return wantToAddGroupUser;
    }

    public void setWantToAddGroupUser(Account wantToAddGroupUser) {
        this.wantToAddGroupUser = wantToAddGroupUser;
    }

    public String getTargetGroupId() {
        return targetGroupId;
    }

    public void setTargetGroupId(String targetGroupId) {
        this.targetGroupId = targetGroupId;
    }


}
