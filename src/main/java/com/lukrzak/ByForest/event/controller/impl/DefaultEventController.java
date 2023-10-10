package com.lukrzak.ByForest.event.controller.impl;

import com.lukrzak.ByForest.event.controller.api.EventController;
import com.lukrzak.ByForest.event.dto.PostEventRequest;
import com.lukrzak.ByForest.event.model.Event;
import com.lukrzak.ByForest.event.service.api.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.lukrzak.ByForest.ByForestApplication.BASE_URL;

@RestController
@RequestMapping(BASE_URL + "/events")
@RequiredArgsConstructor
public class DefaultEventController implements EventController {

	private final EventService eventService;

	@GetMapping("/{id}")
	@Override
	public Event findEvent(long id) {
		return null;
	}

	@PostMapping
	@Override
	public void saveEvent(PostEventRequest postEventRequest) {

	}

	@DeleteMapping("/{id}")
	@Override
	public void deleteEvent(long id) {
		System.out.println("test");
	}

}
