package com.lukrzak.ByForest.event;

import com.lukrzak.ByForest.event.dto.GetEventResponse;
import com.lukrzak.ByForest.event.dto.PatchStatusRequest;
import com.lukrzak.ByForest.event.dto.PostEventRequest;
import com.lukrzak.ByForest.event.model.Event;
import com.lukrzak.ByForest.event.repository.EventRepository;
import com.lukrzak.ByForest.event.repository.EventStatusRepository;
import com.lukrzak.ByForest.event.util.EventStatusValues;
import com.lukrzak.ByForest.user.UserTestUtils;
import com.lukrzak.ByForest.user.model.User;
import com.lukrzak.ByForest.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;
import java.util.List;

import static com.lukrzak.ByForest.event.EventTestUtils.DATABASE_IMAGE;
import static com.lukrzak.ByForest.event.EventTestUtils.EVENTS_ENDPOINT_URI;
import static com.lukrzak.ByForest.event.EventTestUtils.EVENT_NAME_PARAMETER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class EventApiTests {

	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private EventStatusRepository eventStatusRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private WebTestClient webTestClient;

	@Container
	@ServiceConnection
	private final static MySQLContainer<?> database = new MySQLContainer<>(DockerImageName.parse(DATABASE_IMAGE))
			.withDatabaseName("events-test-database")
			.withUsername("admin")
			.withPassword("password");

	@AfterEach
	void clean() {
		eventStatusRepository.deleteAll();
		eventRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	void testFindingEventsByName() {
		int amount = 3;
		String differentName = "AAA";
		List<Event> generatedEvents = EventTestUtils.generateEvents(amount);
		User user = userRepository.save(UserTestUtils.getCorrectUser());
		Event otherEvent = Event.builder()
				.name(differentName)
				.date(LocalDate.now())
				.creator(user)
				.place(differentName)
				.build();
		eventRepository.saveAll(generatedEvents);
		eventRepository.save(otherEvent);

		List<GetEventResponse> resultOfEventParameter = getFoundEventsByNameResult("event");
		List<GetEventResponse> resultOfDifferentParameter = getFoundEventsByNameResult(differentName);

		assertEquals(amount, generatedEvents.size());
		assertEquals(1, resultOfDifferentParameter.size());
		resultOfEventParameter.forEach(e -> assertTrue(e.getName().contains("event")));
		assertEquals(otherEvent.getName(), resultOfDifferentParameter.get(0).getName());
		assertEquals(otherEvent.getDate(), resultOfDifferentParameter.get(0).getDate());
		assertEquals(otherEvent.getId(), resultOfDifferentParameter.get(0).getId());
		assertEquals(otherEvent.getPlace(), resultOfDifferentParameter.get(0).getPlace());
		assertEquals(otherEvent.getCreator().getLogin(), resultOfDifferentParameter.get(0).getCreator());
	}

	@Test
	void testAddingNewEvent() {
		PostEventRequest postEventRequest = EventTestUtils.getCorrectPostEventRequest();
		PostEventRequest incorrectPostEventRequest = EventTestUtils.getPostEventRequestWithIncorrectLogin();
		userRepository.save(UserTestUtils.getCorrectUser());
		userRepository.save(User.builder()
				.login("login1")
				.email("example@em.com")
				.password("Password1@3")
				.build()
		);

		String responseMessage = getPostEventResponse(postEventRequest);
		String errorResponseMessage = getPostEventResponse(incorrectPostEventRequest);

		assertEquals("Event " + postEventRequest.getName() + " has been created", responseMessage);
		// Two users should have eventStatus record in database
		assertEquals(2, eventStatusRepository.findAll().size());
		assertEquals("User with login " + incorrectPostEventRequest.getCreatorLogin() + "does not exist", errorResponseMessage);
	}

	@Test
	void testChangingStatus() {
		long otherEventId = 2;
		PatchStatusRequest patchStatusRequest = EventTestUtils.getCorrectPatchStatusRequest();
		PatchStatusRequest incorrectLoginPatchStatusRequest = EventTestUtils.getPatchStatusRequestWithIncorrectLogin();
		User user = UserTestUtils.getCorrectUser();
		Event otherEventWithCorrectUser = Event.builder()
				.id(otherEventId)
				.name("name")
				.place("place")
				.date(LocalDate.now())
				.creator(user)
				.build();
		PatchStatusRequest patchStatusRequestForOtherEvent = PatchStatusRequest.builder()
				.status(EventStatusValues.GOING)
				.login(user.getLogin())
				.build();
		userRepository.save(user);
		eventRepository.save(EventTestUtils.getCorrectEvent());
		eventRepository.save(otherEventWithCorrectUser);
		eventStatusRepository.save(EventTestUtils.getCorrectEventStatus());

		String responseMessage = getPatchStatusEvent(patchStatusRequest, EventTestUtils.getCorrectEvent().getId());
		String incorrectLoginPatchStatusResponse = getPatchStatusEvent(incorrectLoginPatchStatusRequest, EventTestUtils.getCorrectEvent().getId());
		String incorrectIdPatchStatusResponse = getPatchStatusEvent(patchStatusRequest, EventTestUtils.getIncorrectEventId());
		String patchStatusResponseWithOtherEvent = getPatchStatusEvent(patchStatusRequestForOtherEvent, otherEventId);

		assertEquals("User " + patchStatusRequest.getLogin() + " updated status of event with id: " + EventTestUtils.getCorrectEvent().getId(), responseMessage);
		assertEquals("User with login " + incorrectLoginPatchStatusRequest.getLogin() + "does not exist", incorrectLoginPatchStatusResponse);
		assertEquals("Event with id " + EventTestUtils.getIncorrectEventId() + " does not exist", incorrectIdPatchStatusResponse);
		assertEquals("User " + user + " is not invited to event " + otherEventWithCorrectUser, patchStatusResponseWithOtherEvent);
		assertEquals(patchStatusRequest.getStatus(), eventStatusRepository.findByEventAndUser(EventTestUtils.getCorrectEvent(), user).get().getStatus());
	}

	private List<GetEventResponse> getFoundEventsByNameResult(String eventName) {
		return webTestClient.get()
				.uri(EVENTS_ENDPOINT_URI + EVENT_NAME_PARAMETER + eventName)
				.exchange()
				.expectStatus()
				.isOk()
				.expectBodyList(GetEventResponse.class)
				.returnResult()
				.getResponseBody();
	}

	private String getPostEventResponse(PostEventRequest body) {
		return webTestClient.post()
				.uri(EVENTS_ENDPOINT_URI)
				.bodyValue(body)
				.exchange()
				.expectBody(String.class)
				.returnResult()
				.getResponseBody();
	}

	private String getPatchStatusEvent(PatchStatusRequest body, long id) {
		return webTestClient.patch()
				.uri(EVENTS_ENDPOINT_URI + "/" + id)
				.bodyValue(body)
				.exchange()
				.expectBody(String.class)
				.returnResult()
				.getResponseBody();
	}

}
