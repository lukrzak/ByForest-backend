package com.lukrzak.ByForest.event.controller;

import com.lukrzak.ByForest.event.dto.GetEventResponse;
import com.lukrzak.ByForest.event.dto.PostEventRequest;
import com.lukrzak.ByForest.event.model.Event;
import com.lukrzak.ByForest.exception.UserDoesntExistException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface EventController {

	ResponseEntity<List<GetEventResponse>> getEventsByName(String name);

	ResponseEntity<String> addEvent(PostEventRequest postEventRequest) throws UserDoesntExistException;

}
