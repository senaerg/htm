package com.numenta.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


public class Column implements Comparable<Column>{
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	private int xPos;
	private int yPos;
	private double boost = 1.0;// TODO choose reasonable boost
	private double overlap;
	private boolean active;
	public static int MINIMAL_OVERLAP = 4;// TODO choose reasonable overlap
	private double minimalDesiredDutyCycle;
	private ArrayList<Boolean> activeList = new ArrayList<Boolean>();
	private ArrayList<Boolean> timesGreaterOverlapThanMinOverlap = new ArrayList<Boolean>();
	private List<Column> neigbours;
	private Synapse[] potentialSynapses;
	
	/*
	 * A sliding average representing how often column c has been active after
	 * inhibition (e.g. over the last 1000 iterations).
	 */
	private double activeDutyCycle;
	/*
	 * A sliding average representing how often column c has had significant
	 * overlap (i.e. greater than minOverlap) with its inputs (e.g. over the
	 * last 1000 iterations).
	 */
	private double overlapDutyCycle;
	
	//for temoral pooler
	public static int CELLS_PER_COLUMN=3;
	private boolean[] predictiveStatesBefore=new boolean[CELLS_PER_COLUMN];
	private boolean[] predictiveStatesNow=new boolean[CELLS_PER_COLUMN];
//	private boolean[]activeStatesBefore=new boolean[CELLS_PER_COLUMN];
	private boolean[] activeStatesNow=new boolean[CELLS_PER_COLUMN];
	
//	public boolean[] getActiveStatesBefore() {
//		return activeStatesBefore;
//	}
//
//	public void setActiveStatesBefore(boolean[] activeStatesBefore) {
//		this.activeStatesBefore = activeStatesBefore;
//	}

	public boolean[] getActiveStatesNow() {
		return activeStatesNow;
	}

	public void setActiveStatesNow(boolean[] activeStatesNow) {
		this.activeStatesNow = activeStatesNow;
	}

	public boolean[] getPredictiveStatesNow() {
		return predictiveStatesNow;
	}

	public void setPredictiveStatesNow(boolean[] predictiveStatesNow) {
		this.predictiveStatesNow = predictiveStatesNow;
	}

	private Cell[] cells;
	
	//for temoral pooler
	
	
	
	public static int getCELLS_PER_COLUMN() {
		return CELLS_PER_COLUMN;
	}

	public static void setCELLS_PER_COLUMN(int cELLSPERCOLUMN) {
		CELLS_PER_COLUMN = cELLSPERCOLUMN;
	}

	public boolean[] getPredictiveStatesBefore() {
		return predictiveStatesBefore;
	}

	public void setPredictiveStatesBefore(boolean[] predictiveStatesBefore) {
		this.predictiveStatesBefore = predictiveStatesBefore;
	}

	public int getxPos() {
		return xPos;
	}

	public void setxPos(int xPos) {
		this.xPos = xPos;
	}

	public int getyPos() {
		return yPos;
	}

	public void setyPos(int yPos) {
		this.yPos = yPos;
	}
	public void calculateBoost(double minimalDesiredDutyCycle) {
		
		if (this.getActiveDutyCycle() > minimalDesiredDutyCycle) {
			this.boost = 1.0;
		} else {
			this.boost += minimalDesiredDutyCycle;
		}
//		logger.log(Level.INFO, "new calculated boost=" + this.boost);
	}

	public void addGreaterThanMinimalOverlap(boolean greaterThanMinimalOverlap) {

//		logger.log(Level.INFO, "timesGreate"
//				+ timesGreaterOverlapThanMinOverlap.size());
		this.timesGreaterOverlapThanMinOverlap
				.add(0, greaterThanMinimalOverlap);
		if (timesGreaterOverlapThanMinOverlap.size() > 1000) {
			timesGreaterOverlapThanMinOverlap.remove(1000);
		}
	}

	public ArrayList<Boolean> getTimesGreaterOverlapThanMinOverlap() {
		return timesGreaterOverlapThanMinOverlap;
	}

	

