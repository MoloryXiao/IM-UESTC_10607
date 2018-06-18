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
import javax.swing.JOptionPane;

import network.commonClass.Account;
import network.messageOperate.MessageOperate;

import java.awt.event.*;
import java.util.Vector;

/**
 * 添加好友窗口
 * @author LeeKadima
 * @version 1.0
 * 【实现】添加好友功能基本实现
 * @version 
 */

public class AddFriendWindow extends JFrame {
	private static final String ADD_FRIEND_WINDOW_TITLE = "添加好友";
	private static final int ADD_FRIEND_WINDOW_WIDTH = 850;
	private static final int ADD_FRIEND_WINDOW_HEIGHT = 550;
	
	private JTabbedPane		option_tabbed;
	private JPanel			search_friend_option,find_group_option,delete_friend_option;
	private JPanel 			center_panel;
	private search_panel	search_friend_panel,delete_friend_panel;

	private String			mine_id;
	Vector<String> 			friend_info;

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
		
		KeyListener search_button_key_listener = new KeyListener() {
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
		
//		------------------Find Group---------------------------
		find_group_option = new JPanel();
		find_group_option.setLayout(new BorderLayout());
		
		search_panel search_group_panel = new search_panel("请输入群组号码" , search_panel.ADD_FRIEND_TYPE_PANEL);
		find_group_option.add(search_group_panel,BorderLayout.CENTER);
		option_tabbed.addTab("添加群组", find_group_option);
		
//		------------------delete friend panel---------------------------
		delete_friend_option = new JPanel();
		delete_friend_option.setLayout(new BorderLayout());
		
		delete_friend_panel = new search_panel("请输入查找号码" , search_panel.DELETE_FRIEND_TYPE_PANEL);
		delete_friend_option.add(delete_friend_panel,BorderLayout.CENTER);	
		option_tabbed.addTab("删除好友", delete_friend_option);

		this.add(center_panel,BorderLayout.CENTER);
	}

	/**
	 * 将服务器返回的查找好友信息显示到选项卡-添加好友的结果框内
	 * @param account
	 *        若account为空，则用添加好友的标签显示，列表隐藏
	 *        不为空，则用添加好友的列表显示，标签隐藏
	 */
	public void showFriendInfotInSearchFriendPanel(Account account) {
		if (account == null) {
			search_friend_panel.showFriendInfoInSearchLable("没有找到符合搜索条件的用户");
		}
		else {
			if (!friend_info.isEmpty()) {
				friend_info.removeAllElements();
			}
			friend_info.add(account.getId() + "      " + account.getNikeName() +  "      " + account.getSignature());
			search_friend_panel.showFriendInfoInSearchList(friend_info);
		}
	}
	
	public void setDeleteFriendPanelList(Vector<String> vec_delete_friend) {
		delete_friend_panel.showFriendInfoInSearchList(vec_delete_friend);
	}
}


class search_panel extends JPanel{
	
	public  static final int ADD_FRIEND_TYPE_PANEL = 0;
	public  static final int DELETE_FRIEND_TYPE_PANEL = 1;
	
	private static String my_account_id = null;
	
	private JTextField 			search_text_field;
	private JButton	  			search_button,function_button;
	private JPanel 	  			search_component_panel,search_result_panel,south_panel;
	private JLabel				search_result_lable;
	
	private JList<String>		search_friend_JjList_string;
	private Vector<String> 		friend_list;
	
	private String			 	text_area_string;
	private int					panel_type;
	
	public search_panel(String text_area_str , int type) {
		
		text_area_string = text_area_str;
		panel_type = type;
		
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
		
		search_friend_JjList_string = new JList<String>();
		search_friend_JjList_string.setPreferredSize(new Dimension(0,300));		//设置默认宽度
		search_friend_JjList_string.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e){
				if(search_friend_JjList_string.getSelectedIndex() != -1) {
					if(e.getClickCount() == 1){
						function_button.setVisible(true);
					}
				}
			}
		});

		search_result_lable = new JLabel();
		
		search_result_panel.add(search_friend_JjList_string,BorderLayout.NORTH);
		search_result_panel.add(search_result_lable,BorderLayout.CENTER);
		search_result_panel.add(blank_panel,BorderLayout.WEST);
		
		search_friend_JjList_string.setVisible(false);
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
				int index = search_friend_JjList_string.getSelectedIndex();
				Object object = search_friend_JjList_string.getModel().getElementAt(index);
				if (panel_type == ADD_FRIEND_TYPE_PANEL) {
					RecvSendController.addToSendQueue(
							MessageOperate.packageAddFriendMsg(
									(getFriendIDFromClickResult(object.toString())), my_account_id));

					JOptionPane.showMessageDialog(null, "已发送好友添加请求");
				}
				else if (panel_type == DELETE_FRIEND_TYPE_PANEL) {
					RecvSendController.addToSendQueue(
							MessageOperate.packageDelFriendMsg(
									(getFriendIDFromClickResult(object.toString())), my_account_id));

					RecvSendController.addToSendQueue(MessageOperate.packageAskFriendListMsg());	// 拉取一次好友列表
					JOptionPane.showMessageDialog(null, "已删除好友");
					deleteFriendFrmListByIndex(index);

				}
				
			}
		});
		south_panel.add(function_button);

		this.add(south_panel,BorderLayout.SOUTH);
		
	}
	
	private void setFunctionButtonName() {
		if (panel_type == 0) {
			function_button.setText("添加");
		}
		else if(panel_type == 1){
			function_button.setText("删除");
		}
		else {
			function_button.setText("未知");
		}
	}
	
	private String getFriendIDFromClickResult(String str) {
		String friend_id = new String();
		for (int i = 0 ; i < str.length() ; i++) {
				if(str.charAt(i) == ' '){
					break;
				}
				friend_id += str.charAt(i);
			}
			return friend_id;
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

			search_friend_JjList_string.setVisible(false);
			search_result_lable.setVisible(true);
			function_button.setVisible(false);
			search_result_lable.setText("请输入正确的Kim号码");
			return false;
		}
		return true;
	}
	
	
	private void setJListVisbleAndLableNot() {
		search_friend_JjList_string.setVisible(true);
		search_result_lable.setVisible(false);
	}
	
	
	public String getTextArea() {
		return search_text_field.getText();
	}
	
	public void setTextArea(String content) {
		search_text_field.setText(content);
	}
	
//	public void () {
//		
//	}
	public void setTextAreaKeyListener(KeyListener keyListener) {
		search_text_field.addKeyListener(keyListener);
	}
	
	private void deleteFriendFrmListByIndex(int index) {
		friend_list.remove(index);
	}

	public void showFriendInfoInSearchLable(String content) {
		
		search_friend_JjList_string.setVisible(false);
		search_result_lable.setVisible(true);
		search_result_lable.setText(content);
	}
	public void showFriendInfoInSearchList(Vector<String> friend_info) {
		friend_list = new Vector<String>();
		friend_list = friend_info;
		setJListVisbleAndLableNot();
		search_friend_JjList_string.setListData(friend_info);
	}

	public void showAllFriendInDeletePanel() {
		setJListVisbleAndLableNot();
		search_friend_JjList_string.setListData(friend_list);
	}
	
	public void setFunctionBtnVisble(boolean chs) {
		if (chs == true) {
			function_button.setVisible(true);
		}
		else {
			function_button.setVisible(false);
		}
	}
}


