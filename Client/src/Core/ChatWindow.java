package Core;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class ChatWindow extends JFrame{
	
	private static final String ChatWindow_TITLE = "Chating";
	private static final int WINDOW_WIDTH = 935;
	private static final int WINDOW_HEIGHT = 680;
	private JLabel 		head_img_label,friend_name_label,signature_label;
	private JPanel 		panel_north,panel_south,panel_north_center;
	private JPanel 		panelUnderSplit,panelUnderSplit_UpPanel,panelUnderSplit_BottomPanel;
	private JButton 	menu_font_btn,send_button,close_button;
	private JTextArea 	inputTextArea,showTextArea;
	private ImageIcon 	head_img_icon;
	private FlowLayout 	flowLayout_Bottom;
	private JSplitPane 	splitPane;
	private FlowLayout 	flowLayout;
	private JScrollPane scrollPane,inputPanel;
	
	public ChatWindow(Object oj) {		
		this.setTitle(ChatWindow_TITLE);
		this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		//this.setDefaultLookAndFeelDecorated(true);
		
		SetNorthPane(oj);
		SetSouthPane();
		
		this.setVisible(true);
		
	}
	private void SetNorthPane(Object oj){
		String contactOject = (String)oj;
		
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
		friend_name_label.setText(contactOject);
		panel_north_center.add(friend_name_label,BorderLayout.CENTER);
		
		signature_label = new JLabel();
		signature_label.setText("Never waste time any more");
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
		//-----------Button-------------
		menu_font_btn = new JButton();					
		menu_font_btn.setText("×ÖÌå");
		panelUnderSplit_UpPanel.add(menu_font_btn);
		//------------------------------
		panelUnderSplit.add(panelUnderSplit_UpPanel,BorderLayout.NORTH);
		
		scrollPane = new JScrollPane();
		panelUnderSplit.add(scrollPane,BorderLayout.CENTER);
				
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
				showTextArea.append(inputTextArea.getText()+"\r\n");
				showTextArea.setLineWrap(true);
				inputTextArea.setText("");
			}
		};
		ActionListener close_listener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				
			};
		};
		//--------------------------------
		send_button.addActionListener(send_listener);
		close_button.addActionListener(close_listener);
		 
		panelUnderSplit.add(panelUnderSplit_BottomPanel,BorderLayout.SOUTH);

		inputTextArea = new JTextArea();
		
		scrollPane.setViewportView(inputTextArea);
		
		inputPanel = new JScrollPane();
		splitPane.setLeftComponent(inputPanel);
		
		showTextArea = new JTextArea();
		showTextArea.setEditable(false);
		inputPanel.setViewportView(showTextArea);
		
		this.add(panel_south,BorderLayout.CENTER);
	}
}