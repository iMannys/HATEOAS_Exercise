package dk.pro3.hateoas_exercise.server.controller;

import dk.pro3.hateoas_exercise.server.assembler.CountryModelAssembler;
import dk.pro3.hateoas_exercise.server.assembler.model.CountryModel;
import dk.pro3.hateoas_exercise.server.controller.request.ChangeCapitalRequest;
import dk.pro3.hateoas_exercise.server.controller.request.CreateCountryRequest;
import dk.pro3.hateoas_exercise.domain.enums.Continent;
import dk.pro3.hateoas_exercise.server.service.CountryService;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class CountryController
{
  private final CountryService countryService;
  private final CountryModelAssembler countryModelAssembler;

  public CountryController(CountryService countryService,
      CountryModelAssembler countryModelAssembler)
  {
    this.countryService = countryService;
    this.countryModelAssembler = countryModelAssembler;
  }

  @GetMapping("/country")
  public ResponseEntity<CollectionModel<CountryModel>> getCountries()
  {
    return ResponseEntity.ok(countryModelAssembler.toCollectionModel(
        countryService.getAllCountries()).add(linkTo(
        methodOn(CountryController.class).getCountries()).withSelfRel()));
  }

  @GetMapping("/country/{code}")
  public ResponseEntity<CountryModel> getCountry(@PathVariable String code)
  {
    return ResponseEntity.ok(
        countryModelAssembler.toModel(countryService.getCountry(code)));
  }

  @GetMapping(value = "/country", params = "continent")
  public ResponseEntity<CollectionModel<CountryModel>> getCountriesByContinent(
      @RequestParam Continent continent)
  {
    return ResponseEntity.ok(countryModelAssembler.toCollectionModel(
        countryService.getCountriesByContinent(continent)).add(linkTo(
        methodOn(CountryController.class).getCountriesByContinent(
            continent)).withSelfRel()));
  }

  @PostMapping("/country")
  public ResponseEntity<CountryModel> createCountry(
      @Valid @RequestBody CreateCountryRequest createCountryRequest)
  {
    CountryModel countryModel = countryModelAssembler.toModel(
        countryService.createCountry(createCountryRequest.name(),
            createCountryRequest.code(), createCountryRequest.continents(),
            createCountryRequest.capitalName(),
            createCountryRequest.capitalPopulation()));
    return ResponseEntity.created(
            countryModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
        .body(countryModel);
  }

  @PatchMapping("/country/{code}")
  public ResponseEntity<CountryModel> changeCapital(@PathVariable String code,
      @Valid @RequestBody ChangeCapitalRequest changeCapitalRequest)
  {
    return ResponseEntity.ok(countryModelAssembler.toModel(
        countryService.changeCapital(code, changeCapitalRequest.cityId())));
  }

  @DeleteMapping("/country/{code}")
  public ResponseEntity<Void> deleteCountry(@PathVariable String code)
  {
    countryService.deleteCountry(code);
    return ResponseEntity.noContent().build();
  }
}
