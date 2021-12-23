package final_project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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



public class ShowCourse {

    private Connection connection;

    private final Button btShow = new Button("Show");
    private final Button btCancel = new Button("Cancel");
    private final TextField tfSSN = new TextField();

    public String show(Connection connection_) {
        if (connection_ == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Alert");
            alert.setContentText("You need to connect to database first");
            alert.showAndWait();

            return "";
        }
        this.connection = connection_;
        VBox vBoxAll = new VBox(5);
        HBox hBox1 = new HBox(4);
        HBox hBox2 = new HBox(4);
        hBox1.getChildren().add(new Label("SSN : "));
        hBox1.getChildren().add(tfSSN);
        HBox.setHgrow(tfSSN, Priority.ALWAYS);
        hBox2.getChildren().add(btCancel);
        hBox2.getChildren().add(btShow);
        hBox2.setAlignment(Pos.TOP_RIGHT);
        vBoxAll.getChildren().addAll(hBox1, hBox2);
        vBoxAll.setPadding(new Insets(10, 10, 10, 10));

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);

        Scene scene = new Scene(vBoxAll);
        stage.setScene(scene);
        stage.setMinWidth(400);
        btCancel.setOnAction(e -> stage.close());
        btShow.setOnAction(e -> handleShowButton(stage));
        stage.showAndWait();
        return row;
    }
    int getStudent(String SSN) {
        int ret = -1;
        String query = "select ssn from Students where ssn LIKE ? ;";
        PreparedStatement preparedStmt;

        try {
            preparedStmt = connection.prepareStatement(query);
            preparedStmt.setString(1, SSN);
            ResultSet rs = preparedStmt.executeQuery();
            while (rs.next()) {
                ret = rs.getInt("ssn");
            }
            rs.close();

            preparedStmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(AddStudent.class.getName()).log(Level.SEVERE, null, ex);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Exception when trying to add student");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
            return -1;

        }

        return ret;
    }

    String row="";

    void handleShowButton(Stage stage) {
        row="";
        if (tfSSN.getText().length() == 0) {
            return;
        }
        int ssn = getStudent(tfSSN.getText().trim());
        System.out.println(ssn);
        if (ssn < 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Student found");
            alert.setContentText("No Student found with this SSN");
            alert.showAndWait();

            return;
        }
        String query = "SELECT firstName, lastName, courseId, grade FROM Students st, Enrollment en WHERE st.ssn=? AND en.ssn=?;";
        PreparedStatement preparedStmt;

        try {
            preparedStmt = connection.prepareStatement(query);
            preparedStmt.setInt(1, ssn);
            preparedStmt.setInt(2, ssn);
            ResultSet rs = preparedStmt.executeQuery();

            row+=F("Last Name");
            row+=F("First Name");
            row+=F("Course Title",40);
            row+=F("Grade");
            row+="\n";

            while (rs.next()) {
                row+=F(rs.getString("firstName")+ "");
                row+=F(rs.getString("lastName")+ "");
                row+=F(rs.getString("courseId")+ "");
                row+=F(rs.getString("grade")+ "");
                row+="\n";
            }
            rs.close();
            preparedStmt.close();
            stage.close();

        } catch (SQLException ex) {
            Logger.getLogger(AddStudent.class.getName()).log(Level.SEVERE, null, ex);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Exception when trying to add student");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }

    }
    String F(String string){

        return String.format("%-20s", string);
    }
    String F(String string,int characters){

        return String.format("%-"+characters+"s", string);
    }
}
