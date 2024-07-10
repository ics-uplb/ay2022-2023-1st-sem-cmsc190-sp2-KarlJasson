package simulation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

public class PathFinder {

	
	private String resourceFile = "nodeDistances.txt";
	
	// constructor
	public PathFinder() {
		
		/*	TO DO
		 * 	1) Find file of pre calculated paths
		 * 		- of file exists, populate table
		 * 		- proceed with empty table if no file exists or file is empty
		 * 
		 * 	2) Keep a table of the distance between two nodes.
		 * 		- given a pair of nodes, fetch entry from table.
		 * 		- if no entry matches, make new entry and add into table
		 * 		- ideally, file is appended mid simulation and not at end.
		 * 		- A -> B = B -> A? Prepare contingency in case unequal.
		 * */
	}
	
	public double getDistance(int src, int dest) {
		//find in table
		
		return -6.942;
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
	
	
	public double getDistanceBetweenNodes(int src, int dest) {
		
		int intA = 0;
		int intB = 0;
		double distance = -6.942;
		
		try {
    		InputStream strm = this.getFileFromResourceAsStream(resourceFile);
    		
    		BufferedReader br = new BufferedReader(new InputStreamReader(strm)); 
			String str = "";
			
			while((str=br.readLine())!=null && str.length()!=0) { // br reads lines until it encounters End of File
				String[] split = str.trim().split(","); // The entire line is first trimmed of leading and trailing whitspace, then split
				intA = Integer.parseInt(split[0]);
				intB = Integer.parseInt(split[1]);
				
				if((src == intA && dest == intB) || (src == intB && dest == intA)) {
					distance = Double.parseDouble(split[2]);
					break;
				}
			}
			
			br.close();
			
    	}catch(Exception fnf) {
    		System.out.println("ERROR: parameters.txt file not found");
    		System.out.println(fnf.getMessage());
    	}
		return distance;
	}
	
	
	public void addToTable(int inputA, int inputB, int distance ) {
		try {
			StringBuilder builder = new StringBuilder();

			builder.append(Integer.toString(inputA)+",");
			builder.append(Integer.toString(inputB)+",");
			builder.append(Integer.toString(distance)+"\n");
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(resourceFile));
			writer.write(builder.toString());//save Node pairing and their distance as string.
			writer.close();
		}catch(Exception e) {
			System.out.println("(nodeDistances.txt) FILE WRITING ENCOUNTERED AN ERROR");
			
		}
		
	}
}
