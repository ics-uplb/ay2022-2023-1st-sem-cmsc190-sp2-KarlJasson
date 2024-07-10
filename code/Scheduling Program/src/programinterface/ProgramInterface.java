package programinterface;


import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

import patrolscheduler.MainProgram;
import patrolscheduler.UPFAgent;
import javax.swing.border.EtchedBorder;

public class ProgramInterface {
	
	private JFrame frmOcsgScheduler;
	
	private int intervalLength;
	private int numOfIntervals;
	private int numOfPatches;
	
	private int z1patrols;
	private int z2patrols;
	private int z3patrols;
	
	MainProgram scheduler;
	private int[][] guiSched;
	private int[][] flags;
	private int[][] patrolAssignments;

	private String[] patchNames;
	private int[] intLengths;
	private int[] presetProbabilities;
	private int[] userProbabilities;
	
	private int[] sampleFlags = {1,1,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,1};
	
	/**
	 * Launch the application.
	 */
 	public static void main(String[] args) {
		
		////////////////////
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		}
		///*//////
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ProgramInterface window = new ProgramInterface();
					window.frmOcsgScheduler.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ProgramInterface() {
		
		// default value at 10
		intervalLength = 10;	
		
		// must be initialized with default value 1
		numOfIntervals = 1;		

		// 
		z1patrols = 0;			
		z2patrols = 0;
		z3patrols = 0;
		
		// used by the program GUI
		
		scheduler = new MainProgram();
//		scheduler.generateMap();
		
//		scheduler.generateSchedule();
//		scheduler.viewSchedule(true);
		
		patchNames = scheduler.getPatchNames();
		numOfPatches = scheduler.getPatchCount();
		
		presetProbabilities = scheduler.getPresetFrequencies();
		userProbabilities = new int[presetProbabilities.length];
		
		intLengths = new int[] {10, 15, 20, 30, 60};
	
		
		initialize();
	}

	
	public static String removeLastCharacter(String str) {
		str = str.stripTrailing();
		
		String result = Optional.ofNullable(str)
		.filter(sStr -> sStr.length() != 0)
		.map(sStr -> sStr.substring(0, sStr.length() - 1))
		.orElse(str);
		
		return result;
	}
	
