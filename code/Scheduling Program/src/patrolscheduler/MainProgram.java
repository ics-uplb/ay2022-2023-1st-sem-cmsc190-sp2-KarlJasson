package patrolscheduler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;

public class MainProgram {
	
	static final int MINUTES_PER_DAY = 1440;

	private int[][] distance;
	private LocalDate targetDate = LocalDate.now();
	private DayOfWeek dateNOW = targetDate.getDayOfWeek();
	
	private int patrolCount;
	int[] numOfPatrols = new int[3];
	int[] counter = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	
	int intervalCount = 0;
	int intervalLength = 10;
	
	ArrayList<CampusPatch> patches = new ArrayList<CampusPatch>();
	
	ArrayList<UPFAgent> patrols = new ArrayList<UPFAgent>();
	
	
	List<Pair<CampusPatch, Double>> zone1;
	List<Pair<CampusPatch, Double>> zone2;
	List<Pair<CampusPatch, Double>> zone3;
	
	int[][] schedule;
	int[][] savedFlags;
	int[][] patrolAssignments;

	int[] preset_weights;
	
	int[] visitCount;
	
	//// CONSTRUCTOR METHOD ////
	public MainProgram() {
		targetDate = LocalDate.now();
		dateNOW = targetDate.getDayOfWeek();
		
		/*  TRiAL
		preset_weights = new int[]{
				40,	35,	30,	25,	20,	15,	10,	5,	
				30, 25, 20, 15, 10, 5,
				20, 15, 10, 5
				};
		/////////////*/
		
		/*  TRiAL ////////////
				preset_weights = new int[]{
						10,10,10,10,10,10,10,10,	
						10,10,10,10,10,10,
						10,10,10,10
						};
		/////////////*/
		
//		/*  FINAL ////////////
		preset_weights = new int[]{
				15,	20,	15,	10,	20,	10,	5,	5,	
				25,20,10,20,15,5,
				10,10,45,35
				};
		/////////////*/
		
		generateMap();
		setDistances();
	}

	
	//// GENERAL METHODS ////	
	public void setParameters(int intC, int intL, int z1, int z2, int z3) {
		this.intervalCount = intC;
		this.intervalLength = intL;
		
		numOfPatrols[0] = z1;
		numOfPatrols[1] = z2;
		numOfPatrols[2] = z3;
		
		patrolCount = z1+z2+z3;
		
		newPatrols(numOfPatrols);
//		getPatrolInfo();
		loadInitialFrequencies();
//		loadFromFile();
		
		schedule = new int[intervalCount][patches.size()];
		savedFlags = new int[intervalCount][patches.size()];
		patrolAssignments = new int[intervalCount][patches.size()];
		visitCount = new int[patches.size()];
		
		for(int i=0; i<patrolAssignments.length; i++) {
			for(int j=0; j<patrolAssignments[i].length; j++) {
				patrolAssignments[i][j] = Integer.MIN_VALUE;
			}
		}
	}

	public String dateAsString() {
		String[] month = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
		String[] week = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"};
		String date = Integer.toString(targetDate.getDayOfMonth());
		String year = Integer.toString(targetDate.getYear());

		return week[targetDate.getDayOfWeek().getValue()-1]+", "+month[targetDate.getMonthValue()-1]+" "+date+" "+year;
	}
	
	public void changeDateForward(int input) { 
		long a = (long) input;
		targetDate = targetDate.plusDays(a);
	}
	
	public void changeDateBack(int input) {
		long a = (long) input;
		targetDate = targetDate.minusDays(a);
	}
	
