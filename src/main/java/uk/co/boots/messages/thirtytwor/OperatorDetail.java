package uk.co.boots.messages.thirtytwor;

import java.util.ArrayList;

import lombok.Data;

@Data
public class OperatorArrayList extends ArrayList<OperatorLine> {
	private int numberOfLines;
	
}
