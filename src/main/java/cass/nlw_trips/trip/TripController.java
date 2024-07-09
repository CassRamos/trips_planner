package cass.nlw_trips.trip;

import cass.nlw_trips.participant.ParticipantService;
import cass.nlw_trips.trip.DTOs.TripCreateResponse;
import cass.nlw_trips.trip.DTOs.TripRequestPayload;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        this.participantService.registerParticipantsToTrip(tripRequestPayload.emails_to_invite(), newTrip.getId());

        return ResponseEntity.ok(new TripCreateResponse(newTrip.getId()));
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
}
