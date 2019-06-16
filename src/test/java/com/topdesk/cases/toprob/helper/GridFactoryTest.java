package com.topdesk.cases.toprob.helper;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.topdesk.cases.toprob.Coordinate;
import com.topdesk.cases.toprob.Grid;

@SuppressWarnings("static-method")
public class GridFactoryTest {

	@Test
	public void minimal() {
		Grid grid = GridFactory.create(
				"rk",
				"AB");
		
		assertEquals(2, grid.getWidth());
		assertEquals(2, grid.getHeight());
		
		assertEquals(new Coordinate(0, 0), grid.getRoom());
		
		assertEquals(new Coordinate(1, 0), grid.getKitchen());
		
		assertEquals(0, grid.getHoles().size());
		
		assertEquals(new Coordinate(0, 1), grid.getBug(0));
		assertEquals(new Coordinate(1, 1), grid.getBug(1));
		assertEquals(new Coordinate(0, 1), grid.getBug(2));
		assertEquals(new Coordinate(1, 1), grid.getBug(3));
		assertEquals(new Coordinate(0, 1), grid.getBug(4));
		assertEquals(new Coordinate(1, 1), grid.getBug(5));
	}
	
	@Test
	public void bigger() {
		Grid grid = GridFactory.create(
				"..E..",
				"...or",
				"...oo",
				"..C..",
				".....",
				"..B..",
				".A...",
				"k....",
				"..D..");
		
		assertEquals(5, grid.getWidth());
		assertEquals(9, grid.getHeight());
		
		assertEquals(new Coordinate(4, 1), grid.getRoom());
		
		assertEquals(new Coordinate(0, 7), grid.getKitchen());
		
		Set<Coordinate> holes= new HashSet<>(Arrays.asList(new Coordinate(3, 1), new Coordinate(3, 2), new Coordinate(4, 2)));
		assertEquals(holes, grid.getHoles());
		
		assertEquals(new Coordinate(1, 6), grid.getBug(0));
		assertEquals(new Coordinate(2, 5), grid.getBug(1));
		assertEquals(new Coordinate(2, 3), grid.getBug(2));
		assertEquals(new Coordinate(2, 8), grid.getBug(3));
		assertEquals(new Coordinate(2, 0), grid.getBug(4));
		
		assertEquals(new Coordinate(1, 6), grid.getBug(5));
		assertEquals(new Coordinate(2, 5), grid.getBug(6));
	}
	
	// Exceptions
	
	@Test(expected = NullPointerException.class)
	public void npe() {
		String[] in = null; // inlining this gives an warning in Eclipse
		GridFactory.create(in);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void notRectangular() {
		GridFactory.create(
				"rk",
				"AB",
				".");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void noRoom() {
		GridFactory.create(
				".k",
				"AB",
				"..");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void noKitchen() {
		GridFactory.create(
				"r.",
				"AB",
				"..");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void noBugs() {
		GridFactory.create(
				"rk",
				"..",
				"..");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void bugOnlyOnePlace() {
		GridFactory.create(
				"rk",
				"..",
				".A");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void bugsNotConsecutive() {
		GridFactory.create(
				"rk",
				"AB",
				".D");
	}

	@Test(expected = IllegalArgumentException.class)
	public void illegalCharacter() {
		GridFactory.create(
				"rk",
				" .",
				"AB");
	}
}
