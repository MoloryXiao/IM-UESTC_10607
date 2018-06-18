package Core;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

import javax.swing.*;

import com.sun.jmx.snmp.tasks.ThreadService;

import network.commonClass.Account;
import network.commonClass.Envelope;
import network.messageOperate.MessageOperate;
import javax.swing.border.Border;

/**
 * 好友聊天窗口
 * @author LeeKadima Murrey
 * @version 3.0
 * 【删减】冗余且残缺的成员属性-用于账户标识
 * 【修改】构造方法
 * 【添加】新的标识父子窗口账户信息的成员属性
 * 【添加】消息收发队列方法
 */


public class GroupChatWindow extends JFrame{
	private Account		account_parent;		// 当前用户账户
	private Account		account_mine;		// 好友账户
	
	private static final String 	ChatWindow_TITLE = "Chating...";
	private static final int 		WINDOW_WIDTH = 935;
	private static final int 		WINDOW_HEIGHT = 680;
	
	private static 	SimpleDateFormat DATE_FORMAT;
	
	final static String[] allFontSize={
			"8" ,"9", "10","12","14","16","18",
			"20","24","28","32","36","40","44",
			"48","54","60","66","72","80","88"  
    };  
	
	private Font 				font;
	private JLabel 				head_img_label,friend_name_label,signature_label;
	private JPanel 				panel_north,panel_south,panel_north_center;
	private JPanel 				panelUnderSplit,panelUnderSplit_UpPanel,panelUnderSplit_BottomPanel;
	private JButton 			send_button,close_button;
	private JTextArea 			inputTextArea,showTextArea;
	private ImageIcon 			head_img_icon;
	private FlowLayout 			flowLayout_Bottom;
	private JSplitPane 			splitPane;
	private FlowLayout 			flowLayout;
	private JScrollPane 		inputScrollpane,showScrollPane;	
	private JComboBox<String>	fontType;
	private JComboBox<String> 	fontSize;
	
//	-----------------------
	private	JPanel				panel_east,panel_east_up;
	private JScrollPane			panel_east_down;
	private	JTabbedPane			group_notification_tabbed;
	private JList<String>		notification_list;
	private JPopupMenu			notification_popup_menu;
	private JMenuItem			menu_item_1,menu_item_2,menu_item_3;
	
	private JTabbedPane			group_member_tabbed;
	private	JList<String>		member_list;
	private JPopupMenu			member_popup_menu;
	private JMenuItem			menu_item_chat , menu_item_add_friend , menu_item_report;
	private int					seleted_member_index;


	/**
	 * 构造函数
	 * @param myself 自身账号
	 * @param parent_ID
	 */
	public GroupChatWindow(Account myself,Account parent) {
//	public GroupChatWindow() {
			
		DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		/* 设置窗口定位信息 */
		this.account_mine = new Account(myself.getId(),myself.getNikeName(),
				myself.getOnline(),myself.getSignature());
		this.account_parent = new Account(parent.getId(),parent.getNikeName(),
				parent.getOnline(),parent.getSignature());
		
		this.setTitle(ChatWindow_TITLE);
		this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		
		this.setResizable(false);
		this.setLocationRelativeTo(null);							//窗口剧中
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		SetNorthPane();
		SetSouthPane();
		SetEastPane();
		this.setVisible(true);
	}
	
	private	void SetEastPane() {
		panel_east = new JPanel();
		panel_east.setLayout(new BorderLayout());		//设置东部类型
		panel_east.setPreferredSize(new Dimension(225, 0));
		
//		-----------  UP  -----------
		group_notification_tabbed = new JTabbedPane();	//新建选项卡
		
		panel_east_up = new JPanel();					//新建选项卡显示信息面板
		panel_east_up.setLayout(new BorderLayout());
		panel_east_up.setPreferredSize(new Dimension(0, 225));
		
		notification_popup_menu = new JPopupMenu();
		notification_list = new JList<String>();

		Vector<String> strings = new Vector<String>();
		strings.add("xxx");
		strings.add("yyy");
		strings.add("zzz");
		strings.add("zzz");
		strings.add("zzz");
		strings.add("zzz");
		strings.add("zzz");
		strings.add("zzz");
		
		JList<String> menu= new JList<>();
		menu.setListData(strings);
		notification_list.setListData(strings);
		
		
		menu_item_1 = new JMenuItem("添加");
		menu_item_2 = new JMenuItem("删除");
		menu_item_3 = new JMenuItem("修改");
		menu_item_1.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				System.out.println("add notification");
			}
		});
		menu_item_2.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				System.out.println("delete notification");
			}
		});
		menu_item_3.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				System.out.println("alter notification");
			}
		});
		notification_popup_menu.add(menu_item_1);
		notification_popup_menu.add(menu_item_2);
		notification_popup_menu.add(menu_item_3);
		
		notification_list.add(notification_popup_menu);
		notification_list.addMouseListener(new MouseAdapter(){
			 public void mouseReleased(MouseEvent e) {
//	           if(e.getButton() == MouseEvent.BUTTON3 && notification_list.getSelectedIndex() != -1)
//				 if (e.isPopupTrigger()) {
//				 if( e.isPopupTrigger() && notification_list.getSelectedIndex() != -1) {
				 if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0 && !e.isControlDown() && !e.isShiftDown()) {
					 	notification_popup_menu.show(e.getComponent(),e.getX(),e.getY());
	           			System.out.println(notification_list.getSelectedIndex());
				 }
			}
		});
		/*
		notification_list.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent arg0) {
				notification_list.setSelectedIndices(new int[] {});
			}
			
			@Override
			public void focusGained(FocusEvent arg0) {
				// TODO Auto-generated method stub
				;
			}
		});
		*/
		group_notification_tabbed.addTab("群通知", panel_east_up);
		panel_east_up.add(notification_list,BorderLayout.CENTER);
		panel_east.add(group_notification_tabbed,BorderLayout.NORTH);
