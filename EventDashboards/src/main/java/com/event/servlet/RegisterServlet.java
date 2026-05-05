package com.event.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		String name = request.getParameter("name");
		String email = request.getParameter("email");
		String password = request.getParameter("password");

		try {

			Class.forName("com.mysql.cj.jdbc.Driver");

			Connection con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/eventdb",
					"root",
					"password"
			);

			PreparedStatement ps = con.prepareStatement(
					"INSERT INTO users(name,email,password) VALUES(?,?,?)"
			);

			ps.setString(1, name);
			ps.setString(2, email);
			ps.setString(3, password);

			int i = ps.executeUpdate();

			if (i > 0) {
				out.println("<h2>Registration Successful!</h2>");
			} else {
				out.println("<h2>Registration Failed</h2>");
			}

			con.close();

		} catch (Exception e) {
			e.printStackTrace();
			out.println("Error: " + e.getMessage());
		}
	}
}