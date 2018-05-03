package Server.util;

import Server.Server;

import java.util.Scanner;

public class AdminUtil {
	/**
	 * 显示在线用户数，并显示在线用户ID
	 */
	public static void showOnlineUser( String mode ) {
		
		if (Server.getOnlineCounter() == 0) {
			System.out.println("[ ADMIN ] 当前无人在线！");
			return;
		} else {
			System.out.println("[ ADMIN ] 当前在线人数【 "
					                   + Server.getOnlineCounter() + " 】");
			if (!mode.equals("all")) return;
		}
		
		for (String key : Server.getSingleClientThreadDb().keySet()) {
			System.out.print("\t" + key);
		}
		System.out.println();
	}
	
	private static void kickSomeone( String cmd ) {
		
		String kickId = new String();
		
		if (cmd.length() >= 9) {
			for (int i = 4; i < cmd.length(); i++) {
				if (cmd.charAt(i) != ' ') kickId += cmd.charAt(i);
			}
		} else {
			System.out.println("[ ADMIN ] 请按照以下格式输入：kick <id>！");
			return;
		}
		try {
			Server.getSingleClientThreadDb().get(kickId).interrupt();
			System.out.println("[ ADMIN ] 用户ID：" + kickId + " 被踢！");
		} catch (Exception e) {
			System.out.println("[ ADMIN ] 用户ID：" + kickId + " 不在线，无法踢出！");
		}
		
	}
	
	private static void showHelp() {
		
		System.out.println("=========================  现 有 命 令  ========================\n"
				                   + "\t0. help\t\t显示现有命令\n"
				                   + "\t1. show\t\t输出当前在线人数，[-a/--all] 显示当前所有登录的用户ID\n"
				                   + "\t2. exit\t\t终止IM服务器程序\n"
				                   + "\t3. time\t\t打开/关闭时间戳显示（已移除）\n"
				                   + "\t4. kick\t\t登出用户，[id] 用户ID\n"
				                   + "===============================================================");
		
	}
	
	public static void adminMode() {
		
		System.out.println("[ ADMIN ] 命令行模式已启动！");
		showHelp();
		
		Scanner scanner = new Scanner(System.in);
		
		while (true) {
			
			String cmd = scanner.nextLine();
			
			if (cmd.equals("exit")) {
				
				System.out.println("=========================== goodbye! ===========================");
				System.exit(0);
				
			} else if (cmd.equals("help")) {
				
				showHelp();
				
			} else if (cmd.equals("show")) {
				
				showOnlineUser(null);
				
			} else if (cmd.equals("show -a") || cmd.equals("show --all")) {
				
				showOnlineUser("all");
				
			} else if (cmd.equals("time")) {
				
				System.out.println("时间戳功能已经移除");
				//  移除通过控制台进行日志管理，时间戳功能不在需要
				
			} else if (cmd.length() >= 4 && cmd.substring(0, 4).equals("kick")) {
				
				kickSomeone(cmd);
				
			} else {
				
				System.out.println("[ ADMIN ] 无效的命令！键入“help”显示当前所有命令");
				
			}
			
		}
	}
}
