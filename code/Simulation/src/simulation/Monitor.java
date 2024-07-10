package simulation;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import sim.engine.SimState;
import sim.engine.Steppable;

public class Monitor implements Steppable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5218563177654495284L;

	private Patrols world;
	private int[][] schedule;

	private boolean killSwitch = false;
	private boolean blitz = false;
	
	private int intervalLength;
	private int minuteLength;
	private int intervalDuration;
	private int intervalCount;
	private int shiftDuration;
	
	private int em_time;
	
	private int scheduleCounter = 0;
	
	
	
	
	public Monitor(Patrols state, int intervalC, int intervalL, int patrols) {
		world = state;

		killSwitch = false;
		blitz = false;
		scheduleCounter = 0;
		
		minuteLength = 60;
		
		

		intervalLength = intervalL;
		intervalCount = intervalC;
		
		intervalDuration = intervalL * minuteLength;
		
		shiftDuration = intervalDuration * intervalCount;
//		shiftDuration = 5000;
		
	}
	
	public Monitor(Patrols state, int intervalC, int intervalL, int patrols, int[][] sched) {
		world = state;

		killSwitch = false;
		blitz = false;
		scheduleCounter = 0;
		
		minuteLength = 60;
		
		this.schedule = sched;
		
		
		
		

		intervalLength = intervalL;
		intervalCount = intervalC;
		
		intervalDuration = intervalL * minuteLength;
		
		shiftDuration = intervalDuration * intervalCount;
//		shiftDuration = 5000;
		
		Random rando = new Random();
		em_time = rando.nextInt(shiftDuration-2000) + 1000;
		
	}
	
	
	
	
	public void setIntervalLength(int length) {
		intervalLength = length;
		intervalCount = shiftDuration / intervalLength;
	}

	@Override
	public void step(SimState state) {
		// TODO Auto-generated method stub
		
		if(state.schedule.getSteps() == world.shiftDuration) {
			getData();
			state.kill();
		}

		if(killSwitch) {
			state.kill();
		}
		
//		if(state.schedule.getSteps() == 5000) {
//			getData();
//			state.kill();
//		}
		
		
		int timing = (int) world.schedule.getSteps()/this.intervalDuration;
		
		for(int i=0; i<world.agentList.size(); i++) {
			world.agentList.get(i).monitorOrders(timing);
		}
		
//		if((state.schedule.getSteps() > 0 && state.schedule.getSteps() < world.shiftDuration) && state.schedule.getSteps() % 2400 == 0) {
		if(state.schedule.getSteps() == em_time) {
			
			int timestamp = (int) state.schedule.getSteps();
			/*
			 * Baker Hall			2296
			 * Carabao Park Steps	1042
			 * CEAT junction		3763
			 * Mariang Banga 		2835
			 * Milka Krem 			1694
			 * Oblation Park		2070
			 * Raymundo Gate		4234
			*/
			
			int targetLoc = 3763;
			String locName = getTargetLocationName(targetLoc);
			
			double closest = Double.MAX_VALUE;
			int candidate = -1;
			for(int i=0; i<world.agentList.size(); i++) {
				double a1 = world.agentList.get(i).distanceCalculation(targetLoc);
				if(a1 < closest) {
					closest = a1;
					candidate = i;
				}
			}
			String patName = world.agentList.get(candidate).getName();
			System.out.println(Integer.toString(timestamp)+": Call patrol "+patName+" to ["+locName+"]");

			if(candidate >= 0) world.agentList.get(candidate).respondToCall(targetLoc);
			
//			world.agentList.get(0).respondToCall(loc_RaymundoGate);
//			world.agentList.get(1).respondToCall(loc_RaymundoGate);
//			world.agentList.get(2).respondToCall(loc_RaymundoGate);
//			world.agentList.get(3).respondToCall(loc_RaymundoGate);
//			world.agentList.get(4).respondToCall(loc_RaymundoGate);
		}
		stepMonitor((int)state.schedule.getSteps());
	}
	
	public void stepMonitor(int input) {
    	
    	int stepCheck = input % intervalDuration;
//    	int schedCounter = input / intervalDuration;
    	if(stepCheck == 0) {
    		System.out.println(Integer.toString(input)+" of "+Double.toString(shiftDuration)+" steps reached.");
		}
    }
	
	public boolean getMoveCommand() {
		
		return true;
	} 
	
	public void killSwitch() {
		this.killSwitch = true;
	}
	
	public int getScheduleTiming() {
		return (int) world.schedule.getSteps()/intervalLength;
	}
	
	public void getData() {
		ArrayList<Patrol> al = new ArrayList<Patrol>(world.agentList);
		
		DecimalFormat df = new DecimalFormat("#.####");
		df.setRoundingMode(RoundingMode.CEILING);
		
		for(int i=0; i<al.size(); i++) {
			int m = al.get(i).stepsMoving;
			int t = al.get(i).stepsMoving + al.get(i).stepsIdle;
			int r = al.get(i).stepsResponding;
			//int r = al.get(i).stepsUrgent;
			
			//double q = (((double) m)/t)*100;
			
			String s_m = Integer.toString(m);
			String s_t = Integer.toString(t);
			String s_r = Integer.toString(r);
			
			String name = al.get(i).getName();
			int r_count = al.get(i).response_count;
//			System.out.println(name+": "+s_m+"/"+s_t+" ("+df.format(q)+")");
			System.out.println(name+": responded "+Integer.toString(r_count)+" times. "+s_r);
			
		}
		
	}

	private String getTargetLocationName(int input) {
		/*
		 * Baker Hall			2296
		 * Carabao Park Steps	1042
		 * CEAT junction		3763
		 * Mariang Banga 		2835
		 * Milka Krem 			1694
		 * Oblation Park		2070
		 * Raymundo Gate		4234
		*/
		switch(input) {
			case 2296:	return "Baker Hall";
			case 1042: 	return "Carabao Park Steps";
			case 3763:	return "CEAT Junction";
			case 2835:	return "Mariang Banga";
			case 1694:	return "Milka Krem";
			case 2070:	return "Oblation Park";
			case 4234:	return "Raymundo Gate";
			default:	return "no location match";
		}
	}
}
