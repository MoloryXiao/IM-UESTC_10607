package Server.Database;

import Server.Server;
import Server.util.LoggerProvider;
import network.Builder.AccountBuilder;
import network.commonClass.Account;
import network.commonClass.Group;
import network.commonClass.Login;
import network.commonClass.Picture;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

public class DatabaseOperator {
	
	private static final String USER_NAME = "username";
	private static final String ID = "id";
	
	/*============================================ [ User  相关 ] =============================================== */
	
	/**
	 * DESCRIPTION：查询数据库中用户详细信息
	 *
	 * @param sql 查询个人信息的sql语句
	 * @return 查找到返回用户Account类型信息，否则返回null
	 */
	private static Account getUserDetails( String sql ) {
		
		Connection conn = DatabaseConnector.getConnection();
		Statement stmt = DatabaseConnector.getStatement(conn);
		ResultSet rs = DatabaseConnector.query(stmt, sql);// Execute a query
		
		Account account = null;
		
		try {
			
			if (rs != null) // 查询出现错误
				
				if (rs.next()) { // Extract data from result set
					
					Picture picture = null;
					
					try {
						picture = new Picture("./" + rs.getString("portrait"));
					} catch (IOException e) {
						LoggerProvider.logger.error("[ ERROR ] Database：图片打开失败！尝试打开默认图片！");
						try {
							picture = new Picture("./portrait/default/default.jpg");
							LoggerProvider.logger.info("[  O K  ] Database：默认图片打开成功！");
						} catch (IOException e1) {
							LoggerProvider.logger.error("[ ERROR ] Database：默认图片打开失败！");
						}
					}
					
					// Retrieve by column name
					account = new AccountBuilder(rs.getString("id"),
							rs.getString("username"), rs.getString("sign"))
							          .mobilePhone(rs.getString("phone_number"))
							          .mail(rs.getString("mail"))
							          .stage((byte) Integer.parseInt(rs.getString("stage")))
							          .old(Integer.parseInt(rs.getString("old")))
							          .sex(rs.getString("sex").charAt(0) == '1')
							          .home(rs.getString("home_address"))
							          .picture(picture) // TODO 客户端需要处理为null的情况
							          .online(Server.isUserOnline(rs.getString("id")))
							          .createAccount();
				}
			
		} catch (SQLException e) {
			
			LoggerProvider.logger.error("[ ERROR ] Database：数据库查询结果集时出现问题(在函数 getUserDetails() 中)");
		}
		
		DatabaseConnector.closeConnection(conn, stmt, rs);
		
		return account;
		
	}
	
	/**
	 * 查询用户登录信息是否与数据库中相匹配
	 *
	 * @param loginInfo 请求登录的用户信息
	 * @return 登录成功返回请求登录的用户Account类型信息，否则返回null
	 */
	public static Account getUserDetailsByLoginInfo( Login loginInfo ) {
		
		String sql = "SELECT * FROM " + DatabaseConnector.getDbName()
				             + ".`t_user_base_info` WHERE id = " + loginInfo.getAccountId()
				             + " AND `password` = \'" + loginInfo.getPassword() + "\'";
		
		return getUserDetails(sql);
		
	}
	
	/**
	 * 在数据库中查询用户全部信息
	 *
	 * @param uid 查询的用户信息
	 * @return 查询成功返回用户Account类型信息，否则返回null
	 */
	public static Account getUserDetailsById( String uid ) {
		
		String sql = "SELECT * FROM " + DatabaseConnector.getDbName()
				             + ".`t_user_base_info` WHERE id = " + uid;
		
		return getUserDetails(sql);
		
	}
	
