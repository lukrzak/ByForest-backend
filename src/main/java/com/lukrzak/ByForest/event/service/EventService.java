package com.lukrzak.ByForest.event.service;

import com.lukrzak.ByForest.event.dto.GetEventResponse;
import com.lukrzak.ByForest.event.dto.PostEventRequest;
import com.lukrzak.ByForest.event.model.Event;
import com.lukrzak.ByForest.exception.UserDoesntExistException;

import java.util.List;

public interface EventService {

	List<GetEventResponse> findAllByNameLike(String name);

	Event saveEvent(PostEventRequest postEventRequest) throws UserDoesntExistException;

}

