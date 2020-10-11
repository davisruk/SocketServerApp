package uk.co.boots.dsp.messages.thirtytwor;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;
import lombok.ToString;
import uk.co.boots.dsp.messages.shared.OrderLine;

@Data
@ToString(exclude="orderLine")
@Entity
@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({ "id", "numberOfLines", "operatorList" })
public class OperatorDetail {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	@JsonIgnore
	@OneToOne
    private OrderLine orderLine;
		
	private int numberOfLines;
	
	@OneToMany(mappedBy="operatorDetail",cascade={CascadeType.ALL})
	private List<OperatorLine> operatorList;

	public OperatorDetail() {
		operatorList = new ArrayList<OperatorLine>();
	}
	
	public void addOperatorLine(OperatorLine ol) {
		ol.setOperatorDetail(this);
		operatorList.add(ol);
	}
	
}
