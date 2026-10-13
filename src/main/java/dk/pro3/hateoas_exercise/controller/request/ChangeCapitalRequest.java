package dk.pro3.hateoas_exercise.controller.request;

import jakarta.validation.constraints.NotNull;

public record ChangeCapitalRequest(@NotNull Long cityId)
{
}
