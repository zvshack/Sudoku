package sudoku;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
public class Board
{
    private int[][] board;
    //initialize the stack of a single integer from the 2d array
    
    private class Move {
        int row1;
        int col1;
        int value1;
        public Move(int row1, int col1, int value1)
        {
            this.row1 = row1;
            this.col1 = col1;
            this.value1 = value1;
        }
    }
    Stack<Move> undostack = new Stack<Move>();
    Stack<Move> redostack = new Stack<Move>();
    public Board()
    {
        board = new int[9][9];
    }

    public static Board loadBoard(InputStream in)
    {
        Board board = new Board();
                    
        try (Scanner scanner2 = new Scanner(in)) {
            for (int row = 0; row < 9; row++) {
                for (int col = 0; col < 9; col++) {
                    int value = scanner2.nextInt();
                    System.out.println(value);
                    if (value < 0 || value > 9) {
                        //this sends an alert if a value is an int not between 0-9 when loading the board
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setTitle("Error Dialog");
                        alert.setHeaderText("Invalid Value in Board");
                        alert.setContentText("Value must be between 1 and 9 (or 0 to for an empty cell)");
                        alert.showAndWait();
                        throw new IllegalArgumentException("Invalid Value in Board");
                    }
                    board.setCell(row, col, value);
                }
            }
            if(scanner2.hasNext()) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setHeaderText("Invalid Board");
                alert.setContentText("Board must have exactly 81 values");
                alert.showAndWait();
                throw new IllegalArgumentException("Board must have exactly 81 values");
            }
            //catches if the board has more than 81 values
        } catch (Exception e) {
            e.printStackTrace(); // Or handle the exception as appropriate
        }
        

        return board;
        
    }

    public int[][] getMoves()
    {
        int[][] moves = new int[2][undostack.size()];
        for(int i = 0; i < undostack.size(); i++)
        {
            Move move = undostack.get(i);
            moves[0][i] = move.row1;
            moves[1][i] = move.col1;
        }
        return moves;
    }

    public boolean isLegal(int row, int col, int value)
    {
        return value >= 1 && value <= 9 && getPossibleValues(row, col).contains(value);
    }

    public void setCell(int row, int col, int value)
    {
        if (value < 0 || value > 9)
        {
            throw new IllegalArgumentException("Value must be between 1 and 9 (or 0 to reset a value)");
        }
        if (value != 0 && !getPossibleValues(row, col).contains(value))
        {
            Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setHeaderText("Invalid Value or Board");
                alert.setContentText("Value " + value + " is not possible for this cell");
                alert.showAndWait();
            throw new IllegalArgumentException("Value " + value + " is not possible for this cell");
        }
        // based on other values in the sudoku grid
        board[row][col] = value;
        //save this move into the both the undo stack and redo stack
            if (value != 0){
            undostack.push(new Move(row, col, value));
            redostack.clear();
            }
        
            
    }
    public boolean checkBoard(){
        for (int row = 0; row < 9; row++){
            for (int col = 0; col < 9; col++){
                if (!getPossibleValues(row, col).isEmpty()){
                    return false;
                }
            }
        }
        return true;
    }

    
    //deletes the top of the undo stack
    public void deletetop(){
        if (undostack.size() > 0){
            undostack.pop();
        }
        else{
            System.out.println("No moves to undo");
        }
    }
    //redoes the deleted value that has been undid
    public void redo(){
        if (redostack.size() > 0){
            Move move = redostack.pop();
            undostack.push(move);
            board[move.row1][move.col1] = move.value1;
        }
        else{
            System.out.println("No moves to redo");
        }
    }
    public void undo(){
        if (undostack.size() > 0){
            Move move = undostack.pop();
            redostack.push(move);
            board[move.row1][move.col1] = 0;
        }
        else{
            System.out.println("No moves to undo");
        }
    }
    public int getCell(int row, int col)
    {
        return board[row][col];
    }

    public boolean hasValue(int row, int col)
    {
        return getCell(row, col) > 0;
    }

    public Set<Integer> getPossibleValues(int row, int col)
    {
        Set<Integer> possibleValues = new HashSet<>();
        for (int i = 1; i <= 9; i++)
        {
            possibleValues.add(i);
        }
        // check the row
        for (int c = 0; c < 9; c++)
        {
            possibleValues.remove(getCell(row, c));
        }
        // check the column
        for (int r = 0; r < 9; r++)
        {
            possibleValues.remove(getCell(r, col));
        }
        // check the 3x3 square
        int startRow = row / 3 * 3;
        int startCol = col / 3 * 3;
        for (int r = startRow; r < startRow + 3; r++)
        {
            for (int c = startCol; c < startCol + 3; c++)
            {
                possibleValues.remove(getCell(r, c));
            }
        }
        return possibleValues;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < 9; row++)
        {
            for (int col = 0; col < 9; col++)
            {
                sb.append(getCell(row, col));
                if (col < 8)
                {
                    sb.append(" ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}
