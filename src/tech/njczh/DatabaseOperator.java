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
			return null;
		}
//		} finally {
//			try {
//				if (statement != null)
//					statement.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}

	}

	public int update(String sql) throws SQLException {

		try {
			statement = connection.createStatement();
			return statement.executeUpdate(sql);
		} catch (SQLException e) {
			return -1;
		}
//		 finally {
//			try {
//				if (statement != null)
//					statement.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}

	}

	public boolean isLoginInfoCorrect(Login loginInfo) throws SQLException {

		connectDatabase();

		ResultSet rs = query("SELECT `id`,`password` FROM `SECD`.`user`"); // Execute a query

		while (rs.next()) { // Extract data from result set

			if ((rs.getString("id").equals(loginInfo.getAccountId()))
					&& (rs.getString("password").equals(loginInfo.getPassword()))) // Retrieve by column name
				return true;
		}

		disconnectDatabase();

		return false;
	}

	public Account getAccountById(String Id) throws SQLException {

		connectDatabase();

		ResultSet rs = query("SELECT * FROM SECD.user where id = " + Id + ";");
		rs.next();

		Account account = new Account(rs.getString("id"), rs.getString("username"), false, rs.getString("sign"));

		disconnectDatabase();

		return account;

	}

	public ArrayList<Account> getFriendListFromDb() throws SQLException {

		ArrayList<Account> friendsList = new ArrayList<Account>();

		connectDatabase();

		ResultSet rs = query("SELECT *	 FROM `SECD`.`user`");// Execute a query

		while (rs.next()) { // Extract data from result set
			// Retrieve by column name
			friendsList.add(new Account(rs.getString("id"), rs.getString("username"), false, rs.getString("sign")));
		}

		disconnectDatabase();

		return friendsList;
	}

}
