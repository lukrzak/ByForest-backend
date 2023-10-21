package com.lukrzak.ByForest.event.controller;

import com.lukrzak.ByForest.event.dto.GetEventResponse;
import com.lukrzak.ByForest.event.dto.PostEventRequest;
import com.lukrzak.ByForest.event.dto.PatchStatusRequest;
import com.lukrzak.ByForest.event.service.EventService;
import com.lukrzak.ByForest.exception.EventException;
import com.lukrzak.ByForest.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.lukrzak.ByForest.ByForestApplication.BASE_URL;

@RestController
@RequestMapping(BASE_URL + "/events")
@RequiredArgsConstructor
public class DefaultEventController implements EventController {

	private final EventService eventService;

	@Override
	@GetMapping
	public ResponseEntity<List<GetEventResponse>> getEventsByName(@RequestParam("name") String name) {
		return ResponseEntity.ok().body(eventService.findAllByNameLike(name));
	}

	@Override
	@PostMapping
	public ResponseEntity<String> addEvent(@RequestBody PostEventRequest postEventRequest) throws UserException {
		eventService.saveEvent(postEventRequest);
		return ResponseEntity.ok().body("Event " + postEventRequest.getName() + " has been created");
	}

	@Override
	@PatchMapping("/update/{id}")
	public ResponseEntity<String> changeStatus(@PathVariable Long id, @RequestBody PatchStatusRequest patchStatusRequest) throws UserException, EventException {
		eventService.changeStatus(id, patchStatusRequest);
		return ResponseEntity.ok().body("User " + patchStatusRequest.getLogin() + " updated status of event with id: " + id);
	}

}
