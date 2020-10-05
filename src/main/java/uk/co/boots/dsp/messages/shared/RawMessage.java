package uk.co.boots.dsp.messages.shared;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Data
@ToString(exclude="tote")
@Entity
public class RawMessage {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String messageType;
    @Column(columnDefinition="TEXT")	
	private String message;

	@ManyToOne (cascade={CascadeType.ALL})
	@JoinColumn (name="tote_id")
	private Tote tote;
	
    @Temporal(TemporalType.TIMESTAMP)
    Date creationDateTime;
    
	public RawMessage (String messageType, String message, Date creationTime) {
		this.message = message;
		this.messageType = messageType;
		this.creationDateTime = creationTime;
	}
    
}
