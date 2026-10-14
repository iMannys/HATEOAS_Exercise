package dk.pro3.hateoas_exercise.client.dto;

// "capital", not "isCapital": that is the field name in the JSON
public record CityDTO(String name, int population, boolean capital)
{
}
