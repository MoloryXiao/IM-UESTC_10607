package Core;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.*;
/**
 * 登陆窗口
 * @author Murrey
 * @version 1.0
 */
public class LoginWindow extends JFrame{
	private static final int 	i_window_width = 520;
	private static final int 	i_window_height = 400;
	private boolean 			flag_connectServer = false;
	private boolean 			flag_rememberBtn = false;
	private boolean 			flag_autoBtn = false;
	private String 				str_login_yhm=null;
	private String				str_login_psw=null;
		
	JButton 			btn_login;
	JPanel 				panel_bottom,panel_middle;
	JLabel 				label_top,label_head_image,label_register_new,label_forget_psw;
	ImageIcon			imageIcon_headImage;	
	JTextField 			text_field;
	JPasswordField 		password_field;
	JCheckBox 			cbox_remember,cbox_auto_login;	
	GridBagLayout 		gb_layout;
	GridBagConstraints 	gb_constraint;
	
	/**
	 * LoginWindow 构造函数
	 */
	public LoginWindow(String yhm,String psw,boolean rememberSelected,boolean autoLoginSelected){
		this.str_login_yhm = yhm;
		this.str_login_psw = psw;
		this.flag_rememberBtn = rememberSelected;
		this.flag_autoBtn = autoLoginSelected;
		
		this.setTitle("KIM");
		this.setSize(i_window_width,i_window_height);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setIconImage((new ImageIcon("image/chick.png")).getImage());
		
		/* 头像图片设置 */
		imageIcon_headImage = new ImageIcon("image/loginHeadImage.jpg");
		
		/* Banner标签设置 */
		label_top = new JLabel();
		label_top.setIcon(new ImageIcon("image/banner.png"));
		label_top.setPreferredSize(new Dimension(i_window_width,i_window_height/2-50));		//bias:50
		label_head_image = new JLabel();
		label_head_image.setIcon(imageIcon_headImage);
		
		/* 注册新账号按钮 */
		label_register_new = new JLabel("<html><a href='#'>注册新账号</a></html>"); 
		label_register_new.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		label_register_new.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				try {
					Runtime.getRuntime().exec("cmd.exe /c start " + "http://39.108.95.130:9000/Register");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		/* 忘记密码按钮 */
		label_forget_psw = new JLabel("<html><a href='#'>找回密码</a></html>");
		label_forget_psw.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		label_forget_psw.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				try {
					Runtime.getRuntime().exec("cmd.exe /c start " + "http://39.108.95.130:9000/FindPasswd");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		/* 文本框和密码框设置 */
		panel_bottom = new JPanel();  
		panel_middle = new JPanel();  
		text_field = new JTextField(10);
		password_field = new JPasswordField(10);
		text_field.setText(this.str_login_yhm);
		password_field.setText(this.str_login_psw);
		password_field.addKeyListener(new KeyListener(){
			public void keyTyped(KeyEvent e) {}
			public void keyPressed(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {
				if(e.getKeyChar() == KeyEvent.VK_ENTER ){
					if(checkLoginInfo()) 
						LoginWindow.this.flag_connectServer = true;
					else
						LoginWindow.this.btn_login.setEnabled(true);
				}
			}
		});
		
		/* 登录按钮设置 */
		btn_login = new JButton("登  陆");
		btn_login.setPreferredSize(new Dimension(i_window_width/3,30));
		btn_login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(checkLoginInfo()) 
					flag_connectServer = true;
				else
					btn_login.setEnabled(true);
			}
		});
		
		/* 记住密码、自动登陆复选框设置 */
		cbox_remember = new JCheckBox("记住密码");
		cbox_remember.setSelected(this.flag_rememberBtn);
		cbox_remember.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(cbox_remember.isSelected()) flag_rememberBtn = true;
				else flag_rememberBtn = false;
			}
		});
		cbox_auto_login = new JCheckBox("自动登陆");
		cbox_auto_login.setSelected(this.flag_autoBtn);
		cbox_auto_login.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(cbox_auto_login.isSelected()) flag_autoBtn = true;
				else flag_autoBtn = false;
			}
		});
				
		/* GridBag 布局 */
		gb_layout = new GridBagLayout();
		gb_constraint = new GridBagConstraints();
		Insets ins = new Insets(0,0,0,0);
		gb_constraint.fill = GridBagConstraints.BOTH;
		gb_constraint.weightx = 1.0;
		gb_constraint.weighty = 1.0;
		
		panel_middle.setLayout(gb_layout);

		gb_constraint.gridx = 0; gb_constraint.gridy = 0;				// 头像
		gb_constraint.gridheight = 3; gb_constraint.gridwidth = 3;		
		ins.set(0, 30, 0, 0);	
		gb_constraint.insets = ins;
		gb_layout.setConstraints(label_head_image, gb_constraint);
		panel_middle.add(label_head_image);

		gb_constraint.gridx = 3; gb_constraint.gridy = 0;				// 文本框
		gb_constraint.gridheight = 1; gb_constraint.gridwidth = 6;		
		ins.set(20, 0, 0, 0);
		gb_constraint.insets = ins;
		gb_layout.setConstraints(text_field, gb_constraint);
		panel_middle.add(text_field);

		gb_constraint.gridx = 9; gb_constraint.gridy = 0;				// 注册新账号
		gb_constraint.gridheight = 1; gb_constraint.gridwidth = 2;		
		ins.set(15, 30, 0, 0);
		gb_constraint.insets = ins;
		gb_layout.setConstraints(label_register_new, gb_constraint);
		panel_middle.add(label_register_new);

		gb_constraint.gridx = 3; gb_constraint.gridy = 1;				// 密码框
		gb_constraint.gridheight = 1; gb_constraint.gridwidth = 6;		
		ins.set(0, 0, 0, 0);
		gb_constraint.insets = ins;
		gb_layout.setConstraints(password_field, gb_constraint);
		panel_middle.add(password_field);

		gb_constraint.gridx = 9; gb_constraint.gridy = 1;				// 忘记密码
		gb_constraint.gridheight = 1; gb_constraint.gridwidth = 2;		
		ins.set(0, 30, 0, 0);
		gb_constraint.insets = ins;
		gb_layout.setConstraints(label_forget_psw, gb_constraint);
		panel_middle.add(label_forget_psw);

		gb_constraint.gridx = 3; gb_constraint.gridy = 2;				// 记住密码
		gb_constraint.gridheight = 1; gb_constraint.gridwidth = 3;		
		ins.set(0, 0, 0, 0);
		gb_constraint.insets = ins;
		gb_layout.setConstraints(cbox_remember, gb_constraint);
		panel_middle.add(cbox_remember);

		gb_constraint.gridx = 6; gb_constraint.gridy = 2;				// 自动登陆
		gb_constraint.gridheight = 1; gb_constraint.gridwidth = 3;		
		ins.set(0, 0, 0, 0);
		gb_constraint.insets = ins;
		gb_layout.setConstraints(cbox_auto_login, gb_constraint);
		panel_middle.add(cbox_auto_login);

		/* 面板添加  */
		panel_bottom.add(btn_login);
		this.add(label_top,BorderLayout.NORTH);
		this.add(panel_bottom,BorderLayout.SOUTH);
		this.add(panel_middle,BorderLayout.CENTER);

		this.setAlwaysOnTop(false);
		this.setVisible(true);
	}
	
	/**
	 * 对文本框进行检查 符合后进行登陆验证
	 */
	private boolean checkLoginInfo(){		
		str_login_yhm = text_field.getText();
		str_login_psw = new String(password_field.getPassword());
		if(str_login_yhm.equals("")){
			System.out.println("LoginError: yhm is empty.");
			JOptionPane.showMessageDialog(null, "用户名为空！请检查登陆信息！",
					"Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if(str_login_psw.equals("")){
			System.out.println("LoginError: psw is empty.");
			JOptionPane.showMessageDialog(null, "密码为空！请检查登陆信息！",
					"Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}
	
	/**************** setter and getter ****************/	
	public boolean isConnectServer(){
		System.out.print("");
		return this.flag_connectServer;
	}
	public String getYhm(){
		return this.str_login_yhm;
	}
	public String getPsw(){
		return this.str_login_psw;
	}
	public void setLoginButtonStatus(boolean status){
		this.btn_login.setEnabled(status);
	}
	public void setConnectFlag(boolean status){
		this.flag_connectServer = status;
	}
	public boolean getRememberBtnFlag(){
		return this.flag_rememberBtn;
	}
	public boolean getAutoBtnFlag(){
		return this.flag_autoBtn;
	}
}
