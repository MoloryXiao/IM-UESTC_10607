package network.commonClass;

/**
 * 描述：删除好友结果消息类
 * @author ZiQin
 * @version 1.0.0
 */
public class DelGroupResult {
    /**
     * 被删除的群id
     */
    private String gid;
    /**
     * 删除结果
     */
    private boolean delResult;

    public DelGroupResult(String gid, boolean delResult) {
        this.gid = gid;
        this.delResult = delResult;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public boolean getDelResult() {
        return delResult;
    }

    public void setDelResult(boolean delResult) {
        this.delResult = delResult;
    }
}
