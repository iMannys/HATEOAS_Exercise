package dk.pro3.hateoas_exercise.server.controller;

import dk.pro3.hateoas_exercise.server.assembler.CityModelAssembler;
import dk.pro3.hateoas_exercise.server.assembler.model.CityModel;
import dk.pro3.hateoas_exercise.server.controller.request.CreateCityRequest;
import dk.pro3.hateoas_exercise.server.controller.request.UpdatePopulationRequest;
import dk.pro3.hateoas_exercise.server.service.CityService;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController public class CityController
{
  private final CityService cityService;
  private final CityModelAssembler cityModelAssembler;

  public CityController(CityService cityService,
      CityModelAssembler cityModelAssembler)
  {
    this.cityService = cityService;
    this.cityModelAssembler = cityModelAssembler;
  }

  @GetMapping("/city")
  public ResponseEntity<CollectionModel<CityModel>> getCities()
  {
    return ResponseEntity.ok(
        cityModelAssembler.toCollectionModel(cityService.getAllCities()).add(
            linkTo(methodOn(CityController.class).getCities()).withSelfRel()));
  }

  @GetMapping("/city/{id}")
  public ResponseEntity<CityModel> getCity(@PathVariable Long id)
  {
    return ResponseEntity.ok(
        cityModelAssembler.toModel(cityService.getCity(id)));
  }

  @GetMapping(value = "/city", params = "name")
  public ResponseEntity<CollectionModel<CityModel>> getCitiesByName(
      @RequestParam String name)
  {
    return ResponseEntity.ok(
        cityModelAssembler.toCollectionModel(cityService.getCitiesByName(name))
            .add(linkTo(methodOn(CityController.class).getCitiesByName(
                name)).withSelfRel()));
  }

  @GetMapping("/country/{code}/cities")
  public ResponseEntity<CollectionModel<CityModel>> getCitiesByCountry(
      @PathVariable String code)
  {
    return ResponseEntity.ok(cityModelAssembler.toCollectionModel(
        cityService.getCitiesByCountry(code)).add(linkTo(
        methodOn(CityController.class).getCitiesByCountry(
            code)).withSelfRel()));
  }

  @PostMapping("/country/{code}/cities")
  public ResponseEntity<CityModel> createCity(@PathVariable String code,
      @Valid @RequestBody CreateCityRequest createCityRequest)
  {
    CityModel cityModel = cityModelAssembler.toModel(
        cityService.createCity(code, createCityRequest.name(),
            createCityRequest.population()));
    return ResponseEntity.created(
            cityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
        .body(cityModel);
  }

  @PatchMapping("/city/{id}")
  public ResponseEntity<CityModel> updatePopulation(@PathVariable Long id,
      @Valid @RequestBody UpdatePopulationRequest updatePopulationRequest)
  {
    return ResponseEntity.ok(cityModelAssembler.toModel(
        cityService.updatePopulation(id,
            updatePopulationRequest.population())));
  }

  @DeleteMapping("/city/{id}")
  public ResponseEntity<Void> deleteCity(@PathVariable Long id)
  {
    cityService.deleteCity(id);
    return ResponseEntity.noContent().build();
  }
}
