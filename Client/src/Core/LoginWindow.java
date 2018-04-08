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
import Network_Client.*;

public class LoginWindow extends JFrame{
	private static final int i_window_width = 520;
	private static final int i_window_height = 400;
	private boolean connect_server_flag = false;
	private boolean remember_btn_flag = false;
	private boolean auto_btn_flag = false;
	private String login_yhm=null,login_psw=null;
		
	JButton 			login_button;
	JPanel 				bottom_panel,middle_panel;
	JLabel 				top_label,head_image_label,register_new_label,forget_psw_label;
	ImageIcon			head_image_icon;	
	JTextField 			text_field;
	JPasswordField 		password_field;
	JCheckBox 			cbox_remember,cbox_auto_login;	
	GridBagLayout 		gb_layout;
	GridBagConstraints 	gb_constraint;
	
	/**
	 * LoginWindow 构造函数
	 */
	public LoginWindow(String yhm,String psw,boolean rememberSelected,boolean autoLoginSelected){
		this.login_yhm = yhm;
		this.login_psw = psw;
		this.remember_btn_flag = rememberSelected;
		this.auto_btn_flag = autoLoginSelected;
		
		this.setTitle("KIM");
		this.setSize(i_window_width,i_window_height);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setIconImage((new ImageIcon("image/chick.png")).getImage());
		
		/* ImageIcon图片设置 */
		head_image_icon = new ImageIcon("image/loginHeadImage.jpg");
		
		/* Label标签设置 */
		top_label = new JLabel();
		top_label.setIcon(new ImageIcon("image/banner.png"));
		top_label.setPreferredSize(new Dimension(i_window_width,i_window_height/2-50));		//bias:50
		head_image_label = new JLabel();
		head_image_label.setIcon(head_image_icon);
		/* 注册新账号按钮跳转 */
		register_new_label = new JLabel("<html><a href='#'>注册新账号</a></html>"); 
		register_new_label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		register_new_label.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				try {
					Runtime.getRuntime().exec("cmd.exe /c start " + "http://39.108.95.130:9000/JavaWeb");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		/* 忘记密码按钮跳转 */
		forget_psw_label = new JLabel("<html><a href='#'>找回密码</a></html>");
		forget_psw_label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		forget_psw_label.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				try {
					Runtime.getRuntime().exec("cmd.exe /c start " + "http://39.108.95.130:9000/FindPasswd");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		/* 创建和设置面板对象 */
		bottom_panel = new JPanel();  
		middle_panel = new JPanel();  
		text_field = new JTextField(10);
		password_field = new JPasswordField(10);
		text_field.setText(this.login_yhm);
		password_field.setText(this.login_psw);
		password_field.addKeyListener(new KeyListener(){
			public void keyTyped(KeyEvent e) {}
			public void keyPressed(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {
				if(e.getKeyChar() == KeyEvent.VK_ENTER ){
					login_button.setEnabled(false);		// 禁用按钮
					if(checkLoginInfo()) 
						connect_server_flag = true;
					else
						login_button.setEnabled(true);
				}
			}
		});
		
		/* Button按钮设置 */
		login_button = new JButton("登  陆");
		login_button.setPreferredSize(new Dimension(i_window_width/3,30));
		login_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				login_button.setEnabled(false);		// 禁用按钮
				if(checkLoginInfo()) 
					connect_server_flag = true;
				else
					login_button.setEnabled(true);
			}
		});
		cbox_remember = new JCheckBox("记住密码");
		cbox_remember.setSelected(this.remember_btn_flag);
		cbox_remember.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(cbox_remember.isSelected()) remember_btn_flag = true;
				else remember_btn_flag = false;
			}
		});
		cbox_auto_login = new JCheckBox("自动登陆");
		cbox_auto_login.setSelected(this.auto_btn_flag);
		cbox_auto_login.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(cbox_auto_login.isSelected()) auto_btn_flag = true;
				else auto_btn_flag = false;
			}
		});
				
		/* GridBag 布局 */
		gb_layout = new GridBagLayout();
		gb_constraint = new GridBagConstraints();
		Insets ins = new Insets(0,0,0,0);
		gb_constraint.fill = GridBagConstraints.BOTH;
		gb_constraint.weightx = 1.0;
		gb_constraint.weighty = 1.0;
		
		middle_panel.setLayout(gb_layout);

		gb_constraint.gridx = 0; gb_constraint.gridy = 0;				// 头像
		gb_constraint.gridheight = 3; gb_constraint.gridwidth = 3;		
		ins.set(0, 30, 0, 0);	
		gb_constraint.insets = ins;
		gb_layout.setConstraints(head_image_label, gb_constraint);
		middle_panel.add(head_image_label);

		gb_constraint.gridx = 3; gb_constraint.gridy = 0;				// 文本框
		gb_constraint.gridheight = 1; gb_constraint.gridwidth = 6;		
		ins.set(20, 0, 0, 0);
		gb_constraint.insets = ins;
		gb_layout.setConstraints(text_field, gb_constraint);
		middle_panel.add(text_field);

		gb_constraint.gridx = 9; gb_constraint.gridy = 0;				// 注册新账号
		gb_constraint.gridheight = 1; gb_constraint.gridwidth = 2;		
		ins.set(15, 30, 0, 0);
		gb_constraint.insets = ins;
		gb_layout.setConstraints(register_new_label, gb_constraint);
		middle_panel.add(register_new_label);

		gb_constraint.gridx = 3; gb_constraint.gridy = 1;				// 密码框
		gb_constraint.gridheight = 1; gb_constraint.gridwidth = 6;		
		ins.set(0, 0, 0, 0);
		gb_constraint.insets = ins;
		gb_layout.setConstraints(password_field, gb_constraint);
		middle_panel.add(password_field);

		gb_constraint.gridx = 9; gb_constraint.gridy = 1;				// 忘记密码
		gb_constraint.gridheight = 1; gb_constraint.gridwidth = 2;		
		ins.set(0, 30, 0, 0);
		gb_constraint.insets = ins;
		gb_layout.setConstraints(forget_psw_label, gb_constraint);
		middle_panel.add(forget_psw_label);

		gb_constraint.gridx = 3; gb_constraint.gridy = 2;				// 记住密码
		gb_constraint.gridheight = 1; gb_constraint.gridwidth = 3;		
		ins.set(0, 0, 0, 0);
		gb_constraint.insets = ins;
		gb_layout.setConstraints(cbox_remember, gb_constraint);
		middle_panel.add(cbox_remember);

		gb_constraint.gridx = 6; gb_constraint.gridy = 2;				// 自动登陆
		gb_constraint.gridheight = 1; gb_constraint.gridwidth = 3;		
		ins.set(0, 0, 0, 0);
		gb_constraint.insets = ins;
		gb_layout.setConstraints(cbox_auto_login, gb_constraint);
		middle_panel.add(cbox_auto_login);

		/* 面板添加  */
		bottom_panel.add(login_button);
		this.add(top_label,BorderLayout.NORTH);
		this.add(bottom_panel,BorderLayout.SOUTH);
		this.add(middle_panel,BorderLayout.CENTER);

		this.setAlwaysOnTop(false);
		this.setVisible(true);
	}
	
	public boolean isConnectServer(){
		System.out.print("");
		return this.connect_server_flag;
	}
	public String getYhm(){
		return this.login_yhm;
	}
	public String getPsw(){
		return this.login_psw;
	}
	public void setLoginButtonStatus(boolean status){
		login_button.setEnabled(status);
	}
	public void setConnectFlag(boolean status){
		this.connect_server_flag = status;
	}
	public boolean getRememberBtnFlag(){
		return this.remember_btn_flag;
	}
	public boolean getAutoBtnFlag(){
		return this.auto_btn_flag;
	}
	
	/**
	 * 对文本框进行检查 符合后进行登陆验证
	 */
	private boolean checkLoginInfo(){		
		login_yhm = text_field.getText();
		login_psw = new String(password_field.getPassword());
		if(login_yhm.equals("")){
			System.out.println("LoginError: yhm is empty.");
			JOptionPane.showMessageDialog(null, "用户名为空！请检查登陆信息！",
					"Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if(login_psw.equals("")){
			System.out.println("LoginError: psw is empty.");
			JOptionPane.showMessageDialog(null, "密码为空！请检查登陆信息！",
					"Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}
	
	
}
