package dk.pro3.hateoas_exercise.client.dto;

import java.time.LocalDate;

public record VisitDTO(LocalDate arrivalDate, LocalDate departureDate, String journal,
                       String placeName)
{
}
