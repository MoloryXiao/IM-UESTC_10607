package com.qq.web;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.qq.database.DatabaseOperator;

/**
 * 处理注册KIM号的Servlet服务
 * @author ZiQin
 * @version V1.0.0
 */
@WebServlet("/RegistorID")
public class RegistorID extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 状态常量
	 */
	private static final byte OK = 1;
	private static final byte ERROR_ID_CREATEFAILE = 2;
	private static final byte ERROR_DB_NOTCONNECT = 3;
	private static final byte ERROR_DB_INITFAILE = 4;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegistorID() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			switch (registor(request, response)) {
			case OK:
				request.getRequestDispatcher("/ShowID.jsp").forward(request, response);
				break;
			case ERROR_ID_CREATEFAILE:
			case ERROR_DB_NOTCONNECT:
			case ERROR_DB_INITFAILE:
			default:
				response.sendRedirect("Error.html");
			}
		}
		catch (IOException ioException) {
			ioException.printStackTrace();
			System.out.println("[ERROR] IO exception!");
		}
		catch (SQLException sqlException) {
			sqlException.printStackTrace();
			System.out.println("[ERROR] SQL exception");
		}
		catch (ServletException servletException) {
			servletException.printStackTrace();
			System.out.println("[ERROR] Servlet exception");
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	/**
	 * 处理注册事务
	 * @param request HTTP请求
	 * @param response HTTP响应
	 * @return 操作结果状态
	 * @throws IOException 		IO异常
	 * @throws SQLException 	数据库异常
	 * @throws ServletException Servlet异常
	 */
	private byte registor(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ServletException {
		String nickName = request.getParameter("nickname");
		String password = request.getParameter("password");
		String phoneNumber = request.getParameter("phoneNumber");
		
		DatabaseOperator databaseOperator = new DatabaseOperator();
		if (databaseOperator.setupDatabase()) {
			if (databaseOperator.connectDatabase()) {
				String statement = new String("INSERT INTO user (username, phone_number, password) VALUES ('" 
					+ nickName + "', '" + phoneNumber + "', '" + password + "');");
				if (databaseOperator.update(statement) > 0) {
					ResultSet rs = databaseOperator.query("SELECT * FROM user WHERE phone_number='" + phoneNumber + "' AND + username='" + nickName + "' AND password='" + password + "';");
					rs.next();
					String id = rs.getString("id");
					request.setAttribute("userId", id);
					databaseOperator.disconnectDatabase();
				}
				else {
					databaseOperator.disconnectDatabase();
					return ERROR_ID_CREATEFAILE;
				}
			}
			else {
				return ERROR_DB_NOTCONNECT;
			}
		}
		else {
			return ERROR_DB_INITFAILE;
		}
		return OK;
	}

}
