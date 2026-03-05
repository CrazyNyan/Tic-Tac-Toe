import java.util.Scanner;
import java.util.ArrayList;

public class TicTacToe implements TicTacToeManager {
	
	private static int lineSize;
	private static int dimensions;
	private static int boardSize;
	protected static boolean winDetected = false;
	
	public static ArrayList<Space> board = new ArrayList<Space>();
	
	public static void main(String[] args) {
		// Initialize variables and creating objects
		Scanner input = new Scanner(System.in);
		boolean isFirstPlayer = true;
		boolean validInput = false;
		boolean pauseInbetweenMoves;
		int moves = 0;
		int userInput;

		String firstPlayer;
		String secondPlayer;
		// Prompt the user to set up the game and choose if there will be any computer players
		do {
			System.out.println("Please select the first player by typing either 'user' or 'computer'");
			firstPlayer = input.next();
			firstPlayer = firstPlayer.toLowerCase();
			if (firstPlayer.equals("user") || firstPlayer.equals("computer")) {
				validInput = true;
			}
		} while (!validInput);
		validInput = false;
		do {
			System.out.println("Please select the second player by typing either 'user' or 'computer'");
			secondPlayer = input.next();
			secondPlayer = secondPlayer.toLowerCase();
			if (secondPlayer.equals("user") || secondPlayer.equals("computer")) {
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
				boardSize = userInput;
				validInput = true;
			} else {
				validInput = false;
			}
		} while(!validInput);
		
		// If the board is larger than 3, prompt the user to enter how large of a connection is required for a score
		if (userInput > 3) {
			validInput = false;
			do {
				System.out.println("Please enter the amout of spaces that need to be togeather for winning, default is 4, max is 10:");
				userInput = input.nextInt();
				if (userInput >= 3 && (userInput <= 10 && userInput <= boardSize)) {
					validInput = true;
					lineSize = userInput;
				}
			} while (!validInput);
		} else {
			lineSize = 3;
		}
		
		// Prompt the user to enter the dimensions of the board
		do {
			System.out.println("Please enter the dimensions of the board as a single integer, lowest is 2, highest is 10.");
			System.out.println("Note: any dimension higher than 4 will cause EXTREME LAG");
			userInput = input.nextInt();
			if (userInput >= 2 && userInput <= 10) {
				dimensions = userInput;
				validInput = true;
			} else {
				validInput = false;
			}
		} while(!validInput);
		
		System.out.println("Type y to enable a pause inbetween moves:");
		if (input.next().charAt(0) == 'y') {
			pauseInbetweenMoves = true;
		} else {
			pauseInbetweenMoves = false;
		}
		
		// Fill the board
		int[] coords = new int[dimensions];
		for (int i = 0; i < (int) (Math.pow(boardSize, dimensions)); i++) {
			// Create the new Space object
			Space space = new Space(coords, dimensions, lineSize, boardSize, board.size());
			
			// Add the Space object onto the arrayList
			board.add(space);
			
			// Increment the coordinate array
			coordinateIncrementer(coords, boardSize - 1, 0);
		}
		
		System.out.println("Game start!");
		
		do {
			// Display the current board
			if (dimensions <= 3) {
				if (boardSize < 16 || (firstPlayer.equals("user") || secondPlayer.equals("user")) || pauseInbetweenMoves) {
					displayBoard(dimensions, 0);
				} else if (moves % 25 == 0 && !pauseInbetweenMoves) {
					displayBoard(dimensions, 0);
				}
			}
			// Execute the move, prompting the correct player
			if (isFirstPlayer) {
				// If firstPlayer is a user, prompt them and update board
				if (firstPlayer.charAt(0) == 'u') {
					System.out.println("This is the board state, please enter your move");
					board.get(userMove(input)).updateIdentity(1);
				} else {
					// If firstPlayer is a computer, update board		
					board.get(computerMove(true)).updateIdentity(1);
				}
				// Switch the player order
				isFirstPlayer = false;
			} else {
				// If secondPlayer is a user, prompt them and update board
				if (secondPlayer.charAt(0) == 'u') {
					System.out.println("This is the board state, please enter your move");
					board.get(userMove(input)).updateIdentity(-1);
				} else {	
					// If secondPlayer is a computer, update board
					board.get(computerMove(false)).updateIdentity(-1);
				}
				// Switch the player order
				isFirstPlayer = true;
			}
			
			if (pauseInbetweenMoves) {
				System.out.println("Paused, press anything to perform next move: ");
				input.next();
			}
			// Increment the moves counter
			moves++;
		} while (!winDetected && moves != (int) (Math.pow(boardSize, dimensions)));
		// Display results of the game
		if (dimensions <= 3) {
			displayBoard(dimensions, 0);
		}
		if (winDetected) {
			if (!isFirstPlayer) { // isFirstPlayer gets changed in the game loop, so the reverse must be used
				System.out.printf("X has won the game, congradulations! The game took %d moves\n", moves);
			} else {
				System.out.printf("O has won the game, congradulations! The game took %d moves\n", moves);
			}
		} else {
			System.out.println("The game is a tie, no one won :(");
		}
	}
	
