package com.lukrzak.ByForest.event;

import com.lukrzak.ByForest.event.dto.PatchStatusRequest;
import com.lukrzak.ByForest.event.dto.PostEventRequest;
import com.lukrzak.ByForest.event.model.Event;
import com.lukrzak.ByForest.event.model.EventStatus;
import com.lukrzak.ByForest.event.repository.EventRepository;
import com.lukrzak.ByForest.event.repository.EventStatusRepository;
import com.lukrzak.ByForest.event.service.DefaultEventService;
import com.lukrzak.ByForest.exception.EventException;
import com.lukrzak.ByForest.exception.UserException;
import com.lukrzak.ByForest.user.UserTestUtils;
import com.lukrzak.ByForest.user.model.User;
import com.lukrzak.ByForest.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultEventServiceTests {

	private final static User dummyUser = UserTestUtils.getDummyUser();

	private final static Event dummyEvent = EventTestUtils.getDummyEvent();

	private final static EventStatus eventStatus = EventTestUtils.getEventStatus();

	private static DefaultEventService eventService;

	@BeforeAll
	static void setup() {
		EventRepository eventRepository = mock(EventRepository.class);
		EventStatusRepository eventStatusRepository = mock(EventStatusRepository.class);
		UserRepository userRepository = mock(UserRepository.class);
		eventService = new DefaultEventService(eventRepository, eventStatusRepository, userRepository);

		doReturn(Optional.empty()).when(userRepository).findByLogin(anyString());
		doReturn(Optional.empty()).when(eventRepository).findById(anyLong());
		doReturn(Optional.empty()).when(eventStatusRepository).findByEventAndUser(any(), any());
		when(userRepository.findByLogin(dummyUser.getLogin()))
				.thenReturn(Optional.of(dummyUser));
		when(eventRepository.findAllByNameLike(eq(dummyEvent.getName())))
				.thenReturn(EventTestUtils.generateEvents(5));
		when(eventRepository.findById(dummyEvent.getId()))
				.thenReturn(Optional.of(dummyEvent));
		when(eventStatusRepository.findByEventAndUser(dummyEvent, dummyUser))
				.thenReturn(Optional.of(eventStatus));
	}

	@Test
	void testFindingEventsByName() {
		eventService.findAllByNameLike(dummyEvent.getName());
	}

	@Test
	void testSavingEvent() throws UserException {
		PostEventRequest postEventRequest = EventTestUtils.getPostEventRequest();
		PostEventRequest postEventRequestWithIncorrectLogin = EventTestUtils.getPostEventRequestWithIncorrectLogin();

		eventService.saveEvent(postEventRequest);
		assertThrows(UserException.class, () -> eventService.saveEvent(postEventRequestWithIncorrectLogin));
	}

	@Test
	void testChangingStatus() throws EventException, UserException {
		PatchStatusRequest request = EventTestUtils.getPatchStatusRequest();
		PatchStatusRequest incorrectPatch = EventTestUtils.getIncorrectPatchStatusRequest();

		eventService.changeStatus(dummyEvent.getId(), request);

		assertThrows(UserException.class, () -> eventService.changeStatus(dummyEvent.getId(), incorrectPatch));
		assertThrows(EventException.class, () -> eventService.changeStatus(2L, request));
	}

}
