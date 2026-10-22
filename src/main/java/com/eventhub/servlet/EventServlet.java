
package com.eventhub.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.eventhub.util.DBConnection;

@WebServlet("/events")
public class EventServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");

        try (
            Connection connection = DBConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(
                "SELECT event_id, event_name, event_date, venue, description,category "
                + "FROM events"
            )
        ) {
            var out = response.getWriter();
            out.print("[");

            boolean first = true;

            while (rs.next()) {
                if (!first) {
                    out.print(",");
                }
                first = false;

                out.print("{");
                out.print("\"id\":" + rs.getInt("event_id") + ",");
                out.print("\"name\":\"" + escapeJson(rs.getString("event_name")) + "\",");
                out.print("\"date\":\"" + rs.getDate("event_date") + "\",");
                out.print("\"venue\":\"" + escapeJson(rs.getString("venue")) + "\",");
                out.print("\"description\":\"" + escapeJson(rs.getString("description")) + "\",");
                out.print("\"category\":\"" + escapeJson(rs.getString("category")) + "\"");
                out.print("}");
            }

            out.print("]");

        } catch (Exception e) {
            throw new ServletException("Unable to load events.", e);
        }
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }

        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r");
    }
}