	// This method prints the current board to the screen. ONLY WORKS FOR 2-3 DIMENSIONS
	public static void displayBoard(int dimensions, int index) {
		// Displaying the board
		if (dimensions > 3) {
			throw new IllegalStateException("DISPLAYBOARD CALLED WITH TOO MANY DIMENSIONS");
		}
		if (dimensions == 2) {
			for (int i = 0; i < boardSize; i++) {
				// Print the side numbers
				System.out.printf("%3d", i + 1);
				// Print the char of the Spaces
				for (int j = 0; j < boardSize; j++) {
					// Get the identity of the Space
					System.out.printf(" %c " , convertChar(board.get(index).s_identity));
					// Increment the index
					index++;
					// Print the spacer ||
					if (j % boardSize != boardSize - 1) {
						System.out.printf("|");
					}
				}
				// Print the spacer lines
				System.out.printf("\n");			
				if (i != boardSize - 1) {
					System.out.printf("   ");
					for (int j = 0; j < boardSize - 1; j++) {
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
		} else {
			for (int i = 0; i < boardSize; i++) {
				System.out.println("Slice: " + (i + 1));
				displayBoard(2, index);
				index += boardSize * boardSize;
			}
		}
	}
	
	// This Method converts the board from numbers to letters / spaces
	public static char convertChar(int num) {
		// 0 = ' ', 1 = X -1 = O	
		if (num == 1) {
			return 'X';
		} else if (num == -1) {
			return 'O';
		} else {
			return ' ';
		}
			
	}
	
	// This method returns the user's move as a Move object
	public static int userMove(Scanner input) {
		// Initialize variables and create objects
		int userMove = 0;
		int userIndex = 0;
		boolean validInput = false;
		do {
			for (int i = 0; i < dimensions; i++) {
				System.out.println("Please enter the " + (i + 1) + " coordinate:");
				// Check to see if the input is valid
				do {		
					userMove = input.nextInt() - 1;
					validInput = false;
					if (userMove < boardSize && userMove >= 0) {
						// If yes, add it to the index
						validInput = true;
						userIndex += userMove * (int) (Math.pow(boardSize, i));
						System.out.println("The user index is " + userIndex);
					} else {
						System.out.printf("That is outside the range of the board.\n Please enter a new value:");
						validInput = false;
					}
				} while (!validInput);
			}
			// Check if the index is an open spot
			if (board.get(userIndex).s_identity == 0) {
				validInput = true;
			} else {
				validInput = false;
				System.out.println("That space already has a value in it, please enter a different value.");						
			}
		} while (!validInput);
		return userIndex;
	}
	
	// This method takes the board, and gives the best move back
	public static int computerMove(boolean isFirstPlayer) {
		// Initialize variables
		
		// This will hold the value for the moves, then the method will pick the one with the highest score
		int highestValue = -1;
		int highestValueIndex = 0;
		// These are for making the computer moves random
		int[] randomizingMove = new int[board.size()];
		int randomizedInt;
		int ties = 0;
		
		
		// Find the best move
		if (isFirstPlayer) {
			for (int i = 0; i < board.size(); i++) {
				if (board.get(i).s_weightX > highestValue) {
					highestValue = board.get(i).s_weightX;
					highestValueIndex = i;
				}
			}
		} else {
			for (int i = 0; i < board.size(); i++) {
				if (board.get(i).s_weightO > highestValue) {
					highestValue = board.get(i).s_weightO;
					highestValueIndex = i;
				}
			}
		}
		
		// Randomize moves in case there more than one best move
		/*
		if (isFirstPlayer) {
			for (int i = 0; i < board.size(); i++) {
				if (board.get(i).s_weightX == highestValue) {
					randomizingMove[ties] = i;
					ties++;
				}
			}
		} else {
			for (int i = 0; i < board.size(); i++) {
				if (board.get(i).s_weightO == highestValue) {
					randomizingMove[ties] = i;
					ties++;
				}
			}
		}
		// Pick a random bestMove
		if (ties >= 2) {
			randomizedInt = ((int) (Math.random() * ties));
			highestValue = randomizingMove[randomizedInt];
		}
		*/
		return highestValueIndex;
	}
	
	// This method increments an array, and if a value exceeds a threshold it increments the next index
	public static void coordinateIncrementer(int[] array, int threshold, int index) {
		array[index]++;
		if (array[index] > threshold && index < array.length - 1) {
			array[index] = 0;
			coordinateIncrementer(array, threshold, index + 1);
		}
	}
	
	/*
	 * OLD MAIN METHOD:: 
	 * 
		
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
		
		
	 */
	
	/*
	 * Old depth calc
	 * // Iterate though the board one move in the future
			if (recurseCounter > 0) {
			int[][] futureBoard = new int[boardSize][boardSize];			
		
			for (int i = 0; i < boardSize; i++) {
				for (int j = 0; j < boardSize; j++) {
					futureBoard[i][j] = board[i][j];
				}
			}
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
	 */
	
	
}
