package com.lukrzak.ByForest.event.dto;

import com.lukrzak.ByForest.event.util.EventStatusValues;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PatchStatusRequest {

	private final EventStatusValues status;

	private final String login;

}
