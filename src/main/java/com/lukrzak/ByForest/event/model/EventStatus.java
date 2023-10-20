package com.lukrzak.ByForest.event.model;

import com.lukrzak.ByForest.event.util.EventStatusValues;
import com.lukrzak.ByForest.user.model.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventStatus {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private EventStatusValues status;

	@ManyToOne
	private Event event;

	@ManyToOne
	private User user;

}
