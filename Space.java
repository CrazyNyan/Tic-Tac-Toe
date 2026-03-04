import java.util.ArrayList;
class Space extends TicTacToe implements TicTacToeManager {
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
		public int s_weightX;
		public int s_weightO;
		
		// This field holds the amount of dimensions for the board
		public int s_dimensions;
		
		// This field holds the size of the line for a board
		public int s_lineSize;
		
		// This field holds the size of the board
		public int s_boardSize;
		
		// This field is true if a win has been detected, and is false if no win is detected
		public boolean s_isWin;
		
		// This field contains the index of the Space
		public int s_index;
		// Space should not be constructed with no coordinates
		Space () {
			throw new IllegalStateException("Space was initalized with no value!");
		}
		
		// Constructor with just coords
		Space (int[] coordinates, int dimensions, int lineSize, int boardSize, int index) {
			this(coordinates, 0, 0, dimensions, lineSize, boardSize, index);
		}
		
		// Constructor, rays gets created inside of Space
		Space (int[] coordinates, int identity, double weight, int dimensions, int lineSize, int boardSize, int index) {
			// Initialize variables
			s_coordinates = new int[dimensions];
	
			arrayCopier(coordinates, s_coordinates);
			s_identity = identity;
			s_dimensions = dimensions;
			s_lineSize = lineSize;
			s_boardSize = boardSize;
			s_index = index;
			
			// Create the Ray ArrayList
			s_rays = new ArrayList<Ray>();
			// Call the rayCreation method
			int[] direction = new int[dimensions];
			rayCreation(direction, 0);
			s_weightX = s_rays.size();
			s_weightO = s_rays.size();
			
			System.out.println("Im a new space object with these coordinates: ");
			for (int i = 0; i < coordinates.length; i++) {
				System.out.print(coordinates[i] + ", ");
			}
			System.out.println("\n and: " + s_rays.size() + " Rays");
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
		
		
		// This method updates the identity of the Space, the X0_ value, and calls updateWeight
		public void updateIdentity(int newIdentity) {
			s_identity = newIdentity;
			updateWeight(true, s_index);
		}
		
		public void updateWeight(boolean pingToUpdate, int indexOfChange) {
			if (pingToUpdate) {
				// Create a new ArrayList that holds the index of the Space objects that have been updated
				ArrayList<Integer> pingedSpaces = new ArrayList<Integer>();
				
				// Go thought every Ray and ping every space, as long as they arn't in pingedSpaces
				for (int i = 0; i < s_rays.size(); i++) {
					Ray currRay = s_rays.get(i);
					// Iterate through all indexes in this ray
					for (int j = 0; j < s_lineSize; j++) {
						// Check if the index has already been pinged
						boolean isNewPing = true;
						int index = currRay.r_indexes[j];
						// Check the pingedSpaces ArrayList
						for (int k = 0; k < pingedSpaces.size(); k++) {
							if (index == pingedSpaces.get(k)) {
								isNewPing = false;
							}
						}
						
						// If it has, then do not ping again
						if (!isNewPing) {
							continue;
						}
						
						// Call the updateWeight method on the Space object of the index
						board.get(index).updateWeight(false, s_index);
						// Add the index to pinged spaces
						pingedSpaces.add(index);
					}
				}

			} else if (s_identity == 0){
			
				System.out.println("I have been pinged! My coords are: ");
				for (int i = 0; i < s_coordinates.length; i++) {
					System.out.print(s_coordinates[i] + ", ");
				}
				System.out.println();
				// Updating the weight
			
				// Iterate
				for (int i = 0; i < s_rays.size(); i++) {
					// Current Ray object
					Ray currRay = s_rays.get(i);
					boolean hasSpace = false;
					
					// Check if the Ray has the index that needs to be updated
					for (int j = 0; j < s_lineSize; j++) {
						if (currRay.r_indexes[j] == indexOfChange) {
							hasSpace = true;
							break;
						}
					}
					
					if (hasSpace) {
						// Initialize variables
						int placeValue = 0;
						boolean hasX = false;
						boolean hasO = false;
						
						// Iterate through the Ray to see how "good" it is to play into
						// Grab the values for placeValue from the Spaces in the Ray
						for (int j = 0; j < s_lineSize; j++) {
							// Grab the Space object
							int identity = board.get(currRay.r_indexes[j]).s_identity;
							
							placeValue += identity;
							
							if (identity == 1) {
								hasX = true;
							}
							if (identity == -1) {
								hasO = true;
							}
						}
						// Run the weight calculation and add the result into the Ray
						currRay.r_weightX = weightCalc(placeValue, hasX, hasO);
						currRay.r_weightO = weightCalc(placeValue * -1, hasO, hasX);
					}
				}
				
				// Add up all the weights across the Rays
				s_weightX = 0;
				s_weightO = 0;
				
				for (int i = 0; i < s_rays.size(); i++) {
					s_weightX += s_rays.get(i).r_weightX;
					s_weightO += s_rays.get(i).r_weightO;
				}
			} else {
				s_weightX = -1;
				s_weightO = -1;
			}
		}
		
		public static int weightCalc (int boardValue, boolean hasSame, boolean hasOpposite) {
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
				} else if (boardValue < 0 && !hasSame) {
					boardValue *= -1;
					return (int) ((Math.pow(10, boardValue) / 2));
				} else {
					// If the line is empty, return 1
					return 1;
				}
			}
		}
	}