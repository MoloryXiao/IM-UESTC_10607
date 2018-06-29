package Core;

//import java.awt.BorderLayout;
//import java.awt.Dimension;
//import javax.swing.JButton;
//import javax.swing.JFrame;
//import javax.swing.JPanel;
//import javax.swing.JTabbedPane;
//
//import java.awt.BorderLayout;
//import java.awt.Color;
//import java.awt.Dimension;
//import java.awt.event.ActionListener;
//import java.awt.event.FocusEvent;
//import java.awt.event.FocusListener;
//
//import javax.swing.JButton;
//import javax.swing.JPanel;
//import javax.swing.JTextArea;

import java.awt.*;
import javax.swing.*;

import network.commonClass.Account;
import network.commonClass.Group;
import network.messageOperate.MessageOperate;

import java.awt.event.*;
import java.util.ArrayList;
import java.util.Vector;

/**
 * 添加好友窗口
 * @author LeeKadima
 * @version 1.1
 * 【注销】查询群组功能
 * @version 1.0
 * 【实现】添加好友功能基本实现
 * @version 
 */

public class AddFriendWindow extends JFrame {
	private static final String ADD_FRIEND_WINDOW_TITLE = "添加好友";
	private static final int ADD_FRIEND_WINDOW_WIDTH = 850;
	private static final int ADD_FRIEND_WINDOW_HEIGHT = 550;
	
	private JTabbedPane		option_tabbed;
	private JPanel			search_friend_option,delete_friend_option,
							search_group_option,delete_group_option,
							create_group_option;
	private JPanel 			center_panel;
	private search_panel	search_friend_panel,delete_friend_panel,search_group_panel,delete_group_panel;

	private String			mine_id;
	Vector<String> 			friend_info;
	
	private JTextField		create_group_name_text;
	private JTextField		create_group_description_text;
	private JLabel			cue_create_group_name;
	private JLabel			cue_create_group_description;
	private JButton			create_group_button;
	
