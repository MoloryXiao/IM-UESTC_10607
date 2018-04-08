package Core;

import javax.swing.UIManager;
public class ChatTool {
	private static boolean plugin_Flag;
	private static LoginWindow login_wind;
	private static FriendsListWindow fd_wind;
	
	public static void main(String []args){
		@SuppressWarnings("unused")
		ChatTool ct = new ChatTool();
	}
	public ChatTool(){
		plugin_Flag = true;
		try
	    {
			if(plugin_Flag){
				org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
				UIManager.put("RootPane.setupButtonVisible", false);
				UIManager.put("TabbedPane.tabAreaInsets", new javax.swing.plaf.InsetsUIResource(3,5,2,20));
			}
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
		login_wind = new LoginWindow();
		while(!login_wind.getEnterFlag()){}
		System.out.println("成功登陆！正在拉取好友列表...");
		fd_wind = new FriendsListWindow();
	}
}