	//// MAP/NETWORK METHODS ////
	public void generateMap() {
		patches.add(new CampusPatch(0, "Carabao Park",1)); 
		patches.add(new CampusPatch(1, "CEM/Raymundo Gate",1)); 
		patches.add(new CampusPatch(2, "BioSci",1)); 
		patches.add(new CampusPatch(3, "Main Library",1)); 
		patches.add(new CampusPatch(4, "Oblation Park",1)); 
		patches.add(new CampusPatch(5, "Palma Bridge",1)); 
		patches.add(new CampusPatch(6, "Silangan Rd/CSI",1)); 
		patches.add(new CampusPatch(7, "Math Building",1)); 

		patches.add(new CampusPatch(8, "DL Umali Hall",2)); 
		patches.add(new CampusPatch(9, "Student Union Building",2)); 
		patches.add(new CampusPatch(10, "Baker Hall",2)); 
		patches.add(new CampusPatch(11, "Animal Science Area",2)); 
		patches.add(new CampusPatch(12, "CEAT",2)); 
		patches.add(new CampusPatch(13, "YMCA",2)); 

		patches.add(new CampusPatch(14, "Univ. Health Service",3));
		patches.add(new CampusPatch(15, "Splash Properties",3));
		patches.add(new CampusPatch(16, "CFNR",3));
		patches.add(new CampusPatch(17, "Forestry Dorms",3)); 
	}

	public void setDistances() {
		int x = patches.size();
		distance = new int[x][x];
		
		distance[0] = new int[] {0, 240, 350, 500, 260, 350, 260, 400, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		distance[1] = new int[] {240, 0, 220, 450, 300, 400, 400, 550, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		distance[2] = new int[] {350, 220, 0, 350, 120, 250, 400, 550, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		distance[3] = new int[] {500, 450, 350, 0, 220, 270, 550, 700, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		distance[4] = new int[] {260, 300, 120, 220, 0, 150, 300, 450, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		distance[5] = new int[] {350, 400, 250, 270, 150, 0, 400, 450, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		distance[6] = new int[] {260, 400, 400, 550, 300, 400, 0, 160, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		distance[7] = new int[] {400, 550, 550, 700, 450, 450, 160, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		
		distance[8] = new int[]  {0, 0, 0, 0, 0, 0, 0, 0, 0, 170, 350, 600, 750, 400, 0, 0, 0, 0};
		distance[9] = new int[]  {0, 0, 0, 0, 0, 0, 0, 0, 170, 0, 220, 450, 650, 400, 0, 0, 0, 0};
		distance[10] = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 350, 220, 0, 230, 400, 190, 0, 0, 0, 0};
		distance[11] = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 600, 450, 230, 0, 190, 400, 0, 0, 0, 0};
		distance[12] = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 750, 650, 400, 190, 0, 600, 0, 0, 0, 0};
		distance[13] = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 400, 400, 190, 400, 600, 0, 0, 0, 0, 0};
		
		distance[14] = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 400, 1000, 1300};
		distance[15] = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 400, 0, 650, 950};
		distance[16] = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1000, 650, 0, 300};
		distance[17] = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1300, 950, 300, 0};
		
	}
	
	
	//// PROBABILITY/FREQUENCY METHODS ////
	public void loadInitialFrequencies() {
		updateFrequencies(preset_weights);
	}
	
	public void loadFromFile() {
	}
	
	public void savePresetToFile() {
	}
	
	public void updateFrequencies(int[] f1) {
		if(patches.size() != f1.length ) {
			System.out.println("ERROR: sizes mismatch ("+Integer.toString(patches.size())+" and "+Integer.toString(f1.length)+")");
			
		}
		for(int i=0; i<patches.size(); i++) {
			patches.get(i).setWeight(f1[i]);
		}
//		viewFrequencies();
	}
	
