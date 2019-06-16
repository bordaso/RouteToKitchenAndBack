package com.topdesk.cases.toprob;

import java.util.Comparator;

public class Opportunity {
	
	private final Coordinate point;
	private final Instruction instruction;
	
	private Step step;
	private int value;
	public static final Comparator<Opportunity> compByValueDesc = (o1, o2) -> o2.getValue()-o1.getValue();
	
	public Opportunity(Coordinate point, Instruction instruction) {
		super();
		this.point = point;
		this.instruction = instruction;
	}
	
	public Instruction getInstruction() {
		return instruction;
	}

	public Coordinate getPoint() {
		return point;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public Step getStep() {
		return step;
	}

	public void setStep(Step step) {
		this.step = step;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((point == null) ? 0 : point.hashCode());
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
		Opportunity other = (Opportunity) obj;
		if (point == null) {
			if (other.point != null)
				return false;
		} else if (!point.equals(other.point))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Opportunity [point=" + point + ", value=" + value + "]";
	}
	

	
}
