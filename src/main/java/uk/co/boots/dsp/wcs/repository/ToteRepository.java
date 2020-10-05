package uk.co.boots.dsp.wcs.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import uk.co.boots.dsp.messages.shared.Tote;

public interface ToteRepository extends PagingAndSortingRepository<Tote, Long> {
	
}