	public AddFriendWindow(String login_id) {
		
		this.setTitle(ADD_FRIEND_WINDOW_TITLE);		
		this.setSize(ADD_FRIEND_WINDOW_WIDTH, ADD_FRIEND_WINDOW_HEIGHT);
		
		initID(login_id);
		setCenterPane();
		
		this.setResizable(false);										//窗口大小不变
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);								//窗口剧中
		this.setVisible(true);
		this.requestFocus();
	}
	
	private void initID(String id) {
		mine_id = new String();
		mine_id = id;
		friend_info = new Vector<String>();
	}
	private void setCenterPane() {
		
		center_panel = new JPanel(); 
		center_panel.setLayout(new BorderLayout());
		
		option_tabbed = new JTabbedPane();
		center_panel.add(option_tabbed,BorderLayout.CENTER);
		
//		------------------Find Friend---------------------------
		search_friend_option = new JPanel();
		search_friend_option.setLayout(new BorderLayout());
		
		search_friend_panel = new search_panel("请输入对方的Kim号码" , search_panel.ADD_FRIEND_TYPE_PANEL);
		search_friend_panel.setMyAccountID(mine_id);
		
		ActionListener search_button_click_listener = new ActionListener() {					//设置添加好友选项卡内搜索按钮监听器
			public void actionPerformed(ActionEvent e) {
				if(search_friend_panel.isSafetyInput()) {
					
					RecvSendController.addToSendQueue(
							MessageOperate.packageSearchFriendMsg(
									search_friend_panel.getTextArea() , mine_id));	
				}
			}
		};
		
		KeyListener search_button_key_listener = new KeyListener() {							//搜索按钮回车监听
			public void keyTyped(KeyEvent arg0) {}
			public void keyReleased(KeyEvent arg0) {}
			public void keyPressed(KeyEvent e) {
				if(e.getKeyChar() == KeyEvent.VK_ENTER) {
					if(search_friend_panel.isSafetyInput()) {
						
						RecvSendController.addToSendQueue(
								MessageOperate.packageSearchFriendMsg(
										search_friend_panel.getTextArea() , mine_id));	
						
					}		
				}
				
			}
		}; 
		search_friend_panel.setSearchbuttonListener(search_button_click_listener);
		search_friend_panel.setTextAreaKeyListener(search_button_key_listener);
		
		search_friend_option.add(search_friend_panel,BorderLayout.CENTER);
		option_tabbed.addTab("添加好友",search_friend_option);
		
//		------------------delete friend panel---------------------------
		delete_friend_option = new JPanel();
		delete_friend_option.setLayout(new BorderLayout());
		
		delete_friend_panel = new search_panel("请输入查找号码" , search_panel.DELETE_FRIEND_TYPE_PANEL);
		delete_friend_option.add(delete_friend_panel,BorderLayout.CENTER);	
		option_tabbed.addTab("删除好友", delete_friend_option);

				
//		------------------Find Group---------------------------
		search_group_option = new JPanel();
		search_group_option.setLayout(new BorderLayout());
		
		search_group_panel = new search_panel("请输入群组号码" , search_panel.ADD_GROUP_TYPE_PANEL);
		search_group_panel.setMyAccountID(mine_id);
		
		ActionListener group_search_button_click_listener = new ActionListener() {		//设置添加群组选项卡内搜索按钮监听器
			public void actionPerformed(ActionEvent e) {
				if(search_group_panel.isSafetyInput()) {
					
					RecvSendController.addToSendQueue(
							MessageOperate.packageAskSearchGroup(
									search_group_panel.getTextArea()));					//查找群
										
				}
			}
		};
		
		KeyListener group_search_button_key_listener = new KeyListener() {			//设置添加群组选项卡内搜索按钮回车监听器
			public void keyTyped(KeyEvent arg0) {}
			public void keyReleased(KeyEvent arg0) {}
			public void keyPressed(KeyEvent e) {
				if(e.getKeyChar() == KeyEvent.VK_ENTER) {
					if(search_group_panel.isSafetyInput()) {
						
						RecvSendController.addToSendQueue(
								MessageOperate.packageAskSearchGroup(
										search_group_panel.getTextArea()));				//查找群
						
					}		
				}
				
			}
		}; 
		
		search_group_panel.setSearchbuttonListener(group_search_button_click_listener);
		search_group_panel.setTextAreaKeyListener(group_search_button_key_listener);
		
		search_group_option.add(search_group_panel,BorderLayout.CENTER);
		option_tabbed.addTab("添加群组", search_group_option);
		
//		------------------delete group panel---------------------------
		delete_group_option= new JPanel();
		delete_group_option.setLayout(new BorderLayout());
		
		delete_group_panel = new search_panel("请输入查找号码" , search_panel.DELETE_GROUP_TYPE_PANEL);
		delete_group_option.add(delete_group_panel,BorderLayout.CENTER);	
		option_tabbed.addTab("删除群组", delete_group_option);

//		------------------Create group panel---------------------------
		create_group_option = new JPanel();
		create_group_option.setLayout(null);
		
		create_group_name_text = new JTextField();
		create_group_description_text = new JTextField();
		
		cue_create_group_name = new JLabel("请输入创建群名称:");
		cue_create_group_description = new JLabel("请输入创建群描述:");
		
		create_group_button = new JButton();
		
		
//		cue_create_group_name.setText("请输入创建群名称:");
//		cue_create_group_description.setText("请输入创建群描述:");
		
		cue_create_group_name.setBounds(135 , 100 , 150 , 30);
		create_group_name_text.setBounds(270, 100, 320,30);
		
		cue_create_group_description.setBounds(135 , 145 , 150 , 30);
		create_group_description_text.setBounds(270, 145, 320,30);
		
		create_group_button.setText("创建群组");
		create_group_button.setBounds(360, 200, 100, 30);
		create_group_button.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if(create_group_name_text.getText().length() == 0 || create_group_description_text.getText().length() == 0)
					JOptionPane.showMessageDialog(null, "请检查输入信息", "提示", JOptionPane.INFORMATION_MESSAGE);
				else {
					RecvSendController.addToSendQueue(MessageOperate.packageAskCreateGroup(mine_id, 
																							create_group_name_text.getText(), 
																							create_group_description_text.getText()));
				}
					
				
			}
		});
		
		create_group_option.add(create_group_name_text);
		create_group_option.add(create_group_description_text);
		create_group_option.add(cue_create_group_name);
		create_group_option.add(cue_create_group_description);
		create_group_option.add(create_group_button);
		
		cue_create_group_name.setVisible(true);
		cue_create_group_description.setVisible(true);
		
		option_tabbed.addTab("创建群组", create_group_option);
		

