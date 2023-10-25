package com.lukrzak.ByForest.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class GetEventResponse {

	private final Long id;

	private final String name;

	private final String place;

	private final LocalDate date;

	private final String creator;

}
