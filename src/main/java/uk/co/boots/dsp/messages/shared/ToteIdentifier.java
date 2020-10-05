package uk.co.boots.dsp.messages.shared;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import lombok.Data;
import lombok.ToString;
import uk.co.boots.dsp.messages.BasicRecord;

@Entity
@Data
@ToString(exclude="tote")
public class ToteIdentifier extends BasicRecord {

	@OneToOne(mappedBy = "toteIdentifier")
    private Tote tote;
	
	public ToteIdentifier(){
		super();
		super.setIdentifier('T');
	}
}
