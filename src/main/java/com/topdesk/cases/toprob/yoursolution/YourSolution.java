package com.topdesk.cases.toprob.yoursolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.topdesk.cases.toprob.Coordinate;
import com.topdesk.cases.toprob.Grid;
import com.topdesk.cases.toprob.Instruction;
import com.topdesk.cases.toprob.Opportunity;
import com.topdesk.cases.toprob.Solution;
import com.topdesk.cases.toprob.Step;

// Implemented by BORDASO
// 2019.06.16.
// Budapest, Hungary
public class YourSolution implements Solution {
	
	private int TIME=0;
	private int ORIGINAL_TIME=0;
	
	private Grid grid;
	private int widthX;
	private int heightY;	
	
	private int optimalRouteValue;
	
	private Coordinate robiRoom;
	private Coordinate kitchen;
	private Coordinate robi;
	
	private List<Step> steps = new ArrayList<>();
	private List<Opportunity> allOpportunities = new ArrayList<>();
	
	
	private Set<Coordinate> holes;
	private List<Instruction> followMe = new ArrayList<>();
	private List<Instruction> route= new ArrayList<>();
	private Map<List<Instruction>, Integer> routesWithValues = new HashMap<>();
	private boolean routeOptimizer;

	@Override
	public List<Instruction> solve(Grid grid, int time) {
		if(time<0) {
			throw new IllegalArgumentException();
		}
		return init(grid, time);
	}

	private List<Instruction> init(Grid grid, int time) {	
		
		this.ORIGINAL_TIME=time;
		this.TIME=time;		
		this.grid = grid;
		this.widthX = grid.getWidth();
		this.heightY = grid.getHeight();
		this.robi = grid.getRoom();
		this.robiRoom = grid.getRoom();
		this.kitchen = grid.getKitchen();
		this.holes = grid.getHoles();
		
		steps.clear();
		route.clear();
		
		optimalRouteValueCalculator();
		
		steps.add(new Step(robiRoom));
		
		// Recursively valuate the opportunities from the current step, chooses the best and creates a new step
		routePlanner();
		
		// Switch the preferred route direction, so the valuation will be different and might lead to a better/shorter solution
		routeOptimizer1();
		
		// Ran out of time to implement, but another optimization could be to find steps which not immediately
		//following each other BUT actually neighbours, this way shorten the length of the route
	//	routeOptimizer2();
		
		mirrorTheRouteToGoBack(route);
		

	return	route;
	}


	private List<Instruction>  routePlanner() {
				
		Step nextStep = opportunityValuation(routeOptimizer);		
		steps.add(nextStep);
		robi = nextStep.getMyCoordinate();
		
		// Set the route
		calculateRouteInstructionAndBugCheckAndTimeSet();
		
		//Calculate till the robi reach the kitchen
		while(!robi.equals(kitchen)) {
			routePlanner();
		}
		
		return route;
	}
	
private void routeOptimizer1() {
		
		routesWithValues.put(route, route.size());
		
		if(route.size()>optimalRouteValue) {			
			routeOptimizer=true;
			route= new ArrayList<>();
			steps = new ArrayList<>();
			robi = robiRoom;
			TIME = ORIGINAL_TIME;
			steps.add(new Step(robiRoom));
			routePlanner();
			routesWithValues.put(route, route.size());
		}
		
		if(!routesWithValues.isEmpty()) {
		route =	routesWithValues.entrySet().stream()
			.collect(Collectors.maxBy((o1, o2) -> o2.getValue()-o1.getValue()))
			.get()
			.getKey();
		}
		
	}

	private void calculateRouteInstructionAndBugCheckAndTimeSet() {		
		if(grid.getBug(TIME+1).equals(robi)) {
			route.add(Instruction.PAUSE);
			increaseTIME();
		}
		route.add(steps.get(steps.size()-1).getStepCreatorOpportunity().getInstruction());
		increaseTIME();
	}
	
	
	
