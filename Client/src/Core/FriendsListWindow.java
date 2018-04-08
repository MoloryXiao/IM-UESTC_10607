package Core;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.*;

public class FriendsListWindow extends JFrame{
	private int 		i_window_width=320,i_window_height=700;
	private int 		i_screen_width,i_screen_height;
	private int 		i_loc_X,i_loc_Y;
	private Toolkit 	tool_kit;
	private Dimension 	screenSize;
	private Vector<Integer> vec_friend_window;
	
	private JPanel 				top_panel,middle_panel,
								middle_friends_panel,middle_groups_panel,middle_else_panel;
	private JLabel 				head_image_label,name_label,sign_label;
	private JTabbedPane 		tabbed_pane;
	private JList<String> 		friends_list;
	private GridBagLayout 		top_gbLayout;
	private GridBagConstraints 	top_gbConstr;
	
	ArrayList<ChatWindow> alist;
	
	/**
	 * FriendsListWindow 构造函数
	 */
	public FriendsListWindow(){
		vec_friend_window = new Vector();
		alist = new ArrayList();
		/* 获取屏幕相关信息 */
		tool_kit=Toolkit.getDefaultToolkit();
		screenSize=tool_kit.getScreenSize();
		
		/* 设置窗口基本信息 */
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);					// 设置关闭键操作
		this.setIconImage((new ImageIcon("image/chick.png")).getImage());		// 设置图标
		setWindowSize();			// 设置窗口尺寸
		setWindowRight();			// 设置窗口位于屏幕右侧		
		this.setResizable(false);	// 是否可拖拽
		this.setTitle("KIM");		// 窗口名称
		
		topPaneSet(); 
		middlePaneSet(); 
		
		this.setAlwaysOnTop(true);	// 是否置顶
		this.setVisible(true);		// 是否可视化
	}
	
	/**
	 * 设置窗口尺寸
	 */
	private void setWindowSize(){
		//i_window_height = (int)(screenSize.height/3*2);		// 取2/3倍屏幕高度
		//i_window_width = (int)(screenSize.width/3/3*1.5);
		this.setSize(i_window_width,i_window_height);
	}
	
	/**
	 * 设置窗口位置
	 */
	private void setWindowRight(){
		i_screen_width = screenSize.width;
		i_screen_height = screenSize.height;
		i_loc_X = (i_screen_width/3*2)+(i_screen_width/3 - i_window_width)/2;	// 位于屏幕右侧
		i_loc_Y = (i_screen_height - i_window_height)/2;
		this.setLocation(i_loc_X,i_loc_Y);
	}
	
	/**
	 * 顶部Pane设置
	 */
	private void topPaneSet(){
		/* top面板各组件设置 */
		top_panel = new JPanel();
		head_image_label = new JLabel();							// 头像图标
		head_image_label.setIcon((new ImageIcon("image/p70_piano.jpg")));
		//head_image_label.setPreferredSize(new Dimension(20,20));
		name_label = new JLabel("昵称：Piano Duet");					// 昵称
		sign_label = new JLabel("坚持不是梦想，放弃才是胜利！");		// 个性签名	
		// 面板布局设置
		top_gbLayout = new GridBagLayout();
		top_gbConstr = new GridBagConstraints();
		Insets ins = new Insets(0,0,0,0);
		top_gbConstr.fill = GridBagConstraints.BOTH;
		top_gbConstr.weightx = 1.0;
		top_gbConstr.weighty = 1.0;
		top_panel.setLayout(top_gbLayout);
		// 组件布局
		top_gbConstr.gridx = 0; top_gbConstr.gridy = 0;				// 头像
		top_gbConstr.gridheight = 2; top_gbConstr.gridwidth = 2;
		ins.set(10, 5, 10, 5);
		top_gbConstr.insets = ins;
		top_gbLayout.setConstraints(head_image_label, top_gbConstr);
		top_panel.add(head_image_label);
		top_gbConstr.gridx = 2; top_gbConstr.gridy = 0;				// 昵称
		top_gbConstr.gridheight = 1; top_gbConstr.gridwidth = 4;
		ins.set(0,0,0,0);
		top_gbLayout.setConstraints(name_label, top_gbConstr);
		top_panel.add(name_label);
		top_gbConstr.gridx = 2; top_gbConstr.gridy = 1;				// 签名档
		top_gbConstr.gridheight = 1; top_gbConstr.gridwidth = 4;
		ins.set(0, 0, 0, 0);
		top_gbConstr.insets = ins;
		top_gbLayout.setConstraints(sign_label, top_gbConstr);
		top_panel.add(sign_label);
		
		/* 将面板添加到容器中 */
		this.add(top_panel,BorderLayout.NORTH);
	}
	
	/**
	 * 中部Pane设置
	 */
	private void middlePaneSet(){
		/* 组件初始化 */
		tabbed_pane = new JTabbedPane();
		middle_panel = new JPanel();
		middle_friends_panel = new JPanel();
		middle_groups_panel = new JPanel();
		middle_else_panel = new JPanel();
		friends_list = new JList<String>();
		
		/* List列表设置 */
		friends_list.setListData(new String[]{"小明","小军","小红","小鬼","乐乐"});
		friends_list.setPreferredSize(new Dimension(i_window_width-60, (int)(i_window_height*0.7)));
		friends_list.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if(friends_list.getSelectedIndex() != -1) {
					if(e.getClickCount() == 2){
						twoClick(friends_list.getSelectedValue(),friends_list.getSelectedIndex());
					}
				}
			}
		});
		
		/* Panel面板设置 */
		middle_groups_panel.setBackground(Color.green);
		middle_else_panel.setBackground(Color.red);
		
		/* Panel面板添加 */
		middle_panel.add(tabbed_pane);
		tabbed_pane.add("好友",middle_friends_panel);
		tabbed_pane.add("群组",middle_groups_panel);
		tabbed_pane.add("其他",middle_else_panel);
		middle_friends_panel.add(friends_list);
		
		/* 添加到容器  */
		this.add(middle_panel,BorderLayout.CENTER);
	}
	
	/**
	 * 双击好友进行的操作
	 * @param 双击的对象
	 * @param i
	 */
	private void twoClick(Object value,int i){
		if(vec_friend_window.indexOf(i) == -1){
			vec_friend_window.add(i);
//			Runnable rnb = ()->{
				ChatWindow cw = new ChatWindow(value);
				alist.add(cw);
				System.out.println("打开与 "+value+" 的聊天窗口...");
//			};
//			new Thread(rnb).start();
		}else{
			alist.get(i).setVisible(true);
			alist.get(i).setAlwaysOnTop(true);
			alist.get(i).setAlwaysOnTop(false);
		}
	}
}