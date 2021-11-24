package nl.andrewl.railsignalapi.model;

public enum Direction {
	NORTH,
	SOUTH,
	EAST,
	WEST,
	NORTH_WEST,
	NORTH_EAST,
	SOUTH_WEST,
	SOUTH_EAST;

	public Direction opposite() {
		return switch (this) {
			case NORTH -> SOUTH;
			case SOUTH -> NORTH;
			case EAST -> WEST;
			case WEST -> EAST;
			case NORTH_EAST -> SOUTH_WEST;
			case NORTH_WEST -> SOUTH_EAST;
			case SOUTH_EAST -> NORTH_WEST;
			case SOUTH_WEST -> NORTH_EAST;
		};
	}

	public boolean isOpposite(Direction other) {
		return this.opposite().equals(other);
	}

	public static Direction parse(String s) {
		s = s.trim().toUpperCase();
		try {
			return Direction.valueOf(s);
		} catch (IllegalArgumentException e) {
			return switch (s) {
				case "N" -> NORTH;
				case "S" -> SOUTH;
				case "E" -> EAST;
				case "W" -> WEST;
				case "NW" -> NORTH_WEST;
				case "NE" -> NORTH_EAST;
				case "SW" -> SOUTH_WEST;
				case "SE" -> SOUTH_EAST;
				default -> throw new IllegalArgumentException("Invalid direction: " + s);
			};
		}
	}
}
