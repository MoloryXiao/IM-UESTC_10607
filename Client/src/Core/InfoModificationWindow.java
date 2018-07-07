package Core;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import network.Builder.AccountBuilder;
import network.commonClass.Account;
import network.commonClass.Picture;
import network.messageOperate.MessageOperate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
/**
 * 信息修改窗口 用于修改个人信息
 * @author Murrey
 * @version 2.0/0619
 * 【链接】工程代码 将常量改为变量
 * 【优化】上传图片功能和拖拽功能 并保存到本地和上传到服务器
 * 【优化】头像显示
 * 【添加】完成按钮的事件处理
 * @version 1.0/0618
 * Init.
 */
public class InfoModificationWindow extends JFrame{
	private int i_window_width = 400;
	private int i_window_height = 615;
	private int userName = 122392319;
	private Account account_modify;
	
	private boolean isImageModified = false;
	
	private JLabel label_headImage,label_loadImage;
	private JPanel panel_north,panel_south;
	private JPanel panel_nickName,panel_userName,panel_phone,
		panel_mail,panel_location,panel_person,panel_signature,panel_headImage;
	private JLabel label_nickName,label_userName,label_phone,
		label_mail,label_location,label_person,label_signature;
	private JButton btn_exec,btn_ok;	
	private JButton btn_upload;
	private Picture pic_upload;
	
	private JTextField textField_nickName,textField_userName,
		textField_phone,textField_mail,textField_location,
		textField_person,textField_signature;
	
