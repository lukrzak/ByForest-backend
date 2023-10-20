package com.lukrzak.ByForest.event;

import com.lukrzak.ByForest.event.dto.PostEventRequest;
import com.lukrzak.ByForest.event.model.Event;
import com.lukrzak.ByForest.user.UserTestUtils;
import com.lukrzak.ByForest.user.model.User;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class EventTestUtils {

	private static final Event dummyEvent = Event.builder()
			.id(1L)
			.creator(UserTestUtils.getDummyUser())
			.date(LocalDate.now())
			.place("place")
			.name("event")
			.build();

	public static Event getDummyEvent() {
		return dummyEvent;
	}

	public static List<Event> generateEvents(int amount) {
		List<Event> events = new LinkedList<>();
		for (int i = 0; i < amount; i++) {
			Event newEvent = Event.builder()
					.name("event" + i)
					.date(LocalDate.now())
					.creator(new User())
					.place("place" + i)
					.build();
			events.add(newEvent);
		}

		return events;
	}

	public static PostEventRequest getPostEventRequest() {
		return PostEventRequest.builder()
				.place("place")
				.date(LocalDate.now())
				.name("event")
				.creatorLogin("login")
				.invitedUsersLogins(List.of("login1", "login2"))
				.build();
	}

	public static PostEventRequest getPostEventRequestWithIncorrectLogin() {
		return PostEventRequest.builder()
				.place("place")
				.date(LocalDate.now())
				.name("event")
				.creatorLogin("invalid")
				.invitedUsersLogins(List.of("login1", "login2"))
				.build();
	}

}
