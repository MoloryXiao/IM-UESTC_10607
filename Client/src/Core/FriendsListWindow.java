package Core;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.*;

import network.commonClass.Account;
import network.commonClass.Envelope;
import network.commonClass.Group;
import network.commonClass.Picture;
import network.messageOperate.MessageOperate;
/**
 * 好友列表窗口
 * @author Murrey
 * @version 3.2
 * 【修改】请求个人信息的函数
 * 【注销】群组和其他选项卡
 * 【优化】更新个人信息的函数
 * 【优化】显示头像时读取的图片内容
 * @version 3.1
 * 【优化】个人信息的显示样式
 * @version 3.0
 * 【删减】部分冗余的成员变量
 * 【修改】构造函数
 * 【添加】消息收发队列方法
 * 【添加】更新个人信息的方法
 */
public class FriendsListWindow extends JFrame{	
	private static final int 	i_window_width = 320,i_window_height = 700;
	private int 				i_loc_X,i_loc_Y;
	
	private Account 			account_mine;
	
	private int 				i_friends_sum;
	private int 				i_groups_sum;	//add
	
	private int 				i_online_count;
	private String 				new_friend_id;			//新的好友请求ID
	
	private String 				new_add_group_friend_id;	//新的加入群组用户ID
	private String				new_add_group_id;			//新的加入群组ID
	
	private JScrollPane 		scroll_friends_list;
	private JScrollPane			scroll_groups_list;
	private JPanel 				panel_top,panel_middle,panel_bottom,
								panel_middle_friends,panel_middle_groups,panel_middle_else;
	private JLabel 				label_head_image,label_name,label_sign;
	private JTabbedPane 		tabbed_pane;
	private JList<String> 		jList_str_friendsName;
	private JList<String>		jList_str_groupsName;		//add
	private JButton				btn_manage_friend,btn_logout,btn_logoff,
								btn_new_friend_request,btn_new_group_friend_request;
	
	private ArrayList<Account> 	arrayList_account_friends;
	private ArrayList<Group>	arrayList_account_groups;		
	private ArrayList<Account>  arrayList_account_group_member;
	
	private Account 			account_newWindow;
	private Group				group_newWindow;
	
	private String				createdGroupID;
	private String				createdGroupSignature;
	
	/**
	 * FriendsListWindow 构造函数
	 */
	public FriendsListWindow(){				
		/* 设置窗口基本信息 */
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);					// 设置关闭键操作
		this.setIconImage((new ImageIcon("./Client/image/chick.png")).getImage());		// 设置图标
		setWindowSize();			// 设置窗口尺寸
		setWindowRight();			// 设置窗口位于屏幕右侧
		this.setResizable(false);	// 是否可拖拽
		this.setTitle("KIM");		// 窗口名称
		
		topPaneSet(); 
		middlePaneSet(); 
		bottomPaneSet();
		
		this.setAlwaysOnTop(true);	// 是否置顶
		this.setVisible(true);		// 是否可视化
		
//		RecvSendController.addToSendQueue(MessageOperate.packageAskMyselfInfoMsg()); // 请求个人信息
		
		RecvSendController.addToSendQueue(MessageOperate.packageAskUserDetail());	// 请求个人信息
				
		RecvSendController.addToSendQueue(MessageOperate.packageAskFriendListMsg()); // 请求好友列表
		