	public void viewVisitCount() {
		for(int i=0; i<counter.length; i++) {
			System.out.println(patches.get(i).getName()+": "+Integer.toString(counter[i]));
		}
	}
	
	
	//// PATROL METHODS ////
	public void newPatrols(int[] numOfPatrols) {
		int x = numOfPatrols[0];
		int y = numOfPatrols[0]+numOfPatrols[1];
		int z = numOfPatrols[0]+numOfPatrols[1]+numOfPatrols[2];
		
//		String ace = Integer.toString(x) + Integer.toString(y) +Integer.toString(z);
//		System.out.println("ZonePatrols:"+ace);
		
		for(int i=0; i<patrolCount; i++) {
			UPFAgent newAgent = new UPFAgent(i);
			if(i < x) {
				newAgent.setAssignedZone(1);
			}else if(i>=x && i<y) {
				newAgent.setAssignedZone(2);
			}else if(i>=y && i<z){
				newAgent.setAssignedZone(3);
			}else {
				System.out.print("An error has been encountered. Program Terminated");
				System.exit(0);
			}
			patrols.add(newAgent);
		}
	}
	
	
	//// SCHEDULE METHODS ////
	public void addRestrictions(int selected, int[] flags) {
		savedFlags[selected] = Arrays.copyOf(flags, flags.length);
	}
	
	public void editInterval(int selected, int[] flags) {
		schedule[selected] = scheduleInterval(flags);
	}
	
	public void generateSchedule() {
		for(int i=0; i<schedule.length; i++) {
			schedule[i] = scheduleInterval(savedFlags[i]);
		}
	}
	
	public int[] scheduleInterval(int[] flags) {
		int[] frequencies = new int[patches.size()];
		ArrayList<Pair<CampusPatch,Double>> tempoList;
		int[] flagchecker;
		int deduction;
		
		for(int i=0; i<patches.size(); i++) {
			frequencies[i] = patches.get(i).getWeight();
		}
		
		zone1 = new ArrayList<Pair<CampusPatch, Double>>();
		zone2 = new ArrayList<Pair<CampusPatch, Double>>();
		zone3 = new ArrayList<Pair<CampusPatch, Double>>();
		
		for(int i=0; i<patches.size(); i++) {
			Pair<CampusPatch, Double> nPair = new Pair<CampusPatch, Double>(patches.get(i), Double.valueOf(frequencies[i]));

			// add each nPair to the proper ArrayList, determined by zones
			if(i<8) {
				zone1.add(nPair);
			}else if(i>= 8 && i<14) {
				zone2.add(nPair);
			}else {
				zone3.add(nPair);
			}
		}

		// initialize array for return value
		int[] result = new int[patches.size()];
		/*
		 * patch selection: 
		 * 		-use numOfPatrols to determine how many iterations per zone
		 * 		-create clones zone1, zone2, and zone3
		 * 		-every patch selected is removed from clones ArrayLists. Originals are intact.
		 */		
		
		//zone1
		tempoList = new ArrayList<Pair<CampusPatch,Double>>(zone1);
		flagchecker = Arrays.copyOfRange(flags, 0, 8);
		deduction = IntStream.of(flagchecker).sum();
		
		for(int i=deduction; i<numOfPatrols[0]; ) {
			if(!verifyTempoList(tempoList)) break;
			CampusPatch selectedPatch = new EnumeratedDistribution<CampusPatch>(tempoList).sample();
			int identifier = selectedPatch.getID();
			
			if(flags[identifier] == 0) {
//				System.out.println("SELECTED "+selectedPatch.getName());
				counter[identifier]++;
				result[identifier] = 1;
				
				for(int j=0; j<tempoList.size(); j++) {
					
					if(tempoList.get(j).getKey().getID() == identifier) {
						tempoList.remove(j);
					}
				}
				i++;
			}
		}
		
		// zone2
		tempoList = new ArrayList<Pair<CampusPatch,Double>>(zone2);
		flagchecker = Arrays.copyOfRange(flags, 8, 14);
		deduction = IntStream.of(flagchecker).sum();
		
		for(int i=deduction; i<numOfPatrols[1]; ) {
			if(!verifyTempoList(tempoList)) break;
			CampusPatch selectedPatch = new EnumeratedDistribution<CampusPatch>(tempoList).sample();
			int identifier = selectedPatch.getID();
			if(flags[identifier] == 0) {
//				System.out.println("SELECTED "+selectedPatch.getName());
				counter[identifier]++;
				result[identifier] = 1;

				for(int j=0; j<tempoList.size(); j++) {
					if(tempoList.get(j).getKey().getID() == identifier) {
						tempoList.remove(j);
					}
				}
				i++;
			}
		}
		
		// zone3
		tempoList = new ArrayList<Pair<CampusPatch,Double>>(zone3);
		flagchecker = Arrays.copyOfRange(flags, 14, flags.length);
		deduction = IntStream.of(flagchecker).sum();
		
		for(int i=deduction; i<numOfPatrols[2]; ) {
			if(!verifyTempoList(tempoList)) break;
			CampusPatch selectedPatch = new EnumeratedDistribution<CampusPatch>(tempoList).sample();
			int identifier = selectedPatch.getID();
		
			if(flags[identifier] == 0) {
//				System.out.println("SELECTED "+selectedPatch.getName());
				counter[identifier]++;
				result[identifier] = 1;
				
				for(int j=0; j<tempoList.size(); j++) {
					if(tempoList.get(j).getKey().getID() == identifier) {
						tempoList.remove(j);
					}
				}
				i++;
			}
		}
		
		for(int i=0; i<flags.length; i++) {
			if(flags[i] == 1)
				result[i] = 1;
		}
		
		for(int i=0; i<visitCount.length; i++) {
			visitCount[i] += result[i];
		}
		return result;
	}

