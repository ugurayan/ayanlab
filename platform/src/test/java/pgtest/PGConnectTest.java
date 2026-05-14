package pgtest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class PGConnectTest {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:postgresql://147.93.138.158:5432/postgres";
        String user = "core";
        String pass = "UgurAyanLabs9602.";
        System.out.println("Connecting to: " + url + " as " + user);
        try (Connection c = DriverManager.getConnection(url, user, pass);
             Statement s = c.createStatement()) {
            try (ResultSet rs = s.executeQuery("SELECT version()")) {
                if (rs.next()) System.out.println("PG: " + rs.getString(1));
            }
            System.out.println("Connection OK");
        }
    }
}

