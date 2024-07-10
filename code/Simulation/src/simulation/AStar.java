package simulation;


import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.planargraph.DirectedEdgeStar;
import com.vividsolutions.jts.planargraph.Node;
import java.util.ArrayList;
import java.util.HashMap;
import sim.util.geo.GeomPlanarGraphDirectedEdge;



@SuppressWarnings("restriction")
public class AStar{

	String callerName;
	boolean showMessage = false;
	
	public AStar(String patrolName, boolean debug) {
		// TODO Auto-generated constructor stub
	
	this.showMessage = debug;
	this.callerName = patrolName;
}



public ArrayList<GeomPlanarGraphDirectedEdge> astarPath(Node start, Node goal){
    // initial check
	if (start == null || goal == null){
	    System.out.println("Error: invalid node provided to AStar");
	}
	
//	System.out.println(start.toString());
//	System.out.println(goal.toString());
	
	
	// set up the containers for the result
	ArrayList<GeomPlanarGraphDirectedEdge> result = new ArrayList<GeomPlanarGraphDirectedEdge>();
	
	// containers for the metainformation about the Nodes relative to the
	// A* search
	HashMap<Node, AStarNodeWrapper> foundNodes = new HashMap<Node, AStarNodeWrapper>();
	
	
	
	AStarNodeWrapper startNode = new AStarNodeWrapper(start);
	AStarNodeWrapper goalNode = new AStarNodeWrapper(goal);
	foundNodes.put(start, startNode);
	foundNodes.put(goal, goalNode);
	
	startNode.gx = 0;
	startNode.hx = heuristic(start, goal);
	startNode.fx = heuristic(start, goal);
	
	// A* containers: nodes to be investigated, nodes that have been investigated
	ArrayList<AStarNodeWrapper> closedSet = new ArrayList<AStarNodeWrapper>(); 
	ArrayList<AStarNodeWrapper> openSet = new ArrayList<AStarNodeWrapper>();
	
	openSet.add(startNode);
	
	
	if(showMessage)System.out.println("OpenSet size: "+ Integer.toString(openSet.size()));
	
	// while there are reachable nodes to investigate
	while (openSet.size() > 0){ 
	
		AStarNodeWrapper x = findMin(openSet); // find the shortest path so far
		if (x.node == goal){ // we have found the shortest possible path to the goal!
			
			if(showMessage)System.out.println("reconstruct Path\n");
			// Reconstruct the path and send it back.
			return reconstructPath(goalNode);
		}
		
		openSet.remove(x); // maintain the lists
		closedSet.add(x);
	
		if(showMessage)System.out.println("lists changed");
	
		// check all the edges out from this Node
		DirectedEdgeStar des = x.node.getOutEdges();
		
		if(showMessage)System.out.println("des size: "+des.getEdges().size());
		
		for (Object o : des.getEdges().toArray()){
		    GeomPlanarGraphDirectedEdge l = (GeomPlanarGraphDirectedEdge) o;
		    Node next = null;
		    next = l.getToNode();
		
		    // get the A* meta information about this Node
			AStarNodeWrapper nextNode;
			if (foundNodes.containsKey(next)){
			    nextNode = foundNodes.get(next);
			    if(showMessage)System.out.println("foundNodes contains next");
			    
			} else{
			    nextNode = new AStarNodeWrapper(next);
			    foundNodes.put(next, nextNode);
			    if(showMessage)System.out.println("Next not in foundNodes");
			}
			
			if (closedSet.contains(nextNode)){ // it has already been considered
				if(showMessage)System.out.println("node already in closed set");
				continue;
			}
	
			// otherwise evaluate the cost of this node/edge combo
			double tentativeCost = x.gx + length(l);
			boolean better = false;
			
			if (!openSet.contains(nextNode)) {
				if(showMessage)System.out.println("nextNode not in openSet");
				
			    openSet.add(nextNode);
			    nextNode.hx = heuristic(next, goal);
			    better = true;
			} else if (tentativeCost < nextNode.gx)	{
				if(showMessage)System.out.println("found better path");
				
			    better = true;
			}
	
			// store A* information about this promising candidate node
			if(showMessage)System.out.println("Better? "+better);
	            
			if (better){
                nextNode.cameFrom = x;
                nextNode.edgeFrom = l;
                nextNode.gx = tentativeCost;
                nextNode.fx = nextNode.gx + nextNode.hx;
            }
        }
    }
    return result;
}

////////////////////////////////////////////////////////

/**
 * Takes the information about the given node n and returns the path that
 * found it.
 * @param n the end point of the path
 * @return an ArrayList of GeomPlanarGraphDirectedEdges that lead from the
 * given Node to the Node from which the serach began
 */
ArrayList<GeomPlanarGraphDirectedEdge> reconstructPath(AStarNodeWrapper n)
{
    ArrayList<GeomPlanarGraphDirectedEdge> result = new ArrayList<GeomPlanarGraphDirectedEdge>();
    AStarNodeWrapper x = n;
    while (x.cameFrom != null){
        result.add(0, x.edgeFrom); // add this edge to the front of the list
        x = x.cameFrom;
    }

    return result;
}


/**
 * Measure of the estimated distance between two Nodes. Extremely basic, just
 * Euclidean distance as implemented here.
 * @param x
 * @param y
 * @return notional "distance" between the given nodes.
 */
double heuristic(Node x, Node y){
    Coordinate xnode = x.getCoordinate();
    Coordinate ynode = y.getCoordinate();
//    return Math.sqrt(Math.pow(xnode.x - ynode.x, 2) + Math.pow(xnode.y - ynode.y, 2));
    
    return xnode.distance(ynode);
}



/**
 * @param e
 * @return The length of an edge
 */
double length(GeomPlanarGraphDirectedEdge e){
	
    Coordinate xnode = e.getFromNode().getCoordinate();
    Coordinate ynode = e.getToNode().getCoordinate();
    //return Math.sqrt(Math.pow(xnode.x - ynode.x, 2)+ Math.pow(xnode.y - ynode.y, 2));
    return xnode.distance(ynode);
}



/**
 *  Considers the list of Nodes open for consideration and returns the node
 *  with minimum fx value
 * @param set list of open Nodes
 * @return
 */
AStarNodeWrapper findMin(ArrayList<AStarNodeWrapper> set){
    double min = 100000;
    AStarNodeWrapper minNode = null;
    for (AStarNodeWrapper n : set)
    {
        if (n.fx < min)
        {
            min = n.fx;
            minNode = n;
        }
    }
    return minNode;
}



/**
 * A wrapper to contain the A* meta information about the Nodes
 *
 */
class AStarNodeWrapper{

    // the underlying Node associated with the metainformation
	Node node;
	// the Node from which this Node was most profitably linked
	AStarNodeWrapper cameFrom;
	// the edge by which this Node was discovered
    GeomPlanarGraphDirectedEdge edgeFrom;
    
    double gx;
    double hx;
    double fx;



        public AStarNodeWrapper(Node n){
            node = n;
            gx = 0;
            hx = 0;
            fx = 0;
            cameFrom = null;
            edgeFrom = null;
        }
    }
}
