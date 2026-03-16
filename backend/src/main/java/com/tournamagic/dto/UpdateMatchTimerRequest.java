package com.tournamagic.dto;

public record UpdateMatchTimerRequest(
        Boolean running,
        Boolean reset,
        String direction,
        Integer durationSeconds,
        Integer notifyIntervalSeconds
) {
}
