
package com.eventhub.servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.eventhub.util.DBConnection;

@WebServlet("/register")
public class RegistrationServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String name = request.getParameter("name");
		String email = request.getParameter("email");
		String phone = request.getParameter("phone");
		String department = request.getParameter("department");
		String year = request.getParameter("year");
		String eventId = request.getParameter("event");

		response.setContentType("text/html;charset=UTF-8");

		String sql = """
				INSERT INTO students (name, email, phone, department, year)
				VALUES (?, ?, ?, ?, ?)
				""";

		try (Connection connection = DBConnection.getConnection()) {

			// Start a transaction
			connection.setAutoCommit(false);

			try {
				// 1. Insert the student
				try (PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

					ps.setString(1, name);
					ps.setString(2, email);
					ps.setString(3, phone);
					ps.setString(4, department);
					ps.setInt(5, Integer.parseInt(year));

					ps.executeUpdate();

					// 2. Get the generated student ID
					int studentId;

					try (var keys = ps.getGeneratedKeys()) {
						if (keys.next()) {
							studentId = keys.getInt(1);
						} else {
							throw new SQLException("Student ID was not generated.");
						}
					}

					// 3. Insert the registration
					String registrationSql = """
							INSERT INTO registrations
							    (student_id, event_id, registration_date)
							VALUES (?, ?, CURRENT_DATE)
							""";

					try (PreparedStatement registrationPs = connection.prepareStatement(registrationSql)) {

						registrationPs.setInt(1, studentId);
						registrationPs.setInt(2, Integer.parseInt(eventId));

						registrationPs.executeUpdate();
					}
				}

				// 4. Save both inserts together
				connection.commit();

				response.sendRedirect("success.html");

			} catch (SQLException | NumberFormatException e) {
				connection.rollback();
				throw e;
			}

		} catch (SQLException | NumberFormatException e) {
			throw new ServletException("Unable to complete event registration.", e);
		}

	}
}
