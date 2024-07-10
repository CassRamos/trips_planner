package cass.nlw_trips.participant;

import cass.nlw_trips.participant.DTOs.ParticipantCreateResponse;
import cass.nlw_trips.participant.DTOs.ParticipantData;
import cass.nlw_trips.trip.Trip;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ParticipantService {
    private final ParticipantRepository participantRepository;

    public ParticipantService(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    public void registerParticipantsToTrip(List<String> participantsToInvite, Trip trip) {
        List<Participant> participants = participantsToInvite
                .stream()
                .map(email -> new Participant(email, trip))
                .toList();

        this.participantRepository.saveAll(participants);
    }

    public ParticipantCreateResponse registerParticipantToTrip(String email, Trip trip) {
        Participant newParticipant = new Participant(email, trip);
        this.participantRepository.save(newParticipant);

        return new ParticipantCreateResponse(newParticipant.getId());
    }

    public void triggerConfirmationEmailToParticipants(UUID tripId) {
    }

    public void triggerConfirmationEmailToParticipant(String email) {
    }

    public List<ParticipantData> getAllParticipantsFromTrip(UUID tripId) {
        return this.participantRepository
                .findByTripId(tripId)
                .stream()
                .map(participant -> (
                                new ParticipantData(
                                        participant.getId(),
                                        participant.getName(),
                                        participant.getEmail(),
                                        participant.getIsConfirmed()
                                )
                        )
                ).toList();
    }
}
