package nl.andrewl.railsignalapi.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(path = "/api/status")
public class StatusController {

	@GetMapping
	public Map<String, Object> getStatus() {
		return Map.of("status", "online");
	}
}
