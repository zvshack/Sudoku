package sudoku;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.ButtonType;
public class Sudoku extends Application
{
    private Board board = new Board();
    public static final int SIZE = 9;
    private VBox root;
    private TextField[][] textFields = new TextField[SIZE][SIZE];
    private int width = 800;
    private int height = 800;
    private boolean updatingBoard = false;

    @Override
    public void start(Stage primaryStage) throws Exception
    {   
        root = new VBox();

        //System.out.println(new File(".").getAbsolutePath());

        root.getChildren().add(createMenuBar(primaryStage));

        GridPane gridPane = new GridPane();
        root.getChildren().add(gridPane);
        gridPane.getStyleClass().add("grid-pane");

        // create a 9x9 grid of text fields
        for (int row = 0; row < SIZE; row++)
        {
            for (int col = 0; col < SIZE; col++)
            {
                textFields[row][col] = new TextField();
                TextField textField = textFields[row][col];
                
                // setting ID so that we can look up the text field by row and col
                // IDs are #3-4 for the 4th row and 5th column (start index at 0)
                textField.setId(row + "-" + col);
                gridPane.add(textField, col, row);
                // using CSS to get the darker borders correct
                if (row % 3 == 2 && col % 3 == 2)
                {
                    // we need a special border to highlight the borrom right
                    textField.getStyleClass().add("bottom-right-border");
                }
                else if (col % 3 == 2) { 
                    // Thick right border
                    textField.getStyleClass().add("right-border");
                }
                else if (row % 3 == 2) { 
                    // Thick bottom border
                    textField.getStyleClass().add("bottom-border");
                }

                // add a handler for when we select a textfield
                textField.setOnMouseClicked(event -> {
                    // toggle highlighting
                    if (textField.getStyleClass().contains("text-field-selected"))
                    {
                        // remove the highlight if we click on a selected cell
                        textField.getStyleClass().remove("text-field-selected");
                    }
                    else
                    {
                        // otherwise 
                        textField.getStyleClass().add("text-field-selected");
                    }
                    //highlight all of the same number as the number clicked
                    String id = textField.getId();
                    String[] parts = id.split("-");
                    int r = Integer.parseInt(parts[0]);
                    int c = Integer.parseInt(parts[1]);
                    int value = board.getCell(r,c);
                    
                        for (int ro = 0; ro < 9; ro++){
                            for (int co = 0; co < 9; co++){
                                if(board.getCell(ro, co)== value && value != 0){
                                    textFields[ro][co].getStyleClass().add("text-field-selected");
                                }
                                else{
                                    textFields[ro][co].getStyleClass().remove("text-field-selected");
                                }
                            }
                        }
                       
                });

                // add a handler for when we lose focus on a textfield
                textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
                    if (!newValue)
                    {
                        // remove the highlight when we lose focus
                        textField.getStyleClass().remove("text-field-selected");
                    }
                });
                //BACKSPACE-HANDLER
                // add handler for when we press the BACKSPACE key
                // to clear the textfield background from red when typing in an incorrect value
                textField.setOnKeyPressed(event -> {
                    if (event.getCode().toString().equals("BACK_SPACE"))
                    {
                        textField.getStyleClass().remove("text-field-highlight");
                    }
                });

                // RIGHT-CLICK handler
                // add handler for when we RIGHT-CLICK a textfield
                // to bring up a selection of possible values
                textField.setOnContextMenuRequested(event -> {
                    // change the textfield background to blue while keeping the rest of the css the same
                    textField.getStyleClass().add("text-field-selected");
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Possible values");
                    // TODO: show a list of possible values that can go in this square
                    // get the row and column of the text field
                    String id = textField.getId();
                    String[] parts = id.split("-");
                    int r = Integer.parseInt(parts[0]);
                    int c = Integer.parseInt(parts[1]);
                    String values = board.getPossibleValues(r, c).toString();
                    //show only the values that are legal moves
                    alert.setContentText(values);
                    alert.showAndWait();
                    textField.getStyleClass().remove("text-field-selected");
                });

                // using a listener instead of a KEY_TYPED event handler
                // KEY_TYPED requires the user to hit ENTER to trigger the event
                textField.textProperty().addListener((observable, oldValue, newValue) -> {
                    if(updatingBoard) return;

                    if (!newValue.matches("[1-9]?")) {
                        // restrict textField to only accept single digit numbers from 1 to 9
                        textField.setText(oldValue);
                    }
                    String id = textField.getId();
                    String[] parts = id.split("-");
                    int r = Integer.parseInt(parts[0]);
                    int c = Integer.parseInt(parts[1]);
                    
                    if (newValue.length() > 0)
                    {
                        try
                        {
                            System.out.printf("Setting cell %d, %d to %s\n", r, c, newValue);
                            int value = Integer.parseInt(newValue);
                            board.setCell(r, c, value);
                            //check if this was the 9th value placed of its kind
                            int valueCont = 0;
                            for(int ro = 0; ro < 9; ro++){
                                for(int co = 0; co < 9; co++){
                                    if(board.getCell(ro, co) == value){
                                        valueCont++;
                                    }
                                }
                            }
                            
                            if(valueCont == 9 && board.checkBoard() == false){
                               
                                for(int ro = 0; ro < 9; ro++){
                                    for(int co = 0; co < 9; co++){
                                        if(board.getCell(ro, co) == value){
                                            System.out.println("here");
                                            
                                            
                                        }
                                    }
                                }
                                Alert alert = new Alert(AlertType.INFORMATION);
                                alert.setTitle("Done with value " + value);
                                alert.setHeaderText("You have placed all the " + value + "s!");
                                alert.setContentText("Nice job!");
                                alert.showAndWait();
                            }
                            
                            //send a good job message and color the board when finishing the puzzle
                            if(board.checkBoard() == true){
                                for (int row1 = 0; row1 < 9; row1++)
                                {
                                    for (int col1 = 0; col1 < 9; col1++)
                                    {
                                        if (row1 == 4){
                                        textFields[row1][col1].setStyle("-fx-background-color: Green;");
                                        }
                                        else if (col1 == 4 && row1 < 4){
                                            textFields[row1][col1].setStyle("-fx-background-color: Orange;");
                                        }
                                        else if (col1 == row1 && row1 < 4){
                                            textFields[row1][col1].setStyle("-fx-background-color: Red;");
                                        }
                                        else if (col1 == 4 && row1 > 4){
                                            textFields[row1][col1].setStyle("-fx-background-color: Indigo;");
                                        }
                                        else if (col1 == row1 && row1 > 4){
                                            textFields[row1][col1].setStyle("-fx-background-color: Violet;");
                                        }
                                        else if (col1 + row1 == 8 && col1 < 4){
                                            textFields[row1][col1].setStyle("-fx-background-color: Blue;");
                                        }
                                        else if (col1 + row1 == 8 && col1 > 4){
                                            textFields[row1][col1].setStyle("-fx-background-color: Yellow;");
                                        }
                                    }
                                }
                                Alert alert = new Alert(AlertType.INFORMATION);
                                alert.setTitle("Congratulations!");
                                alert.setHeaderText("You have completed the puzzle!");
                                alert.setContentText("Good job!");
                                alert.showAndWait();
                            }
                            this.updateBoard();

                            // remove the highlight when we set a value
                            textField.getStyleClass().remove("text-field-selected");
                        }
                        catch (NumberFormatException e)
                        {
                            // ignore; should never happen
                        }
                        catch (Exception e)
                        {
                            board.setCell(r, c, Integer.parseInt(oldValue));
                            textField.getStyleClass().remove("text-field-selected");
                            textField.getStyleClass().add("text-field-highlight");
                            this.updateBoard();
                            
                            
                            System.out.println("Invalid Value: " + newValue);
                        }
                    }
                    else
                    {
                        board.setCell(r, c, 0);
                        board.deletetop();
                        this.updateBoard();
                    }
                });
            }
        }

        // add key listener to the root node to grab ESC keys
        root.setOnKeyPressed(event -> {
            System.out.println("Key pressed: " + event.getCode());
            switch (event.getCode())
            {
                // check for the ESC key
                case ESCAPE:
                    // clear all the selected text fields
                    for (int row = 0; row < SIZE; row++)
                    {
                        for (int col = 0; col < SIZE; col++)
                        {
                            TextField textField = textFields[row][col];
                            textField.getStyleClass().remove("text-field-selected");
                        }
                    }
                    break;
                default:
                    System.out.println("you typed key: " + event.getCode());
                    break;
                
            }
        });

        Scene scene = new Scene(root, width, height);

        URL styleURL = getClass().getResource("/style.css");
		String stylesheet = styleURL.toExternalForm();
		scene.getStylesheets().add(stylesheet);
        primaryStage.setTitle("Sudoku");
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
        	System.out.println("oncloserequest");
        });
    }

    private void updateBoard()
    {
        updatingBoard = true;
        for (int row = 0; row < SIZE; row++)
        {
            for (int col = 0; col < SIZE; col++)
            {
                TextField textField = textFields[row][col];
                int value = board.getCell(row, col);
                if (value > 0)
                {
                    textField.setText(Integer.toString(value));
                    
                }
                else
                {
                    textField.setText("");
                    
                }
            }
        }
        updatingBoard = false;
    }

    private MenuBar createMenuBar(Stage primaryStage)
    {
        MenuBar menuBar = new MenuBar();
    	menuBar.getStyleClass().add("menubar");

        //
        // File Menu
        //
    	Menu fileMenu = new Menu("File");

        addMenuItem(fileMenu, "Load from file", () -> {
            System.out.println("Load from file");
            FileChooser fileChooser = new FileChooser();
            // XXX: this is a hack to get the file chooser to open in the right directory
            // we should probably have a better way to find this folder than a hard coded path
			fileChooser.setInitialDirectory(new File("../puzzles"));
			File sudokuFile = fileChooser.showOpenDialog(primaryStage);
            if (sudokuFile != null)
            {
                System.out.println("Selected file: " + sudokuFile.getName());
                
                try {
                    //TODO: loadBoard() method should throw an exception if the file is not a valid sudoku board
                    board = Board.loadBoard(new FileInputStream(sudokuFile));
                    updateBoard();
                    board.undostack.clear();
                } catch (Exception e) {
                    // pop up and error window
                    Alert alert = new Alert(AlertType.ERROR);
    	            alert.setTitle("Unable to load sudoku board from file "+ sudokuFile.getName());
    	            alert.setHeaderText(e.getMessage());
                    alert.setContentText(e.getMessage());
                    e.printStackTrace();
                    if (e.getCause() != null) e.getCause().printStackTrace();
                    
                    alert.showAndWait();
                }
            }
        });

        // save to text
        addMenuItem(fileMenu, "Save to text", () -> {
            System.out.println("Save puzzle to text");
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File("../puzzles"));
            File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null)
            {
                System.out.println("Selected file: " + file.getName());
                if (file.exists()) {
                    
                    Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.setTitle("Confirm Overwrite");
                    alert.setHeaderText("File already exists. Do you want to overwrite?");
                    alert.setContentText("Click OK to overwrite the existing file or Cancel to choose a different file.");
                    ButtonType Bok = new ButtonType("OK");
                    ButtonType Bcancel = new ButtonType("Cancel");
                    alert.getButtonTypes().setAll(Bok, Bcancel);
                    alert.showAndWait().ifPresent(type -> {
                        if (type == Bcancel) {
                            return;
                        }
                    });
                    
                }
                try {
                    //TODO: check if the file already exists, and ask the user if they want to overwrite
                    writeToFile(file, board.toString());
                } catch (Exception e) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Unable to save to file");
                    alert.setHeaderText("Unsaved changes detected!");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
            }
        });
        
        addMenuItem(fileMenu, "Print Board", () -> {
            // Debugging method that just prints the board
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Board");
            alert.setHeaderText(null);
            alert.setContentText(board.toString());
            alert.showAndWait();
        });
        // add a separator to the fileMenu
        fileMenu.getItems().add(new SeparatorMenuItem());

        addMenuItem(fileMenu, "Exit", () -> {
            System.out.println("Exit");
            primaryStage.close();
        });

        menuBar.getMenus().add(fileMenu);

        //
        // Edit
        //
        Menu editMenu = new Menu("Edit");

        addMenuItem(editMenu, "Undo", () -> {
            System.out.println("Undo");
            board.undo();
            this.updateBoard();
            
    });
        addMenuItem(editMenu, "Redo", () -> {
            System.out.println("Redo");
            board.redo();
            this.updateBoard();
            
    });

        addMenuItem(editMenu, "Show values entered", () -> {
            System.out.println("Show all the values we've entered since we loaded the board");
            //TODO: pop up a window showing all of the values we've entered CHECK
            //highlight all the cells that have been changed since the board was loaded
            int[][] moves = board.getMoves();
            for(int i = 0; i < moves[0].length; i++){
                int row = moves[0][i];
                int col = moves[1][i];
                textFields[row][col].getStyleClass().add("text-field-selected");
            }
            updateBoard();
        });
        addMenuItem(editMenu, "Unhighlight Values", () -> {
            System.out.println("Unhighlight all shown values");
            //unhighlight every cell
            for (int row = 0; row < 9; row++)
            {
                for (int col = 0; col < 9; col++)
                {
                    textFields[row][col].getStyleClass().remove("text-field-selected");
                    textFields[row][col].getStyleClass().remove("-text-field-highlight");
                }
                updateBoard();
            }
        });
        menuBar.getMenus().add(editMenu);

        //
        // Hint Menu
        //
        Menu hintMenu = new Menu("Hints");

        addMenuItem(hintMenu, "Show hint", () -> {
            System.out.println("Show hint");
            
            outerLoop:
            for(int row = 0; row < 9; row++){
                for(int col = 0; col < 9; col++){
                    //check if the cell is empty
                    if(board.getCell(row, col) == 0 && board.getPossibleValues(row, col).size() == 1){
                        textFields[row][col].getStyleClass().add("text-field-selected");

                        break outerLoop;
                    }
                }
            }
            updateBoard();
        });

        menuBar.getMenus().add(hintMenu);

        Menu solveMenu = new Menu("Solver");

        addMenuItem(solveMenu, "Solve Next", () -> {
            System.out.println("Solve Next");
            
            outerLoop:
            for(int row = 0; row < 9; row++){
                for(int col = 0; col < 9; col++){
                    //check if the cell is empty
                    if(board.getCell(row, col) == 0 && board.getPossibleValues(row, col).size() == 1){
                        board.setCell(row, col, board.getPossibleValues(row, col).iterator().next());
                    if(row == 8 && col == 8  && board.getPossibleValues(row, col).size() != 1){
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Unable to solve the next value");
                        alert.setHeaderText("The puzzle cannot be solved without guesses");
                        alert.setContentText("Please check the values entered, or make a guess and try again");
                        alert.showAndWait();
                    }
                        break outerLoop;
                    }
                }
            }
            
            updateBoard();
        });
        
        addMenuItem(solveMenu, "Solve All", () -> {
            System.out.println("Solve All");
            outerloop2:
            while(board.checkBoard() == false){
                
            outerLoop:
            for(int row = 0; row < 9; row++){
                for(int col = 0; col < 9; col++){
                    //check if the cell is empty
                    if(board.getCell(row, col) == 0 && board.getPossibleValues(row, col).size() == 1){
                        board.setCell(row, col, board.getPossibleValues(row, col).iterator().next());

                        break outerLoop;
                    }
                    if(row == 8 && col == 8  && board.getPossibleValues(row, col).size() != 1){
                        break outerloop2;
                    }
                }
            }
            updateBoard();
            }
            if(board.checkBoard() != true){
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Unable to solve puzzle");
                alert.setHeaderText("The puzzle cannot be solved without guesses");
                alert.setContentText("Please check the values entered, or make a guess and try again");
                alert.showAndWait();
            }
            else{
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Congratulations!");
                alert.setHeaderText("You have completed the puzzle with cheating!");
                alert.setContentText("No Colors For You!");
                alert.showAndWait();
            }
        });

        menuBar.getMenus().add(solveMenu);
        
        return menuBar;
    }

    private static void writeToFile(File file, String content) throws IOException
    {
        Files.write(file.toPath(), content.getBytes());
    }

    private void addMenuItem(Menu menu, String name, Runnable action)
    {
        MenuItem menuItem = new MenuItem(name);
        menuItem.setOnAction(event -> action.run());
        menu.getItems().add(menuItem);
    }
        
    public static void main(String[] args) 
    {
        launch(args);
    }
}
