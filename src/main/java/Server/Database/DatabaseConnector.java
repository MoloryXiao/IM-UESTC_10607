package Server.Database;

import Server.util.LoggerProvider;
import org.apache.commons.dbcp2.BasicDataSourceFactory;

import java.io.IOException;
import java.util.Properties;
import java.sql.*;
import javax.sql.DataSource;

/**
 * @author 97njczh
 * @version 2018/04/29 使用连接池管理连接，实现连接池配置由文件读取
 */
public class DatabaseConnector {
	
	private static final String configFile = "DBCPconfig.properties";
	
	private static DataSource dataSource;
	
	private static String DB_NAME;
	
	public static String getDbName() {
		
		return DB_NAME;
	}
	
	/**
	 * 启动数据库，并初始化连接池 Version 2018/04/29
	 *
	 * @return 数据库与连接池是否就绪，是返回true
	 */
	public static boolean setupDatabase() {
		
		boolean result = false;
		
		try {
			
			// 读取配置文件，并加载配置文件
			Properties dbProperties = new Properties();
			dbProperties.load(DatabaseConnector.class.getClassLoader().getResourceAsStream(configFile));
			
			// 按照配置文件的设置初始化连接池（其中包含了加载jdbc驱动）
			dataSource = BasicDataSourceFactory.createDataSource(dbProperties);
			
			// 连接测试，并获取数据库名
			Connection connection = getConnection();
			Statement statement = connection.createStatement();
			ResultSet resultSet = query(statement, "select database();");
			
			resultSet.next();
			DB_NAME = resultSet.getString(1);
			
			DatabaseMetaData dbMd = connection.getMetaData();
			
			LoggerProvider.logger.info("[  O K  ] 数据库已就绪：" + dbMd.getDatabaseProductName()
					                           + "（版本：" + dbMd.getDatabaseProductVersion() + "）");
			
			closeConnection(connection, statement, resultSet);
			
			result = true;
			
		} catch (ClassNotFoundException e) { // 无法启动加载数据库驱动，核心逻辑无法实现
			
			LoggerProvider.logger.error("[ ERROR ] Databse：JDBC_DRIVER 加载错误，数据库驱动启动失败！");
			
		} catch (IOException e) {
			
			LoggerProvider.logger.error("[ ERROR ] Database：获取数据库配置错误！异常：" + e.getMessage());
			
		} catch (SQLException e) {
			
			LoggerProvider.logger.error("[ ERROR ] Database：获取元数据错误！异常：" + e.getMessage());
			
		} catch (Exception e) {
			
			LoggerProvider.logger.error("[ ERROR ] Database：初始化连接池失败！异常：" + e.getMessage());
		}
		
		return result;
	}
	
	/* ======================================[ OPEN CONNECTION ]==================================== */
	
	/**
	 * create date: 2018/04/29
	 * 从连接池中获取一个连接
	 *
	 * @return 从连接池中获取的一个连接
	 */
	public static Connection getConnection() {
		
		Connection connection = null;
		
		try {
			// 从连接池中获取一个连接
			connection = dataSource.getConnection();
			
		} catch (SQLException e) {
			
			LoggerProvider.logger.error("[ ERROR ] Database：由连接池中获取数据库连接失败！");
			
		}
		
		return connection;
	}
	
	public static Statement getStatement( Connection conn ) {
		
		Statement statement = null;
		
		try {
			
			statement = conn.createStatement();
			
		} catch (SQLException e) {
			
			LoggerProvider.logger.error("[ ERROR ] Database：创建Statement时出现错误！");
			
		}
		
		return statement;
	}
	
	
	/* ======================================[ CLOSE CONNECTION ]=================================== */
	
	public static void closeResultSet( ResultSet rs ) {
		
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				LoggerProvider.logger.error(
						"[ ERROR ] Database：关闭数据库ResultSet时出现错误！错误信息：" + e.getMessage());
			}
		}
	}
	
	public static void closeStatement( Statement stmt ) {
		
		if (stmt != null) {
			try {
				stmt.close();
			} catch (Exception e) {
				LoggerProvider.logger.error(
						"[ ERROR ] Database：关闭数据库Statement时出现错误！错误信息：" + e.getMessage());
			}
		}
	}
	
	public static void closeConnection( Connection conn ) {
		
		try {
			if (conn != null && !conn.isClosed()) {
				// conn.setAutoCommit(true);
				conn.close();   // 并不真正关闭，而是释放回连接池
			}
		} catch (Exception e) {
			LoggerProvider.logger.error(
					"[ ERROR ] Database：关闭数据库连接时出现错误！错误信息：" + e.getMessage());
		}
	}
	
	public static void closeStatement( Statement stmt, ResultSet rs ) {
		
		closeResultSet(rs);
		closeStatement(stmt);
	}
	
	public static void closeConnection( Connection conn, Statement stmt ) {
		
		closeStatement(stmt);
		closeConnection(conn);
	}
	
	public static void closeConnection( Connection conn, Statement stmt, ResultSet rs ) {
		
		closeResultSet(rs);
		closeStatement(stmt);
		closeConnection(conn);
	}
	
	
	/* =========================================[ OPERATION ]====================================== */
	
	public static ResultSet query( Statement statement, String sql ) {
		
		try {
			
			return statement.executeQuery(sql);
			
		} catch (SQLException e) {
			
			LoggerProvider.logger.error(
					"[ ERROR ] Database：SQL Query 查询时发生错误！请检查语法：\n" + sql);
			return null;
			
		}
	}
	
	public static int update( Statement statement, String sql ) {
		
		try {
			
			return statement.executeUpdate(sql);
			
		} catch (SQLException e) {
			
			LoggerProvider.logger.error(
					"[ ERROR ] Database：SQL Update 查询时发生错误！请检查语法：\n" + sql);
			return -1;
			
		}
	}
	
}
