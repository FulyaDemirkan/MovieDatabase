////////////////////////////////////////////////////////////////////////////////
//
// FXMLAssignment1Controller.java
// ==================
// A stand-alone Java application to display all the movies by selected year, 
// genre or name.
//
// AUTHOR:  F. Fulya Demirkan (demirkaf@sheridancollege.ca)
// CREATED: 30/Sep/2017
// UPDATED: 06/Oct/2017
//
////////////////////////////////////////////////////////////////////////////////
package fd;

import fd.sql.JdbcHelper;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

public class FXMLAssignment1Controller implements Initializable
{

    @FXML
    private Label lblError, lblOptionInfo, lblOptionError;

    @FXML
    private Button btnConnect, btnSearch;

    @FXML
    private TextField txtUser, txtUrl;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private ComboBox ddlYear, ddlGenre;

    @FXML
    private TableView<Movie> tblMovies;

    @FXML
    private TableColumn<Movie, String> genreColumn;

    @FXML
    private TextField txtTitle;

       // new jdbcHelper object
    JdbcHelper jdbc = new JdbcHelper();

    // connection success variable for toggleButtons() method
    boolean check = false;

    // preparedStatement parameter list
    ArrayList<Object> params = new ArrayList();

    // hashmap for storing genres
    HashMap<Integer, String> genreList = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        /*
        Adds a confirmation alert to window close (X) button. If user confirms 
        disconnects from db.
         */
        Platform.runLater(new Runnable()
        {
            // abstract run method from Runnable class
            @Override
            public void run()
            {
                // get the value of the property Window
                Window win = btnConnect.getScene().getWindow();
                // add an eventhandler for close requests
                win.setOnCloseRequest(new EventHandler<WindowEvent>()
                {
                    /*
                    Display a confirmation alert
                     */
                    @Override
                    public void handle(WindowEvent event)
                    {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                                "Are you sure you want to exit?",
                                ButtonType.YES, ButtonType.NO);
                        alert.setHeaderText(null);
                        alert.setTitle("Exit Program");
                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.get().equals(ButtonType.YES))
                        {
                            jdbc.disconnect();
                            Platform.exit();
                        } else
                        {
                            event.consume();
                        }
                    }
                });
            }
        });
        
        // disable search buttons
        toggleButtons(true);
        
    }

    ////////////////////////////////////////////////////////////////////////////
    // Makes a connection to db. If connection successfully established, 
    // toggles search area members to enabled, connect button text to disconnect, 
    // creates options, and clears any previous error messages. 
    // If connection is already established clicking on disconnect button 
    // disconnects from db, changes button text to connect and toggles search 
    // area members to disabled. 
    // If connection is unsuccessful displays an error message.
    ////////////////////////////////////////////////////////////////////////////
    public void connection(ActionEvent event) throws Exception
    {
        // if connection has not established yet connect, else disconnect
        if (!check)
        {
            check = jdbc.connect(txtUrl.getText(), txtUser.getText(), txtPassword.getText());
            if (check)
            {
                btnConnect.setText("Disconnect");
                getGenres();
                createOptions();
                lblError.setText("");
                toggleButtons(false);
                ddlYear.requestFocus();
            } else
            {
                lblError.setText("Connection has not been established.");
            }
        } else
        {
            jdbc.disconnect();
            btnConnect.setText("Connect");
            toggleButtons(true);
            check = false;
            ddlGenre.setValue(null);
            ddlYear.setValue(null);
            txtTitle.setText("");
            lblOptionInfo.setText("");
            tblMovies.setItems(null);
            txtUrl.setText("");
            txtUser.setText("");
            txtPassword.setText("");
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Toggles search area members to enabled or disabled.
    ////////////////////////////////////////////////////////////////////////////
    public void toggleButtons(boolean bool)
    {
        ddlGenre.setDisable(bool);
        ddlYear.setDisable(bool);
        btnSearch.setDisable(bool);
        txtTitle.setDisable(bool);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Retrieves genres from genre table using preparedStatement and store 
    // them into a hashmap.
    ////////////////////////////////////////////////////////////////////////////
    private void getGenres()
    {
        String sql = "SELECT * FROM genre";

        ResultSet rs = jdbc.query(sql, params);

        try
        {
            while (rs.next())
            {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                genreList.put(id, name);
            }
        } catch (SQLException ex)
        {

        }
        params.clear();
    }

    ////////////////////////////////////////////////////////////////////////////
    // Retrieves genres from hashmap table and stores them into an Arraylist 
    // with adding "Any" to the top. Then adds all items into an ObservableList
    // and connects them with the genre dropdown list.
    //
    // Retrieves years from movie table using preparedStatement and stores them
    // into an Arraylist with adding "Any" to the top. Then adds all items into
    // an ObservableList and connects them with the genre dropdown list.
    ////////////////////////////////////////////////////////////////////////////
    public void createOptions()
    {

        // create genre options
        ArrayList genres = new ArrayList();
        genres.add("Any");
        for (int i = 1; i < genreList.size(); i++)
        {
            genres.add(genreList.get(i));
        }
        // add options to dropdown list
        ObservableList obsGenre = FXCollections.observableArrayList(genres);
        ddlGenre.setItems(obsGenre);

        // create year options
        ArrayList years = new ArrayList();
        years.add("Any");

        // retrieve years from movie table
        String sql = "SELECT DISTINCT year FROM movie ORDER BY year DESC";
        ResultSet rs = jdbc.query(sql, params);

        try
        {
            while (rs.next())
            {
                years.add(rs.getInt(1));
            }
        } catch (SQLException e)
        {
            System.err.println("[SQL ERROR] " + e.getSQLState() + ": "
                    + e.getMessage());

        } catch (Exception e)
        {
            System.err.println("[ERROR]: " + e.getMessage());
        }

        // add options to dropdown list
        ObservableList obsYears = FXCollections.observableArrayList(years);
        ddlYear.setItems(obsYears);

        params.clear();

    }

    ///////////////////////////////////////////////////////////////////////////
    // Retrieves genres from hashmap table and stores them into an Arraylist 
    // with adding "Any" to the top. Then adds all items into an ObservableList
    // and connects them with the genre dropdown list.
    //
    // Retrieves years from movie table using preparedStatement and stores them
    // into an Arraylist with adding "Any" to the top. Then adds all items into
    // an ObservableList and connects them with the genre dropdown list.
    //
    // If no option is selected then displays warning message.
    ////////////////////////////////////////////////////////////////////////////
    public void searchMovie(ActionEvent event)
    {
        params.clear();
        lblOptionInfo.setText("");
        lblOptionError.setText("");

        // first part of preparedStatement till conditions
        String sql = "SELECT * FROM movie INNER JOIN MovieGenre ON id = "
                + "MovieGenre.movieId WHERE";
        String s = "";

        // get selected genre
        if (ddlGenre.getSelectionModel().getSelectedIndex() > 0)
        {
            Integer genre = ddlGenre.getSelectionModel().getSelectedIndex();
            params.add(genre);
            s = "a";
        }

        // get selected year
        if (ddlYear.getSelectionModel().getSelectedIndex() > 0)
        {
            Integer year = Integer.parseInt(ddlYear.getSelectionModel().
                    getSelectedItem().toString());
            params.add(year);
            s = s + "b";
        }

        // get typed name
        if (txtTitle.getText() != null && !txtTitle.getText().isEmpty())
        {
            String name = txtTitle.getText();
            params.add(name);
            s = s + "c";
        }

        // generate condition part for preparedStatement
        // a -> only genre, b -> only year, c -> only year 
        // ab -> both genre and year, bc -> both year and title
        // ac -> both genre and year, abc -> all of them
        // TODO: find a simple way to do this
        switch (s)
        {
            case "a":
                sql = sql + " MovieGenre.genreId = ?";
                break;
            case "b":
                sql = sql + " year = ?";
                break;
            case "c":
                sql = sql + " title LIKE ?";
                break;
            case "ab":
                sql = sql + " MovieGenre.genreId = ? AND year = ?";
                break;
            case "ac":
                sql = sql + " MovieGenre.genreId = ? AND title LIKE ?";
                break;
            case "bc":
                sql = sql + " year = ? AND title LIKE ?";
                break;
            case "abc":
                sql = sql + " MovieGenre.genreId = ? AND year = ? AND "
                        + "title LIKE ?";
                break;
            default:
                break;
        }

        // retrieve movies according to conditions using preparedStatement if 
        // at least one option is selected
        if (!s.isEmpty())
        {
            ResultSet rs = jdbc.query(sql, params);

            // list for storing retrieved movie objects for adding them into 
            // observable list
            ArrayList<Movie> movieList = new ArrayList<>();

            /* if movieId is NOT same with the previous one then add a new 
            movie. If movieId is SAME with the previous one then add genre to 
            that movie's genres ArrayList.
             */
            int movieId = -1;
            int index = 0;

            try
            {
                while (rs.next())
                {
                    if (rs.getInt(1) != movieId)
                    {
                        ArrayList<String> genres = new ArrayList<>();
                        Movie movie = new Movie();
                        movie.setTitle(rs.getString(2));
                        movie.setYear(rs.getInt(3));
                        movie.setRunningTime(rs.getInt(4));
                        genres.add(genreList.get(rs.getInt(6)));
                        movie.setGenres(genres);
                        movieId = rs.getInt(1);
                        movieList.add(movie);
                        index++;
                    } else
                    {
                        movieList.get(index - 1).getGenres().add(genreList.
                                get(rs.getInt(6)));
                    }
                }
            } catch (SQLException e)
            {
                System.err.println("[SQL ERROR] " + e.getSQLState() + ": "
                        + e.getMessage());

            } catch (Exception e)
            {
                System.err.println("[ERROR]: " + e.getMessage());
            }

            // format genre arraylist for display (no brackets, null entries 
            // not shown
            genreColumn.setCellValueFactory(cellData
                    -> new SimpleStringProperty(cellData.getValue().
                            printGenres()));

            // add movies into Tableview
            ObservableList<Movie> obsList = FXCollections.
                    observableArrayList(movieList);
            tblMovies.setItems(obsList);

            // show number of records found 
            lblOptionInfo.setText(String.format("Records Found: %,d",
                    movieList.size()));

        } else
        {
            lblOptionError.setText("Please select at least one option");
        }
    }
}
