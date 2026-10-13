package dk.pro3.hateoas_exercise.controller.request;

import jakarta.validation.constraints.NotNull;

public record UpdatePopulationRequest(
    @NotNull(message = "Population is required") Integer population)
{
}
