package programinterface;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.util.ArrayList;

import javax.swing.JPanel;

public class SchedulePanel extends JPanel {
	
	
	private class GridPanel extends JPanel{
		int x;
		int y;
		
		public GridPanel(int a, int b) {
			x=a;
			y=b;
		}
	}
	
	int rows = 0;
	int cols = 0;

	int[][] sched;
	int[][] restrictions;
	
	String[] patNames = {"Alpha", "Bravo", "Charlie", "Delta", "Echo", "Foxtrot", "Golf", "Indigo", "Hotel", "Indigo", "Juliett", "Kilo", "Lima", "Mike", "November", "Oscar", "Papa", "Quebec"};
	
	ArrayList<GridPanel> gridList = new ArrayList<GridPanel>();
	
	public SchedulePanel() {
		this.setLayout(new GridLayout(1,1,1,1));
		this.setBackground(Color.BLACK);
	}
	

	public void updateLayout(int rowCount, int colCount) {
		rows = rowCount;
		cols = colCount;
		
		sched = new int[rows][cols];
		restrictions = new int[rows][cols];
		
		this.setLayout(new GridLayout(rows, cols, 1, 1));
	}
	
	public void clearGrid() {
		gridList.clear();
		this.removeAll();
	}
	
	public void generateGrid() {
		
		clearGrid();
		
		for(int i=0; i<rows; i++) {
			for(int j=0; j<cols; j++) {
				GridPanel gp = new GridPanel(i,j);
				gp.setPreferredSize(new Dimension(50,10));
				if(sched[i][j] > 0) {
					if(restrictions[i][j] > 0) {
						gp.setBackground(Color.ORANGE);
					}
					else {
						gp.setBackground(Color.GREEN);
					}
				}else {
					if(i >= 8 && i < 14) {
						gp.setBackground(new Color(15, 15, 15));
					}else {
						gp.setBackground(Color.WHITE);
					}
				}
				
				gridList.add(gp);
				this.add(gp);
			}
		}
		
	}
	
//	public void clearGrid() {
//		for(int i=0; i<gridList.size(); i++) {
//			gridList.get(i).setBackground(Color.WHITE);
//		}
//	}
	
	///// SET METHODS /////
	
	public void setSched(int[][] input) {
		for(int i=0; i<input.length; i++) {
			for(int j=0; j<input[0].length; j++) {
				sched[j][i] = input[i][j];
			}
		}
	}
	
	public void setRestrictions(int[][] input) {
		for(int i=0; i<input.length; i++) {
			for(int j=0; j<input[0].length; j++) {
				restrictions[j][i] = input[i][j];
			}
		}
	}
	
}
