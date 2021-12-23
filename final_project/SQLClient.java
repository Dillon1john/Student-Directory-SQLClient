package final_project;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import java.sql.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SQLClient extends Application {
    private Connection connection;
    private Statement statement;
    private final TextArea tasqlCommand = new TextArea();
    private TextArea taSQLResult = new TextArea();
    private TextField tfUsername = new TextField();
    private PasswordField pfPassword = new PasswordField();
    private ComboBox cboURL = new ComboBox<>();
    private ComboBox cboDriver = new ComboBox<>();
    private Button btExecuteSQL = new Button("Execute SQL Command");
    private Button btClearSQLCommand = new Button("Clear");
    private Button btConnectDB = new Button("Connect to Database");
    private Button btClearSQLResult = new Button("Clear Result");
    private Label lblConnectionStatus = new Label("No connection now");

    @Override // Override the start method in the Application class
    public void start(Stage primaryStage) {
        cboURL.getItems()
                .addAll(FXCollections.observableArrayList("jdbc:sqlserver://s16988308.onlinehome-server.com:1433;databaseName=CUNY_DB;integratedSecurity=false",
                        "jdbc:mysql://localhost/javabook",
                        "jdbc:mysql://liang.armstrong.edu/javabook",
                        "jdbc:odbc:exampleMDBDataSource",
                        "jdbc:oracle:thin:@liang.armstrong.edu:1521:orcl"));
        cboURL.getSelectionModel().selectFirst();
        cboDriver.getItems().addAll(FXCollections.observableArrayList("com.microsoft.sqlserver.jdbc.SQLServerDriver",
                "com.mysql.jdbc.Driver", "sun.jdbc.odbc.dbcOdbcDriver",
                "oracle.jdbc.driver.OracleDriver"));
        cboDriver.getSelectionModel().selectFirst();
// Menu and menu items
        Menu m1 = new Menu("Database");
        MenuItem ctd = new MenuItem("Connect to Database");
        MenuItem esql = new MenuItem("Execute SQL");
        m1.getItems().addAll(ctd, esql);

        Menu m2 = new Menu("Clear");
        MenuItem csql = new MenuItem("Clear SQL");
        MenuItem cr = new MenuItem("Clear Results");
        m2.getItems().addAll(csql, cr);

        Menu m3 = new Menu("Registration");
        MenuItem ac = new MenuItem("Add Course");
        MenuItem dc = new MenuItem("Drop Course");
        MenuItem sc = new MenuItem("Show Courses");
        m3.getItems().addAll(ac,dc,sc);


        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(m1, m2,m3);

        ac.setOnAction(e -> new AddCourse().show(connection));
        dc.setOnAction(e -> new DropCourse().show(connection));
        sc.setOnAction(e -> {
            ShowCourse showCourse = new ShowCourse();
            String result=showCourse.show(connection);
            taSQLResult.setText(result);
        });

        ctd.setOnAction(e -> connectToDB());
        esql.setOnAction(e -> executeSQL());
        csql.setOnAction(e -> tasqlCommand.setText(null));

        cr.setOnAction(e -> taSQLResult.setText(null));
        GridPane gp = new GridPane();
        gp.add(cboURL, 1, 0);
        gp.add(cboDriver, 1, 1);
        gp.add(tfUsername, 1, 2);
        gp.add(pfPassword, 1, 3);
        gp.add(new Label("JDBC Driver"), 0, 0);
        gp.add(new Label("Database URL"), 0, 1);
        gp.add(new Label("Username"), 0, 2);
        gp.add(new Label("Password"), 0, 3);
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(25);
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setPercentWidth(75);
        gp.getColumnConstraints().addAll(c1, c2);
        HBox hBoxConnection = new HBox();
        hBoxConnection.getChildren().addAll(lblConnectionStatus, btConnectDB);
        hBoxConnection.setAlignment(Pos.CENTER_RIGHT);

        VBox vBoxConnection = new VBox(5);
        vBoxConnection.getChildren().addAll(menuBar, new Label("Enter Database Information"),
                gp, hBoxConnection);
        gp.setStyle("-fx-border-color: black;");
        HBox hBoxSQLCommand = new HBox(5);
        hBoxSQLCommand.getChildren().addAll(btClearSQLCommand, btExecuteSQL);
        hBoxSQLCommand.setAlignment(Pos.CENTER_RIGHT);
        BorderPane borderPaneSqlCommand = new BorderPane();
        borderPaneSqlCommand.setTop(new Label("Enter an SQL Command"));
        borderPaneSqlCommand.setCenter(new ScrollPane(tasqlCommand));
        borderPaneSqlCommand.setBottom(hBoxSQLCommand);
        HBox hBoxConnectionCommand = new HBox(10);
        hBoxConnectionCommand.getChildren().addAll(vBoxConnection, borderPaneSqlCommand);
        BorderPane borderPaneExecutionResult = new BorderPane();
        borderPaneExecutionResult.setTop(new Label("SQL Execution Result"));
        borderPaneExecutionResult.setCenter(taSQLResult);
        borderPaneExecutionResult.setBottom(btClearSQLResult);
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(hBoxConnectionCommand);
        borderPane.setCenter(borderPaneExecutionResult);
// Create a scene and place it in the stage
        Scene scene = new Scene(borderPane, 1000, 400);
        primaryStage.setTitle("Dillon's SQL Client"); // Set the stage title
        primaryStage.setScene(scene); // Place the scene in the stage
        primaryStage.show(); // Display the stage
        btConnectDB.setOnAction(e -> connectToDB());
        btExecuteSQL.setOnAction(e -> executeSQL());
        btClearSQLCommand.setOnAction(e -> tasqlCommand.setText(null));
        btClearSQLResult.setOnAction(e -> taSQLResult.setText(null));
    }

    /**
     * Connect to DB
     */
    private void connectToDB() {
// Get database information from the user input
        String driver = cboDriver.getSelectionModel().getSelectedItem().toString();
        String url = cboURL.getSelectionModel().getSelectedItem().toString();
        String username = tfUsername.getText().trim();
        String password = pfPassword.getText().trim();

// Connection to the database
        try {
            System.out.println(driver);
            Class.forName(driver);
            connection = DriverManager.getConnection(url, username, password);
            lblConnectionStatus.setText("Connected to " + url);
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Execute SQL commands
     */
    private void executeSQL() {
        if (connection == null) {
            taSQLResult.setText("Please connect to a database first");
            return;
        } else {
            String sqlCommands = tasqlCommand.getText().trim();
            String[] commands = sqlCommands.replace('\n', ' ').split(";");
            for (String aCommand : commands) {
                if (aCommand.trim().toUpperCase().startsWith("SELECT")) {
                    processSQLSelect(aCommand);
                } else {
                    processSQLNonSelect(aCommand);
                }
            }
        }
    }

    /**
     * Execute SQL SELECT commands
     */
    private void processSQLSelect(String sqlCommand) {
        try {
// Get a new statement for the current connection
            statement = connection.createStatement();
// Execute a SELECT SQL command
            ResultSet resultSet = statement.executeQuery(sqlCommand);
// Find the number of columns in the result set
            int columnCount = resultSet.getMetaData().getColumnCount();
            String row = "";
// Display column names
            for (int i = 1; i <= columnCount; i++) {
                row += resultSet.getMetaData().getColumnName(i) + "\t";
            }
            taSQLResult.appendText(row + '\n');
            while (resultSet.next()) {
// Reset row to empty
                row = "";
                for (int i = 1; i <= columnCount; i++) {
// A non-String column is converted to a string
                    row += resultSet.getString(i) + "\t";
                }
                taSQLResult.appendText(row + '\n');
            }
        } catch (SQLException ex) {
            taSQLResult.setText(ex.toString());
        }
    }


    private void processSQLNonSelect(String sqlCommand) {
        try {
// Get a new statement for the current connection
            statement = connection.createStatement();
// Execute a non-SELECT SQL command
            statement.executeUpdate(sqlCommand);
            taSQLResult.setText("SQL command executed");
        } catch (SQLException ex) {
            taSQLResult.setText(ex.toString());
        }
    }

    public static void main(String[] args) {
        launch();
    }

}