package simulation;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.planargraph.Node;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.geo.GeomVectorField;
import sim.field.network.Network;
import sim.io.geo.ShapeFileImporter;
import sim.util.geo.GeomPlanarGraph;
import sim.util.geo.GeomPlanarGraphEdge;
import sim.util.geo.MasonGeometry;



/** (NOTE FROM THE DEV TEAM OF MASON)
 * The simulation core.
 * 
 * The simulation can require a LOT of memory, so make sure the virtual machine
 * has enough. Do this by adding the following to the command line, or by
 * setting up your run configuration in Eclipse to include the VM argument:
 * 
 * -Xmx1024M
 * 
 * With smaller simulations this chunk of memory is obviously not necessary. You
 * can take it down to -Xmx800M or some such. If you get an OutOfMemory error,
 * push it up.
 */
public class Patrols extends SimState{

    private static final long serialVersionUID = 1L;
    
    
    //Main function allows simulation to be run in stand-alone, non-GUI mode
    public static void main(String[] args)    {
        doLoop(Patrols.class, args);
        System.exit(0);
    }
    
    // simulator variables ////////////////////////////////////////
    int minuteLength = 60;
    int numPatrols;
    int intervalCount;
    int intervalLength;
    
    int intervalDuration;
    int shiftDuration;
    
    Monitor monitor;
    Navigator navigator;
    PathFinder pathfinder;
    
    // debug attributes used to check size of resulting network/graph
    int edgeCount = 0;
    int nodeCount = 0;
    
    public int scheduleCounter = 0;
    
    public GeomVectorField patches = new GeomVectorField();
	public GeomVectorField buildings = new GeomVectorField();
	public GeomVectorField roads = new GeomVectorField();
    
	public GeomVectorField debugTarget = new GeomVectorField();
	
    // traversable network
    public GeomPlanarGraph network = new GeomPlanarGraph();
    public GeomVectorField junctions = new GeomVectorField();
    
    int[][] fromFile;
    
    // mapping between unique edge IDs and edge instances themselves
    HashMap<Integer, GeomPlanarGraphEdge> idsToEdges = new HashMap<Integer, GeomPlanarGraphEdge>();
//    HashMap<GeomPlanarGraphEdge, ArrayList<Patrol>> edgeTraffic = new HashMap<GeomPlanarGraphEdge, ArrayList<Patrol>>();
    
    public GeomVectorField agents = new GeomVectorField();
    public ArrayList<Patrol> agentList = new ArrayList<Patrol>();

    // end of simulator variables /////////////////////////////////

    
    
    // GUI Model Inspector functions////////////////////////////////
    
    
    
    // end of GUI Model Inspector functions/////////////////////////
    
    
    
    
    // constructor
    public Patrols(long seed)
    {
    	// superclass is SimState
        super(seed);
    }


