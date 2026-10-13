package dk.pro3.hateoas_exercise.assembler;

import dk.pro3.hateoas_exercise.assembler.model.VisitModel;
import dk.pro3.hateoas_exercise.controller.*;
import dk.pro3.hateoas_exercise.domain.City;
import dk.pro3.hateoas_exercise.domain.Country;
import dk.pro3.hateoas_exercise.domain.Visit;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class VisitModelAssembler implements RepresentationModelAssembler<Visit, VisitModel>
{
  @Override
  public VisitModel toModel(Visit visit)
  {
    VisitModel visitModel = new VisitModel(visit.getArrivalDate(), visit.getDepartureDate(),
        visit.getJournal(), visit.getPlace().getName());

    visitModel.add(linkTo(methodOn(VisitController.class).getVisit(
        visit.getId())).withSelfRel());
    visitModel.add(linkTo(methodOn(VisitController.class).getVisits()).withRel("visits"));
    // Links to /visit/{id}/trip instead of /trip/{id}: Visit doesn't know its trip, and
    // looking it up here would put data access in the assembler (and run once per
    // visit in a collection). The lookup happens only if a client follows the link.
    visitModel.add(linkTo(methodOn(TripController.class).getTripOfVisit(visit.getId())).withRel("trip"));
    visitModel.add(linkTo(methodOn(CountryController.class).getCountry(visit.getCountry().getCode())).withRel("country"));
    visitModel.add(linkTo(methodOn(VisitController.class).getVisitsByCountry(visit.getCountry().getCode())).withRel("visitsByCountry"));
    visitModel.add(linkTo(methodOn(VisitController.class).changeDates(
        visit.getId(), null)).withRel("changeDates"));
    visitModel.add(linkTo(methodOn(VisitController.class).updateJournal(
        visit.getId(), null)).withRel("updateJournal"));

    switch (visit.getPlace())
    {
      case City city ->
        visitModel.add(linkTo(methodOn(CityController.class).getCity(city.getId())).withRel("place"));
      case Country country ->
        visitModel.add(linkTo(methodOn(CountryController.class).getCountry(
            country.getCode())).withRel("place"));
    }

    return visitModel;
  }
}
