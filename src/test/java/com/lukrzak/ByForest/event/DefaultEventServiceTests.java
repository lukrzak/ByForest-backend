package com.lukrzak.ByForest.event;

import com.lukrzak.ByForest.event.dto.GetEventResponse;
import com.lukrzak.ByForest.event.dto.PatchStatusRequest;
import com.lukrzak.ByForest.event.dto.PostEventRequest;
import com.lukrzak.ByForest.event.model.Event;
import com.lukrzak.ByForest.event.model.EventStatus;
import com.lukrzak.ByForest.event.repository.EventRepository;
import com.lukrzak.ByForest.event.repository.EventStatusRepository;
import com.lukrzak.ByForest.event.service.DefaultEventService;
import com.lukrzak.ByForest.event.util.EventStatusValues;
import com.lukrzak.ByForest.exception.EventException;
import com.lukrzak.ByForest.exception.UserException;
import com.lukrzak.ByForest.user.model.User;
import com.lukrzak.ByForest.user.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultEventServiceTests {

	private final Event event = Event.builder()
			.id(1L)
			.name("event")
			.place("place")
			.date(LocalDate.now())
			.creator(new User())
			.build();

	private final PostEventRequest postEventRequest = PostEventRequest.builder()
			.name("event")
			.place("place")
			.date(LocalDate.now())
			.creatorLogin("login")
			.invitedUsersLogins(List.of("login1", "login2"))
			.build();

	private final PatchStatusRequest patchStatusRequest = PatchStatusRequest.builder()
			.login("login")
			.status(EventStatusValues.GOING)
			.build();

	private final EventRepository eventRepository = mock(EventRepository.class);

	private final EventStatusRepository eventStatusRepository = mock(EventStatusRepository.class);

	private final UserRepository userRepository = mock(UserRepository.class);

	private final DefaultEventService eventService = new DefaultEventService(eventRepository, eventStatusRepository, userRepository);

	@Test
	void testFindingEventsByName() {
		int amountOfGeneratedEvents = 5;
		List<Event> generatedEvents = EventTestUtils.generateEvents(amountOfGeneratedEvents, new User());
		when(eventRepository.findByNameLikeIgnoreCase(anyString()))
				.thenReturn(generatedEvents);

		List<GetEventResponse> response = eventService.findAllByNameLike(event.getName());

		assertEquals(amountOfGeneratedEvents, response.size());
	}

	@Test
	void testSavingEvent() throws UserException {
		when(userRepository.findByLogin(anyString()))
				.thenReturn(Optional.of(new User()));

		eventService.saveEvent(postEventRequest);
	}

	@Test
	void testSavingEventWithIncorrectLogin() {
		when(userRepository.findByLogin(anyString()))
				.thenReturn(Optional.empty());

		assertThrows(UserException.class, () -> eventService.saveEvent(postEventRequest));
	}

	@Test
	void testChangingStatus() throws EventException, UserException {
		when(userRepository.findByLogin(anyString()))
				.thenReturn(Optional.of(new User()));
		when(eventRepository.findById(anyLong()))
				.thenReturn(Optional.of(new Event()));
		when(eventStatusRepository.findByEventAndUser(any(), any()))
				.thenReturn(Optional.of(new EventStatus()));

		eventService.changeStatus(event.getId(), patchStatusRequest);
	}

	@Test
	void testChangingStatusWithIncorrectLogin() {
		when(userRepository.findByLogin(anyString()))
				.thenReturn(Optional.empty());

		assertThrows(UserException.class, () -> eventService.changeStatus(event.getId(), patchStatusRequest));
	}

	@Test
	void testChangingStatusWithIncorrectEventId() {
		when(userRepository.findByLogin(anyString()))
				.thenReturn(Optional.of(new User()));
		when(eventRepository.findById(anyLong()))
				.thenReturn(Optional.empty());

		assertThrows(EventException.class, () -> eventService.changeStatus(event.getId(), patchStatusRequest));
	}

	@Test
	void testChangingStatusWithEventUserMismatch() {
		when(userRepository.findByLogin(anyString()))
				.thenReturn(Optional.of(new User()));
		when(eventRepository.findById(anyLong()))
				.thenReturn(Optional.of(new Event()));
		when(eventStatusRepository.findByEventAndUser(any(), any()))
				.thenReturn(Optional.empty());

		assertThrows(EventException.class, () -> eventService.changeStatus(event.getId(), patchStatusRequest));
	}

}