//		-----------  DOWN  -----------
		group_member_tabbed = new JTabbedPane();
		
		panel_east_down = new JScrollPane();
		panel_east_down.setPreferredSize(new Dimension(0, 345));
		
		member_popup_menu = new JPopupMenu();
			
		member_list = new JList<String>();
		member_list.setListData(strings);
		
		menu_item_chat = new JMenuItem("发送消息");
		menu_item_add_friend = new JMenuItem("添加好友");
		menu_item_report = new JMenuItem("我要举报！");
		menu_item_chat.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent event) {
				//System.out.println("发送消息");
				//从index获得对象的名字，和本对象名字
			}
		});
		menu_item_add_friend.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent event) {
				System.out.println("添加好友");
			}
		});
		menu_item_report.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent event) {
				System.out.println("举报失败");
			}
		});
		member_popup_menu.add(menu_item_chat);
		member_popup_menu.add(menu_item_add_friend);
		member_popup_menu.add(menu_item_report);
		
		member_list.add(member_popup_menu);
		member_list.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if( e.isPopupTrigger() && member_list.getSelectedIndex() != -1) {
					member_popup_menu.show(e.getComponent(), e.getX(), e.getY());
					System.out.println("This is B "+member_list.getSelectedIndex());
					seleted_member_index = member_list.getSelectedIndex();
				}
				
			}
		});
		group_member_tabbed.addTab("群成员", panel_east_down);
		panel_east_down.setViewportView(member_list);
		panel_east_down.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//		panel_east_down.add(member_list);
		panel_east.add(group_member_tabbed,BorderLayout.CENTER);
