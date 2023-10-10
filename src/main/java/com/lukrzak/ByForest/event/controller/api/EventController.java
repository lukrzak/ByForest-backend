package com.lukrzak.ByForest.event.controller.api;

import com.lukrzak.ByForest.event.dto.PostEventRequest;
import com.lukrzak.ByForest.event.model.Event;

public interface EventController {

	Event findEvent(long id);

	void saveEvent(PostEventRequest postEventRequest);

	void deleteEvent(long id);

}
