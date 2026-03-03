
public interface TicTacToeManager {
	
	// This method copies one array onto the next
	public default void arrayCopier (int[] array1, int[] array2) {
		for (int i = 0; i < array1.length; i++) {
			array2[i] = array1[i];
		}
	}
	// This method inverts (*-1) to all values in an array
	public default void arrayInverter(int[] array) {
		for (int i = 0; i < array.length; i++) {
			array[i] *= -1;
		}
	}
	/*
	 * This method returns a new set of coordinates using the given direction and starting coords.
	 */
	public default int[] rayIncrementer(int[] direction, int[] startingCoords, int amountOfIncrements) {
		// Create a new set of coordinates
		int[] newCoords = new int[direction.length];
		arrayCopier(startingCoords, newCoords);
		// Add the value of direction onto the coordinates
		for (int i = 0; i < direction.length; i++) {
			newCoords[i] += (direction[i] * amountOfIncrements);
		}
		// Return the new coordinates
		return newCoords;
	}
	
	// This method takes in coordinates, and turns them into a standard index to reference the board
	public default int coordinateConverter(int[] coords, int boardSize) {
		int result = 0;
		int placeValue = 0;
			
		// Increment through the coords and add their place value onto result
		for (int i = 0; i < coords.length; i++) {
			placeValue = (int) (Math.pow(boardSize, i));
			result += coords[i] * placeValue;
		}
		return result;
	}
}
