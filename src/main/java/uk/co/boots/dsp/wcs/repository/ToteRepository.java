package uk.co.boots.dsp.wcs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import uk.co.boots.dsp.messages.shared.OrderLine;
import uk.co.boots.dsp.messages.shared.Tote;

public interface ToteRepository extends PagingAndSortingRepository<Tote, Long> {
	@Query(
             "SELECT t " +
             "FROM Tote t " +
             "LEFT JOIN t.toteIdentifier ti " +            		 
             "LEFT JOIN t.orderDetail od " +
             "LEFT JOIN od.orderLines ol " +
             "WHERE ol.orderLineNumber = :orderLineNumber " +
             "AND ti.payload = :toteOrderType"
     )
	public List<Tote> findRelatedToteForOrderLine(@Param("orderLineNumber") String orderLineNumber, @Param("toteOrderType") String toteOrderType);
	
	
	
	@Query(
	"SELECT ol " +
	"FROM OrderLine ol " +
	"LEFT JOIN OrderDetail " +
	"LEFT JOIN Tote t " +
	"LEFT JOIN ToteIdentifier ti " +
	"WHERE ol.orderLineNumber = :orderLineNumber " +
	"AND ti.payload = :toteOrderType"
	)
	public List<OrderLine> findRelatedOrderLineByOrderLineNumber(@Param("orderLineNumber") String orderLineNumber, @Param("toteOrderType") String relatedToteOrderType);
}
