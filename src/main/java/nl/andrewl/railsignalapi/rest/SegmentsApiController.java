package nl.andrewl.railsignalapi.rest;

import lombok.RequiredArgsConstructor;
import nl.andrewl.railsignalapi.rest.dto.FullSegmentResponse;
import nl.andrewl.railsignalapi.rest.dto.SegmentPayload;
import nl.andrewl.railsignalapi.rest.dto.SegmentResponse;
import nl.andrewl.railsignalapi.service.SegmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/rs/{rsId}/s")
@RequiredArgsConstructor
public class SegmentsApiController {
	private final SegmentService segmentService;

	@GetMapping
	public List<SegmentResponse> getSegments(@PathVariable long rsId) {
		return segmentService.getSegments(rsId);
	}

	@GetMapping(path = "/{sId}")
	public FullSegmentResponse getSegment(@PathVariable long rsId, @PathVariable long sId) {
		return segmentService.getSegment(rsId, sId);
	}

	@PostMapping
	public FullSegmentResponse createSegment(@PathVariable long rsId, @RequestBody SegmentPayload payload) {
		return segmentService.create(rsId, payload);
	}

	@DeleteMapping(path = "/{sId}")
	public ResponseEntity<Void> removeSegment(@PathVariable long rsId, @PathVariable long sId) {
		segmentService.remove(rsId, sId);
		return ResponseEntity.noContent().build();
	}
}
