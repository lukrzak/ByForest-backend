package com.lukrzak.ByForest.event;

import com.lukrzak.ByForest.event.dto.GetEventResponse;
import com.lukrzak.ByForest.event.dto.PatchStatusRequest;
import com.lukrzak.ByForest.event.dto.PostEventRequest;
import com.lukrzak.ByForest.event.model.Event;
import com.lukrzak.ByForest.event.model.EventStatus;
import com.lukrzak.ByForest.event.repository.EventRepository;
import com.lukrzak.ByForest.event.repository.EventStatusRepository;
import com.lukrzak.ByForest.event.util.EventStatusValues;
import com.lukrzak.ByForest.user.model.User;
import com.lukrzak.ByForest.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
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

	private final int EVENTS_TO_GENERATE = 5;

	User user = User.builder()
			.email("email1@em.com")
			.login("login1")
			.password("Password!123")
			.build();

	User otherUser = User.builder()
			.email("email2@em.com")
			.login("login2")
			.password("Password!123")
			.build();

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

	@BeforeEach
	void clean() {
		eventStatusRepository.deleteAll();
		eventRepository.deleteAll();
		userRepository.deleteAll();

		insertTestData();
	}

	@Test
	void testFindingEventsByName() {
		String differentName = "AAA";
		Event otherEvent = Event.builder()
				.name(differentName)
				.date(LocalDate.now())
				.creator(user)
				.place(differentName)
				.build();
		eventRepository.save(otherEvent);

		List<GetEventResponse> resultsOfEventParameter = getFoundEventsByNameResult("event");
		List<GetEventResponse> resultsOfDifferentParameter = getFoundEventsByNameResult(differentName);
		GetEventResponse responseOfDifferentParameter = resultsOfDifferentParameter.get(0);

		assertEquals(EVENTS_TO_GENERATE, resultsOfEventParameter.size());
		assertEquals(1, resultsOfDifferentParameter.size());
		resultsOfEventParameter.forEach(e -> assertTrue(e.getName().contains("event")));
		assertEquals(otherEvent.getName(), responseOfDifferentParameter.getName());
		assertEquals(otherEvent.getDate(), responseOfDifferentParameter.getDate());
		assertEquals(otherEvent.getId(), responseOfDifferentParameter.getId());
		assertEquals(otherEvent.getPlace(), responseOfDifferentParameter.getPlace());
		assertEquals(otherEvent.getCreator().getLogin(), responseOfDifferentParameter.getCreator());
	}

	@Test
	void testAddingNewEvent() {
		PostEventRequest postEventRequest = PostEventRequest.builder()
				.name("event")
				.creatorLogin(user.getLogin())
				.date(LocalDate.now())
				.place("place")
				.invitedUsersLogins(List.of("nonExistingUser", otherUser.getLogin()))
				.build();
		PostEventRequest incorrectPostEventRequest = PostEventRequest.builder()
				.name("event")
				.creatorLogin("incorrect")
				.date(LocalDate.now())
				.place("place")
				.invitedUsersLogins(List.of("nonExistingUser", otherUser.getLogin()))
				.build();
		int expectedNumbersOfStatuses = 2;

		String responseMessage = getPostEventResponse(postEventRequest);
		String errorResponseMessage = getPostEventResponse(incorrectPostEventRequest);

		assertEquals("Event " + postEventRequest.getName() + " has been created", responseMessage);
		assertEquals(expectedNumbersOfStatuses, eventStatusRepository.findAll().size());
		assertEquals("User with login " + incorrectPostEventRequest.getCreatorLogin() + " does not exist", errorResponseMessage);
	}

	@Test
	void testChangingStatus() {
		Event correctEvent = eventRepository.findAll().get(0);
		long correctEventId = correctEvent.getId();
		long incorrectEventId = 999L;
		PatchStatusRequest patchStatusRequest = PatchStatusRequest.builder()
				.login(user.getLogin())
				.status(EventStatusValues.GOING)
				.build();
		PatchStatusRequest incorrectLoginPatchStatusRequest = PatchStatusRequest.builder()
				.login("incorrect")
				.status(EventStatusValues.GOING)
				.build();
		PatchStatusRequest patchStatusRequestWithEventUserMismatch = PatchStatusRequest.builder()
				.login(otherUser.getLogin())
				.status(EventStatusValues.GOING)
				.build();
		EventStatus eventStatus = EventStatus.builder()
				.user(user)
				.status(EventStatusValues.UNDEFINED)
				.event(correctEvent)
				.build();
		eventStatusRepository.save(eventStatus);

		String responseMessage = getPatchStatusEvent(patchStatusRequest, correctEventId);
		String incorrectLoginPatchStatusResponse = getPatchStatusEvent(incorrectLoginPatchStatusRequest, correctEventId);
		String incorrectIdPatchStatusResponse = getPatchStatusEvent(patchStatusRequest, incorrectEventId);
		String patchStatusResponseWithOtherEvent = getPatchStatusEvent(patchStatusRequestWithEventUserMismatch, correctEventId);

		assertEquals("User " + patchStatusRequest.getLogin() + " updated status of event with id: " + correctEventId, responseMessage);
		assertEquals("User with login " + incorrectLoginPatchStatusRequest.getLogin() + "does not exist", incorrectLoginPatchStatusResponse);
		assertEquals("Event with id " + incorrectEventId + " does not exist", incorrectIdPatchStatusResponse);
		assertEquals("User " + otherUser + " is not invited to event " + eventRepository.findById(correctEventId).get(), patchStatusResponseWithOtherEvent);
		assertEquals(patchStatusRequest.getStatus(), eventStatusRepository.findByEventAndUser(correctEvent, user).get().getStatus());
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

	private void insertTestData() {
		userRepository.save(user);
		userRepository.save(otherUser);

		List<Event> generatedEvents = EventTestUtils.generateEvents(EVENTS_TO_GENERATE, user);
		eventRepository.saveAll(generatedEvents);
	}

}
