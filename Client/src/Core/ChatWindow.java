package Core;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;

import Core.Controll.NetworkController;
import network.commonClass.Account;
import network.commonClass.Envelope;

/**
 * 好友聊天窗口
 * @author LeeKadima
 * @version 2.0
 */
public class ChatWindow extends JFrame{
	private String 		str_mine_ID;
	private String 		str_friend_ID;
	private String 		str_friend_signature;
	private String 		str_friend_nickName;
	private boolean 	flag_friend_online;
	
	private static final String 	ChatWindow_TITLE = "Chating...";
	private static final int 		WINDOW_WIDTH = 935;
	private static final int 		WINDOW_HEIGHT = 680;
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
	private JScrollBar 			sbar;
	private JScrollPane 		inputpanel,showPanel;	
	private NetworkController 	nkc;
	private JComboBox<String>	fontType;
	private JComboBox<String> 	fontSize;

	/**
	 * 构造函数
	 * @param myself 自身账号
	 * @param parent_ID
	 */
	public ChatWindow(Account myself,String parent_ID) {
		nkc = new NetworkController();
		
		/* 设置窗口定位信息 */
		this.setMine_ID(parent_ID);
		this.setFriend_ID(myself.getID());
		this.setFriend_online(myself.getOnLine());
		this.setFriend_nickName(myself.getNikeName());
		this.setFriend_signature(myself.getSignature());
		
		this.setTitle(ChatWindow_TITLE);
		this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		
		this.setResizable(false);
		this.setLocationRelativeTo(null);
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
		friend_name_label.setText(this.getFriend_nickName());
		panel_north_center.add(friend_name_label,BorderLayout.CENTER);
		
		signature_label = new JLabel();
		signature_label.setText(this.getFriend_nickName());
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
		
		inputpanel = new JScrollPane();
		panelUnderSplit.add(inputpanel,BorderLayout.CENTER);
				
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
		
		inputpanel.setViewportView(inputTextArea);
		
		showPanel = new JScrollPane();
		sbar = showPanel.getVerticalScrollBar();
		splitPane.setLeftComponent(showPanel);
		
		showTextArea = new JTextArea();
		showTextArea.setEditable(false);
		showTextArea.setLineWrap(true);					//set showText line wrap
		
		showPanel.setViewportView(showTextArea);
		
		this.add(panel_south,BorderLayout.CENTER);
	}
	
	/**
	 * 触发发送事件时 将内容打印到显示框并转发到服务器
	 */
	private void submitMessage() {
		if(!inputTextArea.getText().equals("")){
			/* 打印到显示框 */
			String sendStr = inputTextArea.getText();
			showTextArea.append(sendStr+"\n");
			inputTextArea.setText("");
			sbar.setValue(sbar.getMaximum());
			/* 转发到服务器 */
			Envelope evp = new Envelope(this.getFriend_ID(),this.getMine_ID(),sendStr);
			nkc.sendEnvelope(evp);
			System.out.println("ChatInfoSend: " + ChatWindow.this.getMine_ID() + " send “" 
					+ sendStr + "” to " + ChatWindow.this.getFriend_ID());
		}
	}
	
	/**
	 * 将内容显示到窗口上并换行
	 * @param Message 需要显示的信息
	 */
	public void sendMessageToShowtextfield(String Message)
	{
		showTextArea.append(Message+"\r\n");
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
	public String getMine_ID() {
		return str_mine_ID;
	}
	public void setMine_ID(String mine_ID) {
		this.str_mine_ID = mine_ID;
	}

	public String getFriend_ID() {
		return str_friend_ID;
	}
	public void setFriend_ID(String friend_ID) {
		this.str_friend_ID = friend_ID;
	}

	public String getFriend_signature() {
		return str_friend_signature;
	}
	public void setFriend_signature(String friend_signature) {
		this.str_friend_signature = friend_signature;
	}

	public String getFriend_nickName() {
		return str_friend_nickName;
	}
	public void setFriend_nickName(String friend_nickName) {
		this.str_friend_nickName = friend_nickName;
	}
	public boolean isFriend_online() {
		return flag_friend_online;
	}
	public void setFriend_online(boolean friend_online) {
		this.flag_friend_online = friend_online;
	}
}
