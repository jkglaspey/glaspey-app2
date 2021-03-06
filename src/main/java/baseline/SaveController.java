/*
 *  UCF COP3330 Fall 2021 Application Assignment 2 Solution
 *  Copyright 2021 Joshua Glaspey
 */

// The purpose of this class is to allow the user to export a list of items as one of three file types to local storage.

package baseline;

import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Formatter;
import java.util.List;
import java.util.Objects;

public class SaveController {
    // Declare a label that will display an error if the file path is invalid or blank
    @FXML
    private Label errorLabel;

    // Declare a radio button group for specifying the file extension
    @FXML
    private ToggleGroup fileButtons;

    // Declare a text field for setting the file name
    @FXML
    private TextField fileNameField;

    // Declare a label for displaying the selected file path
    @FXML
    private Label filePathLabel;

    // Declare the parent pane
    @FXML
    private StackPane pane;

    // Declare sound for clicking button
    private final AudioClip buttonSoundPlayer = new AudioClip(Objects.requireNonNull(getClass().getResource("sound/buttonClick.mp3")).toExternalForm());
    private final AudioClip smallButtonSoundPlayer = new AudioClip(Objects.requireNonNull(getClass().getResource("sound/smallButtonClick.mp3")).toExternalForm());

    // Grab the previous inventory
    private final List<Item> inventory;

    // Declare constructor to create scene
    public SaveController(List<Item> inventory, Stage stage) {
        this.inventory = inventory;

        // load the correct fxml file
        // Declare a fxml loader
        FXMLLoader root;
        // Declare the loaded scene
        Parent scene;
        try {
            root = new FXMLLoader(Objects.requireNonNull(getClass().getResource("saveScene.fxml")));
            root.setController(this);
            scene = root.load();
        }
        catch (IOException e) {
            e.printStackTrace();
            return;
        }
        // open the scene
        stage.getScene().setRoot(scene);
        stage.show();
    }

    // Declare different constructor for testing
    public SaveController(List<Item> list) {
        this.inventory = list;
    }

    // Change the error color
    public void initialize() {
        // change error text color
        errorLabel.setStyle("-fx-text-fill: red");
    }

    // Method which cancels the current action
    @FXML
    void cancel(ActionEvent event) {
        // play click sound
        buttonSoundPlayer.play();

        // return to old screen
        new MainSceneController(inventory,(Stage)(pane.getScene().getWindow()));
    }

    // Button which opens a directory chooser to specify the new file's saving location
    @FXML
    void chooseFile(ActionEvent event) {
        // play click sound
        buttonSoundPlayer.play();

        // open a new directory chooser and select the desired file path
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(null);
        if(file != null) {
            filePathLabel.setText(file.getPath());
        }
    }

    // Methods which play the small button click sound
    @FXML
    void playSmallButtonClick(ActionEvent event) {
        // play small click sound
        smallButtonSoundPlayer.play();
    }

    // Plays a button click sound when the user enters a name
    @FXML
    void enterName(ActionEvent event) {
        // play click sound
        buttonSoundPlayer.play();
    }

    // Method which attempts to save an inventory to the local storage
    @FXML
    void saveItems(ActionEvent event) {
        // play click sound
        buttonSoundPlayer.play();

        // leave the method if the file path is invalid
        if(isFilePathInvalid(filePathLabel.getText())) {
            // make error label visible
            isErrorLabelVisible(true);

            // leave method
            return;
        }

        // set error label invisible
        isErrorLabelVisible(false);

        // get the selected button from the radio group and run the file exporting
        RadioButton selectedButton = (RadioButton)fileButtons.getSelectedToggle();
        chooseFile(selectedButton.getText());

        // return to old screen
        new MainSceneController(inventory,(Stage)(pane.getScene().getWindow()));
    }

    // Method to determine which exporting method to call
    private void chooseFile(String fileType) {
        // determine which file type to export as
        switch (fileType) {
            // if TSV, call saveToTSV()
            case "TSV" -> saveToTSV(filePathLabel.getText(), fileNameField.getText());

            // if JSON, call saveToJSON()
            case "JSON" -> saveToJSON(filePathLabel.getText(), fileNameField.getText());

            // if html, call saveToHTML()
            case "HTML" -> saveToHTML(filePathLabel.getText(), fileNameField.getText());

            // to satisfy SonarLint
            default -> {
            }
        }
    }

    // Method which determines the validity of the specified file path
    // Note: this is only for if the user decides to delete the file path after specifying it
    private boolean isFilePathInvalid(String path) {
        return (path.isBlank()) || !(Files.exists(Paths.get(path)));
    }

    // Method which changes the visibility of the error message
    private void isErrorLabelVisible(boolean value) {
        // set label visibility
        if(value) errorLabel.setOpacity(1.0);
        else errorLabel.setOpacity(0.0);
    }

    // Save the inventory to a TSV file
    private void saveToTSV(String path, String name) {
        // combine the file path, name, and extension
        String save = combinePath(path, name, ".txt");

        // attempt to initialize an output stream
        File file;
        Formatter stream;
        try {
            // create a file from the string path
            file = new File(save);

            // create a formatter stream to the file
            stream = new Formatter(new FileOutputStream(file));
        }
        // file was not found
        catch (FileNotFoundException e) {
            // set error label to visible
            isErrorLabelVisible(true);
            return;
        }

        // append each item to the TSV file
        for(Item i : inventory) {
            // create a new String builder representing the item as a new line
            String temp = i.getSerialNumber() + "\t" + i.getName() + "\t" + i.getCost();

            // write to new line in file
            stream.format("%s%n", temp);
        }

        // close Formatter
        stream.close();
    }

    // Save the inventory to a JSON file
    private void saveToJSON(String path, String name) {
        // combine the file path, name, and extension
        String save = combinePath(path,name,".json");

        // try to write to output file
        try {
            // create new file writer
            FileWriter stream = new FileWriter(save);

            // loop through inventory
            for (Item i : inventory) {
                // create new Gson
                Gson gson = new Gson();

                // write to json file
                gson.toJson(i, stream);
            }

            // close stream
            stream.close();
        }
        // unhandled file error
        catch (IOException ioException) {
            // set error label to visible
            isErrorLabelVisible(true);

            // delete partially created file (if it exists)
            // note: boolean result is ignored
            new File(save).delete();
        }
    }

    // Save the inventory to HTML file
    private void saveToHTML(String path, String name) {
        // combine the file path, name, and extension
        String save = combinePath(path,name,".html");

        // create a file from the string path
        File file = new File(save);

        // try to create a formatter stream to the file
        Formatter stream;
        try {
            stream = new Formatter(new FileOutputStream(file));
        }
        // unhandled file error
        catch (FileNotFoundException e) {
            // set error label to visible
            isErrorLabelVisible(true);

            // leave method
            return;
        }

        // write a basic heading
        stream.format("""
                <html><body><table>
                <style type="text/css">
                  td {
                    padding: 0 15px;
                  }
                </style><tr><th>Serial Number\t</th><th>Name\t</th><th>Cost</th></tr>
                """);

        // loop through inventory
        for(Item i: inventory) {
            // write line using current item
            stream.format("<tr><td>%s</td><td>%s</td><td>%s</td></tr>",i.getSerialNumber(),i.getName(),i.getCost());
        }
        // write closing tags
        stream.format("%s","</table></body></html>");

        // close stream
        stream.close();
    }

    // Concatenate parts of a string together
    private String combinePath(String path, String name, String extension) {
        // return the combined string
        return path + "/" + name + extension;
    }
}
