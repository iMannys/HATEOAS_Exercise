package dk.pro3.hateoas_exercise.server.controller.request;

import jakarta.validation.constraints.NotBlank;

public record RenameTripRequest(@NotBlank String name)
{
}