	private void initialize() {
		
		final SchedulePanel customschedpanel = new SchedulePanel();
		
		// initialize JFrame
		frmOcsgScheduler = new JFrame();
		frmOcsgScheduler.setTitle("OCSG Scheduler");
		frmOcsgScheduler.setBounds(100, 100, 900, 600);
		frmOcsgScheduler.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmOcsgScheduler.getContentPane().setLayout(null);
		
		// initialize TabbedPane
		JTabbedPane primaryTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		primaryTabbedPane.setBounds(0, 0, 884, 561);
		frmOcsgScheduler.getContentPane().add(primaryTabbedPane);
		
		//// PARAMETERS panel elements ///////////////////////////////////////
		JPanel parameterspanel = new JPanel();
		primaryTabbedPane.addTab("Get Started", null, parameterspanel, null);
		parameterspanel.setLayout(null);

		JPanel mapdisplaypanel = new JPanel();
		mapdisplaypanel.setBackground(Color.BLACK);
		mapdisplaypanel.setBounds(397, 56, 440, 440);
		mapdisplaypanel.setLayout(new BorderLayout(0, 0));
		
		try {
			BufferedImage myPicture = ImageIO.read(new File("./resources/campuszones_uplb.jpg"));
			JLabel piclabel = new JLabel(new ImageIcon(myPicture));
			mapdisplaypanel.add(piclabel, BorderLayout.CENTER);
		}catch(Exception ex) {
			System.out.println("wrong file path!");
			mapdisplaypanel.setToolTipText("File not found");
		}
		
		parameterspanel.add(mapdisplaypanel);
		
		JComboBox intervalcomboBox = new JComboBox();
		intervalcomboBox.setModel(new DefaultComboBoxModel(new String[] {"10 minutes", "15 minutes", "20 minutes", "30 minutes", "60 minutes"}));
		intervalcomboBox.setBounds(200, 126, 123, 33);
		parameterspanel.add(intervalcomboBox);
		
		JSpinner intervalspinner = new JSpinner();
		intervalspinner.setModel(new SpinnerNumberModel(1,1,null,1));
		intervalspinner.setBounds(200, 81, 123, 33);
		parameterspanel.add(intervalspinner);
		
		JLabel intervalLbl1 = new JLabel("Number of Intervals");
		intervalLbl1.setHorizontalAlignment(SwingConstants.RIGHT);
		intervalLbl1.setBounds(74, 86, 114, 24);
		parameterspanel.add(intervalLbl1);
		
		JLabel intervalLbl2 = new JLabel("Interval Length");
		intervalLbl2.setHorizontalAlignment(SwingConstants.RIGHT);
		intervalLbl2.setBounds(100, 130, 88, 24);
		parameterspanel.add(intervalLbl2);
		
		JSpinner z1spinner = new JSpinner();
		z1spinner.setModel(new SpinnerNumberModel(0, 0, 8, 1));
		z1spinner.setBounds(264, 255, 59, 33);
		parameterspanel.add(z1spinner);
		
		JSpinner z2spinner = new JSpinner();
		z2spinner.setModel(new SpinnerNumberModel(0, 0, 6, 1));
		z2spinner.setBounds(264, 300, 59, 33);
		parameterspanel.add(z2spinner);
		
		JSpinner z3spinner = new JSpinner();
		z3spinner.setModel(new SpinnerNumberModel(0, 0, 4, 1));
		z3spinner.setBounds(264, 345, 59, 33);
		parameterspanel.add(z3spinner);
		
		JLabel z1Lbl = new JLabel("Zone 1 (Red)");
		z1Lbl.setHorizontalAlignment(SwingConstants.RIGHT);
		z1Lbl.setBounds(104, 264, 148, 16);
		parameterspanel.add(z1Lbl);
		
		JLabel z2Lbl = new JLabel("Zone 2 (Green)");
		z2Lbl.setHorizontalAlignment(SwingConstants.RIGHT);
		z2Lbl.setBounds(104, 309, 148, 16);
		parameterspanel.add(z2Lbl);
		
		JLabel z3Lbl = new JLabel("Zone 3 (Blue)");
		z3Lbl.setHorizontalAlignment(SwingConstants.RIGHT);
		z3Lbl.setBounds(104, 354, 148, 16);
		parameterspanel.add(z3Lbl);
		
		JLabel durationLbl = new JLabel("Total Time: 0 hours, 0 minutes");
		durationLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		durationLbl.setBounds(74, 171, 249, 24);
		parameterspanel.add(durationLbl);
		
		JButton btnConfirm = new JButton("Confirm");
		btnConfirm.setFont(new Font("SansSerif", Font.BOLD, 12));
		btnConfirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				numOfIntervals = Integer.parseInt(intervalspinner.getValue().toString());
				
				intervalLength = intLengths[intervalcomboBox.getSelectedIndex()];
				
				z1patrols = Integer.parseInt(z1spinner.getValue().toString());
				z2patrols = Integer.parseInt(z2spinner.getValue().toString());
				z3patrols = Integer.parseInt(z3spinner.getValue().toString());
				
				String hours = Integer.toString((numOfIntervals * intervalLength) / 60);
				String mins = Integer.toString((numOfIntervals * intervalLength) % 60);
				durationLbl.setText("Total Time: "+hours+" hours, "+mins+" minutes");
				
				intervalspinner.setEnabled(false);
				intervalcomboBox.setEnabled(false);
				z1spinner.setEnabled(false);
				z2spinner.setEnabled(false);
				z3spinner.setEnabled(false);
				
				customschedpanel.updateLayout(numOfPatches, numOfIntervals);
//				customschedpanel.generateGrid();
				
				if(scheduler == null) {
					System.out.println("NOOB MF");
					System.exit(0);
				}
				scheduler.setParameters(numOfIntervals, intervalLength, z1patrols, z2patrols, z3patrols);

				btnConfirm.setEnabled(false);
//				intervalLength = interval
			}
		});
		btnConfirm.setBounds(217, 426, 130, 33);
		parameterspanel.add(btnConfirm);
		
		JPanel patrolsDisplayPanel = new JPanel();
		patrolsDisplayPanel.setLayout(new BoxLayout(patrolsDisplayPanel, BoxLayout.X_AXIS));
		patrolsDisplayPanel.setBackground(Color.BLACK);
		
		JButton btnReset = new JButton("Reset");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				numOfIntervals = 1;
				
				intervalLength = 10;
				
				z1patrols = 0;
				z2patrols = 0;
				z3patrols = 0;
				
				intervalspinner.setEnabled(true);
				intervalcomboBox.setEnabled(true);
				z1spinner.setEnabled(true);
				z2spinner.setEnabled(true);
				z3spinner.setEnabled(true);
				
				intervalspinner.setValue(1);
				intervalcomboBox.setSelectedIndex(0);
				
				z1spinner.setValue(0);
				z2spinner.setValue(0);
				z3spinner.setValue(0);
				
				customschedpanel.removeAll();
				patrolsDisplayPanel.removeAll();				
				
				btnConfirm.setEnabled(true);
			}
		});
		btnReset.setBounds(74, 426, 130, 33);
		parameterspanel.add(btnReset);
		
		

		////////////////////////////
		
		JPanel probabilitiespanel = new JPanel();
		primaryTabbedPane.addTab("Probabilities", null, probabilitiespanel, null);
		probabilitiespanel.setLayout(null);
		
		JPanel zone1Section = new JPanel();
		zone1Section.setBorder(new TitledBorder(null, "ZONE 1", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(59, 59, 59)));
		zone1Section.setBounds(37, 18, 264, 431);
		probabilitiespanel.add(zone1Section);
		zone1Section.setLayout(null);
		
		JLabel patchLabel = new JLabel("Carabao Park");
		patchLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		patchLabel.setBounds(6, 20, 133, 37);
		zone1Section.add(patchLabel);
		
		JLabel patchLabel_1 = new JLabel("CEM/Raymundo Gate");
		patchLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
		patchLabel_1.setBounds(6, 69, 133, 37);
		zone1Section.add(patchLabel_1);
		
		JLabel patchLabel_2 = new JLabel("BioSci");
		patchLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
		patchLabel_2.setBounds(6, 118, 133, 37);
		zone1Section.add(patchLabel_2);
		
		JLabel patchLabel_3 = new JLabel("Main Library");
		patchLabel_3.setHorizontalAlignment(SwingConstants.RIGHT);
		patchLabel_3.setBounds(6, 167, 133, 37);
		zone1Section.add(patchLabel_3);
		
		JLabel patchLabel_4 = new JLabel("Oblation Park");
		patchLabel_4.setHorizontalAlignment(SwingConstants.RIGHT);
		patchLabel_4.setBounds(6, 216, 133, 37);
		zone1Section.add(patchLabel_4);
		
		JLabel patchLabel_5 = new JLabel("Palma Bridge");
		patchLabel_5.setHorizontalAlignment(SwingConstants.RIGHT);
		patchLabel_5.setBounds(6, 265, 133, 37);
		zone1Section.add(patchLabel_5);
		
		JLabel patchLabel_6 = new JLabel("CSI");
		patchLabel_6.setHorizontalAlignment(SwingConstants.RIGHT);
		patchLabel_6.setBounds(6, 314, 133, 37);
		zone1Section.add(patchLabel_6);
		
		JLabel patchLabel_7 = new JLabel("Math Building");
		patchLabel_7.setHorizontalAlignment(SwingConstants.RIGHT);
		patchLabel_7.setBounds(6, 359, 133, 37);
		zone1Section.add(patchLabel_7);
		
		JSpinner probSpinner = new JSpinner();
		probSpinner.setBounds(151, 24, 51, 28);
		probSpinner.setModel(new SpinnerNumberModel(0,0,null,1));
		probSpinner.setValue(presetProbabilities[0]);
		zone1Section.add(probSpinner);

		JSpinner probSpinner_1 = new JSpinner();
		probSpinner_1.setBounds(151, 73, 51, 28);
		probSpinner_1.setModel(new SpinnerNumberModel(0,0,null,1));
		probSpinner_1.setValue(presetProbabilities[1]);
		zone1Section.add(probSpinner_1);
		
		JSpinner probSpinner_2 = new JSpinner();
		probSpinner_2.setBounds(151, 122, 51, 28);
		probSpinner_2.setModel(new SpinnerNumberModel(0,0,null,1));
		probSpinner_2.setValue(presetProbabilities[2]);
		zone1Section.add(probSpinner_2);

		JSpinner probSpinner_3 = new JSpinner();
		probSpinner_3.setBounds(151, 171, 51, 28);
		probSpinner_3.setModel(new SpinnerNumberModel(0,0,null,1));
		probSpinner_3.setValue(presetProbabilities[3]);
		zone1Section.add(probSpinner_3);
		
		JSpinner probSpinner_4 = new JSpinner();
		probSpinner_4.setBounds(151, 220, 51, 28);
		probSpinner_4.setModel(new SpinnerNumberModel(0,0,null,1));
		probSpinner_4.setValue(presetProbabilities[4]);
		zone1Section.add(probSpinner_4);
		
		JSpinner probSpinner_5 = new JSpinner();
		probSpinner_5.setBounds(151, 269, 51, 28);
		probSpinner_5.setModel(new SpinnerNumberModel(0,0,null,1));
		probSpinner_5.setValue(presetProbabilities[5]);
		zone1Section.add(probSpinner_5);
		
		JSpinner probSpinner_6 = new JSpinner();
		probSpinner_6.setBounds(151, 318, 51, 28);
		probSpinner_6.setModel(new SpinnerNumberModel(0,0,null,1));
		probSpinner_6.setValue(presetProbabilities[6]);
		zone1Section.add(probSpinner_6);
		
		JSpinner probSpinner_7 = new JSpinner();
		probSpinner_7.setBounds(151, 363, 51, 28);
		probSpinner_7.setModel(new SpinnerNumberModel(0,0,null,1));
		probSpinner_7.setValue(presetProbabilities[7]);
		zone1Section.add(probSpinner_7);
		
		
		///////////////////
		
		JPanel zone2Section = new JPanel();
		zone2Section.setBorder(new TitledBorder(null, "ZONE 2", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(59, 59, 59)));
		zone2Section.setBounds(318, 18, 264, 431);
		probabilitiespanel.add(zone2Section);
		zone2Section.setLayout(null);
		
		JLabel patchLabel_8 = new JLabel("DL Umali Hall");
		patchLabel_8.setHorizontalAlignment(SwingConstants.RIGHT);
		patchLabel_8.setBounds(6, 20, 133, 37);
		zone2Section.add(patchLabel_8);
		
		JLabel patchLabel_9 = new JLabel("Student Union Building");
		patchLabel_9.setBounds(6, 69, 133, 37);
		zone2Section.add(patchLabel_9);
		patchLabel_9.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JLabel patchLabel_10 = new JLabel("Baker Hall");
		patchLabel_10.setHorizontalAlignment(SwingConstants.RIGHT);
		patchLabel_10.setBounds(6, 118, 133, 37);
		zone2Section.add(patchLabel_10);
		
		JLabel patchLabel_11 = new JLabel("Animal Science Area");
		patchLabel_11.setHorizontalAlignment(SwingConstants.RIGHT);
		patchLabel_11.setBounds(6, 167, 133, 37);
		zone2Section.add(patchLabel_11);
		
		JLabel patchLabel_12 = new JLabel("CEAT");
		patchLabel_12.setHorizontalAlignment(SwingConstants.RIGHT);
		patchLabel_12.setBounds(6, 216, 133, 37);
		zone2Section.add(patchLabel_12);
		
		JLabel patchLabel_13 = new JLabel("YMCA");
		patchLabel_13.setHorizontalAlignment(SwingConstants.RIGHT);
		patchLabel_13.setBounds(6, 265, 133, 37);
		zone2Section.add(patchLabel_13);
		
		JSpinner probSpinner_8 = new JSpinner();
		probSpinner_8.setBounds(151, 24, 51, 28);
		probSpinner_8.setModel(new SpinnerNumberModel(0,0,null,1));
		probSpinner_8.setValue(presetProbabilities[8]);
		zone2Section.add(probSpinner_8);
		
		JSpinner probSpinner_9 = new JSpinner();
		probSpinner_9.setBounds(151, 73, 51, 28);
		probSpinner_9.setModel(new SpinnerNumberModel(0,0,null,1));
		probSpinner_9.setValue(presetProbabilities[9]);
		zone2Section.add(probSpinner_9);
		
		JSpinner probSpinner_10 = new JSpinner();
		probSpinner_10.setBounds(151, 122, 51, 28);
		probSpinner_10.setModel(new SpinnerNumberModel(0,0,null,1));
		probSpinner_10.setValue(presetProbabilities[10]);
		zone2Section.add(probSpinner_10);
		
		JSpinner probSpinner_11 = new JSpinner();
		probSpinner_11.setBounds(151, 171, 51, 28);
		probSpinner_11.setModel(new SpinnerNumberModel(0,0,null,1));
		probSpinner_11.setValue(presetProbabilities[11]);
		zone2Section.add(probSpinner_11);
		
		JSpinner probSpinner_12 = new JSpinner();
		probSpinner_12.setBounds(151, 220, 51, 28);
		probSpinner_12.setModel(new SpinnerNumberModel(0,0,null,1));
		probSpinner_12.setValue(presetProbabilities[12]);
		zone2Section.add(probSpinner_12);
		
		JSpinner probSpinner_13 = new JSpinner();
		probSpinner_13.setBounds(151, 269, 51, 28);
		probSpinner_13.setModel(new SpinnerNumberModel(0,0,null,1));
		probSpinner_13.setValue(presetProbabilities[13]);
		zone2Section.add(probSpinner_13);
		
		////////////////////////
		
		JPanel zone3Section = new JPanel();
		zone3Section.setBorder(new TitledBorder(null, "ZONE 3", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		zone3Section.setBounds(599, 18, 264, 431);
		probabilitiespanel.add(zone3Section);
		zone3Section.setLayout(null);
		
		JLabel patchLabel_14 = new JLabel("Univ. Health Service");
		patchLabel_14.setHorizontalAlignment(SwingConstants.RIGHT);
		patchLabel_14.setBounds(6, 20, 133, 37);
		zone3Section.add(patchLabel_14);
		
		JLabel patchLabel_15 = new JLabel("Splash Properties");
		patchLabel_15.setHorizontalAlignment(SwingConstants.RIGHT);
		patchLabel_15.setBounds(6, 69, 133, 37);
		zone3Section.add(patchLabel_15);
		
		JLabel patchLabel_16 = new JLabel("CFNR");
		patchLabel_16.setHorizontalAlignment(SwingConstants.RIGHT);
		patchLabel_16.setBounds(6, 118, 133, 37);
		zone3Section.add(patchLabel_16);
		
		JLabel patchLabel_17 = new JLabel("Forestry Dorms");
		patchLabel_17.setHorizontalAlignment(SwingConstants.RIGHT);
		patchLabel_17.setBounds(6, 167, 133, 37);
		zone3Section.add(patchLabel_17);
		
		JSpinner probSpinner_14 = new JSpinner();
		probSpinner_14.setBounds(151, 24, 51, 28);
		probSpinner_14.setModel(new SpinnerNumberModel(0,0,null,1));
		probSpinner_14.setValue(presetProbabilities[14]);
		zone3Section.add(probSpinner_14);
		
		JSpinner probSpinner_15 = new JSpinner();
		probSpinner_15.setBounds(151, 73, 51, 28);
		probSpinner_15.setModel(new SpinnerNumberModel(0,0,null,1));
		probSpinner_15.setValue(presetProbabilities[15]);
		zone3Section.add(probSpinner_15);
		
		
		JSpinner probSpinner_16 = new JSpinner();
		probSpinner_16.setBounds(151, 122, 51, 28);
		probSpinner_16.setModel(new SpinnerNumberModel(0,0,null,1));
		probSpinner_16.setValue(presetProbabilities[16]);
		zone3Section.add(probSpinner_16);
		
		JSpinner probSpinner_17 = new JSpinner();
		probSpinner_17.setBounds(151, 171, 51, 28);
		probSpinner_17.setModel(new SpinnerNumberModel(0,0,null,1));
		probSpinner_17.setValue(presetProbabilities[17]);
		zone3Section.add(probSpinner_17);
		
		JButton btnNewButton = new JButton("Load Preset");
		btnNewButton.setBounds(775, 481, -179, 28);
		probabilitiespanel.add(btnNewButton);
		
		JButton btnLoadPreset = new JButton("Load Preset");
		btnLoadPreset.setBounds(580, 481, 133, 28);
		probabilitiespanel.add(btnLoadPreset);
		
		JButton btnSavePreset = new JButton("Save Preset");
		btnSavePreset.setBounds(730, 481, 133, 28);
		probabilitiespanel.add(btnSavePreset);
		
		//// End for PARAMETERS panel elements //////////////////////

		//// SCHEDULE elements ////////////////////////////
		
		JPanel schedulesPanel = new JPanel();
		primaryTabbedPane.addTab("Schedule", null, schedulesPanel, null);
		schedulesPanel.setLayout(null);
		
		JPanel schedCardPanel = new JPanel();
		schedCardPanel.setBounds(6, 62, 872, 463);
		schedulesPanel.add(schedCardPanel);
		schedCardPanel.setLayout(new CardLayout(0, 0));
		
		JPanel asListPanel = new JPanel();
		schedCardPanel.add(asListPanel, "VIEW_AS_LIST");
		asListPanel.setLayout(null);
		
		JScrollPane listScrollPane = new JScrollPane();
		listScrollPane.setBounds(0, 0, 872, 457);
		asListPanel.add(listScrollPane);
		
		JTextArea listDisplayField = new JTextArea();
		listScrollPane.setViewportView(listDisplayField);
		
		JPanel asGridPanel = new JPanel();
		schedCardPanel.add(asGridPanel, "VIEW_AS_GRID");
		asGridPanel.setLayout(null);
		
		JPanel headersPanel = new JPanel();
		headersPanel.setBounds(6, 16, 129, 421);
		headersPanel.setLayout(new GridLayout(18, 1, 0, 0));
		
		for(int i=0; i<patchNames.length; i++) {
			JLabel jl = new JLabel(patchNames[i], SwingConstants.RIGHT);
			headersPanel.add(jl);
		}
		
		asGridPanel.add(headersPanel);
		
		UIManager.put("ScrollBar.height", 5);
		JScrollPane gridScrollPane = new JScrollPane();
		gridScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		gridScrollPane.setBounds(138, 6, 728, 451);
		
		gridScrollPane.setViewportView(customschedpanel);
		asGridPanel.add(gridScrollPane);
		
		JPanel schedHeadPanel = new JPanel();
		schedHeadPanel.setLayout(null);
		schedHeadPanel.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		schedHeadPanel.setBounds(6, 6, 872, 44);
		schedulesPanel.add(schedHeadPanel);
		
		JButton btnViewAsGrid = new JButton("View as Grid/List");
		btnViewAsGrid.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout)(schedCardPanel.getLayout());
				cl.next(schedCardPanel);
			}
		});
		btnViewAsGrid.setBounds(732, 6, 134, 28);
		schedHeadPanel.add(btnViewAsGrid);
		
		JButton btnGenerateSched = new JButton("Generate Schedule");
		btnGenerateSched.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				updateProbs(0, probSpinner);
				updateProbs(1, probSpinner_1);
				updateProbs(2, probSpinner_2);
				updateProbs(3, probSpinner_3);
				updateProbs(4, probSpinner_4);
				updateProbs(5, probSpinner_5);
				updateProbs(6, probSpinner_6);
				updateProbs(7, probSpinner_7);
				updateProbs(8, probSpinner_8);
				updateProbs(9, probSpinner_9);
				updateProbs(10, probSpinner_10);
				updateProbs(11, probSpinner_11);
				updateProbs(12, probSpinner_12);
				updateProbs(13, probSpinner_13);
				updateProbs(14, probSpinner_14);
				updateProbs(15, probSpinner_15);
				updateProbs(16, probSpinner_16);
				updateProbs(17, probSpinner_17);
				
				
				scheduler.updateFrequencies(userProbabilities);
				scheduler.clearSchedule();
				scheduler.clearCount();
