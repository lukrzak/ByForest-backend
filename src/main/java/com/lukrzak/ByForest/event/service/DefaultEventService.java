package com.lukrzak.ByForest.event.service;

import com.lukrzak.ByForest.event.dto.GetEventResponse;
import com.lukrzak.ByForest.event.dto.PostEventRequest;
import com.lukrzak.ByForest.event.dto.PatchStatusRequest;
import com.lukrzak.ByForest.event.mapper.EventMapper;
import com.lukrzak.ByForest.event.model.Event;
import com.lukrzak.ByForest.event.model.EventStatus;
import com.lukrzak.ByForest.event.repository.EventRepository;
import com.lukrzak.ByForest.event.repository.EventStatusRepository;
import com.lukrzak.ByForest.event.util.EventStatusValues;
import com.lukrzak.ByForest.exception.EventException;
import com.lukrzak.ByForest.exception.UserException;
import com.lukrzak.ByForest.user.model.User;
import com.lukrzak.ByForest.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultEventService implements EventService {

	private final EventRepository eventRepository;

	private final EventStatusRepository eventStatusRepository;

	private final UserRepository userRepository;

	@Override
	public List<GetEventResponse> findAllByNameLike(String name) {
		List<Event> events = eventRepository.findByNameLikeIgnoreCase("%" + name + "%");

		return EventMapper.mapEventCollectionToGetEventResponse(events);
	}

	@Override
	public Event saveEvent(PostEventRequest postEventRequest) throws UserException {
		User user = userRepository.findByLogin(postEventRequest.getCreatorLogin())
				.orElseThrow(() -> new UserException("User with login " + postEventRequest.getCreatorLogin() + "does not exist"));
		log.info("Found user {}", user);
		Event mappedEvent = EventMapper.mapToEvent(postEventRequest, user);
		log.info("Mapped from {} to {}", postEventRequest, eventRepository);
		eventRepository.save(mappedEvent);
		log.info("Saved {}", mappedEvent);

		saveInvitedUsersDefaultStatus(postEventRequest.getInvitedUsersLogins(), mappedEvent);

		return mappedEvent;
	}

	@Override
	public EventStatus changeStatus(Long id, PatchStatusRequest patchStatusRequest) throws UserException, EventException {
		User user = userRepository.findByLogin(patchStatusRequest.getLogin())
				.orElseThrow(() -> new UserException("User with login " + patchStatusRequest.getLogin() + "does not exist"));
		Event event = eventRepository.findById(id)
				.orElseThrow(() -> new EventException("Event with id " + id + " does not exist"));
		EventStatus eventStatus = eventStatusRepository.findByEventAndUser(event, user)
				.orElseThrow(() -> new EventException("User " + user + " is not invited to event " + event));

		eventStatus.setStatus(patchStatusRequest.getStatus());
		return eventStatusRepository.save(eventStatus);
	}

	private void saveInvitedUsersDefaultStatus(List<String> logins, Event event) {
		List<User> users = logins.stream()
				.map(userRepository::findByLogin)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.toList();
		log.info("Invited users: {} to event: {}", users, event);

		users.forEach(u -> {
			EventStatus eventStatus = EventStatus.builder()
					.status(EventStatusValues.UNDEFINED)
					.event(event)
					.user(u)
					.build();
			eventStatusRepository.save(eventStatus);
			log.info("Added UNDEFINED status for user: {}, in event: {}", u, event);
		});
	}

}