		RecvSendController.addToSendQueue(MessageOperate.packageAskGetGroupList());  // 请求群组列表
	}
	
	/**
	 * 顶部Pane设置
	 */	
	private void topPaneSet() {
		/* top面板各组件设置 */
		panel_top = new JPanel();
		panel_top.setLayout(null);		
		panel_top.setPreferredSize(new Dimension(i_window_height,90));
		
		label_head_image = new JLabel();	// 头像图标
		//label_head_image.setIcon((new ImageIcon("image/p70_piano.jpg"))); for debugging/old version.
		label_head_image.setToolTipText("123木头人");
		label_head_image.setBounds(10, 10, 70, 70);
		label_head_image.setCursor(new Cursor(Cursor.HAND_CURSOR));
		label_head_image.addMouseListener(new MouseListener() {		// 点击头像开启信息展示功能
			public void mouseClicked(MouseEvent e) {
				System.out.println("Info: Acquire the Mine-Info window.");
				WindowProducer.addWindowRequest(WindowProducer.INFO_MINE_WIND);
			}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
		});
		
		label_name = new JLabel("昵称：等待服务器应答...");
//		label_name.setFont(new Font("宋体", Font.PLAIN, 13));
		label_name.setBounds(90,20,165,20);
		
		label_sign = new JLabel("个性签名：等待服务器应答...");
		label_sign.setBounds(90,50,165,20);		
		
		panel_top.add(label_head_image);
		panel_top.add(label_name);
		panel_top.add(label_sign);
		
		/* 将面板添加到容器中 */
		this.add(panel_top,BorderLayout.NORTH);
	}
	
	/**
	 * 中部Pane设置
	 */
	private void middlePaneSet(){
		/* 组件初始化 */
		tabbed_pane = new JTabbedPane();
		panel_middle = new JPanel();
		
		panel_middle_friends = new JPanel();
		panel_middle_groups = new JPanel();
		panel_middle_else = new JPanel();
		
//		---------------------   好友列表 ------------------------------
		jList_str_friendsName = new JList<String>();
		jList_str_friendsName.setListData(new String[]{"正在拉取好友列表..."});	
		jList_str_friendsName.setPreferredSize(new Dimension(i_window_width-60, (int)(i_window_height*0.7)));
		jList_str_friendsName.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if(jList_str_friendsName.getSelectedIndex() != -1) {
					if(e.getClickCount() == 2){
						setNewWindowResource(arrayList_account_friends.get(jList_str_friendsName.getSelectedIndex()));
						WindowProducer.addWindowRequest(WindowProducer.CHAT_WIND);
					}
				}
			}
		});	
		scroll_friends_list = new JScrollPane(jList_str_friendsName);
		scroll_friends_list.setPreferredSize(new Dimension(i_window_width-60,440));	
		scroll_friends_list.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll_friends_list.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		
//		---------------------   群组列表 ------------------------------
		jList_str_groupsName  = new JList<String>();
		jList_str_groupsName.setListData(new String[]{"正在拉取群组列表..."});	
		jList_str_groupsName.setPreferredSize(new Dimension(i_window_width-60, (int)(i_window_height*0.7)));
		jList_str_groupsName.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if(jList_str_groupsName.getSelectedIndex() != -1) {
					if(e.getClickCount() == 2){
						setNewGroupWindowResource(arrayList_account_groups.get(jList_str_groupsName.getSelectedIndex()));
						
						WindowProducer.addWindowRequest(WindowProducer.GROUP_CHAT_WIND);				
						
						RecvSendController.addToSendQueue(MessageOperate.packageUpdateGroup(arrayList_account_groups.
								get(jList_str_groupsName.getSelectedIndex()).
								getId()		//双击打开的同时，发送信息给服务器，获取群成员
								));
					}
				}
			}
		});	
		scroll_groups_list = new JScrollPane(jList_str_groupsName);
		scroll_groups_list.setPreferredSize(new Dimension(i_window_width-60,440));	
		scroll_groups_list.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll_groups_list.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		
		/* Panel面板设置 */
		//panel_middle_groups.setBackground(Color.green);
		panel_middle_else.setBackground(Color.red);
		
		/* Panel面板添加 */
		panel_middle.add(tabbed_pane);
		
		tabbed_pane.add("好友",panel_middle_friends);
		tabbed_pane.add("群组",panel_middle_groups);
