package dk.pro3.hateoas_exercise.controller.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

// Give either countryCode or cityId for where the visit takes place;
// TripService rejects both or neither.
public record AddVisitRequest(String countryCode, Long cityId, @NotNull LocalDate arrivalDate,
                              @NotNull LocalDate departureDate, String journal)
{
}
