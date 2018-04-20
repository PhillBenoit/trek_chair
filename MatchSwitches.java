/*
 * 
 * - Matches switch positions for alternate sounds.
 * 
 * - Returns a string value with the directory name for alternate sounds.
 * 
 * - Switch positions are mapped with -1 (left) 0 (middle) and 1(right)
 *   from top to bottom of the left panel
 * 
 */
public class MatchSwitches {
	static public String match(int speed, int amount) {
		
		// 0  0  0 -1 -1  0  0  0
		if (speed == 255 && amount == 235) return "sw2/";
		
		// 0  0  0  1 -1 -1  0  0
		if (speed == 251 && amount == 207) return "dbz/";
		
		// 0  0  0  1  1  0  0  0
		if (speed == 235 && amount == 255) return "sw1/";
		
		// 1  1 -1 -1  1  1 -1 -1
		if (speed == 204 && amount == 51) return "sailormoon/";
		
		// 1 -1 -1  1 -1  1  1 -1
		if (speed == 154 && amount == 101) return "padoru/";

		//sets default sounds if no match is found
		return "";
	}
}
