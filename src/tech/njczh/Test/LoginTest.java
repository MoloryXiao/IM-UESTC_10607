
package tech.njczh.Test;

import java.util.Scanner;

import com.aliyuncs.exceptions.ClientException;
import tech.njczh.Test.SmsDemo;
import java.sql.*;

public class LoginTest
{
	
	private static Scanner scanner;
	
	// JDBC driver name and database URL
	static final String	JDBC_DRIVER	= "com.mysql.jdbc.Driver";
	static final String	DB_URL		= "jdbc:mysql://39.108.95.130:3306/SECD";
	
	// Database credentials
	static final String	USER		= "root";
	static final String	PASSWORD	= "Czh19970319!";
	
	public static void main(String[] args) throws ClientException, InterruptedException
	{
		
		scanner = new Scanner(System.in);
		System.out.println("Performing registration tests...");
		System.out.println("Please enter phone number:");
		String phone_num = scanner.nextLine();
		
		String vcode = createRandomVcode();
		SmsDemo.sendsms(phone_num, vcode);
		// System.out.println(vcode);
		System.out.println("Please enter the vcode:");
		if (!vcode.equals(scanner.nextLine())) {
			System.out.println("Worry!");
			return;
		}
		
		System.out.println("Please enter username:");
		String username = scanner.nextLine();
		System.out.println("Please enter password:");
		String password = scanner.nextLine();
		
		Connection connection = null;
		Statement statement = null;
		try {
			Class.forName(JDBC_DRIVER);
			
			System.out.println("Connecting to a selected database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
			System.out.println("Connected database successfully!");
			
			System.out.println("Inserting records into the table \"user\"...");
			statement = connection.createStatement();
			String sql = "INSERT INTO SECD.user(phone_number, username, password) VALUES (\'" + phone_num + "\',\'"
					+ username + "\',\'" + password + "\')";
			statement.executeUpdate(sql);
			System.out.println("Inserted records into the table!");
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (statement != null) statement.close();
				connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			} // end finally try
		} // end try
		
	} // end main
	
	public static String createRandomVcode()
	{
		// 验证码
		String vcode = "";
		for (int i = 0; i < 6; i++) {
			vcode = vcode + (int) (Math.random() * 10);
		}
		return vcode;
	}
	
}// end LoginTest