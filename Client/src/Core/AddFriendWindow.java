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
import javax.swing.border.Border;

import org.ietf.jgss.MessageProp;

import com.sun.corba.se.spi.orbutil.fsm.State;
import com.sun.org.apache.bcel.internal.generic.NEW;

import network.commonClass.Account;
import network.commonClass.Envelope;
import network.messageOperate.MessageOperate;

import java.awt.event.*;
import java.util.ArrayList;
import java.util.Vector;


public class AddFriendWindow extends JFrame {
	private static final String ADD_FRIEND_WINDOW_TITLE = "添加好友";
	private static final int ADD_FRIEND_WINDOW_WIDTH = 850;
	private static final int ADD_FRIEND_WINDOW_HEIGHT = 550;
	
	private JTabbedPane		option_window;
	private JPanel			find_friend_panel,find_group_panel,find_other_panel,
							delete_friend_panel;
	private JPanel 			center_panel;
	private boolean			search_friend_result;
	private boolean			add_friend_result;
	private search_panel	search_friend_panel,delete_panel;

	private LoginInfo		mine_account_info;
	private Account			friend_account;
	
	public AddFriendWindow(LoginInfo loginInfo) {
		
		mine_account_info = new LoginInfo();
		mine_account_info = loginInfo;
		friend_account = new Account();
		
		this.setTitle(ADD_FRIEND_WINDOW_TITLE);		
		this.setSize(ADD_FRIEND_WINDOW_WIDTH, ADD_FRIEND_WINDOW_HEIGHT);
		
		this.setResizable(false);		//window size can't be change.
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		SetCenterPane();
		
		this.setLocationRelativeTo(null);				//窗口剧中
		this.setVisible(true);
		this.requestFocus();
	}
	
	
	private void SetCenterPane() {
		center_panel = new JPanel(); 
		center_panel.setLayout(new BorderLayout());
		
		option_window = new JTabbedPane();
		center_panel.add(option_window,BorderLayout.CENTER);
		
//		------------------Find Friend---------------------------
		find_friend_panel = new JPanel();
		find_friend_panel.setLayout(new BorderLayout());
		
		search_friend_panel = new search_panel("请输入对方的Kim号码" , search_panel.ADD_FRIEND_TYPE_PANEL);
		search_friend_panel.setMyAccountID(mine_account_info.getLoginYhm());
		
		ActionListener search_friend_panel_search_btn_listener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(search_friend_panel.isSafetyInput()) {
					System.out.println("【Check friend Info】  My ID :"+ 
										 mine_account_info.getLoginYhm() + "   Friend ID:" + 
										 search_friend_panel.getTextAreaString());
					RecvSendController.addToSendQueue(
							MessageOperate.packageSearchFriendMsg(search_friend_panel.getTextAreaString(), 
																		mine_account_info.getLoginYhm()));
				}
			}
			
		};
		search_friend_panel.setSearchbuttonListener(search_friend_panel_search_btn_listener);
		
		find_friend_panel.add(search_friend_panel,BorderLayout.CENTER);
		option_window.addTab("找人",find_friend_panel);
		
//		------------------Find Group---------------------------
		find_group_panel = new JPanel();
		find_group_panel.setLayout(new BorderLayout());
		
		search_panel search_group_panel = new search_panel("请输入群组号码" , search_panel.ADD_FRIEND_TYPE_PANEL);
		find_group_panel.add(search_group_panel,BorderLayout.CENTER);
		option_window.addTab("找群", find_group_panel);
		
//		------------------Find other---------------------------
		find_other_panel = new JPanel();
		option_window.addTab("找其它", find_other_panel);

//		------------------delete friend panel---------------------------
		delete_friend_panel = new JPanel();
		delete_friend_panel.setLayout(new BorderLayout());
		
		delete_panel = new search_panel("请输入查找号码" , search_panel.DELETE_FRIEND_TYPE_PANEL);
		delete_panel.setButtonName("删除");
		delete_friend_panel.add(delete_panel,BorderLayout.CENTER);	
		option_window.addTab("删除好友", delete_friend_panel);

//		
		this.add(center_panel,BorderLayout.CENTER);
	}
	
