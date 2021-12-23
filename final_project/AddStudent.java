package final_project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class AddStudent {

    private Connection connection;
    private Button add_button = new Button("Add");
    private Button cancel_button = new Button("Cancel");
    private TextField tfSSN = new TextField();
    private TextField tfFirstName = new TextField();
    private TextField tfLastName = new TextField();

    void show(Connection connection_) {

        if (connection_ == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Alert");
            alert.setContentText("You need to connect to database first");
            alert.showAndWait();
            return;
        }
        this.connection = connection_;
        VBox vBoxContainer = new VBox(5);
        HBox hb1 = new HBox(4);
        HBox hb2 = new HBox(4);
        HBox hb3 = new HBox(4);
        HBox hb4 = new HBox(4);

        Label l1 = new Label("SSN : ");
        l1.setMinWidth(70);
        hb1.getChildren().add(l1);
        hb1.getChildren().add(tfSSN);
        hb1.setHgrow(tfSSN, Priority.ALWAYS);
        hb2.getChildren().add(cancel_button);
        hb2.getChildren().add(add_button);
        hb2.setAlignment(Pos.TOP_RIGHT);

        Label l2 = new Label("First Name : ");
        l2.setMinWidth(70);
        hb3.getChildren().add(l2);
        hb3.getChildren().add(tfFirstName);
        hb3.setHgrow(tfFirstName, Priority.ALWAYS);

        Label l3 = new Label("Last Name : ");
        l3.setMinWidth(70);
        hb4.getChildren().add(l3);
        hb4.getChildren().add(tfLastName);

        hb3.setHgrow(tfFirstName, Priority.ALWAYS);
        hb4.setHgrow(tfLastName, Priority.ALWAYS);

        vBoxContainer.getChildren().addAll(hb1, hb3, hb4, hb2);
        vBoxContainer.setPadding(new Insets(10, 10, 10, 10));

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);

        cancel_button.setOnAction(e -> stage.close());
        add_button.setOnAction(e -> handleAddButton(stage));
        Scene scene = new Scene(vBoxContainer);
        stage.setScene(scene);
        stage.setMinWidth(400);

        stage.showAndWait();

    }

    void handleAddButton(Stage stage) {
        if (tfSSN.getText().length() > 0 && tfFirstName.getText().length() > 0 && tfLastName.getText().length() > 0) {
            String query = " INSERT INTO Students (ssn, firstName, lastName) "
                    + " values (?, ?, ?);";
            PreparedStatement preparedStmt;
            System.out.println(tfSSN.getText());
            try {
                preparedStmt = connection.prepareStatement(query);
                preparedStmt.setString(1, tfSSN.getText());
                preparedStmt.setString(2, tfFirstName.getText());
                preparedStmt.setString(3, tfLastName.getText());
                preparedStmt.execute();
                preparedStmt.close();
                stage.close();

            } catch (SQLException ex) {
                Logger.getLogger(AddStudent.class.getName()).log(Level.SEVERE, null, ex);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Exception when trying to add student");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();

            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Alert");
            alert.setContentText("You need to fill all fields");
            alert.showAndWait();

        }
    }

}

