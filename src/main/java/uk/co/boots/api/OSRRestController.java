package uk.co.boots.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import uk.co.boots.osr.OSRBuffer;
import uk.co.boots.osr.OSRConfig;

@RestController
@RequestMapping("/osr")
public class OSRRestController {
	@Autowired
	private OSRBuffer osrBuffer;
	
	@PostMapping("/releaseState")
	void setOSRReleaseState(@RequestBody StartParams params) {
		osrBuffer.setStarted (params.start);
	}
	
	@PostMapping("/newConfig")
	void setOSRConfig (@RequestBody OSRConfig newConfig) {
		osrBuffer.setOsrConfig(newConfig);
	}
}
