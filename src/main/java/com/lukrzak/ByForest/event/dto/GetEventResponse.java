package com.lukrzak.ByForest.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class GetEventResponse {

	private String name;

	private String place;

	private LocalDate date;

	private String creator;

}
