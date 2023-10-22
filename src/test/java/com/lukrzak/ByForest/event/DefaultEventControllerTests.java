package com.lukrzak.ByForest.event;

import com.lukrzak.ByForest.event.controller.DefaultEventController;
import com.lukrzak.ByForest.event.dto.GetEventResponse;
import com.lukrzak.ByForest.event.dto.PatchStatusRequest;
import com.lukrzak.ByForest.event.dto.PostEventRequest;
import com.lukrzak.ByForest.event.model.Event;
import com.lukrzak.ByForest.event.model.EventStatus;
import com.lukrzak.ByForest.event.service.EventService;
import com.lukrzak.ByForest.exception.EventException;
import com.lukrzak.ByForest.exception.UserException;
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

	private static final Event correctEvent = EventTestUtils.getCorrectEvent();

	private static final PostEventRequest correctPostEventRequest = EventTestUtils.getCorrectPostEventRequest();

	private static final PatchStatusRequest correctPatchStatusRequest = EventTestUtils.getCorrectPatchStatusRequest();

	private static final EventStatus changedEventStatus = EventTestUtils.getCorrectEventStatus();

	private static final PostEventRequest incorrectPostEventRequest = EventTestUtils.getPostEventRequestWithIncorrectLogin();

	private static final PatchStatusRequest incorrectUserPatchStatusRequest = EventTestUtils.getPatchStatusRequestWithIncorrectLogin();

	private static DefaultEventController eventController;

	private static EventService eventService;

	@BeforeAll
	static void setup() throws UserException, EventException {
		eventService = mock(EventService.class);
		eventController = new DefaultEventController(eventService);

		doThrow(UserException.class).when(eventService).changeStatus(correctEvent.getId(), incorrectUserPatchStatusRequest);
		doThrow(EventException.class).when(eventService).changeStatus(EventTestUtils.getIncorrectEventId(), correctPatchStatusRequest);
		doThrow(UserException.class).when(eventService).saveEvent(incorrectPostEventRequest);

		when(eventService.findAllByNameLike(eq(correctEvent.getName())))
				.thenReturn(EventTestUtils.generateEventResponses(5));
		when(eventService.changeStatus(correctEvent.getId(), correctPatchStatusRequest))
				.thenReturn(changedEventStatus);
		when(eventService.saveEvent(eq(correctPostEventRequest)))
				.thenReturn(correctEvent);
	}

	@Test
	void testFindingEventsByName() {
		ResponseEntity<List<GetEventResponse>> response = eventController.getEventsByName(correctEvent.getName());

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(eventService.findAllByNameLike(correctEvent.getName()), response.getBody());
	}

	@Test
	void testSavingEvent() throws UserException {
		ResponseEntity<String> response = eventController.addEvent(correctPostEventRequest);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Event " + correctPostEventRequest.getName() + " has been created", response.getBody());
		assertThrows(UserException.class, () -> eventController.addEvent(incorrectPostEventRequest));
	}

	@Test
	void testChangingStatus() throws EventException, UserException {
		ResponseEntity<String> response = eventController.changeStatus(1L, correctPatchStatusRequest);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("User " + correctPatchStatusRequest.getLogin() + " updated status of event with id: 1", response.getBody());
		assertThrows(UserException.class, () -> eventController.changeStatus(1L, incorrectUserPatchStatusRequest));
		assertThrows(EventException.class, () -> eventController.changeStatus(EventTestUtils.getIncorrectEventId(), correctPatchStatusRequest));
	}

}
