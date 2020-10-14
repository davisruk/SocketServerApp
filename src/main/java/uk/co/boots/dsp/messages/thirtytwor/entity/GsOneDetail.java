package uk.co.boots.dsp.messages.thirtytwor.entity;

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

import lombok.Data;
import uk.co.boots.dsp.messages.base.entity.OrderLine;

@Entity
@Data
@JsonInclude(Include.NON_NULL)
public class GsOneDetail {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	@JsonIgnore
	@OneToOne
    private OrderLine orderLine;
		
	private int numberOfLines;
	
	@OneToMany(mappedBy="gsOneDetail",cascade={CascadeType.ALL})
	private List<GsOneLine> gsOneLines;

	public GsOneDetail() {
		gsOneLines = new ArrayList<GsOneLine>();
	}
	
	public void addGsOneLine(GsOneLine line) {
		line.setGsOneDetail(this);
		gsOneLines.add(line);
	}
	

}
