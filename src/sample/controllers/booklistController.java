package sample.controllers;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.repositories.DatabaseConnection;
import sample.repositories.DatabaseHandler;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class booklistController implements Initializable {

    DatabaseHandler databaseHandler;
    ObservableList<viewMembersController.Member> list = FXCollections.observableArrayList();
    @FXML
    private TableView<Book> tableView;
    @FXML
    private TableColumn<Book, String> BookIDColumn;
    @FXML
    private TableColumn<Book, String> TitleColumn;
    @FXML
    private TableColumn<Book, String> AuthorColumn;
    @FXML
    private TableColumn<Book, String> PublisherColumn;
    @FXML
    private TableColumn<Book, Integer> QuantityColumn;
    @FXML
    private TableColumn<Book, Boolean> AvailabilityColumn;

    public void deleteBookOption(ActionEvent actionEvent) {
        Book selectedBook = tableView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert("No book selected", "Please select a book for deletion.", Alert.AlertType.ERROR);
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Deleting Book");
        confirmAlert.setContentText("Are you sure you want to delete " + selectedBook.getTitle() + "?");
        Optional<ButtonType> answer = confirmAlert.showAndWait();

        if (answer.filter(buttonType -> buttonType == ButtonType.OK).isPresent()) {
            Boolean deletionResult = DatabaseHandler.getInstance().deleteBook(selectedBook);
            showAlert(deletionResult ? "Book Deleted!" : "Error",
                    deletionResult ? "The selected book has been deleted." : "Book could not be deleted.",
                    deletionResult ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
            if (deletionResult) {
                list.remove(selectedBook);
            }
        } else {
            showAlert("Deletion Cancelled", "Book deletion process cancelled.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String header, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void editBookOption(ActionEvent actionEvent) {
        Book selectedBook = tableView.getSelectionModel().getSelectedItem();

        if (selectedBook == null) {
            showAlert("No book selected", "Please select a book for editing.", Alert.AlertType.ERROR);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/views/addBook.fxml"));
            Parent parent = loader.load();

            addBookController controller = loader.getController();
            controller.inflatedBUI(selectedBook);

            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Edit Book");
            stage.setScene(new Scene(parent));
            stage.show();

        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void handleRefresh(ActionEvent actionEvent) {
        loadData();
    }

    public static class Book {
        private final SimpleStringProperty BookID;
        private final SimpleStringProperty Title;
        private final SimpleStringProperty Author;
        private final SimpleStringProperty Publisher;
        private final SimpleIntegerProperty Quantity;
        private final SimpleBooleanProperty Availability;

        public Book(String BookID, String Title, String Author, String Publisher, Integer Quantity,
                Boolean Availability) {
            this.BookID = new SimpleStringProperty(BookID);
            this.Title = new SimpleStringProperty(Title);
            this.Author = new SimpleStringProperty(Author);
            this.Publisher = new SimpleStringProperty(Publisher);
            this.Quantity = new SimpleIntegerProperty(Quantity);
            this.Availability = new SimpleBooleanProperty(Availability);

        }

        public String getBookID() {
            return BookID.get();
        }

        public String getTitle() {
            return Title.get();
        }

        public String getAuthor() {
            return Author.get();
        }

        public String getPublisher() {
            return Publisher.get();
        }

        public Integer getQuantity() {
            return Quantity.get();
        }

        public Boolean getAvailability() {
            return Availability.get();
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initCol();
        loadData();
    }

    private void initCol() {
        BookIDColumn.setCellValueFactory(new PropertyValueFactory<>("BookID"));
        TitleColumn.setCellValueFactory(new PropertyValueFactory<>("Title"));
        AuthorColumn.setCellValueFactory(new PropertyValueFactory<>("Author"));
        PublisherColumn.setCellValueFactory(new PropertyValueFactory<>("Publisher"));
        QuantityColumn.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
        AvailabilityColumn.setCellValueFactory(new PropertyValueFactory<>("Availability"));

    }

    private void loadData() {
        ObservableList<Book> list = FXCollections.observableArrayList();
        DatabaseHandler handler = DatabaseHandler.getInstance();
        String query = "SELECT * FROM addBook";
        ResultSet resultSet = handler.execQuery(query);

        try {
            while (resultSet.next()) {
                String bookID = resultSet.getString("id");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                String publisher = resultSet.getString("publisher");
                int quantity = resultSet.getInt("quantity");
                boolean availability = resultSet.getBoolean("isAvail");

                list.add(new Book(bookID, title, author, publisher, quantity, availability));
            }
        } catch (SQLException ex) {
            Logger.getLogger(addBookController.class.getName()).log(Level.SEVERE, null, ex);
        }

        tableView.setItems(list);
    }

    @FXML
    private void addNewBook(javafx.event.ActionEvent actionEvent) {

        loadWindow("/sample/views/addBook.fxml", "Add Book");
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

}
