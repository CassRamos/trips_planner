package cass.nlw_trips.link;

import cass.nlw_trips.link.DTOs.LinkData;
import cass.nlw_trips.link.DTOs.LinkRequestPayload;
import cass.nlw_trips.link.DTOs.LinkResponse;
import cass.nlw_trips.trip.Trip;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LinkService {

    private final LinkRepository linkRepository;

    public LinkService(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    public LinkResponse registerLink(LinkRequestPayload linkRequestPayload, Trip trip) {
        Link newLink = new Link(linkRequestPayload.title(), linkRequestPayload.url(), trip);

        linkRepository.save(newLink);

        return new LinkResponse(newLink.getId());
    }

    public List<LinkData> getAllLinksByTripId(UUID tripId) {
        return linkRepository
                .findByTripId(tripId)
                .stream()
                .map(link -> new LinkData(
                        link.getId(),
                        link.getTitle(),
                        link.getUrl())
                )
                .toList();
    }
}
