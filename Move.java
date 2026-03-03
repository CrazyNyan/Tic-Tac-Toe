// This object stores the x and y coordinates, and value, of a move
	class Move {
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

