package com.lukrzak.ByForest.event.controller;

import com.lukrzak.ByForest.event.dto.GetEventResponse;
import com.lukrzak.ByForest.event.dto.PostEventRequest;
import com.lukrzak.ByForest.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.lukrzak.ByForest.ByForestApplication.BASE_URL;

@RestController
@RequestMapping(BASE_URL + "/events")
@RequiredArgsConstructor
public class DefaultEventController implements EventController {

	private final EventService eventService;

	@Override
	public ResponseEntity<List<GetEventResponse>> getEventsByName(String name) {
		return ResponseEntity.ok().body(eventService.findAllByNameLike(name));
	}

	@Override
	public ResponseEntity<String> addEvent(PostEventRequest postEventRequest) {
		return null;
	}

}
