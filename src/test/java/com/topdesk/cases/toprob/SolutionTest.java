package com.topdesk.cases.toprob;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.topdesk.cases.toprob.helper.GridFactory;
import com.topdesk.cases.toprob.yoursolution.YourSolution;

public class SolutionTest {

	private static final int MAX_TIME = 100;

	private Solution solution;

	@Before
	public void before() {
		this.solution = new YourSolution();
	}

	@Test
	public void minimum_grid() {
		Grid grid = GridFactory.create(
				"rk",
				"AB");

		test(grid, 0);
		test(grid, 1);
	}

	@Test
	public void small_grid() {
		Grid grid = GridFactory.create(
				".rBA.",
				"...o.",
				".ook.",
				"..o..");

		test(grid, 0);
		test(grid, 1);
	}

	@Test
	public void small_grid_bug_everywhere() {
		Grid grid = GridFactory.create(
				"NrBAM",
				"ECDoL",
				"FookK",
				"GHoIJ");

		test(grid, 0);
		test(grid, 1);
	}

	@Test
	public void big_grid() {
		Grid grid = GridFactory.create(
				".rBA......",
				"...o......",
				"......oko.",
				"..o.......",
				"..........",
				"..........",
				"..........",
				"..........",
				"..........",
				"..........");

		test(grid, 0);
		test(grid, 1);
	}

	@Test
	public void big_grid_bug_everywhere() {
		Grid grid = GridFactory.create(
				".rBA......",
				"...oW.X.Y.",
				"...U.ookN.",
				".Do....J..",
				".C......Z.",
				".E...I....",
				".F........",
				".V.GH...K.",
				".......LM.",
				"...TSRQPO.");

		test(grid, 0);
		test(grid, 1);
	}

	@Test
	public void annoying_bug() {
		Grid grid = GridFactory.create(
				"rBDFHJLNk",
				".ACEGIKMO");

		test(grid, 0);
		test(grid, 1);
	}

	@Test
	public void annoying_bug_high_grid() {
		String[] lines = new String[1000];
		lines[0] = "rBDFHJLNk";
		lines[1] = ".ACEGIKMO";
		for (int i = 2; i < lines.length; i++) {
			lines[i] = i % 2 == 0 ? ".o.o.o.o." : ".........";
		}

		Grid grid = GridFactory.create(lines);

		test(grid, 0);
		test(grid, 1);
	}

	@Test
	public void randomish_bug() {
		int width = 2;
		int height = 100;
		Coordinate room = new Coordinate(0, 0);
		Coordinate kitchen = new Coordinate(1, 10);

		Random random = new Random(314159265);
		List<Coordinate> bugCells = new ArrayList<>();
		for (int i = 0; i < MAX_TIME; i++) {
			Coordinate coordinate = null;
			boolean valid = false;
			while (!valid) {
				coordinate = new Coordinate(random.nextInt(width), random.nextInt(kitchen.getY() + 2));
				valid = !coordinate.equals(room) && !coordinate.equals(kitchen) && 
						((i == 0) || !coordinate.equals(bugCells.get(i - 1)));
			}

			bugCells.add(coordinate);
		}

		Grid grid = new Grid() {

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
				return room;
			}

			@Override
			public Coordinate getKitchen() {
				return kitchen;
			}

			@Override
			public Set<Coordinate> getHoles() {
				return Collections.emptySet();
			}

			@Override
			public Coordinate getBug(int time) {
				return bugCells.get(time % MAX_TIME);
			}
		};

		test(grid, 0);
		test(grid, 1);
	}

	// Exceptions

	@Test(expected = NullPointerException.class)
	public void npe_grid_null() {
		solution.solve(null, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void iae_negative_time() {
		Grid grid = GridFactory.create(
				"rk",
				"AB");

		solution.solve(grid, -1);
	}

	// Helper stuff below

	private void test(Grid grid, int startTime) {
		List<Instruction> instructions = solution.solve(grid, startTime);
		assertTrue("solution takes to long", instructions.size() <= MAX_TIME);
		runSolution(grid, startTime, instructions);
	}

	private static void runSolution(Grid grid, int startTime, List<Instruction> instructions) {
		Coordinate rob = grid.getRoom();
		int time = startTime;
		int kitchenTime = 0;
		boolean atLeastOnceEnoughKitchenTime = false;

		for (Iterator<Instruction> instructionIt = instructions.iterator(); instructionIt.hasNext();) {
			time++;

			Instruction nextInstruction = instructionIt.next();
			rob = nextInstruction.execute(rob);

			if (rob.equals(grid.getKitchen()) && nextInstruction.equals(Instruction.PAUSE)) {
				kitchenTime++;
				atLeastOnceEnoughKitchenTime = atLeastOnceEnoughKitchenTime || (kitchenTime == 5);
			} else {
				checkValidPostion(grid, time, rob);
				kitchenTime = 0;
			}
		}

		assertTrue("rob did not spend enough time in the kitchen", atLeastOnceEnoughKitchenTime);
		assertTrue("rob did not make it back to his room", rob.equals(grid.getRoom()));
	}

	private static void checkValidPostion(Grid grid, int time, Coordinate rob) {
		assertTrue("rob fell off the grid, time=" + time, onGrid(grid.getWidth(), grid.getHeight(), rob));
		assertFalse("rob bummed into a bug, time=" + time, rob.equals(grid.getBug(time)));
		assertFalse("rob fell into a hole, time=" + time, grid.getHoles().stream().anyMatch(hole -> rob.equals(hole)));
	}

	private static boolean onGrid(int width, int height, Coordinate rob) {
		return rob.getX() >= 0 && rob.getY() >= 0 && rob.getX() < width && rob.getY() < height;
	}
}
