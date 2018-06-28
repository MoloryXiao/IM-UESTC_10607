package network.commonClass;

/**
 * 描述：仅用于请求添加群的结果
 *
 * @author ZiQin
 * @version 1.0.0
 */
public class AddGroupResult {
	/**
	 * 希望添加群的用户id
	 */
	private String uid;
	
	/**
	 * 希望添加的群
	 */
	private String gid;
	
	/**
	 * 群主的想法：同意与否
	 */
	private boolean accept;
	
	public AddGroupResult( String gid, String uid, boolean accept ) {
		
		this.gid = gid;
		this.uid = uid;
		this.accept = accept;
	}
	
	public String getGid() {
		
		return gid;
	}
	
	public void setGid( String gid ) {
		
		this.gid = gid;
	}
	
	public String getUid() {
		
		return uid;
	}
	
	public void setUid( String uid ) {
		
		this.uid = uid;
	}
	
	public boolean isAccept() {
		
		return accept;
	}
	
	public void setAccept( boolean accept ) {
		
		this.accept = accept;
	}
}