    //initialization
    @Override
    public void start(){
    	/* This function contains:
    	 * - Reading and importation of GIS files (in .shp and .dbf format)
    	 * - converting the GIS files for compatibility with MASON
    	 * - initializing Agent instances
    	 * */
        super.start();
        try{
        	readParameters();
        	
        	monitor = new Monitor(this, intervalCount, intervalLength, numPatrols, fromFile);
        	navigator = new Navigator();
//        	pathfinder = new PathFinder();
        	
            // read in the roads to create the transit network
            System.out.println("reading shp files");

//			String patchSHP = "shapefiles/campusPatches.shp";
//			String patchDBF = "shapefiles/campusPatches.dbf";
    		
    		String buildingSHP = "shapefiles/campusBuildings.shp";
    		String buildingDBF = "shapefiles/campusBuildings.dbf";
    	    		
    		String roadSHP = "shapefiles/simulationReadablePaths.shp";
    		String roadDBF = "shapefiles/simulationReadablePaths.dbf";
    		

//			ShapeFileImporter.read(new File(patchSHP).toURI().toURL(), new File(patchDBF).toURI().toURL(), patches);
			ShapeFileImporter.read(new File(buildingSHP).toURI().toURL(), new File(buildingDBF).toURI().toURL(), buildings);
			ShapeFileImporter.read(new File(roadSHP).toURI().toURL(), new File(roadDBF).toURI().toURL(), roads);
			
			
			Envelope globalMBR = patches.getMBR();
			globalMBR.expandToInclude(buildings.getMBR());
			globalMBR.expandToInclude(roads.getMBR());
			
			globalMBR.expandToInclude(debugTarget.getMBR());

            createNetwork();
            
			patches.setMBR(globalMBR);
			buildings.setMBR(globalMBR);
			roads.setMBR(globalMBR);
			junctions.setMBR(globalMBR);
			
			debugTarget.setMBR(globalMBR);

			agents.clear();

			schedule.scheduleRepeating(monitor);
			
			populate(numPatrols);
            agents.setMBR(globalMBR);

            // Ensure that the spatial index is updated after all the agents
            // move
            schedule.scheduleRepeating(agents.scheduleSpatialIndexUpdater(), Integer.MAX_VALUE, 1.0);

            
        } catch (FileNotFoundException ex){
            System.out.println("Error: missing required data file");
        } catch (IOException ex){
            Logger.getLogger(Patrols.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error: IOException encountered (see logger file for details)");
        } catch (Exception ex){
            Logger.getLogger(Patrols.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error:  Unspecified Exception encountered (see logger file for details)");
        }     
    }

   // create road network
    public void createNetwork(){
    	// GeoMason function, takes a vector field and creates a network/graph
    	 
    	// only edges are created at this line
    	network.createFromGeomField(roads);
        
        // loop intantiates a graph edge and inserts them in a HashMap. 
    	// key-value pair is a unique attribute (fid) and the edge instance itself
        for (Object o : network.getEdges()){
            GeomPlanarGraphEdge e = (GeomPlanarGraphEdge) o;
            idsToEdges.put(e.getIntegerAttribute("fid"), e);

            e.setData(new ArrayList<Patrol>());
        }
        
        // this line adds the nodes in the graph
        addIntersectionNodes(network.nodeIterator(), junctions);

    }
    
    private InputStream getFileFromResourceAsStream(String fileName) throws MalformedURLException {
    	/* this function is only called by other functions within the class. 
    	 * files must be in "input folder" in order to be read
    	 * */

        // class loader
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        // the inputstream holding the file content
        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
        	System.out.println(new File(fileName).toURI().toURL().toString());
            return inputStream;
        }

    }
    
    // obtain parameters from scheduler program
    public void readParameters() {
    	
    	try {
    		String inFile = "parameters.txt";
    		InputStream strm = this.getFileFromResourceAsStream(inFile);
    		/* parameters.txt contains three lines of integers
    		 * 1st integer = number of intervals
    		 * 2nd integer = length of an interval
    		 * 3rd integer = number of Agents for the simulation
    		 * */
    		
    		BufferedReader br = new BufferedReader(new InputStreamReader(strm)); 
			String str = "";
			int counter = 0;
			while((str=br.readLine())!=null && str.length()!=0) { // br reads lines until it encounters End of File
				String sample = str.trim(); // The entire line is first trimmed of leading and trailing whitespace, then split
				
				switch(counter) {
					case 0: intervalCount = Integer.parseInt(sample);
							break;
					case 1: intervalLength = Integer.parseInt(sample);
							break;
					case 2: numPatrols = Integer.parseInt(sample);
					
					default: break;
				}
				counter++;
			}
			
			// intervalDuration and intervalLength determine how long the simulation will run 
			intervalDuration = minuteLength * intervalLength;
			shiftDuration = intervalDuration * intervalCount;
			
			br.close();
			
			System.out.println("parameters.txt file read complete");
			
    	}catch(Exception fnf) {
    		System.out.println("ERROR: parameters.txt file not found");
    		System.out.println(fnf.getMessage());
    	}
    }
    
    // read patrol assignments from scheduler program
    public boolean readFromFile() {
    	try {
    		int[][] read = new int[numPatrols][intervalCount];

    		/* patrolAssignments.txt is written similarly to a CSV file. it's generated by the
    		 * scheduler program. It contains numbers corresponding to vantage points in the map.
    		 * The simulator uses these numbers to direct the Agent instances to their next assigned area.
    		 * */
    		String inFile = "patrolAssignments.txt";
    		InputStream strm = this.getFileFromResourceAsStream(inFile);
    		
    		
    		BufferedReader br = new BufferedReader(new InputStreamReader(strm)); 
			String str = "";
			
			int counter = 0;
			while((str=br.readLine())!=null && str.length()!=0) { // br reads lines until it encounters End of File
				String[] split = str.trim().split(","); // The entire line is first trimmed of leading and trailing whitspace, then split
				for(int i=0; i<split.length; i++){
					int entry = Integer.parseInt(split[i]);
					read[counter][i] = entry;
				}
				counter++;
			}
			
			// debugging function: display the obtained values from file
//			displayAssignments(read);
			
			// save the obatined values into a 2d array (fromFile)
			fromFile = read.clone();
			
			br.close();
			return true;
    	}catch(Exception e) {
    		System.out.println(e.getMessage());
    		return false;
    	}
    }
    
    public void populate(int input) {
    	
    	// name assignemts for each agent instance based on their order of instantiation
    	String[] names = {"Alpha","Bravo","Charlie","Delta","Echo","Foxtrot","Golf","Hotel",
    						"Indigo","J","Kilo","Lima","M","N","Oscar","Papa","Q",
    						"Romeo","Sierra","Tango","Uniform","V","W","Xray","Yankee","Zulu"}; 
        
    	// if parameters.txt was successfully read, code block is used
    	if(readFromFile()) {
    		System.out.println("File reading of patrolAssignments.txt successful");
    		for (int i=0; i<input; ) {
    			
    			
	        	int[] schedArray = fromFile[i];
	        	
	        	Patrol a = new Patrol(this, schedArray, i, names[i]);
	            boolean successfulStart = a.start(this);
	
	            if (!successfulStart){
	            	// this code block makes sure that no defective Agent instance is created
	            	System.out.println("Error encounterd while creating Agent instance");
	                continue; // DON'T ADD IT if it's bad
	            }
	            
	            // add Agent instance into the simulation, and into an ArrayList
	            MasonGeometry newGeometry = a.getGeometry();
	            newGeometry.isMovable = true;
	            agents.addGeometry(newGeometry);
	            agentList.add(a);
	            schedule.scheduleRepeating(a);
	            i++;
	
	        }
    	}else {
    		// 
    		System.out.println("Reading of patrolAssingments.txt failed");
	    	for (int i=0; i<input; ) {
	        	int[] schedArray = new int[intervalCount];
	        	
	        	for(int j=0; j<intervalCount; j++) {
	        		// pure random
	//        		schedArray[j] = random.nextInt(18);
	        		
	        		// round robin
	        		schedArray[j] = (i+j) % 18;
	        		
	        		// stay in patch
	//        		schedArray[j] = i;
	        	}
	        	
	        	Patrol a = new Patrol(this, schedArray, i, names[i]);
	            boolean successfulStart = a.start(this);
	
	            if (!successfulStart){
	            	// this code block makes sure that no defective Agent instance is created
	            	System.out.println("ERROR: patrol cannot find path");
	                continue; // DON'T ADD IT if it's bad
	            }
	            MasonGeometry newGeometry = a.getGeometry();
	            newGeometry.isMovable = true;
	            agents.addGeometry(newGeometry);
	            agentList.add(a);
	            schedule.scheduleRepeating(a);
	            i++;
	        }
    	}
    }

   
    private void addIntersectionNodes(Iterator<?> nodeIterator, GeomVectorField intersections){
    	 /* adds nodes corresponding to road intersections to GeomVectorField
         * 
         * @param nodeIterator  Points to first node
         * @param intersections GeomVectorField containing intersection geometry
         * 
         * Nodes will belong to a planar graph populated from LineString network.
         */
    	
        GeometryFactory fact = new GeometryFactory();
        Coordinate coord = null;
        Point point = null;
        
        while (nodeIterator.hasNext()){
            Node node = (Node) nodeIterator.next();
            coord = node.getCoordinate();
            point = fact.createPoint(coord);

            junctions.addGeometry(new MasonGeometry(point));
            
        }
    }
    
    void displayAssignments(int[][] input) {
    	/*	Called by readFromFile(). Displays the 2D array of integers that determine 
    	 * 	what area of the map a Patrol has to go to
    	 *  
    	 *  @param input 	2D array
    	 *  
    	 * */
    	for(int i=0; i<input.length; i++) {
			int[] sub = input[i];
			for(int j=0; j<sub.length; j++) {
				System.out.print('\t'+Integer.toString(sub[j]));
			}
			System.out.println();
		}
    	
    }
}