package programinterface;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import patrolscheduler.UPFAgent;

public class AgentCard extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	UPFAgent sourceAgent;
	
	
	public AgentCard() {
		this.setLayout(new BorderLayout());
		
	}
	
	public AgentCard(UPFAgent input) {
		this.sourceAgent = input;
		this.setLayout(new BorderLayout());
	}
	
	public void setSource(UPFAgent input) {
		this.sourceAgent = input;
	}
	
	public void buildGUI() {
		this.setMinimumSize(new Dimension(200, 450));
		JPanel header = new JPanel();
		header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
		if(sourceAgent.getAssignedZone() == 1) {
			header.setBackground(new Color(255,51,51)); 	// light red
		}else if(sourceAgent.getAssignedZone() == 2) {
			header.setBackground(new Color(0,255,51));		// light green
		}else if(sourceAgent.getAssignedZone() == 3) {
			header.setBackground(new Color(51,153,255));	// light blue
		}else{
			header.setBackground(Color.GRAY);
		}
		
		
		header.add(Box.createRigidArea(new Dimension(5, 5)));
		JLabel agentName = new JLabel("\""+sourceAgent.getName()+"\"");
		agentName.setFont(new Font("Verdana", Font.PLAIN, 24));
		agentName.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		JLabel agentMileage = new JLabel("Estimated Mileage: "+Integer.toString(sourceAgent.getMileage()));
		agentMileage.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		header.add(Box.createRigidArea(new Dimension(5, 5)));
		header.add(agentName);

		header.add(agentMileage);
		header.add(Box.createRigidArea(new Dimension(200, 10)));
//		header.setPreferredSize(new Dimension(50,70));
		
		this.add(header, BorderLayout.NORTH);
		
		
		JPanel assignmentList = new JPanel();
		assignmentList.setLayout(new BoxLayout(assignmentList, BoxLayout.Y_AXIS));
		
		for(int i=0; i<sourceAgent.getStringList().size(); i++){
			JLabel jl = new JLabel("  "+Integer.toString(i+1)+": "+sourceAgent.getStringList().get(i));
			
			assignmentList.add(jl);
		}
		assignmentList.revalidate();
		this.add(assignmentList, BorderLayout.CENTER);
		
		this.revalidate();
		this.repaint();
	}
	
}
