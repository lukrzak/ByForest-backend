package com.lukrzak.ByForest.event;

import com.lukrzak.ByForest.event.dto.PostEventRequest;
import com.lukrzak.ByForest.event.model.Event;
import com.lukrzak.ByForest.event.repository.EventRepository;
import com.lukrzak.ByForest.event.repository.EventStatusRepository;
import com.lukrzak.ByForest.event.service.DefaultEventService;
import com.lukrzak.ByForest.exception.UserDoesntExistException;
import com.lukrzak.ByForest.user.UserTestUtils;
import com.lukrzak.ByForest.user.model.User;
import com.lukrzak.ByForest.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultEventServiceTests {

	private final static User dummyUser = UserTestUtils.getDummyUser();

	private final static Event dummyEvent = EventTestUtils.getDummyEvent();

	private static DefaultEventService eventService;

	@BeforeAll
	static void setup() {
		EventRepository eventRepository = mock(EventRepository.class);
		EventStatusRepository eventStatusRepository = mock(EventStatusRepository.class);
		UserRepository userRepository = mock(UserRepository.class);
		eventService = new DefaultEventService(eventRepository, eventStatusRepository, userRepository);

		when(userRepository.findByLogin(dummyUser.getLogin()))
				.thenReturn(Optional.of(dummyUser));
		when(eventRepository.findAllByNameLike(eq(dummyEvent.getName())))
				.thenReturn(EventTestUtils.generateEvents(5));
	}

	@Test
	void testFindingEventsByName() {
		eventService.findAllByNameLike(dummyEvent.getName());
	}

	@Test
	void testSavingEvent() throws UserDoesntExistException {
		PostEventRequest postEventRequest = EventTestUtils.getPostEventRequest();
		PostEventRequest postEventRequestWithIncorrectLogin = EventTestUtils.getPostEventRequestWithIncorrectLogin();

		eventService.saveEvent(postEventRequest);
		assertThrows(UserDoesntExistException.class, () -> eventService.saveEvent(postEventRequestWithIncorrectLogin));
	}

}
