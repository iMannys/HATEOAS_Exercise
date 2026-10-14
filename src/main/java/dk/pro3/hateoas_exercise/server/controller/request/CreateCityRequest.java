package dk.pro3.hateoas_exercise.server.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCityRequest(@NotBlank(message = "Name is required") String name,
    @NotNull(message = "Population is required") Integer population)
{

}
