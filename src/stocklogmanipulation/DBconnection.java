package stocklogmanipulation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBconnection {
    private String url = "jdbc:mysql://db-identifier-test.crqca2sci3j1.ap-southeast-2.rds.amazonaws.com:3306/stocklogmanipulation";
    private String user = "root";
    private String password = "alicewhdghk124";
    private Connection connection;

    public DBconnection() {
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}