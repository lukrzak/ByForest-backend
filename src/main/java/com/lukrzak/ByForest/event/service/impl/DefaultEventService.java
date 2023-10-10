package com.lukrzak.ByForest.event.service.impl;

import com.lukrzak.ByForest.event.dto.PostEventRequest;
import com.lukrzak.ByForest.event.model.Event;
import com.lukrzak.ByForest.event.repository.EventRepository;
import com.lukrzak.ByForest.event.service.api.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultEventService implements EventService {

	private final EventRepository eventRepository;

	@Override
	public Event findEvent(Long id) {
		return null;
	}

	@Override
	public void saveEvent(PostEventRequest postEventRequest) {

	}

	@Override
	public void deleteEvent(Long id) {

	}

}
