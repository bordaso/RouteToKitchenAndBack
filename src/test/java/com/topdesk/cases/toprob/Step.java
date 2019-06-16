package com.topdesk.cases.toprob;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Step {
	
	private final Opportunity stepCreatorOpportunity;	
	private final Coordinate myCoordinate;
	
	private Set<Opportunity> opportunitiesSet = new HashSet<>();
	private List<Opportunity> opportunitiesSortedDesc;

	public Step(Opportunity stepCreatorOpportunity) {
		super();
		this.stepCreatorOpportunity=stepCreatorOpportunity;
		this.myCoordinate = stepCreatorOpportunity.getPoint();
	}
	
	public Step(Coordinate initialStepRobiRoom) {
		super();
		this.stepCreatorOpportunity=new Opportunity(initialStepRobiRoom, Instruction.PAUSE);
		this.myCoordinate = initialStepRobiRoom;
	}

	public Set<Opportunity> getOpportunitiesSet() {
		return opportunitiesSet;
	}

	public void setOpportunitiesSet(Set<Opportunity> opportunitiesSet) {
		this.opportunitiesSet = opportunitiesSet;
	}
	
	public void setOpportunitiesStepReference(){
		opportunitiesSet.stream().forEach(o-> o.setStep(this));
	}

	public List<Opportunity> getopportunitiesSortedDesc() {
		opportunitiesSortedDesc = new ArrayList<>(opportunitiesSet);
		Collections.sort(opportunitiesSortedDesc, Opportunity.compByValueDesc);
		return opportunitiesSortedDesc;
	}
	
	public Coordinate getMyCoordinate() {
		return myCoordinate;
	}

	public Opportunity getStepCreatorOpportunity() {
		return stepCreatorOpportunity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((stepCreatorOpportunity == null) ? 0 : stepCreatorOpportunity.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Step other = (Step) obj;
		if (stepCreatorOpportunity == null) {
			if (other.stepCreatorOpportunity != null)
				return false;
		} else if (!stepCreatorOpportunity.equals(other.stepCreatorOpportunity))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Step [stepCreatorOpportunity=" + stepCreatorOpportunity + ", myCoordinate=" + myCoordinate + "]";
	}

	
}
