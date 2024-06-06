package sample.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sample.repositories.DatabaseHandler;

import java.net.URL;
import java.util.ResourceBundle;

public class addBookController implements Initializable {

    DatabaseHandler databaseHandler;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private TextField title;
    @FXML
    private TextField id;
    @FXML
    private TextField author;
    @FXML
    private TextField publisher;
    @FXML
    private TextField quantity;
    @FXML
    private Button saveBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private CheckBox check;

    private Boolean isInEditMode = Boolean.FALSE;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        databaseHandler = new DatabaseHandler();
    }

    @FXML
    private void saveAction(ActionEvent actionEvent) {
        String bookId = id.getText();
        String bookName = title.getText();
        String bookAuthor = author.getText();
        String bookPublisher = publisher.getText();
        String bookQuantity = quantity.getText();

        if (isInEditMode) {
            handleEditOperation();
            return;
        }

        if (areFieldsEmpty(bookId, bookName, bookAuthor, bookPublisher, bookQuantity)) {
            showAlert("ERROR", "All fields are required. Please fill them out!");
            return;
        }

        addBookToDatabase(bookId, bookName, bookAuthor, bookPublisher, bookQuantity);
        clear();
    }

    private boolean areFieldsEmpty(String bookId, String bookName, String bookAuthor, String bookPublisher,
            String bookQuantity) {
        return bookId.isEmpty() || bookAuthor.isEmpty() || bookName.isEmpty() || bookPublisher.isEmpty()
                || bookQuantity.isEmpty() || !check.isSelected();
    }

    private void addBookToDatabase(String bookId, String bookName, String bookAuthor, String bookPublisher,
            String bookQuantity) {
        String query = String.format(
                "INSERT INTO addBook (id, title, author, publisher, quantity, isAvail) VALUES ('%s', '%s', '%s', '%s', '%s', true)",
                bookId, bookName, bookAuthor, bookPublisher, bookQuantity);

        if (databaseHandler.execAction(query)) {
            showAlert("SUCCESS", "New book successfully added!");
        } else {
            showAlert("ERROR", "Sorry, we couldn't add this book!");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void handleEditOperation() {
        booklistController.Book book = new booklistController.Book(
                id.getText(),
                title.getText(),
                author.getText(),
                publisher.getText(),
                Integer.parseInt(quantity.getText()),
                true);

        if (databaseHandler.updateBook(book)) {
            showAlert("SUCCESS", "Success! Book updated");
        } else {
            showAlert("ERROR", "Failed! Book cannot be updated");
        }

        clear();
    }

    // Clearing the fields after save button is clicked
    void clear() {
        id.setText("");
        title.setText("");
        author.setText("");
        publisher.setText("");
        quantity.setText("");
        check.setSelected(false);
    }

    // Closing the window when the close button is clicked
    @FXML
    private void cancelAction(ActionEvent actionEvent) {
        ((Stage) rootPane.getScene().getWindow()).close();
    }

    public void inflatedBUI(booklistController.Book book) {
        setBookFields(book);
        isInEditMode = true;
        id.setEditable(false);
    }

    private void setBookFields(booklistController.Book book) {
        id.setText(book.getBookID());
        author.setText(book.getAuthor());
        publisher.setText(book.getPublisher());
        title.setText(book.getTitle());
        quantity.setText(book.getQuantity().toString());
    }

}
