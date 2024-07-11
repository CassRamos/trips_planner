package cass.nlw_trips.link.DTOs;

import java.util.UUID;

public record LinkData
        (
                UUID id,
                String title,
                String url
        ) {
}
