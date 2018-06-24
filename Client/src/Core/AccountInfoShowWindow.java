package Core;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import network.commonClass.Account;
import network.commonClass.Picture;
/**
 * 账户信息显示窗口
 * @author Murrey
 * @version 1.1/0619
 * 【优化】部分变量和输出
 * 【添加】更新窗口内信息的接口
 * @version 1.0/0618
 * Init.
 */
public class AccountInfoShowWindow extends JFrame{
	private int i_window_width = 350;
	private int i_window_height = 550;
	
	private Account accountShow;
	private boolean modifyPermission;
	
	private JPanel panel_north,panel_center,panel_south;
	private JLabel label_headImage;
	private JLabel label_title,label_nickName,label_userID,label_phone,
		label_mail,label_level,label_location,label_person,label_signature;
	private JButton btn_edit,btn_exit;	
	
	public AccountInfoShowWindow(Account account,boolean modify){
		this.accountShow = account.clone();
		this.modifyPermission = modify;
		
		this.setTitle("KIM Self-Info");
		this.setSize(i_window_width,i_window_height);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setIconImage((new ImageIcon("image/chick.png")).getImage());
		
		// 北部
		/* 头像图片设置 */
		panel_north = new JPanel();
		BoxLayout layout_box1 = new BoxLayout(panel_north, BoxLayout.Y_AXIS);
		panel_north.setLayout(layout_box1);
//		imageIcon_headImage = new ImageIcon("image/level_v3.png");
//		Image newImage = imageIcon_headImage.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
//		label_headImage.setIcon(new ImageIcon(newImage));
		Picture pic_show = this.accountShow.getPicture().clone();
		pic_show.reduceImage(100, 100);
		label_headImage = new JLabel();
		label_headImage.setIcon(new ImageIcon(pic_show.getPictureBytes()));
		
		/* 加粗昵称标题 */
		label_title = new JLabel(this.accountShow.getNikeName());
		label_title.setFont(new Font("微软雅黑",Font.BOLD, 20));
		
		/* 居中处理 */
		label_headImage.setAlignmentX(Component.CENTER_ALIGNMENT);
		label_title.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		/* 设置间距 */
		label_headImage.setBorder(new CompoundBorder(label_headImage.getBorder(),new EmptyBorder(10,0,0,0)));
		label_title.setBorder(new CompoundBorder(label_title.getBorder(),new EmptyBorder(10,0,0,0)));
				
		/* 北部布局 */
		panel_north.add(label_headImage);
		panel_north.add(label_title);
		this.add(panel_north,BorderLayout.NORTH);
		
		// 中部
		panel_center = new JPanel();
		BoxLayout layout_box2 = new BoxLayout(panel_center, BoxLayout.Y_AXIS);
		panel_center.setLayout(layout_box2);
		
		/* 设置内容 */
		label_userID = new JLabel("账号：  " + this.accountShow.getId());
		label_nickName = new JLabel("昵称：  " + this.accountShow.getNikeName());
		label_phone = new JLabel("手机：  " + this.accountShow.getMobliePhone());
		label_mail = new JLabel("邮箱：  " + this.accountShow.getMail());
		label_level = new JLabel("等级：  " + this.accountShow.getStage());	// ☆☆☆☆☆
		label_person = new JLabel("年龄：  " + this.accountShow.getOld() + "岁 " ); //+ this.accountShow.isMale());
		label_location = new JLabel("归属：  " + this.accountShow.getHome());
		label_signature = new JLabel("个签：  " + this.accountShow.getSignature());
		
		/* 设置字体 */
		label_userID.setFont(new Font("宋体",Font.PLAIN, 13));
		label_nickName.setFont(new Font("宋体",Font.PLAIN, 13));
		label_phone.setFont(new Font("宋体",Font.PLAIN, 13));
		label_mail.setFont(new Font("宋体",Font.PLAIN, 13));
		label_level.setFont(new Font("宋体",Font.PLAIN, 13));
		label_person.setFont(new Font("宋体",Font.PLAIN, 13));	
		label_location.setFont(new Font("宋体",Font.PLAIN, 13));		
		label_signature.setFont(new Font("宋体",Font.PLAIN, 13));		
		
		/* 居中处理 */
		label_userID.setAlignmentX(Component.LEFT_ALIGNMENT);
		label_nickName.setAlignmentX(Component.LEFT_ALIGNMENT);
		label_phone.setAlignmentX(Component.LEFT_ALIGNMENT);
		label_mail.setAlignmentX(Component.LEFT_ALIGNMENT);		
		label_level.setAlignmentX(Component.LEFT_ALIGNMENT);
		label_person.setAlignmentX(Component.LEFT_ALIGNMENT);
		label_location.setAlignmentX(Component.LEFT_ALIGNMENT);
		label_signature.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		/* 设置间距 */
		label_userID.setBorder(new CompoundBorder(label_userID.getBorder(),new EmptyBorder(20,55,0,0)));
		label_nickName.setBorder(new CompoundBorder(label_nickName.getBorder(),new EmptyBorder(10,55,0,0)));
		label_phone.setBorder(new CompoundBorder(label_phone.getBorder(),new EmptyBorder(10,55,0,0)));
		label_mail.setBorder(new CompoundBorder(label_mail.getBorder(),new EmptyBorder(10,55,0,0)));		
		label_level.setBorder(new CompoundBorder(label_level.getBorder(),new EmptyBorder(10,55,0,0)));
		label_person.setBorder(new CompoundBorder(label_person.getBorder(),new EmptyBorder(10,55,0,0)));
		label_location.setBorder(new CompoundBorder(label_location.getBorder(),new EmptyBorder(10,55,0,0)));
		label_signature.setBorder(new CompoundBorder(label_signature.getBorder(),new EmptyBorder(10,55,0,0)));
		
		/* 中部布局 */
		panel_center.add(label_userID);
		panel_center.add(label_nickName);
		panel_center.add(label_phone);
		panel_center.add(label_mail);
		panel_center.add(label_level);
		panel_center.add(label_person);
		panel_center.add(label_location);
		panel_center.add(label_signature);
		this.add(panel_center, BorderLayout.CENTER);
		
		/* 底部布局 */
		panel_south = new JPanel();
		btn_edit = new JButton("编辑");
		btn_edit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				WindowProducer.addWindowRequest(WindowProducer.INFO_MODIFY_WIND);
			}
		});	
		btn_exit = new JButton("关闭");
		btn_exit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				AccountInfoShowWindow.this.dispose();
			}
		});	
		FlowLayout flowLayout_Bottom = new FlowLayout();
		flowLayout_Bottom.setAlignment(FlowLayout.RIGHT);	// 居右
		panel_south.setLayout(flowLayout_Bottom);
		
		if(this.modifyPermission != false)		// 若显示的是好友账户  则不允许修改
			panel_south.add(btn_edit);
		
		panel_south.add(btn_exit);		
		this.add(panel_south, BorderLayout.SOUTH);
				
		this.setAlwaysOnTop(false);
		this.setVisible(true);
	}
	
	/**
	 * 更新窗口内信息 用于将修改后的信息重新赋值到窗口里
	 * @param account
	 */
	public void updateAccountInfo(Account account) {
		Picture pic_temp = account.getPicture().clone();
		pic_temp.reduceImage(100, 100);
		label_headImage.setIcon(new ImageIcon(pic_temp.getPictureBytes()));
		label_title.setText(account.getNikeName());
		label_userID.setText("账号：  " + account.getId());
		label_nickName.setText("昵称：  " + account.getNikeName());
		label_phone.setText("手机：  " + account.getMobliePhone());
		label_mail.setText("邮箱：  " + account.getMail());
		label_level.setText("等级：  " + account.getStage()+" 级");
		label_person.setText("个人：  " + account.getOld()+" 岁");
		label_location.setText("归属：  " + account.getHome());;
		label_signature.setText("个签：  " + account.getSignature());;
	}
	
}