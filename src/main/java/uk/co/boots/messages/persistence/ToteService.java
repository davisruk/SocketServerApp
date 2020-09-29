package uk.co.boots.messages.persistence;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import uk.co.boots.messages.shared.Tote;


public class ToteService {
/*
	@Autowired
	ToteRepository toteRepository;
	
	public Page<Tote> getTotePage(int pageNumber, int pageSize){
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Order.asc("id")));
		return toteRepository.findAll(pageable);
	}
	
	public Iterable<Tote> findAllTote() {
		return toteRepository.findAll();
	}

	public Tote save(Tote tote) {
		return toteRepository.save(tote);
	}
*/
}
