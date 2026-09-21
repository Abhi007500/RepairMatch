package com.repairmatch.modules.booking.service;

import com.repairmatch.common.exception.BadRequestException;

import java.util.Map;
import java.util.Set;

public final class BookingStateMachine {

    // Valid state transitions
    private static final Map<String, Set<String>> VALID_TRANSITIONS = Map.of(
            "PENDING", Set.of("ACCEPTED", "REJECTED", "CANCELLED"),
            "ACCEPTED", Set.of("IN_PROGRESS", "CANCELLED"),
            "IN_PROGRESS", Set.of("COMPLETED", "CANCELLED"),
            "COMPLETED", Set.of(),
            "REJECTED", Set.of(),
            "CANCELLED", Set.of()
    );

    private BookingStateMachine() {}

    public static void validateTransition(String currentStatus, String targetStatus) {
        if (currentStatus == null || targetStatus == null) {
            throw new BadRequestException("Status cannot be null");
        }

        Set<String> allowedTargets = VALID_TRANSITIONS.getOrDefault(currentStatus.toUpperCase(), Set.of());
        if (!allowedTargets.contains(targetStatus.toUpperCase())) {
            throw new BadRequestException(String.format(
                    "Invalid booking status transition from '%s' to '%s'. Allowed next statuses are: %s",
                    currentStatus, targetStatus, allowedTargets
            ));
        }
    }
}
