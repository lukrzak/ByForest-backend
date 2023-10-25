package com.lukrzak.ByForest.event;

import com.lukrzak.ByForest.event.controller.DefaultEventController;
import com.lukrzak.ByForest.event.dto.GetEventResponse;
import com.lukrzak.ByForest.event.dto.PatchStatusRequest;
import com.lukrzak.ByForest.event.dto.PostEventRequest;
import com.lukrzak.ByForest.event.model.Event;
import com.lukrzak.ByForest.event.model.EventStatus;
import com.lukrzak.ByForest.event.service.EventService;
import com.lukrzak.ByForest.event.util.EventStatusValues;
import com.lukrzak.ByForest.exception.EventException;
import com.lukrzak.ByForest.exception.UserException;
import com.lukrzak.ByForest.user.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultEventControllerTests {

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

	private final EventService eventService = mock(EventService.class);

	private final DefaultEventController eventController = new DefaultEventController(eventService);

	@Test
	void testFindingEventsByName() {
		int amountOfEvents = 5;
		when(eventService.findAllByNameLike(anyString()))
				.thenReturn(EventTestUtils.generateEventResponses(amountOfEvents));

		ResponseEntity<List<GetEventResponse>> response = eventController.getEventsByName(event.getName());

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(eventService.findAllByNameLike(event.getName()), response.getBody());
	}

	@Test
	void testSavingEvent() throws UserException {
		Event correctEvent = new Event();
		when(eventService.saveEvent(any()))
				.thenReturn(correctEvent);

		ResponseEntity<String> response = eventController.addEvent(postEventRequest);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Event " + postEventRequest.getName() + " has been created", response.getBody());
	}

	@Test
	void testSavingUserWithIncorrectLogin() throws UserException {
		when(eventService.saveEvent(any()))
				.thenThrow(UserException.class);

		assertThrows(UserException.class, () -> eventController.addEvent(postEventRequest));
	}

	@Test
	void testChangingStatus() throws EventException, UserException {
		when(eventService.changeStatus(anyLong(), any()))
				.thenReturn(new EventStatus());

		ResponseEntity<String> response = eventController.changeStatus(event.getId(), patchStatusRequest);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("User " + patchStatusRequest.getLogin() + " updated status of event with id: 1", response.getBody());
	}

	@Test
	void testChangingStatusWithThrowingUserException() throws EventException, UserException {
		when(eventService.changeStatus(anyLong(), any()))
				.thenThrow(UserException.class);

		assertThrows(UserException.class, () -> eventController.changeStatus(1L, patchStatusRequest));
	}

	@Test
	void testChangingStatusWithThrowingEventException() throws EventException, UserException {
		when(eventService.changeStatus(anyLong(), any()))
				.thenThrow(EventException.class);

		assertThrows(EventException.class, () -> eventController.changeStatus(1L, patchStatusRequest));
	}

}
