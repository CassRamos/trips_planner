package cass.nlw_trips.trip;

import cass.nlw_trips.participant.DTOs.ParticipantCreateResponse;
import cass.nlw_trips.participant.DTOs.ParticipantData;
import cass.nlw_trips.participant.DTOs.ParticipantRequestPayload;
import cass.nlw_trips.participant.ParticipantService;
import cass.nlw_trips.trip.DTOs.TripCreateResponse;
import cass.nlw_trips.trip.DTOs.TripRequestPayload;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/trips")
public class TripController {

    private final ParticipantService participantService;
    private final TripRepository tripRepository;


    public TripController(ParticipantService participantService, TripRepository tripRepository) {
        this.participantService = participantService;
        this.tripRepository = tripRepository;
    }

    @PostMapping
    public ResponseEntity<TripCreateResponse> createTrip(@RequestBody TripRequestPayload tripRequestPayload) {
        Trip newTrip = new Trip(tripRequestPayload);

        this.tripRepository.save(newTrip);

        this.participantService.registerParticipantsToTrip(tripRequestPayload.emails_to_invite(), newTrip);

        return ResponseEntity.ok(new TripCreateResponse(newTrip.getId()));
    }

    @PostMapping("/{id}/invite")
    public ResponseEntity<ParticipantCreateResponse> inviteParticipant(@PathVariable UUID id,
                                                                       @RequestBody ParticipantRequestPayload participantRequestPayload) {
        Optional<Trip> trip = tripRepository.findById(id);

        if (trip.isPresent()) {
            Trip createdTrip = trip.get();

            ParticipantCreateResponse participantCreateResponse = this.participantService
                    .registerParticipantToTrip(
                            participantRequestPayload.email(),
                            createdTrip
                    );

            if (createdTrip.isConfirmed()) {
                participantService.triggerConfirmationEmailToParticipant(participantRequestPayload.email());
            }

            return ResponseEntity.ok(participantCreateResponse);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripDetails(@PathVariable UUID id) {
        Optional<Trip> trip = this.tripRepository.findById(id);

        return trip
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity
                        .notFound()
                        .build());
    }

    @GetMapping("/{id}/confirm")
    public ResponseEntity<Trip> confirmTrip(@PathVariable UUID id) {
        Optional<Trip> trip = this.tripRepository.findById(id);

        if (trip.isPresent()) {
            Trip confirmationTrip = trip.get();
            confirmationTrip.setConfirmed(true);

            this.tripRepository.save(confirmationTrip);
            this.participantService.triggerConfirmationEmailToParticipants(id);

            return ResponseEntity.ok(confirmationTrip);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<ParticipantData>> getAllParticipantsFromTrip(@PathVariable UUID id) {
        List<ParticipantData> participantList = participantService.getAllParticipantsFromTrip(id);

        return ResponseEntity.ok(participantList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Trip> updateTrip(@PathVariable UUID id, @RequestBody TripRequestPayload tripRequestPayload) {
        Optional<Trip> trip = this.tripRepository.findById(id);

        if (trip.isPresent()) {
            Trip updatedTrip = trip.get();

            updatedTrip.setStartsAt(LocalDateTime.parse(tripRequestPayload.starts_at(), DateTimeFormatter.ISO_DATE_TIME));
            updatedTrip.setEndsAt(LocalDateTime.parse(tripRequestPayload.ends_at(), DateTimeFormatter.ISO_DATE_TIME));
            updatedTrip.setDestination(tripRequestPayload.destination());

            this.tripRepository.save(updatedTrip);

            return ResponseEntity.ok(updatedTrip);
        }
        return ResponseEntity.notFound().build();
    }


}