//				scheduler.clearFlags();
				scheduler.generateSchedule();
				scheduler.clearAssignments();
				scheduler.setAssignments();
				
				scheduler.writeForSimulator();
				
				listDisplayField.setText(generateScheduleList());
				customschedpanel.setSched(guiSched);
				
				customschedpanel.setRestrictions(scheduler.getRestrictions());
				customschedpanel.generateGrid();
				customschedpanel.revalidate();
				
			}
		});
		btnGenerateSched.setBounds(6, 6, 134, 28);
		schedHeadPanel.add(btnGenerateSched);
		
		JButton btnSetRestrictions = new JButton("Set Restrictions");
		btnSetRestrictions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnSetRestrictions.setBounds(143, 6, 134, 28);
		schedHeadPanel.add(btnSetRestrictions);
		
		
		//////////////////////////////////////////
		
		
		JPanel patrolsPanel = new JPanel();
		primaryTabbedPane.addTab("Patrols", null, patrolsPanel, null);
		patrolsPanel.setLayout(null);
		
		JPanel patrolsHeaderPanel = new JPanel();
		patrolsHeaderPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		patrolsHeaderPanel.setBounds(6, 6, 872, 46);
		patrolsPanel.add(patrolsHeaderPanel);
		patrolsHeaderPanel.setLayout(null);
		
		JScrollPane patrolsScrollPane = new JScrollPane();
		patrolsScrollPane.setBounds(6, 60, 872, 465);
		patrolsPanel.add(patrolsScrollPane);
		
		
		
		patrolsScrollPane.setViewportView(patrolsDisplayPanel);
		
		JButton viewPatrolsBtn = new JButton("View/Refresh List");
		viewPatrolsBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				patrolsDisplayPanel.removeAll();
				patrolsDisplayPanel.add(Box.createRigidArea(new Dimension(8, 8)));
				for(int i=0; i<scheduler.getTotalPatrols(); i++) {
					AgentCard ac = new AgentCard(scheduler.getPatrol(i));
					
					ac.buildGUI();
					
					patrolsDisplayPanel.add(ac);
					patrolsDisplayPanel.add(Box.createRigidArea(new Dimension(8, 8)));
					
				}
				
				patrolsDisplayPanel.setPreferredSize(patrolsDisplayPanel.getPreferredSize());
				patrolsDisplayPanel.revalidate();
			}
		});
		
		viewPatrolsBtn.setBounds(6, 6, 169, 28);
		patrolsHeaderPanel.add(viewPatrolsBtn);
	
		
	}

	public String generateScheduleList() { 
		guiSched = scheduler.getSchedule();
		
		String megaString = "";
		StringBuilder sb = new StringBuilder(megaString);
		
		for(int i=0; i<guiSched.length; i++) {
			int[] subArray = guiSched[i];
			String head = "Interval "+Integer.toString(i)+":\n";
			String z1str = "Zone 1: ";
			String z2str = "Zone 2: ";
			String z3str = "Zone 3: ";
			for(int j=0; j<subArray.length; j++) {
				if(subArray[j] > 0) {
					if(j < 8) {
						z1str += patchNames[j]+", ";
					}else if(j >= 8 && j < 14) {
						z2str += patchNames[j]+", ";
					}else {
						z3str += patchNames[j]+", ";
					}
				}
				
			}
			
			
			sb.append(head);
			sb.append(removeLastCharacter(z1str) + "\n");
			sb.append(removeLastCharacter(z2str) + "\n");
			sb.append(removeLastCharacter(z3str) + "\n");
			sb.append("\n");
			
			
		}
		int[] visits = scheduler.getVisitCount();
		sb.append("Number of visits per patch:\n");
		for(int i=0; i<visits.length; i++) {
			sb.append(patchNames[i]+": "+Integer.toString(visits[i])+"\n");
		}
		
		
		megaString = sb.toString();
		return megaString;
	
		
	}
	
	
	public void updateProbs(int a, JSpinner js) {
		userProbabilities[a] = Integer.parseInt(js.getValue().toString());
	}
}