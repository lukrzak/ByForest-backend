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

	private static final User correctUser = UserTestUtils.getCorrectUser();

	private static final Event correctEvent = EventTestUtils.getCorrectEvent();

	private static final EventStatus correctEventStatus = EventTestUtils.getCorrectEventStatus();

	private static DefaultEventService eventService;

	private final PostEventRequest correctPostEventRequest = EventTestUtils.getCorrectPostEventRequest();

	private final PostEventRequest incorrectPostEventRequest = EventTestUtils.getPostEventRequestWithIncorrectLogin();

	private final PatchStatusRequest correctPatchStatusRequest = EventTestUtils.getCorrectPatchStatusRequest();

	private final PatchStatusRequest incorrectPatchStatusRequest = EventTestUtils.getPatchStatusRequestWithIncorrectLogin();

	@BeforeAll
	static void setup() {
		EventRepository eventRepository = mock(EventRepository.class);
		EventStatusRepository eventStatusRepository = mock(EventStatusRepository.class);
		UserRepository userRepository = mock(UserRepository.class);
		eventService = new DefaultEventService(eventRepository, eventStatusRepository, userRepository);

		doReturn(Optional.empty()).when(userRepository).findByLogin(anyString());
		doReturn(Optional.empty()).when(eventRepository).findById(anyLong());
		doReturn(Optional.empty()).when(eventStatusRepository).findByEventAndUser(any(), any());

		when(userRepository.findByLogin(correctUser.getLogin()))
				.thenReturn(Optional.of(correctUser));
		when(eventRepository.findByNameLikeIgnoreCase(eq(correctEvent.getName())))
				.thenReturn(EventTestUtils.generateEvents(5));
		when(eventRepository.findById(correctEvent.getId()))
				.thenReturn(Optional.of(correctEvent));
		when(eventStatusRepository.findByEventAndUser(correctEvent, correctUser))
				.thenReturn(Optional.of(correctEventStatus));
	}

	@Test
	void testFindingEventsByName() {
		eventService.findAllByNameLike(correctEvent.getName());
	}

	@Test
	void testSavingEvent() throws UserException {
		eventService.saveEvent(correctPostEventRequest);

		assertThrows(UserException.class, () -> eventService.saveEvent(incorrectPostEventRequest));
	}

	@Test
	void testChangingStatus() throws EventException, UserException {
		eventService.changeStatus(correctEvent.getId(), correctPatchStatusRequest);

		assertThrows(UserException.class, () -> eventService.changeStatus(correctEvent.getId(), incorrectPatchStatusRequest));
		assertThrows(EventException.class, () -> eventService.changeStatus(EventTestUtils.getIncorrectEventId(), correctPatchStatusRequest));
	}

}