//	private void SetSouthPane() {
//		south_panel = new JPanel();
//		south_panel.setLayout(null);
//		south_panel.setPreferredSize(new Dimension(0, 60));
//		
//		add_button = new JButton();
//		add_button.setText("添加");
////		add_button.setSize(new Dimension(300, 40));				//110 40
//		add_button.setBounds(350, 0, 110, 40);
//		south_panel.add(add_button);
//
//		this.add(south_panel,BorderLayout.SOUTH);
//	}
	
	public void setAddFriendResult(boolean bool) {
		add_friend_result = bool;
	}
	public boolean getAddFriendResult() {
		return add_friend_result;
	}
	public void setSearchFriendResult(boolean bool) {
		search_friend_result = bool;
	}
	public boolean getSearchFriendResult() {
		return search_friend_result;
	}
	
//	public void addFriendSuccessHint() {
//			JOptionPane.showMessageDialog(null, "添加好友成功", "提示", JOptionPane.INFORMATION_MESSAGE);
//	}
//	public void addFriendFailureHing() {
//		search_friend_panel.setTableInPanel("没有找到符合搜索条件的用户");
//	}
	public void showFriendInfo(Account account) {
		search_friend_panel.setOneAccountInList(new String(account.getId() + "      " + 
														   account.getNikeName() +  "      " + 
														   account.getSignature()));
	}
	public void setFriendAccount(Account account) {
		System.out.println("In setFriendAccount");
		if (account == null) {
			search_friend_panel.setTableInPanel("没有找到符合搜索条件的用户");
		}
		else {
			friend_account = account;
			showFriendInfo(friend_account);
		}
	}
	public void setDeleteFriendPanel(Vector<String> vec_delete_friend) {
		delete_panel.setDeleteFriendJlist(vec_delete_friend);
	}



}


class search_panel extends JPanel{
	
	final static String[] INPUT_WRONG_FORMAT = {"请输入正确的Kim号码"};
	final static String[]  FIND_NULL_USER = {"没有找到符合搜索条件的用户"};
	public static final int ADD_FRIEND_TYPE_PANEL = 0;
	public static final int DELETE_FRIEND_TYPE_PANEL = 1;
	private static String my_account_id = null;
	
	private JTextArea 			search_text_area;
	private JButton	  			search_button,function_button;
	private JPanel 	  			search_component_panel,
					  			search_result_panel,
					  			south_panel;
	private JLabel				search_result_lable;
	private JList<String>		search_friend_jList_string;
	private ListModel<String> 	search_result_listmode;
	private String[]			content_in_JList = null;
	
	//	final static ListModel<String> 
	String			 		text_area_string = null;
	
	private int				panel_type = 0;
	
