package cass.nlw_trips.activity;

import cass.nlw_trips.activity.DTOs.ActivityData;
import cass.nlw_trips.activity.DTOs.ActivityRequestPayload;
import cass.nlw_trips.activity.DTOs.ActivityResponse;
import cass.nlw_trips.trip.Trip;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public ActivityResponse registerActivity(ActivityRequestPayload activityRequestPayload,
                                             Trip trip) {
        Activity newActivity = new Activity
                (
                        activityRequestPayload.title(),
                        activityRequestPayload.occursAt(),
                        trip
                );

        activityRepository.save(newActivity);

        return new ActivityResponse(newActivity.getId());
    }

    public List<ActivityData> getAllActivitiesByTripId(UUID tripId) {
        return activityRepository
                .findByTripId(tripId)
                .stream()
                .map(activity -> new ActivityData
                        (
                                activity.getId(),
                                activity.getTitle(),
                                activity.getOccursAt()))
                .toList();
    }
}
