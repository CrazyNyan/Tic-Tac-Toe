class Ray implements TicTacToeManager {
		// Fields
		
		// The r_coordinates 2d array holds all the coords for the Spaces that are in the Ray
		public int[][] r_coordinates;
		
		// The r_indexes array holds the indexes of the Space objects instead of the coordinates
		public int[] r_indexes;
		
		// This boolean is true if the Ray fits into the board, false if not
		public boolean r_valid;
		
		// These ints holds the weight of the Ray
		public int r_weightX;
		public int r_weightO;
		
		// Space should not be constructed with no coordinates
		Ray () {
			throw new IllegalStateException("Ray was initalized with no values!");
		}
		// Main Constructor
		Ray (int[] direction, int[] startingCoords, int dimensions, int lineSize, int boardSize) {
			// Initialize arrays
			r_coordinates = new int[lineSize][dimensions];
			
			r_indexes = new int[lineSize];
			
			// Initialize weight
			r_weightX = 1;
			r_weightO = 1;
			
			// Create the r_coordinates array 
			for (int i = 0; i < lineSize; i++) {
				arrayCopier(rayIncrementer(direction, startingCoords, i), r_coordinates[i]);
			}
			
			
			/*
			 * Check if the ray is valid, the only coords that need to be checked are the final ones,
			 * as the first ones are checked upon creation
			 */
			r_valid = true;
			for (int i = 0; i < dimensions; i++) {
				if (r_coordinates[lineSize - 1][i] >= boardSize || r_coordinates[lineSize - 1][i] < 0) {
					r_valid = false;
					break;
				}
			}
			
			// Sort the coordinates from smallest to largest
			
			// Convert the coordinates to indexes
			for (int i = 0; i < r_coordinates.length; i++) {
				r_indexes[i] = coordinateConverter(r_coordinates[i], boardSize);
			}
			
			// Perform the sort
			for (int i = lineSize - 1; i > 0; i--) {
				for (int j = 0; j < i; j++) {
					int tempValue;
					//int[] tempArray = new int[dimensions];
					
					if (r_indexes[j] > r_indexes[j + 1]) {
						tempValue = r_indexes[j];
						r_indexes[j] = r_indexes[j + 1];
						r_indexes[j + 1] = tempValue;
						
						
						// Probably dont need this:
						//arrayCopier(r_coordinates[j], tempArray);
						//arrayCopier(r_coordinates[j], r_coordinates[j + 1]);
						//arrayCopier(tempArray, r_coordinates[j + 1]);
					}
				}
			}
			
			// If any index = itself, must be invalid
			for (int i = 0; i < lineSize - 1; i++) {
				if (r_indexes[i] == r_indexes[i + 1]) {
					r_valid = false;
				}
			}
			
		}

	}