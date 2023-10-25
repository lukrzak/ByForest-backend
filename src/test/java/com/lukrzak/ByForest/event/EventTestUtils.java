package com.lukrzak.ByForest.event;

import com.lukrzak.ByForest.event.dto.GetEventResponse;
import com.lukrzak.ByForest.event.model.Event;
import com.lukrzak.ByForest.user.model.User;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import static com.lukrzak.ByForest.ByForestApplication.BASE_URL;

public class EventTestUtils {

	public static final String DATABASE_IMAGE = "mysql:8.1.0";

	public static final String EVENTS_ENDPOINT_URI = BASE_URL + "/events";

	public static final String EVENT_NAME_PARAMETER = "?name=";

	public static List<Event> generateEvents(int amount, User creator) {
		List<Event> events = new LinkedList<>();
		for (int i = 0; i < amount; i++) {
			Event newEvent = Event.builder()
					.name("event" + i)
					.date(LocalDate.now())
					.creator(creator)
					.place("place" + i)
					.build();
			events.add(newEvent);
		}

		return events;
	}

	public static List<GetEventResponse> generateEventResponses(int amount) {
		List<GetEventResponse> responses = new LinkedList<>();
		for (int i = 0; i < amount; i++) {
			GetEventResponse newResponse = GetEventResponse.builder()
					.place("place" + i)
					.creator("login" + i)
					.name("event" + i)
					.date(LocalDate.now())
					.build();
			responses.add(newResponse);
		}

		return responses;
	}

}
