package cass.nlw_trips.activity.DTOs;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ActivityData(
        UUID id,
        String title,
        LocalDateTime occursAt
) {
}
