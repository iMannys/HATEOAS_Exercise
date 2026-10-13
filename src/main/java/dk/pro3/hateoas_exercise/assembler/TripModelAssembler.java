package dk.pro3.hateoas_exercise.assembler;

import dk.pro3.hateoas_exercise.assembler.model.TripModel;
import dk.pro3.hateoas_exercise.controller.CountryController;
import dk.pro3.hateoas_exercise.controller.TripController;
import dk.pro3.hateoas_exercise.domain.Country;
import dk.pro3.hateoas_exercise.domain.Trip;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TripModelAssembler implements RepresentationModelAssembler<Trip, TripModel>
{
  @Override
  public TripModel toModel(Trip trip)
  {
    TripModel tripModel = new TripModel(trip.getName(), trip.getStartDate(), trip.getEndDate())
        .add(linkTo(methodOn(TripController.class).getTrip(trip.getId())).withSelfRel())
        .add(linkTo(methodOn(TripController.class).getTrips()).withRel("trips"))
        .add(linkTo(methodOn(TripController.class).getVisitsOfTrip(trip.getId())).withRel("visits"));

    // Countries are linked, not embedded (same reason as Place in VisitModelAssembler).
    // Several links with one rel become a JSON array in HAL.
    for (Country country : trip.getCountries())
    {
      tripModel.add(linkTo(methodOn(CountryController.class).getCountry(
          country.getCode())).withRel("visitedCountries"));
    }

    return tripModel;
  }
}
