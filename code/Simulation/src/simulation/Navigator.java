package simulation;

import java.util.Random;

public class Navigator {

	Random randomizer = new Random();
	
	public Navigator() {}
	
	public int getFromPatch(int input) {
		
		int[] selection;
		switch(input) {
			case 0:	// CPark
					selection = new int[]{840, 1091, 1157, 1243, 1527, 3879};
					return selection[randomizer.nextInt(selection.length)];
//					//return selection[0];
			
			case 1: // Raymundo Gate/CEM
					selection = new int[]{1925, 2281, 4179, 4234, 4354};
					return selection[randomizer.nextInt(selection.length)];
//					//return selection[0];
					
			case 2: // BioSci
					selection = new int[]{2367, 2644, 2962, 4254, 4404};
					return selection[randomizer.nextInt(selection.length)];
//					//return selection[0];

			case 3: // Main Library
					selection = new int[]{3184, 3355, 3372, 3386, 4599};
					return selection[randomizer.nextInt(selection.length)];
//					//return selection[0];
					
			case 4: // Oblation Park
					selection = new int[]{1802, 1891, 2031, 2070, 2113, 2443};
					return selection[randomizer.nextInt(selection.length)];
					//return selection[0];
				
			case 5: // Palma Bridge
					selection = new int[]{1600, 2162, 2427, 2457, 2551, 2748};
					return selection[randomizer.nextInt(selection.length)];
					//return selection[0];

			case 6: // Silangan Rd/MSI
					selection = new int[]{648, 680, 793};
					return selection[randomizer.nextInt(selection.length)];
					//return selection[0];

			case 7: // Math Building, PhySci
					selection = new int[]{594, 998, 1189, 1401};
					return selection[randomizer.nextInt(selection.length)];
					//return selection[0];
					

			case 8: // Men's / Women's Dorm
					selection = new int[]{2578, 2997, 3148, 3151, 3156, 3401};
					return selection[randomizer.nextInt(selection.length)];
					//return selection[0];

			case 9: // SU
					selection = new int[]{2336, 2828, 2859, 3062, 3066};
					return selection[randomizer.nextInt(selection.length)];
					//return selection[0];

			case 10: // Baker Hall
					selection = new int[]{949, 1364, 1694, 3857};
					return selection[randomizer.nextInt(selection.length)];
					//return selection[0];

			case 11: // AnSci
					selection = new int[]{657, 777, 1485, 3795, 3901, 3937};
					return selection[randomizer.nextInt(selection.length)];
					//return selection[0];

			case 12: // CEAT
					selection = new int[]{248, 307, 548, 657, 3736};
					return selection[randomizer.nextInt(selection.length)];
					//return selection[0];

			case 13: // YMCA/Dorms
					selection = new int[]{2288, 2411, 2878, 3129};
					return selection[randomizer.nextInt(selection.length)];
					//return selection[0];

			case 14: // UH
					selection = new int[]{3403, 3451, 3490, 4624};
					return selection[randomizer.nextInt(selection.length)];
					//return selection[0];

			case 15: // Copeland Housing
					selection = new int[]{3455, 3471, 3503};
					return selection[randomizer.nextInt(selection.length)];
					//return selection[0];

			case 16: // CFNR, Arbor Square
					selection = new int[]{3552, 3559, 3597, 4668};
					return selection[randomizer.nextInt(selection.length)];
					//return selection[0];

			case 17: // Forestry Dorms
					selection = new int[]{3582, 3589, 3591, 4671};
					return selection[randomizer.nextInt(selection.length)];
					//return selection[0];
			
			default: return -1;
		}
	}

	public int getFromPatch() {
		Random r = new Random();
		int num = r.nextInt(4673);
		return num+1;
	}
}
