package Windows;

public class LoginInfo{
	private boolean 			flag_rememberBtn;
	private boolean 			flag_autoBtn;
	private String 				str_login_yhm;
	private String				str_login_psw;
	
	public LoginInfo() {
		this.setLoginYhm(null);
		this.setLoginPsw(null);
		this.setAutoBtn(false);
		this.setRememberBtn(false);
	}

	public boolean isRememberBtn() {
		return flag_rememberBtn;
	}

	public void setRememberBtn(boolean flag_rememberBtn) {
		this.flag_rememberBtn = flag_rememberBtn;
	}

	public boolean isAutoBtn() {
		return flag_autoBtn;
	}

	public void setAutoBtn(boolean flag_autoBtn) {
		this.flag_autoBtn = flag_autoBtn;
	}

	public String getLoginYhm() {
		return str_login_yhm;
	}

	public void setLoginYhm(String str_login_yhm) {
		this.str_login_yhm = str_login_yhm;
	}

	public String getLoginPsw() {
		return str_login_psw;
	}

	public void setLoginPsw(String str_login_psw) {
		this.str_login_psw = str_login_psw;
	}
}