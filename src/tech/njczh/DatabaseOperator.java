/**
 * 
 */

package tech.njczh;

import java.sql.*;
import java.util.ArrayList;

import tech.njczh.Server.Account;
import tech.njczh.Server.Login;

/**
 * @author 97njczh
 *
 */
public class DatabaseOperator {

	// JDBC driver name and database URL
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DB_URL = "jdbc:mysql://39.108.95.130:3306/SECD?useSSL=false";

	// Database credentials
	private static final String USER = "root";
	private static final String PASSWORD = "root1234";

	private Connection connection;
	private Statement statement;

	public DatabaseOperator() {

		connection = null;
		statement = null;

	}

	public boolean setupDatabase() {
		try {

			// Register JDBC driver
			Class.forName(JDBC_DRIVER);
			return true;

		} catch (ClassNotFoundException e) {
			// e.printStackTrace();
			System.out.println("[ ERROR ] JDBC_DRIVER 加载错误");
			return false;

		}
	}

	public boolean connectDatabase() {

		try {

			connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
			return true;

		} catch (SQLException e) {
			// e.printStackTrace();
			System.out.println("[ ERROR ] 连接数据库时出现错误！");
			return false;

		}
	}

	public boolean disconnectDatabase() {

		try {

			connection.close();
			return true;

		} catch (SQLException e) {
			// e.printStackTrace();
			System.out.println("[ ERROR ] 关闭数据库时出现错误！");
			return false;

		}
	}

	public boolean connectTest() {

		if (connectDatabase() && disconnectDatabase())
			return true;
		else
			return false;
	}

	public ResultSet query(String sql) {

		try {
			statement = connection.createStatement();
			return statement.executeQuery(sql);
		} catch (SQLException e) {
			System.out.println("[ ERROR ] SQL查询时发生错误！请检查语法："+sql);
			return null;
		}
		// } finally {
		// try {
		// if (statement != null)
		// statement.close();
		// } catch (SQLException e) {
		// e.printStackTrace();
		// }
		// }

	}

	public int update(String sql) throws SQLException {

		try {
			statement = connection.createStatement();
			return statement.executeUpdate(sql);
		} catch (SQLException e) {
			return -1;
		}
		// finally {
		// try {
		// if (statement != null)
		// statement.close();
		// } catch (SQLException e) {
		// e.printStackTrace();
		// }
		// }

	}

	/**
	 * 判断用户登陆信息是否正确
	 * 
	 * @param loginInfo
	 * @return 登陆成功，返回Account用户信息，否则返回null
	 * @throws SQLException
	 */
	public Account isLoginInfoCorrect(Login loginInfo) throws SQLException {

		connectDatabase();

		Account loginAccount = null;

		ResultSet rs = query("SELECT `id`, `username`,`sign` FROM `SECD`.`user` WHERE id = " + loginInfo.getAccountId()
				+ " AND `password` = \'" + loginInfo.getPassword() + "\'");// Execute a query

		if (rs != null) { // 查询出现错误
			if (rs.next()) // Extract data from result set
				// Retrieve by column name
				loginAccount = new Account(rs.getString("id"), rs.getString("username"), true, rs.getString("sign")); 
		}
		
		disconnectDatabase();

		return loginAccount;
	}

	public Account getAccountById(String Id) throws SQLException {

		connectDatabase();

		ResultSet rs = query("SELECT * FROM SECD.user where id = " + Id + ";");
		rs.next();
		Account account = new Account(rs.getString("id"), rs.getString("username"), false, rs.getString("sign"));

		disconnectDatabase();

		return account;

	}

	public ArrayList<Account> getFriendListFromDb(String id) throws SQLException {

		ArrayList<Account> friendsList = new ArrayList<Account>();

		connectDatabase();

		ResultSet rs = query(
				"SELECT 	SlaveID,	username,	sign \n" + "FROM 	SECD.relationship a,	SECD.`user` b \n"
						+ "WHERE	a.MasterId = " + id + " AND b.id = a.SlaveID;");// Execute a query

		while (rs.next()) { // Extract data from result set
			// Retrieve by column name
			boolean onlineFlag;
			if (ThreadDatabase.serverThreadDb.containsKey(rs.getString(1)))
				onlineFlag = true;
			else
				onlineFlag = false;
			friendsList.add(new Account(rs.getString(1), rs.getString(2), onlineFlag, rs.getString(3)));
		}

		disconnectDatabase();

		return friendsList;
	}

}