	public search_panel(String text_area_str , int type) {
		text_area_string = text_area_str;
		panel_type = type;
		
		System.out.println("In search panel");
		
		//-------------------Initializing---------------------

		//----------------------------------------------------
		
		this.setLayout(new BorderLayout());
		search_component_panel = new JPanel();
		search_component_panel.setPreferredSize(new Dimension(0, 85));
		search_component_panel.setBackground(new Color(245, 245, 245));
		search_component_panel.setLayout(null);
		
		search_text_area = new JTextArea();
		search_text_area.setBounds(220, 25, 320,30);  	// x , y , width , height
		search_text_area.setForeground(Color.lightGray);
		search_text_area.setText(text_area_string);
		
		search_text_area.addFocusListener(new FocusListener() {
					
			@Override
			public void focusGained(FocusEvent e) {
				if(search_text_area.getText().equals(text_area_string)) {
					search_text_area.setForeground(Color.black);
					search_text_area.setText("");
					
				}
				System.out.println("get focus");
			}
			
			@Override
			public void focusLost(FocusEvent e) {
				if(search_text_area.getText().equals("")) {
					search_text_area.setForeground(Color.lightGray);
					search_text_area.setText(text_area_string);
				
				}
				System.out.println("lost focus");
			}
		});
		search_text_area.setOpaque(true);
		search_component_panel.add(search_text_area);
		
		search_button = new JButton();
		search_button.setText("搜索");
		search_button.setBounds(550, 25, 60, 30);	
		search_component_panel.add(search_button);
		
		this.add(search_component_panel,BorderLayout.NORTH);
		
		//-----------------------search_result_panel--------------------
		search_result_panel = new JPanel(new BorderLayout());
//		search_result_panel.setBackground(new Color(236	, 236, 236));
		
		JPanel blank_panel = new JPanel();
		blank_panel.setPreferredSize(new Dimension(300,0));
//		blank_panel.setBackground(new Color(236, 236, 236));
		
		search_friend_jList_string = new JList<String>();
		search_friend_jList_string.setPreferredSize(new Dimension(0,300));		//设置默认宽度
		search_friend_jList_string.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e){
				if(search_friend_jList_string.getSelectedIndex() != -1) {
					if(e.getClickCount() == 1){
						function_button.setVisible(true);
					}
				}
			}
		});

		search_result_lable = new JLabel();
		
		search_result_panel.add(search_friend_jList_string,BorderLayout.NORTH);
		search_result_panel.add(search_result_lable,BorderLayout.CENTER);
		search_result_panel.add(blank_panel,BorderLayout.WEST);
		
		search_friend_jList_string.setVisible(false);
		search_result_lable.setVisible(false);
		//----------------------------------------------------------------
		
		this.add(search_result_panel,BorderLayout.CENTER);
		
		south_panel = new JPanel();
		south_panel.setLayout(null);
		south_panel.setPreferredSize(new Dimension(0, 60));
		
		function_button = new JButton();
		function_button.setText("添加");
		function_button.setVisible(false);
		function_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int index = search_friend_jList_string.getSelectedIndex();
				Object object = search_friend_jList_string.getModel().getElementAt(index);
				
				if (panel_type == 0) {
					RecvSendController.addToSendQueue(
							MessageOperate.packageAddFriendMsg(
									(getFriendIDFromClickResult(object.toString())), my_account_id));

							System.out.println(getFriendIDFromClickResult(object.toString()));
							JOptionPane.showMessageDialog(null, "已发送好友添加请求");
				}
				else if (panel_type == 1) {
					RecvSendController.addToSendQueue(
							MessageOperate.packageDelFriendMsg(
									(getFriendIDFromClickResult(object.toString())), my_account_id));
							
							System.out.println(getFriendIDFromClickResult("Friend ID:" + getFriendIDFromClickResult(object.toString())));
							System.out.println("My ID:" + my_account_id);
							JOptionPane.showMessageDialog(null, "已发送删除添加请求");
				}
				
			}
		});
		
		
//		add_button.setSize(new Dimension(300, 40));				//110 40
		function_button.setBounds(350, 0, 110, 40);
		south_panel.add(function_button);

		this.add(south_panel,BorderLayout.SOUTH);
		
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
	}
	
	public boolean isSafetyInput(){
		if(search_text_area.getText().equals("") || search_text_area.getText().contains(" ")) {

			search_friend_jList_string.setVisible(false);
			search_result_lable.setVisible(true);
			function_button.setVisible(false);
			search_result_lable.setText(INPUT_WRONG_FORMAT[0]);
			return false;
		}
		return true;
	}
	
	public String getTextAreaString() {
		return search_text_area.getText();
	}
	
	public void setTableInPanel(String content) {
		
		search_friend_jList_string.setVisible(false);
		search_result_lable.setVisible(true);
		search_result_lable.setText(content);
	}
	public void setOneAccountInList(String content) {
		System.out.println(content);
		content_in_JList = new String[1];
		content_in_JList[0] = content;
		
		search_friend_jList_string.setVisible(true);
		search_result_lable.setVisible(false);	
		
		search_friend_jList_string.setListData(content_in_JList);
		
	}

	public void setContentsInList(String[] content) {
		search_friend_jList_string.setVisible(true);
		search_result_lable.setVisible(false);
		search_friend_jList_string.setListData(content);
	}
	
	public void setDeleteFriendJlist(Vector<String> delete_friend_list) {
		Vector<String> friend_list = new Vector<String>();
		friend_list = delete_friend_list;
		search_friend_jList_string.setVisible(true);
		search_result_lable.setVisible(false);
		search_friend_jList_string.setListData(friend_list);
	}
	public void setButtonName(String name) {
		function_button.setText(name);
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


