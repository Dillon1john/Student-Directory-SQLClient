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


public class AddCourse {

    private Connection connection;
    private Button add_button = new Button("Add");
    private Button cancel_button = new Button("Cancel");
    private TextField tfSSN = new TextField();
    private ComboBox cbCourseId = new ComboBox();
    private ComboBox cbGrade = new ComboBox();

    void show(Connection connection_) {
        if (connection_ == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Alert");
            alert.setContentText("You need to connect to database first");
            alert.showAndWait();

            return;
        }
        this.connection = connection_;
        VBox vBoxAll = new VBox(5);
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

        Label l2 = new Label("Course ID : ");
        l2.setMinWidth(70);
        hb3.getChildren().add(l2);
        hb3.getChildren().add(cbCourseId);
        cbCourseId.setMinWidth(150);

        Label l3 = new Label("Grade : ");
        l3.setMinWidth(70);
        hb4.getChildren().add(l3);
        hb4.getChildren().add(cbGrade);

        hb3.setHgrow(cbCourseId, Priority.ALWAYS);
        cbGrade.getItems().add("A");
        cbGrade.getItems().add("B");
        cbGrade.getItems().add("C");
        cbGrade.getItems().add("D");
        cbGrade.getItems().add("F");


        vBoxAll.getChildren().addAll(hb1, hb3, hb4, hb2);
        vBoxAll.setPadding(new Insets(10, 10, 10, 10));

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        cancel_button.setOnAction(e -> stage.close());
        add_button.setOnAction(e -> handleAddButton(stage));
        Scene scene = new Scene(vBoxAll);
        stage.setScene(scene);
        stage.setMinWidth(400);
        fillCourses();
        stage.showAndWait();
    }
    private Hashtable<String, Integer> courses = new Hashtable<String, Integer>();

    int getStudent(String SSN){
        int ret=-1;
        cbCourseId.getItems().clear();
        String query = "select ssn from Students where ssn LIKE ? ;";
        PreparedStatement preparedStmt;

        try {
            preparedStmt = connection.prepareStatement(query);
            preparedStmt.setString(1, SSN);
            ResultSet rs = preparedStmt.executeQuery();
            while (rs.next()) {
                ret= rs.getInt("ssn");
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

    void fillCourses() {
        cbCourseId.getItems().clear();
        String query = "select courseID, subjectID from Course;";
        PreparedStatement preparedStmt;

        try {
            preparedStmt = connection.prepareStatement(query);
            ResultSet rs = preparedStmt.executeQuery();
            while (rs.next()) {
                cbCourseId.getItems().add(rs.getString("courseID"));
                courses.put(rs.getString("courseID"),rs.getInt("subjectID"));
            }
            rs.close();
            preparedStmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(AddStudent.class.getName()).log(Level.SEVERE, null, ex);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Exception when trying to add student");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();

        }
    }

    void handleAddButton(Stage stage) {

        if (tfSSN.getText().length() > 0 && cbCourseId.getValue()!=null && cbGrade.getValue()!=null) {
            String subjectID = cbCourseId.getValue().toString();
            Integer courseID = courses.get(cbCourseId.getValue().toString());


            if(courseID==null){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("No Course found with this ID");
                alert.setContentText(cbCourseId.getValue().toString());
                alert.showAndWait();
                return;
            }
            String grade=cbGrade.getValue().toString();
            int studentSSN=getStudent(tfSSN.getText().trim());
            if(studentSSN<0){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("No Student found with this SSN");
                alert.setContentText("No Student found with this SSN");
                alert.showAndWait();
                return;
            }

            String query = " INSERT INTO Enrollment (ssn, courseId, dateRegistered, grade) "
                    + " values (?, ?, ?, ?);";
            PreparedStatement preparedStmt;
            long millis=System.currentTimeMillis();
            java.sql.Date CurrentDate=new java.sql.Date(millis);

            System.out.println(courseID.intValue());
            System.out.println(courses);

            try {
                preparedStmt = connection.prepareStatement(query);
                preparedStmt.setInt(1, studentSSN);
                preparedStmt.setString(2, subjectID);
                preparedStmt.setDate(3, CurrentDate);
                preparedStmt.setString(4, grade);
                preparedStmt.execute();
                System.out.println(preparedStmt);
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
            alert.setContentText("All fields must be filled!");
            alert.showAndWait();

        }
    }
}

