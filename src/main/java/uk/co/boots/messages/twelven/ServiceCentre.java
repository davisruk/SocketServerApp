package uk.co.boots.messages.twelven;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import lombok.Data;
import lombok.ToString;
import uk.co.boots.messages.BasicRecord;
import uk.co.boots.messages.shared.Tote;

@Entity
@Data
@ToString(exclude="tote")
public class ServiceCentre extends BasicRecord {

	@OneToOne(mappedBy = "serviceCentre")
    private Tote tote;
    
	public ServiceCentre() {
		super();
		super.setIdentifier('E');
	}
}
