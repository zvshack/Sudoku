# Sudoku using JavaFX

## TODO: notes for you to implement
1. loadBoard() method should throw an exception if the file is not a valid sudoku board 

1. when saving: check if the file already exists, and ask the user if they want to overwrite it (check)

1. Undo the last move (slight check)
    * requires a way to store a stack of moves
as long that it is okay that it does not undo cells that have been deleted (basically its a glorified delete key that deletes the most recent cell)

1. Undo, show values entered: show all the values we've entered since we loaded the board (check)

1. Hint, Show Hint: highlight all cells where only one legal value is possible (check)

1. on right-click handler: show a list of possible values that can go in this square (check)

## Also add two interesting features of your own
* This is for the final 10 points to get to 100. 
    * If your definition of "interesting" is "the minimum I can do to finish this assignment", then you may end up with A- instead of A. Try for something genuinely interesting.
