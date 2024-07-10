package patrolscheduler;

import java.util.ArrayList;
import org.apache.commons.math3.util.Pair;

public class UPFAgent {

	private int agentid;
	private String name;
	
	private int assignedZone;
	private int mileage;	
	CampusPatch currentLocation;
	CampusPatch targetLocation;
	ArrayList<Pair<CampusPatch, Integer>> assignedSchedule;
	ArrayList<String> simpleList;
	ArrayList<Integer> intList;
	
	
	public UPFAgent(int id) {
		this.agentid = id;
		setName(id);
		
		assignedSchedule = new ArrayList<Pair<CampusPatch, Integer>>();
		simpleList = new ArrayList<String>();
		intList = new ArrayList<Integer>();
		
		mileage = 0;
	}
	
	public void setName(int id) {
		//Alpha, Bravo, Charlie, Delta, Echo, Foxtrot, Golf, Hotel, India, Juliett, Kilo, Lima, Mike, November, Oscar, Papa, Quebec, Romeo, Sierra, Tango, Uniform, Victor, Whiskey, X-ray, Yankee, Zulu.
		switch(id) {
		case 0: this.name = "Alpha";
				break;
		case 1: this.name = "Bravo";
				break;
		case 2: this.name = "Charlie";
				break;
		case 3: this.name = "Delta";
				break;
		case 4: this.name = "Echo";
				break;
		case 5: this.name = "Foxtrot";
				break;
		case 6: this.name = "Golf";
				break;
		case 7: this.name = "Hotel";
				break;
		case 8: this.name = "Indigo";
				break;
		case 9: this.name = "Juliett";
				break;
		case 10: this.name = "Kilo";
				break;
		case 11: this.name = "Lima";
				break;
		case 12: this.name = "Mike";
				break;
		case 13: this.name = "November";
				break;
		case 14: this.name = "Oscar";
				break;
		case 15: this.name = "Papa";
				break;
		case 16: this.name = "Quebec";
				break;
		case 17: this.name = "Romeo";
				break;
		case 18: this.name = "Sierra";
				break;
		case 19: this.name = "Tango";
				break;
		case 20: this.name = "Uniform";
				break;
		case 21: this.name = "Victor";
				break;
		case 22: this.name = "Whiskey";
				break;
		case 23: this.name = "X-Ray";
				break;
		case 24: this.name = "Yankee";
				break;
		case 25: this.name = "Zulu";
				break;
		default: this.name = "Agent"+Integer.toString(id);
		}
	}
	
	public void goToTarget(CampusPatch tp) {
		targetLocation = tp;
		int schedsize = assignedSchedule.size();
		int val = 1;

		/// version 1, a bit messy but will most likely work
//		if(schedsize > 0) {
//			if(assignedSchedule.get(schedsize).getFirst().equals(tp)) {
//				val += assignedSchedule.get(schedsize).getSecond();
//				assignedSchedule.remove(schedsize);
//			}
//		}
		
		/// version 2. may not work
		if(schedsize > 0 && assignedSchedule.get(schedsize-1).getFirst().equals(tp)) {
			val += assignedSchedule.get(schedsize-1).getSecond();
			assignedSchedule.remove(schedsize-1);
		}
		
		///// TAKE NOTE, CHOOSE 1 VERSION, COMMENT OUT THE OTHER
		
		assignedSchedule.add(new Pair<>(tp, val));
		simpleList.add(tp.getName());
		intList.add(tp.getID());
		currentLocation = targetLocation;
		
	}

	public void reportAssignment() {
		System.out.println(this.getName());
		for(int i=0; i<assignedSchedule.size(); i++) {
			String first = assignedSchedule.get(i).getFirst().getName();
			String second = Integer.toString(assignedSchedule.get(i).getSecond());
			
			System.out.println(first+": "+second+" intervals");
		}
		String mileage = "Estimated Mileage: "+Integer.toString(this.mileage);
		System.out.println(mileage+"\n");
	} 
	
	public void clearAssignment() {
		assignedSchedule.clear();
		simpleList.clear();
		intList.clear();
		targetLocation = null;
		currentLocation = null;
		mileage = 0;
	} 
	
	// SET methods
	public void setID(int input) {
		this.agentid = input;
	}
	
	public void setName(String input) {
		this.name = input;
	}
	 
	public void setAssignedZone(int input) {
		this.assignedZone = input;
	}
	
	public void setCurrentLocation(CampusPatch c) {
		this.currentLocation = c;
	}
	
	public void setMileage(int input) {
		this.mileage = input;
	}
	
	public void addToMileage(int input) {
		this.mileage += input;
	}
	
	
	
	// GET methods
	public String getInfo() {
		return "Agent "+name +" assigned to: Zone"+Integer.toString(assignedZone);
	}
	
	public int getID() {
		return this.agentid;
	}
	
	public String getName() {
		return this.name;
	}
	 
	public int getLocation() {
		return this.currentLocation.getID();
	}
	
	public int getAssignedZone() {
		return this.assignedZone;
	}
	
	public int getMileage() {
		return this.mileage;
	}
	
	public int getSchedSize() {
		return assignedSchedule.size();
	}
	
	public ArrayList<Pair<CampusPatch, Integer>> getAssignment(){
		return assignedSchedule;
	}
	
	public String getReport() {

		String out = this.name+"\n";
		
		for(int i=0; i<assignedSchedule.size(); i++) {
			String first = assignedSchedule.get(i).getFirst().getName();
			String second = Integer.toString(assignedSchedule.get(i).getSecond());
			
			out += Integer.toString(i+1)+": "+first+" ("+second+" interval/s)";
			out += "\n";
		}
		
		out += "Estimated Mileage: "+Integer.toString(this.mileage)+"\n\n";
		
		return out;
	}
	
	public ArrayList<String> getStringList(){
		return simpleList;
	}
	
	public int[] getIntegerList(){
		int[] arr = intList.stream().mapToInt(i -> i).toArray();
		return arr;
	}

}

