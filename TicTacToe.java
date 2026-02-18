import java.util.Scanner;

public class TicTacToe {

	// This is the value if a move somehow becomes invalid
	private static final int INVALID_MOVE = -99999;
	
	private static int lineSize;
	private static int dimensions;
	private static int boardSize;
	
	public static void main(String[] args) {
		// Initialize variables and creating objects
		int[][] board; // This array holds the gamestate in values of -1, 0, 1
		int[][] RCDBoard; // This array holds all of the possible rows, columns, and diagonals with the spaces in the second set of arrays
		Scanner input = new Scanner(System.in);
		boolean isFirstPlayer = true;
		boolean validInput = false;
		int moves = 0;
		int userInput;
		// This is how many times computerMove is allowed to recurse, if the board is larger than 11, maxRecursions should be set to 0
		int maxRecursions = 3;
		String firstPlayer;
		String secondPlayer;
		
		// Prompt the user to set up the game and choose if there will be any computer players
		do {
			System.out.println("Please select the first player by typing either 'user' or 'computer'");
			firstPlayer = input.next();
			firstPlayer = firstPlayer.toLowerCase();
			if (firstPlayer.charAt(0) == 'u' ||firstPlayer.charAt(0) == 'c') {
				validInput = true;
			}
		} while (!validInput);
		validInput = false;
		do {
			System.out.println("Please select the second player by typing either 'user' or 'computer'");
			secondPlayer = input.next();
			secondPlayer = secondPlayer.toLowerCase();
			if (secondPlayer.charAt(0) == 'u' ||secondPlayer.charAt(0) == 'c') {
				validInput = true;
			}
		} while (!validInput);
		validInput = false;
		
		// Prompt the user to enter the size of the board, and create the board to that size
		do {
			System.out.println("Please enter the size of the board as a single integer, lowest is 3, highest is 999.");
			System.out.println("Note: if a user is present in the game, size is capped at 25.");
			userInput = input.nextInt();
			if ((userInput >= 3 && userInput <= 999) &&
					!(userInput > 25 && (firstPlayer.charAt(0) == 'u' || secondPlayer.charAt(0) == 'u'))) {
				validInput = true;
			} else {
				validInput = false;
			}
		} while(!validInput);
		
		// Create the board based on the size the user input
		board = new int[userInput][userInput];
		boardSize = userInput;
		// Set maxRecursions if the board is larger than specific values
		switch (userInput) {
			case 15, 14, 13, 12, 11, 10: maxRecursions = 1; break;
			case 9, 8, 7: maxRecursions = 2; break;
			case 6, 5: maxRecursions = 3; break;
			case 4, 3: maxRecursions = 5; break;
			default: maxRecursions = 0;
		}
		
		// If the board is larger than 3, prompt the user to enter how large of a connection is required for a score
		if (userInput > 3) {
			validInput = false;
			do {
				System.out.println("Please enter the amout of spaces that need to be togeather for winning, default is 4, max is 10:");
				userInput = input.nextInt();
				if (userInput >= 3 && (userInput <= 10 && userInput <= boardSize)) {
					validInput = true;
				}
			} while (!validInput);
		}
		lineSize = userInput;
		// Declare the RCD board
		RCDBoard = new int[RCDBoardSizer(boardSize)][lineSize * 2];
		// Call the method to create the RCDBoard
		RCDBoardGeneration(RCDBoard, boardSize);
		
		// This is the main loop, which cycles between players for moves
		
		// Declare a new Move object
		Move move = new Move(0,0);
		
		do {
			// Display the current board
			if (boardSize < 16 || (firstPlayer.charAt(0) == 'u' || secondPlayer.charAt(0) == 'u')) {
					displayBoard(board);
			} else if (moves % 25 == 0) {
					displayBoard(board);
			}
			// Execute the move, prompting the correct player
			if (isFirstPlayer) {
				// If firstPlayer is a user, prompt them and update board
				if (firstPlayer.charAt(0) == 'u') {
					System.out.println("This is the board state, please enter your move");
					move = userMove(board, input);	
					board[move.m_x][move.m_y] = 1;
				} else {
					// If firstPlayer is a computer, update board
					if ((((boardSize * boardSize) - moves - 1)) < maxRecursions) {
						 move = computerMove(board, RCDBoard, (boardSize * boardSize) - moves - 1, maxRecursions, true);
					} else {		
						 move = computerMove(board, RCDBoard, maxRecursions, maxRecursions, true);
					}
					board[move.m_x][move.m_y] = 1;
				}
				// Switch the player order
				isFirstPlayer = false;
			} else {
				// If secondPlayer is a user, prompt them and update board
				if (secondPlayer.charAt(0) == 'u') {
					System.out.println("This is the board state, please enter your move");
					move = userMove(board, input);		
					board[move.m_x][move.m_y] = -1;
				} else {	
					// If secondPlayer is a computer, update board
					if ((((boardSize * boardSize) - moves - 1)) < maxRecursions) {
						 move = computerMove(board, RCDBoard, (boardSize * boardSize) - moves - 1, maxRecursions, false);
					} else {		
						 move = computerMove(board, RCDBoard, maxRecursions, maxRecursions, false);
					}
					
					board[move.m_x][move.m_y] = -1;
				}
				// Switch the player order
				isFirstPlayer = true;
			}
			// Increment the moves counter
			moves++;
		} while (!winCheck(board, RCDBoard) && moves != (boardSize * boardSize));
		// Display results of the game
		displayBoard(board);
		if (winCheck(board, RCDBoard)) {
			if (!isFirstPlayer) { // isFirstPlayer gets changed in the game loop, so the reverse must be used
				System.out.printf("X has won the game, congradulations! The game took %d moves\n", moves);
			} else {
				System.out.printf("O has won the game, congradulations! The game took %d moves\n", moves);
			}
		} else {
			System.out.println("The game is a tie, no one won :(");
		}
	}
	
