import java.util.ArrayList;
class Space implements TicTacToeManager {
		// Variables
		
		/*
		 * The array s_coordinates will store the coordinates of the space, the reason for the array 
		 * is to store an arbitrary amount of coordinates. If it were variables, there would be an upper
		 * limit. The array is created when the boardCreator passes in the coordinates in array form
		 */
		public int[] s_coordinates;
		
		// Identity is declaring if it is a X, O or empty, with X = 1, O = -1
		public int s_identity = 0;
		
		/*
		 * The array s_rays will be created when a Space object is declared. The Space object
		 * iterates through every possible ray (using the lineSize as the limiting size) and
		 * stores Ray objects
		 */
		public ArrayList<Ray> s_rays;
		
		// Weight is for the computer only, it stores how good it is to play into it.
		public double s_weight;
		
		// This field holds the amount of dimensions for the board
		public int s_dimensions;
		
		// This field holds the size of the line for a board
		public int s_lineSize;
		
		// This field holds the size of the board
		public int s_boardSize;
		
		// Space should not be constructed with no coordinates
		Space () {
			throw new IllegalStateException("Space was initalized with no value!");
		}
		
		// Constructor with just coords
		Space (int[] coordinates, int dimensions, int lineSize, int boardSize) {
			this(coordinates, 0, 0, dimensions, lineSize, boardSize);
		}
		
		// Constructor, rays gets created inside of Space
		Space (int[] coordinates, int identity, double weight, int dimensions, int lineSize, int boardSize) {
			// Initialize variables
			s_coordinates = new int[dimensions];
	
			arrayCopier(coordinates, s_coordinates);
			s_identity = identity;
			s_weight = weight;
			s_dimensions = dimensions;
			s_lineSize = lineSize;
			s_boardSize = boardSize;
			
			// Create the ray array
			/*
			 * The most amount of ray a space can be in is smaller than
			 * 3^dimensions * lineSize - 1
			 */
			s_rays = new ArrayList<Ray>();
			// Call the rayCreation method
			int[] direction = new int[dimensions];
			rayCreation(direction, 0);
			
		}
		/*
		 * This method creates rays for the Space object. The direction array is the direction of the
		 * ray cast to create the ray. The length of the array is the dimensions.
		 * recursionCounter increases each recursion.
		 * rays is past to have the lowest layer input new rays
		 */
		protected void rayCreation (int[] direction, int recursionCounter) {
			// Check if at the bottom of a recursion chain, if not, recurse with new direction
			if (recursionCounter == direction.length) {
				// Create new rays with the given direction and startingCoords
				
				// Iterate through the possible rays
				for (int i = 0; i < s_lineSize - 1; i++) {
					// Create temporary values for the coordinates for the start of the ray and direction
					int[] tempCoords = new int[s_lineSize];
					int[] tempDirection = new int[s_dimensions];
					boolean validCoords = false;
					arrayCopier(direction, tempDirection);
					arrayInverter(tempDirection);
					
					// Create the new starting coords for the ray in the opposite direction of the ray 
					tempCoords = rayIncrementer(tempDirection, s_coordinates, i);
					
					// Check if the tempCoords are inside of the board
					for (int j = 0; j < s_dimensions; j++) {
						if (tempCoords[j] >= 0 && tempCoords[j] < s_boardSize) {
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
					// Uninvert the direction
					arrayInverter(tempDirection);
					// Create the ray
					Ray ray = new Ray(tempDirection, tempCoords, s_dimensions, s_lineSize, s_boardSize);
					
					// If the coordinates are invalid, check the next set
					if (!ray.r_valid) {
						continue;
					}
					
					// Add the ray into rays				
					// Check if the ray is in rays already, if yes discard, if not add
					if (rayValidation(ray)) {
						System.out.println("New ray created!");
						s_rays.add(ray);
					}
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
		
		// TODO remove
		public static void printArray(int[] array) {
			for (int i = 0; i < array.length; i++) {
				System.out.print(array[i]);
			}
		}
		
		/* 
		 * This method checks if a ray is present in the s_rays ArrayList
		 * Returns TRUE if it is NOT in the ArrayList
		 * Returns FALSE if it is IN the ArrayList
		 */
		public boolean rayValidation (Ray ray) {
			boolean newRay = true;
			
			for (int i = 0; i < s_rays.size() && newRay == true; i++) {
				Ray tempRay = s_rays.get(i);
				boolean difDetected = false;
				// Check if the indexes of the tempRay are blank, if yes then a new ray
				for (int j = 0; j < s_lineSize; j++) {
					if (ray.r_indexes[j] != tempRay.r_indexes[j]) {
						difDetected = true;
						break;
					} else {
						difDetected = false;
					}
				}
				if(!difDetected) {
					newRay = false;
				}
				
			}
			return newRay;
		}
		
	}