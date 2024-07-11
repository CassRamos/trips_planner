package cass.nlw_trips.trip;

import cass.nlw_trips.activity.ActivityService;
import cass.nlw_trips.activity.DTOs.ActivityData;
import cass.nlw_trips.activity.DTOs.ActivityRequestPayload;
import cass.nlw_trips.activity.DTOs.ActivityResponse;
import cass.nlw_trips.link.DTOs.LinkData;
import cass.nlw_trips.link.DTOs.LinkRequestPayload;
import cass.nlw_trips.link.DTOs.LinkResponse;
import cass.nlw_trips.link.LinkService;
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
    private final ActivityService activityService;
    private final LinkService linkService;
    private final TripRepository tripRepository;


    public TripController(ParticipantService participantService, ActivityService activityService, LinkService linkService, TripRepository tripRepository) {
        this.participantService = participantService;
        this.activityService = activityService;
        this.linkService = linkService;
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

    @PostMapping("/{id}/activities")
    public ResponseEntity<ActivityResponse> registerActivity(@PathVariable UUID id, @RequestBody ActivityRequestPayload activityRequestPayload) {
        Optional<Trip> trip = tripRepository.findById(id);

        if (trip.isPresent()) {
            Trip selectedTrip = trip.get();

            ActivityResponse activityResponse = activityService.registerActivity(activityRequestPayload, selectedTrip);

            return ResponseEntity.ok(activityResponse);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/links")
    public ResponseEntity<LinkResponse> registerLink(@PathVariable UUID id, @RequestBody LinkRequestPayload linkRequestPayload) {
        Optional<Trip> trip = tripRepository.findById(id);

        if (trip.isPresent()) {
            Trip selectedTrip = trip.get();

            LinkResponse linkResponse = linkService.registerLink(linkRequestPayload, selectedTrip);

            return ResponseEntity.ok(linkResponse);
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
    public ResponseEntity<List<ParticipantData>> getAllParticipantsByTripId(@PathVariable UUID id) {
        List<ParticipantData> participantList = participantService.getAllParticipantsFromTrip(id);

        return ResponseEntity.ok(participantList);
    }

    @GetMapping("{id}/activities")
    public ResponseEntity<List<ActivityData>> getAllActivitiesByTripId(@PathVariable UUID id) {
        List<ActivityData> activityDataList = activityService.getAllActivitiesByTripId(id);

        return ResponseEntity.ok(activityDataList);
    }

    @GetMapping("{id}/links")
    public ResponseEntity<List<LinkData>> getAllLinksByTripId(@PathVariable UUID id) {
        List<LinkData> linkDataList = linkService.getAllLinksByTripId(id);
        return ResponseEntity.ok(linkDataList);
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
