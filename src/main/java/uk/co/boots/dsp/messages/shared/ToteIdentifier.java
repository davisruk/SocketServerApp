package uk.co.boots.dsp.messages.shared;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.ToString;
import uk.co.boots.dsp.messages.BasicRecord;

@Entity
@Data
@ToString(exclude="tote")
@JsonInclude(Include.NON_NULL)
public class ToteIdentifier extends BasicRecord {

	@Transient
	public static String MANUAL_TOTE = "01"; //CPNA (Central Pharmacy Not Automated)
	@Transient
	public static String ADAPTED_TOTE = "02"; // CPS (Central Pharmacy Split/ Sortable)
	@Transient
	public static String EMPTY_TOTE = "03"; // Empty Order
	@Transient
	public static String ASSOCIATED_TOTE = "04"; // CPC (Central Pharmacy Constituent) 
	@Transient
	public static String FULLPACK_TOTE = "05"; // CPF (Central Pharmacy Fully Automated)

	@JsonIgnore
	@OneToOne(mappedBy = "toteIdentifier")
    private Tote tote;
	
	public ToteIdentifier(){
		super();
		super.setIdentifier('T');
	}
}
