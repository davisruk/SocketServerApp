package uk.co.boots.dsp.wcs.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import uk.co.boots.dsp.messages.base.entity.OrderLine;
import uk.co.boots.dsp.messages.base.entity.RawMessage;
import uk.co.boots.dsp.messages.base.entity.Tote;

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
	
	@Query(
            "SELECT t " +
            "FROM Tote t " +
            "LEFT JOIN t.toteIdentifier ti " +            		 
            "LEFT JOIN t.transportContainer tc " +
            "WHERE t.header.orderId like %:filter% " +
            "OR t.header.sheetNumber like %:filter% " +
            "OR ti.payload like %:filter% " +
            "OR tc.payload like %:filter%"
    )
	public Page<Tote> findAllTotesUsingFilter(Pageable pageable, @Param("filter") String filter);

	@Query(
            "SELECT t " +
            "FROM Tote t " +
            "LEFT JOIN t.toteIdentifier ti " +            		 
            "LEFT JOIN t.transportContainer tc " +
            "WHERE t.header.orderId like %:filter% " +
            "OR t.header.sheetNumber like %:filter% " +
            "OR ti.payload like %:filter% " +
            "OR tc is null"
    )
	public Page<Tote> findAllTotesUsingFilterReplaceNoneWithNullForContainer(Pageable pageable, @Param("filter") String filter);

	@Query(
	"SELECT rm " +
	"FROM RawMessage rm " +
	"WHERE rm.tote.id = :toteId"
	)
	public List<RawMessage> findAllRawMessagesByToteId(@Param("toteId") long toteId);
	
	@Query(
	"SELECT rm " +
	"FROM RawMessage rm " +
	"WHERE rm.id = :id"
	)
	public RawMessage findRawMessageById(@Param("id") long id);
	
    @Modifying
    @Query(
            value = "truncate table Tote cascade",
            nativeQuery = true
    )
	void truncateToteTable();
}
