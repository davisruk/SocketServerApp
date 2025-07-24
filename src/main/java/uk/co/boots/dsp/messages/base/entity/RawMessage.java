package uk.co.boots.dsp.messages.base.entity;

import java.util.Date;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

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
