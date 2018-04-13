package com.qq.web;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.qq.database.DatabaseOperator;

/**
 * 处理修改密码的Servlet服务
 * @author ZiQin
 * @version V 1.0.0
 */
@WebServlet("/ChangeKey")
public class ChangeKey extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	/**
	 * 状态常量
	 */
	private static final byte SUCCESS = 0;
	private static final byte FAIL = 1;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChangeKey() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {
			switch (change(request, response)) {
			case SUCCESS:
				response.sendRedirect("Success.html");
				break;
			case FAIL:
			default:
				response.sendRedirect("Fail.html");
			}
		}
		catch (IOException ioException) {
			System.out.println("[ERROR] IO exception");
			ioException.printStackTrace();
		}
		catch (SQLException sqlException) {
			System.out.println("[ERROR] SQL exception");
			sqlException.printStackTrace();
		}
		catch (ServletException servletException) {
			System.out.println("[ERROR] servlet exception");
			servletException.printStackTrace();
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	/**
	 * 修改数据库端密码的逻辑服务
	 * @param request 请求
	 * @param response 响应
	 * @return 操作结果状态码
	 * @throws ServletException Servlet异常
	 * @throws IOException IO异常
	 * @throws SQLException SQL异常
	 */
	private byte change(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
		String id = (String) this.getServletContext().getAttribute("id");
		DatabaseOperator databaseOperator = (DatabaseOperator) this.getServletContext().getAttribute("db");
		String key = request.getParameter("key");
		String stat = new String("UPDATE user SET password='" + key + "' WHERE id='" + id + "';");
		if (databaseOperator.update(stat) > 0) {
			databaseOperator.disconnectDatabase();
			return SUCCESS;
		}
		else {
			databaseOperator.disconnectDatabase();
			return FAIL;
		}
	}

}
