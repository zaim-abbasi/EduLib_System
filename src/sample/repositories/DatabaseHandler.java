package sample.repositories;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import sample.controllers.booklistController;
import sample.controllers.viewMembersController;
import sample.controllers.booklistController.Book;

import javax.swing.*;

import java.sql.*;

public final class DatabaseHandler {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/EDULIB_Final";

    private static DatabaseHandler handler = null;
    private static Connection conn = null;
    private static Statement statement = null;

    public DatabaseHandler() {
        createConnection();
    }

    // public static DatabaseHandler getInstance() {
    //     if (handler == null) {
    //         handler = new DatabaseHandler();
    //     }
    //     return handler;
    // }


    public static synchronized DatabaseHandler getInstance() {
        if (handler == null) {
            handler = new DatabaseHandler();
        }
        return handler;
    }

    // Connecting to the database
    void createConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String password = "root";
            String userName = "root";
            conn = DriverManager.getConnection(DB_URL, userName, password);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public ResultSet execQuery(String query) {
        try {
            return conn.createStatement().executeQuery(query);
        } catch (SQLException ex) {
            System.out.println("Exception at execQuery: " + ex.getMessage());
            return null;
        }
    }

    public boolean execAction(String query) {
        try {
            conn.createStatement().execute(query);
            return true;
        } catch (SQLException ex) {
            System.out.println("Exception at execAction: " + ex.getMessage());
            return false;
        }
    }

    // Graphs
    public ObservableList<PieChart.Data> getBookGraphicStatistics() {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();

        try {
            int totalBooks = getBookCount("SELECT COUNT(*) FROM addBook");
            data.add(new PieChart.Data("Total Books (" + totalBooks + ")", totalBooks));

            int issuedBooks = getBookCount("SELECT COUNT(*) FROM issuedBooks");
            data.add(new PieChart.Data("Issued Copies Of Books (" + issuedBooks + ")", issuedBooks));

        } catch (SQLException ex) {
            System.out.println("Error fetching book statistics: " + ex.getMessage());
        }

        return data;
    }

    private int getBookCount(String query) throws SQLException {
        ResultSet rs = execQuery(query);
        return rs.next() ? rs.getInt(1) : 0;
    }

    public ObservableList<PieChart.Data> getMemberGraphicStatistics() {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();

        try {
            int totalMembers = getmemberCount("SELECT COUNT(*) FROM addMember");
            data.add(new PieChart.Data("Total Members (" + totalMembers + ")", totalMembers));

            int membersWithBooks = getmemberCount("SELECT COUNT(DISTINCT memberID) FROM issuedBooks");
            data.add(new PieChart.Data("Members With Books (" + membersWithBooks + ")", membersWithBooks));

        } catch (SQLException ex) {
            System.out.println("Error fetching member statistics: " + ex.getMessage());
        }

        return data;
    }

    private int getmemberCount(String query) throws SQLException {
        ResultSet rs = execQuery(query);
        return rs.next() ? rs.getInt(1) : 0;
    }

    public boolean deleteMember(viewMembersController.Member member) {
        String deleteStatement = "DELETE FROM addMember WHERE memberID = ?";
        try (PreparedStatement statement = conn.prepareStatement(deleteStatement)) {
            statement.setString(1, member.getMemberID());
            return statement.executeUpdate() == 1;
        } catch (SQLException ex) {
            // Handle or log the exception
            System.out.println("Exception while deleting member: " + ex.getMessage());
            return false;
        }
    }

    public boolean deleteBook(booklistController.Book book) {
        String deleteStatement = "DELETE FROM addBook WHERE id = ?";
        try (PreparedStatement statement = conn.prepareStatement(deleteStatement)) {
            statement.setString(1, book.getBookID());
            return statement.executeUpdate() == 1;
        } catch (SQLException ex) {
            // Handle or log the exception
            System.out.println("Exception while deleting book: " + ex.getMessage());
            return false;
        }
    }

    // Updating members
    public boolean updateMember(viewMembersController.Member updatedMember) {
        String updateQuery = "UPDATE addMember SET name = ?, email = ?, phone = ? WHERE memberID = ?";
        try (PreparedStatement statement = conn.prepareStatement(updateQuery)) {
            statement.setString(1, updatedMember.getName());
            statement.setString(2, updatedMember.getEmail());
            statement.setString(3, updatedMember.getPhone());
            statement.setString(4, updatedMember.getMemberID());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            System.out.println("Error updating member: " + ex.getMessage());
            return false;
        }
    }

    // Updating books
    public boolean updateBook(booklistController.Book updatedBook) {
        String updateQuery = "UPDATE addBook SET title = ?, author = ?, publisher = ?, quantity = ? WHERE id = ?";
        try (PreparedStatement statement = conn.prepareStatement(updateQuery)) {
            statement.setString(1, updatedBook.getTitle());
            statement.setString(2, updatedBook.getAuthor());
            statement.setString(3, updatedBook.getPublisher());
            statement.setInt(4, updatedBook.getQuantity());
            statement.setString(5, updatedBook.getBookID());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            System.out.println("Error updating book: " + ex.getMessage());
            return false;
        }
    }
}
