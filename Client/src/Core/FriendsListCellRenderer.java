package Core;

import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 * 用于自定义好友信息列表显示的样式
 * @author Murrey
 * @version 1.0
 *
 */
public class FriendsListCellRenderer extends DefaultListCellRenderer{
	private int []rows;
	private int count;
	private Color row_color;
	
	public FriendsListCellRenderer(int[] rows, Color color){
		this.rows = rows;
		this.row_color = color;	
	}
	public FriendsListCellRenderer(int count,Color color){
		this.count = count;
		this.row_color = color;
	}
	
	 public Component getListCellRendererComponent(JList list,Object value,int index,
             boolean isSelected,boolean cellHasFocus ) {
		 super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
		 if(rows != null){
			 for(int i=0;i<rows.length;i++){
				 if(index == rows[i]){
					 setForeground(this.row_color);
				 }
			 }
		 }else{
			 for(int i=0;i<count;i++){
				 if(index == i){
					 setForeground(this.row_color);
				 }
			 }
		 }
		 
		 return this;
	 }
}
