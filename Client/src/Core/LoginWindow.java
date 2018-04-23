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

import network.messageOperate.MessageOperate;
/**
 * 登陆窗口
 * @author Murrey
 * @version 2.1
 * 【添加】登陆进度条
 * @version 2.0
 * 【删减】部分冗余的成员属性
 * 【添加】类-登陆信息
 * 【修改】构造方法
 * 【完善】检查输入合法性的机制
 * 【添加】消息收发队列方法
 */
public class LoginWindow extends JFrame{
	private static final int 	i_window_width = 520;
	private static final int 	i_window_height = 400;
	private static LoginInfo	info_login;
	private static final int	magic_number = 1234;
		
	JButton 			btn_login;
	JPanel 				panel_bottom,panel_middle;
	JLabel 				label_top,label_head_image,label_register_new,label_forget_psw;
	ImageIcon			imageIcon_headImage;	
	JTextField 			text_field;
	JPasswordField 		password_field;
	JCheckBox 			cbox_remember,cbox_auto_login;	
	GridBagLayout 		gb_layout;
	GridBagConstraints 	gb_constraint;
	JProgressBar 		pbar_login;
	
	/**
	 * LoginWindow 构造函数
	 */
	public LoginWindow(LoginInfo info_login){
		
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
					Runtime.getRuntime().exec("cmd.exe /c start " + "http://39.108.95.130:9000/Registor");
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
		text_field.setText(info_login.getLoginYhm());
		password_field.setText(info_login.getLoginPsw());
		password_field.addKeyListener(new KeyListener(){
			public void keyTyped(KeyEvent e) {}
			public void keyPressed(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {
				if(e.getKeyChar() == KeyEvent.VK_ENTER ){
					if(checkLoginInfo()) 
						verifyInfoWithServer();
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
					verifyInfoWithServer();					
				else
					btn_login.setEnabled(true);
			}
		});
		
		/* 记住密码、自动登陆复选框设置 */
		cbox_remember = new JCheckBox("记住密码");
		cbox_remember.setSelected(info_login.isRememberBtn());
		cbox_remember.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(cbox_remember.isSelected()) LoginWindow.info_login.setRememberBtn(true);
				else LoginWindow.info_login.setRememberBtn(false);
			}
		});
		cbox_auto_login = new JCheckBox("自动登陆");
		cbox_auto_login.setSelected(info_login.isAutoBtn());
		cbox_auto_login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(cbox_auto_login.isSelected()) LoginWindow.info_login.setAutoBtn(true);
				else LoginWindow.info_login.setAutoBtn(false);
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

		/* 进度条 */
		pbar_login = new JProgressBar()
		{
		    public Dimension getPreferredSize() {
		    		return new Dimension(180, 17);
		    }
		};	
		pbar_login.setIndeterminate(true);
		pbar_login.setVisible(false);
		
		/* 面板添加  */
		panel_bottom.add(btn_login);
		panel_bottom.add(pbar_login);
		this.add(label_top,BorderLayout.NORTH);
		this.add(panel_bottom,BorderLayout.SOUTH);
		this.add(panel_middle,BorderLayout.CENTER);

