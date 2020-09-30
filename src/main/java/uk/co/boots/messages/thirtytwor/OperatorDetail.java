package uk.co.boots.messages.thirtytwor;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import lombok.Data;
import lombok.ToString;
import uk.co.boots.messages.shared.OrderLine;

@Data
@ToString(exclude="orderLine")
@Entity
public class OperatorDetail {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	@OneToOne
    private OrderLine orderLine;
		
	private int numberOfLines;
	
	@OneToMany(mappedBy="operatorDetail",cascade={CascadeType.ALL})
	private List<OperatorLine> operatorList;

	public OperatorDetail() {
		operatorList = new ArrayList<OperatorLine>();
	}
	
}
