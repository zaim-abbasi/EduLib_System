package sample.repositories;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// A class to connect to database , used for  login
public class DatabaseConnection {

    //public Connection databaseLink;

    // public Connection getConnection() {

    //     // use jdbc at port 3306

    //     String url = "jdbc:mysql://localhost:3306/EDULIB_Final";
    //     String databaseUser = "root";
    //     String databasePassword = "root";

    //     try {
    //         Class.forName("com.mysql.cj.jdbc.Driver");
    //         databaseLink = DriverManager.getConnection(url, databaseUser, databasePassword);
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     return databaseLink;
    // }

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
