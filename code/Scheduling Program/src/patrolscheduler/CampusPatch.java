package patrolscheduler;

public class CampusPatch {
	
	private int patchID; // primary ID, when patches are created, they are named from 0 onwards
	private String patchName = "Placeholder"; // name, for easier identification by user
	private int weight = 0;
	
	private int zoneGroup = 0;
	
	private int weightMorning = 0;
	private int weightEvening = 0;
	private int weightNight = 0;
	
		
	/* patchClass identifies what type of are a certain patch primarily is. (School facilities = 1, Public space = 2, residential = 3, default = 0)
	 * passRate describes how often people pass by the patch (main roads are used more often)
	 *  
	 * */
	
	//private int zoneID = 0; // group patches together to prevent agents travelling to far patches, default = 0
	
	public CampusPatch(int id, String name) {
		this.patchID = id;
		this.patchName = name;
	}

	public CampusPatch(int id, String name, int zone) {
		this.patchID = id;
		this.patchName = name;
		this.zoneGroup = zone;
	}
	
	public CampusPatch(int id, String name, int weight, int zone) {
		this.patchID = id;
		this.patchName = name;
		this.weight = weight;
		this.zoneGroup = zone;
	}
	
	public CampusPatch(int id, String name, int f1, int f2, int f3) {
		this.patchID = id;
		this.patchName = name;
		this.weightMorning = f1;
		this.weightEvening = f2;
		this.weightNight = f3;
		this.weight = 10;
	}
	
	// SET Methods
	public void setName(String newname) {
		this.patchName = newname;
	}
	
	public void setID(int id) {
		this.patchID = id;
	}
	
	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	public void setMorningWeight(int weight) {
		this.weightMorning = weight;
	}
	
	public void setEveningWeight(int weight) {
		this.weightEvening = weight;
	}
	
	public void setNightWeight(int weight) {
		this.weightNight = weight;
	}
	
	// GET Methods
	public String getInfo() {
//		return Integer.toString(patchID)+") \""+patchName+"\""
//				+" Morning:"+Integer.toString(weightMorning)
//				+", Evening:"+Integer.toString(weightEvening)
//				+", Night:"+Integer.toString(weightNight);
		
		return Integer.toString(patchID)+") \""+patchName+"\"" +Integer.toString(weight);
	}
	
	public String getName() {
		return this.patchName;
	}
	
	public int getID() {
		return this.patchID;
	}
	
	public int getWeight() {
		return this.weight;
	}
	
	public int getWeight(int timeFrame) {
		switch(timeFrame) {
		case 0: return this.weightMorning;
		case 1: return this.weightEvening;
		case 2: return this.weightNight;
		}
		return weight;
	}
	
	public int getMorningWeight() {
		return this.weightMorning;
	}
	
	public int getEveningWeight() {
		return this.weightEvening;
	}
	
	public int getNightWeight() {
		return this.weightNight;
	}
}

//CampusPatch cpark = new CampusPatch(0, "Carabao Park");
//CampusPatch cem = new CampusPatch(1, "CEM/Raymundo gate");
//CampusPatch biosci = new CampusPatch(2, "BioSci");
//CampusPatch mainlib = new CampusPatch(3, "Main Library");
//CampusPatch opark = new CampusPatch(4, "Oblation Park");	
//CampusPatch palma = new CampusPatch(5, "Palma Bridge");
//CampusPatch kaliwa = new CampusPatch(6, "V. Dawis (Kaliwa)");
//
//CampusPatch dlumali = new CampusPatch(7, "DL Umali Hall");
//CampusPatch subldg = new CampusPatch(8, "Student Union Building");
//CampusPatch baker = new CampusPatch(9, "Baker Hall");
//CampusPatch ansci = new CampusPatch(10, "Animal Science Area");
//CampusPatch ceat = new CampusPatch(11, "CEAT");
//CampusPatch ymca = new CampusPatch(12, "YMCA");
//
//CampusPatch uhs = new CampusPatch(13, "Univ. Health Service");
//CampusPatch housing = new CampusPatch(14, "Housing");
//CampusPatch fprdi = new CampusPatch(15, "Forest products/Makiling botanical");
//CampusPatch c5dorms = new CampusPatch(16, "UP dorms");