	public InfoModificationWindow(AccountInfoShowWindow wind_father,Account account) {
		this.account_modify = account.clone();
		
		this.setTitle("KIM Info-Modification");
		this.setSize(i_window_width,i_window_height);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setIconImage((new ImageIcon("image/chick.png")).getImage());
		
		// 北部容器
		panel_north = new JPanel();		
		panel_north.setLayout(new BoxLayout(panel_north, BoxLayout.Y_AXIS));	// 设置北部容器 Y 轴线布局

		panel_userName = new JPanel();		// 用户名面板：包含用户Label
		label_userName = new JLabel("帐 号：");
		textField_userName = new JTextField(20); 
		textField_userName.setEnabled(false);
		textField_userName.setText(this.account_modify.getId());
		panel_userName.setPreferredSize(new Dimension(i_window_width,12));		// 设置panel大小
		panel_userName.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));	// 设置间距
		
		panel_nickName = new JPanel();		// 昵称面板：包含昵称Label、昵称TextField
		label_nickName = new JLabel("昵 称：");
		textField_nickName = new JTextField(20);
		textField_nickName.setText(this.account_modify.getNikeName());
		panel_nickName.setPreferredSize(new Dimension(i_window_width,12));		// 设置panel大小
		panel_nickName.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
		
		panel_phone = new JPanel();			// 手机号码面板：包含手机Label、手机TextField
		label_phone = new JLabel("手 机：");
		textField_phone = new JTextField(20);
		textField_phone.setText(this.account_modify.getMobliePhone());
		panel_phone.setPreferredSize(new Dimension(i_window_width,12));		// 设置panel大小
		panel_phone.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
		
		panel_mail = new JPanel();			// 邮箱面板：包含邮箱Label、邮箱TextField
		label_mail = new JLabel("邮 箱：");	
		textField_mail = new JTextField(20);
		textField_mail.setText(this.account_modify.getMail());
		panel_mail.setPreferredSize(new Dimension(i_window_width,12));		// 设置panel大小
		panel_mail.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
		
		panel_location = new JPanel();		// 归属面板：包含归属Label、归属TextField
		label_location = new JLabel("归 属：");
		textField_location = new JTextField(20);
		textField_location.setText(this.account_modify.getHome());
		panel_location.setPreferredSize(new Dimension(i_window_width,12));		// 设置panel大小
		panel_location.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
		
		panel_person = new JPanel();		// 个人面板：包含个人Label、个人TextField
		label_person = new JLabel("年 龄：");
		textField_person = new JTextField(20);
		textField_person.setText(this.account_modify.getOld()+"");
		panel_person.setPreferredSize(new Dimension(i_window_width,12));		// 设置panel大小
		panel_person.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
			
		panel_signature = new JPanel();		// 个性签名面板：包含个签Label、个签TextField
		label_signature = new JLabel("个 签：");
		textField_signature = new JTextField(20);
		textField_signature.setText(this.account_modify.getSignature());
		panel_signature.setPreferredSize(new Dimension(i_window_width,12));		// 设置panel大小
		panel_signature.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
		
		panel_headImage = new JPanel();		// 上传头像面板
		FlowLayout flout = new FlowLayout(FlowLayout.CENTER); flout.setHgap(20);	// 布局设置
		panel_headImage.setLayout(flout);
		panel_headImage.setPreferredSize(new Dimension(i_window_width,105));		// 设置面板大小
		panel_headImage.setBorder(BorderFactory.createTitledBorder("头像上传（可拖拽上传）"));				// 设置面板边框
		Picture pic_show = this.account_modify.getPicture().clone();
		pic_show.reduceImage(100, 100);
		label_headImage = new JLabel(new ImageIcon(pic_show.getPictureBytes()));	// 原头像
		label_loadImage = new JLabel(new ImageIcon("image/default.png"));			// 上传的头像
		btn_upload = new JButton("上传→");		// 上传按钮
		
		String destPath = "image/" + InfoModificationWindow.this.account_modify.getId() +".jpg";	// 拷贝时用到的新文件路径
		btn_upload.addActionListener(new ActionListener() {		// 按钮点击事件
			public void actionPerformed(ActionEvent e) {	
				JFileChooser chooser = new JFileChooser();		// 设置选择器  
				chooser.setMultiSelectionEnabled(false);		// 设为单选  
				
				FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & GIF Images","jpg");	// 设置文件过滤器
				       // ("JPG & GIF Images", "jpg", "gif");
			    chooser.setFileFilter(filter);	
				int returnVal = chooser.showOpenDialog(btn_upload);	// 打开目录窗口 并等待返回值
				
				if (returnVal == JFileChooser.APPROVE_OPTION) {		// 如果符合文件类型  
					
					String fileName = chooser.getSelectedFile().getName();
					String fileType = fileName.substring(fileName.lastIndexOf('.')+1, fileName.length());
					
					if((fileName.lastIndexOf('.') == -1) || !(fileType.equals("jpg"))) {
						JOptionPane.showMessageDialog(null, "请检查文件类型！");						
					}else {
						String sourcePath = chooser.getSelectedFile().getAbsolutePath();	// 源图片绝对地址
						
						File sourceFile = new File(sourcePath);	
						File destFile = new File(destPath);	
	                    if(destFile.exists())
	                    	destFile.delete();
	                    try {
							Files.copy(sourceFile.toPath(), destFile.toPath());		// 将上传的图片进行拷贝
						} catch (IOException e1) {
							System.out.println("Error: cannot read the File "+sourcePath+".");
							e1.printStackTrace();
						}

	                    try {
							pic_upload = new Picture(destPath);		// 使用 Picture 对象打开图片
//							pic_upload.reduceImage(100,100);		// 重置图片大小
							pic_upload.savePicture(destPath, "jpg");
							Picture pic_show = pic_upload.clone();
							pic_show.reduceImage(100,100);
							label_loadImage.setIcon(new ImageIcon(pic_show.getPictureBytes()));
							System.out.println("Info: load the new image successfully.");
							JOptionPane.showMessageDialog(null, "添加成功！");
							isImageModified = true;
						} catch (IOException e1) {
							e1.printStackTrace();
							System.out.println("Error: cannot open the File "+destPath+".");
						}
					}
				}
			}  
		});  
		new DropTarget(panel_headImage, DnDConstants.ACTION_COPY_OR_MOVE, new DropTargetAdapter()
        {
            @Override
            public void drop(DropTargetDropEvent dtde)	//重写适配器的drop方法
            {
                try
                {
                    if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor))	//如果拖入的文件格式受支持
                    {    					
                        dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);	//接收拖拽来的数据
                        @SuppressWarnings("unchecked")
						List<File> list =  (List<File>) (dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor));

                    	String filePath = list.get(0).toPath().toString();
    					String fileType = filePath.substring(filePath.lastIndexOf('.')+1, filePath.length());
    					
    					if((filePath.lastIndexOf('.') == -1) || !(fileType.equals("jpg"))) {
    						JOptionPane.showMessageDialog(null, "请检查文件类型！");						
    					}else {
    						File dest = new File(destPath);		
                            if(dest.exists())	// 若已存在 则删除原有文件
                            	dest.delete();
                            Files.copy(list.get(0).toPath(), dest.toPath());	// 将文件复制到工程目录下
                                                        
                            pic_upload = new Picture(destPath);   
    						pic_upload.savePicture(destPath, "jpg");
    						Picture pic_show = pic_upload.clone();
    						pic_show.reduceImage(100,100);
                            label_loadImage.setIcon(new ImageIcon(pic_show.getPictureBytes()));	// 显示新上传的图片
        					System.out.println("Info: load the image successfully.");
                            
    	                    JOptionPane.showMessageDialog(null, "添加成功！");
                            dtde.dropComplete(true);	//指示拖拽操作已完成
                            isImageModified = true;
    					}
                    }
                    else
                    {
                        dtde.rejectDrop()	;//否则拒绝拖拽来的数据
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
		
		panel_nickName.add(label_nickName,BorderLayout.NORTH);		// 北部各面板添加组件
		panel_nickName.add(textField_nickName,BorderLayout.NORTH);
		panel_userName.add(label_userName,BorderLayout.NORTH);
		panel_userName.add(textField_userName,BorderLayout.NORTH);
		panel_phone.add(label_phone,BorderLayout.NORTH);
		panel_phone.add(textField_phone,BorderLayout.NORTH);
		panel_mail.add(label_mail,BorderLayout.NORTH);
		panel_mail.add(textField_mail,BorderLayout.NORTH);
		panel_location.add(label_location,BorderLayout.NORTH);
		panel_location.add(textField_location,BorderLayout.NORTH);
		panel_person.add(label_person,BorderLayout.NORTH);
		panel_person.add(textField_person,BorderLayout.NORTH);
		panel_signature.add(label_signature,BorderLayout.NORTH);
		panel_signature.add(textField_signature,BorderLayout.NORTH);
		panel_headImage.add(label_headImage,BorderLayout.WEST);
		panel_headImage.add(btn_upload,BorderLayout.WEST);
		panel_headImage.add(label_loadImage,BorderLayout.NORTH);
		
		panel_nickName.setAlignmentX(Component.LEFT_ALIGNMENT);		// 设置面板居左
		panel_userName.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel_phone.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel_mail.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel_location.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel_person.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel_signature.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel_headImage.setAlignmentX(Component.LEFT_ALIGNMENT);

		panel_north.add(panel_userName);	// 北部容器添加组件
		panel_north.add(panel_nickName);
		panel_north.add(panel_phone);
		panel_north.add(panel_mail);
		panel_north.add(panel_location);
		panel_north.add(panel_person);
		panel_north.add(panel_signature);
		panel_north.add(panel_headImage);
		
		// 南部容器
		panel_south = new JPanel();
		btn_exec = new JButton("取消");
		btn_exec.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				InfoModificationWindow.this.dispose();
			};
		});
		btn_ok = new JButton("完成");
		btn_ok.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				account_modify.setNickName(textField_nickName.getText());
				account_modify.setMobliePhone(textField_phone.getText());
				account_modify.setMail(textField_mail.getText());
				account_modify.setHome(textField_location.getText());
				account_modify.setOld(Integer.parseInt(textField_person.getText()));
				account_modify.setSignature(textField_signature.getText());
				if(isImageModified) account_modify.setPicture(pic_upload.getPictureBytes());				
				
				// 发送新信息至服务器
				RecvSendController.addToSendQueue(MessageOperate.packageUserDetail(account_modify));	// 请求个人信息
				// 更新个人信息窗口
				wind_father.updateAccountInfo(account_modify);
				// 更新好友列表窗口的个人信息
				RecvSendController.addToSendQueue(MessageOperate.packageAskUserDetail());
				
				InfoModificationWindow.this.dispose();
			}
		});
		FlowLayout flowLayout_Bottom = new FlowLayout();
		flowLayout_Bottom.setAlignment(FlowLayout.RIGHT);	// 居右		
		panel_south.setLayout(flowLayout_Bottom);		
		
		panel_south.add(btn_ok);	// 南部容器添加组件
		panel_south.add(btn_exec);

		// 窗口添加面板
		this.add(panel_north,BorderLayout.CENTER);		
		this.add(panel_south,BorderLayout.SOUTH);		
		
		this.setAlwaysOnTop(false);
		this.setVisible(true);		
	}
	
}