	private Step opportunityValuation(boolean startFromAnotherDirection) {
		
		//Create a list from all available moving options
		List<Instruction> instructionList = Arrays.asList(Instruction.values());
		
		//Create a List with all Opportunities from the current (==robi) coordinate and Valuate each Opportunity
		List<Opportunity> instructionToOpportunityList = instructionList.stream()
				.filter(i-> !i.equals(Instruction.PAUSE))
				.map(i-> new Opportunity(i.execute(robi), i))
				.peek(o-> {opportunityOperation(o, startFromAnotherDirection);})
				.collect(Collectors.toList());
		
		//Set the latest step FROM where we valuate the opportunities, with the valuated opportunites
		steps.get(steps.size()-1).getOpportunitiesSet().clear();
		steps.get(steps.size()-1).getOpportunitiesSet().addAll(instructionToOpportunityList);
		steps.get(steps.size()-1).setOpportunitiesStepReference();
		
		// Stepback check and best opportunity calculation for new Step creation
		Optional<Opportunity> bestOpportunity = haveIVisitedThisCoordinateThenGetBestOpp(instructionToOpportunityList);			
		
		Step nextStep = new Step(bestOpportunity.get()); 
		
		return nextStep; //best place to debug
	}

	// Valutation logic
	private void opportunityOperation(Opportunity o, boolean startFromAnotherDirection) {		
		
		if(wouldRobiFallInAHole(o.getPoint()) || wouldRobiFallofTheGrid(o.getPoint())){
		o.setValue(-99);
		return;
		}
		
		calculateGeneralDirection(startFromAnotherDirection);
		
		// The LATEST STEP direction where we came from, -> valid and BAD and PREVIOUS direction where we can go is set to -2
		if(o.equals(steps.get(steps.size()-1).getStepCreatorOpportunity())) {
			o.setValue(-2);
			return;
		}

		// All valid and BAD direction where we MIGHT go is set to -1
		if(!followMe.contains(o.getInstruction())) {
			o.setValue(-1);
			return;
		}	
		
		// The first valid and GOOD direction where we WILL go is set to 1, the second which we might choose otherwise will set to 0	
		if(followMe.contains(o.getInstruction())  && (followMe.size()==1 || followMe.get(1)==o.getInstruction())) {
			o.setValue(1);
			return;
		}
		
		o.setValue(0);
	}
	
	private Optional<Opportunity> haveIVisitedThisCoordinateThenGetBestOpp(List<Opportunity> valuatedOpportunityList) {

		//See all opportunities that has created all previous steps
		allOpportunities=steps.stream().map(s-> s.getStepCreatorOpportunity()).collect(Collectors.toList());
		
		//Never step FORWARD to a coordinate which has been already visited, it can be revisited BACKWARD
		return stepBackCalculation(valuatedOpportunityList);	
	}
	
	// Stepback logic1 -> to only where we came from
	private Optional<Opportunity> stepBackCalculation(List<Opportunity> valuatedOpportunityList) {
		
		Optional<Opportunity> bestOpportunityFiltered =  valuatedOpportunityList.stream()	
		.filter(o-> !allOpportunities.contains(o))
		.filter(o-> o.getValue()!=-99)
		.collect(Collectors.maxBy((o1, o2) -> o1.getValue()-o2.getValue()));
		
		
		if(!bestOpportunityFiltered.isPresent()) {	
		return prepareStepBack();
		}
		
		return 	bestOpportunityFiltered;		
	}
	
	// Stepback logic2
	private Optional<Opportunity> prepareStepBack() {
		Step previousStep = steps.get(steps.size()-2);
		previousStep.getopportunitiesSortedDesc().get(0).setValue(-99);
		steps.remove(steps.size()-1);
		route.remove(route.size()-1);
		
		if(route.get(route.size()-1)==Instruction.PAUSE) {
			route.remove(route.size()-1);
			decreaseTIME();
		}
		decreaseTIME();
		
		robi=previousStep.getMyCoordinate();
		
		return Optional.of(previousStep.getopportunitiesSortedDesc().get(0));
	}
	
