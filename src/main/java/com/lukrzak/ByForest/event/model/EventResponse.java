package com.lukrzak.ByForest.event.model;

import com.lukrzak.ByForest.event.util.EventResponseStatus;
import com.lukrzak.ByForest.user.model.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class EventResponse {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private EventResponseStatus status;

	@OneToOne
	private Event event;

	@OneToOne
	private User user;

}