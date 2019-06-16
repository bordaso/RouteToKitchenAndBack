package com.topdesk.cases.toprob.helper;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.topdesk.cases.toprob.Coordinate;
import com.topdesk.cases.toprob.Grid;

/**
 * Helper class to create a {@link Grid}. Use this class by calling its static
 * method {@link #create}.
 * 
 * <p>
 * A grid is created by parsing an array of strings. The first character of the
 * first element of this array corresponds with {@link Coordinate}
 * {@code [0, 0]}. The last character of the last element of this array
 * corresponds with {@code Coordinate} {@code [width - 1, height - 1]}. Where
 * {@code width} and {@code height} are the dimensions of the grid.
 * 
 * <p>
 * The characters of these strings can be (and can only be):
 * <ul>
 * <li>{@code '.'} - empty cell
 * <li>{@code 'r'} - Rob's room
 * <li>{@code 'k'} - the kitchen
 * <li>{@code 'o'} - a hole; there can be multiple holes
 * <li>Any character greater than or equal to {@code 'A'} and smaller than or
 * equal to {@code 'Z'} - These are the different positions of the bug.
 * {@code 'A'} is the position of the bug at time 0, {@code 'B'} is the position
 * of the bug at time 1, etc. After all bug cells have been used, the sequence
 * starts over again.
 * </ul>
 * 
 * <p>
 * Requirements:
 * <ul>
 * <li>The grid is rectangular. That is why all elements in the given string
 * array have to be of equal length.
 * <li>The room, the kitchen and the bug are all mandatory.
 * <li>There can be zero or more holes.
 * <li>The bug has to appear at at least two places. That is why there should be
 * at least an {@code 'A'} and a {@code 'B'} in the strings to parse.
 * <li>The upper case characters for the bug must be consecutive, e.g. there
 * cannot be a {@code 'D'} without also an {@code 'A'}, a {@code 'B'} and a
 * {@code 'C'}.
 * </ul>
 * 
 * <p>
 * Example:
 * <p>
 * {@code .r.k.}<br>
 * {@code ...oo}<br>
 * {@code A.C.B}<br>
 * <p>
 * <ul>
 * <li>This grid has width 5 and height 3
 * <li>The room is at coordinate {@code [1, 0]}
 * <li>The kitchen is at coordinate {@code [3, 0]}
 * <li>There are holes at coordinates {@code [3, 1]} and {@code [4, 1]}
 * <li>The bug moves between coordinates {@code [0, 2]} (at time=0),
 * {@code [4, 2]} (at time=1), {@code [2, 2]} (at time=2), {@code [0, 2]} (at
 * time=3), {@code [4, 2]} (at time=4), etc.
 * </ul>
 */
public final class GridFactory {

	private static final char EMPTY = '.';
	private static final char ROOM = 'r';
	private static final char KITCHEN = 'k';
	private static final char HOLE = 'o';
	private static final char MIN_BUG = 'A';
	private static final char MAX_BUG = 'Z';
	
	private final String[] lines;
	private final int height;
	private final int width;

	private Coordinate room = null;
	private Coordinate kitchen = null;
	private final Set<Coordinate> holes = new HashSet<>();
	private final Map<Integer, Coordinate> bugLocations = new HashMap<>();

	private GridFactory(String... in) {
		this.lines = in.clone();
		this.height = lines.length;
		this.width = lines[0].length();
	}

	/**
	 * Creates a grid by parsing the given {@code lines}. See the documentation
	 * on the level of this class for more detailed documentation.
	 * 
	 * @param lines
	 *            an array of strings to be parsed
	 * @return the {@link Grid}
	 * @throws NullPointerException
	 *             if lines is {@code null}
	 * @throws IllegalArgumentException
	 *             if one of the requirements as described on the level of this
	 *             class is violated
	 */
	public static Grid create(String... lines) {
		return new GridFactory(lines)
				.parse()
				.toGrid();
	}

	private GridFactory parse() {
		for (int y = 0; y < height; y++) {
			String line = lines[y];

			if (line.length() != width) {
				throw new IllegalArgumentException("lines of grid have different lenghts");
			}

			for (int x = 0; x < width; x++) {
				setProperty(x, y, line.charAt(x));
			}
		}

		testForMandatoryElements();

		return this;
	}

	private void setProperty(int x, int y, char c) {
		if (c == ROOM) {
			setRoom(x, y);
		} else if (c == KITCHEN) {
			setKitchen(x, y);
		} else if (c == HOLE) {
			addHole(x, y);
		} else if (c >= MIN_BUG && c <= MAX_BUG) {
			addBug(x, y, c - MIN_BUG);
		} else if (c != EMPTY) {
			throw new IllegalArgumentException("illegal character, character=" + c);
		}
	}

	private void setRoom(int x, int y) {
		if (room != null) {
			throw new IllegalArgumentException("more than one room found");
		}
	
		room = new Coordinate(x, y);
	}

	private void setKitchen(int x, int y) {
		if (kitchen != null) {
			throw new IllegalArgumentException("more than one kitchen found");
		}
	
		kitchen = new Coordinate(x, y);
	}

	private void addHole(int x, int y) {
		holes.add(new Coordinate(x, y));
	}

	private void addBug(int x, int y, int time) {
		if (bugLocations.put(time, new Coordinate(x, y)) != null) {
			throw new IllegalArgumentException("bug at different places same time, time=" + time);
		}
	}

	private void testForMandatoryElements() {
		if (room == null) {
			throw new IllegalArgumentException("no room found");
		}
	
		if (kitchen == null) {
			throw new IllegalArgumentException("no kitchen found");
		}
	
		if (bugLocations.size() < 2) {
			throw new IllegalArgumentException("at least two places for bug needed");
		}
		
		if (bugLocations.keySet().stream()
				.max(Comparator.naturalOrder())
				.get() != bugLocations.size() - 1) {
	
			throw new IllegalArgumentException("gaps in times for bug location");
		}
	}

	private Grid toGrid() {
		Coordinate fRoom = room;
		Coordinate fKitchen = kitchen;

		return new Grid() {

			@Override
			public int getWidth() {
				return width;
			}

			@Override
			public int getHeight() {
				return height;
			}

			@Override
			public Coordinate getRoom() {
				return fRoom;
			}

			@Override
			public Coordinate getKitchen() {
				return fKitchen;
			}

			@Override
			public Set<Coordinate> getHoles() {
				return new HashSet<>(holes);
			}

			@Override
			public Coordinate getBug(int time) {
				return bugLocations.get(time % bugLocations.size());
			}
		};
	}
}
