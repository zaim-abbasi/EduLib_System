package sample.controllers;

import javafx.beans.property.*;
import javafx.collections.*;

import javafx.event.ActionEvent;
import javafx.fxml.*;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.repositories.DatabaseHandler;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

//Controller for the main window
public class MainController implements Initializable {

    Connection conn;
    DatabaseHandler databaseHandler;
    PieChart bookChart;
    PieChart memberChart;
    ObservableList<Issue> issueData = FXCollections.observableArrayList();
    boolean isReadyForSubmission = false;

    @FXML
    private TextField bookID;
    @FXML
    private TextField memberID;
    @FXML
    private StackPane memberInfoContainer;
    @FXML
    private StackPane bookInfoContainer;
    @FXML
    private TextField bookIdInput;
    @FXML
    private Text bookName;
    @FXML
    private Text authorName;
    @FXML
    private Text availability;
    @FXML
    private TextField memberIdInput;
    @FXML
    private Text contact;
    @FXML
    private Text memberName;
    @FXML
    private StackPane rootPane;
    @FXML
    private TableView<Issue> tableView;
    @FXML
    private TableColumn<Issue, String> idColumn;
    @FXML
    private TableColumn<Issue, String> nameColumn;
    @FXML
    private TableColumn<Issue, String> emailColumn;
    @FXML
    private TableColumn<Issue, String> bidColumn;
    @FXML
    private TableColumn<Issue, String> titleColumn;
    @FXML
    private TableColumn<Issue, String> authorColumn;
    @FXML
    private TableColumn<Issue, String> issueDateColumn;
    @FXML
    private TableColumn<Issue, String> renewColumn;