//		---------------------------------------------
		this.add(center_panel,BorderLayout.CENTER);	
	}

	/**
	 * 将服务器返回的查找好友信息显示到选项卡-添加好友的结果框内
	 * @param account
	 *        若account为空，则用添加好友的标签显示，列表隐藏
	 *        不为空，则用添加好友的列表显示，标签隐藏
	 */
//	---------------------- new add friend functoin ----------------------
	public void ShowFriendInSearchFriendPanel(Account account) {
		if (null== account) {
			search_friend_panel.showFriendInfoInSearchLable("没有找到符合搜索条件的群组");
		}
		else {
			search_friend_panel.setAndshowFriendInSearchFriendList(account);
		}
	}
	
	public void ShowFriendsListInDeleteFriendPanel(ArrayList<Account> accounts) {
		
		if (null== accounts) {
			delete_friend_panel.showFriendInfoInSearchLable("没有找到符合搜索条件的群组");
		}
		else {
			delete_friend_panel.setAndshowFriendsInSearchGroupList(accounts);
		}
	}
	
	public void ShowGroupListInSearchGroupPanel(Group group) {
		if (null== group) {
			search_group_panel.showFriendInfoInSearchLable("没有找到符合搜索条件的群组");
		}
		else {
			search_group_panel.setAndshowGroupInSearchGroupList(group);
		}
	}
	
	public void ShowGroupsListInDeleteGroupPanel(ArrayList<Group> groups) {
		
		if (null== groups) {
			delete_group_panel.showFriendInfoInSearchLable("没有找到符合搜索条件的群组");
		}
		else {
			delete_group_panel.setAndshowGroupsInSearchGroupList(groups);
		}
	}
	
}


class search_panel extends JPanel{
	
	public  static final int ADD_FRIEND_TYPE_PANEL 		= 0;
	public  static final int DELETE_FRIEND_TYPE_PANEL 	= 1;
	public	static final int ADD_GROUP_TYPE_PANEL 		= 2;
	public	static final int DELETE_GROUP_TYPE_PANEL 	= 3;
	
	
	private static String my_account_id = null;
	
	private JTextField 			search_text_field;
	private JButton	  			search_button,function_button;
	private JPanel 	  			search_component_panel,search_result_panel,south_panel;
	private JLabel				search_result_lable;
	
	private JList<String>		search_jList_string;
	private Vector<String> 		search_list_vector;
	
	private String			 	text_area_string;
	private int					panel_type;
	
//	-------------------------------------------
	private Account									search_result_friend;
	static private ArrayList<Account>						search_result_friends;
	private network.commonClass.Group				search_result_group;
	static private ArrayList<network.commonClass.Group>	search_result_groups;
	
	public search_panel(String text_area_str , int type) {
		
		text_area_string = text_area_str;
		panel_type = type;
		
		initVariableByType();						//根据panel类型初始化相关的变量
		
		this.setLayout(new BorderLayout());
		
		search_component_panel = new JPanel();
		search_component_panel.setPreferredSize(new Dimension(0, 85));
		search_component_panel.setBackground(new Color(245, 245, 245));
		search_component_panel.setLayout(null);

		search_text_field = new JTextField();
		search_text_field.setBounds(220, 25, 320,30);  	// x , y , width , height
		search_text_field.setForeground(Color.lightGray);
		search_text_field.setText(text_area_string);
		search_text_field.addFocusListener(new FocusListener() {				//监听器实现文本框默认提示文字
			
//			-------- Initialize search_text_area and search_button -------
			public void focusGained(FocusEvent e) {
				if(search_text_field.getText().equals(text_area_string)) {
					search_text_field.setForeground(Color.black);
					search_text_field.setText("");
				}
			}
			
			public void focusLost(FocusEvent e) {
				if(search_text_field.getText().equals("")) {
					search_text_field.setForeground(Color.lightGray);
					search_text_field.setText(text_area_string);
				}
			}
		});
		search_text_field.setOpaque(true);									//设置文本框边界为透明
		search_component_panel.add(search_text_field);
		
		search_button = new JButton();
		search_button.setText("搜索");
		search_button.setBounds(550, 25, 60, 30);	
		search_component_panel.add(search_button);

		this.add(search_component_panel,BorderLayout.NORTH);
		
//		-------------- Initialize search result panel--------------------
		search_result_panel = new JPanel(new BorderLayout());
		
		JPanel blank_panel = new JPanel();
		blank_panel.setPreferredSize(new Dimension(300,0));
		
		search_jList_string = new JList<String>();
		search_jList_string.setPreferredSize(new Dimension(0,300));		//设置默认宽度
		search_jList_string.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e){
				if(search_jList_string.getSelectedIndex() != -1) {
					if(e.getClickCount() == 1){
						function_button.setVisible(true);
					}
				}
			}
		});

		search_result_lable = new JLabel();
		
		search_result_panel.add(search_jList_string,BorderLayout.NORTH);
		search_result_panel.add(search_result_lable,BorderLayout.CENTER);
		search_result_panel.add(blank_panel,BorderLayout.WEST);
		
		search_jList_string.setVisible(false);
		search_result_lable.setVisible(false);
		
		this.add(search_result_panel,BorderLayout.CENTER);
		

