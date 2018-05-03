package Server.util;

import java.util.Date;

public class ShowDate {
	
	private static boolean showFlag = true;
	
	public static void setShowFlag() {
		
		if (showFlag) {
			ShowDate.showFlag = false;
			System.out.println("[ ADMIN ] 日志时间戳已关闭！");
		} else {
			ShowDate.showFlag = true;
			ShowDate.showDate();
			System.out.println("[ ADMIN ] 日志时间戳已打开！");
		}
		
	}
	
	public static void showDate() {
		
		if (showFlag) System.out.printf("%1$tH:%1$tM:%1$tS ", new Date());
		//System.out.printf("%1$tm/%1$td %1$tH:%1$tM:%1$tS ", new Date());
	}
	
	
}
