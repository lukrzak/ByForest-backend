package com.lukrzak.ByForest.event.mapper;

import com.lukrzak.ByForest.event.dto.GetEventResponse;
import com.lukrzak.ByForest.event.dto.PostEventRequest;
import com.lukrzak.ByForest.event.model.Event;
import com.lukrzak.ByForest.user.model.User;

import java.util.LinkedList;
import java.util.List;

public interface EventMapper {

	static List<GetEventResponse> mapEventCollectionToGetEventResponse(List<Event> events) {
		List<GetEventResponse> mappedEvents = new LinkedList<>();
		events.forEach(e -> {
			GetEventResponse mappedEvent = GetEventResponse.builder()
					.name(e.getName())
					.place(e.getPlace())
					.date(e.getDate())
					.creator(e.getCreator().getLogin())
					.build();
			mappedEvents.add(mappedEvent);
		});

		return mappedEvents;
	}

	static Event mapToEvent(PostEventRequest postEventRequest, User creator) {
		return Event.builder()
				.name(postEventRequest.getName())
				.place(postEventRequest.getPlace())
				.date(postEventRequest.getDate())
				.creator(creator)
				.build();
	}

}
