package simulation;

import sim.portrayal.DrawInfo2D;
import sim.portrayal.Inspector;
import sim.portrayal.geo.GeomPortrayal;
import sim.portrayal.geo.GeomVectorFieldPortrayal;
import sim.portrayal.simple.*;
import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics2D;

import com.vividsolutions.jts.io.ParseException;
import org.jfree.data.xy.XYSeries;    // the data series we'll add to


import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.util.media.chart.TimeSeriesChartGenerator;  // the charting facility

public class PatrolsWithUI extends GUIState {

	public XYSeries series;    // the data series we'll add to
    public TimeSeriesChartGenerator chart;  // the charting facility
	
	public Display2D display;
	public JFrame displayFrame;
	public JFrame chartFrame;
	
	private GeomVectorFieldPortrayal campusPortrayal = new GeomVectorFieldPortrayal();
	private GeomVectorFieldPortrayal roadsPortrayal = new GeomVectorFieldPortrayal();
	private GeomVectorFieldPortrayal buildingsPortrayal = new GeomVectorFieldPortrayal();
	private GeomVectorFieldPortrayal patchesPortrayal = new GeomVectorFieldPortrayal();
	private GeomVectorFieldPortrayal agentsPortrayal = new GeomVectorFieldPortrayal();
	
//	private GeomVectorFieldPortrayal debugTargetPortrayal = new GeomVectorFieldPortrayal();
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PatrolsWithUI vid = null;
        try {
        	vid = new PatrolsWithUI();
        } catch (Exception ex) {
        	System.out.println(ex.getMessage());
        }
        
        Console c = new Console(vid);
        c.setVisible(true);
        
  	}
	
	public PatrolsWithUI() throws ParseException {
        super(new Patrols(System.currentTimeMillis()));
        

    }
	
	public PatrolsWithUI(SimState state){
		super(state);
		
//		schedulerUI = new ProgramInterface();
	}
	
	public static String getName() {
		return "OCSG Scheduler/Simulation";
	}

	public void start() {
		super.start();
		setupPortrayals();
	}
	
	public void load(SimState state) {
		super.load(state);
		setupPortrayals();
	}
	
	public void setupPortrayals() {
		Patrols patrols = (Patrols) state;
		
//		
//		patchesPortrayal.setField(patrols.patches);
//		patchesPortrayal.setPortrayalForAll(new GeomPortrayal(new Color(0,255,51,180), true));
		
		buildingsPortrayal.setField(patrols.buildings);
		buildingsPortrayal.setPortrayalForAll(new GeomPortrayal(Color.DARK_GRAY, true));
		
//		campusPortrayal.setField(patrols.misc);
//		campusPortrayal.setPortrayalForAll(new GeomPortrayal(Color.LIGHT_GRAY, true));
		
		roadsPortrayal.setField(patrols.roads);
		roadsPortrayal.setPortrayalForAll(new GeomPortrayal(Color.BLACK, true));
		
		GeomPortrayal customPortrayal = new GeomPortrayal(Color.BLUE, 0.0001, true);
		LabelledPortrayal2D labelPort = new LabelledPortrayal2D(null, null);
		labelPort.setLabelScaling(2);
		
		System.out.println("scaling value: "+ Integer.toString(labelPort.getLabelScaling()));
		
		agentsPortrayal.setField(patrols.agents);
		agentsPortrayal.setPortrayalForAll(customPortrayal);
//		agentsPortrayal.setPortrayalForAll(new LabelledPortrayal2D(customPortrayal, null, Color.BLACK, false));
        
		display.reset();
		display.setBackdrop(Color.white);
		
		display.repaint();
	}
	
	public void init(Controller c) {
		super.init(c);
		display = new Display2D(600,600,this);
		
		display.setScale(1.0);
		
		display.setScrollPosition(0.0, 0.0);
		display.setClipping(false);

		display.attach(patchesPortrayal, "Patches Layer");
		display.attach(buildingsPortrayal, "Buildings Layer");
		display.attach(campusPortrayal, "Campus Map");
		display.attach(roadsPortrayal, "Roads Layer");
		
//		display.attach(debugTargetPortrayal, "debug Target");
		
		display.attach(agentsPortrayal, "Patrols");
		
		displayFrame = display.createFrame();
		displayFrame.setTitle("Campus Map");
		c.registerFrame(displayFrame);
		displayFrame.setVisible(true);
	       
	      
	}
	
	public void quit() {
		super.quit();
		if (displayFrame != null) {
			displayFrame.dispose();
		}
		displayFrame = null;
		display = null;
	}
	
	public Object getSimulationInspectedObject(){ 
		return state; 
	}
	
	public Inspector getInspector(){
		Inspector i = super.getInspector();
		i.setVolatile(true);
		return i;
	}
	
}
