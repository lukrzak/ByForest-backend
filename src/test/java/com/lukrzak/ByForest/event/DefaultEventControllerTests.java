package com.lukrzak.ByForest.event;

import com.lukrzak.ByForest.event.controller.DefaultEventController;
import com.lukrzak.ByForest.event.dto.GetEventResponse;
import com.lukrzak.ByForest.event.dto.PostEventRequest;
import com.lukrzak.ByForest.event.model.Event;
import com.lukrzak.ByForest.event.service.EventService;
import com.lukrzak.ByForest.exception.UserDoesntExistException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultEventControllerTests {

	private final static Event dummyEvent = EventTestUtils.getDummyEvent();

	private final static PostEventRequest incorrectPostEventRequest = EventTestUtils.getPostEventRequestWithIncorrectLogin();

	private static DefaultEventController eventController;

	private static EventService eventService;

	@BeforeAll
	static void setup() throws UserDoesntExistException {
		eventService = mock(EventService.class);
		eventController = new DefaultEventController(eventService);

		when(eventService.findAllByNameLike(eq(dummyEvent.getName())))
				.thenReturn(EventTestUtils.generateEventResponses(5));
		doThrow(UserDoesntExistException.class).when(eventService).saveEvent(incorrectPostEventRequest);
	}

	@Test
	void testFindingEventsByName() {
		ResponseEntity<List<GetEventResponse>> response = eventController.getEventsByName(dummyEvent.getName());

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(eventService.findAllByNameLike(dummyEvent.getName()), response.getBody());
	}

	@Test
	void testSavingEvent() throws UserDoesntExistException {
		PostEventRequest request = EventTestUtils.getPostEventRequest();

		ResponseEntity<String> response = eventController.addEvent(request);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Event " + request.getName() + " has been created", response.getBody());
		assertThrows(UserDoesntExistException.class, () -> eventController.addEvent(incorrectPostEventRequest));
	}

}
