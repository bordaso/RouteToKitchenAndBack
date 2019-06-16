package com.topdesk.cases.toprob;

import java.util.List;

/**
 * A solution to find a safe path from the room to the kitchen and back.
 * 
 * <p>
 * Instances should provide a void (no arguments) constructor.
 * 
 * <p>
 * Instances of implementations of this interface are immutable.
 */
public interface Solution {
	/**
	 * Finds a path from the room to the kitchen and back. After the last move
	 * Rob should be in his room. The solution is at most 100 instructions, i.e.
	 * the size of the returned {@code List} is smaller than or equal to 100.
	 * This solution is not guaranteed to be the shortest path.
	 * <p>
	 * Rob starts his journey at the given {@code time} in his room.
	 * 
	 * @param grid
	 *            the grid to find a solution for
	 * @param time
	 *            the time that Rob starts his journey. This value is bigger
	 *            than or equals to zero
	 * @return a {@code List} with all instructions
	 * @throws NullPointerException
	 *             if {@code grid} is {@code null}
	 * @throw IllegalArgumentException if {@code time} is negative.
	 */
	List<Instruction> solve(Grid grid, int time);
}
