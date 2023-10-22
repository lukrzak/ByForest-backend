package com.lukrzak.ByForest.event;

import com.lukrzak.ByForest.event.dto.GetEventResponse;
import com.lukrzak.ByForest.event.dto.PatchStatusRequest;
import com.lukrzak.ByForest.event.dto.PostEventRequest;
import com.lukrzak.ByForest.event.model.Event;
import com.lukrzak.ByForest.event.model.EventStatus;
import com.lukrzak.ByForest.event.util.EventStatusValues;
import com.lukrzak.ByForest.user.UserTestUtils;
import com.lukrzak.ByForest.user.model.User;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class EventTestUtils {

	private static final Event dummyEvent = Event.builder()
			.id(1L)
			.creator(UserTestUtils.getCorrectUser())
			.date(LocalDate.now())
			.place("place")
			.name("event")
			.build();

	public static Event getCorrectEvent() {
		return dummyEvent;
	}

	public static Long getIncorrectEventId() {
		return dummyEvent.getId() + 1;
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

	public static PostEventRequest getCorrectPostEventRequest() {
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

	public static PatchStatusRequest getCorrectPatchStatusRequest() {
		return PatchStatusRequest.builder()
				.login(UserTestUtils.getCorrectUser().getLogin())
				.status(EventStatusValues.GOING)
				.build();
	}

	public static PatchStatusRequest getPatchStatusRequestWithIncorrectLogin() {
		return PatchStatusRequest.builder()
				.login("incorrect")
				.status(EventStatusValues.GOING)
				.build();
	}

	public static EventStatus getCorrectEventStatus() {
		return EventStatus.builder()
				.status(EventStatusValues.UNDEFINED)
				.event(getCorrectEvent())
				.user(UserTestUtils.getCorrectUser())
				.build();
	}

}
