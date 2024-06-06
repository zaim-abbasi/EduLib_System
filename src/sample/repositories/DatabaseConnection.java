package sample.repositories;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    public static Connection connection;
    public static Connection getConnection() {
    if (connection == null) {
      try {
        String url = "jdbc:mysql://localhost:3306/EDULIB_Final";
        String user = "root";
        String password = "root";
        connection = DriverManager.getConnection(url, user, password);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return connection;
  }
}
