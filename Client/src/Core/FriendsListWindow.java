package Core;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.*;

import Network_Client.*;

public class FriendsListWindow extends JFrame{
	private int 		i_window_width=320,i_window_height=700;
	private int 		i_screen_width,i_screen_height;
	private int 		i_loc_X,i_loc_Y;
	private Toolkit 	tool_kit;
	private Dimension 	screenSize;
	private boolean 	flag_create_chatWind = false;
	private ArrayList<Account> friends_arrList;
	
	private JScrollPane 		friends_list_scroll;
	private JPanel 				top_panel,middle_panel,bottom_panel,
								middle_friends_panel,middle_groups_panel,middle_else_panel;
	private JLabel 				head_image_label,name_label,sign_label;
	private JTabbedPane 		tabbed_pane;
	private JList<String> 		friends_name_list;
	private GridBagLayout 		top_gbLayout;
	private GridBagConstraints 	top_gbConstr;
	private JButton				logout_btn;
	
	private Object new_window_value;
	private int new_window_orderNum;
	private int i_friends_sum;
	
	/**
	 * FriendsListWindow 构造函数
	 */
	public FriendsListWindow(){
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
		bottomPaneSet();
		
		this.setAlwaysOnTop(true);	// 是否置顶
		this.setVisible(true);		// 是否可视化
	}
	
	/**
	 * 顶部Pane设置
	 */
	private void topPaneSet(){
		/* top面板各组件设置 */
		top_panel = new JPanel();
		head_image_label = new JLabel();							// 头像图标
		head_image_label.setIcon((new ImageIcon("image/p70_piano.jpg")));
		head_image_label.setToolTipText("123木头人");
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
		friends_name_list = new JList<String>();
		
		friends_name_list.setListData(new String[]{"正在拉取好友列表..."});	
		friends_name_list.setPreferredSize(new Dimension(i_window_width-60, (int)(i_window_height*0.7)));
		friends_name_list.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if(friends_name_list.getSelectedIndex() != -1) {
					if(e.getClickCount() == 2){
						twoClick(friends_name_list.getSelectedValue(),friends_name_list.getSelectedIndex());
					}
				}
			}
		});	
		friends_list_scroll = new JScrollPane(friends_name_list);
		friends_list_scroll.setPreferredSize(new Dimension(i_window_width-60,440));	//440
		friends_list_scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		friends_list_scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		
		
		/* Panel面板设置 */
		middle_groups_panel.setBackground(Color.green);
		middle_else_panel.setBackground(Color.red);
		
		/* Panel面板添加 */
		middle_panel.add(tabbed_pane);
		tabbed_pane.add("好友",middle_friends_panel);
		tabbed_pane.add("群组",middle_groups_panel);
		tabbed_pane.add("其他",middle_else_panel);
		middle_friends_panel.add(friends_list_scroll);
		
		/* 添加到容器  */
		this.add(middle_panel,BorderLayout.CENTER);
	}
	
	private void bottomPaneSet(){
		bottom_panel = new JPanel();
		logout_btn = new JButton("退出");
		logout_btn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		FlowLayout flowLayout_Bottom = new FlowLayout();
		flowLayout_Bottom.setAlignment(FlowLayout.RIGHT);		
		bottom_panel.setLayout(flowLayout_Bottom);
		
		bottom_panel.add(logout_btn);
		this.add(bottom_panel,BorderLayout.SOUTH);
	}
	
	
	/**
	 * 双击好友进行的操作
	 * @param 双击的对象
	 * @param i
	 */
	private void twoClick(Object value,int i){
		setNewWindowResource(value,i);
		this.flag_create_chatWind = true;
	}
	
	public boolean getCreateChatWindFlag(){		
		System.out.print("");
		return this.flag_create_chatWind;
	}
	public void setCreateChatWindFlag(boolean flag){
		this.flag_create_chatWind = flag;
	}
	private void setNewWindowResource(Object value,int orderNumber){
		this.new_window_value = value;
		this.new_window_orderNum = orderNumber;
	}
	public Object getNewWindowValue(){
		return this.new_window_value;
	}
	public int getNewWindowOrderNum(){
		return this.new_window_orderNum;
	}
	
	/**
	 * 设置窗口尺寸
	 */
	private void setWindowSize(){
		//i_window_height = (screenSize.height/3*2);		// 取2/3倍屏幕高度
		//i_window_width = (screenSize.width/3/3*1.5);
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


	public void setFriendsList(ArrayList<Account> inArrList){
		this.friends_arrList = new ArrayList<Account>(inArrList);
	}
	
	public void updateFriendsList(ArrayList<Account> inArrList){
		setFriendsList(inArrList);			// 将传进来的好友列表设置到成员属性中
		sortFriendsListByOnline();			// 对成员属性进行排序 依据在线情况
		int online_count = countOnlineNums();
		friendsListShow(online_count);
	}
	
	
	/**
	 * 将在线好友置顶
	 * @param inArrList 好友列表
	 * @return 排好序的好友列表
	 */
	private void sortFriendsListByOnline(){
		Comparator<Account> cmptor = new Comparator<Account>(){
			public int compare(Account a, Account b) {
				int status_A = a.getOnlineStatus()?1:-1;
				int status_B = b.getOnlineStatus()?1:-1;
				if(status_A <= status_B) return 1;
				else return -1;
			}
		};
		Collections.sort(friends_arrList,cmptor);		
	}

	public void friendsListShow(int online_count){
		/* List列表设置 */
		Vector<String> fd_name_vec = new Vector<String>();
		//Vector<Account> fd_account_vec = new Vector<Account>();
		this.i_friends_sum =  friends_arrList.size();
		for(int i=0;i<this.i_friends_sum;i++){
			fd_name_vec.add(friends_arrList.get(i).getNikeName());
			//fd_account_vec.add(friends_arrList.get(i));
		}
		
		//friends_name_list.setListData(fd_name_vec.toArray(new String[fd_name_vec.size()]));
		friends_name_list.setListData(fd_name_vec);
		FriendsListCellRenderer flcr = new FriendsListCellRenderer(online_count,Color.RED);
		friends_name_list.setCellRenderer(flcr);
		if(this.i_friends_sum >= 15)
			friends_list_scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		else
			friends_list_scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
	}
	
	/**
	 * 计数在线个数
	 * 前提：必须调用排序算法
	 * @param inArrList
	 * @return count 在线人数
	 */
	private int countOnlineNums(){
		int count = 0;
		for(int i=0;i<friends_arrList.size();i++){
			if(friends_arrList.get(i).getOnlineStatus()) count++;
			else break;
		}
		return count;
	}
	
	public void printFriendsArrList(){
		for(int i=0;i<friends_arrList.size();i++){
			System.out.println("ListInfo: Get the friend "+(i+1)+" - "+friends_arrList.get(i).getNikeName());
			System.out.println("ListInfo: Get the friend "+(i+1)+" - "+friends_arrList.get(i).getSignature());
			System.out.println("ListInfo: Get the friend "+(i+1)+" - "+friends_arrList.get(i).getID());
			System.out.println("ListInfo: Get the friend "+(i+1)+" - "+friends_arrList.get(i).getOnlineStatus());
		}
	}
}