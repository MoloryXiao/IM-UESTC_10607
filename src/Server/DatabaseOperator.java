package Server;

import network.commonClass.*;

import java.sql.*;
import java.util.ArrayList;

/**
 * @author 97njczh
 */
public class DatabaseOperator {
	
	// JDBC driver name and database URL
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DB_URL = "jdbc:mysql://118.24.107.193:3306/rP87cbTu?useSSL=false";
	
	// Database credentials
	private static final String USER = "root";
	private static final String PASSWORD = "jASVun7NmnRn";
	
	private static final String DB_NAME = "`rP87cbTu`";
	
	private Connection connection;
	private Statement statement;
	
	public DatabaseOperator() {
		
		connection = null;
		statement = null;
		
	}
	
	private static boolean setupDatabase() {
		
		try {
			
			Class.forName(JDBC_DRIVER);            // Register JDBC driver
			return true;
			
		} catch (ClassNotFoundException e) {
			
			ShowDate.showDate();
			System.out.println("[ ERROR ] JDBC_DRIVER 加载错误，数据库驱动启动失败！");
			return false;
			
		}
	}
	
	private boolean connectDatabase() {
		
		try {
			
			connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
			return true;
			
		} catch (SQLException e) {
			ShowDate.showDate();
			System.out.println("[ ERROR ] 连接数据库时出现错误！");
			return false;
			
		}
	}
	
	private boolean disconnectDatabase() {
		
		try {
			
			connection.close();
			return true;
			
		} catch (SQLException e) {
			ShowDate.showDate();
			System.out.println("[ ERROR ] 关闭数据库时出现错误！");
			return false;
			
		}
	}
	
	private boolean connectTest() {
		
		return (connectDatabase() && disconnectDatabase());
		
	}
	
	public static boolean connectDbTest() {
		
		DatabaseOperator databaseOperator = new DatabaseOperator();
		
		if (setupDatabase() && databaseOperator.connectTest()) {
			ShowDate.showDate();
			System.out.println("[  O K  ] 数据库连接测试成功，数据库已就绪！");
			return true;
		} else {
			ShowDate.showDate();
			System.out.println("[ ERROR ] 数据库连接测试失败，无法正确访问数据库！");
			return false;
		}
		
	}
	
	public ResultSet query( String sql ) {
		
		try {
			
			statement = connection.createStatement();
			return statement.executeQuery(sql);
			
		} catch (SQLException e) {
			ShowDate.showDate();
			System.out.println("[ ERROR ] SQL Query 查询时发生错误！请检查语法：" + sql);
			return null;
			
		}
		
	}
	
	public int update( String sql ) throws SQLException {
		
		try {
			
			statement = connection.createStatement();
			return statement.executeUpdate(sql);
			
		} catch (SQLException e) {
			ShowDate.showDate();
			System.out.println("[ ERROR ] SQL Update 查询时发生错误！请检查语法：" + sql);
			return -1;
			
		}
		
	}
	
	/**
	 * 判断用户登陆信息是否正确
	 *
	 * @param loginInfo
	 * @return 登陆成功，返回Account用户信息，否则返回null
	 * @throws SQLException
	 */
	public Account isLoginInfoCorrect( Login loginInfo ) {
		
		connectDatabase();
		
		Account loginAccount = null;
		
		ResultSet rs = query("SELECT `id`,`username`,`sign` FROM " + DB_NAME
				                     + ".`user` WHERE id = " + loginInfo.getAccountId()
				                     + " AND `password` = \'" + loginInfo.getPassword() + "\'");// Execute a query
		try {
			
			if (rs != null) { // 查询出现错误
				
				if (rs.next()) // Extract data from result set
					// Retrieve by column name
					loginAccount = new Account(rs.getString("id"),
							rs.getString("username"), true,
							rs.getString("sign"));
				
			}
			
		} catch (SQLException e) {
			
			loginAccount = null;
			ShowDate.showDate();
			System.out.println("[ ERROR ] 数据库查询结果集时出现问题(在函数isLoginInfoCorrect中)");
			
		} finally {
			
			disconnectDatabase();
			
			return loginAccount;
		}
		
	}
	
	public Account getAccountById( String Id ) throws SQLException {
		
		connectDatabase();
		
		Account account = new Account();
		
		ResultSet rs = query("SELECT * FROM " + DB_NAME
				                     + ".`user` where id = " + Id + ";");
		
		if (rs != null) {
			
			if (rs.next())
				account = new Account(rs.getString("id"),
						rs.getString("username"), false,
						rs.getString("sign"));
			else {
				ShowDate.showDate();
				System.out.println("[ ERROR ] 数据库中查无此ID：" + Id);
			}
			
		}
		
		disconnectDatabase();
		
		return account;
		
	}
	
	public ArrayList<Account> getFriendListFromDb( String id ) {
		
		connectDatabase();
		
		ArrayList<Account> friendsList = new ArrayList<Account>();
		
		ResultSet rs = query("SELECT 	SlaveID, username, sign \n" // Execute a query
				                     + "FROM " + DB_NAME + ".relationship a, " + DB_NAME + ".`user` b \n"
				                     + "WHERE	a.MasterId = " + id + " AND b.id = a.SlaveID;");
		
		try {
			
			if (rs != null) {  // 查询出现错误
				
				while (rs.next()) { // Extract data from result set
					
					friendsList.add(new Account(
							rs.getString(1),
							rs.getString(2),
							Server.isUserOnline(rs.getString(1)), // 判断好友是否在线
							rs.getString(3)));
					
				}
			}
			
		} catch (SQLException e) {
			
			friendsList = null;
			ShowDate.showDate();
			System.out.println("[ ERROR ] 数据库查询结果集时出现问题(在函数getFriendListFromDb中)");
			
		} finally {
			
			disconnectDatabase();
			
			return friendsList;
			
		}
		
		
	}
	
}
