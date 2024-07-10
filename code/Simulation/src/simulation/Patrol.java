package simulation;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.linearref.LengthIndexedLine;
import com.vividsolutions.jts.planargraph.DirectedEdgeStar;
import com.vividsolutions.jts.planargraph.Node;
import java.util.ArrayList;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.geo.GeomPlanarGraphDirectedEdge;
import sim.util.geo.GeomPlanarGraphEdge;
import sim.util.geo.MasonGeometry;
import sim.util.geo.PointMoveTo;

public final class Patrol implements Steppable{

	private static final long serialVersionUID = -1113018274619047013L;
	private String patrolName;
	private int patrolID;
	
	private int[] itinerary;
		
    Patrols world;
    
    int delay = 0;
    
    GeomPlanarGraphEdge srcEdge;
    GeomPlanarGraphEdge destEdge;
    
    Node sourceNode = null;
    Node targetNode = null;
    
    Node dc_sourceNode = null;
    Node dc_targetNode = null;
    float dc_pathlength = 0;
        
//    boolean oneGoal = true;
    boolean idle = true;
    boolean override = false;
    
    int stepsIdle;
    int stepsMoving;
    
    int response_count = 0;
    int stepsResponding;
    
    int status = 0;
    
    int scheduleIndex = 0;
    
    boolean urgent = false;
    int assignmentFromMonitor = 0;
    int emergencyLocation = -1;

    // point that denotes agent's position
    private MasonGeometry location;
    
    // How much to move the agent by in each step()
//    private double moveRate = 0.0000012;
//    private final double moveRate = 0.00001;
    private final double moveRate = 0.000019;
//    private final double moveRate = 0.000015;
//    private double moveRate = 0.00003;
    
    // Used by agent to walk along line segment
    private LengthIndexedLine segment = null;
    double startIndex = 0.0; // start position of current line
    double endIndex = 0.0; // end position of current line
    double currentIndex = 0.0; // current location along line
   
    GeomPlanarGraphEdge currentEdge = null;
    
    int linkDirection = 1;
    double speed = 0; // useful for graph
    
    
//    ArrayList<GeomPlanarGraphDirectedEdge> pathToTarget;
    ArrayList<GeomPlanarGraphDirectedEdge> pathToTarget = new ArrayList<GeomPlanarGraphDirectedEdge>();
    int indexOnPath = 0;
    int pathDirection = 1;
    boolean reachedDestination;
    PointMoveTo pointMoveTo = new PointMoveTo();


    // constructor
    public Patrol(Patrols p, int[] patrolRoute, int id, String name){
        world = p;
        
        patrolID = id;
        itinerary = patrolRoute;
        patrolName = "Unit "+ name;
        
        idle = false;
        scheduleIndex = 0;
        
        this.stepsIdle = 0;
        this.stepsMoving = 0;
        this.stepsResponding = 0;
        
        // 0 = ready, 1 = active patrol, 2 = transferring, 3 = emergency response
        

        reachedDestination = true;
        
        int eID = world.navigator.getFromPatch(itinerary[0]);
        GeomPlanarGraphEdge ge = world.idsToEdges.get((int) eID);
        sourceNode = ge.getDirEdge(0).getFromNode();
        int edgeId = world.navigator.getFromPatch(itinerary[scheduleIndex]);
        targetNode = world.idsToEdges.get((int) edgeId).getDirEdge(0).getToNode();
        
        // set the location to be displayed
        GeometryFactory fact = new GeometryFactory();
        location = new MasonGeometry(fact.createPoint(new Coordinate(10, 10))) ;
        location.isMovable = true;
        Coordinate startCoord = null;
        startCoord = sourceNode.getCoordinate();
        updatePosition(startCoord);
        
    }
    
    public void resetMovementVariables() {
    	pathToTarget = new ArrayList<GeomPlanarGraphDirectedEdge>();
        indexOnPath = 0;
        pathDirection = 1;
        reachedDestination = false;
        pointMoveTo = new PointMoveTo();
    	
    }
    
    public Node newTargetEdge(GeomPlanarGraphEdge goalEdge) {	
    	return goalEdge.getDirEdge(0).getToNode();
    }
    
   
    public boolean start(Patrols state){
    	 /** Initialization of an Agent: find an A* path to work!
        *
        * @param state
        * @return whether or not the agent successfully found a path to work
        */
        findNewAStarPath(state);
        if (pathToTarget.isEmpty()){
            System.out.println("Initialization of agent failed: it is located in a part of the network that cannot access the given goal node");
            return false;
        } else{
            return true;
        }
    }

