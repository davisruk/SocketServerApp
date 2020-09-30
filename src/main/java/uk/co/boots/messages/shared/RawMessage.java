package uk.co.boots.messages.shared;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.ToString;

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
}
