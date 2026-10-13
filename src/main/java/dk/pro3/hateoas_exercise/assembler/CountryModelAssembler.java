package dk.pro3.hateoas_exercise.assembler;

import dk.pro3.hateoas_exercise.assembler.model.CountryModel;
import dk.pro3.hateoas_exercise.controller.CityController;
import dk.pro3.hateoas_exercise.controller.CountryController;
import dk.pro3.hateoas_exercise.controller.VisitController;
import dk.pro3.hateoas_exercise.domain.Country;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CountryModelAssembler implements RepresentationModelAssembler<Country, CountryModel>
{
  @Override public CountryModel toModel(Country country)
  {
    return new CountryModel(country.getName(), country.getCode(), country.getContinents())
        .add(linkTo(methodOn(CountryController.class).getCountry(country.getCode())).withSelfRel())
        .add(linkTo(methodOn(CountryController.class).getCountries()).withRel("countries"))
        .add(linkTo(methodOn(CityController.class).getCitiesByCountry(country.getCode())).withRel("cities"))
        .add(linkTo(methodOn(VisitController.class).getVisitsByCountry(
            country.getCode())).withRel("visits"))
        .add(linkTo(methodOn(CityController.class).getCity(country.getCapital().getId())).withRel("capital"));
  }
}