    // Plots a path between the Agent's home Node and its work Node
    private void findNewAStarPath(Patrols geoTest){
    	
        // get the home and work Nodes with which this Agent is associated
        Node currentJunction = geoTest.network.findNode(location.geometry.getCoordinate());
//    	Node currentJunction = null;
//        if(pathToTarget.size() >= 1) {
//    		currentJunction = pathToTarget.get(0).getFromNode();
//    	}
//    	else {
//    		currentJunction = world.network.findNode(location.geometry.getCoordinate());
//    	}
        
        Node destinationJunction = this.targetNode;
        
        // find the appropriate A* path between them
        AStar pathfinder = new AStar(patrolName, false);
        
        ArrayList<GeomPlanarGraphDirectedEdge> path = pathfinder.astarPath(currentJunction, destinationJunction);
        //System.out.println("Path size: "+Integer.toString(path.size()));
        
        // if the path works, lay it in
        if (path != null && path.size() > 0) {

            // save it
        	pathToTarget = path;

            // set up how to traverse this first link
            GeomPlanarGraphEdge edge =(GeomPlanarGraphEdge) path.get(0).getEdge();
			setupEdge(edge);
            
            // update the current position for this link
            updatePosition(segment.extractPoint(currentIndex));
        }
        
        
    }
    
    
    public double distanceCalculation(int targetID){
    	
    	
    	double totalDistance = 0;
    	Node dc_currentJunction = null;
    	Node dc_destinationJunction = null;
    	
    	
    	ArrayList<GeomPlanarGraphDirectedEdge> testPath = (ArrayList<GeomPlanarGraphDirectedEdge>) this.pathToTarget.clone();
    	// get the source and target Nodes with which this Agent is associate
    	if(testPath.size() >= 1) {
    		dc_currentJunction = testPath.get(0).getFromNode();
    	}
    	else {
    		dc_currentJunction = world.network.findNode(this.location.geometry.getCoordinate());
    	}
    	
		dc_destinationJunction = world.idsToEdges.get(targetID).getDirEdge(0).getToNode();
		
		// find the appropriate A* path between them
		AStar dc_pathfinder = new AStar(patrolName, false);

		ArrayList<GeomPlanarGraphDirectedEdge> dc_path = dc_pathfinder.astarPath(dc_currentJunction, dc_destinationJunction);

		
		// if the path works, lay it in
		if (dc_path != null && dc_path.size() > 0) {
        	for(int i=0; i<dc_path.size(); i++) {
        		GeomPlanarGraphEdge dc_edge = (GeomPlanarGraphEdge) dc_path.get(i).getEdge();
        		totalDistance += dc_edge.getLine().getLength();	
        	}
        }
    	
//		Node source = world.network.findNode(this.location.geometry.getCoordinate());
//    	Node source = pathToTarget.get(0).getFromNode();
//    	Node target = world.idsToEdges.get(targetID).getDirEdge(0).getToNode();
//    	
//    	totalDistance = source.getCoordinate().distance(target.getCoordinate());
    	
        return Math.abs(totalDistance);
        
        
    }
    
    
    /** Called every tick by the scheduler */
    /** moves the agent along the path */
    public void step(SimState state){
    	
    	stepTracker();
    	
    	if(this.urgent) {
    		delay = 0;
    		if(pathToTarget.size() >= 1) {
    			pathToTarget.subList(1, pathToTarget.size()).clear();
    		}
    	}
    	
    	if(delay > 0) {
    		delay--;
    		return;
    	}
        // check that we've been placed on an Edge
        if (segment == null){
            return;
        } // check that we haven't already reached our destination
        else if (reachedDestination){
        	this.status = 0;
        	patrolMovement();
        }
        
        // move along the current segment
    	speed = moveRate * linkDirection;
        currentIndex += speed;

        // check to see if the progress has taken the current index beyond its goal
        // given the direction of movement. If so, proceed to the next edge
        if (linkDirection == 1 && currentIndex > endIndex){
            Coordinate currentPos = segment.extractPoint(endIndex);
            updatePosition(currentPos);
            transitionToNextEdge(currentIndex - endIndex);
        } else if (linkDirection == -1 && currentIndex < startIndex){
            Coordinate currentPos = segment.extractPoint(startIndex);
            updatePosition(currentPos);
            transitionToNextEdge(startIndex - currentIndex);
        }else { // just update the position!
            Coordinate currentPos = segment.extractPoint(currentIndex);
            updatePosition(currentPos);
        }
    }
    
    void stepTracker() {
    	switch(this.status) {
    	
    	case 2: 	this.stepsMoving++;
    				break;
    	case 3: 	this.stepsResponding++;
    				break;
    	default: 	this.stepsIdle++;
    	
    	}
    }
    
   
    void transitionToNextEdge(double residualMove){
    	 /**
         * Transition to the next edge in the path
         * @param residualMove the amount of distance the agent can still travel
         * this turn
         */

        // update the counter for where the index on the path is
        indexOnPath += pathDirection;

        // check to make sure the Agent has not reached the end
        // of the path already
        if ((pathDirection > 0 && indexOnPath >= pathToTarget.size()) || (pathDirection < 0 && indexOnPath < 0)){
        	reachedDestination = true;
    		indexOnPath -= pathDirection; // make sure index is correct
            return;
        }

        // move to the next edge in the path
        GeomPlanarGraphEdge edge = (GeomPlanarGraphEdge) pathToTarget.get(indexOnPath).getEdge();
        setupEdge(edge);
        speed = moveRate * linkDirection;
        currentIndex += speed;

        // check to see if the progress has taken the current index beyond its goal
        // given the direction of movement. If so, proceed to the next edge
        if (linkDirection > 0 && currentIndex > endIndex){
            transitionToNextEdge(currentIndex - endIndex);
        } else if (linkDirection < 0 && currentIndex < startIndex){
            transitionToNextEdge(startIndex - currentIndex);
        }
    }

    
    ///////////// HELPER FUNCTIONS ////////////////////////////

