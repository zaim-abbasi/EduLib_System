package sample.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sample.repositories.DatabaseHandler;

import java.lang.reflect.Member;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

// This is a controller for adding members to the database
public class addMemberController implements Initializable {

    DatabaseHandler databaseHandler;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private TextField memberID;
    @FXML
    private TextField name;
    @FXML
    private TextField email;
    @FXML
    private TextField phone;
    @FXML
    private RadioButton female;
    @FXML
    private RadioButton male;
    @FXML
    private CheckBox check;
    @FXML
    private Button saveBtn;
    @FXML
    private Button cancelBtn;

    private Boolean isInEditMode = Boolean.FALSE;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        databaseHandler = new DatabaseHandler();
    }

    @FXML
    private void saveButton(ActionEvent actionEvent) {
        String mID = memberID.getText();
        String mName = name.getText();
        String mEmail = email.getText();
        String mPhone = phone.getText();
        String mGender = male.isSelected() ? "male" : (female.isSelected() ? "female" : "");

        if (isInEditMode) {
            handleEditOperation();
            return;
        }

        if (mID.isEmpty() || mName.isEmpty() || mEmail.isEmpty() || mPhone.isEmpty() || mGender.isEmpty()) {
            showAlert("ERROR", "All fields are required. Please fill them out!");
            return;
        }

        String query = "INSERT INTO addMember (memberID, name, email, phone, gender) VALUES ('"
                + mID + "', '"
                + mName + "', '"
                + mEmail + "', '"
                + mPhone + "', '"
                + mGender + "')";

        if (databaseHandler.execAction(query)) {
            showAlert("SUCCESS", "New member successfully added!");
        } else {
            showAlert("ERROR", "Sorry, we couldn't add this member!");
        }

        clear();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void handleEditOperation() {
        String gender = "female"; // Default gender set to female
        String memberId = memberID.getText();
        String memberName = name.getText();
        String memberEmail = email.getText();
        String memberPhone = phone.getText();

        // Create a Member object with the updated information
        viewMembersController.Member member = new viewMembersController.Member(memberId, memberName, memberEmail,
                memberPhone, gender);

        // Update the member in the database and show appropriate feedback
        if (databaseHandler.updateMember(member)) {
            showAlert("SUCCESS", "Success! Member updated");
        } else {
            showAlert("ERROR", "Failed! Member cannot be updated");
        }

        clear(); // Clear form fields after operation
    }

    // Clearing the window after the save button is clicked
    public void clear() {
        memberID.setText("");
        name.setText("");
        email.setText("");
        phone.setText("");
        male.setSelected(false);
        female.setSelected(false);
        check.setSelected(false);
    }

    // close the window if user clicks the close button
    @FXML
    private void cancelButton(ActionEvent actionEvent) {
        ((Stage) rootPane.getScene().getWindow()).close();
    }

    public void inflatedUI(viewMembersController.Member member) {
        // Set member details in the UI fields
        memberID.setText(member.getMemberID());
        name.setText(member.getName());
        email.setText(member.getEmail());
        phone.setText(member.getPhone());
        memberID.setEditable(false);
        isInEditMode = true;

        // Set gender radio button based on member's gender
        String gender = member.getGender().toLowerCase();
        if (gender.equals("male")) {
            male.setSelected(true);
        } else if (gender.equals("female")) {
            female.setSelected(true);
        }
    }

}
