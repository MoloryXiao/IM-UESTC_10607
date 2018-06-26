package com.qq.web;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aliyuncs.exceptions.ClientException;
import com.qq.database.DatabaseOperator;
import com.qq.sms.SmsServer;

/**
 * 接收用户ID并发送验证短信的Servlet服务
 * @author ZiQin
 * @version V1.0.0
 */
@WebServlet("/FindPasswordServlet")
public class FindPasswordServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 状态常量
	 */
	private static final byte OK = 0;
	private static final byte ERROR_DB_NOTCONNECT = 1;
	private static final byte ERROR_DB_INITFAIL = 2;
	private static final byte ERROR_ID_NOTFOUND = 3;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FindPasswordServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			switch (getPasswd(request, response)) {
			case OK:
				request.getRequestDispatcher("recvVcode.jsp").forward(request,response);
				break;
			case ERROR_ID_NOTFOUND:
				response.sendRedirect("NotFound.html");
				break;
			case ERROR_DB_INITFAIL:
				response.sendRedirect("SystemError.html");
				System.out.println("[ERROR] Database init fail");
				break;
			case ERROR_DB_NOTCONNECT:
				response.sendRedirect("SystemError.html");
				System.out.println("[ERROR] Database connect fail");
				break;
			default:
				response.sendRedirect("SystemError.html");
				System.out.println("[ERROR] Other fail");
			}
		}
		catch (ServletException servletException) {
			System.out.println("[ERROR] servlet 异常");
			servletException.printStackTrace();
		}
		catch (IOException ioException) {
			System.out.println("[ERROR] IO 异常");
			ioException.printStackTrace();
		}
		catch (SQLException sqlException) {
			System.out.println("[ERROR] SQL 异常");
			sqlException.printStackTrace();
		}
		catch (ClientException clientException) {
			System.out.println("[ERROR] client 异常");
			clientException.printStackTrace();
		}
		catch (InterruptedException interruptedException) {
			System.out.println("[ERROR] interrupted 异常");
			interruptedException.printStackTrace();
		}
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	/**
	 * 获取ID并发送验证码的服务函数 
	 * @param request 请求
	 * @param response 响应
	 * @return 操作结果状态码
	 * @throws ServletException servlet 异常
	 * @throws IOException IO 异常
	 * @throws SQLException SQL 异常
	 * @throws ClientException Client 异常
	 * @throws InterruptedException 中断异常
	 */
	private byte getPasswd(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException, ClientException, InterruptedException {
		
		String id = request.getParameter("id");
		DatabaseOperator databaseOperator = new DatabaseOperator();
		if (databaseOperator.setupDatabase()) {
			if (databaseOperator.connectDatabase()) {
				ResultSet rSet = databaseOperator.query("SELECT * FROM user WHERE id=\'" + id + "\';");
				if (rSet.next()) {
					String phoneNumber = rSet.getString("phone_number");
					System.out.println(phoneNumber);
					String vcode = createRandomVcode();
					System.out.println(vcode);
					SmsServer.sendsms(phoneNumber, vcode);
					request.getSession().setAttribute("VCode", vcode);
					request.getSession().setAttribute("id",  id);
					request.getSession().setAttribute("db", databaseOperator);
					return OK;
				}
				else {
					databaseOperator.disconnectDatabase();
					return ERROR_ID_NOTFOUND;
				}
			}
			else {
				return ERROR_DB_NOTCONNECT;
			}
		}
		else {
			return ERROR_DB_INITFAIL;
		}
		
	}
	
	/**
	 * 生成验证码的服务函数
	 * @return 验证码
	 */
	private String createRandomVcode()
	{
		String vcode = "";
		for (int i = 0; i < 6; i++) {
			vcode = vcode + (int) (Math.random() * 10);
		}
		return vcode;
	}

}