		this.setAlwaysOnTop(false);
		this.setVisible(true);
	}
	
	/**
	 * 检查文本框内容合法性
	 */
	private boolean checkLoginInfo(){	
		String yhmInput = text_field.getText();
		String pswInput = new String(password_field.getPassword());
		
		boolean flag = true;
		if(yhmInput.equals("")){
			System.out.println("LoginError: yhm is empty.");
			JOptionPane.showMessageDialog(null, "用户名为空！请检查登陆信息！",
					"Error", JOptionPane.ERROR_MESSAGE);
			flag = false;
		}
		if(pswInput.equals("")){
			System.out.println("LoginError: psw is empty.");
			JOptionPane.showMessageDialog(null, "密码为空！请检查登陆信息！",
					"Error", JOptionPane.ERROR_MESSAGE);
			flag = false;
		}
		if(yhmInput.contains(" ")) {
			System.out.println("LoginError: yhm contains space.");
			JOptionPane.showMessageDialog(null, "用户名存在空格！请检查登陆信息！",
					"Error", JOptionPane.ERROR_MESSAGE);
			flag = false;
		}
		if(pswInput.contains(" ")) {
			System.out.println("LoginError: psw contains space.");
			JOptionPane.showMessageDialog(null, "密码存在空格！请检查登陆信息！",
					"Error", JOptionPane.ERROR_MESSAGE);
			flag = false;
		}
		if(flag) {
			info_login.setLoginYhm(yhmInput);
			info_login.setLoginPsw(pswInput);
			return true;
		}else 
			return false;
			
	}
	
	/**
	 * 与服务器交互 进行信息验证
	 */
	public void verifyInfoWithServer() {
		setWaitingStatus(true);
		/* 若不使用新线程会导致登陆窗口阻塞死屏 */
		Runnable rnb = ()->{
			if(!RecvSendController.connectToServer()) {
				System.out.println("Connection error.");
				setWaitingStatus(false);
			}else {
				System.out.println("Connection OK.");
				
				String yhm = info_login.getLoginYhm();
				String psw = info_login.getLoginPsw();
				
				System.out.println("LoginInfo: send to server. yhm: "+yhm);
				System.out.println("LoginInfo: send to server. psw: "+psw);
				try {
					Thread.sleep(magic_number);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				RecvSendController.addToSendQueue(MessageOperate.packageLoginMsg(yhm, psw));
			}
		};
		Thread thd = new Thread(rnb);
		thd.start();
	}

	/**
	 * 设置进度条是否显示 即是否进入登陆等待状态
	 * @param flag
	 */
	public void setWaitingStatus(boolean flag) {
		btn_login.setVisible(!flag);
		pbar_login.setVisible(flag);
	}
	
	/**************** setter and getter ****************/
	public String getYhm() {
		return info_login.getLoginYhm();
	}
	public String getPsw() {
		return info_login.getLoginPsw();
	}
	public boolean getRememberBtnFlag() {
		return info_login.isRememberBtn();
	}
	public boolean getAutoBtnFlag() {
		return info_login.isAutoBtn();
	}
	public static void setLoginInfoResource(String yhm,String psw,
		boolean rememberSelected,boolean autoLoginSelected) {
		info_login = new LoginInfo();
		info_login.setLoginYhm(yhm);
		info_login.setLoginPsw(psw);
		info_login.setRememberBtn(rememberSelected);
		info_login.setAutoBtn(autoLoginSelected);
	}
	public static LoginInfo getLoginInfoResource() {
		return info_login;
	}
}

/* 登陆信息类 用于记录窗口填入值 */
class LoginInfo{
	private boolean 			flag_rememberBtn;
	private boolean 			flag_autoBtn;
	private String 				str_login_yhm;
	private String				str_login_psw;
	
	public LoginInfo() {
		this.setLoginYhm(null);
		this.setLoginPsw(null);
		this.setAutoBtn(false);
		this.setRememberBtn(false);
	}

	public boolean isRememberBtn() {
		return flag_rememberBtn;
	}

	public void setRememberBtn(boolean flag_rememberBtn) {
		this.flag_rememberBtn = flag_rememberBtn;
	}

	public boolean isAutoBtn() {
		return flag_autoBtn;
	}

	public void setAutoBtn(boolean flag_autoBtn) {
		this.flag_autoBtn = flag_autoBtn;
	}

	public String getLoginYhm() {
		return str_login_yhm;
	}

	public void setLoginYhm(String str_login_yhm) {
		this.str_login_yhm = str_login_yhm;
	}

	public String getLoginPsw() {
		return str_login_psw;
	}

	public void setLoginPsw(String str_login_psw) {
		this.str_login_psw = str_login_psw;
	}
}
