package dk.pro3.hateoas_exercise.client.dto;

import java.time.LocalDate;

public record TripDTO(String name, LocalDate startDate, LocalDate endDate)
{
}
