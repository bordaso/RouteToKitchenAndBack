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
	
	private Coordinate buggy;
	
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
		
		
		//&& routesWithValues.size()<2
//		routesWithValues.put(route, route.size());
//		
//		if(route.size()>optimalRouteValue && routesWithValues.size()<2) {			
//			routeOptimizer=true;
//			route= new ArrayList<>();
//			steps = new ArrayList<>();
//			robi = robiRoom;
//			TIME = ORIGINAL_TIME;
//			steps.add(new Step(robiRoom));
//			routePlanner();
//		}
//		
//		if(!routesWithValues.isEmpty()) {
//		route =	routesWithValues.entrySet().stream()
//			.collect(Collectors.maxBy((o1, o2) -> o2.getValue()-o1.getValue()))
//			.get()
//			.getKey();
//		}
		
		routePlanner();
		
		mirrorTheRouteToGoBack(route);
		

	return	route;

	}

	// REPEAT HERE AS A RECURSIVE METHOD
	private List<Instruction>  routePlanner() {
		
		// ITT MÁR TUDNOM KELL HOVA LÉPEK A LEGJOBBNAK KELL ITT LENNIE!!!!!!!!!!
		Step nextStep = opportunityValuation(routeOptimizer);
		
		steps.add(nextStep);
		
		//ITT LÉPEK
		robi = nextStep.getMyCoordinate();
		
		// ITT ÁLLÍTOM A ROUTOT
		calculateRouteInstructionAndBugCheckAndTimeSet();
		
		
		
		// addig számol amig a konyhába nem érünk EGYENLŐRE
		while(!robi.equals(kitchen)) {
			routePlanner();
		}
		
	///	routeShortCutter(); <<<<<<<<<<<?????????????????
		

		
		return route;
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
		
		//Create a List whith all Opportunities from the current (==robi) coordinate and Valuate each Opportunity
		List<Opportunity> instructionToOpportunityList = instructionList.stream()
				.filter(i-> !i.equals(Instruction.PAUSE))
				.map(i-> new Opportunity(i.execute(robi), i))
				.peek(o-> {opportunityOperation(o, startFromAnotherDirection);})
				.collect(Collectors.toList());
		
		steps.get(steps.size()-1).getOpportunitiesSet().clear();
		steps.get(steps.size()-1).getOpportunitiesSet().addAll(instructionToOpportunityList);
		steps.get(steps.size()-1).setOpportunitiesStepReference();
		
		Optional<Opportunity> bestOpportunity = haveIVisitedThisCoordinateThenGetBestOpp(instructionToOpportunityList);			
		
		Step nextStep = new Step(bestOpportunity.get()); 
		
		return nextStep; //SET BREAKPOINT HERE TO TEST<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	}

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

		allOpportunities=steps.stream().map(s-> s.getStepCreatorOpportunity()).collect(Collectors.toList());
		
		//Never step FORWARD to a coordinate which has been already visited, it can be revisited BACKWARD
		return stepBackCalculation(valuatedOpportunityList);	
	}
	
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
		
		//System.out.println("PRE-ORDER "+followMe);		
		followMe = reverseListItem? followMe.stream().collect(()-> new LinkedList<Instruction>(), (ac, e)->{ac.offerFirst(e);}, (ac, e)->{ac.addAll(e);}):followMe;
	//	System.out.println("POST-ORDER "+followMe);

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
	
	private Coordinate instructionToCoordinate(Instruction inputInstruction) {
		switch(inputInstruction) {
		case NORTH:
			return decreaseYgoNorth(robi);
		case SOUTH:
			return increaseYgoSouth(robi);
		case WEST:
			return decreaseXgoWest(robi);
		case EAST:
			return increaseXgoEast(robi);
		default:
			return robi;
		}
	}
	
	private Coordinate increaseXgoEast(Coordinate inputCoordinate) {
		return new Coordinate(inputCoordinate.getX() + 1, inputCoordinate.getY());
	}

	private Coordinate decreaseXgoWest(Coordinate inputCoordinate) {
		return new Coordinate(inputCoordinate.getX() - 1, inputCoordinate.getY());
	}

	private Coordinate increaseYgoSouth(Coordinate inputCoordinate) {
		return new Coordinate(inputCoordinate.getX(), inputCoordinate.getY() + 1);
	}

	private Coordinate decreaseYgoNorth(Coordinate inputCoordinate) {
		return new Coordinate(inputCoordinate.getX(), inputCoordinate.getY() - 1);
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
			.collect(()->mirrorRoute, (ac, e)->{mirrorCalculator(ac, e);}, (ac, e)->{ac.addAll(e);});
		
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
