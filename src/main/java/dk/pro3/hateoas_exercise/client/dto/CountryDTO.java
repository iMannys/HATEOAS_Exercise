package dk.pro3.hateoas_exercise.client.dto;

import java.util.Set;

// Continents as plain strings, so the client does not depend on the server's enum
public record CountryDTO(String name, String code, Set<String> continents)
{
}
