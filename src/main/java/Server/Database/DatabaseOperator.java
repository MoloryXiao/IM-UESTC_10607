package Server.Database;

import Server.Server;
import Server.util.LoggerProvider;
import Server.util.ShowDate;
import network.commonClass.Account;
import network.commonClass.Login;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DatabaseOperator {
	
	private static final String USER_NAME = "username";
	private static final String ID = "id";
	
	/**
	 * 查询用户登录信息是否与数据库中相匹配
	 *
	 * @param loginInfo 请求登录的用户信息
	 * @return 登录成功返回请求登录的用户Account类型信息，否则返回null
	 */
	public static Account isLoginInfoCorrect( Login loginInfo ) {
		
		String sql = "SELECT `id`,`username`,`sign` FROM " + DatabaseConnector.getDbName()
				             + ".`user` WHERE id = " + loginInfo.getAccountId()
				             + " AND `password` = \'" + loginInfo.getPassword() + "\'";
		
		Connection conn = DatabaseConnector.getConnection();
		Statement stmt = DatabaseConnector.getStatement(conn);
		ResultSet rs = DatabaseConnector.query(stmt, sql);// Execute a query
		
		Account loginAccount = null;
		try {
			
			if (rs != null) { // 查询出现错误
				
				if (rs.next()) // Extract data from result set
					// Retrieve by column name
					loginAccount = new Account(rs.getString("id"),
							rs.getString("username"), true,
							rs.getString("sign"));
				
			}
			
		} catch (SQLException e) {
			
			LoggerProvider.logger.error("[ ERROR ] Database：数据库查询结果集时出现问题(在函数 isLoginInfoCorrect() 中)");
			
		}
		
		DatabaseConnector.closeConnection(conn, stmt, rs);
		
		return loginAccount;
		
		
	}
	
	/**
	 * 从数据库中获取用户好友列表
	 *
	 * @param id 请求好友列表的用户id
	 * @return ArrayList<Account> 用户的好友列表
	 */
	public static ArrayList<Account> getFriendListFromDb( String id ) {
		
		String sql = "SELECT SlaveID, username, sign FROM " // Execute a query
				             + DatabaseConnector.getDbName() + ".relationship a, "
				             + DatabaseConnector.getDbName() + ".`user` b \n"
				             + "WHERE a.MasterId = " + id + " AND b.id = a.SlaveID;";
		
		Connection conn = DatabaseConnector.getConnection();
		Statement stmt = DatabaseConnector.getStatement(conn);
		ResultSet rs = DatabaseConnector.query(stmt, sql);
		
		ArrayList<Account> friendsList = new ArrayList<Account>();
		
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
			
			LoggerProvider.logger.error("[ ERROR ] Database：数据库查询结果集时出现问题(在函数 getFriendListFromDb() 中)");
			
		}
		
		DatabaseConnector.closeConnection(conn, stmt, rs);
		
		return friendsList;
	}
	
	/**
	 * 通过某个关键字在数据库中查找某用户
	 *
	 * @param type  关键字类型
	 * @param value 关键字值
	 * @return 找到返回用户信息，未找到返回null
	 */
	private static Account searchUser( String type, String value ) {
		
		String sql = "SELECT * FROM " + DatabaseConnector.getDbName()
				             + ".`user` where " + type + " = " + value + ";";
		
		Connection conn = DatabaseConnector.getConnection();
		Statement stmt = DatabaseConnector.getStatement(conn);
		ResultSet rs = DatabaseConnector.query(stmt, sql);
		
		Account account = null;
		
		try {
			
			if (rs != null) {
				
				if (rs.next())
					
					account = new Account(rs.getString("id"),
							rs.getString("username"), false,
							rs.getString("sign"));
			}
//			} else {
//
//				LoggerProvider.logger.info("[  O K  ] Database：数据库中查无此" + type + "用户：" + value);
//
//			}
		} catch (SQLException e) {
			
			LoggerProvider.logger.error("[ ERROR ] Database：数据库查询结果集时出现问题(在函数 searchUser() 中)");
		}
		
		DatabaseConnector.closeConnection(conn, stmt, rs);
		
		return account;
		
	}
	
	/**
	 * 通过用户名在数据库中查找某用户
	 *
	 * @param userName 欲查找用户的用户昵称
	 * @return 找到返回用户信息，未找到返回null
	 */
	public static Account searchUserByName( String userName ) {
		
		return searchUser(USER_NAME, userName);
	}
	
	/**
	 * 通过用户名在数据库中查找某用户
	 *
	 * @param id 欲查找用户的用户id
	 * @return 找到返回用户信息，未找到返回null
	 */
	public static Account searchUserById( String id ) {
		
		return searchUser(ID, id);
	}
	
	/**
	 * 在数据库中查找双方是否为已经是好友关系
	 *
	 * @param masterId 用户ID
	 * @param slaveId  用户ID
	 * @return 已经是好友返回ture，否则返回false；
	 */
	public static boolean isFriendAlready( String masterId, String slaveId ) {
		
		boolean result = false;
		
		String sql = "SELECT * FROM " + DatabaseConnector.getDbName() + ".`relationship` WHERE"
				             + "( `MasterID` = " + masterId + " AND `SlaveID` = " + slaveId + " ) "
				             + "OR ( `MasterID` = " + slaveId + " AND `SlaveID` = " + masterId + " )";
		
		Connection conn = DatabaseConnector.getConnection();
		Statement stmt = DatabaseConnector.getStatement(conn);
		ResultSet rs = DatabaseConnector.query(stmt, sql);
		
		try {
			
			rs.last();
			if (rs.getRow() == 2) result = true;
			
		} catch (SQLException e) {
			
			LoggerProvider.logger.error("[ ERROR ] Database：数据库查询结果集时出现问题(在函数 isFriendAlready() 中)");
		}
		
		DatabaseConnector.closeConnection(conn, stmt, rs);
		
		return result;
		
	}
	
	/**
	 * 添加好友接受者同意发起人添加好友请求后，在数据库中注册该好友关系
	 *
	 * @param masterId：添加好友发起人
	 * @param slaveId：添加好友接受者
	 * @return 好友关系在数据库中是否建立成功
	 */
	public static boolean addFriend( String masterId, String slaveId ) {
		
		boolean result = false;
		
		String sql = "INSERT INTO " + DatabaseConnector.getDbName() + ".`relationship` (`MasterID`, `SlaveID`) "
				             + "VALUES (" + masterId + "," + slaveId + "), (" + slaveId + "," + masterId + ")\n";
		
		Connection conn = DatabaseConnector.getConnection();
		Statement stmt = DatabaseConnector.getStatement(conn);
		if (DatabaseConnector.update(stmt, sql) == 2) result = true;
		
		DatabaseConnector.closeConnection(conn, stmt);
		
		return result;
	}
	
	/**
	 * 好友关系中的某一方确认删除好友后，在数据库中删除该好友关系
	 *
	 * @param targetId：删除好友发起人
	 * @param sourceId：被删除好友用户
	 * @return 好友关系在数据库中是否删除成功
	 */
	public static boolean delFriend( String targetId, String sourceId ) {
		
		boolean result = false;
		
		String sql = "DELETE FROM " + DatabaseConnector.getDbName() + ".`relationship` WHERE"
				             + "( `MasterID` = " + targetId + " AND `SlaveID` = " + sourceId + " ) "
				             + "OR ( `MasterID` = " + sourceId + " AND `SlaveID` = " + targetId + " )";
		
		Connection conn = DatabaseConnector.getConnection();
		Statement stmt = DatabaseConnector.getStatement(conn);
		if (DatabaseConnector.update(stmt, sql) == 2) result = true;
		
		DatabaseConnector.closeConnection(conn, stmt);
		
		return result;
		
	}
	
	public static boolean modifyMyInfo( Account myInfo ) {
		
		boolean result = false;
		
		String sql = "";
		
		Connection conn = DatabaseConnector.getConnection();
		Statement stmt = DatabaseConnector.getStatement(conn);
		if (DatabaseConnector.update(stmt, sql) == 1) result = true;
		
		DatabaseConnector.closeConnection(conn, stmt);
		
		return result;
	}
	
	
}
