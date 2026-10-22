
package com.eventhub.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class EventRepository {

    public static void main(String[] args) {

        String sql = "SELECT * FROM events";

        try (
            Connection connection = DBConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)
        ) {

            while (resultSet.next()) {

                int id = resultSet.getInt("event_id");
                String name = resultSet.getString("event_name");
                String venue = resultSet.getString("venue");

                System.out.println("Event ID: " + id);
                System.out.println("Event Name: " + name);
                System.out.println("Venue: " + venue);
                System.out.println("--------------------");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
