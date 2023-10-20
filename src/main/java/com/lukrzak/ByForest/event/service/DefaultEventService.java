package com.lukrzak.ByForest.event.service;

import com.lukrzak.ByForest.event.dto.GetEventResponse;
import com.lukrzak.ByForest.event.dto.PostEventRequest;
import com.lukrzak.ByForest.event.dto.PatchStatusRequest;
import com.lukrzak.ByForest.event.mapper.EventMapper;
import com.lukrzak.ByForest.event.model.Event;
import com.lukrzak.ByForest.event.model.EventStatus;
import com.lukrzak.ByForest.event.repository.EventRepository;
import com.lukrzak.ByForest.event.repository.EventStatusRepository;
import com.lukrzak.ByForest.exception.EventDoesntExistException;
import com.lukrzak.ByForest.exception.UserDoesntExistException;
import com.lukrzak.ByForest.user.model.User;
import com.lukrzak.ByForest.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultEventService implements EventService {

	private final EventRepository eventRepository;

	private final EventStatusRepository eventStatusRepository;

	private final UserRepository userRepository;

	@Override
	public List<GetEventResponse> findAllByNameLike(String name) {
		List<Event> events = eventRepository.findAllByNameLike(name);

		return EventMapper.mapEventCollectionToGetEventResponse(events);
	}

	@Override
	public Event saveEvent(PostEventRequest postEventRequest) throws UserDoesntExistException {
		User user = userRepository.findByLogin(postEventRequest.getCreatorLogin())
				.orElseThrow(() -> new UserDoesntExistException("User with login " + postEventRequest.getCreatorLogin() + "does not exist"));
		log.info("Found user {}", user);
		Event mappedEvent = EventMapper.mapToEvent(postEventRequest, user);
		log.info("Mapped from {} to {}", postEventRequest, eventRepository);
		eventRepository.save(mappedEvent);
		log.info("Saved {}", mappedEvent);

		return mappedEvent;
	}

	@Override
	public void changeStatus(Long id, PatchStatusRequest patchStatusRequest) throws UserDoesntExistException, EventDoesntExistException {
		User user = userRepository.findByLogin(patchStatusRequest.getLogin())
				.orElseThrow(() -> new UserDoesntExistException("User with login " + patchStatusRequest.getLogin() + "does not exist"));
		Event event = eventRepository.findById(id)
				.orElseThrow(() -> new EventDoesntExistException("Event with id " + id + " does not exist"));
		EventStatus eventStatus = eventStatusRepository.findByEventAndUser(event, user)
				.orElseThrow(NullPointerException::new);

		eventStatus.setStatus(patchStatusRequest.getStatus());
		eventStatusRepository.save(eventStatus);
	}

}
