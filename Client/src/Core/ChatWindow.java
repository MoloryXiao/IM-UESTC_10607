package Core;

import java.awt.*;

import javax.swing.*;

public class ChatWindow extends JFrame{
	private int i_window_width = 900,i_window_height = 700;
	private int i_screen_width,i_screen_height;
	private int i_loc_X,i_loc_Y;
	private JLabel name_label;
	
	protected void setWindowToScreenCenter(){
		Toolkit kit=Toolkit.getDefaultToolkit();
		Dimension screenSize=kit.getScreenSize();
		i_screen_width = screenSize.width;
		i_screen_height = screenSize.height;
		i_loc_X = (i_screen_width - i_window_width)/2;
		i_loc_Y = (i_screen_height - i_window_height)/2;
		this.setLocation(i_loc_X,i_loc_Y);
	}
	
	public ChatWindow(String str){
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setIconImage((new ImageIcon("image/chick.png")).getImage());
		this.setSize(i_window_width,i_window_height);
		this.setResizable(false);
		this.setTitle("KIM");
		this.setBackground(Color.WHITE);
		setWindowToScreenCenter();
		
		name_label = new JLabel(str);
		
		this.add(name_label);

		this.setVisible(true);
	}
}
