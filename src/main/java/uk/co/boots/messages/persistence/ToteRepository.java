package uk.co.boots.messages.persistence;

import org.springframework.data.repository.PagingAndSortingRepository;

import uk.co.boots.messages.shared.Tote;

public interface ToteRepository extends PagingAndSortingRepository<Tote, Long> {
	
}
