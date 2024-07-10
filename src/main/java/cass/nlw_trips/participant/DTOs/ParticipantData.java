package cass.nlw_trips.participant.DTOs;

import java.util.UUID;

public record ParticipantData(UUID id,
                              String name,
                              String email,
                              Boolean isConfirmed) {
}
