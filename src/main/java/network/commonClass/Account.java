package network.commonClass;

import java.io.IOException;

/**
 * 描述：Account类是用户信息的封装，继承AccountBase类
 *
 * @author 土豆
 * @version 1.2.0
 */
public class Account extends AccountBase {
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
	
	/**
	 * 描述：构造函数
	 */
	public Account() {
		
		super();
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
	
	/**
	 * 描述：构造函数
	 *
	 * @param id        用户账号
	 * @param name      昵称
	 * @param online    是否在线
	 * @param signature 个性签名
	 */
	public Account( String id, String name, boolean online, String signature ) {
		
		super(id);
		this.name = new String(name);
		this.online = online;
		this.signature = new String(signature);
//		以下代码用于临时测试，以后删除
//		try {
//			this.picture = new Picture(Account.class.getClassLoader().getResource("default.jpg").getPath());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		picture = null;
		//	this.picture = null;
		
		stage = 0;
		old = 0;
		sex = false;
		home = null;
		mobliePhone = null;
		mail = null;
	}
	
	/**
	 * 描述：构造函数
	 */
	public Account( String id, String name, boolean online, String signature, byte[] picture ) {
		
		super(id);
		this.name = new String(name);
		this.online = online;
		this.signature = new String(signature);
		this.picture = new Picture(picture);
		
		stage = 0;
		old = 0;
		sex = false;
		home = null;
		mobliePhone = null;
		mail = null;
	}
	
	/**
	 * 描述：构造函数
	 */
	public Account( String id, String name, boolean online, String signature, Picture picture ) {
		
		super(id);
		this.name = new String(name);
		this.online = online;
		this.signature = new String(signature);
		this.picture = picture;
		
		stage = 0;
		old = 0;
		sex = false;
		home = null;
		mobliePhone = null;
		mail = null;
	}
	
	/**
	 * 描述：构造函数
	 *
	 * @param id
	 * @param name
	 * @param mobliePhone
	 * @param mail
	 * @param stage
	 * @param old
	 * @param sex
	 * @param home
	 * @param signature
	 * @param isOnline
	 * @param picture
	 */
	public Account( String id,
	                String name,
	                String mobliePhone,
	                String mail,
	                byte stage,
	                int old,
	                boolean sex,
	                String home,
	                String signature,
	                boolean isOnline,
	                Picture picture ) {
		
		super.setId(id);
		this.name = name;
		this.mobliePhone = mobliePhone;
		this.mail = mail;
		this.stage = stage;
		this.old = old;
		this.sex = sex;
		this.home = home;
		this.signature = signature;
		this.online = isOnline;
		this.picture = picture;
	}
	
	/**
	 * 描述：获取ID
	 *
	 * @return 返回ID号
	 */
	public String getId() {
		
		return new String(super.id);
	}
	
	/**
	 * 描述：设置昵称
	 *
	 * @param nName 新昵称
	 */
	public void setNickName( String nName ) {
		
		name = nName;
	}
	
	/**
	 * 描述：获取昵称
	 *
	 * @return name 昵称
	 */
	public String getNikeName() {
		
		return new String(name);
	}
	
	/**
	 * 描述：获取个性签名
	 *
	 * @return signature 个性签名
	 */
	public String getSignature() {
		
		return new String(signature);
	}
	
	/**
	 * 描述：设置个性签名
	 *
	 * @param sign 个性签名
	 */
	public void setSignature( String sign ) {
		
		signature = sign;
	}
	
	/**
	 * 描述：获取是否在线
	 *
	 * @return 状态
	 */
	public boolean getOnline() {
		
		return online;
	}
	
	/**
	 * 描述：设置是否在线
	 *
	 * @param online 当前状态
	 */
	public void setOnline( boolean online ) {
		
		this.online = online;
	}
	
	/**
	 * 描述：获取用户头像
	 *
	 * @return 头像图片字节流
	 */
	public Picture getPicture() {
		
		return this.picture;
	}
	
	/**
	 * 描述：设置用户头像
	 */
	public void setPicture( byte[] pictureStream ) {
		
		this.picture = new Picture(pictureStream);
	}
	
	/**
	 * 描述：获取手机号码
	 *
	 * @return 手机号码
	 */
	public String getMobliePhone() {
		
		return mobliePhone;
	}
	
	/**
	 * 描述：设置手机号码
	 *
	 * @param mobliePhone 手机号码
	 */
	public void setMobliePhone( String mobliePhone ) {
		
		this.mobliePhone = mobliePhone;
	}
	
	/**
	 * 描述：获取邮箱
	 *
	 * @return 邮箱地址
	 */
	public String getMail() {
		
		return mail;
	}
	
	/**
	 * 设置邮箱
	 *
	 * @param mail 邮箱地址
	 */
	public void setMail( String mail ) {
		
		this.mail = mail;
	}
	
	/**
	 * 描述：获取等级
	 *
	 * @return 等级
	 */
	public byte getStage() {
		
		return stage;
	}
	
	/**
	 * 描述：设置等级
	 *
	 * @param stage 等级
	 */
	public void setStage( byte stage ) {
		
		this.stage = stage;
	}
	
	/**
	 * 获取年龄
	 *
	 * @return 年龄
	 */
	public int getOld() {
		
		return old;
	}
	
	/**
	 * 描述：设置年龄
	 *
	 * @param old 年龄
	 */
	public void setOld( int old ) {
		
		this.old = old;
	}
	
	/**
	 * 是否男
	 *
	 * @return 是或否
	 */
	public boolean isMale() {
		
		return sex;
	}
	
	/**
	 * 描述：设置性别
	 *
	 * @param isMale 是男？
	 */
	public void setSex( boolean isMale ) {
		
		this.sex = isMale;
	}
	
	/**
	 * 获取归属地
	 *
	 * @return 归属地
	 */
	public String getHome() {
		
		return home;
	}
	
	/**
	 * 设置归属地
	 *
	 * @param home 归属地
	 */
	public void setHome( String home ) {
		
		this.home = home;
	}
}
