package com.lukrzak.ByForest.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PostEventRequest {

	private final String name;

	private final String place;

	private final LocalDate date;

	private final String creatorLogin;

	private final List<String> invitedUsersLogins;

}
