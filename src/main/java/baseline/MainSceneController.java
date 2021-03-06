/*
 *  UCF COP3330 Fall 2021 Application Assignment 2 Solution
 *  Copyright 2021 Joshua Glaspey
 */

// The purpose of this class is to display the inventory as well as provide the controls for managing the list.

package baseline;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainSceneController {

    // Declare pane for keeping vertical order of screen
    @FXML
    private VBox bindPane;

    // Declare container for bottom buttons
    @FXML
    private HBox bottomControls;

    // Declare button which allows the user to delete the selected item
    @FXML
    private Button deleteItemButton;

    // Declare button which allows the user to edit the selected item
    @FXML
    private Button editItemButton;

    // Declare control for showing all the inventory data to the user
    @FXML
    private TableView<Item> itemView;

    // Declare observable list for holding only the viewable items (either ALL or searched results)
    private final ObservableList<Item> listOfItems = FXCollections.observableArrayList();

    // Declare list for holding all items in an inventory
    private List<Item> inventory;

    // Declare container for center table view
    @FXML
    private HBox middleControls;

    // Declare column holding the monetary values of each item
    @FXML
    private TableColumn<Item, String> monetaryColumn;

    // Declare column holding the names of each item
    @FXML
    private TableColumn<Item, String> nameColumn;

    // Declare parent pane for all controls
    @FXML
    private StackPane pane;

    // Declare pane for orienting the right side of the top controls
    @FXML
    private Pane positionPane1;

    // Declare pane for orienting the left side of the top controls
    @FXML
    private Pane positionPane2;

    // Declare pane for orienting the right side of the middle controls
    @FXML
    private Pane positionPane3;

    // Declare pane for orienting the left side of the middle controls
    @FXML
    private Pane positionPane4;

    // Declare pane for orienting the right side of the bottom controls
    @FXML
    private Pane positionPane5;

    // Declare pane for orienting the left side of the bottom controls
    @FXML
    private Pane positionPane6;

    // Declare group for allowing the user to change the search query
    @FXML
    private ToggleGroup searchButtons;

    // Declare column holding the serial numbers of each item
    @FXML
    private TableColumn<Item, String> serialColumn;

    // Declare text box which allows the user to search for items
    @FXML
    private TextField textPane;

    // Declare container for top controls
    @FXML
    private HBox topControls;

    // Declare the loaded scene
    private Parent scene;

    // Initialize button-click sounds
    private final AudioClip buttonSoundPlayer = new AudioClip(Objects.requireNonNull(getClass().getResource("sound/buttonClick.mp3")).toExternalForm());
    private final AudioClip smallButtonSoundPlayer = new AudioClip(Objects.requireNonNull(getClass().getResource("sound/smallButtonClick.mp3")).toExternalForm());

    // Call constructor to display the inventory to the user
    public MainSceneController(List<Item> inventory, Stage stage) {
        // copy items to be displayed
        this.inventory = inventory;

        // load the correct fxml file
        // Declare a fxml loader
        FXMLLoader root;
        scene = null;
        try {
            root = new FXMLLoader(Objects.requireNonNull(getClass().getResource("mainScene.fxml")));
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

    // Create constructor for testing values
    public MainSceneController() {
        inventory = new ArrayList<>();
    }

    // Get the current inventory
    public List<Item> getInventory() {
        return inventory;
    }

    // Get the current list of items
    public ObservableList<Item> getListOfItems() {
        return listOfItems;
    }

    // Initialize values
    public void initialize() {
        // change column resize policy
        itemView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // align text in tableview
        nameColumn.setStyle("-fx-alignment: CENTER-LEFT;");
        serialColumn.setStyle("-fx-alignment: TOP-LEFT;");
        monetaryColumn.setStyle("-fx-alignment: TOP-LEFT;");

        // change middle background values
        middleControls.setStyle("-fx-background-image: url('/baseline/image/middlebackground.png')");
        itemView.setStyle("-fx-background-image: url('/baseline/image/tablebackground.png')");

        // remove tableview default text
        itemView.setPlaceholder(new Label(""));

        // bind vbox dimensions to stack pane
        bindPane.prefWidthProperty().bind(pane.widthProperty());
        bindPane.prefHeightProperty().bind(pane.heightProperty());

        // keep controls in vertical center
        VBox.setVgrow(topControls,Priority.ALWAYS);
        VBox.setVgrow(middleControls,Priority.ALWAYS);
        VBox.setVgrow(bottomControls,Priority.ALWAYS);

        // keep controls in horizontal center
        HBox.setHgrow(positionPane1,Priority.ALWAYS);
        HBox.setHgrow(positionPane2,Priority.ALWAYS);
        HBox.setHgrow(positionPane3,Priority.ALWAYS);
        HBox.setHgrow(positionPane4,Priority.ALWAYS);
        HBox.setHgrow(positionPane5,Priority.ALWAYS);
        HBox.setHgrow(positionPane6,Priority.ALWAYS);

        // get values from inventory
        resetListToInventory();

        // initialize the description column
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setCellFactory(t -> {
            // set cell to item name
            TableCell<Item, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);

            // set cell properties
            cell.setPrefHeight(Region.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(nameColumn.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            text.setFill(Color.valueOf("#c0c0c0"));
            text.setTextAlignment(TextAlignment.LEFT);

            // display the text
            return cell;
        });
        nameColumn.setOnEditCommit( t -> (
                t.getTableView().getItems().get(t.getTablePosition().getRow())).setName(t.getNewValue()));

        // initialize the serial number column
        serialColumn.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        serialColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        serialColumn.setOnEditCommit( t -> (
                t.getTableView().getItems().get(t.getTablePosition().getRow())).setSerialNumber(t.getNewValue()));

        // initialize the cost column
        monetaryColumn.setCellValueFactory(new PropertyValueFactory<>("cost"));
        monetaryColumn.setCellFactory(t -> {
            // set cell to item cost
            TableCell<Item, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);

            // set cell properties
            cell.setPrefHeight(Region.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(monetaryColumn.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            text.setFill(Color.valueOf("#c0c0c0"));
            text.setTextAlignment(TextAlignment.LEFT);

            // display the text
            return cell;
        });
        monetaryColumn.setOnEditCommit( t -> (
                t.getTableView().getItems().get(t.getTablePosition().getRow())).setCost(t.getNewValue()));

        // set the values in the table to the observable list
        itemView.setItems(listOfItems);

        // disable the edit button
        buttonsDisabled(true);

        // setup listener for selecting a table item
        itemView.getSelectionModel().selectedItemProperty().addListener((click,oldValue,newValue) -> {
            // all items were deleted, disable edit button
            if(itemView.getSelectionModel().isEmpty()) {
                buttonsDisabled(true);
                return;
            }

            // verify an item was clicked
            try {
                itemView.getSelectionModel().getSelectedIndex();
            }
            // there's no selected item, so disable edit button
            catch (Exception e) {
                buttonsDisabled(true);
                return;
            }

            // enable edit button
            buttonsDisabled(false);
        });
    }

    // refresh the table view to update values
    private void refreshTable() {
        nameColumn.setVisible(false);
        nameColumn.setVisible(true);
    }

    // reset values in list to inventory
    private void resetListToInventory() {
        // null check
        if(inventory == null) {
            inventory = new ArrayList<>();
            return;
        }

        // clear current list
        listOfItems.clear();

        // copy inventory to list
        listOfItems.addAll(inventory);
    }

    // Create a new item by opening a new scene
    @FXML
    void createNewItem(ActionEvent event) {
        // play click sound
        buttonSoundPlayer.play();

        // open fxml for handling new item creation
        new ItemController(inventory,(Stage)scene.getScene().getWindow());
    }

    // Delete all items, but open new scene for confirmation
    @FXML
    void deleteAllItems(ActionEvent event) {
        // play click sound
        buttonSoundPlayer.play();

        // open fxml for handling new item creation
        new DeleteAllItemsController(inventory,(Stage)scene.getScene().getWindow());
    }

    // Delete currently selected item, but open new scene for confirmation
    @FXML
    void deleteItem(ActionEvent event) {
        // play click sound
        buttonSoundPlayer.play();

        // if there is a currently selected item in the tableview
        // get the selected item
        Item selectedItem = itemView.getSelectionModel().getSelectedItem();

        // if item is null, leave
        if(selectedItem == null) return;

        // remove it
        inventory.remove(selectedItem);
        listOfItems.remove(selectedItem);

        // refresh the table
        refreshTable();
    }

    // Edit the currently-selected item in a new scene
    @FXML
    void editItem(ActionEvent event) {
        // play click sound
        buttonSoundPlayer.play();

        // get index of selected item
        int index = getIndex(itemView.getSelectionModel().getSelectedItem());

        // fail check
        if(index < 0) return;

        // pull up itemScene
        new ItemController(inventory,index,(Stage)scene.getScene().getWindow());
    }

    // Search for the index of an item by serial number
    private int getIndex(Item item) {
        for(int i = 0; i < inventory.size(); i++) {
            // compare serial numbers and break when unique duplicate is found
            if(inventory.get(i).getSerialNumber().equals(item.getSerialNumber())) return i;
        }

        // search fails
        return -1;
    }

    // Disable / enable the edit item button depending on if an item was selected
    private void buttonsDisabled(boolean value) {
        editItemButton.setDisable(value);
        deleteItemButton.setDisable(value);
    }

    // Open a new scene where the user can use a file chooser to select a saved inventory
    @FXML
    void loadInventory(ActionEvent event) {
        // play click sound
        buttonSoundPlayer.play();

        // open fxml for handling saving an inventory
        new ImportController(inventory,(Stage)scene.getScene().getWindow());
    }

    // Open a new scene where the user can save the current inventory to the local machine
    @FXML
    void saveInventory(ActionEvent event) {
        // play click sound
        buttonSoundPlayer.play();

        // open fxml for handling saving an inventory
        new SaveController(inventory,(Stage)scene.getScene().getWindow());
    }

    // Search for an item in the list depending on the search query, and display only the results on the list
    @FXML
    void searchForItem(ActionEvent event) {
        // play click sound
        buttonSoundPlayer.play();

        // if name button is selected, search by name
        if( ((RadioButton)searchButtons.getSelectedToggle()).getText().equals("Name")) searchForName(textPane.getText());

        // otherwise, search by serial #
        else searchForSerialNumber(textPane.getText());

        // refresh the table with updated listOfItems
        refreshTable();
    }

    // Search for a specific item name in the inventory
    private void searchForName(String name) {
        // empty listOfItems
        listOfItems.clear();

        // if the inventory string contains name, add the item to list
        for(Item i: inventory) {
            if(i.getName().contains(name)) listOfItems.add(i);
        }
    }

    // Search for a specific item serial number in the inventory
    private void searchForSerialNumber(String serialNumber) {
        // empty listOfItems
        listOfItems.clear();

        // if the inventory string contains serialNumber, add the item to list
        for(Item i: inventory) {
            if(i.getSerialNumber().contains(serialNumber)) listOfItems.add(i);
        }
    }

    // Change the search query to "Name"
    @FXML
    void playSmallClickSound(ActionEvent event) {
        // play click sound
        smallButtonSoundPlayer.play();
    }
}