//		------------------------------
		this.add(panel_east,BorderLayout.EAST);

	}
	
	/**
	 * 设置北部内容
	 */
	private void SetNorthPane(){
		
		panel_north = new JPanel();
		panel_north.setLayout(new BorderLayout(5,5));
		panel_north.setPreferredSize(new Dimension(600, 105));

		head_img_icon = new ImageIcon("image/HeadImage.jpg");
		head_img_label = new JLabel(head_img_icon);
		head_img_label.setPreferredSize(new Dimension(150,150));
		panel_north.add(head_img_label,BorderLayout.WEST);
		
		panel_north_center = new JPanel();
		panel_north_center.setLayout(new BorderLayout());
		panel_north.add(panel_north_center,BorderLayout.CENTER);
		
		friend_name_label = new JLabel();
		friend_name_label.setText(this.account_mine.getNikeName());
		friend_name_label.setText("Group name");
		panel_north_center.add(friend_name_label,BorderLayout.CENTER);
		
		signature_label = new JLabel();
		signature_label.setText(this.account_mine.getSignature());
		signature_label.setText("Group signature");
		panel_north_center.add(signature_label,BorderLayout.SOUTH);
//		this.add(panel_north,BorderLayout.NORTH);
	}
	
	/**
	 * 设置南部内容
	 */
	private void SetSouthPane(){
		panel_south = new JPanel();
		panel_south.setLayout(new BorderLayout());
		
		splitPane = new JSplitPane();
		splitPane.setDividerLocation(300);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		panel_south.add(splitPane,BorderLayout.CENTER);
		
		panelUnderSplit = new JPanel();
		panelUnderSplit.setLayout(new BorderLayout());
		splitPane.setRightComponent(panelUnderSplit);    //set the panelUnderSplit down
		
		panelUnderSplit_UpPanel = new JPanel();
		flowLayout = new FlowLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);		//set alignment to left
		panelUnderSplit_UpPanel.setLayout(flowLayout);

		GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		final String[] allFontType;
		allFontType = graphicsEnvironment.getAvailableFontFamilyNames();
		fontType = new JComboBox<String>(allFontType);
		fontSize = new JComboBox<String>(allFontSize);
		fontSize.setPreferredSize(new Dimension(80, 30));
		
		ActionListener fontListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String fontTypeChosen = (String)fontType.getSelectedItem();
				int	   fontSizeChosen = Integer.parseInt((String)fontSize.getSelectedItem());
				font = new Font(fontTypeChosen, Font.PLAIN, fontSizeChosen);
				inputTextArea.setFont(font);
				showTextArea.setFont(font);
			}
		};
		
		fontType.addActionListener(fontListener);
		fontSize.addActionListener(fontListener);
		
		panelUnderSplit_UpPanel.add(fontType);
		panelUnderSplit_UpPanel.add(fontSize);
		//------------------------------
		panelUnderSplit.add(panelUnderSplit_UpPanel,BorderLayout.NORTH);
		
		inputScrollpane = new JScrollPane();
		panelUnderSplit.add(inputScrollpane,BorderLayout.CENTER);
				
		panelUnderSplit_BottomPanel = new JPanel();
		flowLayout_Bottom = new FlowLayout();
		flowLayout_Bottom.setAlignment(FlowLayout.RIGHT);
		panelUnderSplit_BottomPanel.setLayout(flowLayout_Bottom);
		//-----------Button-------------
		send_button = new JButton();
		send_button.setText("发送");
		panelUnderSplit_BottomPanel.add(send_button);
		close_button = new JButton();
		close_button.setText("关闭");
		panelUnderSplit_BottomPanel.add(close_button);
		//-----------send_listener----------
		ActionListener send_listener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				submitMessage();
			}
		};
		
		KeyListener enter_Listener = new KeyListener() {
			public void keyTyped(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {}
			public void keyPressed(KeyEvent e) {
				if(e.getKeyChar() == KeyEvent.VK_ENTER && e.isControlDown()){
					submitMessage();
				}				
			}			
		};
		send_button.addActionListener(send_listener);
		//-----------close_listener----------
		closeChatWindowButton_Listener closeChatWindowButton_Listener = new closeChatWindowButton_Listener(this);
		close_button.addActionListener(closeChatWindowButton_Listener);
		//--------------------------------
		
		panelUnderSplit.add(panelUnderSplit_BottomPanel,BorderLayout.SOUTH);

		inputTextArea = new JTextArea();
		inputTextArea.addKeyListener(enter_Listener);
		inputTextArea.setLineWrap(true);				//set inputText line wrap
		
		inputScrollpane.setViewportView(inputTextArea);
		
		showScrollPane = new JScrollPane();
		splitPane.setLeftComponent(showScrollPane);
		
		showTextArea = new JTextArea();
		showTextArea.setEditable(false);
		showTextArea.setLineWrap(true);					//set showText line wrap
		
		showScrollPane.setViewportView(showTextArea);
		
		panel_south.add(panel_north,BorderLayout.NORTH);		//将北部面板放在南部面板的北面。让北部和南部放一个面板里头。
		this.add(panel_south,BorderLayout.CENTER);
	}
	
	/**
	 * 触发发送事件时 将内容打印到显示框并转发到服务器
	 */
	private void submitMessage() {
		if(!inputTextArea.getText().equals("")){
			/* 打印到显示框 */
			String sendStr = inputTextArea.getText();
			showTextArea.append(new String(getMyIDAndTimeStamp()));
			showTextArea.append(sendStr + "\n");
			inputTextArea.setText("");
			
			showScrollPane.getVerticalScrollBar().setValue(						//聊天窗口置底	方法失效
					showScrollPane.getVerticalScrollBar().getMaximum() + 1);
			showTextArea.setCaretPosition(showTextArea.getText().length());
			
			/* 转发到服务器 */
			Envelope evp = new Envelope(this.account_mine.getId(),this.account_parent.getId(),sendStr);
			RecvSendController.addToSendQueue(MessageOperate.packageEnvelope(evp));
			System.out.println("ChatInfoSend: " + this.account_parent.getId() + " send “" 
					+ sendStr + "” to " + this.account_mine.getId());
		}
	}
	
	/**
	 * 获取系统时间
	 */
	public String getMyIDAndTimeStamp() {
		String sendTime = DATE_FORMAT.format(new Date());
		String sendStamp = "ID:"+this.account_parent.getId() + " " + sendTime + "\n";
		
		return sendStamp;
//		return sendTime;
	}
	
	private String getFriendIDAndTimeStamp() {
		String sendTime = DATE_FORMAT.format(new Date());
		String sendStamp = "ID:"+ this.account_mine.getId() + " " + sendTime + "\n";
		
		return sendStamp;
//		return sendTime;

	}
	/**
	 * 将内容显示到窗口上并换行
	 * @param Message 需要显示的信息
	 */
	public void sendMessageToShowtextfield(String Message)
	{
		showTextArea.append(new String(getFriendIDAndTimeStamp()) + Message+"\n");
		
		showTextArea.append(new String(getFriendIDAndTimeStamp()));
		showTextArea.append(Message + "\n");
		
		showScrollPane.getVerticalScrollBar().setValue(						//聊天窗口置底	方法失效
				showScrollPane.getVerticalScrollBar().getMaximum());

		showTextArea.setCaretPosition(showTextArea.getText().length());
	}
		
	/**
	 * 关闭按钮监听类
	 */
	private class closeChatWindowButton_Listener implements ActionListener{
		private JFrame jFrame;
		public closeChatWindowButton_Listener (JFrame jFrame) {
			this.jFrame = jFrame;
		}
		public void actionPerformed(ActionEvent e) {
			jFrame.dispose();
		}
	}
	
	/**************** setter and getter ****************/
}
