package uk.co.boots.dsp.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import uk.co.boots.dsp.wcs.osr.OSRBuffer;
import uk.co.boots.dsp.wcs.osr.OSRConfig;
import uk.co.boots.dsp.wcs.service.ToteService;
import uk.co.boots.dsp.wcs.track.TrackController;
import uk.co.boots.dsp.wcs.track.TrackStatus;

@RestController
@RequestMapping("/osr")
public class OSRRestController {
	@Autowired
	private OSRBuffer osrBuffer;

	@Autowired
	private TrackController trackController;
	
	@Autowired
	private ToteService toteService;
	
	@PostMapping("/releaseState")
	ResponseEntity<OSRConfig> setOSRReleaseState(@RequestBody StartParams params) {
		osrBuffer.setStarted (params.start);
		return ResponseEntity.ok().body(osrBuffer.getOSRConfig());
	}
	
	@PostMapping("/newConfig")
	ResponseEntity<OSRConfig> setOSRConfig (@RequestBody OSRConfig newConfig) {
		osrBuffer.setOsrConfig(newConfig);
		return ResponseEntity.ok().body(newConfig);
	}
	
	@PostMapping("/thirtyTwoRShortState")
	void set32RSendState(@RequestBody SendThirtyTwoRParams params) {
		osrBuffer.setSendThirtyTwoRShort(params.send);
	}
	
	@PostMapping("/resetRun")
	public ResponseEntity<String> resetRun() {
		osrBuffer.setStarted(false);
		trackController.resetTrackController();
		toteService.truncateToteTable();
		trackController.start();
		return new ResponseEntity<>("Run has been reset.", HttpStatus.OK);		
	}
	
	@GetMapping("/config")
	ResponseEntity<OSRConfig> getOSRConfig () {
		return ResponseEntity.ok().body(osrBuffer.getOSRConfig());
	}	
	
}