	public boolean verifyTempoList(ArrayList<Pair<CampusPatch,Double>> a) {
		int counter = 0;
		for(int i=0; i<a.size(); i++) {
			counter += a.get(i).getSecond();
		}
		if (counter > 0) return true;
		return false;
	}
	
	public void clearSchedule() {
		
		if(schedule != null) {
			for(int i=0; i<schedule.length; i++) {
				for(int j=0; j<schedule[0].length; j++) {
					schedule[i][j] = 0;
					patrolAssignments[i][j] = 0;
				}
			}
			
			
		}
		
	}
	
	public void clearFlags() {
		if(savedFlags != null) {
		
			for(int i=0; i<savedFlags.length; i++) {
				for(int j=0; j<savedFlags[0].length; j++) {
					savedFlags[i][j] = 0;
				}
			}
		
		}
	} 
	
	public void clearCount() {
		for(int i=0; i<visitCount.length; i++) {
			visitCount[i] = 0;
		}
	}
	
	public void writeForSimulator() {
		try {
			StringBuilder builder = new StringBuilder();
			builder.append(this.intervalCount+"\n");
			builder.append(this.intervalLength+"\n");
			builder.append(this.patrolCount+"\n");
//			builder.append("\n");//append new line at the end of the row
			BufferedWriter writer = new BufferedWriter(new FileWriter("./parameters.txt"));
			writer.write(builder.toString());//save the string representation of the board
			writer.close();
		}catch(Exception e) {
			System.out.println("(parameters.txt) FILE WRITING ENCOUNTERED AN ERROR");
			
		}
		
		/////////////////////////
		
		int[][] output = new int[this.patrolCount][this.intervalCount];
		
		for(int i=0; i<output.length; i++) {
			output[i] = patrols.get(i).getIntegerList();
		}
		
		
		for(int i=0; i<output.length; i++) {
			int[] out2 = output[i]; 
			for(int j=0; j<out2.length; j++) {
				System.out.print("\t"+Integer.toString(out2[j]));
			}
			
			System.out.println();
		}
		
		try {
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < output.length; i++)//for each row
		{
			int[] out2 = output[i];
		   for(int j = 0; j < out2.length; j++)//for each column
		   {
		      builder.append(out2[j]+"");//append to the output string
		      if(j < out2.length - 1)//if this is not the last row element
		         builder.append(",");//then add comma (if you don't like commas you can use spaces)
		   }
		   builder.append("\n");//append new line at the end of the row
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter("./patrolAssignments.txt"));
		writer.write(builder.toString());//save the string representation of the board
		writer.close();
		}catch(Exception e) {
			System.out.println("(assignments.txt)FILE WRITING EENCOUNTERED AN ERROR");
			
		}
		
	}
	
	//// PATROL ASSIGNMENT METHODS ////
	
	public void clearAssignments() {
		for(int i=0; i<patrols.size(); i++) {
			patrols.get(i).clearAssignment();
		}
	}
	
	public void setAssignments() {
		for(int i=0; i<schedule.length; i++) {
			int[] a = schedule[i];
			int[] b = patrolAssignments[i];
			
			if(i == 0) {
				int x = 0;
				for(int j=0; j<a.length; j++) {
					if(a[j] > 0) {
						b[j] = x;
						patrols.get(x).goToTarget(patches.get(j));
						x++;
					}
				}
			}else {
				
				patrolAssignments[i] = assignPatrols(a);
			}
		}
	}
	
	public int[] assignPatrols(int[] input) {

		int x = numOfPatrols[0];
		int y = x + numOfPatrols[1];
		int z = y + numOfPatrols[2];
		
		ArrayList<UPFAgent> dummyList;
		int[] output = new int[input.length];
		
		for(int i=0; i<output.length; i++) {
			output[i] = -1;
		}
		
		/// zone 1 ///
		
		dummyList = new ArrayList<UPFAgent>(patrols);
		List<UPFAgent> z1 = dummyList.subList(0, x);
		
		int minVal = Integer.MIN_VALUE;
		while(!z1.isEmpty()) {
			int s = 0;
			for(int i=0; i<z1.size(); i++) {
				if(z1.get(i).getMileage() > minVal) {
					minVal = z1.get(i).getMileage();
					s = i;
				}
			}
			
			UPFAgent pop = z1.get(s);
			int r = -1;
			int maxVal = Integer.MAX_VALUE;
			for(int j=0; j<8; j++) {
				if(input[j] > 0 && output[j]<0) {
					if(distance[pop.getLocation()][j] < maxVal) {
						r = j;
						maxVal = pop.getMileage();
					}
				}
			}
			pop.addToMileage(distance[pop.getLocation()][r]);
			pop.goToTarget(patches.get(r));
			
			output[r] = pop.getID();
			
			z1.remove(s);
		}
		
		/// zone 2 ///
		dummyList = new ArrayList<UPFAgent>(patrols);
		List<UPFAgent> z2 = dummyList.subList(x,y);
		
		minVal = Integer.MIN_VALUE;
		while(!z2.isEmpty()) {
			int s = 0;
			for(int i=0; i<z2.size(); i++) {
				if(z2.get(i).getMileage() > minVal) {
					minVal = z2.get(i).getMileage();
					s = i;
				}
			}

			UPFAgent pop = z2.get(s);
			int r = -1;
			int maxVal = Integer.MAX_VALUE;
			for(int j=8; j<14; j++) {
				if(input[j] > 0 && output[j]<0) {
					if(distance[pop.getLocation()][j] < maxVal) {
						r = j;
						maxVal = pop.getMileage();
					}
				}
			}
			pop.addToMileage(distance[pop.getLocation()][r]);
			pop.goToTarget(patches.get(r));
			
			output[r] = pop.getID();
			
			z2.remove(s);
		}
		
		/// zone 3 ///
		dummyList = new ArrayList<UPFAgent>(patrols);
		List<UPFAgent> z3 = dummyList.subList(y,z);
		minVal = Integer.MIN_VALUE;
		while(!z3.isEmpty()) {
			int s = 0;
			for(int i=0; i<z3.size(); i++) {
				if(z3.get(i).getMileage() > minVal) {
					minVal = z3.get(i).getMileage();
					s = i;
				}
			}

			UPFAgent pop = z3.get(s);
			int r = -1;
			int maxVal = Integer.MAX_VALUE;
			for(int j=14; j<18; j++) {
				if(input[j] > 0 && output[j]<0) {
					if(distance[pop.getLocation()][j] < maxVal) {
						r = j;
						maxVal = pop.getMileage();
					}
				}
			}
			pop.addToMileage(distance[pop.getLocation()][r]);
			pop.goToTarget(patches.get(r));
			
			output[r] = pop.getID();
			
			z3.remove(s);
		}
		
//		System.out.println("Output ");
//		for(int i=0; i<output.length; i++) {
//			System.out.print("\t"+Integer.toString(output[i]));
//		}
//		System.out.println();
		
		return output;
	}
	
	
	//// GET METHODS ////
	public int getTotalPatrols() {
		return patrols.size();
	}
	
	public int getPatchCount() {
		return patches.size();
	}
	
	public int[] getFrequencies() {
		int[] output = new int[patches.size()];
		
		for(int i=0; i<patches.size(); i++) {
			output[i] = patches.get(i).getWeight(); 
		}
		return output;
	}
	
	public int getZonePatrols(int input) {
		return numOfPatrols[input];
	}
	
	public LocalDate getTargetDate() {
		return targetDate;
	}
	
	public int[][] getSchedule(){

		return schedule;
	}

	public int[] getVisitCount() {
		return visitCount;
	}
	
	public int[][] getRestrictions(){
		return savedFlags;
	}	
	
	public int[][] getAssignments(){
		return this.patrolAssignments;
	} 

	public int[] getPresetFrequencies() {
		return preset_weights;
	}
	
	public String[] getPatchNames() {
		String[] output = new String[patches.size()];
		for(int i=0; i<patches.size(); i++) {
			output[i] = patches.get(i).getName();
		}
		
		return output;
	}
	
	public String getAllPatrolAssignments() {
		String output = "";
//		StringBuilder sb = new StringBuilder(output);
		for(int i=0; i<patrols.size(); i++) {
//			sb.append(patrols.get(i).getReport());
			output += patrols.get(i).getReport();
		}
		
		return output;
	}
	
	public UPFAgent getPatrol(int input) {
		return patrols.get(input);
	}
	
	//// DEBUGGING METHODS ////
	public void viewFrequencies() {
		for(int i=0; i<patches.size(); i++) {
			System.out.println(patches.get(i).getInfo());
		}
	}

	public void viewSchedule(boolean asList) {
		for(int i=0; i<schedule.length; i++) {
			int[] a = schedule[i];
			if(asList) {
				String fuse = "Interval "+Integer.toString(i)+": ";
				
				for(int j=0; j<a.length;j++) {
					if(a[j] == 1) {
						fuse = fuse.concat(patches.get(j).getName()+" | ");
					}
				}
				System.out.println(fuse);
				
			}else {
				System.out.print("Interval "+Integer.toString(i)+":");
				for(int j=0; j<a.length;j++) {
					System.out.print("\t"+Integer.toString(a[j]));
				}
				System.out.println();
			}
		}
	}
	
	public void viewAssignments() {
		for(int i=0; i<patrolAssignments.length; i++) {
			int[] a = patrolAssignments[i];
			System.out.print("Interval "+Integer.toString(i)+":");
			for(int j=0; j<a.length;j++) {
				if(a[j] >= 0) {
					System.out.print("\t"+Integer.toString(a[j]));
				}else {
					System.out.print("\t[]");
				}
			}
			System.out.println();
		}
	}
	
	public void getPatrolInfo() {
		for(int i=0; i<patrols.size(); i++) {
			System.out.println(patrols.get(i).getInfo());
		}
	}
	
	public void showReports() {
		for(int i=0; i<patrols.size(); i++) {
			patrols.get(i).reportAssignment();
		}
	}
	
	public void showPatrolRoute() {
		
		for(int a=0; a<patrols.size(); a++) {
			UPFAgent b = patrols.get(a);
			
			System.out.println("Agent "+b.getName());
			
			for(int i=0; i<b.getAssignment().size(); i++) {
				System.out.println(b.getAssignment().get(i).getFirst().getName()+", intervals: "+Integer.toString(b.getAssignment().get(i).getSecond()));
				
			}
		
			System.out.println("Mileage:"+Integer.toString(b.getMileage())+"\n");
		}
	}
}