//		---------- Initialize south panel and search button ----------
		south_panel = new JPanel();
		south_panel.setLayout(null);
		south_panel.setPreferredSize(new Dimension(0, 60));
		
		function_button = new JButton();
		setFunctionButtonName();
		function_button.setVisible(false);
		function_button.setBounds(350, 0, 110, 40);
		function_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if(!search_jList_string.isSelectionEmpty()) {
					System.out.println("get Seleted index" + search_jList_string.getSelectedIndex());
				if (panel_type == ADD_FRIEND_TYPE_PANEL) {
					
					boolean flag_friend = false;
					if (search_result_friend.getId().equals(my_account_id)) {
						JOptionPane.showMessageDialog(null, "请勿添加自己为好友");
						flag_friend = true;
					}
					else {
						if (null == search_result_friends) {
							System.out.println("The friends is null");
						}
						System.out.println("search result friend size:" + search_result_friends.size());
						for(int i =0 ; i<search_result_friends.size();i++) {
							if(search_result_friends.get(i).getId().equals(search_result_friend.getId())) {
							JOptionPane.showMessageDialog(null, "请勿重复添加好友");
							flag_friend = true;
							break;
							}
						}
					}
					if(false == flag_friend) {
					RecvSendController.addToSendQueue(
							MessageOperate.packageAddFriendMsg(
									(search_result_friend.getId()), my_account_id));
					JOptionPane.showMessageDialog(null, "已发送好友添加请求");
					}
					
				}
				else if (panel_type == DELETE_FRIEND_TYPE_PANEL) {
					RecvSendController.addToSendQueue(
							MessageOperate.packageDelFriendMsg(
									(search_result_friends.get(search_jList_string.getSelectedIndex()).getId()), my_account_id));

					RecvSendController.addToSendQueue(MessageOperate.packageAskFriendListMsg());	// 拉取一次好友列表
					JOptionPane.showMessageDialog(null, "已删除好友");
					
						
				}
				else if(panel_type == ADD_GROUP_TYPE_PANEL) {
					RecvSendController.addToSendQueue(					
							MessageOperate.packageAskAddGroup(search_result_group.getId()));						//TODO ALTER ADD_GROUP_REQUSET
									
					JOptionPane.showMessageDialog(null, "已发送群组添加请求");
					
//					System.out.println("add group seleted index:" + search_result_groups.
//							get(search_jList_string.getSelectedIndex()).
//							getId());
				}
				else if(panel_type == DELETE_GROUP_TYPE_PANEL) {
					
					System.out.println("delete group seleted index:" + search_result_groups.
							get(search_jList_string.getSelectedIndex()).
							getId());
					
					RecvSendController.addToSendQueue(
							MessageOperate.pacakgeAskDelGroup(
									search_result_groups.get(search_jList_string.getSelectedIndex()).getId()));		//TODO ALTER ALTER_GROUP_REQUSET
//					
//					RecvSendController.addToSendQueue(MessageOperate.packageUpdateGroup(search_result_groups.
//															get(search_jList_string.getSelectedIndex()).
//															getId()));	// 拉取群组列表
					
					RecvSendController.addToSendQueue(MessageOperate.packageAskGetGroupList());
					
					System.out.println("delete group seleted index:" + search_result_groups.
															get(search_jList_string.getSelectedIndex()).
															getId());
					JOptionPane.showMessageDialog(null, "已删除群组");
					

				}
				
				}
			}
		});
		south_panel.add(function_button);

		this.add(south_panel,BorderLayout.SOUTH);
		
	}
	

	
	private void setFunctionButtonName() {
		if (panel_type == 0 || panel_type == 2) {
			function_button.setText("添加");
		}
		else if(panel_type == 1 || panel_type == 3){
			function_button.setText("删除");
		}
		else {
			function_button.setText("未知");
		}
	}
	
	public void setMyAccountID(String str) {
		my_account_id = str;
	}
	
	public void setSearchbuttonListener(ActionListener actionListener){
		search_button.addActionListener(actionListener);
		System.out.println("Set search button action listener");
	}
	public void setSearchbuttonListener(KeyListener keyListener) {
		search_button.addKeyListener(keyListener);
		System.out.println("Set search button key listener");
	}
	
	public boolean isSafetyInput(){
		if(search_text_field.getText().equals("") || search_text_field.getText().contains(" ")) {

			search_jList_string.setVisible(false);
			search_result_lable.setVisible(true);
			function_button.setVisible(false);
			search_result_lable.setText("请输入正确的Kim号码");
			return false;
		}
		return true;
	}
	
	
	private void setJListVisbleAndLableNot() {
		search_jList_string.setVisible(true);
		search_result_lable.setVisible(false);
	}
	
	
	public String getTextArea() {
		return search_text_field.getText();
	}
	
	public void setTextArea(String content) {
		search_text_field.setText(content);
	}
	
	public void setTextAreaKeyListener(KeyListener keyListener) {
		search_text_field.addKeyListener(keyListener);
	}

	public void showFriendInfoInSearchLable(String content) {
		
		search_jList_string.setVisible(false);
		search_result_lable.setVisible(true);
		search_result_lable.setText(content);
	}

	
	public void setFunctionBtnVisble(boolean chs) {
		search_list_vector = new Vector<>();
		if (chs == true) {
			function_button.setVisible(true);
		}
		else {
			function_button.setVisible(false);
		}
	}
