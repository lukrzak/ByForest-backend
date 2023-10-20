package com.lukrzak.ByForest.event.service;

import com.lukrzak.ByForest.event.dto.GetEventResponse;
import com.lukrzak.ByForest.event.dto.PostEventRequest;
import com.lukrzak.ByForest.event.mapper.EventMapper;
import com.lukrzak.ByForest.event.model.Event;
import com.lukrzak.ByForest.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultEventService implements EventService {

	private final EventRepository eventRepository;

	@Override
	public List<GetEventResponse> findAllByNameLike(String name) {
		List<Event> events = eventRepository.findAllByNameLike(name);
		return EventMapper.mapEventCollectionToGetEventResponse(events);
	}

	@Override
	public Event saveEvent(PostEventRequest postEventRequest) {
		return null;
	}

}
