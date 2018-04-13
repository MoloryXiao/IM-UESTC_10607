package com.qq.database;

import java.sql.*;

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
	
	public boolean setupDatabase() {
		
		try {
			
			Class.forName(JDBC_DRIVER);            // Register JDBC driver
			return true;
			
		} catch (ClassNotFoundException e) {
			
			System.out.println("[ ERROR ] JDBC_DRIVER 加载错误，数据库驱动启动失败！");
			return false;
			
		}
	}
	
	public boolean connectDatabase() {
		
		try {
			
			connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
			return true;
			
		} catch (SQLException e) {
			
			System.out.println("[ ERROR ] 连接数据库时出现错误！");
			return false;
			
		}
	}
	
	public boolean disconnectDatabase() {
		
		try {
			
			connection.close();
			return true;
			
		} catch (SQLException e) {
			
			System.out.println("[ ERROR ] 关闭数据库时出现错误！");
			return false;
			
		}
	}
	
	public boolean connectTest() {
		
		return (connectDatabase() && disconnectDatabase());
		
	}
	
	public ResultSet query( String sql ) {
		
		try {
			
			statement = connection.createStatement();
			return statement.executeQuery(sql);
			
		} catch (SQLException e) {
			
			System.out.println("[ ERROR ] SQL Query 查询时发生错误！请检查语法：" + sql);
			return null;
			
		}
		
	}
	
	public int update( String sql ) throws SQLException {
		
		try {
			
			statement = connection.createStatement();
			return statement.executeUpdate(sql);
			
		} catch (SQLException e) {
			
			System.out.println("[ ERROR ] SQL Update 查询时发生错误！请检查语法：" + sql);
			return -1;
			
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
				System.out.println("[ ERROR ] 数据库中查无此ID：" + Id);
			}
			
		}
		
		disconnectDatabase();
		
		return account;
		
	}
	
}