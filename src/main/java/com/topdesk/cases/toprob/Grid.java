package com.topdesk.cases.toprob;

import java.util.Set;

/**
 * The grid on which Rob lives. The grid is rectangular, i.e. all rows have an
 * equal width and all columns have an equal height.
 * <p>
 * On the grid there is Rob's room, there is the kitchen, there are one or more
 * holes and there is one bug. The location of all these elements is guaranteed
 * to be different. The bug can for instance never appear in Rob's room or in
 * the kitchen.
 * <p>
 * It is also guaranteed that there is a free path from the room to the kitchen.
 */
public interface Grid {
	/**
	 * The width of the grid. This value is greater than or equal to one.
	 * 
	 * @return the width
	 */
	int getWidth();

	/**
	 * The height of the grid. This value is greater than or equal to one.
	 * 
	 * @return the height
	 */
	int getHeight();

	/**
	 * The location of the room Rob lives in.
	 * 
	 * @return the room
	 */
	Coordinate getRoom();

	/**
	 * The location of the kitchen where Rob has to go to.
	 * 
	 * @return the kitchen
	 */
	Coordinate getKitchen();

	/**
	 * A {@code Set} of all holes that Rob has to be careful not to fall into.
	 * 
	 * @return all holes.
	 */
	Set<Coordinate> getHoles();

	/**
	 * The location of the bug at the given {@code time}. If Rob is at the same
	 * place at the same time as the bug is, Rob will not survive.
	 * <p>
	 * The bug is never at the same location two times in a row. In other
	 * words:<br>
	 * {@code getBug(time)} will yield another result than
	 * {@code getBug(time + 1)}
	 * <p>
	 * Be aware though that the bug might show up again at the same cell
	 * sometime later. So:<br>
	 * {@code getBug(time)} and {@code getBug(time + n)} for any n>1 might have
	 * the same result.
	 * <p>
	 * The bug does not have to move between adjacent cells, but can appear
	 * anywhere on the grid.
	 * 
	 * @param time
	 *            the time for which the location is requested
	 * @return the location of the bug
	 */
	Coordinate getBug(int time);
}
