package com.lukrzak.ByForest.event.service.api;

import com.lukrzak.ByForest.event.dto.PostEventRequest;
import com.lukrzak.ByForest.event.model.Event;

public interface EventService {

	Event findEvent(Long id);

	void saveEvent(PostEventRequest postEventRequest);

	void deleteEvent(Long id);

}
