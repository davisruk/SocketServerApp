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
import uk.co.boots.messages.shared.Tote;

@Data
@ToString(exclude="tote")
@Entity
public class ToteStatusDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	@OneToOne
    private Tote tote;
	
	private int numberOfLines;
	private int statusLength;
	
	//@OneToMany(mappedBy="statusDetail",cascade={CascadeType.PERSIST,CascadeType.REMOVE})
	@OneToMany(mappedBy="statusDetail",cascade={CascadeType.ALL})
	private List<Status> statusList;
	
	public ToteStatusDetail() {
		statusList = new ArrayList<Status>();
	}
	

	public int getStatusOffset() {
		// we don't count numberOfLines
		return 0;
	}
		
}