    void monitorOrders(int assignment) {
    	this.assignmentFromMonitor = assignment;
//    	this.emergencyLocation = er;
//    	this.urgent = urgency;
    }
    
    void respondToCall(int target) {
    	this.urgent = true;
    	this.emergencyLocation = target;
    	this.status = 3;
    	
    }
    
    void patrolMovement() {

    	int newEdgeID = -1;
    	if(this.urgent) {	
    		this.delay = 0;
    		this.response_count++;
			resetMovementVariables();
			newEdgeID = this.emergencyLocation;
			this.status = 3;
//			System.out.println(this.patrolName + " Heading to Call");
			this.urgent = false;
    	}
    	else {
    		delay = world.random.nextInt(120)+60;
    		resetMovementVariables();
			newEdgeID = world.navigator.getFromPatch(itinerary[this.assignmentFromMonitor]);
//			System.out.println(this.patrolName + " Heading to Next Assignment");
    	} 
	
		GeomPlanarGraphEdge newTarget = world.idsToEdges.get((int) newEdgeID);
		this.sourceNode = world.network.findNode(this.location.getGeometry().getCoordinate());
		this.targetNode = newTargetEdge(newTarget);
		this.findNewAStarPath(world);
		
		
	}

    
    
    public void checkAll(int[] input) {
    	for(int i=0; i<input.length; i++) {
    		GeomPlanarGraphEdge grap = world.idsToEdges.get((int) input[i]);
    		targetNode = grap.getDirEdge(0).getToNode();
    		boolean checker = this.start(world);
    		
    		if(!checker) {
    			System.out.println("id "+Integer.toString(input[i])+" FAIL");
    		}else {
    			System.out.println("YEP");
    		}
    		
    	}
    }
    
    public void checkRoutes(int[] input) {
    	int pass = 0;
    	int fail = 0;
    	for(int i=0; i<input.length; i++) {
    		GeomPlanarGraphEdge sEdge = world.idsToEdges.get((int) input[i]);
    		sourceNode = sEdge.getDirEdge(0).getFromNode();
    		for(int j=0; j<input.length; j++) {
    			if(i==j) continue;
    			
    			GeomPlanarGraphEdge dEdge = world.idsToEdges.get((int) input[j]);
    			targetNode = dEdge.getDirEdge(0).getToNode();
    			
    			boolean wut = this.start(world);
    			
    			if(wut) {
    				pass++;
    			}else {
    				fail++;
    			}
    		}
    	}
    	
    	String ps = Integer.toString(pass);
    	String fl = Integer.toString(fail);
    	System.out.println("Pass "+ps+" | "+fl+" Fail");
    }
    
    void setupEdge(GeomPlanarGraphEdge edge){
    	 /** Sets the Agent up to proceed along an Edge
         * @param edge the GeomPlanarGraphEdge to traverse next
         * */

        currentEdge = edge;

        // set up the new segment and index info
        LineString line = edge.getLine();
        segment = new LengthIndexedLine(line);
        startIndex = segment.getStartIndex();
        endIndex = segment.getEndIndex();
        linkDirection = 1;

        // check to ensure that Agent is moving in the right direction
        double distanceToStart = line.getStartPoint().distance(location.geometry), distanceToEnd = line.getEndPoint().distance(location.geometry);
        if (distanceToStart <= distanceToEnd)
        { // closer to start
            currentIndex = startIndex;
            linkDirection = 1;
        } else if (distanceToEnd < distanceToStart)
        { // closer to end
            currentIndex = endIndex;
            linkDirection = -1;
        }
    }

    // move the agent to the given coordinates 
    public void updatePosition(Coordinate c){
        pointMoveTo.setCoordinate(c);
        //location.geometry.apply(pointMoveTo);

        world.agents.setGeometryLocation(location, pointMoveTo);
    }

    // return geometry representing agent location 
    public MasonGeometry getGeometry(){
        return location;
    }

    
    ////////////// OTHER FUNCTIONS //////////////////////////
    
    public void showItinerary() {
    	System.out.print(Integer.toString(patrolID));
    	for(int i=0; i<itinerary.length; i++) {
    		System.out.print("\t"+Integer.toString(itinerary[i]));
    	}
    	System.out.println();
    }
    	
    public String getName() {
    	return this.patrolName;
    } 
    
    public String getLabel() {
    	return getName();
    }
    
    
}