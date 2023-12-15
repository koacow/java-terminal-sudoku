    /**
 * Sudoku.java
 * 
 * Implementation of a class that emulates the Sudoku puzzle game
 * in the Java terminal.
 */

import java.io.*;   // allows us to read from a file
import java.util.*;

public class Sudoku {    
    // The current contents of the cells of the puzzle. 
    private int[][] grid;
    
    /*
     * Indicates whether the value in a given cell is fixed 
     * (i.e., part of the initial configuration).
     * valIsFixed[r][c] is true if the value in the cell 
     * at row r, column c is fixed, and false otherwise.
     */
    private boolean[][] valIsFixed;
    
    /*
     * This 3-D array allows us to determine if a given subgrid (i.e.,
     * a given 3x3 region of the puzzle) already contains a given
     * value.  We use 2 indices to identify a given subgrid:
     *
     *    (0,0)   (0,1)   (0,2)
     *
     *    (1,0)   (1,1)   (1,2)
     * 
     *    (2,0)   (2,1)   (2,2)
     * 
     * For example, subgridHasVal[0][2][5] will be true if the subgrid
     * in the upper right-hand corner already has a 5 in it, and false
     * otherwise.
     */
    private boolean[][][] subgridHasVal;
    private boolean[][] rowHasVal;
    private boolean[][] colHasVal;
    
    /*** ADD YOUR ADDITIONAL FIELDS HERE. ***/
    
    
    /* 
     * Constructs a new Puzzle object, which initially
     * has all empty cells.
     */
    public Sudoku() {
        this.grid = new int[9][9];
        this.valIsFixed = new boolean[9][9];     
        
        /* 
         * Note that the third dimension of the following array is 10,
         * because we need to be able to use the possible values 
         * (1 through 9) as indices.
         */
        this.subgridHasVal = new boolean[3][3][10];        

        /*** INITIALIZE YOUR ADDITIONAL FIELDS HERE. ***/
        this.rowHasVal = new boolean[9][10];
        this.colHasVal = new boolean[9][10];
    }
    
    /*
     * Place the specified value in the cell with the specified
     * coordinates, and update the state of the puzzle accordingly.
     */
    public void placeVal(int val, int row, int col) {
        this.grid[row][col] = val;
        this.subgridHasVal[row/3][col/3][val] = true;
        this.rowHasVal[row][val] = true;
        this.colHasVal[col][val] = true;
    }
        
    /*
     * remove the specified value from the cell with the specified
     * coordinates, and update the state of the puzzle accordingly.
     */
    public void removeVal(int val, int row, int col) {
        this.grid[row][col] = 0;
        this.subgridHasVal[row/3][col/3][val] = false;
        this.rowHasVal[row][val] = false;
        this.colHasVal[col][val] = false;
    }  
        
