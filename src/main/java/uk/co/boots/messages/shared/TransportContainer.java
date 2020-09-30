package uk.co.boots.messages.shared;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import lombok.Data;
import lombok.ToString;
import uk.co.boots.messages.BasicRecord;
@Entity
@Data
@ToString(exclude="tote")
public class TransportContainer extends BasicRecord {

	@OneToOne(mappedBy = "transportContainer")
    private Tote tote;

	
	public TransportContainer () {
		super();
		super.setIdentifier('C');
	}	
}
