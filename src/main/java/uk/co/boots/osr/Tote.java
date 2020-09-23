package uk.co.boots.osr;

import lombok.Data;

@Data
public class Tote {
	private String identifier;
	private String twelveN;
	private String twentyTwoN;
	private String thirtyTwoRShort;
	private String thirtyTwoRLong;
	
	public Tote (String initial12NMessage) {
		twelveN = initial12NMessage;
		// temporary whilst we have no 32R short & long generators
		thirtyTwoRShort = twelveN.replace("12N", "32R") + "-short";
		thirtyTwoRLong = twelveN.replace("12N", "32R") + "-long";
		twentyTwoN = "22N" + initial12NMessage.substring(3);
	}
}