//		tabbed_pane.add("其他",panel_middle_else);
		
		panel_middle_friends.add(scroll_friends_list);
		panel_middle_groups.add(scroll_groups_list);
		
		/* 添加到容器  */
		this.add(panel_middle,BorderLayout.CENTER);
	}
	
	/**
	 * 底层Pane设置
	 */
	private void bottomPaneSet(){
		panel_bottom = new JPanel();
		btn_logout = new JButton("退出");
		btn_logout.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});		
		
		btn_manage_friend = new JButton("管理好友");
		btn_manage_friend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WindowProducer.addWindowRequest(WindowProducer.ADD_FRIEND_WIND);
			}
		});
		
		btn_new_friend_request = new JButton("新的好友请求");
		btn_new_friend_request.setVisible(false);
		
		btn_new_friend_request.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int option = JOptionPane.showConfirmDialog(null, 
													"来自:" + new_friend_id + "的好友请求，是否同意？", "新的好友请求", 
													JOptionPane.YES_NO_OPTION);
				Envelope env = new Envelope(new_friend_id, account_mine.getId(), "");
				if(option == JOptionPane.YES_OPTION) {
					RecvSendController.addToSendQueue(MessageOperate.packageAddFriendResultMsg(env, true));
					RecvSendController.addToSendQueue(MessageOperate.packageAskFriendListMsg());
				}
				else {
					RecvSendController.addToSendQueue(MessageOperate.packageAddFriendResultMsg(env, false));
				}
				btn_new_friend_request.setVisible(false);
			}
		});
		
		btn_new_group_friend_request = new JButton("新的群组请求");
		btn_new_group_friend_request.setVisible(false);
		
		btn_new_group_friend_request.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int option = JOptionPane.showConfirmDialog(null, 
													"来自:" + new_add_group_friend_id + "的群组添加请求，是否同意？", "新的群组请求", 
													JOptionPane.YES_NO_OPTION);
				if(option == JOptionPane.YES_OPTION) {
					System.out.println("ask group list 123");
					RecvSendController.addToSendQueue(MessageOperate.packageAddGroupRes( new_add_group_id , new_add_group_friend_id, true));
					RecvSendController.addToSendQueue(MessageOperate.packageAskGetGroupList());
					System.out.println("ask group list");
				}
				else {
					RecvSendController.addToSendQueue(MessageOperate.packageAddGroupRes( new_add_group_id , new_add_group_friend_id, false));
				}
				btn_new_group_friend_request.setVisible(false);
			}
		});
		
		FlowLayout flowLayout_Bottom = new FlowLayout();
		flowLayout_Bottom.setAlignment(FlowLayout.RIGHT);	// 居右
		panel_bottom.setLayout(flowLayout_Bottom);
		
		panel_bottom.add(btn_new_friend_request);
		panel_bottom.add(btn_new_group_friend_request);
		
		panel_bottom.add(btn_manage_friend);
		panel_bottom.add(btn_logout);
		this.add(panel_bottom,BorderLayout.SOUTH);
	}
	/*
	 * 设置新的好友添加请求可见状态
	 */
	public void setNewFriendRequesttBottonVisible(Boolean bool) {
		if(bool == true) {
			btn_new_friend_request.setVisible(true);
		}
		else {
			btn_new_friend_request.setVisible(false);
		}
	}
	
	/*
	 * 设置新的好友添加请求可见状态
	 */
	public void setNewGroupRequesttBottonVisible(Boolean bool) {
		if(bool == true) {
			btn_new_group_friend_request.setVisible(true);
		}
		else {
			btn_new_group_friend_request.setVisible(false);
		}
	}
	/**
	 * 设置窗口尺寸
	 */
	private void setWindowSize(){
//		i_window_height = (screenSize.height/3*2);		// 取2/3倍屏幕高度
//		i_window_width = (screenSize.width/3/3*1.5);
		this.setSize(i_window_width,i_window_height);
	}
	
	/**
	 * 设置窗口位置
	 */
	private void setWindowRight(){
		/* 获取屏幕相关信息 */
		Toolkit tool_kit=Toolkit.getDefaultToolkit();
		Dimension screenSize=tool_kit.getScreenSize();
		
		int i_screen_width = screenSize.width;
		int i_screen_height = screenSize.height;
		i_loc_X = (i_screen_width/3*2)+(i_screen_width/3 - i_window_width)/2;	// 位于屏幕右侧
		i_loc_Y = (i_screen_height - i_window_height)/2;
		this.setLocation(i_loc_X,i_loc_Y);
	}
	
	/**
	 * 根据传入好友信息数组 设置到自身成员属性
	 * 并内部进行在线状态的排序 最后将排序后的结果予以显示
	 * @param inArrList 从服务器获取到的好友信息数组
	 */
	public void updateFriendsList(ArrayList<Account> inArrList){
		setFriendsList(inArrList);			// 将传进来的好友列表设置到成员属性中
		sortFriendsListByOnline();			// 对成员属性进行排序 依据在线情况
		this.i_online_count = countOnlineNums();	// 计算在线人数
		friendsListShow();		// 在窗口中显示最新的好友列表
	}
	
	/**
	 * 根据传入群组信息数组 设置到自身成员属性
	 * @param inArrList 从服务器获取到的群组信息数组
	 */
	public void updateGroupsList(ArrayList<Group> inArrList ){						//修改为群类型
		setGroupsList(inArrList);			// 将传进来的群组列表设置到成员属性中
		groupsListShow();					// 在窗口中显示最新的群组列表
	}
	
	
	/**
	 * 更新用户信息-成员属性
	 * @param myselfAccount 用户信息
	 */
	public void updateMineInfo(Account myselfAccount) {		
		this.account_mine = myselfAccount.clone();
		
		/* 对窗口相关标签进行更新 */
		label_name.setText("昵称："+this.account_mine.getNikeName());	// 昵称
		label_sign.setText("个性签名："+this.account_mine.getSignature());	// 个性签名
		Picture temp_pic = new Picture();
		temp_pic = this.account_mine.getPicture().clone();
		temp_pic.reduceImage(70, 70);
		temp_pic.reduceImageToCircle();
		label_head_image.setIcon(new ImageIcon(temp_pic.getPictureBytes()));
		label_head_image.setToolTipText(this.account_mine.getNikeName());
		
		label_name.setToolTipText("昵称："+this.account_mine.getNikeName());
		label_sign.setToolTipText("个性签名："+this.account_mine.getSignature());
	}
	
	/**
	 * 将在线好友置顶
	 * @param inArrList 好友列表
	 * @return 排好序的好友列表
	 */
	private void sortFriendsListByOnline(){
		Comparator<Account> cmptor = new Comparator<Account>(){
			public int compare(Account a, Account b) {
				int status_A = a.getOnline()?1:-1;
				int status_B = b.getOnline()?1:-1;
				if(status_A <= status_B) return 1;
				else return -1;
			}
		};
		Collections.sort(arrayList_account_friends,cmptor);	// 根据在线状态对好友列表进行排序
	}
	
	/**
	 * 将好友信息列表设置到JList组件中 予以展示
	 */
	public void friendsListShow(){		
		Vector<String> vec_str_friendsName = new Vector<String>();
		this.i_friends_sum =  arrayList_account_friends.size();
		for(int i=0;i<this.i_friends_sum;i++) {
			Account friendAcc = new Account();
			friendAcc = arrayList_account_friends.get(i);
			int userNotReadNum = EnvelopeRepertory.getUserNotReadMessNum(friendAcc.getId());
			if(userNotReadNum > 0)
				vec_str_friendsName.add(friendAcc.getNikeName() + "(" + userNotReadNum + ")" );
			else
				vec_str_friendsName.add(friendAcc.getNikeName());
		}
			
		
		/* JList列表设置 */
		jList_str_friendsName.setListData(vec_str_friendsName);
		FriendsListCellRenderer flcr = new FriendsListCellRenderer(this.i_online_count,Color.RED);
		jList_str_friendsName.setCellRenderer(flcr);	// 应用自定义的列表样式显示器
		if(this.i_friends_sum >= 15)	// 对滚动条进行设置
			scroll_friends_list.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		else
			scroll_friends_list.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
	}
	
	/**
	 * 将群组信息列表设置到JList组件中 予以展示
	 */
	public void groupsListShow(){		
		Vector<String> vec_str_groupsName = new Vector<String>();
		this.i_groups_sum =  arrayList_account_groups.size();
		for(int i=0;i<this.i_groups_sum;i++) {
			Group groupInfo = new Group();				//修改为群类型 √
			groupInfo = arrayList_account_groups.get(i);
			int userNotReadNum = GroupEnvelopeRepertory.getUserNotReadMessNum(groupInfo.getId());
			if(userNotReadNum > 0)
				vec_str_groupsName.add(groupInfo.getName() + "(" + userNotReadNum + ")" );
			else
				vec_str_groupsName.add(groupInfo.getName());
		}
		
//		System.out.println("number:"+i_groups_sum);
//		for(String o : vec_str_groupsName) {
//			System.out.println(o);
//		}
		
		/* JList列表设置 */
		jList_str_groupsName.setListData(vec_str_groupsName);
		if(this.i_friends_sum >= 15)	// 对滚动条进行设置
			scroll_friends_list.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		else
			scroll_friends_list.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

	}
	/**
	 * 计数在线个数
	 * 前提：必须调用排序算法
	 * @param inArrList
	 * @return count 在线人数
	 */
	private int countOnlineNums(){
		int count = 0;
		for(int i=0;i<arrayList_account_friends.size();i++){
			if(arrayList_account_friends.get(i).getOnline()) count++;
			else break;
		}
		return count;
	}
	
	/**
	 * 调试用
	 * 打印当前好友列表信息
	 */
	public void printFriendsArrList(){
		for(int i=0;i<arrayList_account_friends.size();i++){
			System.out.println("ListInfo: Get the friend "+(i+1)+" - "+arrayList_account_friends.get(i).getNikeName());
			System.out.println("ListInfo: Get the friend "+(i+1)+" - "+arrayList_account_friends.get(i).getSignature());
			System.out.println("ListInfo: Get the friend "+(i+1)+" - "+arrayList_account_friends.get(i).getId());
			System.out.println("ListInfo: Get the friend "+(i+1)+" - "+arrayList_account_friends.get(i).getOnline());
		}
	}

	/**************** setter and getter ****************/	
	public void setFriendsList(ArrayList<Account> inArrList){
		this.arrayList_account_friends = new ArrayList<Account>(inArrList);
	}
	
	public void setGroupsList(ArrayList<Group> inArrList){		
		this.arrayList_account_groups = new ArrayList<Group>(inArrList);
	}
	
	public String getGroupID() {
		return this.createdGroupID;
	}
	
	public String getGroupSinature() {
		return this.createdGroupSignature;
	}
	
	public void setNewWindowResource(Account friend_account){
		this.account_newWindow = new Account();
		this.account_newWindow = friend_account;
	}
	
	private void setNewGroupWindowResource(Group group_account) {
		this.group_newWindow = new Group();
		this.group_newWindow = group_account;
		
		System.out.println(group_account.getId()+" " +group_account.getName());
		ArrayList<Account> test = new ArrayList<>();
		test = group_account.getMember();
		if(null == test) System.out.println("member null");
//		for(int i=0;i<test.size();i++) {
//			System.out.println(test.get(i).getNikeName());
//		}
	}
	
	public Account getNewWindowResource(){
		return this.account_newWindow;
	}
	public Group getNewChatWindowResource() {
		return this.group_newWindow;
	}
	
	public Account getMineAccount() {
		return this.account_mine;
	}
	public void setNewFriendID(String str) {
		new_friend_id = str;
	}
	
	public void setNewAddGroupFriendID(String gid , Account user) {
		
		new_add_group_id = gid;
		
		new_add_group_friend_id = user.getId();
	}
	
	public void addFriendSuccessHint() {
		JOptionPane.showMessageDialog(null, "添加好友成功", "提示", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public void addFriendFailureHing() {
		JOptionPane.showMessageDialog(null, "添加好友失败", "提示", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public void addGroupSuccessHint() {
		JOptionPane.showMessageDialog(null, "添加群组成功", "提示", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public void addGroupFailureHing() {
		JOptionPane.showMessageDialog(null, "添加群组失败", "提示", JOptionPane.INFORMATION_MESSAGE);
	}	
	
//	public Vector<String> getFriendList(){		
//		Vector<String> vec_str_friendsName = new Vector<String>();
//		int friend_number =  arrayList_account_friends.size();
//		for(int i = 0 ; i < friend_number ; i++)
//			vec_str_friendsName.add( arrayList_account_friends.get(i).getId() + "     " +
//									 arrayList_account_friends.get(i).getNikeName() + "     " +
//									 arrayList_account_friends.get(i).getSignature());
//		return vec_str_friendsName;
//	}
	
	public ArrayList<Account> getFriendsList(){
		ArrayList<Account> accounts = new ArrayList<>();
		accounts = arrayList_account_friends;
		return accounts;
	}
	public ArrayList<Group> getGroupList(){		
		ArrayList<Group> groups = new ArrayList<>();
		groups = arrayList_account_groups;
		return groups;
		
	}

}