	public void addActive(boolean active) {
//		logger.log(Level.INFO, "activeList" + activeList.size());
		activeList.add(0, active);
		if (activeList.size() > 1000) {
			activeList.remove(1000);
		}
	}

	

	public double getOverlapDutyCycle() {
		return overlapDutyCycle;
	}

	public double getActiveDutyCycle() {
		return activeDutyCycle;
	}




	public double getMinimalDesiredDutyCycle() {
		return minimalDesiredDutyCycle;
	}

	public void setMinimalDesiredDutyCycle(double minimalDesiredDutyCycle) {
		this.minimalDesiredDutyCycle = minimalDesiredDutyCycle;
	}


	public double getOverlap() {
		return overlap;
	}

	public void setOverlap(double d) {
		this.overlap = d;
	}

	public Synapse[] getPotentialSynapses() {
		return potentialSynapses;
	}

	public void setPotentialSynapses(Synapse[] potentialSynapses) {
		this.potentialSynapses = potentialSynapses;
	}

	public List<Column> getNeigbours() {
		return neigbours;
	}

	public void setNeigbours(List<Column> neigbours) {
		this.neigbours = neigbours;
	}

	public Synapse[] getConnectedSynapses() {
		ArrayList<Synapse> connectedSynapses = new ArrayList<Synapse>();
		for (int i = 0; i < potentialSynapses.length; i++) {
			Synapse potentialSynapse = potentialSynapses[i];
			if (potentialSynapse.isActive()) {
				connectedSynapses.add(potentialSynapse);
			}
		}
		Object[] objects = connectedSynapses.toArray();
		Synapse[] synapses = new Synapse[objects.length];
		System.arraycopy(objects, 0, synapses, 0, objects.length);
		connectedSynapses=null;
		return synapses;
	}

	public double getBoost() {
		return boost;
	}

	public void setBoost(double boost) {
		this.boost = boost;
	}

	public void increasePermanances(double d) {
		for (int i = 0; i < this.potentialSynapses.length; i++) {
			Synapse potenSynapse = potentialSynapses[i];
			potenSynapse.setPermanance(potenSynapse.getPermanance() + d);
		}

	}

	public Segment getActiveSegment(int cell, int time, String activeState) {
		// TODO Auto-generated method stub
		return null;
	}

	public double updateOverlapDutyCycle() {

		int totalGt = 0;
		for (int i = 0; i < timesGreaterOverlapThanMinOverlap.size(); i++) {
			if (timesGreaterOverlapThanMinOverlap.get(i)) {
				totalGt++;
			}
		}
		this.overlapDutyCycle = (double) totalGt
				/ timesGreaterOverlapThanMinOverlap.size();

		return overlapDutyCycle;

	}

	// TODO updateActiveDutyCycle(c)
	// Computes a moving average of how often column c has been active after
	// inhibition.

	public double updateActiveDutyCycle() {

		int totalActive = 0;
		for (int i = 0; i < activeList.size(); i++) {
			if (activeList.get(i)) {
				totalActive++;
			}
		}
		this.activeDutyCycle = (double) totalActive / activeList.size();

		return activeDutyCycle;
	}

	public ArrayList<Boolean> getActiveList() {
		return activeList;
	}

	public void setActive(ArrayList<Boolean> activeList) {
		this.activeList = activeList;
	}

	public void setActive(boolean active) {
		addActive(active);
		this.active=active;
		
	}

	public boolean isActive() {
		// TODO Auto-generated method stub
		return this.active;
	}

	public int compareTo(Column column) {
		int returnValue=0;
		if(this.getOverlap()>column.getOverlap()){
			returnValue= -1;
		}else{
			if(this.getOverlap()==column.getOverlap()){
				returnValue=0;
			} else{
				if(this.getOverlap()<column.getOverlap()){
					returnValue=1;
				}				
			}
		}
		return returnValue;
	}

	public  Cell[] getCells() {
		
		return this.cells;
	}
}