//	---------------------------------------------
	private void initVariableByType() {
		
		search_list_vector = new Vector<>();
		
		if (ADD_FRIEND_TYPE_PANEL == panel_type) {
			search_result_friend = new Account();
		}
		else if(DELETE_FRIEND_TYPE_PANEL == panel_type) {
			search_result_friends = new ArrayList<>();
		}
		if(ADD_GROUP_TYPE_PANEL == panel_type) {
			search_result_group = new network.commonClass.Group();
		}
		if(DELETE_GROUP_TYPE_PANEL == panel_type) {
			search_result_groups = new ArrayList<>();
		}
	}
	
	public void setAndshowFriendInSearchFriendList(Account account) {
		
		if(!search_list_vector.isEmpty())
			search_list_vector.clear();
		
		search_result_friend = account;
		
		search_list_vector.add(search_result_friend.getId()+ " " + search_result_friend.getNikeName() + " " + search_result_friend.getSignature());
		System.out.println(search_result_friend.getId()+ " " + search_result_friend.getNikeName() + " " + search_result_friend.getSignature());
		setJListVisbleAndLableNot();
		search_jList_string.setListData(search_list_vector);
	}
	
	public void setAndshowFriendsInSearchGroupList(ArrayList<Account> accounts) {
		
		if(!search_list_vector.isEmpty())
			search_list_vector.clear();
		
		search_result_friends = accounts;
		
		Account tmp = new Account();
		for(int i = 0 ; i<accounts.size() ; i++) {
			tmp = accounts.get(i);
			search_list_vector.add(tmp.getId()+ " " + tmp.getNikeName() + " " + tmp.getSignature());
		}
		
		setJListVisbleAndLableNot();
		
		search_jList_string.setListData(search_list_vector);
	}

	public void setAndshowGroupInSearchGroupList(network.commonClass.Group group) {
		
		if(!search_list_vector.isEmpty())
			search_list_vector.clear();
		
		search_result_group = group;
		
		search_list_vector.add(group.getId()+ " " + group.getName() + " " + group.getDescription());
		setJListVisbleAndLableNot();
		search_jList_string.setListData(search_list_vector);
	}
	public void setAndshowGroupsInSearchGroupList(ArrayList<network.commonClass.Group> groups) {
		
		if(!search_list_vector.isEmpty())
			search_list_vector.clear();
		
		search_result_groups = groups;
		
		Group tmp = new Group();
		for(int i = 0 ; i<groups.size() ; i++) {
			tmp = groups.get(i);
			search_list_vector.add(tmp.getId()+ " " + tmp.getName() + " " + tmp.getDescription());
		}
		
		setJListVisbleAndLableNot();
		
		search_jList_string.setListData(search_list_vector);
	}

	
}


