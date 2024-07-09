package cass.nlw_trips.trip;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface TripRepository extends CrudRepository<Trip, UUID> {
}
