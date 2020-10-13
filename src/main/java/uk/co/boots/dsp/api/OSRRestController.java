package uk.co.boots.dsp.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import uk.co.boots.dsp.wcs.OSRBuffer;
import uk.co.boots.dsp.wcs.OSRConfig;
import uk.co.boots.dsp.wcs.service.ToteService;

@RestController
@RequestMapping("/osr")
public class OSRRestController {
	@Autowired
	private OSRBuffer osrBuffer;

	@Autowired
	private ToteService toteService;
	
	
	@PostMapping("/releaseState")
	void setOSRReleaseState(@RequestBody StartParams params) {
		osrBuffer.setStarted (params.start);
	}
	
	@PostMapping("/newConfig")
	void setOSRConfig (@RequestBody OSRConfig newConfig) {
		osrBuffer.setOsrConfig(newConfig);
	}
	
	@PostMapping("/thirtyTwoRShortState")
	void set32RSendStart(@RequestBody SendThirtyTwoRParams params) {
		osrBuffer.setSendThirtyTwoRShort(params.send);
	}
	
	@PostMapping("/resetRun")
	public ResponseEntity<String> resetRun() {
		osrBuffer.setStarted(false);
		toteService.deleteAll();
		return new ResponseEntity<>("Run has been reset.", HttpStatus.OK);		
	}
	
	
}
