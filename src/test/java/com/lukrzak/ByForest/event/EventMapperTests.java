package com.lukrzak.ByForest.event;

import com.lukrzak.ByForest.event.dto.GetEventResponse;
import com.lukrzak.ByForest.event.dto.PostEventRequest;
import com.lukrzak.ByForest.event.mapper.EventMapper;
import com.lukrzak.ByForest.event.model.Event;
import com.lukrzak.ByForest.user.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventMapperTests {

	@Test
	void testMappingCollectionToGetEventResponses() {
		int amount = 3;
		List<Event> eventsToMap = EventTestUtils.generateEvents(amount, new User());

		List<GetEventResponse> responses = EventMapper.mapEventCollectionToGetEventResponse(eventsToMap);

		assertEquals(amount, responses.size());
		assertEquals(eventsToMap.get(0).getPlace(), responses.get(0).getPlace());
		assertEquals(eventsToMap.get(0).getDate(), responses.get(0).getDate());
		assertEquals(eventsToMap.get(0).getCreator().getLogin(), responses.get(0).getCreator());
		assertEquals(eventsToMap.get(0).getName(), responses.get(0).getName());
	}

	@Test
	void testMappingToEvent() {
		PostEventRequest request = PostEventRequest.builder()
				.place("place")
				.invitedUsersLogins(List.of("login1", "login2"))
				.name("event")
				.creatorLogin("login")
				.date(LocalDate.now())
				.build();
		User user = User.builder()
				.login("login")
				.password("Password!123")
				.email("email@em.com")
				.build();

		Event mappedEvent = EventMapper.mapToEvent(request, user);

		assertEquals(request.getName(), mappedEvent.getName());
		assertEquals(request.getPlace(), mappedEvent.getPlace());
		assertEquals(request.getDate(), mappedEvent.getDate());
		assertEquals(user, mappedEvent.getCreator());
	}

}
