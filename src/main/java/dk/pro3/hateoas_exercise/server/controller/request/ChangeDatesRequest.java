package dk.pro3.hateoas_exercise.server.controller.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ChangeDatesRequest(@NotNull LocalDate arrivalDate, @NotNull LocalDate departureDate)
{
}
