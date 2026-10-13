package dk.pro3.hateoas_exercise.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

// A trip needs its first visit. Give either countryCode or cityId for where it
// takes place; TripService rejects both or neither.
public record CreateTripRequest(@NotBlank String name, String countryCode, Long cityId,
                                @NotNull LocalDate arrivalDate, @NotNull LocalDate departureDate,
                                String journal)
{
}
