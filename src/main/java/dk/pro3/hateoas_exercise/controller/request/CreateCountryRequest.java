package dk.pro3.hateoas_exercise.controller.request;

import dk.pro3.hateoas_exercise.domain.enums.Continent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record CreateCountryRequest(@NotBlank String name, @NotBlank String code, @NotNull Set<Continent> continents,
                                   @NotBlank String capitalName, @NotNull Integer capitalPopulation)
{
}