    private void initCol() {
        issueDateColumn.setCellValueFactory(new PropertyValueFactory<>("issueTime"));
        renewColumn.setCellValueFactory(new PropertyValueFactory<>("renew_count"));
        bidColumn.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("memberID"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

    }

    @FXML
    private void loadBookInfo2() {
        issueData.clear();
        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();

        String bookID = this.bookID.getText();
        String memberID = this.memberID.getText();

        if (bookID.isEmpty() || memberID.isEmpty()) {
            showEmptyFieldsError();
            return;
        }

        String issueQuery = "SELECT * FROM issuedBooks WHERE bookID = '" + bookID + "' and memberID='" + memberID + "'";
        ResultSet issueResultSet = databaseHandler.execQuery(issueQuery);

        try {
            if (!issueResultSet.next()) {
                showIssueNotFoundError(bookID, memberID);
                clearOnSubmissionIssueEntries();
                return;
            }

            do {
                String issueTime = issueResultSet.getTimestamp("issueTime").toString();
                int renewCount = issueResultSet.getInt("renew_count");

                String bookQuery = "SELECT * FROM addBook WHERE id = '" + bookID + "'";
                String memberQuery = "SELECT * FROM addMember WHERE memberID = '" + memberID + "'";

                ResultSet bookResultSet = databaseHandler.execQuery(bookQuery);
                ResultSet memberResultSet = databaseHandler.execQuery(memberQuery);

                if (bookResultSet.next() && memberResultSet.next()) {
                    String title = bookResultSet.getString("title");
                    String author = bookResultSet.getString("author");
                    String name = memberResultSet.getString("name");
                    String email = memberResultSet.getString("email");

                    issueData.add(new Issue(issueTime, renewCount, bookID, title, author, memberID, name, email));
                }
            } while (issueResultSet.next());

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        isReadyForSubmission = true;
        tableView.setItems(issueData);
    }

    private void showEmptyFieldsError() {
        showAlert("ERROR", "You must select Book ID and Member ID to view the information!");
    }

    private void showIssueNotFoundError(String bookID, String memberID) {
        showAlert("ERROR", "The book with ID: " + bookID + " wasn't issued to member with ID: " + memberID + " !");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL rl, ResourceBundle rb) {
        databaseHandler = DatabaseHandler.getInstance();
        initGraph();
        initCol();
    }

    // Showing the graphs
    private void initGraph() {
        bookChart = new PieChart(databaseHandler.getBookGraphicStatistics());
        memberChart = new PieChart(databaseHandler.getMemberGraphicStatistics());
        bookInfoContainer.getChildren().add(bookChart);
        memberInfoContainer.getChildren().add(memberChart);
    }

    // Refreshing the graphs
    public void refreshGraphs() {
        bookChart.setData(databaseHandler.getBookGraphicStatistics());
        memberChart.setData(databaseHandler.getMemberGraphicStatistics());
    }

    // Hiding/Showing the graphs
    private void hideShowGraph(Boolean status) {
        bookChart.setOpacity(status ? 1 : 0);
        memberChart.setOpacity(status ? 1 : 0);
    }

    // Loading the corresponding windows when buttons are clicked
    @FXML
    private void loadAddMember(javafx.event.ActionEvent actionEvent) {
        loadWindow("/sample/views/addMember.fxml", "Add Member");
    }

    @FXML
    private void loadAddBook(javafx.event.ActionEvent actionEvent) {
        loadWindow("/sample/views/addBook.fxml", "Add Book");
    }

    @FXML
    private void loadViewMembers(javafx.event.ActionEvent actionEvent) {
        loadWindow("/sample/views/viewMembers.fxml", "Members List");
    }

    @FXML
    private void loadViewBooks(javafx.event.ActionEvent actionEvent) {
        loadWindow("/sample/views/booklist.fxml", "Book list");
    }

    @FXML
    private void loadViewIssuedBooks(javafx.event.ActionEvent actionEvent) {
        loadWindow("/sample/views/issuedBooks.fxml", "Issued Books");
    }

    void loadWindow(String loc, String title) {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource(loc));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle(title);
            stage.setScene(new Scene(parent));
            stage.getIcons().add(new Image("https://static.thenounproject.com/png/3314579-200.png"));
            stage.setResizable(false);
            stage.show();

        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void loadBookInfo(ActionEvent actionEvent) throws SQLException {
        clearBookcache();

        String id = bookIdInput.getText();
        String query = "SELECT * FROM addBook WHERE id='" + id + "'";
        ResultSet rs = databaseHandler.execQuery(query);
        Boolean found = false;

        try {
            while (rs.next()) {
                displayBookInfo(rs);
                found = true;
            }
            if (!found) {
                showAlert("Book not found", "No such book with this ID is found!");
                bookIdInput.clear();
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void clearBookcache() {
        bookName.setText("");
        authorName.setText("");
        availability.setText("");
    }

    void displayBookInfo(ResultSet rs) throws SQLException {
        hideShowGraph(false);
        String bName = rs.getString("title");
        String bAuthor = rs.getString("author");
        Integer bStatus = rs.getInt("quantity");

        bookName.setVisible(true);
        bookName.setText(bName);
        authorName.setVisible(true);
        authorName.setText(bAuthor);
        String status = (bStatus > 0) ? "Available" : "Not Available";
        availability.setVisible(true);
        availability.setText(status);
    }

    @FXML
    private void loadMemberInfo(ActionEvent actionEvent) throws SQLException {
        clearMembercache();

        String id = memberIdInput.getText();
        String query = "SELECT * FROM addMember WHERE memberID='" + id + "'";
        ResultSet rs = databaseHandler.execQuery(query);
        Boolean found = false;

        try {
            while (rs.next()) {
                displayMemberInfo(rs);
                found = true;
            }
            if (!found) {
                showAlert("Member not found", "No such member with this ID is found!");
                memberIdInput.clear();
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void clearMembercache() {
        memberName.setText("");
        contact.setText("");
    }

    void displayMemberInfo(ResultSet rs) throws SQLException {
        hideShowGraph(false);
        String mName = rs.getString("name");
        String mMobile = rs.getString("email");

        memberName.setVisible(true);
        memberName.setText(mName);
        contact.setVisible(true);
        contact.setText(mMobile);
    }

    // Handling the menu items at menu bar
    @FXML
    private void handleMenuClose(ActionEvent actionEvent) {
        ((Stage) rootPane.getScene().getWindow()).close();
    }

    @FXML
    private void handleAddMember(javafx.event.ActionEvent actionEvent) {
        loadWindow("/sample/views/addMember.fxml", "Add Member");
    }

    @FXML
    private void handleAddBook(javafx.event.ActionEvent actionEvent) {
        loadWindow("/sample/views/addBook.fxml", "Add Book");
    }

    @FXML
    private void handleViewMembers(javafx.event.ActionEvent actionEvent) {
        loadWindow("/sample/views/viewMembers.fxml", "View Members");
    }

    @FXML
    private void handleViewBooks(javafx.event.ActionEvent actionEvent) {
        loadWindow("/sample/views/booklist.fxml", "View Books");
    }

    @FXML
    private void handleViewIssuedBooks(ActionEvent actionEvent) {
        loadWindow("/sample/views/issuedBooks.fxml", "View Issued Books");
    }

    @FXML
    private void aboutHandler(ActionEvent actionEvent) {
        loadWindow("/sample/views/aboutUs.fxml", "About Us");
    }

    // Here we handle the issue book functionality
    @FXML
    private void issueHandler(ActionEvent actionEvent) throws SQLException {
        String memberID = memberIdInput.getText();
        String bookID = bookIdInput.getText();
        String isAvailable = availability.getText();

        if (memberID.isEmpty() || bookID.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText(null);
            alert.setContentText("You must select Book ID and Member ID to issue a book!");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Issue Operation");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to issue the book titled '" + bookName.getText() + "' to '"
                    + memberName.getText() + "' ?");

            Optional<ButtonType> response = alert.showAndWait();
            if (response.get() == ButtonType.OK) {
                String query = "SELECT memberID,bookID from issuedBooks where memberID='" + memberID + "'" +
                        "and bookID ='" + bookID + "'";
                ResultSet result = databaseHandler.execQuery(query);

                try {
                    if (result.next()) {
                        Alert bookIsAlreadyIssuedAlert = new Alert(Alert.AlertType.ERROR);
                        bookIsAlreadyIssuedAlert.setTitle("Duplicate Entry");
                        bookIsAlreadyIssuedAlert.setHeaderText(null);
                        bookIsAlreadyIssuedAlert
                                .setContentText("This book was once issued to this member. You can renew it!");
                        bookIsAlreadyIssuedAlert.showAndWait();
                        clearIssueEntries();
                        hideShowGraph(true);
                        return;
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                if (isAvailable == "Available") {
                    String query1 = "INSERT INTO issuedBooks(bookID,memberID) VALUES("
                            + "'" + bookID + "',"
                            + "'" + memberID + "')";

                    String query2 = "UPDATE addBook SET  quantity=quantity-1  where id='" + bookID + "'";

                    if (databaseHandler.execAction(query1) && databaseHandler.execAction(query2)) {
                        refreshGraphs();
                        Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                        alert1.setTitle("SUCCESS");
                        alert1.setHeaderText(null);
                        alert1.setContentText("Book was issued successfully!");
                        alert1.showAndWait();
                        clearIssueEntries();
                        hideShowGraph(true);

                        String query3 = "SELECT quantity from addBook where id='" + bookID + "'";
                        ResultSet rs = databaseHandler.execQuery(query3);
                        if (rs.next()) {
                            int qty = rs.getInt("quantity");
                            if (qty == 0) {
                                String query4 = "UPDATE addBook SET  isAvail=false  where id='" + bookID + "'";
                                databaseHandler.execAction(query4);
                            }
                        }

                    } else {
                        Alert alert2 = new Alert(Alert.AlertType.ERROR);
                        alert2.setTitle("FAILED");
                        alert2.setHeaderText(null);
                        alert2.setContentText("Sorry, we couldn't issue the book!");
                        alert2.showAndWait();
                        clearIssueEntries();
                        hideShowGraph(true);
                    }
                } else {
                    Alert alert3 = new Alert(Alert.AlertType.ERROR);
                    alert3.setTitle("FAILED");
                    alert3.setHeaderText(null);
                    alert3.setContentText("This book isn't available!");
                    alert3.showAndWait();
                    clearIssueEntries();
                    hideShowGraph(true);
                }
            } else if (response.get() == ButtonType.CANCEL) {
                Alert alert4 = new Alert(Alert.AlertType.INFORMATION);
                alert4.setTitle("CANCELED");
                alert4.setHeaderText(null);
                alert4.setContentText("Book issue was canceled!");
                alert4.showAndWait();
                clearIssueEntries();
                hideShowGraph(true);
            }
        }
    }

    // Clearing issue book information
    private void clearIssueEntries() {
        bookIdInput.clear();
        memberIdInput.clear();
        clearBookcache();
        clearMembercache();
    }

    // Clearing the info
    private void clearOnSubmissionIssueEntries() {
        bookID.clear();
        memberID.clear();
    }

    // Submission tab
    @FXML
    private void loadOnSubmissionOp(ActionEvent event) {
        if (!isReadyForSubmission) {
            showSubmissionError("Please Select A Book To Submit!");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Submission Operation");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to return the book?");
        Optional<ButtonType> response = confirmAlert.showAndWait();

        if (response.isPresent() && response.get() == ButtonType.OK) {
            String bookID = this.bookID.getText();
            String memberID = this.memberID.getText();
            String deleteQuery = "DELETE FROM issuedBooks WHERE bookID = '" + bookID + "' AND memberID = '" + memberID
                    + "'";
            String updateQuery = "UPDATE addBook SET quantity = quantity + 1 WHERE id = '" + bookID + "'";

            if (databaseHandler.execAction(deleteQuery) && databaseHandler.execAction(updateQuery)) {
                showSuccessMessage("Book Has Been Submitted!");
            } else {
                showFailedMessage("Submission Has Been Failed!");
            }
        } else {
            showInfoMessage("Submission Operation canceled!");
        }

        clearOnSubmissionIssueEntries();
        isReadyForSubmission = false;
    }

    // Renewal
    @FXML
    private void loadRenewOp(ActionEvent event) {
        if (!isReadyForSubmission) {
            showSubmissionError("Please Select A Book To Renew!");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Renew Operation");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to renew the book?");
        Optional<ButtonType> response = confirmAlert.showAndWait();

        if (response.isPresent() && response.get() == ButtonType.OK) {
            String updateQuery = "UPDATE issuedBooks SET issueTime = CURRENT_TIMESTAMP, " +
                    "renew_count = renew_count + 1 WHERE bookID = '" + bookID.getText() + "'";
            System.out.println(updateQuery);

            if (databaseHandler.execAction(updateQuery)) {
                showSuccessMessage("Book has been renewed!");
            } else {
                showFailedMessage("Renew Has Been Failed!");
            }
        } else {
            showInfoMessage("Renew Operation canceled!");
        }

        clearOnSubmissionIssueEntries();
        isReadyForSubmission = false;
    }

    private void showSubmissionError(String message) {
        showAlert("Failed", message, Alert.AlertType.ERROR);
    }

    private void showSuccessMessage(String message) {
        showAlert("Success", message, Alert.AlertType.INFORMATION);
    }

    private void showFailedMessage(String message) {
        showAlert("Failed", message, Alert.AlertType.ERROR);
    }

    private void showInfoMessage(String message) {
        showAlert("Canceled", message, Alert.AlertType.INFORMATION);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Logging the admin out if he clicks the log out menu item
    @FXML
    private void handleMenuLogOut(ActionEvent actionEvent) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("../views/login.fxml"));
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.setScene(new Scene(parent));
        stage.show();
    }

    // Logging the admin out if he clicks the log out button
    @FXML
    private void logoutAction(ActionEvent actionEvent) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("../views/login.fxml"));
        Scene scene = new Scene(parent);
        Stage primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Issue Class
    public static class Issue {
        private final SimpleStringProperty memberID;
        private final SimpleStringProperty name;
        private final SimpleStringProperty email;
        private final SimpleStringProperty bookID;
        private final SimpleStringProperty title;
        private final SimpleStringProperty author;
        private final SimpleStringProperty issueTime;
        private final SimpleIntegerProperty renew_count;

        public Issue(String issueTime, Integer renew_count, String bookID, String title, String author, String memberID,
                String name, String email) {
            this.memberID = new SimpleStringProperty(memberID);
            this.name = new SimpleStringProperty(name);
            this.email = new SimpleStringProperty(email);
            this.bookID = new SimpleStringProperty(bookID);
            this.title = new SimpleStringProperty(title);
            this.author = new SimpleStringProperty(author);
            this.issueTime = new SimpleStringProperty(issueTime);
            this.renew_count = new SimpleIntegerProperty(renew_count);
        }

        public String getMemberID() {
            return memberID.get();
        }

        public String getName() {
            return name.get();
        }

        public String getEmail() {
            return email.get();
        }

        public String getBookID() {
            return bookID.get();
        }

        public String getTitle() {
            return title.get();
        }

        public String getAuthor() {
            return author.get();
        }

        public String getIssueTime() {
            return issueTime.get();
        }

        public Integer getRenew_count() {
            return renew_count.get();
        }
    }

}