	// This method prints the current board to the screen
	public static void displayBoard(int[][]board) {
		// Create the board with just chars instead of ints for easy printing
		char[][] convertedBoard = new char[boardSize][boardSize];
		convertBoardChar(board , convertedBoard);
		
		// Displaying the board
		for (int i = 0; i < boardSize; i++) {
			// Check if the board is larger than 25*25, if so display numbers (no user is playing a bigger board than that
			if (boardSize < 26) {
				System.out.printf("%c  ", (char) ('A' + i));
			} else {
				System.out.printf("%3d", i + 1);
			}
			// Print the char of the board index
			for (int j = 0; j < board[i].length; j++) {
				System.out.printf(" %c " , convertedBoard[i][j]);
				
				// Print the spacer ||
				if (j % board[i].length != board[i].length - 1) {
					System.out.printf("|");
				}
			}
			// Print the spacer lines
				System.out.printf("\n");			
			if (i != boardSize - 1) {
				System.out.printf("   ");
				for (int j = 0; j < board[0].length - 1; j++) {
					System.out.printf("----");
				}
				System.out.printf("---");
				System.out.printf("\n");
			}		
		}
		// Print the bottom row of numbers
		System.out.printf("  ");
		for (int i = 0; i < boardSize; i++) {
			System.out.printf("%3d ", i + 1);
		}
		System.out.println();
		System.out.println();
		return;
	}
	
