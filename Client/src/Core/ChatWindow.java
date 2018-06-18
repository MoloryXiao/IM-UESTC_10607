package Core;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;

import network.commonClass.Account;
import network.commonClass.Envelope;
import network.messageOperate.MessageOperate;

/**
 * 好友聊天窗口
 * @author LeeKadima Murrey
 * @version 3.0
 * 【删减】冗余且残缺的成员属性-用于账户标识
 * 【修改】构造方法
 * 【添加】新的标识父子窗口账户信息的成员属性
 * 【添加】消息收发队列方法
 */
public class ChatWindow extends JFrame{
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
//	private JScrollBar 			sbar;
	private JScrollPane 		inputScrollpane,showScrollPane;	
	private JComboBox<String>	fontType;
	private JComboBox<String> 	fontSize;

	/**
	 * 构造函数
	 * @param myself 自身账号
	 * @param parent_ID
	 */
	public ChatWindow(Account myself,Account parent) {
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
		
		this.setVisible(true);
	}
	
	/**
	 * 设置北部内容
	 */
	private void SetNorthPane(){
		
		panel_north = new JPanel();
		panel_north.setLayout(new BorderLayout(5,5));
		panel_north.setPreferredSize(new Dimension(WINDOW_WIDTH, 105));

		head_img_icon = new ImageIcon("image/HeadImage.jpg");
		head_img_label = new JLabel(head_img_icon);
		head_img_label.setPreferredSize(new Dimension(150,150));
		panel_north.add(head_img_label,BorderLayout.WEST);
		
		panel_north_center = new JPanel();
		panel_north_center.setLayout(new BorderLayout());
		panel_north.add(panel_north_center,BorderLayout.CENTER);
		
		friend_name_label = new JLabel();
		friend_name_label.setText(this.account_mine.getNikeName());
		panel_north_center.add(friend_name_label,BorderLayout.CENTER);
		
		signature_label = new JLabel();
		signature_label.setText(this.account_mine.getSignature());
		panel_north_center.add(signature_label,BorderLayout.SOUTH);
		this.add(panel_north,BorderLayout.NORTH);
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
		splitPane.setRightComponent(panelUnderSplit);    //set the panelUnderSplit down?
		
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
			
//			showScrollPane.getVerticalScrollBar().setValue(						//聊天窗口置底	方法失效
//					showScrollPane.getVerticalScrollBar().getMaximum() + 1);
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
	}
	
	private String getFriendIDAndTimeStamp() {
		String sendTime = DATE_FORMAT.format(new Date());
		String sendStamp = "ID:"+ this.account_mine.getId() + " " + sendTime + "\n";
		
		return sendStamp;

	}
	/**
	 * 将内容显示到窗口上并换行
	 * @param Message 需要显示的信息
	 */
	public void sendMessageToShowtextfield(String Message)
	{
//		showTextArea.append(new String(getFriendIDAndTimeStamp()) + Message+"\n");
		
		showTextArea.append(new String(getFriendIDAndTimeStamp()));
		showTextArea.append(Message + "\n");
		
//		showScrollPane.getVerticalScrollBar().setValue(						//聊天窗口置底	方法失效
//				showScrollPane.getVerticalScrollBar().getMaximum());

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
