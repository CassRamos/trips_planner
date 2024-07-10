package cass.nlw_trips.participant;

import cass.nlw_trips.participant.DTOs.ParticipantRequestPayload;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/participants")
public class ParticipantController {


    private final ParticipantRepository participantRepository;

    public ParticipantController(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<Participant> confirmParticipant(@PathVariable UUID id, @RequestBody ParticipantRequestPayload participantRequestPayload) {
        Optional<Participant> participant = participantRepository.findById(id);

        if (participant.isPresent()) {
            Participant participantToConfirm = participant.get();
            participantToConfirm.setIsConfirmed(true);
            participantToConfirm.setName(participantRequestPayload.name());

            participantRepository.save(participantToConfirm);

            return ResponseEntity.ok(participantToConfirm);
        }

        return ResponseEntity.notFound().build();
    }


}