	// This Method converts the board from numbers to letters / spaces
	public static void convertBoardChar(int[][] board, char[][] convertedBoard) {
		// Loop reading each index of the board array, and displaying something depending on what it is
		// 0 = ' ', 1 = X -1 = O	
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (board[i][j] == 1) {
					convertedBoard[i][j] = 'X';
				} else if (board[i][j] == -1) {
					convertedBoard[i][j] = 'O';
				} else {
					convertedBoard[i][j] = ' ';
				}
			}		
		}
	}
	
	// This method returns true if a player has won
	public static boolean winCheck(int[][] board, int[][] RCDBoard) {
		int result;
		/*
		 *  check if someone has won by referencing the RCDBoard and checking if 
		 *  it adds to lineSize or -lineSize.
		 *	First, Iterating through each row, column, and diagonal.
		 */ 
		for (int i = 0; i < RCDBoard.length; i++) {
			// Set result to 0
			result = 0;
			// Iterating through each space on a given row, column, or diagonal
			for (int j = 0; j < RCDBoard[i].length; j += 2) {
				result += board[RCDBoard[i][j]][RCDBoard[i][j + 1]];
			}
			// If the abs of result is equals the lineSize, return true
			if (result == lineSize || result == lineSize * -1) {
				return true;
			}
		}
		// if all checks fail, return false
		return false;
	}
	
	// This method returns the user's move as a Move object
	public static Move userMove(int[][] board, Scanner input) {
		// Initialize variables and create objects
		String userMove = "";
		boolean validInput = false;
		// Check to see if the input is valid
		do {
			userMove = input.next();
			int inputCheck = validateUserInput(board, userMove);
			if (inputCheck == 2) {
				validInput = true;
			} else if (inputCheck == 1) {
				System.out.printf("That is not a possible move, please enter a move that fits in the board:\n");
				validInput = false;
			} else {
				System.out.printf("That is not an accepted format or is outside the range of the board.\nMoves must be made with a combination of a letter and a number\nI.E. a02 or B03\n");
				validInput = false;
			}
		} while (!validInput);

		// The user's move is a string, so it needs to be converted into a int with moveConvert
		return moveConvert(userMove);
	}
	
	
	/* 
	 * Method to determine if the user's input is a valid move into the board
	 * return 0 = invalid input, return 1 = invalid move, return 2 = valid
	 */
	public static int validateUserInput(int[][] board , String input) {
		// Check if the input is exactly 3 characters
		input = input.toLowerCase();
		if (input.length() != 3) {
			return 0;
		/*
		 *  Check if the input is in the format of "b01"
		 *  First line: Checking if the first character is within a-boardSize by typecasting it as a char
		 *  Second line: Checking if the second character is within the boardSize / 10
		 *  Third line: Checking if the third character is within 0-10
		 *  Fourth line: Checking if the second character is not 0, then if the third character is within
		 *  proper range
		 *  Fifth line: Checking to make sure that both char1 and char2 are not zero
		 */
		} else if ((input.charAt(0) >= (char) (97) && input.charAt(0) < (char) (97 + boardSize))
					&& (input.charAt(1) >= (char) (48) && input.charAt(1) <= (char) (48 + (boardSize / 10)))
					&& (input.charAt(2) >= (char) (48) && input.charAt(2) <= (char) (57))
					&& !(input.charAt(2) > (char) (48 + (boardSize % 10)) && (input.charAt(1) >= (char) (48 + (boardSize / 10))))
					&& !(input.charAt(1) == '0' && input.charAt(2) == '0')){	
				// Setting the index of the board to check if a move has been made there or not
				Move move = moveConvert(input);
				// Check to see if move can be made
				if (board[move.m_x][move.m_y] == 0) {
					return 2;
				} else {
					return 1;
				}
			} else {
				return 0;
			}	
	}
	
	// This method converts the character representation of a move input into a numerical representation
	public static Move moveConvert(String move) {
		// Initialize variables
		int moveX = 0;
		int moveY = 0;
		// Make the move string lower case
		move = move.toLowerCase();
		// Convert the X
		moveX = ((int) (move.charAt(0))) - 97;
		// Convert the Y
		moveY += (((int) (move.charAt(1))) - 48) * 10;
		moveY += ((int) (move.charAt(2))) - 49;

		return new Move(moveX, moveY);
	}
	
	// This method takes the board, and gives the best move back
	public static Move computerMove(int[][] board, int[][] RCDBoard, int recurseCounter, int maxRecursions, boolean isFirstPlayer) {
		// Initialize variables
		
		// This will hold the value for the moves, then the method will pick the one with the highest score
		double[][] valueArray = new double[boardSize][boardSize];
		double highestValue = -1;
		// These are for making the computer moves random
		int[] randomizingMove = new int[(boardSize * boardSize) * 2];
		int randomizedInt;
		int ties = 0;
		
		// Main loop to create the valueArray values
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (board[i][j] == 0) {
					// If at the bottom of a recursion chain, only checkOffence
					if (recurseCounter > 0 || maxRecursions == 0) {
						valueArray[i][j] = valueAssignment(board, RCDBoard, i, j, isFirstPlayer, true);
					} else {
						valueArray[i][j] = valueAssignment(board, RCDBoard, i, j, isFirstPlayer, false);
					}
				} else {
					valueArray[i][j] = INVALID_MOVE;
				}
			}
		}
		// Create a new Move object with it being the best move on the current weights
		Move bestMove = new Move(INVALID_MOVE, INVALID_MOVE, highestValue);
		
		// Find the best move
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (valueArray[i][j] > highestValue) {
					highestValue = valueArray[i][j];
					bestMove.m_x = i;
					bestMove.m_y = j;
				}
			}
		}
		
		bestMove.m_weight = highestValue;
		
		/*
		 * If recurseCounter is > 0, then create a new board called futureBoard, which is the same board
		 * as the regular board, but make a move on every possible move, then call computerMove
		 * to generate negative weights for how much offensive potential each move generates
		 */
		if (recurseCounter > 0) {
			int[][] futureBoard = new int[boardSize][boardSize];			
		
			for (int i = 0; i < boardSize; i++) {
				for (int j = 0; j < boardSize; j++) {
					futureBoard[i][j] = board[i][j];
				}
			}

			// Iterate though the board one move in the future
			
			for (int i = 0; i < boardSize; i++) {
				for(int j = 0; j < boardSize; j++) {
					// Create a new move object
					Move futureMove = new Move(i, j, valueArray[i][j]);
					// Check if that new move is valid, if so update futureMove
					if (board[futureMove.m_x][futureMove.m_y] == 0) {
						if (isFirstPlayer) {
							futureBoard[futureMove.m_x][futureMove.m_y] = 1;
						} else {
							futureBoard[futureMove.m_x][futureMove.m_y] = -1;
						}	
						// Check if its a wining position
						if (winCheck(futureBoard, RCDBoard)) { 
							futureMove.m_weight = Math.pow(10, RCDBoard[0].length / 2);
							return futureMove;
						} else {
							// Call computerMove on the futureBoard state and update the valueArray

							// Check one move in the future for the opponent
							Move oppBest = computerMove(futureBoard, RCDBoard, recurseCounter - 1, maxRecursions, !isFirstPlayer);
							
							// Assign that move's value as a negative weight
							futureMove.m_weight -= oppBest.m_weight / 10;
							
							// Update valueArray
							valueArray[futureMove.m_x][futureMove.m_y] = futureMove.m_weight;

							// Revert the futureBoard
							futureBoard[futureMove.m_x][futureMove.m_y] = 0;
						}				
					}
				}
			}
			// Find the new best move
			highestValue = 0;
			for (int i = 0; i < boardSize; i++) {
				for (int j = 0; j < boardSize; j++) {
					if (valueArray[i][j] > highestValue) {
						highestValue = valueArray[i][j];
						bestMove.m_x = i;
						bestMove.m_y = j;
					}
				}
			}		
			bestMove.m_weight = highestValue;
		}
			
		// Randomize moves in case there more than one best move
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (valueArray[i][j] == highestValue) {
					// Two declarations are made to keep randomizingMove a 1d array
					randomizingMove[ties] = i;
					randomizingMove[ties + 1] = j;
					ties += 2;
				}
			}
		}
		
		// Pick a random bestMove
		if (ties >= 4) {
			randomizedInt = ((int) (Math.random() * ties / 2)) * 2;
			bestMove.m_x = randomizingMove[randomizedInt];
			bestMove.m_y = randomizingMove[randomizedInt + 1];
		}
		// Check for weirdness
		if (bestMove.m_x == INVALID_MOVE || bestMove.m_y == INVALID_MOVE)
			throw new IllegalStateException("Invalid move! x:" + bestMove.m_x + " y:" + bestMove.m_y);
		
		return bestMove;
	}
	
	// This method generates a value of how "good" it is to play into a square, for the computer player
	public static double valueAssignment(int[][] board, int[][] RCDBoard, int spaceX, int spaceY, boolean isFirstPlayer, boolean checkDefence) {
		// Initialize variables
		boolean hasO = false;
		boolean hasX = false;
		double value = 0;
		int boardValue;
		
		// Iterate through the RCDBoard and run a addition if the space is present in it
		for (int i = 0; i < RCDBoard.length; i++) {
			boardValue = 0;
			// Iterate through each row, column, diagonal individually
			for (int j = 0; j < RCDBoard[i].length; j += 2) {
				// Check if each row, column, or diagonal has the space being checked
				if (RCDBoard[i][j] == spaceX && RCDBoard[i][j + 1] == spaceY) {
					// If yes, add every value of the row, column, or diagonal to boardValue
					for (int k = 0; k < RCDBoard[i].length; k += 2) {
						boardValue += board[RCDBoard[i][k]][RCDBoard[i][k + 1]];
						// If the RCD hasOpposite or hasSame, update the variables
						if (board[RCDBoard[i][k]][RCDBoard[i][k + 1]] == 1) {
							hasX = true;
						} else if (board[RCDBoard[i][k]][RCDBoard[i][k + 1]] == -1) {
							hasO = true;
						}						
					}
					/* 
					 * If isFirstPlayer, then feed weightCalc the regular values,
					 * If not isFIrstPlayer, then feed weightCalc the opposite values
					 */ 
					if (isFirstPlayer) {	
						value += weightCalc(boardValue, hasX, hasO, checkDefence);
					} else {
						value += weightCalc(boardValue * -1 , hasO, hasX, checkDefence);
					}
					hasX = false; hasO = false;
				}								
			}			
		}
			
		return value;
	}
	
	// This method contains the switch statement for the value of a space given the board value
	public static int weightCalc (int boardValue, boolean hasSame, boolean hasOpposite, boolean checkDefence) {
		// If hasSame and hasOpposite, then return 0
		if (hasSame && hasOpposite) {
			return 0;
		} else {
			/*
			* If positive and not hasOpposite, return boardValue^10
			* If negative and not hasSame AND checkDefence, return boardValue^10 / 2
			*/ 
			if (boardValue > 0 && !hasOpposite) {
				return (int) (Math.pow(10, boardValue));
			} else if ((boardValue < 0 && !hasSame) && checkDefence) {
				boardValue *= -1;
				return (int) ((Math.pow(10, boardValue) / 2));
			} else {
				// If the line is empty, return 1
				return 1;
			}
		}
	}
	
	// This method returns the size for the RCDBoard array
	public static int RCDBoardSizer (int boardSize) {
		// Initialize variables
		int result = 0;
		
		/*
		 * The amount of rows is given by boardSize - lineSize + 1
		 * Multiply by the boardSize to get every possible row on the board.
		 * Columns are the same, so multiply the result by 2
		 */
		result = (boardSize - lineSize + 1) * boardSize * 2;
		/*
		 * Diagonals operate on the same principle
		 * The amount of diagonals is given by boardSize - lineSize + 1
		 * Then multiplied by boardSize - lineSize + 1 to account for the girth
		 * The other set of diagonals are the same, so multiply the result by 2 
		 */
		result += (boardSize - lineSize + 1) * (boardSize - lineSize + 1) * 2;
		// Return the result
		return result;
	}
	
	// This method creates the RCDBoard Array
	public static void RCDBoardGeneration (int[][]RCDBoard, int boardSize) {
		// Initialize variables
		int index = 0;
		
		/*
		 * When iterating through the board, two numbers will be used
		 * boardSize: Used when the checked direction is not the added one
		 * boardSize - lineSize + 1: Used when the checked direction is the added one
		 * The lineSize is added to account for the size of the board and to make sure
		 * it doesn't index incorrectly. The plus one is also there for the same reason
		 */
		
		// Adding rows to the RCDBoard
		// First iteration cycles through the board vertically
		for (int i = 0; i < boardSize; i++) {
			// Second iteration cycles between rows through the row
			for (int j = 0; j < (boardSize - lineSize + 1); j++) {
				// Third iteration adds all the values of the row to the array
				for (int k = 0; k < lineSize * 2; k += 2) {
					RCDBoard[index][k] = i;
					RCDBoard[index][k + 1] = j + k / 2;
				}
				// Increase the index of the multidimensional array
				index++;
			}
		}
		// Adding rows to the RCDBoard
		// First iteration cycles through the board horizontally
		for (int i = 0; i < (boardSize - lineSize + 1); i++) {
			// Second iteration cycles between columns through the columns
			for (int j = 0; j < boardSize; j++) {
				// Third iteration adds all the values of the column to the array
				for (int k = 0; k < lineSize * 2; k += 2) {
					RCDBoard[index][k] = i + k / 2;
					RCDBoard[index][k + 1] = j;
				}
				// Increase the index of the multidimensional array
				index++;
			}
		}
		// Adding the right facing diagonals to the RCDBoard
		// First iteration cycles through the board vertically
		for (int i = 0; i < (boardSize - lineSize + 1); i++) {
			// Second iteration cycles through the board horizontally
			for (int j = 0; j < (boardSize - lineSize + 1); j++) {
				// Third iteration adds all the values of the diagonal to the array
				for (int k = 0; k < lineSize * 2; k += 2) {
					RCDBoard[index][k] = i + k / 2;
					RCDBoard[index][k + 1] = j + k / 2;
				}
				// Increase the index of the multidimensional array
				index++;
			}
		}
		// Adding the left facing diagonals to the RCDBoard
				// First iteration cycles through the board vertically
				for (int i = 0; i < (boardSize - lineSize + 1); i++) {
					// Second iteration cycles through the board horizontally
					for (int j = 0; j < (boardSize - lineSize + 1); j++) {
						// Third iteration adds all the values of the diagonal to the array
						for (int k = 0; k < lineSize * 2; k += 2) {
							RCDBoard[index][k] = i + k / 2;
							RCDBoard[index][k + 1] = (j + lineSize - 1) - (k / 2);
						}
						// Increase the index of the multidimensional array
						index++;
					}
				}
		}
	
	// This method copies one array onto the next
	public static void arrayCopier (int[] array1, int[] array2) {
		for (int i = 0; i < array1.length; i++) {
			array2[i] = array1[i];
		}
	}
	
	// This method inverts (*-1) to all values in an array
	public static void arrayInverter(int[] array) {
		for (int i = 0; i < array.length; i++) {
			array[i] *= -1;
		}
	}
	
	// This method takes in coordinates, and turns them into a standard index to reference the board
	public static int coordinateConverter(int[] coords) {
		int result = 0;
		int placeValue = 0;
		
		// Increment through the coords and add their place value onto result
		for (int i = 0; i < coords.length; i++) {
			placeValue = (int) (Math.pow(boardSize, i));
			result += coords[i] * placeValue;
		}
		
		return result;
	}
	
	// This object stores the x and y coordinates, and value, of a move
	static class Move {
		// Variables
		int m_x;
		int m_y;
		double m_identity;
		double m_weight;
		
		// Allow creation of a Move object without a value
		Move(int x, int y) {
			this(x, y, 0.0d, 0.0d);
		}
		// Allow creation of a Move object without a boardValue
		Move(int x, int y, double z) {
			this(x, y, z, 0.0d);
		}
		// The Move object just is a container of coordinates and values
		Move(int x, int y, double identity, double weight) {
			m_x = x;
			m_y = y;
			m_identity = identity;
			m_weight = weight;
		}
	}
	
	static class Space {
		// Variables
		
		/*
		 * The array s_coordinates will store the coordinates of the space, the reason for the array 
		 * is to store an arbitrary amount of coordinates. If it were variables, there would be an upper
		 * limit. The array is created when the boardCreator passes in the coordinates in array form
		 */
		public static int[] s_coordinates;
		
		// Identity is declaring if it is a X, O or empty, with X = 1, O = -1
		public static int s_identity = 0;
		
		/*
		 * The 3d array s_rays will be created when a Space object is declared. The Space object
		 * iterates through every possible ray (using the lineSize as the limiting size) and
		 * stores not the coordinates of other Space objects, but the index into the board
		 * First dimension is index of the ray
		 * Second dimension is the index of the space inside the ray
		 * Third dimension is the space's coordinates
		 */
		public static int[][][] s_rays;
		
		// Weight is for the computer only, it stores how good it is to play into it.
		public static double s_weight;
		
		// Space should not be constructed with no coordinates
		Space () {
			throw new IllegalStateException("Space was initalized with no value!");
		}
		
		// Constructor with just coords
		Space (int[] coordinates){
			this(coordinates, 0, 0);
		}
		
		// Constructor, rays gets created inside of Space
		Space (int[] coordinates, int identity, double weight) {
			// Initialize variables
			s_coordinates = coordinates;
			s_identity = identity;
			s_weight = weight;
	
			// Create the ray array
			/*
			 * The most amount of ray a space can be in is smaller than
			 * 3^dimensions * lineSize - 1
			 */
			s_rays = new int[(int) (Math.pow(3 , dimensions)) * (lineSize - 1)][lineSize][dimensions];
			
			// Iterate through every ray, and if valid, add to s_rays
			for (int i = 0; i < s_rays.length; i++) {
				
			}
		}
		/*
		 * This method creates rays for the Space object. The direction array is the direction of the
		 * ray cast to create the ray. The length of the array is the dimensions.
		 * recursionCounter increases each recursion.
		 * rays is past to have the lowest layer input new rays
		 */
		public static void rayCreation (int[] direction, int recursionCounter) {
			// Check if at the bottom of a recursion chain, if not, recurse with new direction
			if (recursionCounter == direction.length) {
				// Create new rays with the given direction and startingCoords
				
				// Iterate through the possible rays
				for (int i = 0; i < lineSize - 1; i++) {
					// Create temporary values for the coordinates for the start of the ray and direction
					int[] tempCoords = new int[lineSize];
					int[] tempDirection = new int[dimensions];
					boolean validCoords = false;
					arrayCopier(direction, tempDirection);
					arrayInverter(tempDirection);
					
					// Create the new starting coords for the ray in the opposite direction of the ray 
					tempCoords = rayIncrementer(tempDirection, s_coordinates, i);
					
					// Check if the tempCoords are inside of the board
					for (int j = 0; j < dimensions; j++) {
						if (tempCoords[j] >= 0 && tempCoords[j] < boardSize) {
							validCoords = true;
						} else {
							validCoords = false;
							break;
						}					
					}
					
					// If the coordinates are invalid, check the next set
					if (!validCoords) {
						continue;
					}
								
					// Create the ray
					Ray ray = new Ray(tempDirection, tempCoords);
					
					for (int j = 0; j < lineSize; j++) {
						ray[j] = rayIncrementer(direction, tempCoords, j);
					}
					
					// Check if the final set of coordinates in the ray are inside of the board
					for (int j = 0; j < dimensions; j++) {
						if (ray[lineSize][j] >= 0 && ray[lineSize][j] < boardSize) {
							validCoords = true;
						} else {
							validCoords = false;
							break;
						}					
					}
					
					// If the coordinates are invalid, check the next set
					if (!validCoords) {
						continue;
					}
					
					// Standardize the ray by sorting the coordinates from smallest to largest
					
					
					// Add the ray into rays
					
				}
			} else {
				// Create new array to recurse with
				int[] newDirection = new int[direction.length];
				arrayCopier(direction, newDirection);
				
				// Iterate thought the new directions
				rayCreation(newDirection, recursionCounter + 1);
				newDirection[recursionCounter] = 1;
				rayCreation(newDirection, recursionCounter + 1);
				newDirection[recursionCounter] = -1;
				rayCreation(newDirection, recursionCounter + 1);
			}
		}
		
		/*
		 * This method returns a new set of coordinates using the given direction and starting coords.
		 */
		public static int[] rayIncrementer(int[] direction, int[] startingCoords, int amountOfIncrements) {
			// Create a new set of coordinates
			int[] newCoords = new int[dimensions];
			arrayCopier(newCoords, startingCoords);
			// Add the value of direction onto the coordinates
			for (int i = 0; i < dimensions; i++) {
				newCoords[i] += (direction[i] * amountOfIncrements);
			}
			// Return the new coordinates
			return newCoords;
		}
	}
	
	static class Ray {
		// Fields
		
		// The r_coordinates 2d array holds all the coords for the Spaces that are in the Ray
		int[][] r_coordinates;
		int[] r_direction;
		int[] r_startingCoords;
		// Main Constructor
		Ray(int[] direction, int[] startingCoords) {
			
		}
	}
}
