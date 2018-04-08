import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class LoginWindow extends JFrame{
	private int i_window_width = 520;
	private int i_window_height = 400;
	private int i_screen_width;
	private int i_screen_height;
	private int i_loc_X;
	private int i_loc_Y;
	private boolean enter_flag = false;
	
	JButton login_button;
	
	JPanel bottom_panel,middle_panel;
	
	JLabel top_label,head_image_label,register_label,forget_label;
	
	ImageIcon top_image_icon,head_image_icon;
	
	JTextField text_field;
	JPasswordField password_field;
	JCheckBox cbox_remember,cbox_auto_login;
	
	GridBagLayout gb_layout;
	GridBagConstraints gb_constraint;
	
	public static void imgCircular() throws IOException {
        BufferedImage bi1 = ImageIO.read(new File("image/02.png"));
        // 根据需要是否使用 BufferedImage.TYPE_INT_ARGB
        BufferedImage bi2 = new BufferedImage(bi1.getWidth(), bi1.getHeight(),
                BufferedImage.TYPE_INT_RGB);
        Ellipse2D.Double shape = new Ellipse2D.Double(0, 0, bi1.getWidth(), bi1
                .getHeight());
        Graphics2D g2 = bi2.createGraphics();
        g2.setBackground(Color.WHITE);
        g2.fill(new Rectangle(bi2.getWidth(), bi2.getHeight()));
        g2.setClip(shape);
        // 使用 setRenderingHint 设置抗锯齿
        g2.drawImage(bi1, 0, 0, null);
        g2.dispose();
 
        try {
            ImageIO.write(bi2, "jpg", new File("image/01.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	protected void setWindowToScreenCenter(){
		Toolkit kit=Toolkit.getDefaultToolkit();
		Dimension screenSize=kit.getScreenSize();
		i_screen_width = screenSize.width;
		i_screen_height = screenSize.height;
		i_loc_X = (i_screen_width - i_window_width)/2;
		i_loc_Y = (i_screen_height - i_window_height)/2;
		this.setLocation(i_loc_X,i_loc_Y);
	}
	private boolean verifyInfoWithServer(String yhm,String psw){
		System.out.println("向服务器发送用户名："+yhm);
		System.out.println("向服务器发送用户密码："+psw);
		return true;
	}
	public boolean getEnterFlag(){
		System.out.print("");		// 用于抢占CPU
		return this.enter_flag;
	}
	private void loginVerify(){
		String yhm = text_field.getText();
		String psw = new String(password_field.getPassword());
		boolean verifyRes = false;
		if(yhm.equals("")){
			System.out.println("用户名为空！");
		}else if(psw.equals("")){
			System.out.println("密码为空！");
		}else{
			verifyRes = verifyInfoWithServer(yhm,psw);
		}
		if(verifyRes){
			dispose();
			enter_flag = true;
		}
	}
	public LoginWindow(){
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setIconImage((new ImageIcon("image/chick.png")).getImage());
		this.setSize(i_window_width,i_window_height);
		this.setResizable(false);
		this.setTitle("KIM");
		
		/* 获取屏幕尺寸 并居中窗口到屏幕 */
		setWindowToScreenCenter();
		
		/* ImageIcon图片设置 */
		top_image_icon = new ImageIcon("image/01.png");
		head_image_icon = new ImageIcon("image/01.jpg");
		
		/* Label标签设置 */
		top_label = new JLabel();
		top_label.setIcon(top_image_icon);
		top_label.setPreferredSize(new Dimension(i_window_width,i_window_height/2-50));		//bias:50
		head_image_label = new JLabel();
		head_image_label.setIcon(head_image_icon);
		register_label = new JLabel("<html><a href='#'>注册新账号</a></html>"); 
		register_label.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				try {
					Runtime.getRuntime().exec("cmd.exe /c start " + "https://ssl.zc.qq.com/");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		forget_label = new JLabel("<html><a href='#'>找回密码</a></html>");
		forget_label.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				try {
					Runtime.getRuntime().exec("cmd.exe /c start " + "https://aq.qq.com/");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		register_label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		forget_label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		//head_image_label.setPreferredSize(new Dimension(50,50));
			
		/* Button按钮设置 */
		login_button = new JButton("登  陆");	login_button.setPreferredSize(new Dimension(i_window_width/3,30));
		login_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loginVerify();
			}
		});
		cbox_remember = new JCheckBox("记住密码");
		cbox_auto_login = new JCheckBox("自动登陆");
		
		/* 创建和设置面板对象 */
		bottom_panel = new JPanel();  //bottom_panel.setLayout();
		middle_panel = new JPanel();  //middle_panel.setLayout(null);
		
		text_field = new JTextField(10);
		
		password_field = new JPasswordField(10);
		password_field.addKeyListener(new KeyListener(){
			public void keyTyped(KeyEvent e) {}
			public void keyPressed(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {
				if(e.getKeyChar() == KeyEvent.VK_ENTER )
					loginVerify();
			}
		});
				
		/* 面板添加 */
		bottom_panel.add(login_button); //bottom_panel.add(register_button);
		
		gb_layout = new GridBagLayout();
		gb_constraint = new GridBagConstraints();
		Insets ins = new Insets(0,0,0,0);
		gb_constraint.fill = GridBagConstraints.BOTH;
		gb_constraint.weightx = 1.0;
		gb_constraint.weighty = 1.0;
		
		/* 面板设置 */
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
		gb_layout.setConstraints(register_label, gb_constraint);
		middle_panel.add(register_label);

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
		gb_layout.setConstraints(forget_label, gb_constraint);
		middle_panel.add(forget_label);

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
		
		this.setAlwaysOnTop(false);
		this.add(top_label,BorderLayout.NORTH);
		this.add(bottom_panel,BorderLayout.SOUTH);
		this.add(middle_panel,BorderLayout.CENTER);
		
		this.setVisible(true);
	}
}
