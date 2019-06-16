package com.topdesk.cases.toprob;

import java.util.function.Function;

/**
 * An instruction for Rob on how to move on a {@link Grid}. The method
 * {@link #execute(Coordinate)} has been implemented as a convenience method to
 * execute the instruction.
 */
public enum Instruction {
	/**
	 * Instruction to move towards the north. If rob is moved from coordinate c1
	 * towards the north, the y part of new coordinate c2 will be one smaller
	 * than that of c1.
	 */
	NORTH(c -> new Coordinate(c.getX(), c.getY() - 1)),
	/**
	 * Instruction to move towards the east. If rob is moved from coordinate c1
	 * towards the east, the x part of new coordinate c2 will be one bigger than
	 * that of c1.
	 */
	EAST(c -> new Coordinate(c.getX() + 1, c.getY())),
	/**
	 * Instruction to move towards the south. If rob is moved from coordinate c1
	 * towards the south, the y part of new coordinate c2 will be one bigger
	 * than that of c1.
	 */
	SOUTH(c -> new Coordinate(c.getX(), c.getY() + 1)),
	/**
	 * Instruction to move towards the west. If rob is moved from coordinate c1
	 * towards the west, the x part of new coordinate c2 will be one smaller
	 * than that of c1.
	 */
	WEST(c -> new Coordinate(c.getX() - 1, c.getY())),
	/**
	 * Instruction to wait. Rob does not change his position.
	 */
	PAUSE(c -> c);

	private final Function<Coordinate, Coordinate> move;

	private Instruction(Function<Coordinate, Coordinate> move) {
		this.move = move;
	}

	/**
	 * Executes the instruction to make a move. Every move takes one second.
	 * Also the {@link #PAUSE} instruction takes one second.
	 * 
	 * @param cell
	 *            the current cell
	 * @return the coordinates of the cell after the instruction has been
	 *         executed.
	 */
	public Coordinate execute(Coordinate cell) {
		return move.apply(cell);
	}
}
