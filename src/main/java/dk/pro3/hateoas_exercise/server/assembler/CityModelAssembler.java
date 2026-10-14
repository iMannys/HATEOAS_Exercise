package dk.pro3.hateoas_exercise.server.assembler;

import dk.pro3.hateoas_exercise.server.assembler.model.CityModel;
import dk.pro3.hateoas_exercise.server.controller.CityController;
import dk.pro3.hateoas_exercise.server.controller.CountryController;
import dk.pro3.hateoas_exercise.domain.City;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component public class CityModelAssembler
    implements RepresentationModelAssembler<City, CityModel>
{
  @Override public CityModel toModel(City city)
  {
    return new CityModel(city.getName(), city.getPopulation(), city.isCapital())
        .add(linkTo(
            methodOn(CityController.class).getCity(city.getId())).withSelfRel())
        .add(linkTo(methodOn(CityController.class).getCities()).withRel(
            "cities"))
        .add(linkTo(methodOn(CountryController.class).getCountry(
            city.getCountry().getCode())).withRel("country"));

  }
}
