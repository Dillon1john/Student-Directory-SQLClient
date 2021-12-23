package final_project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class DropCourse {

    private Connection connection;
    private Button btShow = new Button("Show");
    private Button btDrop = new Button("Drop");
    private Button btCancel = new Button("Cancel");
    private TextField tfSSN = new TextField();
    private ComboBox cbCourseId = new ComboBox();

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

        Label l1 = new Label("SSN : ");
        l1.setMinWidth(70);


        hb1.getChildren().add(l1);
        hb1.getChildren().add(tfSSN);
        hb1.getChildren().add(btShow);
        hb1.setHgrow(tfSSN, Priority.ALWAYS);
        hb2.getChildren().add(btCancel);
        hb2.getChildren().add(btDrop);
        hb2.setAlignment(Pos.TOP_RIGHT);

        Label l2 = new Label("Course ID : ");
        l2.setMinWidth(70);
        hb3.getChildren().add(l2);
        hb3.getChildren().add(cbCourseId);
        cbCourseId.setMinWidth(150);
        hb3.setHgrow(cbCourseId, Priority.ALWAYS);
        vBoxContainer.getChildren().addAll(hb1, hb3, hb2);
        vBoxContainer.setPadding(new Insets(10, 10, 10, 10));

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);

        Scene scene = new Scene(vBoxContainer);
        stage.setScene(scene);
        stage.setMinWidth(400);
        btCancel.setOnAction(e -> stage.close());
        btShow.setOnAction(e -> fillCourses());
        btDrop.setOnAction(e -> handleDropButton(stage));
        stage.showAndWait();

    }

    int getStudent(String SSN) {
        int res_ssn = -1;
        cbCourseId.getItems().clear();
        String query = "select ssn from Students where ssn LIKE ? ;";
        PreparedStatement preparedStmt;

        try {
            preparedStmt = connection.prepareStatement(query);
            preparedStmt.setString(1, SSN);
            ResultSet res_set = preparedStmt.executeQuery();

            while (res_set.next()) {
                res_ssn = res_set.getInt("ssn");
            }
            res_set.close();

            preparedStmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(AddStudent.class.getName()).log(Level.SEVERE, null, ex);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Exception when trying to add student");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
            return -1;

        }

        return res_ssn;
    }

    private Hashtable<String, Integer> courses = new Hashtable<String, Integer>();

    void fillCourses() {
        if(tfSSN.getText().length()==0)return ;
        courses.clear();
        cbCourseId.getItems().clear();
        int studentSSN=getStudent(tfSSN.getText().trim());

        if(studentSSN<0){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Student found");
            alert.setContentText("No Student found with this SSN");
            alert.showAndWait();

            return;
        }
        String query = "select distinct t1.ssn,t2.courseID from Enrollment as t1,course as t2 where t1.courseID=t2.courseID and ssn= ? ;";
        PreparedStatement preparedStmt;

        try {
            preparedStmt = connection.prepareStatement(query);
            preparedStmt.setInt(1, studentSSN);
            ResultSet res_set = preparedStmt.executeQuery();


            while (res_set.next()) {
                cbCourseId.getItems().add(res_set.getString("courseID"));
            }
            res_set.close();
            preparedStmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(AddStudent.class.getName()).log(Level.SEVERE, null, ex);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Exception when trying to add student");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();

        }
    }
    void handleDropButton(Stage stage) {

        if (tfSSN.getText().length() > 0 && cbCourseId.getValue()!=null ) {
            String sid= cbCourseId.getValue().toString();
            if(sid==null){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("No Course found with this ID");
                alert.setContentText(cbCourseId.getValue().toString());
                alert.showAndWait();
                return;
            }
            int studentSSN=getStudent(tfSSN.getText().trim());
            if(studentSSN<0){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("No Student found with this SSN");
                alert.setContentText("No Student found with this SSN");
                alert.showAndWait();
                return;
            }

            String query = " delete from Enrollment where ssn=? and courseID=?";
            PreparedStatement preparedStmt;
            System.out.println(sid);
            try {
                preparedStmt = connection.prepareStatement(query);
                preparedStmt.setInt(1, studentSSN);
                preparedStmt.setString(2, sid);
                preparedStmt.execute();
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