	//Setup the general/best direction to follow at each Step to reach the kitchen
	private List<Instruction> calculateGeneralDirection(boolean reverseListItem) {
		int valX = kitchen.getX() - robi.getX();
		int valY = kitchen.getY() - robi.getY();
		
		followMe.clear();
		
		if (valX < 0) {
			followMe.add(Instruction.WEST);
		}
		if (valX > 0) {
			followMe.add(Instruction.EAST);
		}
		if (valY < 0) {
			followMe.add(Instruction.NORTH);
		}
		if (valY > 0) {
			followMe.add(Instruction.SOUTH);
		}
		
		int num=followMe.size()-1;					
		followMe = reverseListItem?  IntStream.rangeClosed(0, num)
			.mapToObj(i->followMe.get(num-i))
			.collect(()->new ArrayList<Instruction>(), (ac, e)->{ac.add(e);}, (ac, ac2)->{ac.addAll(ac2);}):followMe;

		return followMe;
	}

	private boolean wouldRobiFallInAHole(Coordinate modifiedCoordinate) {
		return holes.contains(modifiedCoordinate);
	}

	private boolean wouldRobiFallofTheGrid(Coordinate modifiedCoordinate) {
		return (modifiedCoordinate.getX() > widthX || modifiedCoordinate.getY() > heightY) 
				|| 
				(modifiedCoordinate.getX() < 0 || modifiedCoordinate.getY() < 0);
	}
	
	private void mirrorTheRouteToGoBack(List<Instruction> route) {
		
		final List<Instruction> mirrorRoute= new ArrayList<>();
		
		mirrorRoute.add(Instruction.PAUSE);
		increaseTIME();
		mirrorRoute.add(Instruction.PAUSE);
		increaseTIME();
		mirrorRoute.add(Instruction.PAUSE);
		increaseTIME();
		mirrorRoute.add(Instruction.PAUSE);
		increaseTIME();
		mirrorRoute.add(Instruction.PAUSE);
		increaseTIME();
		
		int num=route.size()-1;		
		IntStream.rangeClosed(0, num)
			.mapToObj(i->route.get(num-i))
			.collect(()->mirrorRoute, (ac, e)->{mirrorCalculator(ac, e);}, (ac, ac2)->{ac.addAll(ac2);});
		
		route.addAll(mirrorRoute);	
	}
	

	private void mirrorCalculator(List<Instruction> ac, Instruction e) {
		
		if(e==Instruction.PAUSE) {
			return;
		}		
		
		switch(e) {
		case NORTH:
			e=Instruction.SOUTH;
			break;
		case SOUTH:
			e=Instruction.NORTH;
			break;
		case WEST:
			e=Instruction.EAST;
			break;
		case EAST:
			e=Instruction.WEST;
			break;
		}
		
		robi=e.execute(robi);
		
		if(grid.getBug(TIME+1).equals(robi)) {
			ac.add(Instruction.PAUSE);
			increaseTIME();
		}
		
		ac.add(e);
		increaseTIME();		
	}
	
	private void routeOptimizer2() {		
		routeShortCutter();
		}

	private void routeShortCutter() {
		
		for(int a =0;a<steps.size();a++) {
			for(int z =steps.size();z>0;z--) {
				areYouNeighbour(steps.get(a), steps.get(z));
			}
		}				
	}	
	
	private void areYouNeighbour(Step earlyStep, Step laterStep) {
		
		List<Instruction> instructionList = Arrays.asList(Instruction.values());

		instructionList.stream()
		.filter(i-> i.execute(laterStep.getMyCoordinate()).equals(earlyStep.getMyCoordinate()))
		.peek(e -> {neighbourValueCalculator(earlyStep, laterStep);})
		.count();		
	}	
	
	private void neighbourValueCalculator(Step earlyStep, Step laterStep) {
		
	    List<Step> stepPairs= new ArrayList<>();
		Map<List<Step>, Integer> stepPairsMap = new HashMap<>();	
		
		int value = steps.indexOf(laterStep)-steps.indexOf(earlyStep);
		
	   stepPairs.add(earlyStep); 
	   stepPairs.add(laterStep);
	   stepPairsMap.put(stepPairs, value);		
	}
	
	private void optimalRouteValueCalculator() {
		int xCalc = kitchen.getX()-robiRoom.getX();
		int yCalc = kitchen.getY()-robiRoom.getY();		
		optimalRouteValue=(Math.abs(xCalc))+(Math.abs(yCalc));
	}
	
	private void increaseTIME() {
	TIME++;
	}
	
	private void decreaseTIME() {
	TIME--;
	}
}
