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
	private static final String PASSWORD = "Czh19970319!";

	private Connection connection;
	private Statement statement;

	public DatabaseOperator() {

		connection = null;
		statement = null;

	}

	public void setupDatabase() {
		try {

			// Register JDBC driver
			Class.forName(JDBC_DRIVER);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public boolean connectDatabase() {

		try {
			connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}

	public boolean disconnectDatabase() {

		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}

	public boolean connectTest() {

		if (connectDatabase() && disconnectDatabase())
			return true;
		else
			return false;
	}

	public ResultSet query(String sql) throws SQLException {

		statement = connection.createStatement();
		return statement.executeQuery(sql);

	}

	public int update(String sql) throws SQLException {

		statement = connection.createStatement();
		return statement.executeUpdate(sql);

	}

	public boolean isLoginInfoCorrect(Login loginInfo) throws SQLException {

		connectDatabase();

		try {

			// Execute a query
			ResultSet rs = query("SELECT `id`,`password` FROM `SECD`.`user`");

			// Extract data from result set
			while (rs.next()) {
				// Retrieve by column name
				if ((rs.getString("id").equals(loginInfo.getAccountId()))
						&& (rs.getString("password").equals(loginInfo.getPassword())))
					return true;
			}

		} catch (SQLException e) {

			e.printStackTrace();

		} finally {

			statement.close();
			disconnectDatabase();
			// System.out.println("Database is closed ? " + connection.isClosed()
			// + "\nstatement is closed ? " + statement.isClosed()
			// + "\n断开数据库！");

		}
		return false;
	}

	public Account getAccountById(String Id) throws SQLException {

		connectDatabase();

		ResultSet rs = query("SELECT * FROM SECD.user where id = " + Id + ";");

		Account account = new Account(rs.getString("id"), rs.getString("username"), false, null);

		disconnectDatabase();

		return account;

	}

	public ArrayList<Account> getFriendListFromDb() throws SQLException {

		ArrayList<Account> friendsList = new ArrayList<Account>();

		connectDatabase();

		try {

			// Execute a query
			ResultSet rs = query("SELECT `id`,`username` FROM `SECD`.`user`");

			// Extract data from result set
			while (rs.next()) {
				// Retrieve by column name
				friendsList.add(new Account(rs.getString("id"), rs.getString("username"), false, "HelloWorld!"));
			}

		} catch (SQLException e) {

			e.printStackTrace();

		} finally {

			statement.close();
			disconnectDatabase();

		}
		return friendsList;
	}

}
