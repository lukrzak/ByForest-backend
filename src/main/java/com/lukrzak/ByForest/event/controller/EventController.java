package com.lukrzak.ByForest.event.controller;

import com.lukrzak.ByForest.event.dto.GetEventResponse;
import com.lukrzak.ByForest.event.dto.PostEventRequest;
import com.lukrzak.ByForest.event.dto.PatchStatusRequest;
import com.lukrzak.ByForest.exception.EventDoesntExistException;
import com.lukrzak.ByForest.exception.UserDoesntExistException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface EventController {

	ResponseEntity<List<GetEventResponse>> getEventsByName(String name);

	ResponseEntity<String> addEvent(PostEventRequest postEventRequest) throws UserDoesntExistException;

	ResponseEntity<String> changeStatus(Long id, PatchStatusRequest patchStatusRequest) throws UserDoesntExistException, EventDoesntExistException;

}