    /*
     * read in the initial configuration of the puzzle from the specified 
     * Scanner, and use that config to initialize the state of the puzzle.  
     * The configuration should consist of one line for each row, with the
     * values in the row specified as integers separated by spaces.
     * A value of 0 should be used to indicate an empty cell.
     * 
     */
    public void readConfig(Scanner input) {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                int val = input.nextInt();
                this.placeVal(val, r, c);
                if (val != 0) {
                    this.valIsFixed[r][c] = true;
                }
            }
            input.nextLine();
        }
    }
                
    /*
     * Displays the current state of the puzzle.
     */        
    public void printGrid() {
        for (int r = 0; r < 9; r++) {
            this.printRowSeparator();
            for (int c = 0; c < 9; c++) {
                System.out.print("|");
                if (this.grid[r][c] == 0) {
                    System.out.print("   ");
                } else {
                    System.out.print(" " + this.grid[r][c] + " ");
                }
            }
            System.out.println("|");
        }
        this.printRowSeparator();
    }
        
    // A private helper method used by display() 
    // to print a line separating two rows of the puzzle.
    private static void printRowSeparator() {
        for (int i = 0; i < 9; i++) {
            System.out.print("----");
        }
        System.out.println("-");
    }

    /**
     * Private helper method for playGame() that checks whether the puzzle has been completed.
     */
    private boolean isValid(int r, int c, int value){
        int subgridR = getSubGridRow(r);
        int subgridC = getSubGridColumn(c);
        if (subgridHasVal[subgridR][subgridC][value] == true || rowHasVal[r][value] == true || colHasVal[c][value] == true){
            return false;
        }
        return true;
    }
    
    /**
     * Private helper method for playGame() that checks whether the puzzle has been completed.
     */
    private boolean isSolved(){
        for (int[] row: grid){
            for (int n: row){
                if (n == 0) return false;
            }
        }
        return true;
    }

    private int getSubGridRow(int r){
        return r/3;
    }

    private int getSubGridColumn(int c){
        return c/3;
    }
         
    /*
     * This is the key recursive-backtracking method.  Returns true if
     * a solution has already been found, and false otherwise.
     * 
     * Each invocation of the method is responsible for finding the
     * value of a single cell of the puzzle. The parameter n
     * is the number of the cell t hat a given invocation of the method
     * is responsible for. We recommend that you consider the cells
     * one row at a time, from top to bottom and left to right,
     * which means that they would be numbered as follows:
     *
     *     0  1  2   3  4  5    6  7  8
     *     9 10 11   12 13 14   15 16 17
     *    18 19 20   21 22 23   24 25 26
     * 
     *    27 28 29   30 31 32   33 34 35
     *    36 37 38   39 40 41   42 43 44
     *    45 46 47   48 49 50   51 52 53
     * 
     *    54 55 56   57 58 59   60 61 62
     *    63 64 65   66 67 68   69 70 71
     *    72 73 74   75 76 77   78 79 80
     */
    private boolean solveRB(int n) {
        /**
         * Pseudo code:
         * if n == 81 - base case
         *      return true
         * 
         * for each value 1-9
         *     if valisfixed[n] == true
         *          go to next value
         *     else
         *         if value is valid
         *              place value in grid
         *              if solveRB(n+1) == true
         *                  return true
         *              remove value from grid 
         *     return false - backtrack
         *                
         *              
         * 
         * */ 
        if (n == 81) return true;
        for (int i = 1; i <= 9; i++){
            if (valIsFixed[n/9][n%9] == true){
                return solveRB(n+1);
            }
            else{
                if(isValid(n,i) == true){
                    placeVal(i, n/9, n%9);
                    if (solveRB(n+1) == true){
                        return true;
                    }
                    removeVal(i, n/9, n%9);
                }
            }
        }
        /* 
         * The following return statement allows the initial code to
         * compile.  Replace it with your full implementation of the
         * recursive-backtracking method.
         */
        return false;
    } 
    
    /*
     * public "wrapper" method for solveRB().
     * Makes the initial call to solveRB, and returns whatever it returns.
     */
    public boolean solve() { 
        boolean foundSol = this.solveRB(0);
        return foundSol;
    }

    public void playGame(){
        Scanner sc = new Scanner(System.in);
        while (!isSolved){
            System.out.println("This is the current state of the puzzle:");
            printGrid();
            System.out.print("Enter the row and column index you would like to change: ");
            int r = sc.nextInt();
            int c = sc.nextInt();
            if (r < 0 || r > 8 || c < 0 || c > 8) {
                System.out.println("Invalid input, row and column index must be between 0 and 8");
                continue;
            }
            System.out.println("Enter the number you would like to add: ");
            int n = sc.nextInt();
            if (n < 1 || n > 9) {
                System.out.println("Invalid input, number must be between 1 and 9");
            }
            if (isValid(r,c,n)){
                placeVal(r,c,n);
            } else{
                System.out.println("Choose again, this position does not work!");
            }

        }
        System.out.println("Congratulations! You solved the puzzle! Here is the final puzzle: ");
        printGrid();
    }
    
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        Sudoku puzzle = new Sudoku();
        
        String filename = "puzzle1.txt";
        
        Scanner input = new Scanner(new File(filename));
        puzzle.readConfig(input);
        
        puzzle.playGame();
    }    
}
