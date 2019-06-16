package com.topdesk.cases.toprob;

/**
 * A container for two integer values, called x and y. Two instances are
 * considered equal if and only if their x values are equal and their y values
 * are equal.
 * <p>
 * Instances of this class are immutable.
 */
public final class Coordinate {

	private final int x;
	private final int y;

	/**
	 * Creates a new instance
	 * 
	 * @param x the value for x
	 * @param y the value for y
	 */
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * @return the value of x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @return the value of y
	 */
	public int getY() {
		return y;
	}

	@Override
	public int hashCode() {
		int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Coordinate)) {
			return false;
		}

		Coordinate other = (Coordinate) obj;
		return other.x == x && other.y == y;
	}

	@Override
	public String toString() {
		return "[" + x + ", " + y + "]";
	}
}
