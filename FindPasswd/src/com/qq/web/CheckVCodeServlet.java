package com.qq.web;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 处理用户输入验证码的Servlet
 * @author ZiQin
 * @version V1.0.1
 */
@WebServlet("/CheckVCodeServlet")
public class CheckVCodeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckVCodeServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			checkVcode(request, response);
		}
		catch (ServletException servletException) {
			System.out.println("[ERROR] Servlet 异常");
			servletException.printStackTrace();
		}
		catch (IOException ioException) {
			System.out.println("[ERROR] IO 异常");
			ioException.printStackTrace();
		}
	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	/**
	 * 检查用户输入的验证码服务函数
	 * @param request 请求
	 * @param response 响应
	 * @throws ServletException servlet异常
	 * @throws IOException IO异常
	 */
	private void checkVcode(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		volatile String vcode = (String) request.getSession().getAttribute("VCode"); 
		System.out.println(vcode);
		volatile String vcodeByUser;
		do {
			vcodeByUser = request.getParameter("vcodetext");
			System.out.println(vcodeByUser);
			if (!vcode.equals(vcodeByUser)) {
				request.getRequestDispatcher("recvVcode.jsp").forward(request,response);
			}
		} while (!vcode.equals(vcodeByUser));
		System.out.println("Ok");
		request.getRequestDispatcher("changeKeys.jsp").forward(request,response);
		
	}
	
}
