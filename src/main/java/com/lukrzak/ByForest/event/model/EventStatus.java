package com.lukrzak.ByForest.event.model;

import com.lukrzak.ByForest.event.util.EventStatusValues;
import com.lukrzak.ByForest.user.model.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "events_responses")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventStatus {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Enumerated(EnumType.STRING)
	private EventStatusValues status;

	@ManyToOne
	private Event event;

	@ManyToOne
	private User user;

}
