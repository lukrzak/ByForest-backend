package com.lukrzak.ByForest.event.service;

import com.lukrzak.ByForest.event.dto.GetEventResponse;
import com.lukrzak.ByForest.event.dto.PostEventRequest;
import com.lukrzak.ByForest.event.dto.PatchStatusRequest;
import com.lukrzak.ByForest.event.model.Event;
import com.lukrzak.ByForest.exception.EventException;
import com.lukrzak.ByForest.exception.UserException;

import java.util.List;

public interface EventService {

	List<GetEventResponse> findAllByNameLike(String name);

	Event saveEvent(PostEventRequest postEventRequest) throws UserException;

	void changeStatus(Long id, PatchStatusRequest patchStatusRequest) throws UserException, EventException;

}

