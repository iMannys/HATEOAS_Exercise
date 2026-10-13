package dk.pro3.hateoas_exercise.controller.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateJournalRequest(@NotBlank String journal)
{
}
