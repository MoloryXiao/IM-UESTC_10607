package Core;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.*;
import Core.Controll.NetworkController;
import network.commonClass.Account;
import network.commonClass.Envelope;
import network.messageOperate.MessageOperate;

public class ChatWindow extends JFrame{
	private String 		mine_ID;
	private String 		friend_ID;
	private String 		friend_signature;
	private String 		friend_nickName;
	private boolean 	friend_online;
	
	private static final String ChatWindow_TITLE = "Chating";
	private static final int WINDOW_WIDTH = 935;
	private static final int WINDOW_HEIGHT = 680;
	final static String[] allFontSize={
			"8" ,"9", "10","12","14","16","18",
			"20","24","28","32","36","40","44",
			"48","54","60","66","72","80","88"  
    };  
	private JLabel 		head_img_label,friend_name_label,signature_label;
	private JPanel 		panel_north,panel_south,panel_north_center;
	private JPanel 		panelUnderSplit,panelUnderSplit_UpPanel,panelUnderSplit_BottomPanel;
	private JButton 	send_button,close_button;
	private JTextArea 	inputTextArea,showTextArea;
	private ImageIcon 	head_img_icon;
	private FlowLayout 	flowLayout_Bottom;
	private JSplitPane 	splitPane;
	private FlowLayout 	flowLayout;
	private JScrollPane inputpanel,showPanel;
	private JComboBox	fontType;
	private JComboBox 	fontSize;
	private Font 		font;
	
	private JScrollBar 	sbar;
	private NetworkController nkc;

	public ChatWindow(Account myself,String parent_ID) {	
		this.friend_ID = myself.getID();
		this.friend_online = myself.getOnLine();
		this.friend_nickName = myself.getNikeName();
		this.friend_signature = myself.getSignature();
		this.mine_ID = parent_ID;
		
		this.setTitle(ChatWindow_TITLE);
		this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		SetNorthPane();
		SetSouthPane();
		
		this.setVisible(true);
	}
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
		friend_name_label.setText(this.friend_nickName);
		panel_north_center.add(friend_name_label,BorderLayout.CENTER);
		
		signature_label = new JLabel();
		signature_label.setText(this.friend_nickName);
		panel_north_center.add(signature_label,BorderLayout.SOUTH);
		this.add(panel_north,BorderLayout.NORTH);
	}
	
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
		//-----------ComboBox-------------
		/*
		menu_font_btn = new JButton();					
		menu_font_btn.setText("×Ö");
		*/
		GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		final String[] allFontType;
		allFontType = graphicsEnvironment.getAvailableFontFamilyNames();
		fontType = new JComboBox(allFontType);
		fontSize = new JComboBox(allFontSize);
		fontSize.setPreferredSize(new Dimension(80, 30));
		
		ActionListener fontListener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
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
		send_button.setText("·¢ËÍ");
		panelUnderSplit_BottomPanel.add(send_button);
		close_button = new JButton();
		close_button.setText("¹Ø±Õ");
		panelUnderSplit_BottomPanel.add(close_button);
		//-----------send_listener----------
		ActionListener send_listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(!inputTextArea.getText().equals("")){
					String sendStr = inputTextArea.getText()+"\n";
					showTextArea.append(sendStr);
					inputTextArea.setText("");
					sbar.setValue(sbar.getMaximum());
					sendMessageToServer(sendStr.substring(0,sendStr.length()-1));
					System.out.println("chatInfo: " + ChatWindow.this.mine_ID + " send ¡°" 
							+ sendStr + "¡± to " + ChatWindow.this.friend_ID);
				}
			}
		};
		
		KeyListener enter_Listener = new KeyListener() {
			public void keyTyped(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {}
			public void keyPressed(KeyEvent e) {
				if(e.getKeyChar() == KeyEvent.VK_ENTER && e.isControlDown())
				{
					if(!inputTextArea.getText().equals("")){
						String sendStr = inputTextArea.getText()+"\n";
						showTextArea.append(sendStr);
						inputTextArea.setText("");
						sendMessageToServer(sendStr.substring(0,sendStr.length()-1));
						System.out.println("chatInfo: " + ChatWindow.this.mine_ID + " send ¡°"
								+ sendStr + "¡± to " + ChatWindow.this.friend_ID);
					}
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
		inputTextArea.setLineWrap(true);				//set inputext line wrap
		
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
	
	private void sendMessageToServer(String message) {
		Envelope evp = new Envelope(this.friend_ID,this.mine_ID,message);
		NetworkController.sendEnvelope(evp);
	}
	
	public void sendMessageToShowtextfield(String Message)
	{
		showTextArea.append(Message+"\r\n");
	}
	
	private class closeChatWindowButton_Listener implements ActionListener{
		private JFrame jFrame;
		private JButton button;
		
		public closeChatWindowButton_Listener (JFrame jFrame) {
			this.jFrame = jFrame;
		}
		public void actionPerformed(ActionEvent e) {
			jFrame.dispose();
		}
	}
	
	private class closeChatWindowEseKey_Listener implements KeyListener{
		private JFrame jFrame;
		
		public closeChatWindowEseKey_Listener(JFrame jFrame) {
			this.jFrame = jFrame;
		}
		public void keyPressed(KeyEvent e) {}
		public void keyReleased(KeyEvent e) {}
		public void keyTyped(KeyEvent e) {
			if(e.getKeyChar() == KeyEvent.VK_ESCAPE)
			{
				jFrame.dispose();
			}
			
		}		
	}
}