	/**
	 * 从数据库中获取用户好友列表
	 *
	 * @param id 请求好友列表的用户id
	 * @return ArrayList<Account> 用户的好友列表
	 */
	public static ArrayList<Account> getFriendsList( String id ) {
		
		String sql = "SELECT SlaveID, username, sign FROM " // Execute a query
				             + DatabaseConnector.getDbName() + ".t_user_relationship a, "
				             + DatabaseConnector.getDbName() + ".`t_user_base_info` b \n"
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
			
			LoggerProvider.logger.error("[ ERROR ] Database：数据库查询结果集时出现问题(在函数 getFriendsList() 中)");
			
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
				             + ".`t_user_base_info` where " + type + " = " + value + ";";
		
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
		
		String sql = "SELECT * FROM " + DatabaseConnector.getDbName() + ".`t_user_relationship` WHERE"
				             + "( `MasterID` = " + masterId + " AND `SlaveID` = " + slaveId + " ) "
				             + "OR ( `MasterID` = " + slaveId + " AND `SlaveID` = " + masterId + " )";
		
		Connection conn = DatabaseConnector.getConnection();
		Statement stmt = DatabaseConnector.getStatement(conn);
		ResultSet rs = DatabaseConnector.query(stmt, sql);
		
		try {
			
			if (rs != null)
				if (rs.last())
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
		
		String sql = "INSERT INTO " + DatabaseConnector.getDbName() + ".`t_user_relationship` (`MasterID`, `SlaveID`) "
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
		
		String sql = "DELETE FROM " + DatabaseConnector.getDbName() + ".`t_user_relationship` WHERE"
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
		
		// TODO 图片更新机制的优化，即如果用户没有更新头像则，不需要重新保存
		
		String filePathS = "./portrait/user/" + myInfo.getId() + ".jpg";  // 用户头像保存相对路径
		
		/* 在服务器硬盘中保存用户头像 */
		Picture picture = myInfo.getPicture();
		picture.savePicture(filePathS, "jpg");
		
		String sql = "UPDATE " + DatabaseConnector.getDbName() + ".`t_user_base_info`  SET "
				             + "`phone_number` = '" + myInfo.getMobliePhone() + "', "
				             + "`username` = '" + myInfo.getNikeName() + "', "
				             + "`sign` = '" + myInfo.getSignature() + "', "
				             + "`mail`  = '" + myInfo.getMail() + "', "
				             + "`old` = " + myInfo.getOld() + ", "
				             + "`sex` = " + (myInfo.isMale() ? 1 : 0) + ", "
				             + "`portrait` = '" + filePathS + "',"
				             + "`home_address` = '" + myInfo.getHome() + "' "
				             + " WHERE `id` = " + myInfo.getId();
		
		Connection conn = DatabaseConnector.getConnection();
		Statement stmt = DatabaseConnector.getStatement(conn);
		if (DatabaseConnector.update(stmt, sql) == 1)
			result = true;
		
		DatabaseConnector.closeConnection(conn, stmt);
		
		return result;
	}
	
	/*============================================ [ Group 相关 ] =============================================== */
	
	public static ArrayList<Group> getGroupsList( String uid ) {
//		String sql = "SELECT * "
//				             + "FROM `" + DatabaseConnector.getDbName() + "`.`t_group_base_info` "
//				             + "WHERE p_group_id "
//				             + "IN (SELECT `p_group_id`FROM `" + DatabaseConnector.getDbName() + "`.`t_group_member` "
//				             + "WHERE `p_group_member_id` = " + uid + " )";
		
		String sql = "SELECT `p_group_id` "
				             + "From `" + DatabaseConnector.getDbName() + "`.`t_group_member`"
				             + "WHERE `p_group_member_id`=" + uid;
		
		Connection conn = DatabaseConnector.getConnection();
		Statement stmt = DatabaseConnector.getStatement(conn);
		ResultSet rs = DatabaseConnector.query(stmt, sql);  // Execute a query
		
		ArrayList<Group> groupsList = new ArrayList<>();
		
		try {
			
			if (rs != null)   // 查询出现错误
				
				while (rs.next())  // Extract data from result set
					
					/* ----- 仅需要获取群信息 ----- */
					groupsList.add(getGroupInfo(rs.getString(1)));
			
			
		} catch (SQLException e) {
			
			groupsList = null;
			
			LoggerProvider.logger.error("[ ERROR ] Database：数据库查询结果集时出现问题(在函数 getGroupsList() 中)");
			
		}
		
		DatabaseConnector.closeConnection(conn, stmt, rs);
		
		return groupsList;
		
	}
	
	public static Vector<Group> loadGroupDetails( int num ) {
		
		Connection conn = DatabaseConnector.getConnection();
		Statement stmt = DatabaseConnector.getStatement(conn);
		
		/* 预先载入数个群组信息 */
		String sql = "SELECT * FROM `t_group_base_info` LIMIT " + num;
		ResultSet rs = DatabaseConnector.query(stmt, sql); // Execute a query
		
		Vector<String> groupIds = new Vector<>();
		
		try {
			
			if (rs != null)  // 查询出现错误
				
				while (rs.next()) { // Extract data from result set
					
					groupIds.add(rs.getString("p_group_id"));
				}
			
		} catch (SQLException e) {
			
			LoggerProvider.logger.error("[ ERROR ] Database：数据库查询结果集时出现问题(在函数 loadGroupDetails() 中)");
			
			DatabaseConnector.closeConnection(conn, stmt, rs);
			
			return null;
		}
		
		Vector<Group> groups = new Vector<>();
		
		for (String groupId : groupIds) {
			groups.add(getGroupDetails(groupId));
		}
		
		return groups;
		
	}
	
	/**
	 * DESCRIPTION：创建群组
	 *
	 * @param newGroup 创建群组的信息
	 * @return 返回创建完成的群
	 */
	public static Group createGroup( Group newGroup ) {
		
		/*
		当创建新群时，为群成员表记录创建者信息，用触发器实现，以下是触发器SQL代码
		CREATE TRIGGER `insert_t_group_member` AFTER INSERT ON `t_group_base_info` FOR EACH ROW
		INSERT INTO `rP87cbTu`.`t_group_member` ( `p_group_id`, `p_group_member_id`, `p_group_member_identity` )
		VALUES	( new.p_group_id, new.p_group_creator_id, 0 );
		 */
		
		String sql = "INSERT INTO " + DatabaseConnector.getDbName()
				             + ".`t_group_base_info`(`p_group_creator_id`,`p_group_name`,`p_group_description`)"
				             + "VALUES(" + newGroup.getOwner().getId() + ","
				             + "\'" + newGroup.getName() + "\',"
				             + newGroup.getDescription() + ");";
		
		Connection conn = DatabaseConnector.getConnection();
		Statement stmt = DatabaseConnector.getStatement(conn);
		ResultSet rs = null;
		if (DatabaseConnector.update(stmt, sql) == 1) {
			// TODO 不够优雅
			rs = DatabaseConnector.query(stmt,
					"SELECT p_group_id FROM " + DatabaseConnector.getDbName() + ".`t_group_base_info`"
							+ "WHERE `p_group_name` = \'" + newGroup.getName() + "\' "
							+ "AND `p_group_creator_id`=\'" + newGroup.getOwner().getId() + "\' "
							+ "AND `p_group_description`=\'" + newGroup.getDescription() + "\'");
		}
		
		String gid = null;
		
		try {

//			ResultSet rs = stmt.getGeneratedKeys();
			
			if (rs != null) {
				
				if (rs.last())
					
					gid = rs.getString("p_group_id");
			}
			
		} catch (SQLException e) {
			
			e.printStackTrace();
			
		}
		
		Group group = getGroupDetails(gid); // 创建之后重新获取群的详细信息
		
		DatabaseConnector.closeConnection(conn, stmt);
		
		return group;
		
	}
	
	public static Group getGroupInfo( String groupId ) {
		
		/* 查询t_group_base_info表获取GID为groupId群的群信息 */
		String sql = "SELECT * FROM `t_group_base_info` WHERE `p_group_id` =" + groupId;
		
		Connection conn = DatabaseConnector.getConnection();
		Statement stmt = DatabaseConnector.getStatement(conn);
		ResultSet rs = DatabaseConnector.query(stmt, sql); // Execute a query
		
		Group group = null;
		
		try {
			
			if (rs != null) {  // 查询出现错误
				
				while (rs.next()) { // Extract data from result set
					
					group = new Group(rs.getString("p_group_id"),
							rs.getString("p_group_name"),
							rs.getString("p_group_description"),
							null);
					
					group.setOwner(
							getUserDetailsById(
									rs.getString("p_group_creator_id")));
				}
			}
			
		} catch (SQLException e) {
			
			LoggerProvider.logger.error("[ ERROR ] Database：数据库查询结果集时出现问题(在函数 getGroupInfo() 中)");
			
		}
		
		DatabaseConnector.closeConnection(conn, stmt, rs);
		
		return group;
	}
	
	/**
	 * DESCRIPTION：获取群详细信息，包括群属性与群成员列表
	 *
	 * @param groupId 需要获取群详细信息的群号
	 * @return 含有详细信息的群对象
	 */
	public static Group getGroupDetails( String groupId ) {
		
		/* 先调用获取基本信息函数 */
		Group group = getGroupInfo(groupId);
		
		if (group == null) return null;
		
		/* 查询t_group_member表获取群成员列表 */
		String sql = "SELECT `t_user_base_info`.*, p_group_member_identity,p_group_member_join_datetime "
				             + "FROM `t_group_member`,`t_user_base_info` "
				             + "WHERE `p_group_id` = " + group.getGid() + " "
				             + "AND `p_group_member_id` = `t_user_base_info`.id";
		
		Connection conn = DatabaseConnector.getConnection();
		Statement stmt = DatabaseConnector.getStatement(conn);
		ResultSet rs = DatabaseConnector.query(stmt, sql);
		
		try {
			
			if (rs != null) {  // 查询出现错误
				
				/* 群详细信息包括群用户列表，该列表仅需要用户的简要信息即可 */
				ArrayList<Account> members = new ArrayList<>();
				
				while (rs.next())  // Extract data from result set
					members.add(new AccountBuilder(
							rs.getString("id"),
							rs.getString("username"),
							rs.getString("sign")).createAccount());
				
				group.setMember(members);
			}
			
		} catch (SQLException e) {
			
			LoggerProvider.logger.error("[ ERROR ] Database：数据库查询结果集时出现问题(在函数 getGroupDetails() 中)");
		}
		
		DatabaseConnector.closeConnection(conn, stmt, rs);
		
		return group;
	}
	
	public static boolean modGroupInfo( Group group ) {
		
		boolean result = false;
		
		String sql = "UPDATE " + DatabaseConnector.getDbName() + ".`t_group_base_info` SET "
				             + "`p_group_name` = '" + group.getName() + "', "
				             + "`p_group_description` = '" + group.getDescription() + "' "
				             + " WHERE `id` = " + group.getGid();
		
		Connection conn = DatabaseConnector.getConnection();
		Statement stmt = DatabaseConnector.getStatement(conn);
		if (DatabaseConnector.update(stmt, sql) == 1) result = true;
		
		DatabaseConnector.closeConnection(conn, stmt);
		
		return result;
		
	}
	
	public static boolean quitGroup( String gid, String uid ) {
		
		boolean result = false;
		
		String sql = "DELETE FROM " + DatabaseConnector.getDbName() + ".`t_group_member` WHERE"
				             + "( `p_group_id` = " + gid + " AND `p_group_member_id` = " + uid + " ) ";
		
		Connection conn = DatabaseConnector.getConnection();
		Statement stmt = DatabaseConnector.getStatement(conn);
		if (DatabaseConnector.update(stmt, sql) == 1) result = true;
		
		DatabaseConnector.closeConnection(conn, stmt);
		
		return result;
	}
	
	public static boolean joinGroup( String gid, String uid ) {
		
		boolean result = false;
		
		String sql = "INSERT INTO " + DatabaseConnector.getDbName() + ".`t_group_member`(`p_group_id`, `p_group_member_id`) "
				             + "VALUES (" + gid + "," + uid + ")";
		
		Connection conn = DatabaseConnector.getConnection();
		Statement stmt = DatabaseConnector.getStatement(conn);
		if (DatabaseConnector.update(stmt, sql) == 1) result = true;
		
		DatabaseConnector.closeConnection(conn, stmt);
		
		return result;
	}
	
	
}
