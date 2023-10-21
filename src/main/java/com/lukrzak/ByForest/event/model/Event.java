package com.lukrzak.ByForest.event.model;

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

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String place;

	private LocalDate date;

	@ManyToOne
	private User creator;

	@Override
	public String toString() {
		return String.format("Event[%s, %s, %s]", name, place, date);
	}
}
