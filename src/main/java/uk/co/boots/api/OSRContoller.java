package uk.co.boots.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import uk.co.boots.osr.OSRBuffer;

@RestController
@RequestMapping("/osr")
public class OSRContoller {
	@Autowired
	private OSRBuffer osrBuffer;
	
	@PostMapping("/start")
	void newEmployee(@RequestBody StartParams params) {
		osrBuffer.setStarted (params.start);
	}
}
